import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';

import { MenuComponent } from './menu';
import { ProductoService, Pagina, Producto } from '../../services/producto.service';
import { FavoritoService } from '../../services/favorito.service';
import { CarritoService } from '../../services/carrito.service';
import { AuthService } from '../auth.service';

/**
 * Pruebas del catalogo.
 *
 * Es la pantalla con mas logica de la tienda: cuatro filtros que se
 * combinan, busqueda con retardo, orden y paginacion. Todo eso se
 * traduce en UNA peticion al backend, asi que casi todas las
 * comprobaciones miran con que argumentos se llamo a buscar().
 */
describe('MenuComponent', () => {

  let producto: jasmine.SpyObj<ProductoService>;
  let favoritos: jasmine.SpyObj<FavoritoService>;
  let componente: MenuComponent;

  /** Respuesta vacia con la forma que devuelve Spring Data. */
  function pagina(total = 0): Pagina<Producto> {
    return {
      content: [], totalElements: total, totalPages: Math.ceil(total / 12),
      number: 0, size: 12, first: true, last: true,
    };
  }

  /** Monta el componente; datosRuta simula lo que declara app.routes. */
  function montar(datosRuta: Record<string, unknown> = {}) {
    TestBed.configureTestingModule({
      imports: [MenuComponent],
      providers: [
        { provide: ProductoService, useValue: producto },
        { provide: FavoritoService, useValue: favoritos },
        { provide: CarritoService, useValue: { contadorObservable$: of(0) } },
        { provide: AuthService, useValue: jasmine.createSpyObj('AuthService', ['logout']) },
        { provide: Router, useValue: jasmine.createSpyObj('Router', ['navigate']) },
        { provide: ActivatedRoute, useValue: { snapshot: { data: datosRuta } } },
      ],
    });

    const fixture = TestBed.createComponent(MenuComponent);
    componente = fixture.componentInstance;
    fixture.detectChanges();          // dispara ngOnInit
    return fixture;
  }

  /** Argumentos de la ultima llamada a buscar(). */
  function ultimaBusqueda() {
    const llamadas = producto.buscar.calls.all();
    return llamadas[llamadas.length - 1].args[0] as any;
  }

  beforeEach(() => {
    producto = jasmine.createSpyObj<ProductoService>('ProductoService',
      ['buscar', 'getCategorias', 'getTipos', 'getDeportes', 'getTallas']);
    producto.buscar.and.returnValue(of(pagina(153)));
    producto.getCategorias.and.returnValue(of([]));
    producto.getTipos.and.returnValue(of([]));
    producto.getDeportes.and.returnValue(of([]));
    producto.getTallas.and.returnValue(of([]));

    favoritos = jasmine.createSpyObj<FavoritoService>('FavoritoService',
      ['getFavoritos', 'agregar', 'eliminar']);
    favoritos.getFavoritos.and.returnValue(of([]));
  });

  // ============================================================
  describe('carga inicial', () => {

    it('pide la primera pagina sin filtros', () => {
      montar();

      expect(producto.buscar).toHaveBeenCalled();
      const args = ultimaBusqueda();
      expect(args.page).toBe(0);
      expect(args.categoria).toBe('');
      expect(args.nombre).toBe('');
    });

    it('guarda el total que devuelve el backend', () => {
      montar();

      expect(componente.totalResultados).toBe(153);
    });

    it('preselecciona la categoria que trae la ruta', () => {
      // Es lo que hace que /mujer funcione reutilizando esta pantalla
      montar({ categoria: 'Mujer' });

      expect(componente.categoriaSeleccionada).toBe('Mujer');
      expect(ultimaBusqueda().categoria).toBe('Mujer');
    });
  });

  // ============================================================
  describe('filtros', () => {

    it('los cuatro viajan juntos en la misma peticion', () => {
      montar();

      componente.filtrarCategoria('Mujer');
      componente.filtrarDeporte('Running');
      componente.filtrarTipo('Polo');
      componente.tallaSeleccionada = { nombre: 'M' };
      componente.cambiarTalla();

      // Antes cada filtro lanzaba su propia consulta y se pisaban entre si
      const args = ultimaBusqueda();
      expect(args.categoria).toBe('Mujer');
      expect(args.deporte).toBe('Running');
      expect(args.tipo).toBe('Polo');
      expect(args.talla).toBe('M');
    });

    it('volver a pulsar el mismo filtro lo desactiva', () => {
      montar();

      componente.filtrarCategoria('Mujer');
      expect(componente.categoriaSeleccionada).toBe('Mujer');

      componente.filtrarCategoria('Mujer');
      expect(componente.categoriaSeleccionada).toBe('');
    });

    it('cambiar de filtro devuelve a la primera pagina', () => {
      montar();

      componente.irAPagina(4);
      expect(componente.pagina).toBe(4);

      componente.filtrarDeporte('Yoga');

      // Seguir en la pagina 5 de un resultado distinto no tiene sentido
      expect(componente.pagina).toBe(0);
      expect(ultimaBusqueda().page).toBe(0);
    });

    it('limpiar los deja todos vacios', () => {
      montar();

      componente.filtrarCategoria('Mujer');
      componente.filtrarDeporte('Running');
      componente.limpiarFiltros();

      expect(componente.hayFiltros).toBeFalse();
      expect(componente.filtrosActivos.length).toBe(0);
    });

    it('cada filtro activo se puede quitar desde su ficha', () => {
      montar();

      componente.filtrarCategoria('Mujer');
      componente.filtrarDeporte('Running');

      expect(componente.filtrosActivos.map(f => f.etiqueta))
        .toEqual(['Mujer', 'Running']);

      componente.filtrosActivos[0].quitar();
      expect(componente.categoriaSeleccionada).toBe('');
    });
  });

  // ============================================================
  describe('busqueda', () => {

    it('espera a que el usuario deje de escribir', fakeAsync(() => {
      montar();
      const alMontar = producto.buscar.calls.count();

      // Cinco pulsaciones seguidas
      'polo '.split('').forEach(() => componente.buscar());

      expect(producto.buscar.calls.count())
        .withContext('no debe consultar mientras se escribe')
        .toBe(alMontar);

      tick(300);

      expect(producto.buscar.calls.count())
        .withContext('una sola consulta al parar')
        .toBe(alMontar + 1);
    }));

    it('envia el termino escrito', fakeAsync(() => {
      montar();

      componente.busqueda = 'legging';
      componente.buscar();
      tick(300);

      expect(ultimaBusqueda().nombre).toBe('legging');
    }));
  });

  // ============================================================
  describe('orden y paginacion', () => {

    it('el orden se manda al backend, no se aplica aqui', () => {
      montar();

      componente.ordenSeleccionado = 'precio,asc';
      componente.cambiarOrden();

      // Ordenar en el navegador solo reordenaria la pagina visible
      expect(ultimaBusqueda().orden).toBe('precio,asc');
    });

    it('el paginador de PrimeNG cambia de pagina', () => {
      montar();

      componente.onCambioPagina({ page: 3 } as any);

      expect(componente.pagina).toBe(3);
      expect(ultimaBusqueda().page).toBe(3);
    });

    it('ignora paginas fuera de rango', () => {
      montar();
      componente.totalPaginas = 13;

      componente.irAPagina(-1);
      expect(componente.pagina).toBe(0);

      componente.irAPagina(99);
      expect(componente.pagina).toBe(0);
    });

    it('primerElemento traduce la pagina al indice que espera el paginador', () => {
      montar();

      componente.pagina = 3;

      // PrimeNG cuenta por elemento, no por pagina
      expect(componente.primerElemento).toBe(36);
    });
  });
});
