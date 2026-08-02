import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClienteService } from '../../services/cliente.service';
import { FavoritoService } from '../../services/favorito.service'; 
import { ProductoService } from '../../services/producto.service'; 
import { Router } from '@angular/router'; 
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-mi-perfil',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mi-perfil.html',
  styleUrl: './mi-perfil.css'
})
export class MiPerfil implements OnInit {

  cliente: any = null;
  editando = false;
  cargando = false;
  errorCarga = '';
  favoritosIds: number[] = [];    
  busqueda: string = ""; 
  contadorCarrito = 0; 
  productos: any[] = []; 
  filtrados: any[] = []; 
  categorias: any[] = []; 

  constructor(private clienteService: ClienteService,
    private productoService: ProductoService, 
    private favService: FavoritoService, 
    private router: Router  ,
        private authService: AuthService
  ) {}

  ngOnInit() {
    this.cargarCategorias(); 
    this.cargarFavoritos(); 
    // Sin rama de error la pantalla se quedaba en "Cargando..." para
    // siempre cuando la peticion fallaba (sesion caducada, backend caido).
    this.clienteService.obtenerPerfil().subscribe({
      next: res => {
        this.cliente = res;
        this.errorCarga = '';
      },
      error: () => this.errorCarga = 'No pudimos cargar tu perfil. Vuelve a iniciar sesión e inténtalo de nuevo.'
    });
  }

  editar() {
    this.editando = true;
  }

  guardar() {
    this.cargando = true;

    this.clienteService.actualizarPerfil(this.cliente)
      .subscribe({
        next: res => {
          this.cliente = res;
          this.editando = false;
          this.cargando = false;
        },
        error: () => this.cargando = false
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

  irTarjetas(){ 
  this.router.navigate(['/tarjetas']); 
  } 

  irPedidos(){
  this.router.navigate(['/pedido']);
  }

  irDirecciones(){
  this.router.navigate(['/direcciones']);
  }

}
