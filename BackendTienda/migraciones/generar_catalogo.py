# -*- coding: utf-8 -*-
"""
Genera el SQL que convierte el catalogo generico de AltaPinta en uno de
ropa deportiva.

Todos los nombres y descripciones son originales, escritos para esta
tienda. No se copia el catalogo de ninguna marca: se usa el vocabulario
habitual del sector (tejido, corte, uso) para que el resultado resulte
creible.

Cada producto recibe ademas:
  - un deporte coherente con la prenda y la categoria
  - un surtido de tallas con stock, en vez de la unica que tenia
"""
import io, os, random

ENTRADA = os.path.join(os.path.dirname(__file__), "catalogo-actual.tsv")
# El TSV de entrada se genera con:
#   mysql -u root -p -D alta_pinta --batch --raw -e "SELECT p.id, COALESCE(c.nombre,'') AS categoria, COALESCE(t.nombre,'') AS tipo, p.precio FROM producto p LEFT JOIN categoria c ON c.id=p.categoria_id LEFT JOIN tipo_prenda t ON t.id=p.tipo_prenda_id ORDER BY p.id" > migraciones/catalogo-actual.tsv
SALIDA = os.path.join(os.path.dirname(__file__), "catalogo-deportivo.sql")
SALIDA_BUSQUEDAS = os.path.join(os.path.dirname(__file__), "busqueda-imagenes.tsv")

# Semilla fija: el mismo catalogo cada vez que se ejecute
random.seed(20260802)

# ---------------------------------------------------------------
# Vocabulario propio
# ---------------------------------------------------------------

# Los nombres son DESCRIPTIVOS a proposito, no de fantasia.
#
# Antes eran "Camiseta Aero" o "Legging Vertex": suenan a marca, pero si
# los pegas en un buscador de imagenes no encuentras nada. Ahora cada
# producto se llama por lo que es, de modo que su propio nombre sirve como
# termino de busqueda para dar con una foto real.

# Los rasgos van declarados prenda por prenda, no por zona del cuerpo.
#
# Aplicarlos en bloque producia disparates: "Casaca deportiva sin
# mangas", "Cortavientos de cuello redondo", "Camiseta de manga corta de
# manga larga" y "Falda deportiva ajustado" (sin concordancia). Cada
# prenda lleva ahora solo los rasgos que le caben, ya concordados.

# Colores habituales en ropa deportiva. Ayudan a afinar la busqueda de
# imagenes y hacen el catalogo mas creible.
COLORES_M = ["negro", "gris", "azul marino", "verde militar", "blanco", "granate"]
COLORES_F = ["negra", "gris", "azul marino", "verde militar", "blanca", "granate"]

TEJIDOS = [
    "tejido técnico de secado rápido",
    "punto elástico de compresión ligera",
    "malla transpirable en zonas de calor",
    "algodón peinado con toque seco",
    "tejido reciclado con acabado suave",
    "poliéster ligero con tratamiento antiolor",
]

# El corte y el remate dependen de si la prenda va arriba o abajo:
# un legging no tiene hombros y una camiseta no tiene cintura.
CORTES = {
    "superior": [
        "corte ajustado que sigue el movimiento",
        "corte holgado para entrenar sin agobios",
        "corte regular, ni pegado ni suelto",
        "hombros libres para no limitar el gesto",
        "sisas amplias que dejan respirar",
    ],
    "inferior": [
        "cintura alta que se mantiene en su sitio",
        "corte ajustado que no estorba en la zancada",
        "corte holgado con caída natural",
        "cintura elástica que no aprieta",
        "largo por encima de la rodilla",
    ],
}

REMATES = {
    "superior": [
        "Costuras planas para evitar rozaduras.",
        "Etiqueta impresa, sin costura en el cuello.",
        "Bajo reforzado que aguanta lavados.",
        "Detalle reflectante para sesiones de tarde.",
    ],
    "inferior": [
        "Cintura elástica con cordón interior.",
        "Bolsillos laterales con cierre.",
        "Costuras planas para evitar rozaduras.",
        "Bolsillo trasero para el móvil.",
    ],
}

# Deporte por tipo de prenda: se elige entre los coherentes
DEPORTE_POR_TIPO = {
    "Short":    ["Running", "Fútbol", "Básquet", "Training"],
    "Polo":     ["Fútbol", "Training", "Outdoor"],
    "Polera":   ["Training", "Running", "Outdoor"],
    "Pantalón": ["Training", "Yoga", "Outdoor"],
    "Casaca":   ["Outdoor", "Running"],
    "Chompa":   ["Outdoor", "Training"],
    "Vestido":  ["Yoga", "Training"],
    "Falda":    ["Yoga", "Training"],
    "":         ["Training"],          # los de Bebé no tienen tipo
}

