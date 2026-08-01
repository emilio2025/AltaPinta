import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule, DecimalPipe } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { FavoritoService } from '../../services/favorito.service';
import { ProductoService } from '../../services/producto.service';
import { CarritoService } from '../../services/carrito.service';
import { AuthService } from '../auth.service';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [CommonModule, RouterModule, HttpClientModule, FormsModule],
  templateUrl: './menu.html',
  styleUrls: ['./menu.css']
})
export class MenuComponent implements OnInit {

  productos: any[] = [];
  filtrados: any[] = [];

  categorias: any[] = [];
  tipos: any[] = [];

  favoritosIds: number[] = [];
  busqueda: string = "";
  contadorCarrito = 0;
  tipoSeleccionado: string = '';
  categoriaSeleccionada: string = '';
  tallas: any[] = [];
  tallaSeleccionada: any = null;

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
        private authService: AuthService
  ){}

  ngOnInit(): void {
    this.cargarProductos();
    this.cargarCategorias();
    this.cargarTipos();
    this.cargarFavoritos();
    this.cargarTipos();
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
      talla: this.tallaSeleccionada?.nombre,
      page: this.pagina,
      size: this.tamanoPagina
    }).subscribe(res => {
      this.filtrados = res.content;
      this.totalPaginas = res.totalPages;
      this.totalResultados = res.totalElements;
    });
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

  /**
   * El desplegable de tallas ya trae su propia opcion "Todas", y [(ngModel)]
   * actualiza tallaSeleccionada antes de que salte (change), asi que aqui solo
   * hay que recargar: alternar el valor lo dejaria siempre en null.
   */
  cambiarTalla(){
    this.reiniciarYFiltrar();
  }

  limpiarFiltros(){
    this.busqueda = '';
    this.categoriaSeleccionada = '';
    this.tipoSeleccionado = '';
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
