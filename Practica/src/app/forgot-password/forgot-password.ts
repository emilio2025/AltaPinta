import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './forgot-password.html',
  styleUrls: ['./forgot-password.css']
})
export class ForgotPasswordComponent {

  correo = '';
  message = '';
  error = '';

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  enviar() {
    this.message = '';
    this.error = '';

    if (!this.correo) {
      this.error = 'Ingresa tu correo';
      return;
    }

    this.auth.requestPasswordReset(this.correo).subscribe({
      next: (res: any) => {
        this.message = res;
        setTimeout(() => {
          this.router.navigate(['/reset-password'], {
            queryParams: { correo: this.correo }
          });
        }, 1500);
      },
      error: err => {
        this.error = this.auth.extractErrorMessage(err, 'Error al enviar el código');
      }
    });
  }
}