# Cada prenda con su genero gramatical y la zona del cuerpo que viste.
# El genero decide "Confeccionado" o "Confeccionada"; la zona decide que
# cortes y remates tienen sentido.
PRENDA = {
    "Short":    [("Short", "m", "inferior", ["", "de tiro medio", "con malla interior"]),
                 ("Short de entrenamiento", "m", "inferior", ["", "de secado rápido"]),
                 ("Short deportivo", "m", "inferior", ["", "con bolsillos"])],
    "Polo":     [("Camiseta técnica", "f", "superior", ["", "de cuello redondo", "sin mangas"]),
                 ("Camiseta de manga corta", "f", "superior", ["", "de cuello redondo"]),
                 ("Camiseta", "f", "superior", ["de manga corta", "de manga larga", "sin mangas"])],
    "Polera":   [("Sudadera", "f", "superior", ["", "con capucha", "con cremallera"]),
                 ("Polerón técnico", "m", "superior", ["", "con capucha"]),
                 ("Sudadera con capucha", "f", "superior", ["", "de algodón"])],
    "Pantalón": [("Pantalón jogger", "m", "inferior", ["", "con puños", "de tiro alto"]),
                 ("Pantalón de entrenamiento", "m", "inferior", ["", "holgado"]),
                 ("Legging", "m", "inferior", ["de tiro alto", "de compresión", "largo"])],
    "Casaca":   [("Cortavientos", "m", "superior", ["", "impermeable", "con capucha"]),
                 ("Chaqueta técnica", "f", "superior", ["", "impermeable", "con capucha"]),
                 ("Casaca deportiva", "f", "superior", ["", "con cremallera"])],
    "Chompa":   [("Chaqueta de media cremallera", "f", "superior", ["", "térmica"]),
                 ("Sudadera térmica", "f", "superior", ["", "de cuello alto"])],
    "Vestido":  [("Vestido deportivo", "m", "superior", ["", "con short interior"]),
                 ("Vestido de entrenamiento", "m", "superior", ["", "sin mangas"])],
    "Falda":    [("Falda-short", "f", "inferior", ["", "con malla interior"]),
                 ("Falda deportiva", "f", "inferior", ["", "con short interior", "plisada"])],
    "":         [("Conjunto deportivo", "m", "superior", ["", "de dos piezas"]),
                 ("Body deportivo", "m", "superior", ["", "de manga corta"])],
}

USO_POR_DEPORTE = {
    "Running":  ["para salir a correr", "para tus kilómetros diarios", "para rodajes largos"],
    "Fútbol":   ["para el partido y el entrenamiento", "para pisar el campo"],
    "Básquet":  ["para la pista", "para jugar sin límites"],
    "Training": ["para el gimnasio", "para tus sesiones de fuerza", "para entrenar a diario"],
    "Yoga":     ["para yoga y movilidad", "para estirar y respirar", "para pilates y yoga"],
    "Outdoor":  ["para salir al aire libre", "para la montaña y el día a día"],
}

# Tallas por categoria: mas surtido que la unica que tenian
TALLAS_POR_CATEGORIA = {
    "Mujer": ["XS", "S", "M", "L", "XL", "XXL"],
    "Varon": ["S", "M", "L", "XL", "XXL", "BIG SIZE"],
    "Niños": ["XS", "S", "M", "L", "XL"],
    "Bebé":  ["PEQUEÑO", "ESTÁNDAR", "XS", "S"],
    "":      ["S", "M", "L"],
}

PUBLICO = {"Mujer": "de mujer", "Varon": "de hombre", "Niños": "de niño", "Bebé": "de bebé", "": ""}


def escapar(texto):
    return texto.replace("\\", "\\\\").replace("'", "''")


