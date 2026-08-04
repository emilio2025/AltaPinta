# Diagramas de proceso — nuevos módulos (RF033–RF054)

Un diagrama BPMN por requisito, con un carril por actor, al modo de Bizagi.
El flujo baja de carril cuando cambia quién ejecuta el paso: eso es lo que
aporta un diagrama con carriles frente a un diagrama de flujo corriente.

Los 22 requisitos están implementados. Los pasos describen lo que el código
hace de verdad, no un diseño ideal.

## Módulo 1 — Seguridad de Autenticación

| Requisito | Nombre | Actores | Diagrama |
|---|---|---|---|
| RF033 | Cierre de sesión | Cliente, Sistema | [ver](RF033_Cierre-de-sesion.svg) |
| RF034 | Recuperación de contraseña | Visitante, Sistema | [ver](RF034_Recuperacion-de-contrasena.svg) |
| RF035 | Validación de contraseña | Cliente, Sistema | [ver](RF035_Validacion-de-contrasena.svg) |

## Módulo 2 — Catálogo de Productos

| Requisito | Nombre | Actores | Diagrama |
|---|---|---|---|
| RF036 | Búsqueda de productos | Cliente, Sistema | [ver](RF036_Busqueda-de-productos.svg) |
| RF037 | Ordenamiento de productos | Cliente, Sistema | [ver](RF037_Ordenamiento-de-productos.svg) |
| RF038 | Paginación | Cliente, Sistema | [ver](RF038_Paginacion.svg) |

## Módulo 3 — Carrito de Compras

| Requisito | Nombre | Actores | Diagrama |
|---|---|---|---|
| RF039 | Persistencia del carrito | Cliente, Sistema | [ver](RF039_Persistencia-del-carrito.svg) |

## Módulo 4 — Gestión de Pedidos

| Requisito | Nombre | Actores | Diagrama |
|---|---|---|---|
| RF040 | Creación de pedido | Cliente, Sistema | [ver](RF040_Creacion-de-pedido.svg) |
| RF041 | Historial de pedidos | Cliente, Sistema | [ver](RF041_Historial-de-pedidos.svg) |
| RF042 | Estados de pedido | Administrador, Sistema, Cliente | [ver](RF042_Estados-de-pedido.svg) |
| RF043 | Cancelación de pedidos | Cliente, Sistema | [ver](RF043_Cancelacion-de-pedidos.svg) |

## Módulo 5 — Proceso de Compra

| Requisito | Nombre | Actores | Diagrama |
|---|---|---|---|
| RF044 | Control de errores (rollback) | Cliente, Sistema | [ver](RF044_Control-de-errores-rollback.svg) |

## Módulo 6 — Pagos

| Requisito | Nombre | Actores | Diagrama |
|---|---|---|---|
| RF045 | Evitar duplicación de pagos | Cliente, Sistema | [ver](RF045_Evitar-duplicacion-de-pagos.svg) |
| RF053 | Integración pagos | Cliente, Sistema | [ver](RF053_Integracion-pagos.svg) |

## Módulo 7 — Gestión de Envíos

| Requisito | Nombre | Actores | Diagrama |
|---|---|---|---|
| RF046 | Registro de direcciones | Cliente, Sistema | [ver](RF046_Registro-de-direcciones.svg) |

## Módulo 8 — Facturación

| Requisito | Nombre | Actores | Diagrama |
|---|---|---|---|
| RF047 | Datos fiscales | Cliente, Sistema | [ver](RF047_Datos-fiscales.svg) |
| RF048 | Numeración de comprobantes | Sistema | [ver](RF048_Numeracion-de-comprobantes.svg) |

## Módulo 9 — Notificaciones e Integración

| Requisito | Nombre | Actores | Diagrama |
|---|---|---|---|
| RF052 | Integración correo | Sistema, Cliente | [ver](RF052_Integracion-correo.svg) |

## Módulo 10 — Administración y Reportes

| Requisito | Nombre | Actores | Diagrama |
|---|---|---|---|
| RF049 | Reportes | Administrador, Sistema | [ver](RF049_Reportes.svg) |
| RF054 | Auditoría | Administrador, Sistema | [ver](RF054_Auditoria.svg) |

## Módulo 11 — Integridad del Sistema

| Requisito | Nombre | Actores | Diagrama |
|---|---|---|---|
| RF050 | Validación de datos | Cliente, Sistema | [ver](RF050_Validacion-de-datos.svg) |
| RF051 | Consistencia de stock | Cliente, Sistema | [ver](RF051_Consistencia-de-stock.svg) |

