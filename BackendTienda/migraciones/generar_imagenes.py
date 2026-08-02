# -*- coding: utf-8 -*-
"""
Genera las ilustraciones de producto de AltaPinta y ajusta los precios.

POR QUE ILUSTRACIONES Y NO FOTOS

Las fotos de producto de una tienda real son material con derechos, y las
de banco de imagenes habria que descargarlas de fuera. Aqui se dibujan
siluetas propias en SVG, en la misma estetica monocroma de la tienda: se
corresponden con la prenda que anuncia el nombre, pesan unos pocos KB y
no dependen de nadie.

Se genera un archivo por combinacion de prenda y deporte, no uno por
producto: asi la cuadricula tiene variedad de fondo sin llenar el disco
de 153 imagenes casi iguales.

PRECIOS

Se ajustan por tipo de prenda a rangos propios de ropa deportiva: una
camiseta no puede costar lo mismo que una chaqueta. Dentro de cada rango
el precio depende del id del producto, asi que es estable entre
ejecuciones.

USO
    python migraciones/generar_imagenes.py
    mysql -u root -p alta_pinta < migraciones/004-imagenes-y-precios.sql
"""
import io, os

AQUI = os.path.dirname(os.path.abspath(__file__))
DIR_IMAGENES = os.path.join(AQUI, "..", "productos-imagenes")
SALIDA_SQL = os.path.join(AQUI, "004-imagenes-y-precios.sql")
BASE_URL = "/imagenes"  # ruta relativa: el frontend antepone la direccion de su entorno

# ---------------------------------------------------------------
# Paleta: el fondo cambia con el deporte, la silueta siempre en negro
# ---------------------------------------------------------------
FONDOS = {
    "Running":  ("#f4f4f4", "#d4ff3f"),
    "Training": ("#ebebeb", "#0a0a0a"),
    "Fútbol":   ("#f0f0f0", "#d4ff3f"),
    "Básquet":  ("#e8e8e8", "#0a0a0a"),
    "Yoga":     ("#f6f6f6", "#d4ff3f"),
    "Outdoor":  ("#e4e4e4", "#0a0a0a"),
}

# ---------------------------------------------------------------
# Siluetas. viewBox 300x400 para encajar en la tarjeta 3:4.
# ---------------------------------------------------------------
SILUETAS = {
    # Camiseta de manga corta
    "camiseta": """
      <path d="M110 90 L75 110 L55 165 L88 182 L98 160 L98 320 L202 320 L202 160
               L212 182 L245 165 L225 110 L190 90
               C182 108 168 118 150 118 C132 118 118 108 110 90 Z"/>
    """,
    # Sudadera: camiseta con capucha y puños
    "sudadera": """
      <path d="M108 92 L70 114 L48 172 L84 190 L96 166 L96 330 L204 330 L204 166
               L216 190 L252 172 L230 114 L192 92
               C184 112 168 124 150 124 C132 124 116 112 108 92 Z"/>
      <path d="M112 92 C124 118 176 118 188 92" fill="none" stroke-width="7"/>
      <path d="M96 300 L204 300" fill="none" stroke-width="7"/>
    """,
    # Chaqueta: sudadera con cremallera central
    "chaqueta": """
      <path d="M108 92 L70 114 L48 172 L84 190 L96 166 L96 330 L204 330 L204 166
               L216 190 L252 172 L230 114 L192 92
               C184 112 168 124 150 124 C132 124 116 112 108 92 Z"/>
      <path d="M150 118 L150 330" fill="none" stroke-width="6"/>
      <circle cx="150" cy="140" r="6" fill="none" stroke-width="5"/>
    """,
    # Media cremallera
    "media": """
      <path d="M108 92 L70 114 L48 172 L84 190 L96 166 L96 330 L204 330 L204 166
               L216 190 L252 172 L230 114 L192 92
               C184 112 168 124 150 124 C132 124 116 112 108 92 Z"/>
      <path d="M150 118 L150 210" fill="none" stroke-width="6"/>
    """,
    # Short
    "short": """
      <path d="M92 120 L208 120 L214 200 L206 268 L162 268 L150 196
               L138 268 L94 268 L86 200 Z"/>
      <path d="M92 148 L208 148" fill="none" stroke-width="6"/>
    """,
    # Pantalon / legging
    "pantalon": """
      <path d="M96 100 L204 100 L210 190 L200 350 L160 350 L150 200
               L140 350 L100 350 L90 190 Z"/>
      <path d="M96 128 L204 128" fill="none" stroke-width="6"/>
    """,
    # Vestido
    "vestido": """
      <path d="M116 96 L88 116 L74 160 L102 172 L108 150 L78 330 L222 330 L192 150
               L198 172 L226 160 L212 116 L184 96
               C176 112 164 120 150 120 C136 120 124 112 116 96 Z"/>
    """,
    # Falda
    "falda": """
      <path d="M100 130 L200 130 L232 300 L68 300 Z"/>
      <path d="M100 158 L200 158" fill="none" stroke-width="6"/>
    """,
    # Conjunto de bebe
    "conjunto": """
      <path d="M112 100 L82 120 L64 168 L94 182 L102 160 L102 250
               C102 280 198 280 198 250 L198 160 L206 182 L236 168 L218 120 L188 100
               C180 116 166 124 150 124 C134 124 120 116 112 100 Z"/>
      <circle cx="132" cy="240" r="5"/>
      <circle cx="168" cy="240" r="5"/>
    """,
}

