import { TestBed } from '@angular/core/testing';

import { CarritoStateService } from './carrito-state.service';

/**
 * Pruebas del contador del carrito.
 *
 * Es el numerito que se ve en la cabecera. Lo comparten varias pantallas a
 * traves de un BehaviorSubject, asi que lo que importa es que todas reciban
 * el mismo valor y que quien se suscriba tarde reciba el valor actual.
 */
describe('CarritoStateService', () => {

  let service: CarritoStateService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [CarritoStateService] });
    service = TestBed.inject(CarritoStateService);
  });

  it('arranca en cero', (done) => {
    service.contador$.subscribe((valor) => {
      expect(valor).toBe(0);
      done();
    });
  });

  it('setContador fija el valor indicado', () => {
    const valores: number[] = [];
    service.contador$.subscribe((v) => valores.push(v));

    service.setContador(5);

    expect(valores).toEqual([0, 5]);
  });

  it('incrementar suma de uno en uno', () => {
    let ultimo = -1;
    service.contador$.subscribe((v) => (ultimo = v));

    service.incrementar();
    service.incrementar();
    service.incrementar();

    expect(ultimo).toBe(3);
  });

  it('incrementar parte del valor actual, no de cero', () => {
    let ultimo = -1;
    service.contador$.subscribe((v) => (ultimo = v));

    service.setContador(10);
    service.incrementar();

    expect(ultimo).toBe(11);
  });

  it('reset vuelve a cero', () => {
    let ultimo = -1;
    service.contador$.subscribe((v) => (ultimo = v));

    service.setContador(7);
    service.reset();

    expect(ultimo).toBe(0);
  });

  it('quien se suscribe despues recibe el valor actual, no el inicial', () => {
    // Es la razon de usar BehaviorSubject: la cabecera puede montarse
    // despues de que otra pantalla haya actualizado el contador.
    service.setContador(4);

    let recibido = -1;
    service.contador$.subscribe((v) => (recibido = v));

    expect(recibido).toBe(4);
  });

  it('todas las pantallas suscritas ven el mismo valor', () => {
    let cabecera = -1;
    let menu = -1;
    service.contador$.subscribe((v) => (cabecera = v));
    service.contador$.subscribe((v) => (menu = v));

    service.setContador(9);

    expect(cabecera).toBe(9);
    expect(menu).toBe(9);
  });
});
