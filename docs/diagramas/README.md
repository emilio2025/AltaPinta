# Diagramas de proceso (BPMN)

54 diagramas, uno por requisito funcional, con un pool y un carril por actor
al modo de Bizagi. El flujo baja de carril cuando cambia quién ejecuta el
paso: eso es lo que aporta un diagrama con carriles frente a un diagrama de
flujo corriente.

| Carpeta | Contenido |
|---|---|
| [`requisitos/`](requisitos) | Los 32 requisitos originales, RF001–RF032 |
| [`nuevos-modulos/`](nuevos-modulos) | Los 22 requisitos nuevos, RF033–RF054, en 11 módulos |
| [`generador/`](generador) | El código que los dibuja |

Están en SVG y no en PNG a propósito: GitHub los dibuja igual, pesan una
décima parte y, al ser texto, un cambio en un diagrama se ve en el `diff`
en lugar de aparecer como un binario distinto.

## Regenerarlos

```bash
cd docs/diagramas/generador
python bpmn_rf.py        # RF001-RF032  -> bpmn/
python bpmn_modulos.py   # RF033-RF054  -> bpmn_modulos/
```

Para obtener PNG (por ejemplo, para pegarlos en un documento de Word que no
acepta SVG) hace falta `sharp`:

```bash
npm install sharp && node convertir.js
```

## De dónde salen los pasos

De leer el código, no de suponer lo que debería hacer el sistema. RF035
refleja las cuatro reglas de `PasswordValidator`; RF051, el bloqueo
pesimista sobre la fila de la talla; RF053, el registro del cobro rechazado
en una transacción aparte; RF034, la respuesta que no revela si el correo
está registrado.

`requisitos_nuevos.py` es la transcripción de *Nuevos requerimientos Alta
Pinta.docx* y es la única fuente válida para los RF033–RF054.

> **Aviso.** En una entrega anterior circularon diagramas con códigos
> RS060–RS069 y RU070. Esos códigos no existen en el pliego de
> requerimientos: fueron un invento y no deben usarse. Los sustituyen los
> de `nuevos-modulos/`.

## Requisitos no implementados

Entre los 32 originales hay dos que el diagrama recoge como diseño previsto
y no como comportamiento actual:

- **RF018** — pago por Yape u otro medio digital. Marcado como opcional en
  el pliego; no está implementado.
- **RF023** — el costo de envío depende de la dirección. Implementado solo
  en parte.

Los 22 requisitos nuevos sí están todos implementados; se comprobó contra el
repositorio antes de dibujarlos.
