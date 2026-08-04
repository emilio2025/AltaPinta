import { Pipe, PipeTransform } from '@angular/core';

import { environment } from '../../environments/environment';

/**
 * Compone la URL de una imagen de producto.
 *
 * En la base de datos se guarda la ruta relativa ("/imagenes/x.jpg"), no
 * la URL completa: guardarla entera ataba los datos a la máquina de
 * desarrollo y al desplegar en otro sitio todas las fotos apuntaban a un
 * servidor inexistente.
 *
 * Este pipe le antepone la dirección del entorno. Sigue aceptando valores
 * absolutos por si queda alguno antiguo en la base de datos, y devuelve la
 * imagen de repuesto cuando el producto no tiene ninguna.
 *
 *     <img [src]="producto.imagenUrl | imagen">
 */
@Pipe({ name: 'imagen', standalone: true })
export class ImagenPipe implements PipeTransform {

  private static readonly REPUESTO = 'assets/altaPinta.jpg';

  transform(valor: string | null | undefined): string {
    if (!valor) return ImagenPipe.REPUESTO;

    // Ya viene completa (dato antiguo o imagen externa): se deja tal cual
    if (valor.startsWith('http://') || valor.startsWith('https://')) {
      return valor;
    }
    // Una imagen del propio frontend tampoco lleva prefijo
    if (valor.startsWith('assets/') || valor.startsWith('data:')) {
      return valor;
    }

    // Vista previa de un archivo recién elegido en el panel de
    // administración: URL.createObjectURL(file) devuelve una dirección
    // "blob:" que ya resuelve sola. La plantilla la pasa por aquí porque el
    // operador | tiene menos precedencia que ||:
    //
    //     [src]="slot.preview || slot.url | imagen"   ->   (a || b) | imagen
    //
    // Anteponerle la dirección del backend producía
    // "http://localhost:8080/blob:http://localhost:4200/…", que no existe:
    // se elegía la foto y no se veía nada.
    if (valor.startsWith('blob:')) {
      return valor;
    }

    const ruta = valor.startsWith('/') ? valor : '/' + valor;
    return environment.apiUrl + ruta;
  }
}
