import { Component, OnInit } from '@angular/core';
import { CarritoService } from '../../services/carrito.service';
import { PedidoService } from '../../services/pedido.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TarjetaService } from '../../services/tarjeta.service';
import { Location } from '@angular/common';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './checkout.html',
  styleUrl: './checkout.css'
})
export class CheckoutComponent implements OnInit {

  tarjetas: any[] = [];
  tarjetaSeleccionada: any = null;

  loading = false;
  error = '';
  exito = false;

  total = 0;
  pedidoResponse: any = null;

  // null = RECOJO EN TIENDA
  envioSeleccionadoId: number | null = null;

  constructor(
    private carritoService: CarritoService,
    private pedidoService: PedidoService,
    private tarjetaService: TarjetaService,
    private router: Router,
    private location: Location
  ) {}

  ngOnInit() {

    // Total del carrito
    this.carritoService.obtener().subscribe(resp => {
      const items = Array.isArray(resp)
        ? resp
        : resp?.items ?? [];

      this.total = items.reduce(
        (sum: number, i: any) => sum + i.cantidad * i.precio,
        0
      );
    });

    // Tarjetas del cliente
    this.tarjetaService.misTarjetas().subscribe({
      next: t => this.tarjetas = t || [],
      error: () => this.error = 'Error cargando tarjetas'
    });

    //  Envío seleccionado (si existe)
    const envio = this.carritoService.getEnvio();
    this.envioSeleccionadoId = envio ? envio.id : null;
  }

  pagar() {

    // Validaciones
    if (!this.tarjetaSeleccionada) {
      this.error = 'Seleccione una tarjeta';
      return;
    }

    if (this.tarjetaSeleccionada.saldo < this.total) {
      this.error = 'Saldo insuficiente en la tarjeta';
      return;
    }

    this.loading = true;
    this.error = '';

    this.pedidoService.confirmar({
      envioId: this.envioSeleccionadoId,
      tarjetaId: this.tarjetaSeleccionada.id
    }).subscribe({
      next: resp => {
        this.loading = false;
        this.exito = true;
        this.pedidoResponse = resp;
        this.carritoService.resetContador();

        setTimeout(() => {
          this.router.navigate(['/mis-pedidos']);
        }, 1500);
      },
      error: err => {
        this.error = err?.error || 'Error procesando pedido, stock insuficiente';
        this.loading = false;
      }
    });
  }

  AgregarTarjeta() {
    this.router.navigate(['/agregarTarjeta']);
  }

  volver() {
    this.location.back();
  }

}
