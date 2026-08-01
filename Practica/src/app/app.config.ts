import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { importProvidersFrom } from '@angular/core';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { providePrimeNG } from 'primeng/config';

import { routes } from './app.routes';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { AltaPintaPreset } from './theme/altapinta-preset';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),

    provideHttpClient(
      withInterceptors([AuthInterceptor])
    ),

    importProvidersFrom(FormsModule),

    provideAnimationsAsync(),
    providePrimeNG({
      theme: {
        preset: AltaPintaPreset,
        options: {
          // Sin modo oscuro automatico: la tienda tiene una identidad
          // clara y definida, y el selector .app-dark deja la puerta
          // abierta a activarlo mas adelante.
          darkModeSelector: '.app-dark',
          // Las clases de PrimeNG van antes que las utilidades de
          // Tailwind, para poder ajustar componentes sin !important.
          cssLayer: {
            name: 'primeng',
            order: 'theme, base, primeng, utilities'
          }
        }
      },
      ripple: true
    })
  ]
};
