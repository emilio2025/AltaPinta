import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { ProductoComponent } from './productos';
import { ProductoService, Pagina, Producto } from '../../services/producto.service';

/**
 * Pruebas del mantenimiento de productos (panel de administracion).
 *
 * Guardar un producto encadena cuatro peticiones —subir imagenes, crear,
 * sincronizar tallas y sincronizar galeria— y cada una depende de la
 * anterior. Si una se rompe, el producto queda a medias en la base de datos.
 */
describe('ProductoComponent (admin)', () => {

  let service: jasmine.SpyObj<ProductoService>;
  let componente: ProductoComponent;

  const TALLAS = [
    { id: 1, nombre: 'S' },
    { id: 2, nombre: 'M' },
    { id: 3, nombre: 'L' },
  ];

  function pagina(total = 25): Pagina<Producto> {
    return {
      content: [], totalElements: total, totalPages: Math.ceil(total / 10),
      number: 0, size: 10, first: true, last: true,
    };
  }

  /** Argumentos de la ultima llamada a buscar(). */
  function ultimaBusqueda() {
    const llamadas = service.buscar.calls.all();
    return llamadas[llamadas.length - 1].args[0] as any;
  }

  function montar() {
    TestBed.configureTestingModule({
      imports: [ProductoComponent],
      providers: [
        { provide: ProductoService, useValue: service },
        { provide: Router, useValue: jasmine.createSpyObj('Router', ['navigate']) },
      ],
    });

    const fixture = TestBed.createComponent(ProductoComponent);
    componente = fixture.componentInstance;
    fixture.detectChanges();          // dispara ngOnInit
    return fixture;
  }

  beforeEach(() => {
    service = jasmine.createSpyObj<ProductoService>('ProductoService', [
      'buscar', 'getCategorias', 'getTipos', 'getDeportes', 'getTallas',
      'crearProducto', 'editarProducto', 'eliminarProducto',
      'subirImagen', 'sincronizarTallas', 'sincronizarImagenes',
    ]);
    service.buscar.and.returnValue(of(pagina()));
    service.getCategorias.and.returnValue(of([]));
    service.getTipos.and.returnValue(of([]));
    service.getDeportes.and.returnValue(of([]));
    service.getTallas.and.returnValue(of(TALLAS));
    service.crearProducto.and.returnValue(of({ id: 77 } as any));
    service.editarProducto.and.returnValue(of({}));
    service.eliminarProducto.and.returnValue(of({}));
    service.sincronizarTallas.and.returnValue(of({}));
    service.sincronizarImagenes.and.returnValue(of([]));
    service.subirImagen.and.returnValue(of({ url: '/imagenes/subida.jpg' }));
  });

  // ============================================================
  describe('comparador de los desplegables', () => {

    it('considera iguales dos objetos con el mismo id', () => {
      // La categoria del producto y la de la lista vienen de peticiones
      // distintas: mismo contenido, distinta instancia. Comparando por
      // referencia el desplegable salia en blanco al editar.
      montar();

      const deLaLista    = { id: 3, nombre: 'Mujer' };
      const delProducto  = { id: 3, nombre: 'Mujer' };

      expect(componente.mismoId(deLaLista, delProducto)).toBeTrue();
      expect(deLaLista === delProducto).toBeFalse();
    });

    it('distingue ids distintos', () => {
      montar();

      expect(componente.mismoId({ id: 1 }, { id: 2 })).toBeFalse();
    });

    it('no revienta con valores vacios', () => {
      montar();

      expect(componente.mismoId(null, { id: 1 })).toBeFalse();
      expect(componente.mismoId(undefined, undefined)).toBeTrue();
    });
  });

  // ============================================================
  describe('paginacion', () => {

    it('traduce la pagina de la tabla a la del backend', () => {
      montar();

      // La tabla numera desde 1 y el backend desde 0
      expect(componente.paginaActual).toBe(1);
      expect(ultimaBusqueda().page).toBe(0);
      expect(ultimaBusqueda().size).toBe(10);
    });

    it('pide solo la pagina visible, no el catalogo entero', () => {
      montar();

      componente.irAPagina(3);

      expect(ultimaBusqueda().page).toBe(2);
      expect(ultimaBusqueda().size).toBe(10);
    });

    it('ignora paginas fuera de rango', () => {
      montar();          // 25 resultados -> 3 paginas

      componente.irAPagina(0);
      expect(componente.paginaActual).toBe(1);

      componente.irAPagina(4);
      expect(componente.paginaActual).toBe(1);
    });

    it('deja al menos una pagina aunque no haya resultados', () => {
      service.buscar.and.returnValue(of(pagina(0)));
      montar();

      expect(componente.totalPaginas).toBe(1);
    });

    it('vuelve a la primera pagina al recargar', () => {
      montar();
      componente.irAPagina(3);

      componente.listar();

      expect(componente.paginaActual).toBe(1);
    });
  });

  // ============================================================
  describe('tallas del formulario', () => {

    it('parte de todas las tallas sin marcar', () => {
      montar();

      expect(componente.tallasForm.length).toBe(3);
      expect(componente.tallasForm.every(t => !t.seleccionada)).toBeTrue();
    });

    it('al editar marca las que el producto ya tiene con su stock', () => {
      montar();

      componente.editar({
        id: 5, nombre: 'Polo', tallasDisponibles: [
          { talla: { id: 2, nombre: 'M' }, stock: 7 },
        ],
      });

      const m = componente.tallasForm.find(t => t.talla.id === 2)!;
      expect(m.seleccionada).toBeTrue();
      expect(m.stock).toBe(7);

      const s = componente.tallasForm.find(t => t.talla.id === 1)!;
      expect(s.seleccionada).toBeFalse();
      expect(s.stock).toBe(0);
    });

    it('solo envia las tallas marcadas', () => {
      montar();

      componente.tallasForm[0].seleccionada = true;
      componente.tallasForm[0].stock = 4;
      componente.tallasForm[2].seleccionada = true;
      componente.tallasForm[2].stock = 9;

      componente.guardar();

      expect(service.sincronizarTallas).toHaveBeenCalledWith(77, [
        { tallaId: 1, stock: 4 },
        { tallaId: 3, stock: 9 },
      ]);
    });
  });

  // ============================================================
  describe('guardado', () => {

    it('crea y encadena tallas e imagenes con el id nuevo', () => {
      montar();
      componente.producto.nombre = 'Casaca cortavientos';

      componente.guardar();

      expect(service.crearProducto).toHaveBeenCalled();
      expect(service.sincronizarTallas).toHaveBeenCalledWith(77, []);
      expect(service.sincronizarImagenes).toHaveBeenCalledWith(77, []);
      expect(service.editarProducto).not.toHaveBeenCalled();
    });

    it('en edicion actualiza en vez de crear', () => {
      montar();
      componente.editar({ id: 5, nombre: 'Polo' });

      componente.guardar();

      expect(service.editarProducto).toHaveBeenCalled();
      expect(service.crearProducto).not.toHaveBeenCalled();
      expect(service.sincronizarTallas).toHaveBeenCalledWith(5, []);
    });

    it('sube primero las imagenes pendientes y usa la primera como portada', () => {
      montar();
      componente.imagenesForm[0].file = new File(['x'], 'frente.jpg');

      componente.guardar();

      expect(service.subirImagen).toHaveBeenCalled();

      // Se comprueba sobre lo enviado y no sobre componente.producto, que
      // para cuando termina la cadena ya lo ha vaciado limpiar().
      const enviado = service.crearProducto.calls.mostRecent().args[0];
      expect(enviado.imagenUrl).toBe('/imagenes/subida.jpg');
      expect(service.sincronizarImagenes).toHaveBeenCalledWith(77, ['/imagenes/subida.jpg']);
      expect(componente.subiendoImagen).toBeFalse();
    });

    it('si falla la subida no llega a crear el producto', () => {
      montar();
      service.subirImagen.and.returnValue(throwError(() => ({ error: { message: 'Formato no admitido' } })));
      componente.imagenesForm[0].file = new File(['x'], 'malo.txt');

      componente.guardar();

      expect(componente.mensajeError).toBe('Formato no admitido');
      expect(componente.subiendoImagen).toBeFalse();
      expect(service.crearProducto).not.toHaveBeenCalled();
    });

    it('muestra el motivo que devuelve el backend', () => {
      montar();
      service.crearProducto.and.returnValue(throwError(() => ({ error: { message: 'El nombre ya existe' } })));

      componente.guardar();

      expect(componente.mensajeError).toBe('El nombre ya existe');
    });

    it('limpia el formulario al terminar bien', () => {
      montar();
      componente.producto.nombre = 'Casaca';

      componente.guardar();

      expect(componente.producto.nombre).toBe('');
      expect(componente.modoEdicion).toBeFalse();
    });
  });

  // ============================================================
  describe('tabla', () => {

    it('el stock total es la suma de todas las tallas', () => {
      montar();

      expect(componente.stockTotal({
        tallasDisponibles: [{ stock: 3 }, { stock: 4 }, { stock: 0 }],
      })).toBe(7);
    });

    it('un producto sin tallas tiene stock cero', () => {
      montar();

      expect(componente.stockTotal({})).toBe(0);
      expect(componente.stockTotal({ tallasDisponibles: [{}] })).toBe(0);
    });

    it('editar trabaja sobre una copia, no sobre la fila de la tabla', () => {
      montar();
      const fila = { id: 5, nombre: 'Polo original' };

      componente.editar(fila);
      componente.producto.nombre = 'Polo cambiado';

      // Si fuera la misma referencia, la tabla cambiaria sin haber guardado
      expect(fila.nombre).toBe('Polo original');
    });
  });
});
