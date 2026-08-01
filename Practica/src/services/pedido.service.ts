import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface PedidoResponse {
  pedidoId: number;
  total: number;
  estado: string;
  tiempoEntrega: string;
}

export interface Pedido {
  id: number;
  total: number;
  estado: string;
  clienteCorreo: string;
  fecha: string;
}

export interface ConfirmarPedidoRequest {
  envioId: number | null;
  tarjetaId: number;
}

@Injectable({ providedIn: 'root' })
export class PedidoService {

  private API_PEDIDO = 'http://localhost:8080/pedido';

  constructor(private http: HttpClient) {}

  private auth() {
    return {
      headers: new HttpHeaders({
        Authorization: 'Bearer ' + localStorage.getItem('token')
      })
    };
  }

  confirmar(data: ConfirmarPedidoRequest): Observable<PedidoResponse> {
    return this.http.post<PedidoResponse>(
      `${this.API_PEDIDO}/confirmar`,
      data,
      this.auth()
    );
  }

  misPedidos(): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.API_PEDIDO}/mis-pedidos`,
      this.auth()
    );
  }

  /**descargarFactura(idPedido: number) {
    return this.http.get(
      `http://localhost:8080/pedido/${idPedido}/factura`,
      { responseType: 'blob' }
    );
  }*/

  descargarFactura(id: number): Observable<Blob> {
    return this.http.get(
      `${this.API_PEDIDO}/${id}/factura`,
      { ...this.auth(), responseType: 'blob' }
    );
  }

  listar(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.API_PEDIDO}/todos`);
  }

  /*actualizarEstado(id: number, nuevoEstado: string): Observable<Pedido> {
    return this.http.put<Pedido>(`${this.API_PEDIDO}/${id}/estado?nuevoEstado=${nuevoEstado}`, {});
  }*/

  actualizarEstado(id: number, nuevoEstado: string): Observable<Pedido> {
    const params = { nuevoEstado };
    const headers = { Authorization: 'Bearer ' + localStorage.getItem('token') };
    return this.http.put<Pedido>(`${this.API_PEDIDO}/${id}/estado`, {}, { headers, params });
  }

  // RF043: Cancelar pedido propio
  cancelarPedido(id: number): Observable<PedidoResponse> {
    return this.http.put<PedidoResponse>(
      `${this.API_PEDIDO}/${id}/cancelar`,
      {},
      this.auth()
    );
  }

}
