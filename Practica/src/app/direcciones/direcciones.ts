import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DireccionService } from '../../services/direccion.service';
import { ProductoService } from '../../services/producto.service';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-direcciones',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './direcciones.html',
  styleUrls: ['./direcciones.css']
})
export class DireccionesComponent implements OnInit {

  direcciones: any[] = [];
  cargando = false;
  categorias: any[] = [];
  contadorCarrito = 0;
  mensajeError = '';

  mostrarFormulario = false;
  modoEdicion = false;
  direccion: any = this.vacia();

  constructor(
    private direccionService: DireccionService,
    private productoService: ProductoService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargarCategorias();
    this.cargarDirecciones();
  }

  vacia() {
    return { id: null, etiqueta: '', direccionCompleta: '', referencia: '', distrito: '' };
  }

  cargarDirecciones() {
    this.cargando = true;
    this.direccionService.listar().subscribe({
      next: data => { this.direcciones = data; this.cargando = false; },
      error: () => this.cargando = false
    });
  }

  nueva() {
    this.direccion = this.vacia();
    this.modoEdicion = false;
    this.mostrarFormulario = true;
    this.mensajeError = '';
  }

  editar(d: any) {
    this.direccion = { ...d };
    this.modoEdicion = true;
    this.mostrarFormulario = true;
    this.mensajeError = '';
  }

  guardar() {
    this.mensajeError = '';
    const peticion = this.modoEdicion
      ? this.direccionService.editar(this.direccion.id, this.direccion)
      : this.direccionService.crear(this.direccion);

    peticion.subscribe({
      next: () => {
        this.mostrarFormulario = false;
        this.cargarDirecciones();
      },
      error: err => this.mensajeError = err.error?.message || 'No se pudo guardar la dirección'
    });
  }

  eliminar(id: number) {
    if (!confirm('¿Eliminar esta dirección?')) return;
    this.direccionService.eliminar(id).subscribe({
      next: () => this.cargarDirecciones(),
      error: err => alert(err.error?.message || 'No se pudo eliminar la dirección')
    });
  }

  cancelar() {
    this.mostrarFormulario = false;
  }

  irPerfil() { this.router.navigate(['/perfil']); }
  irTarjetas() { this.router.navigate(['/tarjetas']); }
  irPedidos() { this.router.navigate(['/pedido']); }
  irFavoritos() { this.router.navigate(['/favoritos']); }
  irCarrito() { this.router.navigate(['/carrito']); }
  irMenu() { this.router.navigate(['/menu']); }

  irCategoria(nombre: string) {
    const diacriticos = new RegExp('[' + String.fromCharCode(0x0300) + '-' + String.fromCharCode(0x036f) + ']', 'g');
    const ruta = nombre.trim()
      .toLowerCase()
      .normalize("NFD").replace(diacriticos, "")
      .replace(/ñ/g, "n")
      .replace(/s$/, "");
    this.router.navigate([`/${ruta}`]);
  }

  cargarCategorias() {
    this.productoService.getCategorias().subscribe(res => this.categorias = res);
  }

  cerrarSesion() {
    this.authService.logout();
  }
}
