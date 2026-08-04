import { ImagenPipe } from './imagen.pipe';
import { environment } from '../../environments/environment';

/**
 * El pipe decide de donde sale cada imagen del catalogo. Si se equivoca,
 * no se ve ninguna foto en toda la tienda.
 */
describe('ImagenPipe', () => {

  let pipe: ImagenPipe;
  const API = environment.apiUrl;

  beforeEach(() => {
    pipe = new ImagenPipe();
  });

  it('antepone la direccion del entorno a una ruta relativa', () => {
    expect(pipe.transform('/imagenes/prenda-camiseta-running.svg'))
      .toBe(`${API}/imagenes/prenda-camiseta-running.svg`);
  });

  it('añade la barra inicial si la ruta no la trae', () => {
    expect(pipe.transform('imagenes/foto.jpg')).toBe(`${API}/imagenes/foto.jpg`);
  });

  it('deja intacta una URL absoluta', () => {
    // En la base de datos pueden quedar valores antiguos con el servidor
    // dentro; deben seguir viendose.
    const absoluta = 'http://localhost:8080/imagenes/antigua.jpg';
    expect(pipe.transform(absoluta)).toBe(absoluta);

    const segura = 'https://cdn.ejemplo.com/foto.webp';
    expect(pipe.transform(segura)).toBe(segura);
  });

  it('no toca las imagenes del propio frontend', () => {
    expect(pipe.transform('assets/altaPinta.jpg')).toBe('assets/altaPinta.jpg');
  });

  it('no toca las imagenes embebidas', () => {
    // Las vistas previas del formulario de producto llegan como data URI
    const dataUri = 'data:image/png;base64,iVBORw0KGgo=';
    expect(pipe.transform(dataUri)).toBe(dataUri);
  });

  it('no toca las vistas previas de un archivo recien elegido', () => {
    // Al elegir una foto en el panel de administracion, el componente hace
    // URL.createObjectURL(file), que devuelve una direccion "blob:". La
    // plantilla la pasa por este pipe porque el operador | tiene menos
    // precedencia que ||:
    //
    //     [src]="slot.preview || slot.url | imagen"   ->   (a || b) | imagen
    //
    // Sin este caso, el pipe le anteponia la direccion del backend y salia
    // "http://localhost:8080/blob:http://localhost:4200/...", que no existe:
    // se elegia la foto y no aparecia nada.
    const blob = 'blob:http://localhost:4200/9f1c-4a2b-8e77';
    expect(pipe.transform(blob)).toBe(blob);
  });

  it('devuelve la imagen de repuesto cuando no hay valor', () => {
    expect(pipe.transform(null)).toBe('assets/altaPinta.jpg');
    expect(pipe.transform(undefined)).toBe('assets/altaPinta.jpg');
    expect(pipe.transform('')).toBe('assets/altaPinta.jpg');
  });
});