# Que silueta corresponde a cada tipo de prenda de la base de datos
SILUETA_POR_TIPO = {
    "Polo": "camiseta",
    "Polera": "sudadera",
    "Casaca": "chaqueta",
    "Chompa": "media",
    "Short": "short",
    "Pantalón": "pantalon",
    "Vestido": "vestido",
    "Falda": "falda",
    "": "conjunto",
}

# Rango de precio por tipo, en soles: (minimo, maximo)
PRECIOS = {
    "Polo":     (49.90, 89.90),
    "Short":    (59.90, 99.90),
    "Pantalón": (99.90, 159.90),
    "Falda":    (69.90, 109.90),
    "Vestido":  (89.90, 139.90),
    "Polera":   (129.90, 189.90),
    "Chompa":   (149.90, 209.90),
    "Casaca":   (179.90, 259.90),
    "":         (39.90, 69.90),
}


def slug(texto):
    """Nombre de archivo sin acentos ni espacios."""
    reemplazos = {"á": "a", "é": "e", "í": "i", "ó": "o", "ú": "u", "ñ": "n"}
    t = texto.lower()
    for k, v in reemplazos.items():
        t = t.replace(k, v)
    return "".join(c if c.isalnum() else "-" for c in t).strip("-")


def svg(silueta, fondo, acento):
    return """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 400" width="300" height="400">
  <rect width="300" height="400" fill="%s"/>
  <rect x="0" y="0" width="300" height="6" fill="%s"/>
  <g fill="#0a0a0a" stroke="#0a0a0a" stroke-width="4" stroke-linejoin="round">
%s
  </g>
</svg>
""" % (fondo, acento, silueta.strip())


def generar():
    os.makedirs(DIR_IMAGENES, exist_ok=True)
    creados = 0

    for tipo, nombre_silueta in SILUETA_POR_TIPO.items():
        for deporte, (fondo, acento) in FONDOS.items():
            archivo = "prenda-%s-%s.svg" % (nombre_silueta, slug(deporte))
            ruta = os.path.join(DIR_IMAGENES, archivo)
            with io.open(ruta, "w", encoding="utf-8") as f:
                f.write(svg(SILUETAS[nombre_silueta], fondo, acento))
            creados += 1

    sql = [
        "-- Imagenes y precios del catalogo deportivo de AltaPinta.",
        "-- Generado por migraciones/generar_imagenes.py",
        "-- Las ilustraciones son propias: siluetas SVG, sin material de terceros.",
        "",
        "START TRANSACTION;",
        "",
        "-- Imagen segun la prenda y el deporte de cada producto",
    ]

    for tipo, nombre_silueta in SILUETA_POR_TIPO.items():
        condicion_tipo = ("p.tipo_prenda_id IS NULL" if tipo == ""
                          else "t.nombre = '%s'" % tipo.replace("'", "''"))
        for deporte in FONDOS:
            archivo = "prenda-%s-%s.svg" % (nombre_silueta, slug(deporte))
            sql.append(
                "UPDATE producto p "
                "LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id "
                "JOIN deporte d ON d.id = p.deporte_id "
                "SET p.imagen_url = '%s/%s' "
                "WHERE %s AND d.nombre = '%s';"
                % (BASE_URL, archivo, condicion_tipo, deporte.replace("'", "''")))

    sql.append("")
    sql.append("-- Precio por tipo de prenda. El escalon depende del id, asi que")
    sql.append("-- el precio es el mismo cada vez que se ejecuta el script.")
    sql.append("--")
    sql.append("-- Se redondea a decena y se le resta un centimo para que acabe")
    sql.append("-- en .90, como en cualquier tienda: 89.90 y no 87.43.")

    for tipo, (minimo, maximo) in PRECIOS.items():
        condicion = ("p.tipo_prenda_id IS NULL" if tipo == ""
                     else "t.nombre = '%s'" % tipo.replace("'", "''"))
        pasos = 8
        salto = (maximo - minimo) / (pasos - 1)
        sql.append(
            "UPDATE producto p LEFT JOIN tipo_prenda t ON t.id = p.tipo_prenda_id "
            "SET p.precio = ROUND((%.2f + (p.id %% %d) * %.2f) / 10) * 10 - 0.10 "
            "WHERE %s;"
            % (minimo, pasos, salto, condicion))

    sql.append("")
    sql.append("COMMIT;")

    with io.open(SALIDA_SQL, "w", encoding="utf-8") as f:
        f.write("\n".join(sql) + "\n")

    print("Ilustraciones creadas: %d" % creados)
    print("SQL: %s" % os.path.basename(SALIDA_SQL))


if __name__ == "__main__":
    generar()
