import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReporteService } from '../../services/reporte.service';
import { TarjetaService } from '../../services/tarjeta.service';
import { ProductoService } from '../../services/producto.service';
import { PedidoService } from '../../services/pedido.service';
import { AuditoriaService } from '../../services/auditoria.service';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.html',
  styleUrls: ['./admin.css']
})
export class AdminDashboardComponent implements OnInit {

  totalVendido = 0;
  totalPedidos = 0;

  fecha = '';
  mes = new Date().getMonth() + 1;
  anio = new Date().getFullYear();
  resultadoFiltro: number | null = null;

  // Recarga de tarjetas
  recargarNumero = '';
  recargarMonto: number | null = null;
  recargarMensaje = '';

  // Productos
  productos: any[] = [];
  totalProductos = 0;

  // Pedidos
  pedidos: any[] = [];

  // RF054: Auditoría
  auditoria: any[] = [];

  constructor(
    private reporteService: ReporteService,
    private tarjetaService: TarjetaService,
    private productoService: ProductoService,
    private pedidoService: PedidoService,
    private auditoriaService: AuditoriaService,
    private router: Router,
    private authService: AuthService,
  ) {}

  ngOnInit() {
    this.reporteService.total().subscribe(res => {
      this.totalVendido = res.totalVendido;
      this.totalPedidos = res.totalPedidos;
    });

    this.cargarPedidos();
    this.cargarAuditoria();
  }

  cargarAuditoria() {
    this.auditoriaService.listar().subscribe(res => this.auditoria = res);
  }

  buscarDia() {
    this.reporteService.porDia(this.fecha)
      .subscribe(r => this.resultadoFiltro = r.total);
  }

  buscarMes() {
    this.reporteService.porMes(this.mes, this.anio)
      .subscribe(r => this.resultadoFiltro = r.total);
  }

  buscarAnio() {
    this.reporteService.porAnio(this.anio)
      .subscribe(r => this.resultadoFiltro = r.total);
  }

  // --- Recargar tarjeta ---
  recargarTarjeta() {
    if (!this.recargarNumero || !this.recargarMonto) return;

    this.tarjetaService.recargar(this.recargarNumero, this.recargarMonto)
      .subscribe({
        next: res => this.recargarMensaje = `Saldo actualizado: S/ ${res.saldo}`,
        error: err => this.recargarMensaje = err.error?.message || 'Error al recargar'
      });
  }

  // --- Cargar productos ---
  // El panel solo muestra un resumen, asi que pide la primera pagina en vez
  // del catalogo completo. El total real viene en totalProductos.
  cargarProductos() {
    this.productoService.listar(0, 10).subscribe(res => {
      this.productos = res.content;
      this.totalProductos = res.totalElements;
    });
  }

  // --- Pedidos y actualización de estado ---
  cargarPedidos() {
    this.pedidoService.listar().subscribe(res => this.pedidos = res);
  }

  /*actualizarEstado(pedido: any) {
    this.pedidoService.actualizarEstado(pedido.id, pedido.estado)
      .subscribe({
        next: res => pedido.estado = res.estado,
        error: err => console.error('Error al actualizar estado', err)
      });
  }*/

  actualizarEstado(pedido: any) {
    this.pedidoService.actualizarEstado(pedido.id, pedido.estado)
      .subscribe({
        next: res => {
          pedido.estado = res.estado;

          alert(`Pedido #${pedido.id} actualizado a "${pedido.estado}". Se ha enviado un correo al cliente.`);
        },
        error: err => {
          console.error('Error al actualizar estado', err);
          alert(`Error al actualizar el pedido #${pedido.id}`);
        }
      });
  }

  irProductos() {
    this.router.navigate(['/productos']);
  }

  irAdmin() {
    this.router.navigate(['/admin']);
  }

  logout() {
    this.authService.logout();
  }
}
