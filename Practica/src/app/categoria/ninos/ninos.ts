import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { ProductoService } from '../../../services/producto.service';
import { FavoritoService } from '../../../services/favorito.service';
import { CarritoService } from '../../../services/carrito.service';
import { AuthService } from '../../auth.service';

@Component({
  selector:'app-ninos',
  standalone:true,
  imports:[CommonModule, FormsModule, RouterModule],
  templateUrl:'./ninos.html',
  styleUrl:'./ninos.css'
})
export class NinosComponent implements OnInit{

  tituloCategoria = "Niños"; 

  productos:any[] = [];
  filtrados:any[] = [];
  favoritos:number[] = [];
  categorias: any[] = [];
  tipos: any[] = [];
  favoritosIds: number[] = [];
  busqueda: string = "";
  contadorCarrito = 0;
  tipoSeleccionado: string = '';
  tallas: any[] = [];
  //tallaSeleccionada: any = null;
  tallaSeleccionada: string = '';
  ordenSeleccionado: string = '';

  // RF038: Paginación
  paginaActual = 1;
  tamanoPagina = 12;

  constructor(
    private productoService:ProductoService,
    private favService:FavoritoService,
    private carritoService: CarritoService,
    private router:Router,
    private authService: AuthService
  ){}

  ngOnInit(){
    this.cargarProductosNinos();
    this.cargarTallasNinos();
    this.cargarTiposNinos();
    this.cargarFavoritos();
    this.cargarCategorias();
    
    this.carritoService.contadorObservable$
      .subscribe(c => this.contadorCarrito = c);
  }

  cargarProductosNinos(){
    this.productoService.getPorCategoria("ninos").subscribe(res=>{
      this.productos = res;
      this.filtrados = res;
    });
  }

  aplicarFiltros() {
    this.filtrados = this.productos.filter(p => {

      const cumpleTipo =
        !this.tipoSeleccionado ||
        p.tipoPrenda?.nombre === this.tipoSeleccionado;

      const cumpleTalla =
        !this.tallaSeleccionada ||
        p.tallasDisponibles?.some((pt: any) => pt.talla?.nombre === this.tallaSeleccionada);

      return cumpleTipo && cumpleTalla;
    });

    this.ordenarProductos();
    this.paginaActual = 1;
  }

  // RF037: Ordenar productos por precio
  ordenarProductos() {
    if (this.ordenSeleccionado === 'asc') {
      this.filtrados = [...this.filtrados].sort((a, b) => a.precio - b.precio);
    } else if (this.ordenSeleccionado === 'desc') {
      this.filtrados = [...this.filtrados].sort((a, b) => b.precio - a.precio);
    }
  }

  // RF038: Paginación
  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.filtrados.length / this.tamanoPagina));
  }

  get filtradosPaginados(): any[] {
    const inicio = (this.paginaActual - 1) * this.tamanoPagina;
    return this.filtrados.slice(inicio, inicio + this.tamanoPagina);
  }

  irAPagina(pagina: number) {
    if (pagina < 1 || pagina > this.totalPaginas) return;
    this.paginaActual = pagina;
  }

  /*filtrarTipo(tipo:string){
    this.productoService.getPorTipo(tipo).subscribe(res => this.filtrados = res);
  }*/

  filtrarTipo(tipo: string) {
    this.tipoSeleccionado = tipo;
    this.aplicarFiltros();
  }

  cargarTiposNinos() {
    this.productoService
      .getTiposPorCategoria('ninos')
      .subscribe(res => {
        console.log('TIPOS:', res);
        this.tipos = res;
      });
  }

  cargarTallasNinos() {
    this.productoService
      .getTallasPorCategoria('ninos')
      .subscribe(res => {
        console.log('TALLAS:', res);
        this.tallas = res;
      });
  }

  /*filtrarTalla(): void {
    if (!this.tallaSeleccionada || this.tallaSeleccionada === "Todos") {
      return this.cargarProductosNinos(); // recarga todos los productos
    }

    this.productoService.getPorTalla(this.tallaSeleccionada).subscribe(data => {
      this.productos = data;
    });
  }*/

  filtrarTalla() {
    this.aplicarFiltros();
  }

  cargarFavoritos(){
    this.favService.getFavoritos().subscribe((r:any[])=>{
      this.favoritos = r.map(f=>f.id);
    });
  }

  toggleFavorito(p:any){
    if(this.favoritos.includes(p.id)){
      this.favService.eliminar(p.id).subscribe(()=> this.favoritos = this.favoritos.filter(id=>id!==p.id));
    }else{
      this.favService.agregar(p.id).subscribe(()=> this.favoritos.push(p.id));
    }
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

  buscar(){
    this.filtrados = this.productos.filter(p =>
      p.nombre.toLowerCase().includes(this.busqueda.toLowerCase())
    );
    this.ordenarProductos();
    this.paginaActual = 1;
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

  esFavorito(id:number){
    return this.favoritosIds.includes(id);
  }

  cargarCategorias(){
    this.productoService.getCategorias().subscribe(res => this.categorias = res);
  }

  cargarTipos(){
    this.productoService.getTipos().subscribe(res => this.tipos = res);
  }

  cargarTallas(){
    this.productoService.getTallas().subscribe(res => this.tallas = res);
  }

  /*filtrarTipo(tipo:string){
    this.productoService.getPorTipo(tipo).subscribe(res => this.filtrados = res);
  }*/

  cerrarSesion() { 
    //localStorage.clear(); 
    this.authService.logout();
  } 

  irPerfil(){ 
    this.router.navigate(['/perfil']); 
  } 

  // Ahora el producto tiene varias tallas: la elección de talla se hace en el detalle.
  agregarCarrito(p:any){
    this.verDetalles(p);
  }
}
