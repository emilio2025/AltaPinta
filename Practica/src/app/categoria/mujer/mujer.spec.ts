import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Mujer } from './mujer';

describe('Mujer', () => {
  let component: Mujer;
  let fixture: ComponentFixture<Mujer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Mujer]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Mujer);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
