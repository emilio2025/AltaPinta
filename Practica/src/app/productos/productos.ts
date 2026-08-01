import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { forkJoin } from 'rxjs';
import { ProductoService } from '../../services/producto.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-producto',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './productos.html',
  styleUrls: ['./productos.css']
})
export class ProductoComponent implements OnInit {

  productos: any[] = [];

  categorias: any[] = [];
  tipos: any[] = [];
  tallas: any[] = [];

  producto = {
    id: null,
    nombre: '',
    descripcion: '',
    precio: 0,
    imagenUrl: '',
    categoria: null,
    tipoPrenda: null,
  }

  // Tallas múltiples: una fila por cada talla global, con checkbox + stock propio
  tallasForm: { talla: any; seleccionada: boolean; stock: number }[] = [];

  // Galería: hasta 3 espacios de imagen por producto, mostrando la prenda completa
  // desde distintos ángulos. Cada slot puede tener una url ya subida (edición) o un
  // archivo nuevo pendiente de subir.
  readonly MAX_IMAGENES = 3;
  imagenesForm: { file: File | null; preview: string | null; url: string | null }[] = [];

  modoEdicion = false;

  subiendoImagen = false;
  mensajeError = '';

  // RF038: Paginación
  paginaActual = 1;
  tamanoPagina = 10;

  constructor(private service: ProductoService, private router: Router) {}

  ngOnInit(): void {
    this.listar();
    this.service.getCategorias().subscribe(res => this.categorias = res);
    this.service.getTipos().subscribe(res => this.tipos = res);
    this.service.getTallas().subscribe(res => {
      this.tallas = res;
      this.reiniciarTallasForm();
    });
    this.reiniciarImagenesForm();
  }

  listar(){
    this.service.getTodos().subscribe(res => {
      this.productos = res;
      this.paginaActual = 1;
    });
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.productos.length / this.tamanoPagina));
  }

  get productosPaginados(): any[] {
    const inicio = (this.paginaActual - 1) * this.tamanoPagina;
    return this.productos.slice(inicio, inicio + this.tamanoPagina);
  }

  irAPagina(pagina: number) {
    if (pagina < 1 || pagina > this.totalPaginas) return;
    this.paginaActual = pagina;
  }

  // Stock total del producto = suma del stock de todas sus tallas
  stockTotal(p: any): number {
    return (p.tallasDisponibles ?? []).reduce((sum: number, pt: any) => sum + (pt.stock ?? 0), 0);
  }

  reiniciarTallasForm(tallasDisponibles: any[] = []) {
    this.tallasForm = this.tallas.map(t => {
      const existente = tallasDisponibles.find((pt: any) => pt.talla?.id === t.id);
      return {
        talla: t,
        seleccionada: !!existente,
        stock: existente ? existente.stock : 0
      };
    });
  }

  reiniciarImagenesForm(existentes: any[] = []) {
    this.imagenesForm = Array.from({ length: this.MAX_IMAGENES }, (_, i) => {
      const ex = existentes[i];
      return { file: null, preview: null, url: ex ? ex.url : null };
    });
  }

  onArchivoSeleccionado(event: Event, index: number) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;
    if (!file) return;
    this.imagenesForm[index].file = file;
    this.imagenesForm[index].preview = URL.createObjectURL(file);
  }

  quitarImagen(index: number) {
    this.imagenesForm[index] = { file: null, preview: null, url: null };
  }

  private urlsGaleria(): string[] {
    return this.imagenesForm
      .map(s => s.url)
      .filter((u): u is string => !!u);
  }

  guardar(){
    this.mensajeError = '';

    const pendientes = this.imagenesForm.filter(s => s.file);
    if (pendientes.length > 0) {
      this.subiendoImagen = true;
      forkJoin(pendientes.map(s => this.service.subirImagen(s.file!))).subscribe({
        next: resultados => {
          resultados.forEach((res, i) => {
            pendientes[i].url = res.url;
            pendientes[i].file = null;
            pendientes[i].preview = null;
          });
          this.subiendoImagen = false;
          this.producto.imagenUrl = this.urlsGaleria()[0] || this.producto.imagenUrl || '';
          this.guardarProducto();
        },
        error: err => {
          this.subiendoImagen = false;
          this.mensajeError = err.error?.message || 'No se pudo subir la imagen';
        }
      });
    } else {
      this.producto.imagenUrl = this.urlsGaleria()[0] || this.producto.imagenUrl || '';
      this.guardarProducto();
    }
  }

  private guardarProducto(){
    if(!this.modoEdicion){
      this.service.crearProducto(this.producto).subscribe({
        next: (creado: any) => this.guardarTallas(creado.id),
        error: err => this.mensajeError = err.error?.message || 'No se pudo guardar el producto'
      });
    } else {
      this.service.editarProducto(this.producto.id!, this.producto).subscribe({
        next: () => this.guardarTallas(this.producto.id!),
        error: err => this.mensajeError = err.error?.message || 'No se pudo guardar el producto'
      });
    }
  }

  private guardarTallas(productoId: number){
    const payload = this.tallasForm
      .filter(tf => tf.seleccionada)
      .map(tf => ({ tallaId: tf.talla.id, stock: tf.stock ?? 0 }));

    this.service.sincronizarTallas(productoId, payload).subscribe({
      next: () => this.guardarImagenes(productoId),
      error: err => this.mensajeError = err.error?.message || 'No se pudieron guardar las tallas'
    });
  }

  private guardarImagenes(productoId: number){
    this.service.sincronizarImagenes(productoId, this.urlsGaleria()).subscribe({
      next: () => { this.listar(); this.limpiar(); this.modoEdicion = false; },
      error: err => this.mensajeError = err.error?.message || 'No se pudieron guardar las imágenes'
    });
  }

  editar(p:any){
    this.producto = { ...p };
    this.modoEdicion = true;
    this.reiniciarTallasForm(p.tallasDisponibles ?? []);
    const existentes = (p.imagenes && p.imagenes.length > 0)
      ? p.imagenes
      : (p.imagenUrl ? [{ url: p.imagenUrl }] : []);
    this.reiniciarImagenesForm(existentes);
  }

  eliminar(id:number){
    if(confirm("¿Seguro de eliminar este producto?")){
      this.service.eliminarProducto(id).subscribe(()=>{
        this.listar();
      });
    }
  }

  limpiar(){
    this.producto = {
      id:null, nombre:'', descripcion:'', precio:0, imagenUrl:'',
      categoria:null, tipoPrenda:null
    }
    this.reiniciarImagenesForm();
    this.reiniciarTallasForm();
  }

  irProductos() {
    this.router.navigate(['/productos']);
  }

  irAdmin() {
    this.router.navigate(['/admin']);
  }
}