def generar():
    # utf-8-sig: PowerShell escribe el TSV con BOM y si no se descarta,
    # la primera columna se llamaria "﻿id" en vez de "id".
    with io.open(ENTRADA, encoding="utf-8-sig") as f:
        lineas = [l.rstrip("\n").rstrip("\r") for l in f if l.strip()]

    cabecera = lineas[0].split("\t")
    filas = [dict(zip(cabecera, l.split("\t"))) for l in lineas[1:]]

    sql = []
    sql.append("-- Catalogo deportivo de AltaPinta.")
    sql.append("-- Generado por scratchpad/generar_catalogo.py")
    sql.append("-- Nombres y descripciones originales: no se copia ninguna marca.")
    sql.append("")
    sql.append("START TRANSACTION;")
    sql.append("")
    sql.append("-- Se rehace el surtido de tallas de cero")
    sql.append("DELETE FROM producto_talla;")
    sql.append("")

    usados = set()
    resumen = {}
    busquedas = []

    for fila in filas:
        pid = fila["id"]
        categoria = fila["categoria"]
        tipo = fila["tipo"]

        deporte = random.choice(DEPORTE_POR_TIPO.get(tipo, ["Training"]))
        prenda, genero, zona, rasgos = random.choice(
            PRENDA.get(tipo, [("Prenda deportiva", "f", "superior", [""])]))

        # Nombre buscable: prenda + rasgo + deporte + color.
        # Ej. "Camiseta técnica de manga corta running negra", que pegado
        # en un buscador de imagenes devuelve fotos utiles.
        colores = COLORES_M if genero == "m" else COLORES_F
        publico_corto = PUBLICO.get(categoria, "").replace("de ", "")

        def montar(rasgo, color, con_publico=False):
            partes = [prenda, rasgo, deporte.lower(), color]
            if con_publico:
                partes.append(publico_corto)
            return " ".join(p for p in partes if p)

        for _ in range(60):
            rasgo = random.choice(rasgos)
            color = random.choice(colores)
            nombre = montar(rasgo, color)
            if nombre not in usados:
                break
        else:
            # Si todas las combinaciones estan cogidas, el publico desempata
            nombre = montar(rasgo, color, con_publico=True)
        usados.add(nombre)

        # "Camiseta de mujer para el gimnasio." — el publico va pegado a la
        # prenda, no al final, que era donde quedaba raro.
        publico = PUBLICO.get(categoria, "")
        sujeto = ("%s %s" % (prenda, publico)).strip()
        participio = "Confeccionado" if genero == "m" else "Confeccionada"

        descripcion = "%s %s. %s en %s, con %s. %s" % (
            sujeto,
            random.choice(USO_POR_DEPORTE[deporte]),
            participio,
            random.choice(TEJIDOS),
            random.choice(CORTES[zona]),
            random.choice(REMATES[zona]),
        )

        sql.append("UPDATE producto SET nombre='%s', descripcion='%s', "
                   "deporte_id=(SELECT id FROM deporte WHERE nombre='%s') WHERE id=%s;"
                   % (escapar(nombre), escapar(descripcion), escapar(deporte), pid))

        # Surtido de tallas con stock
        tallas = TALLAS_POR_CATEGORIA.get(categoria, ["S", "M", "L"])
        for talla in tallas:
            stock = random.randint(4, 40)
            sql.append("INSERT INTO producto_talla (producto_id, talla_id, stock, version) "
                       "SELECT %s, id, %d, 0 FROM talla WHERE nombre='%s';"
                       % (pid, stock, escapar(talla)))

        # Termino de busqueda: sin el rasgo, que estorba al buscar fotos
        publico_corto = PUBLICO.get(categoria, '').replace('de ', '')
        termino = ' '.join(x for x in [prenda.lower(), deporte.lower(),
                                       color, publico_corto] if x)
        busquedas.append((pid, nombre, termino))

        resumen[deporte] = resumen.get(deporte, 0) + 1

    sql.append("")
    sql.append("COMMIT;")

    with io.open(SALIDA, "w", encoding="utf-8") as f:
        f.write("\n".join(sql) + "\n")

    # Listado para buscar fotos: id, nombre y el termino que conviene
    # pegar en un buscador de imagenes. Se ordena por termino para que
    # los productos que comparten busqueda queden juntos y una misma foto
    # sirva para varios.
    with io.open(SALIDA_BUSQUEDAS, "w", encoding="utf-8") as f:
        f.write("id\tnombre\tbuscar\n")
        for pid, nombre, termino in sorted(busquedas, key=lambda x: x[2]):
            f.write("%s\t%s\t%s\n" % (pid, nombre, termino))

    print("Productos procesados: %d" % len(filas))
    print("Sentencias generadas: %d" % len(sql))
    print("Reparto de deportes:")
    for d, n in sorted(resumen.items(), key=lambda x: -x[1]):
        print("   %-10s %d" % (d, n))


if __name__ == "__main__":
    generar()
