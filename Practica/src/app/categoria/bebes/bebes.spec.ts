import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Bebes } from './bebes';

describe('Bebes', () => {
  let component: Bebes;
  let fixture: ComponentFixture<Bebes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Bebes]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Bebes);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
