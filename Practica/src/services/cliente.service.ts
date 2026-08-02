import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { environment } from '../environments/environment';
@Injectable({ providedIn: 'root' })
export class ClienteService {

  private API = `${environment.apiUrl}/cliente`;

  constructor(private http: HttpClient) {}

  private auth() {
    return {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + localStorage.getItem('token')
      })
    };
  }

  obtenerPerfil() {
    return this.http.get<any>(`${this.API}/me`, this.auth());
  }

  actualizarPerfil(data: any) {
    return this.http.put<any>(`${this.API}/actualizar`, data, this.auth());
  }
}
