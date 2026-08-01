import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Deporte } from './producto.service';

/**
 * Catalogo de deportes.
 *
 * La lectura es publica; crear, editar y eliminar exigen ROLE_ADMIN, y eso
 * lo impone el backend en SecurityConfig, no esta pantalla.
 */
@Injectable({ providedIn: 'root' })
export class DeporteService {

  private URL = 'http://localhost:8080/deportes';

  constructor(private http: HttpClient) {}

  listar(): Observable<Deporte[]> {
    return this.http.get<Deporte[]>(this.URL);
  }

  crear(deporte: Partial<Deporte>): Observable<Deporte> {
    return this.http.post<Deporte>(this.URL, deporte);
  }

  actualizar(id: number, deporte: Partial<Deporte>): Observable<Deporte> {
    return this.http.put<Deporte>(`${this.URL}/${id}`, deporte);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.URL}/${id}`);
  }
}
