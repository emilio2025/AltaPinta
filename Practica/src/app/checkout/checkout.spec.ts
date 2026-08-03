import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { CheckoutComponent } from './checkout';
import { CarritoService } from '../../services/carrito.service';
import { PedidoService } from '../../services/pedido.service';
import { TarjetaService } from '../../services/tarjeta.service';

/**
 * Pruebas del checkout.
 *
 * Es el ultimo paso antes de cobrar. Lo que se comprueba aqui es sobre todo
 * que NO se mande la peticion cuando los datos no dan, y que cuando el
 * backend la rechaza el cliente lea por que.
 */
describe('CheckoutComponent', () => {

  let carrito: jasmine.SpyObj<CarritoService>;
  let pedidos: jasmine.SpyObj<PedidoService>;
  let tarjetas: jasmine.SpyObj<TarjetaService>;
  let router: jasmine.SpyObj<Router>;
  let componente: CheckoutComponent;

  const LIMA = { id: 4, lugar: 'Lima', costo: 15 };

  const TARJETA_CON_SALDO = { id: 7, numero: '4111111111111111', saldo: 500 };
  const TARJETA_SIN_SALDO = { id: 8, numero: '4222222222222222', saldo: 10 };

  /** Dos lineas: 89.90 x2 y 120.00 x1 -> 299.80 */
  function itemsDeEjemplo() {
    return [
      { productoId: 10, cantidad: 2, precio: 89.90 },
      { productoId: 20, cantidad: 1, precio: 120.00 },
    ];
  }

  function montar() {
    TestBed.configureTestingModule({
      imports: [CheckoutComponent],
      providers: [
        { provide: CarritoService, useValue: carrito },
        { provide: PedidoService, useValue: pedidos },
        { provide: TarjetaService, useValue: tarjetas },
        { provide: Router, useValue: router },
        { provide: Location, useValue: jasmine.createSpyObj('Location', ['back']) },
      ],
    });

    const fixture = TestBed.createComponent(CheckoutComponent);
    componente = fixture.componentInstance;
    fixture.detectChanges();          // dispara ngOnInit
    return fixture;
  }

  /** Argumentos con los que se llamo a confirmar(). */
  function ultimoConfirmar() {
    return pedidos.confirmar.calls.mostRecent().args[0] as any;
  }

  beforeEach(() => {
    carrito = jasmine.createSpyObj<CarritoService>('CarritoService',
      ['obtener', 'getEnvio', 'resetContador']);
    carrito.obtener.and.returnValue(of(itemsDeEjemplo()));
    carrito.getEnvio.and.returnValue(null);

    pedidos = jasmine.createSpyObj<PedidoService>('PedidoService', ['confirmar']);
    pedidos.confirmar.and.returnValue(of({ pedidoId: 99, estado: 'PAGADO' } as any));

    tarjetas = jasmine.createSpyObj<TarjetaService>('TarjetaService', ['misTarjetas']);
    tarjetas.misTarjetas.and.returnValue(of([TARJETA_CON_SALDO, TARJETA_SIN_SALDO] as any));

    router = jasmine.createSpyObj('Router', ['navigate']);
  });

  // ============================================================
  describe('carga inicial', () => {

    it('calcula el total del carrito', () => {
      montar();

      expect(componente.total).toBeCloseTo(299.80, 2);
    });

    it('acepta el carrito envuelto en un objeto', () => {
      carrito.obtener.and.returnValue(of({ id: 3, items: itemsDeEjemplo() } as any));

      montar();

      expect(componente.total).toBeCloseTo(299.80, 2);
    });

    it('trae las tarjetas del cliente', () => {
      montar();

      expect(componente.tarjetas.length).toBe(2);
    });

    it('avisa si las tarjetas no cargan', () => {
      tarjetas.misTarjetas.and.returnValue(throwError(() => new Error('sin red')));

      montar();

      expect(componente.error).toBe('Error cargando tarjetas');
    });

    it('recoge el envio elegido en el carrito', () => {
      carrito.getEnvio.and.returnValue(LIMA);

      montar();

      expect(componente.envioSeleccionadoId).toBe(4);
    });

    it('sin envio elegido queda como recojo en tienda', () => {
      montar();

      // null significa recojo en tienda, no "falta el dato"
      expect(componente.envioSeleccionadoId).toBeNull();
    });
  });

  // ============================================================
  describe('lo que impide pagar', () => {

    it('sin tarjeta elegida no se manda nada', () => {
      montar();

      componente.pagar();

      expect(pedidos.confirmar).not.toHaveBeenCalled();
      expect(componente.error).toBe('Seleccione una tarjeta');
    });

    it('con saldo insuficiente tampoco', () => {
      montar();

      componente.tarjetaSeleccionada = TARJETA_SIN_SALDO;   // 10 para un total de 299.80
      componente.pagar();

      expect(pedidos.confirmar).not.toHaveBeenCalled();
      expect(componente.error).toBe('Saldo insuficiente en la tarjeta');
    });

    it('un saldo justo si deja pagar', () => {
      // El limite es "menor que", no "menor o igual": pagar con el saldo
      // exacto tiene que funcionar.
      montar();
      componente.tarjetaSeleccionada = { id: 9, saldo: componente.total };

      componente.pagar();

      expect(pedidos.confirmar).toHaveBeenCalled();
    });
  });

  // ============================================================
  describe('pago correcto', () => {

    it('manda la tarjeta y el envio elegidos', () => {
      carrito.getEnvio.and.returnValue(LIMA);
      montar();
      componente.tarjetaSeleccionada = TARJETA_CON_SALDO;

      componente.pagar();

      expect(ultimoConfirmar()).toEqual({ envioId: 4, tarjetaId: 7 });
    });

    it('en recojo en tienda manda el envio en null', () => {
      montar();
      componente.tarjetaSeleccionada = TARJETA_CON_SALDO;

      componente.pagar();

      expect(ultimoConfirmar().envioId).toBeNull();
    });

    it('guarda la respuesta y marca el exito', () => {
      montar();
      componente.tarjetaSeleccionada = TARJETA_CON_SALDO;

      componente.pagar();

      expect(componente.exito).toBeTrue();
      expect(componente.pedidoResponse.pedidoId).toBe(99);
      expect(componente.loading).toBeFalse();
    });

    it('vacia el contador del carrito de la cabecera', () => {
      montar();
      componente.tarjetaSeleccionada = TARJETA_CON_SALDO;

      componente.pagar();

      // Si no, la cabecera seguiria mostrando articulos ya comprados
      expect(carrito.resetContador).toHaveBeenCalled();
    });

    it('lleva a mis pedidos despues de enseñar la confirmacion', fakeAsync(() => {
      montar();
      componente.tarjetaSeleccionada = TARJETA_CON_SALDO;

      componente.pagar();

      expect(router.navigate).not.toHaveBeenCalled();

      tick(1500);

      expect(router.navigate).toHaveBeenCalledWith(['/mis-pedidos']);
    }));
  });

  // ============================================================
  describe('pago rechazado', () => {

    /** Lo que devuelve GlobalExceptionHandler del backend. */
    function rechazo(mensaje: string) {
      return throwError(() => ({ status: 400, error: { message: mensaje } }));
    }

    it('muestra el motivo que devuelve el backend', () => {
      // El backend responde {"message": "..."}, no una cadena suelta. Leer
      // err.error entero deja un objeto en el campo, y la plantilla lo pinta
      // como "[object Object]" justo cuando la compra falla.
      montar();
      pedidos.confirmar.and.returnValue(rechazo('Stock insuficiente: Legging (talla M)'));
      componente.tarjetaSeleccionada = TARJETA_CON_SALDO;

      componente.pagar();

      expect(componente.error).toBe('Stock insuficiente: Legging (talla M)');
    });

    it('libera el boton al fallar', () => {
      montar();
      pedidos.confirmar.and.returnValue(rechazo('Saldo insuficiente en la tarjeta'));
      componente.tarjetaSeleccionada = TARJETA_CON_SALDO;

      componente.pagar();

      expect(componente.loading).toBeFalse();
      expect(componente.exito).toBeFalse();
    });

    it('con un error sin cuerpo usa un mensaje generico', () => {
      montar();
      pedidos.confirmar.and.returnValue(throwError(() => ({ status: 0 })));
      componente.tarjetaSeleccionada = TARJETA_CON_SALDO;

      componente.pagar();

      expect(typeof componente.error).toBe('string');
      expect(componente.error.length).toBeGreaterThan(0);
    });

    it('no navega a mis pedidos si el pago fallo', fakeAsync(() => {
      montar();
      pedidos.confirmar.and.returnValue(rechazo('Stock insuficiente'));
      componente.tarjetaSeleccionada = TARJETA_CON_SALDO;

      componente.pagar();
      tick(2000);

      expect(router.navigate).not.toHaveBeenCalled();
    }));
  });
});
