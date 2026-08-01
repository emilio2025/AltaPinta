import { Component, OnInit } from '@angular/core';
import { FavoritoService } from '../../services/favorito.service';
import { ProductoService } from '../../services/producto.service';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common'; 
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-favoritos',
  imports: [CommonModule, RouterModule], 
  templateUrl: './favoritos.html',
  styleUrls: ['./favoritos.css']
})
export class FavoritosComponent implements OnInit {

  favoritos:any[] = [];
  categorias: any[] = [];

  constructor(
    private favService: FavoritoService,
    private productoService:ProductoService,
    private router: Router ,
        private authService: AuthService
  ) {}

  verDetalle(p:any){
    this.router.navigate(['/detalle', p.id]);
  }

  ngOnInit(): void {
    this.favService.getFavoritos().subscribe(res => {
      this.favoritos = res;
    });
  
    this.cargarCategorias();
  }

  quitar(p: any) {
    this.favService.eliminar(p.id).subscribe(() => {
      this.favoritos = this.favoritos.filter(x => x.id !== p.id);
    });
  }

  irMenu(){
  this.router.navigate(['/menu']);
  }

  CerrarSesion() { 
    //localStorage.clear(); 
    this.authService.logout();
  } 

  irFavoritos(){
  this.router.navigate(['/favoritos']);
  }

  irCarrito(){
    this.router.navigate(['/carrito']);
  }

  irCategoria(nombre:string){
    const ruta = nombre.trim()
        .toLowerCase()
        .normalize("NFD").replace(/[\u0300-\u036f]/g, "")
        .replace(/ñ/g,"n") 
        .replace(/s$/,"");

    this.router.navigate([`/${ruta}`]);
  }

  cargarCategorias(){
    this.productoService.getCategorias().subscribe(res => this.categorias = res);
  }

  irPerfil(){ 
    this.router.navigate(['/perfil']); 
  } 
}

