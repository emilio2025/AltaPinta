import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, tap } from 'rxjs';

import { environment } from '../environments/environment';
@Injectable({ providedIn: 'root' })
export class CarritoService {

  private API = `${environment.apiUrl}/carrito`;

  private contador$ = new BehaviorSubject<number>(0);
  contadorObservable$ = this.contador$.asObservable();

  constructor(private http: HttpClient) {
    this.actualizarContador();
  }

  private auth() {
    return {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + localStorage.getItem('token')
      })
    };
  }

  private envioSeleccionado: any = null;

  setEnvio(envio: any) {
    this.envioSeleccionado = envio;
  }

  getEnvio() {
    return this.envioSeleccionado;
  }

  obtener() {
    return this.http.get<any>(this.API, this.auth())
      .pipe(
        tap(carrito => this.calcularContador(carrito.items))
      );
  }

  agregar(id: number, cantidad: number = 1, tallaId: number) {
    return this.http.post(
      `${this.API}/agregar/${id}?cantidad=${cantidad}&tallaId=${tallaId}`,
      {},
      this.auth()
    ).pipe(
      tap(() => this.actualizarContador())
    );
  }

  actualizar(id: number, cantidad: number, tallaId: number) {
    return this.http.put(
      `${this.API}/actualizar/${id}?cantidad=${cantidad}&tallaId=${tallaId}`,
      {},
      this.auth()
    ).pipe(
      tap(() => this.actualizarContador())
    );
  }

  eliminar(id: number, tallaId: number) {
    return this.http.delete(
      `${this.API}/eliminar/${id}?tallaId=${tallaId}`,
      this.auth()
    ).pipe(
      tap(() => this.actualizarContador())
    );
  }

  private actualizarContador() {
    this.http.get<any>(this.API, this.auth())
      .subscribe(carrito => this.calcularContador(carrito.items));
  }

  private calcularContador(items: any[] = []) {
    const total = items.reduce((sum, i) => sum + i.cantidad, 0);
    this.contador$.next(total);
  }

  resetContador() {
    this.contador$.next(0);
  }

}
