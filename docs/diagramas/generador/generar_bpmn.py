"""
Generador de diagramas BPMN con carriles por actor, al estilo Bizagi.

Diferencia con un diagrama de flujo corriente: aquí el eje vertical NO es el
tiempo, es QUIÉN ejecuta cada paso. El flujo avanza de izquierda a derecha y
baja de carril cuando la responsabilidad cambia de actor. Eso es lo que hace
legible de un vistazo quién hace qué.

Modelo de un diagrama:

    actores = ["Cliente", "Sistema"]
    pasos = [
        ("inicio",  "Cliente"),
        ("tarea",   "Cliente",  "Texto de la tarea"),
        ("gateway", "Sistema",  "¿Pregunta?", "No", "Qué pasa si no"),
        ("tarea",   "Sistema",  "Texto"),
        ("fin",     "Sistema"),
    ]

La compuerta lleva el camino principal ("Sí") al paso siguiente, y la rama
alternativa a la derecha, dentro de su propio carril, terminando en un
evento de fin. Por eso el paso posterior a una compuerta debe estar en OTRO
carril: si no, las dos ramas se pisarían.
"""

import html
import pathlib

# ------------------------------------------------------------------ estilo
CARRIL_H = 150
COL_W = 196
CAB_POOL = 34          # franja vertical con el nombre del proceso
CAB_CARRIL = 118       # franja con el nombre del actor
MARGEN = 18
TITULO_H = 46

TAREA_W, TAREA_H = 150, 62
GW = 46                # semidiagonal de la compuerta
EV_R = 17              # radio de los eventos

TIPO = "font-family='Segoe UI, Helvetica, Arial, sans-serif'"

C_TAREA_F, C_TAREA_B = "#EAF3FB", "#2E75B6"
C_GW_F, C_GW_B = "#FFF2CC", "#BF9000"
C_INI_F, C_INI_B = "#C6E0B4", "#548235"
C_FIN_F, C_FIN_B = "#F8CBAD", "#C00000"
C_LINEA = "#808080"
C_POOL = "#F2F2F2"
NEGRO = "#1a1a1a"


def _esc(s):
    return html.escape(str(s))


def _lineas(texto, max_chars=20):
    if isinstance(texto, list):
        return texto
    palabras, out, act = texto.split(), [], ""
    for w in palabras:
        if len(act) + len(w) + 1 <= max_chars:
            act = f"{act} {w}".strip()
        else:
            out.append(act); act = w
    if act:
        out.append(act)
    return out


def _txt(x, y, texto, tam=11, peso="400", color=NEGRO, anclaje="middle", max_chars=20):
    ls = _lineas(texto, max_chars)
    y0 = y - (len(ls) * (tam + 2)) / 2 + tam - 1
    return "\n".join(
        f"<text x='{x:.1f}' y='{y0 + i*(tam+2):.1f}' {TIPO} font-size='{tam}' "
        f"font-weight='{peso}' fill='{color}' text-anchor='{anclaje}'>{_esc(l)}</text>"
        for i, l in enumerate(ls))


def _tarea(cx, cy, texto):
    x, y = cx - TAREA_W/2, cy - TAREA_H/2
    return (f"<rect x='{x:.1f}' y='{y:.1f}' width='{TAREA_W}' height='{TAREA_H}' "
            f"rx='10' fill='{C_TAREA_F}' stroke='{C_TAREA_B}' stroke-width='2'/>\n"
            + _txt(cx, cy, texto, tam=11, max_chars=21))


def _gateway(cx, cy, texto):
    pts = f"{cx},{cy-GW/2} {cx+GW/2},{cy} {cx},{cy+GW/2} {cx-GW/2},{cy}"
    # El rotulo va ENCIMA del rombo: debajo lo atravesaba la linea del
    # camino principal, que sale por la punta inferior de la compuerta.
    return (f"<polygon points='{pts}' fill='{C_GW_F}' stroke='{C_GW_B}' "
            f"stroke-width='2'/>\n"
            f"<text x='{cx:.1f}' y='{cy+5:.1f}' {TIPO} font-size='15' "
            f"font-weight='700' fill='{C_GW_B}' text-anchor='middle'>+</text>\n"
            + _txt(cx, cy - GW/2 - 15, texto, tam=10, peso="600", max_chars=26))


