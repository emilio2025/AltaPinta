import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Varon } from './varon';

describe('Varon', () => {
  let component: Varon;
  let fixture: ComponentFixture<Varon>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Varon]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Varon);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
