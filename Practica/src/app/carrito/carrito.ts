import { Component, OnInit } from '@angular/core'; 
import { CommonModule } from '@angular/common'; 
import { FormsModule } from '@angular/forms'; 
import { CarritoService } from '../../services/carrito.service'; 
import { EnvioService } from '../../services/envio.service'; 
import { FavoritoService } from '../../services/favorito.service'; 
import { ProductoService } from '../../services/producto.service'; 
import { Router } from '@angular/router'; 
import { AuthService } from '../auth.service';

import { ImagenPipe } from '../pipes/imagen.pipe';
@Component({ 
  selector: 'app-carrito', 
  standalone: true, 
  imports: [ImagenPipe, CommonModule, FormsModule], 
  templateUrl: './carrito.html',
  styleUrl:'./carrito.css',

}) 
export class CarritoComponent implements OnInit { 
  productos: any[] = []; 
  filtrados: any[] = []; 
  categorias: any[] = []; 
  tipos: any[] = []; 
  tallas: any[] = []; 
  favoritosIds: number[] = [];    
  busqueda: string = ""; 
  contadorCarrito = 0; 
  items: any[] = []; 
  total = 0; 
  tipoEnvio: string = 'RECOJO'; 
  envios: any[] = []; 
  envioSeleccionado: any = null; 

  constructor( 
    private carritoService: CarritoService, 
    private envioService: EnvioService, 
    private productoService: ProductoService, 
    private favService: FavoritoService, 
    private router: Router ,
    private authService: AuthService
  ) {} 

  ngOnInit() { 
    this.cargar(); 
    this.cargarEnvios(); 
    this.cargarCategorias(); 
    this.cargarFavoritos(); 
    this.carritoService.contadorObservable$.subscribe(c => this.contadorCarrito = c); 
  } 

  incrementar(item: any) {
    item.cantidad++;
    this.cambiar(item);
  }

  decrementar(item: any) {
    item.cantidad--;

    if (item.cantidad <= 0) {
      this.eliminar(item);
    } else {
      this.cambiar(item);
    }
  }

  actualizarCantidad(item: any) {
    if (!item.cantidad || item.cantidad <= 0) {
      this.eliminar(item);
    } else {
      this.cambiar(item);
    }
  }


  cargar() { 
    this.carritoService.obtener().subscribe(resp => {
      const items: any[] = Array.isArray(resp)
        ? resp
        : resp?.items ?? [];

      this.items = items;
      this.calcularTotal();
    });
  }

  cargarEnvios() { 
    this.envioService.obtenerTodos().subscribe(res => { 
      this.envios = res; 
      if(this.envios.length > 0) this.envioSeleccionado = this.envios[0]; 
    }); 
  } 

  cambiar(item: any) {
    this.carritoService
      .actualizar(item.productoId, item.cantidad, item.tallaId)
      .subscribe(() => this.calcularTotal());
  }

  eliminar(item: any) {
    this.carritoService
      .eliminar(item.productoId, item.tallaId)
      .subscribe(() => this.cargar());
  }

  calcularTotal() {
    let subtotal = this.items.reduce(
      (sum, i) => sum + i.cantidad * i.precio,
      0
    );

    let costoEnvio = 0;

    if (this.tipoEnvio === 'ENVIO' && this.envioSeleccionado) {
      costoEnvio = this.envioSeleccionado.costo;

      this.carritoService.setEnvio(this.envioSeleccionado);
    } else {
      this.carritoService.setEnvio(null);
    }

    this.total = subtotal + costoEnvio;
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

  irPagar() {
    this.router.navigate(['/checkout']);
  }

} 