# -*- coding: utf-8 -*-
"""
Genera los cuestionarios de evaluacion de calidad de AltaPinta segun
ISO/IEC 25010:2023, en el formato que pidio el docente (escala Likert,
un bloque por caracteristica, listo para transcribir a Google Forms).

Cada item se refiere a una funcion que EXISTE en el sistema: las rutas de
Angular y los controladores de Spring que hay en el repositorio. No se
enuncia nada que el software no haga.
"""
import html
import pathlib
import subprocess

SALIDA = pathlib.Path(__file__).parent

# --------------------------------------------------------------------------
#  Las 9 caracteristicas y sus subcaracteristicas segun ISO/IEC 25010:2023.
#  El numero de subcaracteristicas por caracteristica no es uniforme: la
#  norma define 3, 3, 2, 8, 4, 6, 5, 4 y 5 respectivamente (40 en total),
#  y el cuestionario respeta esa proporcion en lugar de repartir los items
#  a partes iguales.
# --------------------------------------------------------------------------

USUARIOS = [
    ("1. Adecuación Funcional",
     "Si el sistema hace lo que el usuario necesita, y lo hace bien.",
     [
        ("Completitud funcional",
         "AltaPinta incluye todo lo que necesito para comprar: buscar prendas, "
         "ver tallas disponibles, añadir al carrito, pagar y consultar mis pedidos."),
        ("Corrección funcional",
         "Los datos que muestra el sistema son correctos: el precio del carrito, "
         "el total con el envío y el stock por talla coinciden con lo que finalmente pago y recibo."),
        ("Pertinencia funcional",
         "Las funciones que ofrece son las apropiadas para una tienda de ropa deportiva; "
         "no sobra ninguna ni tengo que dar rodeos para hacer lo que quiero."),
     ]),

    ("2. Eficiencia de Desempeño",
     "Cuántos recursos consume el sistema para responder.",
     [
        ("Comportamiento temporal",
         "Las páginas del catálogo y el detalle de producto se cargan en un tiempo que me parece aceptable."),
        ("Utilización de recursos",
         "Navegar por AltaPinta no ralentiza mi equipo ni consume una cantidad de datos que me resulte notoria."),
        ("Capacidad",
         "El sistema mantiene su velocidad aunque el catálogo tenga muchas prendas "
         "o aunque yo tenga varios artículos en el carrito."),
     ]),

    ("3. Compatibilidad",
     "Cómo convive el sistema con otros y cómo intercambia información.",
     [
        ("Coexistencia",
         "AltaPinta funciona correctamente con el navegador que uso habitualmente, "
         "sin interferir con otras pestañas o programas abiertos."),
        ("Interoperabilidad",
         "La factura en PDF que emite el sistema se abre y se imprime sin problemas "
         "con el lector de PDF que tengo instalado."),
     ]),

    ("4. Capacidad de Interacción",
     "Antes llamada Usabilidad. Es la característica con más subcaracterísticas "
     "de la norma (ocho), por eso este bloque es el más extenso.",
     [
        ("Reconocibilidad de la adecuación",
         "Desde la primera pantalla entiendo que AltaPinta es una tienda de ropa deportiva "
         "y qué puedo hacer en ella."),
        ("Capacidad de aprendizaje",
         "Aprendí a usar el sistema por mi cuenta, sin que nadie me explicara ni leer instrucciones."),
        ("Operabilidad",
         "Realizar una compra completa (elegir talla, añadir al carrito, pagar) me resulta sencillo."),
        ("Protección contra errores del usuario",
         "El sistema me impide equivocarme: no me deja añadir más unidades de las que hay en stock "
         "ni continuar el pago si falta un dato."),
        ("Compromiso del usuario",
         "El diseño de la tienda me resulta agradable y me da confianza para comprar."),
        ("Inclusividad",
         "Los textos se leen con comodidad, el contraste entre letra y fondo es suficiente "
         "y puedo usar la tienda desde el móvil igual que desde la computadora."),
        ("Asistencia al usuario",
         "Cuando algo sale mal, el mensaje que aparece me dice qué ocurrió y qué debo hacer "
         "(por ejemplo, que no hay stock de una talla concreta)."),
        ("Autodescripción",
         "Los botones, iconos y secciones (Carrito, Favoritos, Mis pedidos) indican claramente "
         "para qué sirven sin tener que probarlos."),
     ]),

    ("5. Fiabilidad",
     "Si el sistema hace su trabajo sin fallar, y qué pasa cuando falla.",
     [
        ("Ausencia de fallos",
         "He usado AltaPinta sin encontrarme con errores, pantallas en blanco ni funciones que no respondan."),
        ("Disponibilidad",
         "El sistema ha estado disponible cada vez que he querido usarlo."),
        ("Tolerancia a fallos",
         "Si se corta la conexión o falla el pago, el sistema no se rompe: me avisa y puedo volver a intentarlo."),
        ("Capacidad de recuperación",
         "Cuando ha ocurrido un problema, no he perdido información: el carrito y mis datos seguían ahí al volver."),
     ]),

    ("6. Seguridad",
     "Percepción del usuario sobre la protección de sus datos. Las evidencias "
     "técnicas de esta característica se recogen en el Formulario 2.",
     [
        ("Confidencialidad",
         "Confío en que mis datos personales y los de mi tarjeta solo son visibles para mí."),
        ("Integridad",
         "Ningún otro usuario puede modificar mi carrito, mis direcciones ni mis pedidos."),
        ("No repudio",
         "Cada compra deja un comprobante (la factura en PDF) que sirve como prueba de la operación."),
        ("Responsabilidad (trazabilidad)",
         "El sistema deja registro de lo que hago: puedo consultar el historial de mis pedidos y su estado."),
        ("Autenticidad",
         "Para entrar a mi cuenta debo identificarme, y el registro exige verificar el correo, "
         "lo que impide que alguien se haga pasar por mí."),
        ("Resistencia",
         "El sistema me obliga a iniciar sesión para acceder a mi carrito, mis tarjetas y mis pedidos; "
         "no se llega a ellos escribiendo la dirección directamente."),
     ]),
]

