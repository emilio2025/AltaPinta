import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ReporteService {

  private API = 'http://localhost:8080/reportes';

  constructor(private http: HttpClient) {}

  private auth() {
    return {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + localStorage.getItem('token')
      })
    };
  }

  total() {
    return this.http.get<any>(`${this.API}/total`, this.auth());
  }

  porDia(fecha: string) {
    return this.http.get<any>(
      `${this.API}/dia?fecha=${fecha}`,
      this.auth()
    );
  }

  porMes(mes: number, anio: number) {
    return this.http.get<any>(
      `${this.API}/mes?mes=${mes}&anio=${anio}`,
      this.auth()
    );
  }

  porAnio(anio: number) {
    return this.http.get<any>(
      `${this.API}/anio?anio=${anio}`,
      this.auth()
    );
  }
}