def _inicio(cx, cy):
    return (f"<circle cx='{cx:.1f}' cy='{cy:.1f}' r='{EV_R}' fill='{C_INI_F}' "
            f"stroke='{C_INI_B}' stroke-width='2'/>")


def _fin(cx, cy):
    return (f"<circle cx='{cx:.1f}' cy='{cy:.1f}' r='{EV_R}' fill='{C_FIN_F}' "
            f"stroke='{C_FIN_B}' stroke-width='3.5'/>")


def _flecha(puntos, etiqueta=None, etq_xy=None):
    d = " L ".join(f"{x:.1f} {y:.1f}" for x, y in puntos)
    s = (f"<path d='M {d}' fill='none' stroke='{NEGRO}' stroke-width='1.5' "
         f"marker-end='url(#pta)'/>")
    if etiqueta:
        ex, ey = etq_xy or puntos[len(puntos)//2]
        s += ("\n" + f"<text x='{ex:.1f}' y='{ey:.1f}' {TIPO} font-size='10' "
              f"font-weight='600' fill='{NEGRO}' text-anchor='middle'>"
              f"{_esc(etiqueta)}</text>")
    return s


def generar_bpmn(titulo, actores, pasos, archivo):
    carril_de = {a: i for i, a in enumerate(actores)}

    # ---- asignar columna a cada paso -------------------------------------
    col, columnas = 0, []
    for p in pasos:
        columnas.append(col)
        # la compuerta consume una columna extra para su rama alternativa
        col += 2 if p[0] == "gateway" else 1
    n_cols = col + 1

    x0 = MARGEN + CAB_POOL + CAB_CARRIL
    y0 = MARGEN + TITULO_H
    ancho = x0 + n_cols * COL_W + MARGEN
    alto = y0 + len(actores) * CARRIL_H + MARGEN

    def cx(c): return x0 + c * COL_W + COL_W/2
    def cy(a): return y0 + carril_de[a] * CARRIL_H + CARRIL_H/2

    o = []
    o.append(_txt(ancho/2, MARGEN + 16, titulo, tam=15, peso="700", max_chars=200))

    # ---- pool y carriles --------------------------------------------------
    alto_pool = len(actores) * CARRIL_H
    o.append(f"<rect x='{MARGEN}' y='{y0}' width='{CAB_POOL}' height='{alto_pool}' "
             f"fill='{C_POOL}' stroke='{C_LINEA}' stroke-width='1.5'/>")
    o.append(f"<text x='{MARGEN + CAB_POOL/2}' y='{y0 + alto_pool/2}' {TIPO} "
             f"font-size='12' font-weight='700' fill='{NEGRO}' text-anchor='middle' "
             f"transform='rotate(-90 {MARGEN + CAB_POOL/2} {y0 + alto_pool/2})'>"
             f"{_esc('Proceso AltaPinta')}</text>")

    for a in actores:
        yc = y0 + carril_de[a] * CARRIL_H
        o.append(f"<rect x='{MARGEN + CAB_POOL}' y='{yc}' width='{CAB_CARRIL}' "
                 f"height='{CARRIL_H}' fill='{C_POOL}' stroke='{C_LINEA}' "
                 f"stroke-width='1.5'/>")
        o.append(f"<rect x='{x0}' y='{yc}' width='{n_cols * COL_W}' "
                 f"height='{CARRIL_H}' fill='#ffffff' stroke='{C_LINEA}' "
                 f"stroke-width='1.5'/>")
        cxx, cyy = MARGEN + CAB_POOL + CAB_CARRIL/2, yc + CARRIL_H/2
        o.append(f"<text x='{cxx}' y='{cyy}' {TIPO} font-size='11.5' "
                 f"font-weight='700' fill='{NEGRO}' text-anchor='middle' "
                 f"transform='rotate(-90 {cxx} {cyy})'>{_esc(a)}</text>")

    # ---- nodos ------------------------------------------------------------
    for p, c in zip(pasos, columnas):
        tipo, actor = p[0], p[1]
        X, Y = cx(c), cy(actor)
        if tipo == "inicio":
            o.append(_inicio(X, Y))
        elif tipo == "tarea":
            o.append(_tarea(X, Y, p[2]))
        elif tipo == "gateway":
            o.append(_gateway(X, Y, p[2]))
            # rama alternativa: sale a la derecha, dentro del mismo carril
            o.append(_fin(cx(c + 1), Y))
            o.append(_flecha([(X + GW/2, Y), (cx(c + 1) - EV_R, Y)],
                             p[3], (X + (cx(c+1) - X)/2, Y - 8)))
            o.append(_txt(cx(c + 1), Y + EV_R + 14, p[4], tam=9.5, peso="500",
                          max_chars=24))
        elif tipo == "fin":
            o.append(_fin(X, Y))

    # ---- conexiones del camino principal ----------------------------------
    def borde(p, c, lado):
        """Punto de salida/entrada de un nodo."""
        tipo, actor = p[0], p[1]
        X, Y = cx(c), cy(actor)
        if tipo in ("inicio", "fin"):
            dx = EV_R
        elif tipo == "gateway":
            dx = GW/2
        else:
            dx = TAREA_W/2
        return (X + dx, Y) if lado == "der" else (X - dx, Y), (X, Y), dx

    for i in range(len(pasos) - 1):
        a, b = pasos[i], pasos[i + 1]
        ca, cbb = columnas[i], columnas[i + 1]
        (ax, ay), (acx, acy), adx = borde(a, ca, "der")
        (bx, by), (bcx, bcy), bdx = borde(b, cbb, "izq")

        if a[0] == "gateway":
            # el camino principal sale por abajo de la compuerta
            ax, ay = acx, acy + GW/2
            # La etiqueta del camino principal es SIEMPRE la contraria a la de
            # la rama alternativa. Fijarla a "Sí" rotulaba las dos salidas
            # igual en las compuertas cuya condicion es negativa, del tipo
            # "¿el correo ya esta registrado?".
            etq_ppal = "No" if a[3].strip().lower().startswith("s") else "Sí"
            if abs(acy - bcy) < 1:
                # Mismo carril: el paso siguiente esta dos columnas a la
                # derecha, pero entre medias hay el evento de fin de la rama
                # alternativa. Se rodea por debajo y se sube ANTES de la
                # tarea, para entrar por su borde izquierdo y no por debajo
                # con la punta dentro de la caja.
                desvio = acy + GW/2 + 40
                subida = bcx - TAREA_W/2 - 26
                o.append(_flecha(
                    [(ax, ay), (ax, desvio), (subida, desvio), (subida, by), (bx, by)],
                    etq_ppal, ((ax + subida) / 2, desvio - 7)))
            else:
                subida = min(ax + 40, bcx - TAREA_W/2 - 26)
                o.append(_flecha(
                    [(ax, ay), (ax, bcy), (bx, by)],
                    etq_ppal, (ax + 20, (ay + bcy) / 2)))
        elif abs(acy - bcy) < 1:
            o.append(_flecha([(ax, ay), (bx, by)]))
        else:
            medio = (acy + bcy) / 2
            o.append(_flecha([(ax, ay), (ax + 26, ay), (ax + 26, bcy), (bx, by)]))

    svg = (f"<svg xmlns='http://www.w3.org/2000/svg' width='{ancho}' height='{alto}' "
           f"viewBox='0 0 {ancho} {alto}'>\n"
           f"<defs><marker id='pta' markerWidth='9' markerHeight='9' refX='8' "
           f"refY='4.5' orient='auto'><path d='M0,0 L9,4.5 L0,9 z' fill='{NEGRO}'/>"
           f"</marker></defs>\n"
           f"<rect width='{ancho}' height='{alto}' fill='#ffffff'/>\n"
           + "\n".join(o) + "\n</svg>\n")

    pathlib.Path(archivo).write_text(svg, encoding="utf-8")
    return ancho, alto