TECNICO = [
    ("7. Mantenibilidad",
     "Facilidad para corregir y evolucionar el software. La evalúa quien lee "
     "el código, no quien compra.",
     [
        ("Modularidad",
         "El sistema está dividido en componentes con responsabilidades separadas "
         "(controlador, servicio, repositorio en el backend; componentes y servicios en Angular), "
         "de modo que tocar uno no obliga a tocar los demás."),
        ("Reusabilidad",
         "Hay piezas reutilizadas en varios puntos del sistema en lugar de duplicadas "
         "(por ejemplo, el servicio de autenticación o el pipe de imágenes)."),
        ("Analizabilidad",
         "Ante un fallo es posible localizar su causa con el código y los registros disponibles."),
        ("Modificabilidad",
         "Se puede modificar una funcionalidad sin introducir defectos en otras partes del sistema."),
        ("Capacidad de ser probado",
         "El sistema cuenta con pruebas automatizadas que se ejecutan solas y que fallan "
         "cuando alguien rompe una funcionalidad."),
     ]),

    ("8. Flexibilidad",
     "Antes llamada Portabilidad.",
     [
        ("Adaptabilidad",
         "El sistema se adapta a distintos entornos y tamaños de pantalla sin cambiar el código."),
        ("Escalabilidad",
         "La arquitectura permite atender un aumento de usuarios o de catálogo "
         "sin rehacer el diseño (por ejemplo, el catálogo se sirve paginado)."),
        ("Capacidad de instalación",
         "Un desarrollador nuevo puede instalar y arrancar el proyecto siguiendo la documentación del repositorio."),
        ("Capacidad de reemplazo",
         "Se podría sustituir un componente por otro equivalente (la base de datos, el servicio de correo) "
         "con un cambio acotado de configuración."),
     ]),

    ("9. Seguridad Física / Operacional (Safety)",
     "Característica incorporada en la revisión de 2023. En un comercio "
     "electrónico no hay riesgo físico, así que se interpreta como la "
     "protección frente a consecuencias no deseadas de la operación: "
     "cobros indebidos, stock descuadrado o pérdida de dinero.",
     [
        ("Restricción operativa",
         "El sistema impide operaciones que dejarían datos incoherentes, como confirmar un pedido "
         "con más unidades de las que hay en stock o con saldo insuficiente en la tarjeta."),
        ("Identificación de riesgos",
         "Están identificadas las operaciones críticas del sistema (el pago y el descuento de stock) "
         "y reciben un tratamiento especial."),
        ("Fallo seguro",
         "Si el pago falla a mitad del proceso, la transacción se deshace por completo: "
         "no se cobra al cliente ni se descuenta stock."),
        ("Advertencia de riesgos",
         "El sistema avisa antes de una acción con consecuencias, como cancelar un pedido."),
        ("Integración segura",
         "Un cobro rechazado queda registrado como evidencia aunque la operación se haya deshecho, "
         "de modo que el fallo no desaparece sin dejar rastro."),
     ]),
]

