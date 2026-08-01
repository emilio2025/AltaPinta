import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CarritoStateService {

  private contadorSubject = new BehaviorSubject<number>(0);
  contador$ = this.contadorSubject.asObservable();

  setContador(valor: number) {
    this.contadorSubject.next(valor);
  }

  incrementar() {
    this.contadorSubject.next(this.contadorSubject.value + 1);
  }

  reset() {
    this.contadorSubject.next(0);
  }
}
