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

  it('devuelve la imagen de repuesto cuando no hay valor', () => {
    expect(pipe.transform(null)).toBe('assets/altaPinta.jpg');
    expect(pipe.transform(undefined)).toBe('assets/altaPinta.jpg');
    expect(pipe.transform('')).toBe('assets/altaPinta.jpg');
  });
});
