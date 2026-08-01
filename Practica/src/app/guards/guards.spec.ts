import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { AuthGuard } from './auth.guard';
import { AdminGuard } from './admin.guard';
import { AuthService } from '../auth.service';

/**
 * Pruebas de los guardias de ruta.
 *
 * Ojo con el alcance: estos guardias solo controlan lo que se ve en el
 * navegador. La seguridad real la impone el backend, que valida el token en
 * cada peticion (ver SeguridadWebTest en BackendTienda). Aqui se comprueba
 * que la interfaz no lleve al usuario a pantallas que luego le van a fallar.
 */
describe('Guardias de ruta', () => {

  let router: jasmine.SpyObj<Router>;
  let auth: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);
    auth = jasmine.createSpyObj<AuthService>('AuthService', ['isLoggedIn', 'isAdmin']);

    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        AdminGuard,
        { provide: Router, useValue: router },
        { provide: AuthService, useValue: auth },
      ],
    });
  });

  // ============================================================
  describe('AuthGuard', () => {

    it('deja pasar si hay sesion iniciada', () => {
      auth.isLoggedIn.and.returnValue(true);

      expect(TestBed.inject(AuthGuard).canActivate()).toBeTrue();
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('sin sesion, bloquea y manda al login', () => {
      auth.isLoggedIn.and.returnValue(false);

      expect(TestBed.inject(AuthGuard).canActivate()).toBeFalse();
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });
  });

  // ============================================================
  describe('AdminGuard', () => {

    beforeEach(() => {
      // El guardia avisa con alert(); se intercepta para que no bloquee
      // la ejecucion de las pruebas.
      spyOn(window, 'alert');
    });

    it('deja pasar al administrador', () => {
      auth.isLoggedIn.and.returnValue(true);
      auth.isAdmin.and.returnValue(true);

      expect(TestBed.inject(AdminGuard).canActivate()).toBeTrue();
      expect(router.navigate).not.toHaveBeenCalled();
      expect(window.alert).not.toHaveBeenCalled();
    });

    it('a un cliente con sesion le niega el paso y lo saca al inicio', () => {
      auth.isLoggedIn.and.returnValue(true);
      auth.isAdmin.and.returnValue(false);

      expect(TestBed.inject(AdminGuard).canActivate()).toBeFalse();
      expect(window.alert).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['/']);
    });

    it('sin sesion manda al login, no al inicio', () => {
      auth.isLoggedIn.and.returnValue(false);

      expect(TestBed.inject(AdminGuard).canActivate()).toBeFalse();
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
      // Sin sesion no se le acusa de falta de permisos: simplemente no entro.
      expect(window.alert).not.toHaveBeenCalled();
    });

    it('sin sesion ni siquiera se pregunta si es admin', () => {
      auth.isLoggedIn.and.returnValue(false);

      TestBed.inject(AdminGuard).canActivate();

      expect(auth.isAdmin).not.toHaveBeenCalled();
    });
  });
});
