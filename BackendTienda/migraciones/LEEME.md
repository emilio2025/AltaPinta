# Migraciones antiguas (antes de Flyway)

**Estos archivos ya no se ejecutan y no hay que ejecutarlos.** Se quedan aquí
solo como registro de lo que se cambió y por qué.

Antes, el esquema lo creaba Hibernate con `ddl-auto=update`. Ese modo añade
tablas y columnas nuevas, pero nunca cambia el tipo de una columna existente
ni borra nada. Todo lo que no sabía hacer se corregía a mano con estos `.sql`,
lo que dejaba una situación incómoda: la única copia fiel del esquema era la
base de datos de una máquina concreta, y nadie más podía reconstruirla igual.

Desde la adopción de Flyway, el esquema vive en el repositorio:

    src/main/resources/db/migration/

`V1__esquema_inicial.sql` es el volcado del esquema tal y como quedó después de
aplicar todo lo de esta carpeta. Es decir: **el contenido de estos cinco
archivos ya está dentro de V1**. Se generó con `mysqldump --no-data` sobre la
base de datos real, no a mano, para que no hubiera diferencias.

## Cómo se hace un cambio de esquema a partir de ahora

1. Crear un archivo nuevo en `src/main/resources/db/migration/`, numerado a
   continuación del último: `V2__descripcion_corta.sql`.
2. Arrancar el backend. Flyway lo aplica solo, una vez, y lo apunta en la tabla
   `flyway_schema_history`.

Nunca se edita un archivo ya aplicado: Flyway guarda su suma de comprobación y
se niega a arrancar si cambia. Si algo salió mal, se corrige con un `V3__` que
lo deshaga.

## Qué hizo cada uno

| Archivo | Qué cambió |
|---|---|
| `001-importes-a-decimal.sql` | Pasó los importes de `DOUBLE` a `DECIMAL(12,2)`. Con `DOUBLE`, sumar precios acumulaba error: totales como `269.70000000000005`. |
| `002-eliminar-cvv.sql` | Borró la columna del CVV. Guardarlo está prohibido por PCI DSS, incluso cifrado. |
| `003-catalogo-deportivo.sql` | Sustituyó el catálogo de prueba por prendas deportivas reales, con sus categorías, tipos y deportes. |
| `004-imagenes-y-precios.sql` | Asignó una imagen a cada prenda y ajustó los precios por tipo de prenda. |
| `005-urls-de-imagen-relativas.sql` | Quitó `http://localhost:8080` del principio de las rutas de imagen. Guardar la URL entera ataba los datos a esta máquina: al desplegar en otro sitio, ninguna foto cargaba. |

Los `.py` y `.tsv` son las herramientas con las que se generaron 003, 004 y 005.
También son historia: no hace falta volver a ejecutarlos.
