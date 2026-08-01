import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TarjetaService } from '../../services/tarjeta.service';
import { Router } from '@angular/router';
import { Location } from '@angular/common';

@Component({
  selector: 'app-agregar-tarjeta',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './agregar-tarjeta.html',
  styleUrl: './agregar-tarjeta.css'
})
export class AgregarTarjetaComponent {

  numero = '';
  titular = '';
  fechaVencimiento = '';
  cvv = '';

  loading = false;
  error = '';

  private readonly regexTarjeta = /^[0-9]{16}$/;
  private readonly regexCVV = /^[0-9]{3}$/;
  private readonly regexFecha = /^(0[1-9]|1[0-2])\/[0-9]{4}$/;

  constructor(
    private tarjetaService: TarjetaService,
    private router: Router,
    private location: Location
  ) {}

  guardar(form: any) {

    this.error = '';

    if (form.invalid) {
      this.error = 'Completa correctamente el formulario';
      return;
    }

    if (!this.regexTarjeta.test(this.numero)) {
      this.error = 'Número de tarjeta inválido';
      return;
    }

    if (!this.regexCVV.test(this.cvv)) {
      this.error = 'CVV inválido';
      return;
    }

    if (!this.regexFecha.test(this.fechaVencimiento)) {
      this.error = 'Fecha de vencimiento inválida (MM/AAAA)';
      return;
    }

    if (this.tarjetaVencida(this.fechaVencimiento)) {
      this.error = 'La tarjeta se encuentra vencida';
      return;
    }

    this.loading = true;

    this.tarjetaService.registrar({
      numero: this.numero,
      titular: this.titular,
      fechaVencimiento: this.fechaVencimiento,
      cvv: this.cvv
    }).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/checkout']);
      },
      error: err => {
        this.loading = false;
        this.error = err.error?.message || err.error || 'Error registrando tarjeta';
      }
    });
  }

  tarjetaVencida(fecha: string): boolean {
    const [mesStr, anioStr] = fecha.split('/');
    const mes = Number(mesStr);
    const anio = Number(anioStr);

    const hoy = new Date();
    const mesActual = hoy.getMonth() + 1;
    const anioActual = hoy.getFullYear();

    if (anio < anioActual) {
      return true;
    }

    if (anio === anioActual && mes < mesActual) {
      return true;
    }

    return false;
  }

  volver() {
    this.location.back();
  }
}
