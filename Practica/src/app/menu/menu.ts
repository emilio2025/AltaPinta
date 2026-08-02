import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule, DecimalPipe } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { FavoritoService } from '../../services/favorito.service';
import { ProductoService } from '../../services/producto.service';
import { CarritoService } from '../../services/carrito.service';
import { AuthService } from '../auth.service';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

import { SelectModule } from 'primeng/select';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [
    CommonModule, RouterModule, HttpClientModule, FormsModule,
    SelectModule, PaginatorModule
  ],
  templateUrl: './menu.html',
  styleUrls: ['./menu.css']
})
export class MenuComponent implements OnInit {

  productos: any[] = [];
  filtrados: any[] = [];

  categorias: any[] = [];
  tipos: any[] = [];
  deportes: any[] = [];

  favoritosIds: number[] = [];
  busqueda: string = "";
  contadorCarrito = 0;
  tipoSeleccionado: string = '';
  categoriaSeleccionada: string = '';
  deporteSeleccionado: string = '';
  tallas: any[] = [];
  tallaSeleccionada: any = null;

  // Orden del catalogo. Lo aplica el backend sobre el catalogo entero.
  ordenSeleccionado = '';
  readonly opcionesOrden = [
    { etiqueta: 'Relevancia',            valor: '' },
    { etiqueta: 'Precio: de menor a mayor', valor: 'precio,asc' },
    { etiqueta: 'Precio: de mayor a menor', valor: 'precio,desc' },
    { etiqueta: 'Nombre: A-Z',           valor: 'nombre,asc' }
  ];

  // Panel de filtros plegable en movil
  filtrosAbiertos = false;

  // Paginacion. El backend numera las paginas desde 0.
  pagina = 0;
  readonly tamanoPagina = 12;
  totalPaginas = 0;
  totalResultados = 0;

  // Evita lanzar una consulta por cada tecla pulsada en el buscador.
  private terminoBuscado = new Subject<string>();

  constructor(
    private productoService: ProductoService,
    private favService: FavoritoService,
    private carritoService: CarritoService,
    private router: Router ,
        private authService: AuthService,
    private route: ActivatedRoute
  ){}

  ngOnInit(): void {
    // Las rutas /varon, /mujer, /nino y /bebe apuntan a esta misma pantalla
    // con la categoria preseleccionada. Antes cada una era un componente
    // propio que duplicaba el catalogo entero.
    const categoriaDeLaRuta = this.route.snapshot.data['categoria'];
    if (categoriaDeLaRuta) {
      this.categoriaSeleccionada = categoriaDeLaRuta;
    }

    this.cargarProductos();
    this.cargarCategorias();
    this.cargarTipos();
    this.cargarDeportes();
    this.cargarFavoritos();
    this.cargarTallas();

    this.carritoService.contadorObservable$
    .subscribe(c => this.contadorCarrito = c);

    this.terminoBuscado
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe(() => this.reiniciarYFiltrar());
  }

  /**
   * Pide al backend la pagina actual con todos los filtros activos.
   *
   * Antes cada filtro trabajaba por su cuenta sobre el catalogo ya
   * descargado y se pisaban entre si: elegir un tipo descartaba la
   * categoria seleccionada. Ahora los cuatro viajan juntos en la consulta.
   */
  aplicarFiltros() {
    this.productoService.buscar({
      nombre: this.busqueda,
      categoria: this.categoriaSeleccionada,
      tipo: this.tipoSeleccionado,
      deporte: this.deporteSeleccionado,
      talla: this.tallaSeleccionada?.nombre,
      orden: this.ordenSeleccionado,
      page: this.pagina,
      size: this.tamanoPagina
    }).subscribe(res => {
      this.filtrados = res.content;
      this.totalPaginas = res.totalPages;
      this.totalResultados = res.totalElements;
    });
  }

  /** Filtros activos, para pintarlos como fichas retirables. */
  get filtrosActivos(): { etiqueta: string; quitar: () => void }[] {
    const fichas: { etiqueta: string; quitar: () => void }[] = [];

    if (this.categoriaSeleccionada) {
      fichas.push({ etiqueta: this.categoriaSeleccionada, quitar: () => this.filtrarCategoria(this.categoriaSeleccionada) });
    }
    if (this.deporteSeleccionado) {
      fichas.push({ etiqueta: this.deporteSeleccionado, quitar: () => this.filtrarDeporte(this.deporteSeleccionado) });
    }
    if (this.tipoSeleccionado) {
      fichas.push({ etiqueta: this.tipoSeleccionado, quitar: () => this.filtrarTipo(this.tipoSeleccionado) });
    }
    if (this.tallaSeleccionada) {
      fichas.push({ etiqueta: `Talla ${this.tallaSeleccionada.nombre}`, quitar: () => { this.tallaSeleccionada = null; this.reiniciarYFiltrar(); } });
    }
    return fichas;
  }

