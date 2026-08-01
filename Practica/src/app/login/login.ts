import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterModule, CommonModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  loginData = { correo: '', password: '' };
  errorMessage = '';

  constructor(private auth: AuthService, private router: Router) {
    if (this.auth.isLoggedIn()) {
      if (this.auth.isAdmin()) {
        this.router.navigate(['/admin']);
      } else {
        this.router.navigate(['/menu']);
      }
    }
  }

  onLoginSubmit(): void {
    this.errorMessage = '';

    this.auth.login(this.loginData).subscribe({
      next: (res: any) => {
        localStorage.clear();
        localStorage.setItem('token', res.token);
        localStorage.setItem('rol', res.rol);
        localStorage.setItem('nombre', res.nombre);
        localStorage.setItem('correo', res.correo);

        // Redirección según correo
        if (this.auth.isAdmin()) {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/menu']);
        }
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Correo o contraseña incorrectos';
      }
    });
  }
}
