import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { FavoritoService } from '../../services/favorito.service';
import { ProductoService } from '../../services/producto.service';
import { RouterModule } from '@angular/router';

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
  productos: any[] = [];       
  filtrados: any[] = [];         
  favoritosIds: number[] = [];

  busqueda: string = "";

  categorias: any[] = [];

  constructor(
    private productoService: ProductoService,
    private favService: FavoritoService,
    private router: Router
  ){}

  ngOnInit(): void {
    this.cargarProductos();
    this.cargarCategorias();
    this.cargarFavoritos();
  }

 cargarProductos(){
    this.productoService.getTodos().subscribe(res => {

      console.log(" Productos cargados:", res); 

      this.productos = res;
      this.filtrados = res;

      let HM = res.filter(p => 
        p.categoria?.nombre?.toLowerCase() === "mujer" ||
        p.categoria?.nombre?.toLowerCase() === "varón"
      );

      this.productosHero = HM.sort(() => Math.random() - 0.5).slice(0, 2);

      this.productosBebes = res.filter(p => 
        p.categoria?.nombre?.toLowerCase() === "bebé" ||
        p.categoria?.nombre?.toLowerCase() === "niños"
      ).sort(() => Math.random() - 0.5).slice(0, 3);

    });
  }

  cargarCategorias(){
    this.productoService.getCategorias().subscribe(res => this.categorias = res);
  }

  filtrarCategoria(cat:string){
    this.productoService.getPorCategoria(cat).subscribe(res => this.filtrados = res);
  }

  buscar(){
    this.filtrados = this.productos.filter(p =>
      p.nombre.toLowerCase().includes(this.busqueda.toLowerCase())
    );
  }

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