  get hayFiltros(): boolean {
    return this.filtrosActivos.length > 0 || !!this.busqueda.trim();
  }

  /** Vuelve a la primera pagina: al cambiar un filtro, seguir en la 5 no tiene sentido. */
  private reiniciarYFiltrar() {
    this.pagina = 0;
    this.aplicarFiltros();
  }

  cargarTallas(){
    this.productoService.getTallas().subscribe(res => this.tallas = res);
  }

  cargarProductos(){
    this.aplicarFiltros();
  }

  cargarCategorias(){
    this.productoService.getCategorias().subscribe(res => this.categorias = res);
  }

  cargarTipos(){
    this.productoService.getTipos().subscribe(res => this.tipos = res);
  }

  filtrarTipo(tipo:string){
    this.tipoSeleccionado = this.tipoSeleccionado === tipo ? '' : tipo;
    this.reiniciarYFiltrar();
  }

  filtrarCategoria(cat:string){
    this.categoriaSeleccionada = this.categoriaSeleccionada === cat ? '' : cat;
    this.reiniciarYFiltrar();
  }

  filtrarDeporte(deporte:string){
    this.deporteSeleccionado = this.deporteSeleccionado === deporte ? '' : deporte;
    this.reiniciarYFiltrar();
  }

  cargarDeportes(){
    this.productoService.getDeportes().subscribe(res => this.deportes = res);
  }

  /**
   * El desplegable de tallas ya trae su propia opcion "Todas", y [(ngModel)]
   * actualiza tallaSeleccionada antes de que salte (change), asi que aqui solo
   * hay que recargar: alternar el valor lo dejaria siempre en null.
   */
  cambiarTalla(){
    this.reiniciarYFiltrar();
  }

  cambiarOrden(){
    this.reiniciarYFiltrar();
  }

  limpiarFiltros(){
    this.busqueda = '';
    this.categoriaSeleccionada = '';
    this.tipoSeleccionado = '';
    this.deporteSeleccionado = '';
    this.tallaSeleccionada = null;
    this.reiniciarYFiltrar();
  }

  buscar(){
    this.terminoBuscado.next(this.busqueda);
  }

  irAPagina(n: number){
    if (n < 0 || n >= this.totalPaginas) return;
    this.pagina = n;
    this.aplicarFiltros();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  paginaAnterior(){ this.irAPagina(this.pagina - 1); }
  paginaSiguiente(){ this.irAPagina(this.pagina + 1); }

  /** Indice del primer elemento de la pagina, que es lo que espera p-paginator. */
  get primerElemento(): number {
    return this.pagina * this.tamanoPagina;
  }

  /** El paginador de PrimeNG informa del indice; aqui se traduce a numero de pagina. */
  onCambioPagina(evento: PaginatorState) {
    this.pagina = evento.page ?? 0;
    this.aplicarFiltros();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cargarFavoritos() {
    this.favService.getFavoritos().subscribe({
      next: data => {
        this.favoritosIds = data.map(f => f.id);
      },
      error: err => {
        console.error('Error favoritos', err);

        /*if (err.status === 403) {
          this.cerrarSesion();
        }*/
      }
    });
  }


  irFavoritos(){
  this.router.navigate(['/favoritos']);
  }

  irCarrito(){
    this.router.navigate(['/carrito']);
  }

  esFavorito(id: number): boolean {
    return this.favoritosIds.includes(id);
  }

  toggleFavorito(id: number, e: Event) {
    e.stopPropagation();

    if (this.esFavorito(id)) {
      this.favService.eliminar(id).subscribe(() => {
        this.favoritosIds = this.favoritosIds.filter(f => f !== id);
      });
    } else {
      this.favService.agregar(id).subscribe(() => {
        this.favoritosIds.push(id);
      });
    }
  }

  // Ahora el producto tiene varias tallas: la elección de talla se hace en el detalle.
  agregarCarrito(p:any){
    this.verDetalles(p);
  }

  verDetalles(p:any){
    this.router.navigate(['/detalle',p.id]);
  }

  irCategoria(nombre:string){
    const ruta = nombre.trim()
        .toLowerCase()
        .normalize("NFD").replace(/[\u0300-\u036f]/g, "")
        .replace(/ñ/g,"n") 
        .replace(/s$/,"");

    this.router.navigate([`/${ruta}`]);
  }

  irMenu(){
  this.router.navigate(['/menu']);
  }

  cerrarSesion() { 
    //localStorage.clear(); 
    this.authService.logout();
  } 

  irPerfil(){ 
    this.router.navigate(['/perfil']); 
  } 
}
