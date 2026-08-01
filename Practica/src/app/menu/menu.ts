import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule, DecimalPipe } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { FavoritoService } from '../../services/favorito.service';
import { ProductoService } from '../../services/producto.service';
import { CarritoService } from '../../services/carrito.service';
import { AuthService } from '../auth.service';

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
  tallas: any[] = [];
  tallaSeleccionada: any = null;

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
  }

  aplicarFiltros() {
    this.filtrados = this.productos.filter(p => {

      const cumpleTalla =
        !this.tallaSeleccionada ||
        p.tallasDisponibles?.some((pt: any) => pt.talla?.nombre === this.tallaSeleccionada.nombre);

      return cumpleTalla;
    });
  }

  cargarTallas(){
    this.productoService.getTallas().subscribe(res => this.tallas = res);
  }

  cargarProductos(){
    this.productoService.getTodos().subscribe(res=>{
      this.productos = res;
      this.filtrados = res;
    });
  }

  cargarCategorias(){
    this.productoService.getCategorias().subscribe(res => this.categorias = res);
  }

  cargarTipos(){
    this.productoService.getTipos().subscribe(res => this.tipos = res);
  }

  filtrarTipo(tipo:string){
    this.productoService.getPorTipo(tipo).subscribe(res => this.filtrados = res);
  }

  filtrarCategoria(cat:string){
    this.productoService.getPorCategoria(cat).subscribe(res => this.filtrados = res);
  }

  buscar(){
    this.filtrados = this.productos.filter(p =>
      p.nombre.toLowerCase().includes(this.busqueda.toLowerCase())
    );
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