ESCALA = [
    ("1", "Totalmente en desacuerdo"),
    ("2", "En desacuerdo"),
    ("3", "Ni de acuerdo ni en desacuerdo"),
    ("4", "De acuerdo"),
    ("5", "Totalmente de acuerdo"),
]


def bloque(caracteristicas, inicio=1):
    partes, n = [], inicio
    for titulo, nota, items in caracteristicas:
        partes.append('<section class="car">')
        partes.append(f'<h3>{html.escape(titulo)}</h3>')
        partes.append(f'<p class="nota">{html.escape(nota)}</p>')
        partes.append('<table class="items"><thead><tr>'
                      '<th class="n">N.º</th><th class="sub">Subcaracterística</th>'
                      '<th>Ítem — indique su grado de acuerdo</th>'
                      '<th class="lik">1 &nbsp; 2 &nbsp; 3 &nbsp; 4 &nbsp; 5</th>'
                      '</tr></thead><tbody>')
        for sub, texto in items:
            partes.append(
                f'<tr><td class="n">{n}</td><td class="sub">{html.escape(sub)}</td>'
                f'<td>{html.escape(texto)}</td>'
                f'<td class="lik">&#9711; &#9711; &#9711; &#9711; &#9711;</td></tr>')
            n += 1
        partes.append('</tbody></table></section>')
    return "\n".join(partes), n


cuerpo_usuarios, siguiente = bloque(USUARIOS, 1)
cuerpo_tecnico, total = bloque(TECNICO, siguiente)

filas_escala = "".join(
    f'<tr><td class="v">{v}</td><td>{html.escape(t)}</td></tr>' for v, t in ESCALA)

