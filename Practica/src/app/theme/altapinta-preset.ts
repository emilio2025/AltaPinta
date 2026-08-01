import Aura from '@primeuix/themes/aura';
import { definePreset } from '@primeuix/themes';

/**
 * Tema de AltaPinta para PrimeNG.
 *
 * Estetica de tienda deportiva: base monocroma (negro, blanco y grises) para
 * que el protagonismo se lo lleve la fotografia del producto, y un unico
 * acento en lima para llamadas a la accion y estados activos.
 *
 * La escala "primary" es de grises intencionadamente: en esta interfaz el
 * color primario de PrimeNG lo usan botones y campos, y ahi queremos negro.
 * El acento se aplica por CSS a los elementos concretos que lo necesitan.
 */
export const AltaPintaPreset = definePreset(Aura, {
  semantic: {
    primary: {
      50:  '#f6f6f6',
      100: '#e7e7e7',
      200: '#d1d1d1',
      300: '#b0b0b0',
      400: '#888888',
      500: '#6d6d6d',
      600: '#5d5d5d',
      700: '#4f4f4f',
      800: '#454545',
      900: '#3d3d3d',
      950: '#0a0a0a'
    },
    colorScheme: {
      light: {
        primary: {
          color: '#0a0a0a',
          contrastColor: '#ffffff',
          hoverColor: '#2b2b2b',
          activeColor: '#000000'
        },
        surface: {
          0:   '#ffffff',
          50:  '#fafafa',
          100: '#f4f4f4',
          200: '#e8e8e8',
          300: '#d6d6d6',
          400: '#a3a3a3',
          500: '#737373',
          600: '#525252',
          700: '#404040',
          800: '#262626',
          900: '#171717',
          950: '#0a0a0a'
        }
      },
      dark: {
        primary: {
          color: '#ffffff',
          contrastColor: '#0a0a0a',
          hoverColor: '#e5e5e5',
          activeColor: '#d4d4d4'
        },
        surface: {
          0:   '#0a0a0a',
          50:  '#141414',
          100: '#1c1c1c',
          200: '#262626',
          300: '#333333',
          400: '#4d4d4d',
          500: '#737373',
          600: '#a3a3a3',
          700: '#d4d4d4',
          800: '#e5e5e5',
          900: '#f5f5f5',
          950: '#ffffff'
        }
      }
    }
  },
  components: {
    button: {
      root: {
        // Botones rectos y tipografia en mayusculas: el lenguaje visual
        // habitual en tiendas deportivas.
        borderRadius: '0',
        paddingX: '1.75rem',
        paddingY: '0.85rem',
        label: { fontWeight: '700' }
      }
    },
    card: {
      root: { borderRadius: '0', shadow: 'none' }
    },
    inputtext: {
      root: { borderRadius: '0' }
    },
    select: {
      root: { borderRadius: '0' }
    },
    paginator: {
      root: { borderRadius: '0' }
    }
  }
});
