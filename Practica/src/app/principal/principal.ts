import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { FavoritoService } from '../../services/favorito.service';
import { ProductoService } from '../../services/producto.service';
import { RouterModule } from '@angular/router';
import { forkJoin, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [CommonModule, RouterModule, HttpClientModule, FormsModule],
  templateUrl: './principal.html',
  styleUrls: ['./principal.css']
})
export class PrincipalComponent implements OnInit {

  productosHero: any[] = [];
  productosBebes: any[] = [];
  filtrados: any[] = [];
  favoritosIds: number[] = [];

  busqueda: string = "";
  categoriaSeleccionada: string = "";

  categorias: any[] = [];

  // Paginacion. El backend numera las paginas desde 0.
  pagina = 0;
  readonly tamanoPagina = 12;
  totalPaginas = 0;
  totalResultados = 0;

  // El buscador dispara una peticion por pulsacion; el debounce espera a que
  // el usuario deje de escribir para no lanzar una consulta por letra.
  private terminoBuscado = new Subject<string>();

  constructor(
    private productoService: ProductoService,
    private favService: FavoritoService,
    private router: Router
  ){}

  ngOnInit(): void {
    this.terminoBuscado
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe(() => {
        this.pagina = 0;
        this.cargarPagina();
      });

    this.cargarDestacados();
    this.cargarCategorias();
    this.cargarFavoritos();
  }

  /** True cuando hay una busqueda o una categoria activa. */
  get mostrandoResultados(): boolean {
    return !!this.busqueda.trim() || !!this.categoriaSeleccionada;
  }

  /**
   * Portada: unas pocas prendas de muestra. Se piden por categoria y con
   * tamaño de pagina pequeño, en lugar de descargar el catalogo entero y
   * filtrarlo aqui como se hacia antes.
   */
  cargarDestacados(){
    const adultos = this.productoService.buscar({ categoria: 'Mujer', size: 10 });
    const varon = this.productoService.buscar({ categoria: 'Varón', size: 10 });
    const bebes = this.productoService.buscar({ categoria: 'Bebé', size: 10 });
    const ninos = this.productoService.buscar({ categoria: 'Niños', size: 10 });

    forkJoin([adultos, varon]).subscribe(([m, v]) => {
      this.productosHero = this.muestraAleatoria([...m.content, ...v.content], 2);
    });

    forkJoin([bebes, ninos]).subscribe(([b, n]) => {
      this.productosBebes = this.muestraAleatoria([...b.content, ...n.content], 3);
    });
  }

  private muestraAleatoria(productos: any[], cuantos: number): any[] {
    return [...productos].sort(() => Math.random() - 0.5).slice(0, cuantos);
  }

  cargarCategorias(){
    this.productoService.getCategorias().subscribe(res => this.categorias = res);
  }

  /** Pide al backend la pagina actual con los filtros activos. */
  cargarPagina(){
    this.productoService.buscar({
      nombre: this.busqueda,
      categoria: this.categoriaSeleccionada,
      page: this.pagina,
      size: this.tamanoPagina
    }).subscribe(res => {
      this.filtrados = res.content;
      this.totalPaginas = res.totalPages;
      this.totalResultados = res.totalElements;
    });
  }

  filtrarCategoria(cat: string){
    // Volver a pulsar la misma categoria la desactiva.
    this.categoriaSeleccionada = this.categoriaSeleccionada === cat ? "" : cat;
    this.pagina = 0;
    this.cargarPagina();
  }

  buscar(){
    this.terminoBuscado.next(this.busqueda);
  }

  limpiarFiltros(){
    this.busqueda = "";
    this.categoriaSeleccionada = "";
    this.pagina = 0;
    this.filtrados = [];
    this.totalPaginas = 0;
    this.totalResultados = 0;
  }

  irAPagina(n: number){
    if (n < 0 || n >= this.totalPaginas) return;
    this.pagina = n;
    this.cargarPagina();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  paginaAnterior(){ this.irAPagina(this.pagina - 1); }
  paginaSiguiente(){ this.irAPagina(this.pagina + 1); }

  cargarFavoritos(){
    this.favService.getFavoritos().subscribe((r:any[]) => {
      this.favoritosIds = r.map(p=>p.id);
    });
  }

  esFavorito(id:number){
    return this.favoritosIds.includes(id);
  }

  toggleFavorito(id:number, e:Event){
    e.stopPropagation();
    if(this.esFavorito(id)){
      this.favService.eliminar(id).subscribe(() => {
        this.favoritosIds = this.favoritosIds.filter(f => f !== id);
      });
    } else {
      this.favService.agregar(id).subscribe(() => {
        this.favoritosIds.push(id);
      });
    }
  }

  verDetalles(p:any){
    this.router.navigate(['/detalle', p.id]);
  }

  irFavoritos(){ this.router.navigate(['/login']); }
  irCarrito(){ this.router.navigate(['/login']); }
  irLogin(){this.router.navigate(['/login']);}

  agregarCarrito(producto:any){this.router.navigate(['/login']);  }


  agregarFavorito(p:any){
    /*if (this.esFavorito(p.id)) {
      this.favService.eliminar(p.id).subscribe(() => {
        this.favoritosIds = this.favoritosIds.filter(f => f !== p.id);
        console.log(" Eliminado de favoritos", p.nombre);
      });
    } else {
      this.favService.agregar(p.id).subscribe(() => {
        this.favoritosIds.push(p.id);
        console.log(" Agregado a favoritos", p.nombre);
      });
    }*/
   this.router.navigate(['/login']);
    
  }

  irMenu(){
  this.router.navigate(['/principal']);
  }

}
