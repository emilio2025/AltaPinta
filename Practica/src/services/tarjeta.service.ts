import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface TarjetaResponse {
  id: number;
  numero: string;
  titular: string;
  saldo: number;
}

@Injectable({ providedIn: 'root' })
export class TarjetaService {

  private API = 'http://localhost:8080/tarjetas';

  constructor(private http: HttpClient) {}

  private auth() {
    return {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + localStorage.getItem('token')
      })
    };
  }

  misTarjetas(): Observable<TarjetaResponse[]> {
    return this.http.get<TarjetaResponse[]>(this.API, this.auth());
  }

  registrar(data: any): Observable<TarjetaResponse> {
    return this.http.post<TarjetaResponse>(this.API, data, this.auth());
  }

  recargar(numero: string, monto: number): Observable<any> {
    return this.http.put(`${this.API}/admin/recargar?numero=${numero}&monto=${monto}`, {});
  }
}
