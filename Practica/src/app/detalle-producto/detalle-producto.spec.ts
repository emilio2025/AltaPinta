import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { of, throwError } from 'rxjs';

import { DetalleProductoComponent } from './detalle-producto';
import { ProductoService } from '../../services/producto.service';
import { FavoritoService } from '../../services/favorito.service';
import { CarritoService } from '../../services/carrito.service';
import { CarritoStateService } from '../../services/carrito-state.service';
import { EnvioService } from '../../services/envio.service';

/**
 * Pruebas de la ficha de producto.
 *
 * Es el unico sitio donde se elige talla, asi que aqui se decide que
 * termina en el carrito. Un fallo aqui deja comprar una talla agotada.
 */
describe('DetalleProductoComponent', () => {

  let productos: jasmine.SpyObj<ProductoService>;
  let favoritos: jasmine.SpyObj<FavoritoService>;
  let carrito: jasmine.SpyObj<CarritoService>;
  let estado: jasmine.SpyObj<CarritoStateService>;
  let componente: DetalleProductoComponent;

  const TALLA_M   = { id: 100, talla: { id: 2, nombre: 'M' }, stock: 5 };
  const TALLA_L   = { id: 101, talla: { id: 3, nombre: 'L' }, stock: 0 };
  const TALLA_XL  = { id: 102, talla: { id: 4, nombre: 'XL' } };   // sin stock declarado

  function productoDeEjemplo(extra: Record<string, unknown> = {}) {
    return {
      id: 10,
      nombre: 'Camiseta tecnica de running',
      precio: 89.90,
      imagenUrl: '/imagenes/prenda-camiseta.svg',
      tallasDisponibles: [TALLA_M, TALLA_L, TALLA_XL],
      ...extra,
    };
  }

  function montar(producto: unknown = productoDeEjemplo()) {
    productos.getUno.and.returnValue(of(producto as any));

    TestBed.configureTestingModule({
      imports: [DetalleProductoComponent],
      providers: [
        { provide: ProductoService, useValue: productos },
        { provide: FavoritoService, useValue: favoritos },
        { provide: CarritoService, useValue: carrito },
        { provide: CarritoStateService, useValue: estado },
        { provide: EnvioService, useValue: { obtenerTodos: () => of([]) } },
        { provide: Router, useValue: jasmine.createSpyObj('Router', ['navigate']) },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id: '10' }) } },
        },
      ],
    });

    const fixture = TestBed.createComponent(DetalleProductoComponent);
    componente = fixture.componentInstance;
    fixture.detectChanges();          // dispara ngOnInit
    return fixture;
  }

  beforeEach(() => {
    productos = jasmine.createSpyObj<ProductoService>('ProductoService', ['getUno', 'getCategorias']);
    productos.getCategorias.and.returnValue(of([]));

    favoritos = jasmine.createSpyObj<FavoritoService>('FavoritoService',
      ['getFavoritos', 'agregar', 'eliminar']);
    favoritos.getFavoritos.and.returnValue(of([]));
    favoritos.agregar.and.returnValue(of({}));
    favoritos.eliminar.and.returnValue(of({}));

    carrito = jasmine.createSpyObj<CarritoService>('CarritoService', ['agregar']);
    carrito.agregar.and.returnValue(of({}));

    estado = jasmine.createSpyObj<CarritoStateService>('CarritoStateService', ['incrementar']);
  });

  // ============================================================
  describe('galeria', () => {

    it('usa las fotos del producto cuando las tiene', () => {
      montar(productoDeEjemplo({
        imagenes: [{ url: '/imagenes/frente.svg' }, { url: '/imagenes/espalda.svg' }],
      }));

      expect(componente.galeria()).toEqual(['/imagenes/frente.svg', '/imagenes/espalda.svg']);
      expect(componente.imagenActiva).toBe('/imagenes/frente.svg');
    });

    it('recurre a la imagen principal si no hay galeria', () => {
      montar();

      expect(componente.galeria()).toEqual(['/imagenes/prenda-camiseta.svg']);
      expect(componente.imagenActiva).toBe('/imagenes/prenda-camiseta.svg');
    });

    it('no falla con un producto sin ninguna imagen', () => {
      montar(productoDeEjemplo({ imagenUrl: null }));

      expect(componente.galeria()).toEqual([]);
    });

    it('al pulsar una miniatura cambia la foto grande', () => {
      montar(productoDeEjemplo({
        imagenes: [{ url: '/imagenes/frente.svg' }, { url: '/imagenes/espalda.svg' }],
      }));

      componente.verImagen('/imagenes/espalda.svg');

      expect(componente.imagenActiva).toBe('/imagenes/espalda.svg');
    });
  });

  // ============================================================
  describe('eleccion de talla', () => {

    it('acepta una talla con stock', () => {
      montar();

      componente.seleccionarTalla(TALLA_M);

      expect(componente.tallaSeleccionada).toBe(TALLA_M);
    });

    it('no deja elegir una talla agotada', () => {
      montar();

      componente.seleccionarTalla(TALLA_L);

      expect(componente.tallaSeleccionada).toBeNull();
    });

    it('no deja elegir una talla sin stock declarado', () => {
      // Si el backend omite el campo, lo prudente es tratarlo como agotado
      montar();

      componente.seleccionarTalla(TALLA_XL);

      expect(componente.tallaSeleccionada).toBeNull();
    });

    it('elegir una agotada no borra la que ya estaba puesta', () => {
      montar();

      componente.seleccionarTalla(TALLA_M);
      componente.seleccionarTalla(TALLA_L);

      expect(componente.tallaSeleccionada).toBe(TALLA_M);
    });
  });

  // ============================================================
  describe('añadir al carrito', () => {

    it('no hace nada sin talla elegida', () => {
      montar();

      componente.agregarCarrito();

      expect(carrito.agregar).not.toHaveBeenCalled();
    });

    it('manda el id de la talla, no el de la fila de stock', () => {
      // TALLA_M.id es 100 (producto_talla) y TALLA_M.talla.id es 2 (la talla).
      // Confundirlos añade al carrito una talla que no existe.
      montar();

      componente.seleccionarTalla(TALLA_M);
      componente.agregarCarrito();

      expect(carrito.agregar).toHaveBeenCalledWith(10, 1, 2);
    });

    it('suma uno al contador de la cabecera', () => {
      montar();

      componente.seleccionarTalla(TALLA_M);
      componente.agregarCarrito();

      expect(estado.incrementar).toHaveBeenCalled();
    });

    it('libera el boton al terminar', () => {
      montar();

      componente.seleccionarTalla(TALLA_M);
      componente.agregarCarrito();

      expect(componente.loadingCarrito).toBeFalse();
    });

    it('libera el boton tambien si la peticion falla', () => {
      // El boton se deshabilita con loadingCarrito; si un error lo deja en
      // true, el cliente no puede reintentar sin recargar la pagina.
      montar();
      spyOn(console, 'error');       // el componente registra el fallo; no ensuciar la salida
      carrito.agregar.and.returnValue(throwError(() => new Error('sin red')));

      componente.seleccionarTalla(TALLA_M);
      componente.agregarCarrito();

      expect(componente.loadingCarrito).toBeFalse();
      expect(estado.incrementar).not.toHaveBeenCalled();
    });
  });

  // ============================================================
  describe('favorito', () => {

    it('marca el corazon si el producto ya estaba en favoritos', () => {
      favoritos.getFavoritos.and.returnValue(of([{ id: 10 }] as any));

      montar();

      expect(componente.esFav).toBeTrue();
    });

    it('lo deja apagado si no estaba', () => {
      favoritos.getFavoritos.and.returnValue(of([{ id: 99 }] as any));

      montar();

      expect(componente.esFav).toBeFalse();
    });

    it('alterna entre añadir y quitar', () => {
      montar();

      componente.toggleFav();
      expect(favoritos.agregar).toHaveBeenCalledWith(10);
      expect(componente.esFav).toBeTrue();

      componente.toggleFav();
      expect(favoritos.eliminar).toHaveBeenCalledWith(10);
      expect(componente.esFav).toBeFalse();
    });
  });
});
