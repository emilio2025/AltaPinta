import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './reset-password.html',
  styleUrls: ['./reset-password.css']
})
export class ResetPasswordComponent {

  codigo = '';
  password = '';
  message = '';
  error = '';

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  cambiar() {
    this.message = '';
    this.error = '';

    if (!this.codigo || !this.password) {
      this.error = 'Completa todos los campos';
      return;
    }

    // RF035: Validar criterios de seguridad de la contraseña
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
    if (!passwordRegex.test(this.password)) {
      this.error = 'Mínimo 8 caracteres, una mayúscula, una minúscula y un número.';
      return;
    }

    this.auth.resetPassword(this.codigo, this.password).subscribe({
      next: (res: any) => {
        this.message = res;
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1500);
      },
      error: err => {
        this.error = this.auth.extractErrorMessage(err, 'Error al cambiar la contraseña');
      }
    });
  }
}
