import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
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

@Injectable({
  providedIn: 'root'
})
export class ProductoService {

  private URL = "http://localhost:8080";

  constructor(private http: HttpClient) {}

  getTodos(){ return this.http.get<any[]>(`${this.URL}/productos`); }
  getUno(id:number){ return this.http.get<any>(`${this.URL}/productos/${id}`); }

  getCategorias(){ return this.http.get<any[]>(`${this.URL}/categorias`); }
  getPorCategoria(nombre:string){ return this.http.get<any[]>(`${this.URL}/productos/categoria/${nombre}`); }

  getTipos(){ return this.http.get<any[]>(`${this.URL}/tipos`); }
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

  listar(): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.URL}/productos`);
  }

}

