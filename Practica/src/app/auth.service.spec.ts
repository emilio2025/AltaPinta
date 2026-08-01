import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router } from '@angular/router';

import { AuthService } from './auth.service';

/**
 * Pruebas del servicio de autenticacion.
 *
 * Cubren sobre todo dos cosas: que la sesion se guarde y se limpie bien en
 * localStorage, y que extractErrorMessage sepa leer los errores del backend,
 * que llegan en tres formatos distintos segun el endpoint.
 */
describe('AuthService', () => {

  let service: AuthService;
  let httpMock: HttpTestingController;
  let router: jasmine.SpyObj<Router>;

  const API = 'http://localhost:8080/api/auth';
  const ADMIN = 'altapintaunamba@gmail.com';

  beforeEach(() => {
    const routerSpy = jasmine.createSpyObj<Router>('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Router, useValue: routerSpy },
      ],
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;

    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  // ============================================================
  describe('sesion', () => {

    it('guarda token, nombre, correo y rol al iniciar sesion', () => {
      service.saveAuthData({
        token: 'abc123',
        nombre: 'Ruth',
        correo: 'cliente@unamba.edu.pe',
        rol: 'USER',
      });

      expect(localStorage.getItem('token')).toBe('abc123');
      expect(localStorage.getItem('nombre')).toBe('Ruth');
      expect(localStorage.getItem('correo')).toBe('cliente@unamba.edu.pe');
      expect(localStorage.getItem('rol')).toBe('USER');
    });

    it('sin token no hay sesion iniciada', () => {
      expect(service.isLoggedIn()).toBeFalse();
    });

    it('con token hay sesion iniciada', () => {
      localStorage.setItem('token', 'abc123');

      expect(service.isLoggedIn()).toBeTrue();
    });

    it('al cerrar sesion se borra todo y se vuelve al login', () => {
      service.saveAuthData({
        token: 'abc123',
        nombre: 'Ruth',
        correo: 'cliente@unamba.edu.pe',
        rol: 'USER',
      });

      service.logout();

      expect(localStorage.getItem('token')).toBeNull();
      expect(localStorage.getItem('correo')).toBeNull();
      expect(service.isLoggedIn()).toBeFalse();
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('devuelve el correo y el rol guardados', () => {
      localStorage.setItem('correo', 'cliente@unamba.edu.pe');
      localStorage.setItem('rol', 'USER');

      expect(service.getEmail()).toBe('cliente@unamba.edu.pe');
      expect(service.getRol()).toBe('USER');
    });
  });

  // ============================================================
  describe('isAdmin', () => {

    it('reconoce el correo de administrador', () => {
      localStorage.setItem('correo', ADMIN);

      expect(service.isAdmin()).toBeTrue();
    });

    it('un cliente normal no es administrador', () => {
      localStorage.setItem('correo', 'cliente@unamba.edu.pe');

      expect(service.isAdmin()).toBeFalse();
    });

    it('sin sesion no es administrador', () => {
      expect(service.isAdmin()).toBeFalse();
    });

    it('un correo parecido al del admin no cuela', () => {
      localStorage.setItem('correo', 'altapintaunamba@gmail.com.attacker.com');

      expect(service.isAdmin()).toBeFalse();
    });
  });

  // ============================================================
  describe('extractErrorMessage', () => {
    // El backend responde de tres formas distintas segun el endpoint, y
    // varios servicios usan responseType 'text', asi que un error JSON llega
    // como cadena sin parsear.

    it('lee el mensaje cuando el error ya viene como objeto', () => {
      const err = { error: { message: 'Correo ya registrado' } };

      expect(service.extractErrorMessage(err, 'fallback')).toBe('Correo ya registrado');
    });

    it('parsea el mensaje cuando el JSON llega como texto', () => {
      const err = { error: '{"message":"Codigo invalido"}' };

      expect(service.extractErrorMessage(err, 'fallback')).toBe('Codigo invalido');
    });

    it('devuelve el texto tal cual cuando no es JSON', () => {
      const err = { error: 'El DNI ya está registrado' };

      expect(service.extractErrorMessage(err, 'fallback')).toBe('El DNI ya está registrado');
    });

    it('usa el mensaje por defecto cuando no hay error', () => {
      expect(service.extractErrorMessage({}, 'Algo salió mal')).toBe('Algo salió mal');
      expect(service.extractErrorMessage(null, 'Algo salió mal')).toBe('Algo salió mal');
    });

    it('usa el mensaje por defecto si el objeto no trae message', () => {
      const err = { error: { codigo: 500 } };

      expect(service.extractErrorMessage(err, 'Algo salió mal')).toBe('Algo salió mal');
    });
  });

  // ============================================================
  describe('llamadas al backend', () => {

    it('login envia las credenciales al endpoint correcto', () => {
      const credenciales = { correo: 'cliente@unamba.edu.pe', password: 'Password1' };

      service.login(credenciales).subscribe();

      const req = httpMock.expectOne(`${API}/login`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(credenciales);
      req.flush({ token: 'abc123' });
    });

    it('la solicitud de recuperacion manda el correo como parametro', () => {
      service.requestPasswordReset('cliente@unamba.edu.pe').subscribe();

      const req = httpMock.expectOne(
        (r) => r.url === `${API}/password/solicitar`
      );
      expect(req.request.method).toBe('POST');
      expect(req.request.params.get('correo')).toBe('cliente@unamba.edu.pe');
      req.flush('ok');
    });

    it('el cambio de contrasena manda codigo y contrasena nueva', () => {
      service.resetPassword('123456', 'NuevaPass1').subscribe();

      const req = httpMock.expectOne((r) => r.url === `${API}/password/cambiar`);
      expect(req.request.params.get('codigo')).toBe('123456');
      expect(req.request.params.get('nuevaPassword')).toBe('NuevaPass1');
      req.flush('ok');
    });
  });
});
