import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';

import { environment } from '../environments/environment';
@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = `${environment.apiUrl}/api/auth`;

  constructor(private http: HttpClient, private router: Router) {}

  // Registro
  register(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data, { responseType: 'text' });
  }

  // Verificación
  verify(data: { correo: string; codigo: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/verify`, data, { responseType: 'text' });
  }

  // Login
  login(data: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, data);
  }

  // Extrae el mensaje de error del backend, ya sea texto plano o JSON {"message": "..."}
  // (los endpoints de este servicio usan responseType 'text', así que un error JSON llega como string sin parsear)
  extractErrorMessage(err: any, fallback: string): string {
    if (!err?.error) return fallback;
    if (typeof err.error === 'object') return err.error.message || fallback;
    try {
      const parsed = JSON.parse(err.error);
      return parsed?.message || err.error;
    } catch {
      return err.error || fallback;
    }
  }

  // Guardado correcto del token y datos
  saveAuthData(response: any): void {
    localStorage.setItem('token', response.token);
    localStorage.setItem('nombre', response.nombre);
    localStorage.setItem('correo', response.correo);
    localStorage.setItem('rol', response.rol);
  }

  // Cerrar sesión
  logout(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  // Sesión activa
  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  // Obtener correo del usuario logueado
  getEmail(): string | null {
    return localStorage.getItem('correo');
  }

  // Obtener rol del usuario logueado
  getRol(): string | null {
    return localStorage.getItem('rol');
  }

  /**
   * Administrador segun el rol que devolvio el backend al iniciar sesion.
   *
   * Antes se comparaba el correo con una constante escrita aqui mismo, lo
   * que dejaba dos fuentes de verdad: ascender a alguien en la base de datos
   * le daba permisos en la API pero la interfaz seguia tratandolo como
   * cliente, sin ningun aviso.
   *
   * Esto solo decide que se muestra. Quien autoriza de verdad es el backend,
   * que relee el rol de la base en cada peticion: cambiar este valor a mano
   * en el navegador no da acceso a nada.
   */
  isAdmin(): boolean {
    return this.getRol() === 'ADMIN';
  }

  // RECUPERAR CONTRASEÑA
  requestPasswordReset(correo: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/password/solicitar`, null, { params: { correo }, responseType: 'text' });
  }

  validateResetCode(codigo: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/password/validar`, null, { params: { codigo }, responseType: 'text' });
  }

  resetPassword(codigo: string, nuevaPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/password/cambiar`, null, { params: { codigo, nuevaPassword }, responseType: 'text' });
  }
}