HTML = f"""<meta charset="utf-8">
<title>Cuestionarios de evaluación de la calidad — AltaPinta</title>
<style>
  @page {{ size: A4; margin: 18mm 16mm 16mm 16mm; }}
  * {{ box-sizing: border-box; }}
  body {{ font-family: "Segoe UI", Calibri, sans-serif; font-size: 10.2pt;
         color: #1b1b1b; line-height: 1.45; }}
  h1 {{ font-size: 18pt; margin: 0 0 2mm; color: #0f2f4f; }}
  h2 {{ font-size: 13pt; margin: 9mm 0 2mm; color: #0f2f4f;
        border-bottom: 2px solid #0f2f4f; padding-bottom: 1.5mm; }}
  h3 {{ font-size: 11pt; margin: 6mm 0 1mm; color: #14507e; }}
  .sup {{ font-size: 9.4pt; color: #555; margin: 0 0 6mm; }}
  .nota {{ font-size: 9.2pt; color: #555; margin: 0 0 2mm; font-style: italic; }}
  table {{ border-collapse: collapse; width: 100%; margin-bottom: 3mm; }}
  th, td {{ border: 1px solid #c8d3dd; padding: 1.6mm 2mm; vertical-align: top;
           text-align: left; }}
  thead th {{ background: #eef3f8; font-size: 9pt; color: #0f2f4f; }}
  td.n {{ width: 9mm; text-align: center; color: #666; }}
  td.sub, th.sub {{ width: 40mm; font-size: 9.3pt; }}
  td.lik, th.lik {{ width: 26mm; text-align: center; letter-spacing: 1.4px;
                    color: #7a8a99; white-space: nowrap; }}
  td.v {{ width: 12mm; text-align: center; font-weight: 600; }}
  .ficha td {{ font-size: 9.6pt; }}
  .ficha td:first-child {{ width: 45mm; background: #f6f8fa; font-weight: 600; }}
  .aviso {{ border-left: 3px solid #c8992b; background: #fdf8ec;
            padding: 3mm 4mm; margin: 4mm 0; font-size: 9.4pt; }}
  section.car {{ break-inside: avoid; }}
  .salto {{ break-before: page; }}
  .pie {{ margin-top: 8mm; font-size: 8.8pt; color: #666;
          border-top: 1px solid #ccc; padding-top: 2mm; }}
</style>

<h1>Cuestionarios de evaluación de la calidad del software</h1>
<p class="sup"><strong>Sistema evaluado:</strong> AltaPinta — tienda de ropa deportiva en línea &nbsp;·&nbsp;
<strong>Norma:</strong> ISO/IEC 25010:2023, modelo de calidad del producto &nbsp;·&nbsp;
<strong>Asignatura:</strong> Ingeniería de Software II (AIS73), UNAMBA<br>
<strong>Elaborado por:</strong> Jhosep Emilio Paniagua Ferrel (222182)</p>

<h2>Cómo se usa este instrumento</h2>
<p>La norma ISO/IEC 25010:2023 define <strong>9 características</strong> de calidad y, dentro de ellas,
<strong>{total - 1} subcaracterísticas</strong>. Este instrumento incluye <strong>un ítem por
subcaracterística</strong>, de modo que ninguna quede sin medir y ninguna pese más de lo que
la norma le concede.</p>

<div class="aviso">
<strong>Por qué son dos formularios y no uno.</strong> Un cliente de la tienda puede juzgar si la
compra fue fácil o si el precio cuadraba, pero no puede opinar sobre la modularidad del código
ni sobre si existen pruebas automatizadas: no ve el código. Aplicar esas preguntas a
compradores produciría respuestas sin fundamento y una media inflada.
Por eso las características 1 a 6 se aplican a <strong>usuarios finales</strong> y las
características 7 a 9 al <strong>equipo técnico</strong>, que sí puede sustentar su respuesta.
</div>

<h3>Escala Likert de 5 puntos</h3>
<table><thead><tr><th class="v">Valor</th><th>Significado</th></tr></thead>
<tbody>{filas_escala}</tbody></table>

<h3>Cómo se calcula el resultado</h3>
<p>Por cada característica se promedian los valores de sus ítems. Ese promedio se interpreta
con la escala del formato de informe de la asignatura:</p>
<table><thead><tr><th>Rango de media</th><th>Equivalente (%)</th><th>Nivel de cumplimiento</th></tr></thead>
<tbody>
<tr><td>1.00 – 2.33</td><td>20.00 % – 46.60 %</td><td>Bajo / No cumple</td></tr>
<tr><td>2.34 – 3.66</td><td>46.80 % – 73.20 %</td><td>Medio / Cumple parcialmente</td></tr>
<tr><td>3.67 – 5.00</td><td>73.40 % – 100.00 %</td><td>Alto / Cumple satisfactoriamente</td></tr>
</tbody></table>
<p>El porcentaje se obtiene como <em>media &divide; 5 &times; 100</em>. Conviene reportar junto a la
media la <strong>desviación estándar</strong>: dos grupos con la misma media pueden esconder un
consenso o un desacuerdo total, y eso cambia la conclusión.</p>

<div class="salto"></div>
<h2>Formulario 1 — Usuarios finales</h2>
<p class="sup">Dirigido a personas que hayan comprado o navegado en AltaPinta.
Características 1 a 6 · {siguiente - 1} ítems · duración estimada 6 minutos.</p>

<h3>Datos del participante (sin identificación personal)</h3>
<table class="ficha"><tbody>
<tr><td>Edad</td><td>&#9711; 18–24 &nbsp; &#9711; 25–34 &nbsp; &#9711; 35–44 &nbsp; &#9711; 45 o más</td></tr>
<tr><td>Dispositivo usado</td><td>&#9711; Computadora &nbsp; &#9711; Teléfono móvil &nbsp; &#9711; Tableta</td></tr>
<tr><td>Navegador</td><td>&#9711; Chrome &nbsp; &#9711; Edge &nbsp; &#9711; Firefox &nbsp; &#9711; Otro: __________</td></tr>
<tr><td>¿Completó una compra?</td><td>&#9711; Sí &nbsp; &#9711; No, solo navegué</td></tr>
<tr><td>Módulos que utilizó</td><td>&#9711; Catálogo &nbsp; &#9711; Carrito &nbsp; &#9711; Pago
   &nbsp; &#9711; Mis pedidos &nbsp; &#9711; Favoritos &nbsp; &#9711; Recuperar contraseña</td></tr>
</tbody></table>
<p class="nota">Estos datos permiten segmentar los resultados. Sin ellos no se puede saber si una
puntuación baja viene de todos los usuarios o solo de quienes entraron desde el móvil.</p>

{cuerpo_usuarios}

<h3>Preguntas abiertas</h3>
<table class="ficha"><tbody>
<tr><td>¿Qué fue lo más difícil de hacer en la tienda?</td><td>&nbsp;<br>&nbsp;<br>&nbsp;</td></tr>
<tr><td>¿Encontró algún error? ¿En qué pantalla?</td><td>&nbsp;<br>&nbsp;<br>&nbsp;</td></tr>
<tr><td>¿Qué añadiría o cambiaría?</td><td>&nbsp;<br>&nbsp;<br>&nbsp;</td></tr>
</tbody></table>

<div class="salto"></div>
<h2>Formulario 2 — Equipo técnico y evaluadores</h2>
<p class="sup">Dirigido a quienes tienen acceso al código, al repositorio y al entorno de despliegue.
Características 7 a 9 · {total - siguiente} ítems.</p>

<div class="aviso">
Cada respuesta de este formulario debe poder sustentarse con una evidencia concreta
(un archivo, una prueba, una captura). Una puntuación sin evidencia no es una medición:
es una opinión. Anótela en la columna de la derecha.
</div>

<h3>Datos del evaluador</h3>
<table class="ficha"><tbody>
<tr><td>Rol</td><td>&#9711; Desarrollador &nbsp; &#9711; Tester &nbsp; &#9711; Docente evaluador &nbsp; &#9711; Otro</td></tr>
<tr><td>¿Revisó el código fuente?</td><td>&#9711; Sí, completo &nbsp; &#9711; Parcialmente &nbsp; &#9711; No</td></tr>
<tr><td>¿Ejecutó el proyecto en local?</td><td>&#9711; Sí &nbsp; &#9711; No</td></tr>
</tbody></table>

{cuerpo_tecnico}

<h3>Evidencia por característica</h3>
<table class="ficha"><tbody>
<tr><td>Mantenibilidad</td><td>&nbsp;<br>&nbsp;</td></tr>
<tr><td>Flexibilidad</td><td>&nbsp;<br>&nbsp;</td></tr>
<tr><td>Seguridad operacional</td><td>&nbsp;<br>&nbsp;</td></tr>
</tbody></table>

<p class="pie">Instrumento elaborado sobre el modelo de calidad del producto de la norma
ISO/IEC 25010:2023 y el formato de informe de la asignatura. Los ítems describen funciones
verificables en el repositorio del sistema evaluado.</p>
"""

