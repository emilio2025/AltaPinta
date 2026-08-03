import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductoService } from '../../services/producto.service';
import { FavoritoService } from '../../services/favorito.service';
import { CarritoService } from '../../services/carrito.service';
import { CarritoStateService } from '../../services/carrito-state.service';
import { CommonModule, DecimalPipe } from '@angular/common';
import { EnvioService } from '../../services/envio.service'; 
import { Router } from '@angular/router'; 

import { ImagenPipe } from '../pipes/imagen.pipe';
@Component({
  selector: 'app-detalle-producto',
  imports: [ImagenPipe, CommonModule, DecimalPipe],
  templateUrl: './detalle-producto.html',
  styleUrls: ['./detalle-producto.css']
})
export class DetalleProductoComponent implements OnInit {

  productos: any[] = []; 
  filtrados: any[] = []; 
  categorias: any[] = []; 
  tipos: any[] = []; 
  tallas: any[] = []; 
  favoritosIds: number[] = [];    
  busqueda: string = ""; 
  contadorCarrito = 0; 
  producto: any = null;
  esFav = false;
  loadingCarrito = false;
  tallaSeleccionada: any = null;
  imagenActiva: string = '';

  constructor(
    private route: ActivatedRoute,
    private productoService: ProductoService,
    private favService: FavoritoService,
    private carritoService: CarritoService,
    private carritoState: CarritoStateService,
    private envioService: EnvioService, 
    private router: Router 
  ) {}

  ngOnInit(): void {
    this.cargarCategorias(); 
    this.cargarFavoritos(); 
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.productoService.getUno(id).subscribe(p => {
      this.producto = p;
      const galeria = this.galeria();
      this.imagenActiva = galeria.length > 0 ? galeria[0] : p.imagenUrl;

      this.favService.getFavoritos().subscribe(res => {
        this.esFav = res.some((r:any) => r.id === id);
      });
    });
  }

  // Fotos de la galería del producto (2-3 imágenes mostrando la prenda desde
  // distintos ángulos); si el producto no tiene galería propia, se usa imagenUrl.
  galeria(): string[] {
    if (!this.producto) return [];
    if (this.producto.imagenes && this.producto.imagenes.length > 0) {
      return this.producto.imagenes.map((im: any) => im.url);
    }
    return this.producto.imagenUrl ? [this.producto.imagenUrl] : [];
  }

  verImagen(url: string) {
    this.imagenActiva = url;
  }

  toggleFav() {
    if (this.esFav) {
      this.favService.eliminar(this.producto.id)
        .subscribe(() => this.esFav = false);
    } else {
      this.favService.agregar(this.producto.id)
        .subscribe(() => this.esFav = true);
    }
  }

  seleccionarTalla(pt: any) {
    if ((pt.stock ?? 0) <= 0) return;
    this.tallaSeleccionada = pt;
  }

  agregarCarrito() {
    // loadingCarrito también deshabilita el botón, así que salir aquí evita
    // añadir la prenda dos veces si se pulsa antes de que la vista se refresque.
    if (!this.tallaSeleccionada || this.loadingCarrito) return;

    this.loadingCarrito = true;

    this.carritoService.agregar(this.producto.id, 1, this.tallaSeleccionada.talla.id).subscribe({
      next: () => {
        this.carritoState.incrementar();
        this.loadingCarrito = false;
      },
      // Sin esta rama, un fallo de red dejaba el botón deshabilitado para
      // siempre: el cliente no podía reintentar sin recargar la página.
      error: err => {
        console.error('No se pudo añadir al carrito', err);
        this.loadingCarrito = false;
      }
    });
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
    this.router.navigate(['/login']); 
  } 

  irFavoritos(){ 
  this.router.navigate(['/favoritos']); 
  } 

  irCarrito(){ 
    this.router.navigate(['/carrito']); 
  } 

  buscar(){ 
    this.filtrados = this.productos.filter(p => 
      p.nombre.toLowerCase().includes(this.busqueda.toLowerCase()) 
    ); 
  } 
 
  cargarCategorias(){ 
    this.productoService.getCategorias().subscribe(res => this.categorias = res); 
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

  irPerfil(){ 
    this.router.navigate(['/perfil']); 
  } 
}
