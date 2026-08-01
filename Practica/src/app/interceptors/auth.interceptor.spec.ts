import { HttpEvent, HttpHandlerFn, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { AuthInterceptor } from './auth.interceptor';

/**
 * Pruebas del interceptor que adjunta el token JWT.
 *
 * Es la pieza que hace que el resto de la aplicacion funcione: si dejara de
 * poner la cabecera Authorization, todas las pantallas privadas empezarian a
 * recibir 403 del backend.
 */
describe('AuthInterceptor', () => {

  /** Captura la peticion que el interceptor deja pasar al siguiente eslabon. */
  let peticionEnviada: HttpRequest<unknown> | null;

  const siguiente: HttpHandlerFn = (req): Observable<HttpEvent<unknown>> => {
    peticionEnviada = req;
    return of(new HttpResponse({ status: 200 }));
  };

  beforeEach(() => {
    peticionEnviada = null;
    localStorage.clear();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('adjunta el token como Bearer cuando hay sesion', () => {
    localStorage.setItem('token', 'abc123');

    AuthInterceptor(new HttpRequest('GET', '/carrito'), siguiente).subscribe();

    expect(peticionEnviada!.headers.get('Authorization')).toBe('Bearer abc123');
  });

  it('no adjunta nada cuando no hay sesion', () => {
    AuthInterceptor(new HttpRequest('GET', '/productos'), siguiente).subscribe();

    expect(peticionEnviada!.headers.has('Authorization')).toBeFalse();
  });

  it('no toca el resto de la peticion', () => {
    localStorage.setItem('token', 'abc123');
    const cuerpo = { productoId: 7, cantidad: 2 };

    AuthInterceptor(new HttpRequest('POST', '/carrito/agregar/7', cuerpo), siguiente).subscribe();

    expect(peticionEnviada!.method).toBe('POST');
    expect(peticionEnviada!.url).toBe('/carrito/agregar/7');
    expect(peticionEnviada!.body).toEqual(cuerpo);
  });

  it('deja pasar la peticion en ambos casos', () => {
    let respuestas = 0;

    AuthInterceptor(new HttpRequest('GET', '/productos'), siguiente)
      .subscribe(() => respuestas++);

    localStorage.setItem('token', 'abc123');
    AuthInterceptor(new HttpRequest('GET', '/carrito'), siguiente)
      .subscribe(() => respuestas++);

    expect(respuestas).toBe(2);
  });

  it('un token vacio no genera una cabecera invalida', () => {
    // Si el backend devolviera un token vacio, mandar "Bearer " suelto
    // provocaria un 403 confuso en vez de tratarse como sesion ausente.
    localStorage.setItem('token', '');

    AuthInterceptor(new HttpRequest('GET', '/carrito'), siguiente).subscribe();

    expect(peticionEnviada!.headers.has('Authorization')).toBeFalse();
  });
});
