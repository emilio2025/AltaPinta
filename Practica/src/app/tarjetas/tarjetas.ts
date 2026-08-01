import { Component, OnInit } from '@angular/core';
import { CommonModule, } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TarjetaService, TarjetaResponse } from '../../services/tarjeta.service';
import { Router } from '@angular/router';
import { ProductoService } from '../../services/producto.service';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-tarjetas',
  imports: [CommonModule, FormsModule], 
  templateUrl: './tarjetas.html',
  styleUrls: ['./tarjetas.css']
})
export class TarjetasComponent implements OnInit {

  tarjetas: TarjetaResponse[] = [];
  cargando = false;
  contadorCarrito = 0; 
  categorias: any[] = []; 

  constructor(private tarjetaService: TarjetaService,
      private router: Router,
      private productoService: ProductoService ,
          private authService: AuthService  ) {}

  ngOnInit(): void {
    this.cargarCategorias(); 
    this.cargarTarjetas();
  }

  cargarTarjetas() {
    this.cargando = true;
    this.tarjetaService.misTarjetas().subscribe({
      next: data => {
        this.tarjetas = data;
        this.cargando = false;
      },
      error: () => this.cargando = false
    });
  }

  registrarNueva() {
    this.router.navigate(['/agregarTarjeta']);
  }

  
  irTarjetas(){ 
  this.router.navigate(['/tarjetas']); 
  } 

  irPedidos(){ 
  this.router.navigate(['/pedido']); 
  } 

  irFavoritos(){ 
  this.router.navigate(['/favoritos']); 
  } 

  irCarrito(){ 
    this.router.navigate(['/carrito']); 
  } 

  irMenu(){ 
  this.router.navigate(['/menu']); 
  } 

  irCategoria(nombre:string){ 
    const ruta = nombre.trim() 
        .toLowerCase() 
        .normalize("NFD").replace(/[\u0300-\u036f]/g, "") 
        .replace(/ñ/g,"n")  
        .replace(/s$/,""); 
    this.router.navigate([`/${ruta}`]); 
  } 

  cerrarSesion() { 
    //localStorage.clear(); 
    this.authService.logout();
  } 

  cargarCategorias(){ 
    this.productoService.getCategorias().subscribe(res => this.categorias = res); 
  } 

  irPerfil() { 
    this.router.navigate(['/perfil']); 
  }

}
