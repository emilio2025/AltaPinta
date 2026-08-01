import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  constructor(private auth: AuthService, private router: Router) {}

  canActivate(): boolean {
    if (!this.auth.isLoggedIn()) {
      this.router.navigate(['/login']);
      return false;
    }

    if (!this.auth.isAdmin()) {
      alert('Acceso denegado: solo admin puede entrar aquí');
      this.router.navigate(['/']);
      return false;
    }

    return true;
  }
}