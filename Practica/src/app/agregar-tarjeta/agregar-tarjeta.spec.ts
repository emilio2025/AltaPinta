import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AgregarTarjeta } from './agregar-tarjeta';

describe('AgregarTarjeta', () => {
  let component: AgregarTarjeta;
  let fixture: ComponentFixture<AgregarTarjeta>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AgregarTarjeta]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AgregarTarjeta);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
