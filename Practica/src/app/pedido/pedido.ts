import { Component, OnInit } from '@angular/core';
import { PedidoService } from '../../services/pedido.service';
import { CommonModule, DatePipe } from '@angular/common'; 
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ProductoService } from '../../services/producto.service';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-pedidos',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe], 
  templateUrl: './pedido.html',
  styleUrls: ['./pedido.css']
})
export class PedidosComponent implements OnInit {

  pedidos: any[] = [];
  cargando = false;
  contadorCarrito = 0; 
  categorias: any[] = []; 

  constructor(private pedidoService: PedidoService, 
    private router: Router,
    private productoService: ProductoService ,
        private authService: AuthService ) {}

  ngOnInit(): void {
    this.cargarCategorias(); 
    this.cargarPedidos();
  }

  cargarPedidos() {
    this.cargando = true;
    this.pedidoService.misPedidos().subscribe({
      next: data => {
        this.pedidos = data;
        this.cargando = false;
      },
      error: () => this.cargando = false
    });
  }

  descargarFactura(pedidoId: number) {
    this.pedidoService.descargarFactura(pedidoId).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `factura_${pedidoId}.pdf`;
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }

  // RF043: Cancelar pedido propio (solo mientras está PAGADO)
  esCancelable(estado: string): boolean {
    return estado === 'PAGADO';
  }

  cancelarPedido(pedidoId: number) {
    if (!confirm('¿Seguro que deseas cancelar este pedido?')) {
      return;
    }
    this.pedidoService.cancelarPedido(pedidoId).subscribe({
      next: () => this.cargarPedidos(),
      error: err => alert(err.error?.message || err.error || 'No se pudo cancelar el pedido')
    });
  }

  /** Clase de la etiqueta de estado, en la paleta de la tienda. */
  estadoBadge(estado: string): string {
    switch (estado) {
      case 'PAGADO':     return 'ap-estado ap-estado--pagado';
      case 'EN CAMINO':  return 'ap-estado ap-estado--camino';
      case 'EN DESTINO': return 'ap-estado ap-estado--destino';
      case 'RECOGIDO':   return 'ap-estado ap-estado--recogido';
      case 'CANCELADO':  return 'ap-estado ap-estado--cancelado';
      default:           return 'ap-estado';
    }
  }

  irDirecciones(){
    this.router.navigate(['/direcciones']);
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
