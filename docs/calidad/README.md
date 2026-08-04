# Evaluación de la calidad del software

| Archivo | Qué es |
|---|---|
| [`Plan_de_Evaluacion_de_Software.pdf`](Plan_de_Evaluacion_de_Software.pdf) | **Documento oficial del equipo.** Define el instrumento, la muestra, el baremo y el procedimiento. Incluye el cuestionario en su anexo. |
| [`Cuestionarios_Calidad_ISO25010_AltaPinta.pdf`](Cuestionarios_Calidad_ISO25010_AltaPinta.pdf) | Instrumento ampliado, con un ítem por cada subcaracterística de la norma en su versión 2023. |
| [`Cuestionarios_para_Google_Forms.txt`](Cuestionarios_para_Google_Forms.txt) | El mismo instrumento en texto, para transcribirlo sin teclearlo. |
| [`cuestionarios.py`](cuestionarios.py) | Genera los dos archivos anteriores. |

## Los dos instrumentos no son el mismo, y conviene saberlo

El plan del equipo trae su propio cuestionario en el anexo. El instrumento
ampliado se construyó por separado, antes de conocer el plan, siguiendo el
formato de informe de la asignatura. **No coinciden:**

| | Plan del equipo | Instrumento ampliado |
|---|---|---|
| Ítems | 20 cerrados + 1 abierta | 40 |
| Dimensiones | 6 | 9 |
| Nomenclatura | *Usabilidad*, *Compatibilidad y accesibilidad* — nombres de la versión de 2011 | *Capacidad de Interacción*, *Flexibilidad*, *Seguridad operacional* — nombres de la de 2023 |
| Formularios | Uno, a usuarios finales | Dos: usuarios finales y equipo técnico |
| Baremo | Cinco niveles; aprueba con media ≥ 3,41 | Tres niveles, los del formato de informe de la asignatura |
| Confiabilidad | Alfa de Cronbach ≥ 0,70 sobre un piloto de 10 usuarios | No contemplada |
| Muestra | 30 usuarios | Sin fijar |

**Cuál manda: el plan del equipo.** Es el documento acordado, con
responsables y fechas asignadas, y es el que define el método de
confiabilidad. Aplicar los dos cuestionarios a los mismos usuarios daría dos
promedios distintos para la misma dimensión y ninguno de los dos sería
defendible.

## Qué hacer con el instrumento ampliado

No es redundante del todo. El plan evalúa **6 dimensiones** y la norma en su
versión **2023 define 9**; el formato de informe de la asignatura pide las
nueve. Las tres que el plan no alcanza son **Mantenibilidad**,
**Flexibilidad** y **Seguridad física/operacional**, y no las alcanza por una
razón sensata: un comprador no puede juzgar si el código es modular ni si
hay pruebas automatizadas.

Esas tres son justamente el **Formulario 2** del instrumento ampliado, los
14 ítems dirigidos al equipo técnico. La combinación coherente es:

- **Formulario del plan (20 ítems)** → a los 30 usuarios, tal como está.
- **Formulario 2 del instrumento ampliado (14 ítems)** → al equipo técnico,
  para cubrir las tres características restantes.
- **Formulario 1 del instrumento ampliado (26 ítems)** → no aplicar: se
  solapa con el del plan y usa otro baremo.

Esta es una recomendación, no una decisión tomada: quien decide es el
equipo.

## Nota sobre el cronograma

El plan fija la aplicación entre el **07 y el 12 de julio de 2026**. Esas
fechas ya pasaron; si el cuestionario aún no se ha aplicado, hay que
actualizar la Tabla 1.5 antes de entregar el informe de resultados, o el
documento describirá un trabajo en fechas que no se corresponden con las
reales.
