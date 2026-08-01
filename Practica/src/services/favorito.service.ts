import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class FavoritoService {

  private api = 'http://localhost:8080/favoritos';

  constructor(private http: HttpClient) {}

  getFavoritos(): Observable<any[]> {
    return this.http.get<any[]>(this.api);
  }

  agregar(id: number): Observable<any> {
    return this.http.post(`${this.api}/${id}`, null);
  }

  eliminar(id: number): Observable<any> {
    return this.http.delete(`${this.api}/${id}`);
  }
}
