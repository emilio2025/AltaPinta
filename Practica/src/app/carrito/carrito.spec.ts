import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of } from 'rxjs';

import { CarritoComponent } from './carrito';
import { CarritoService } from '../../services/carrito.service';
import { EnvioService } from '../../services/envio.service';
import { ProductoService } from '../../services/producto.service';
import { FavoritoService } from '../../services/favorito.service';
import { AuthService } from '../auth.service';

/**
 * Pruebas del carrito.
 *
 * Aqui se calcula lo que el cliente cree que va a pagar. El importe
 * definitivo lo recalcula el backend con BigDecimal al confirmar, pero si
 * las dos cuentas no coinciden el cliente ve un precio y le cobran otro.
 */
describe('CarritoComponent', () => {

  let carrito: jasmine.SpyObj<CarritoService>;
  let envios: jasmine.SpyObj<EnvioService>;
  let componente: CarritoComponent;

  const LIMA = { id: 1, lugar: 'Lima', costo: 15 };
  const PROVINCIA = { id: 2, lugar: 'Cusco', costo: 25 };

  /** Dos lineas: 89.90 x2 y 120.00 x1  ->  subtotal 299.80 */
  function itemsDeEjemplo() {
    return [
      { productoId: 10, tallaId: 1, cantidad: 2, precio: 89.90 },
      { productoId: 20, tallaId: 3, cantidad: 1, precio: 120.00 },
    ];
  }

  function montar() {
    TestBed.configureTestingModule({
      imports: [CarritoComponent],
      providers: [
        { provide: CarritoService, useValue: carrito },
        { provide: EnvioService, useValue: envios },
        { provide: ProductoService, useValue: { getCategorias: () => of([]) } },
        { provide: FavoritoService, useValue: { getFavoritos: () => of([]) } },
        { provide: AuthService, useValue: jasmine.createSpyObj('AuthService', ['logout']) },
        { provide: Router, useValue: jasmine.createSpyObj('Router', ['navigate']) },
      ],
    });

    const fixture = TestBed.createComponent(CarritoComponent);
    componente = fixture.componentInstance;
    fixture.detectChanges();          // dispara ngOnInit
    return fixture;
  }

  beforeEach(() => {
    carrito = jasmine.createSpyObj<CarritoService>('CarritoService',
      ['obtener', 'actualizar', 'eliminar', 'setEnvio']);
    carrito.obtener.and.returnValue(of(itemsDeEjemplo()));
    carrito.actualizar.and.returnValue(of({}));
    carrito.eliminar.and.returnValue(of({}));
    (carrito as any).contadorObservable$ = of(0);

    envios = jasmine.createSpyObj<EnvioService>('EnvioService', ['obtenerTodos']);
    envios.obtenerTodos.and.returnValue(of([LIMA, PROVINCIA]));
  });

  // ============================================================
  describe('carga', () => {

    it('acepta la respuesta como lista', () => {
      montar();

      expect(componente.items.length).toBe(2);
    });

    it('acepta la respuesta envuelta en un objeto', () => {
      // El backend devuelve el carrito entero, no solo sus lineas
      carrito.obtener.and.returnValue(of({ id: 7, items: itemsDeEjemplo() } as any));

      montar();

      expect(componente.items.length).toBe(2);
    });

    it('no se rompe si el carrito viene vacio', () => {
      carrito.obtener.and.returnValue(of([]));

      montar();

      expect(componente.items).toEqual([]);
      expect(componente.total).toBe(0);
    });

    it('preselecciona el primer destino de envio', () => {
      montar();

      expect(componente.envioSeleccionado).toEqual(LIMA);
    });
  });

  // ============================================================
  describe('total', () => {

    it('suma cantidad por precio de cada linea', () => {
      montar();

      // 89.90x2 + 120.00x1. El decimal binario deja cola, y por eso la
      // plantilla lo pinta con number:'1.2-2'.
      expect(componente.total).toBeCloseTo(299.80, 2);
    });

    it('con recojo en tienda no cobra envio', () => {
      montar();

      componente.tipoEnvio = 'RECOJO';
      componente.calcularTotal();

      expect(componente.total).toBeCloseTo(299.80, 2);
    });

    it('con envio a domicilio suma el costo del destino', () => {
      montar();

      componente.tipoEnvio = 'ENVIO';
      componente.envioSeleccionado = PROVINCIA;
      componente.calcularTotal();

      expect(componente.total).toBeCloseTo(324.80, 2);
    });

    it('recalcula al cambiar de destino', () => {
      montar();
      componente.tipoEnvio = 'ENVIO';

      componente.envioSeleccionado = LIMA;
      componente.calcularTotal();
      expect(componente.total).toBeCloseTo(314.80, 2);

      componente.envioSeleccionado = PROVINCIA;
      componente.calcularTotal();
      expect(componente.total).toBeCloseTo(324.80, 2);
    });

    it('no cobra envio si aun no hay destino elegido', () => {
      envios.obtenerTodos.and.returnValue(of([]));
      montar();

      componente.tipoEnvio = 'ENVIO';
      componente.calcularTotal();

      expect(componente.total).toBeCloseTo(299.80, 2);
    });
  });

  // ============================================================
  describe('envio guardado para el pago', () => {

    it('guarda el destino cuando es a domicilio', () => {
      montar();

      componente.tipoEnvio = 'ENVIO';
      componente.envioSeleccionado = PROVINCIA;
      componente.calcularTotal();

      // El checkout lee de aqui el envio que se mandara al backend
      expect(carrito.setEnvio).toHaveBeenCalledWith(PROVINCIA);
    });

    it('lo borra al volver a recojo en tienda', () => {
      montar();
      componente.tipoEnvio = 'ENVIO';
      componente.envioSeleccionado = LIMA;
      componente.calcularTotal();

      componente.tipoEnvio = 'RECOJO';
      componente.calcularTotal();

      // Si quedara guardado, el cliente pagaria un envio que no pidio
      expect(carrito.setEnvio).toHaveBeenCalledWith(null);
    });
  });

  // ============================================================
  describe('cantidades', () => {

    it('incrementar manda la cantidad nueva', () => {
      montar();
      const linea = componente.items[0];

      componente.incrementar(linea);

      expect(linea.cantidad).toBe(3);
      expect(carrito.actualizar).toHaveBeenCalledWith(10, 3, 1);
    });

    it('decrementar manda la cantidad nueva mientras quede algo', () => {
      montar();
      const linea = componente.items[0];   // cantidad 2

      componente.decrementar(linea);

      expect(carrito.actualizar).toHaveBeenCalledWith(10, 1, 1);
      expect(carrito.eliminar).not.toHaveBeenCalled();
    });

    it('bajar de uno borra la linea en vez de dejarla en cero', () => {
      montar();
      const linea = componente.items[1];   // cantidad 1

      componente.decrementar(linea);

      expect(carrito.eliminar).toHaveBeenCalledWith(20, 3);
      expect(carrito.actualizar).not.toHaveBeenCalled();
    });

    it('vaciar la casilla de cantidad borra la linea', () => {
      montar();
      const linea = componente.items[0];

      // El input numerico deja el campo en null si se borra a mano
      linea.cantidad = null;
      componente.actualizarCantidad(linea);

      expect(carrito.eliminar).toHaveBeenCalledWith(10, 1);
    });

    it('borrar una linea recarga el carrito', () => {
      montar();
      const llamadasIniciales = carrito.obtener.calls.count();

      componente.eliminar(componente.items[0]);

      expect(carrito.obtener.calls.count()).toBe(llamadasIniciales + 1);
    });
  });
});
