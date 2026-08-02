/**
 * Configuración de DESARROLLO.
 *
 * El backend corre aparte, en el puerto 8080, mientras que "ng serve"
 * sirve la aplicación en el 4200. Por eso aquí sí hace falta la dirección
 * completa.
 */
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080',
};
