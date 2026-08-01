import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterModule, CommonModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  
  registerData = {
    nombre: '',
    correo: '',
    password: '',
    direccion: '',
    dni: ''
  };
  
  verificationData = {
    correo: '', 
    codigo: ''
  };

  isRegistered = false; 
  message = '';
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) { }

  onRegisterSubmit(): void {
    this.message = '';
    this.errorMessage = '';
    
    this.verificationData.correo = this.registerData.correo;

    this.authService.register(this.registerData).subscribe({
      next: (res) => {
        this.message = res;
        this.isRegistered = true; 
      },
      error: (err) => {
        this.errorMessage = this.authService.extractErrorMessage(err, 'Error desconocido al intentar registrar.');
      }
    });
  }

  onVerifySubmit(): void {
    this.message = '';
    this.errorMessage = '';

    this.authService.verify(this.verificationData).subscribe({
      next: (res) => {
        alert(res); 
        this.router.navigate(['/login']); 
      },
      error: (err) => {
        this.errorMessage = this.authService.extractErrorMessage(err, 'Error desconocido al verificar el código.');
      }
    });
  }
}