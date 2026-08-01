import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";

export interface Talla {
  id: number;
  nombre: string;
}

export interface ProductoTalla {
  id?: number;
  talla: Talla;
  stock: number;
}

export interface ProductoImagen {
  id?: number;
  url: string;
  orden: number;
}

export interface Producto {
  id: number;
  nombre: string;
  precio: number;
  tallasDisponibles?: ProductoTalla[];
  imagenes?: ProductoImagen[];
  descripcion?: string;
}

export interface TipoPrenda {
  id: number;
  nombre: string;
}

/** Respuesta paginada de Spring Data. */
export interface Pagina<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;        // pagina actual, empezando en 0
  size: number;
  first: boolean;
  last: boolean;
}

/** Deporte al que va dirigida una prenda. */
export interface Deporte {
  id: number;
  nombre: string;
  icono?: string;
}

/** Criterios de busqueda del catalogo. Todos opcionales. */
export interface FiltrosProducto {
  nombre?: string;
  categoria?: string;
  tipo?: string;
  deporte?: string;
  talla?: string;
  page?: number;
  size?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProductoService {

  private URL = "http://localhost:8080";

  constructor(private http: HttpClient) {}

  /**
   * Catalogo paginado con filtros opcionales.
   *
   * Los filtros se resuelven en el backend: la busqueda cubre el catalogo
   * entero, no solo los productos ya descargados. Los valores vacios no se
   * envian para que el servidor los trate como "sin filtro".
   */
  buscar(filtros: FiltrosProducto = {}): Observable<Pagina<Producto>> {
    let params = new HttpParams()
      .set('page', filtros.page ?? 0)
      .set('size', filtros.size ?? 12);

    if (filtros.nombre?.trim())    params = params.set('nombre', filtros.nombre.trim());
    if (filtros.categoria?.trim()) params = params.set('categoria', filtros.categoria.trim());
    if (filtros.tipo?.trim())      params = params.set('tipo', filtros.tipo.trim());
    if (filtros.deporte?.trim())   params = params.set('deporte', filtros.deporte.trim());
    if (filtros.talla?.trim())     params = params.set('talla', filtros.talla.trim());

    return this.http.get<Pagina<Producto>>(`${this.URL}/productos`, { params });
  }

  getUno(id:number){ return this.http.get<any>(`${this.URL}/productos/${id}`); }

  getCategorias(){ return this.http.get<any[]>(`${this.URL}/categorias`); }
  getPorCategoria(nombre:string){ return this.http.get<any[]>(`${this.URL}/productos/categoria/${nombre}`); }

  getTipos(){ return this.http.get<any[]>(`${this.URL}/tipos`); }
  getDeportes(){ return this.http.get<Deporte[]>(`${this.URL}/deportes`); }
  getPorTipo(nombre:string){ return this.http.get<any[]>(`${this.URL}/productos/tipo/${nombre}`); }

  getTallas(){ return this.http.get<any[]>(`${this.URL}/tallas`); }
  getPorTalla(nombre:string){ return this.http.get<any[]>(`${this.URL}/productos/talla/${nombre}`); }

  crearProducto(data:any){ return this.http.post(`${this.URL}/productos`,data); }
  editarProducto(id:number,data:any){ return this.http.put(`${this.URL}/productos/${id}`,data); }
  eliminarProducto(id:number){ return this.http.delete(`${this.URL}/productos/${id}`); }

  subirImagen(file: File): Observable<{ url: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ url: string }>(`${this.URL}/productos/imagen`, formData);
  }

  // Tallas múltiples: reemplaza por completo las tallas/stock disponibles de un producto
  sincronizarTallas(productoId: number, tallas: { tallaId: number; stock: number }[]) {
    return this.http.put(`${this.URL}/productos/${productoId}/tallas`, tallas);
  }

  // Galería: reemplaza por completo las imágenes del producto (2-3 recomendadas, en orden)
  sincronizarImagenes(productoId: number, urls: string[]) {
    return this.http.put<ProductoImagen[]>(`${this.URL}/productos/${productoId}/imagenes`, urls);
  }

  getTiposPorCategoria(categoria: string) {
    return this.http.get<TipoPrenda[]>(
      `${this.URL}/tipos/categoria/${categoria}`
    );
  }

  getTallasPorCategoria(categoria: string) {
    return this.http.get<Talla[]>(
      `${this.URL}/tallas/categoria/${categoria}`
    );
  }

  /** Alias historico de buscar(); se mantiene porque lo usa el panel de admin. */
  listar(page = 0, size = 12): Observable<Pagina<Producto>> {
    return this.buscar({ page, size });
  }

}

