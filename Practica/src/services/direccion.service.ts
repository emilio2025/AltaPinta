import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { environment } from '../environments/environment';
@Injectable({ providedIn: 'root' })
export class DireccionService {

  private API = `${environment.apiUrl}/direcciones`;

  constructor(private http: HttpClient) {}

  private auth() {
    return {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + localStorage.getItem('token')
      })
    };
  }

  listar() {
    return this.http.get<any[]>(this.API, this.auth());
  }

  crear(data: any) {
    return this.http.post<any>(this.API, data, this.auth());
  }

  editar(id: number, data: any) {
    return this.http.put<any>(`${this.API}/${id}`, data, this.auth());
  }

  eliminar(id: number) {
    return this.http.delete(`${this.API}/${id}`, this.auth());
  }
}
