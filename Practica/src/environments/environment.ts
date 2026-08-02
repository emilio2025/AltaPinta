/**
 * Configuración de PRODUCCIÓN.
 *
 * Angular sustituye este archivo por environment.development.ts al
 * compilar en modo desarrollo (ver fileReplacements en angular.json).
 *
 * apiUrl vacío significa "el mismo origen que sirve la aplicación": es lo
 * habitual cuando el backend queda detrás del mismo dominio o de un proxy
 * inverso. Si el backend vive en otro dominio, ponlo aquí sin la barra
 * final, por ejemplo "https://api.altapinta.pe".
 */
export const environment = {
  production: true,
  apiUrl: '',
};