destino_html = SALIDA / "cuestionarios.html"
destino_html.write_text(HTML, encoding="utf-8")

CHROME = [
    r"C:\Program Files\Google\Chrome\Application\chrome.exe",
    r"C:\Program Files (x86)\Google\Chrome\Application\chrome.exe",
    r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe",
]
exe = next((c for c in CHROME if pathlib.Path(c).exists()), None)
if not exe:
    raise SystemExit("no se encontro Chrome ni Edge")

pdf = SALIDA / "Cuestionarios_Calidad_ISO25010_AltaPinta.pdf"
subprocess.run([exe, "--headless", "--disable-gpu", "--no-pdf-header-footer",
                f"--print-to-pdf={pdf}", destino_html.as_uri()],
               check=True, capture_output=True, timeout=180)

# --------------------------------------------------------------------------
#  Version en texto plano. El PDF es para entregar; esto es para trabajar:
#  transcribir 40 items a Google Forms leyendolos de un PDF es innecesario
#  cuando se pueden copiar y pegar.
# --------------------------------------------------------------------------
def texto(caracteristicas, inicio=1):
    lineas, n = [], inicio
    for titulo, _, items in caracteristicas:
        lineas.append("")
        lineas.append("=" * 70)
        lineas.append(f"SECCION — {titulo}")
        lineas.append("=" * 70)
        for sub, item in items:
            lineas.append("")
            lineas.append(f"[{n}] ({sub})")
            lineas.append(item)
            n += 1
    return lineas, n


escala_txt = "  ".join(f"{v}={t}" for v, t in ESCALA)
lin_u, sig = texto(USUARIOS, 1)
lin_t, _ = texto(TECNICO, sig)

plano = [
    "CUESTIONARIOS DE EVALUACION DE LA CALIDAD DEL SOFTWARE",
    "Sistema: AltaPinta  |  Norma: ISO/IEC 25010:2023",
    "",
    "Para transcribir a Google Forms:",
    "  - Cree DOS formularios distintos (usuarios finales / equipo tecnico).",
    "  - Cada seccion del formulario corresponde a una caracteristica.",
    "  - Tipo de pregunta: 'Escala lineal', de 1 a 5.",
    f"  - Etiquetas de la escala: {escala_txt}",
    "  - Marque todos los items como obligatorios: un item en blanco",
    "    desplaza la media de esa caracteristica sin que se note.",
    "",
    "#" * 70,
    "FORMULARIO 1 — USUARIOS FINALES (caracteristicas 1 a 6)",
    "#" * 70,
] + lin_u + [
    "",
    "#" * 70,
    "FORMULARIO 2 — EQUIPO TECNICO (caracteristicas 7 a 9)",
    "#" * 70,
] + lin_t

destino_txt = SALIDA / "Cuestionarios_para_Google_Forms.txt"
destino_txt.write_text("\n".join(plano) + "\n", encoding="utf-8")
print(f"TXT: {destino_txt}")

print(f"items totales: {total - 1}")
print(f"  formulario 1 (usuarios): {siguiente - 1}")
print(f"  formulario 2 (tecnico):  {total - siguiente}")
print(f"PDF: {pdf}  ({pdf.stat().st_size // 1024} KB)")
