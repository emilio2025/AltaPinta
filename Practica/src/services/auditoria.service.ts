import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { environment } from '../environments/environment';
@Injectable({ providedIn: 'root' })
export class AuditoriaService {

  private API = `${environment.apiUrl}/auditoria`;

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
}
