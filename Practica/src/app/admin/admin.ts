import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReporteService } from '../../services/reporte.service';
import { TarjetaService } from '../../services/tarjeta.service';
import { ProductoService } from '../../services/producto.service';
import { PedidoService } from '../../services/pedido.service';
import { AuditoriaService } from '../../services/auditoria.service';
import { DeporteService } from '../../services/deporte.service';
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

  // --- Deportes ---
  deportes: any[] = [];
  nuevoDeporte = { nombre: '', icono: 'pi-bolt' };
  deporteMensaje = '';

  /** Iconos de PrimeIcons entre los que elegir al crear un deporte. */
  readonly iconosDisponibles = [
    'pi-bolt', 'pi-forward', 'pi-circle', 'pi-star',
    'pi-heart', 'pi-compass', 'pi-sun', 'pi-flag'
  ];

  constructor(
    private reporteService: ReporteService,
    private tarjetaService: TarjetaService,
    private productoService: ProductoService,
    private pedidoService: PedidoService,
    private auditoriaService: AuditoriaService,
    private deporteService: DeporteService,
    private router: Router,
    private authService: AuthService,
  ) {}

  // ---------- Deportes ----------
  cargarDeportes() {
    this.deporteService.listar().subscribe(res => this.deportes = res);
  }

  crearDeporte() {
    const nombre = this.nuevoDeporte.nombre.trim();
    if (!nombre) return;

    this.deporteService.crear({ nombre, icono: this.nuevoDeporte.icono }).subscribe({
      next: () => {
        this.deporteMensaje = `Deporte "${nombre}" creado`;
        this.nuevoDeporte = { nombre: '', icono: 'pi-bolt' };
        this.cargarDeportes();
      },
      error: err => this.deporteMensaje = err.error?.message || 'No se pudo crear el deporte'
    });
  }

  eliminarDeporte(d: any) {
    // Los productos que lo tengan asignado quedaran sin deporte, no se borran.
    if (!confirm(`¿Eliminar el deporte "${d.nombre}"? Los productos que lo usen quedarán sin deporte asignado.`)) return;

    this.deporteService.eliminar(d.id).subscribe({
      next: () => {
        this.deporteMensaje = `Deporte "${d.nombre}" eliminado`;
        this.cargarDeportes();
      },
      error: err => this.deporteMensaje = err.error?.message || 'No se pudo eliminar el deporte'
    });
  }

  ngOnInit() {
    this.reporteService.total().subscribe(res => {
      this.totalVendido = res.totalVendido;
      this.totalPedidos = res.totalPedidos;
    });

    this.cargarPedidos();
    this.cargarAuditoria();
    this.cargarDeportes();
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
