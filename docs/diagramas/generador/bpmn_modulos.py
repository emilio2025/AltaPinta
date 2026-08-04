# -*- coding: utf-8 -*-
"""
Los 22 diagramas BPMN de los NUEVOS modulos de AltaPinta: RF033 a RF054,
agrupados en los 11 modulos que define el documento de requerimientos.

Mismo estilo que los 32 diagramas de los requisitos antiguos: un pool con
carriles por actor, al modo de Bizagi. El flujo cambia de carril cuando
cambia quien ejecuta el paso.

Los pasos describen lo que el sistema hace de verdad, comprobado contra el
codigo: PasswordValidator para RF035, el bloqueo pesimista de stock para
RF051, la transaccion aparte del pago rechazado para RF053, etc.
"""

import pathlib
import re
from generar_bpmn import generar_bpmn
from requisitos_nuevos import MODULOS, POR_ID

CLI, ADM, SIS, VIS = "Cliente", "Administrador", "Sistema", "Visitante"

PROCESOS = {

# ---------------------------------------- Módulo 1: Seguridad de Autenticación
"RF033": ([CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Pulsar «Cerrar sesión»"),
    ("tarea", SIS, "Descartar el token de la sesión"),
    ("tarea", SIS, "Limpiar los datos del cliente en el navegador"),
    ("tarea", SIS, "Llevar al usuario a la portada"), ("fin", SIS)]),

"RF034": ([VIS, SIS], [
    ("inicio", VIS), ("tarea", VIS, "Pedir restablecer la contraseña con su correo"),
    ("gateway", SIS, "¿El correo está registrado?", "No",
     "Responder igual, sin revelar si existe"),
    ("tarea", SIS, "Generar un enlace con caducidad y enviarlo"),
    ("tarea", VIS, "Abrir el enlace y escribir la nueva contraseña"),
    ("gateway", SIS, "¿El enlace sigue vigente?", "No", "Pedir una solicitud nueva"),
    ("tarea", SIS, "Guardar el hash y anular el enlace"), ("fin", SIS)]),

"RF035": ([CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Escribir la contraseña elegida"),
    ("gateway", SIS, "¿Cumple los cuatro criterios?", "No",
     "Indicar el criterio que falta"),
    ("tarea", SIS, "Cifrar la contraseña con BCrypt"),
    ("tarea", SIS, "Guardar la credencial"), ("fin", SIS)]),

# ---------------------------------------------- Módulo 2: Catálogo de Productos
"RF036": ([CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Escribir el nombre en el buscador"),
    ("tarea", SIS, "Esperar a que termine de teclear"),
    ("gateway", SIS, "¿Hay prendas que coincidan?", "No",
     "Avisar de que no hay resultados"),
    ("tarea", SIS, "Mostrar las prendas encontradas"), ("fin", SIS)]),

"RF037": ([CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Elegir el criterio de ordenación"),
    ("tarea", SIS, "Pedir el catálogo ya ordenado"),
    ("tarea", SIS, "Volver a pintar la lista"), ("fin", SIS)]),

"RF038": ([CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Abrir el catálogo"),
    ("tarea", SIS, "Pedir solo la página que se ve"),
    ("tarea", SIS, "Mostrar las prendas y cuántas páginas hay"),
    ("gateway", CLI, "¿Pasa a otra página?", "Sí", "Repetir con la nueva página"),
    ("fin", SIS)]),

# ------------------------------------------------ Módulo 3: Carrito de Compras
"RF039": ([CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Añadir una prenda al carrito"),
    ("tarea", SIS, "Guardar la línea en el carrito del cliente"),
    ("tarea", CLI, "Cerrar sesión y volver otro día"),
    ("tarea", SIS, "Recuperar el carrito guardado"),
    ("tarea", SIS, "Mostrarlo con sus líneas intactas"), ("fin", SIS)]),

# ------------------------------------------------ Módulo 4: Gestión de Pedidos
"RF040": ([CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Confirmar la compra"),
    ("tarea", SIS, "Tomar las líneas del carrito"),
    ("gateway", SIS, "¿Hay stock de todas las tallas?", "No",
     "Rechazar e indicar la talla sin stock"),
    ("tarea", SIS, "Crear el pedido con sus líneas"),
    ("tarea", SIS, "Vaciar el carrito"), ("fin", SIS)]),

"RF041": ([CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Abrir «Mis pedidos»"),
    ("tarea", SIS, "Recuperar solo los pedidos de ese cliente"),
    ("gateway", SIS, "¿Tiene pedidos?", "No", "Mostrar el historial vacío"),
    ("tarea", SIS, "Listar cada pedido con su estado"), ("fin", SIS)]),

"RF042": ([ADM, SIS, CLI], [
    ("inicio", ADM), ("tarea", ADM, "Elegir el pedido y su nuevo estado"),
    ("gateway", SIS, "¿El cambio de estado es válido?", "No",
     "Rechazar la transición"),
    ("tarea", SIS, "Guardar el estado y la fecha"),
    ("tarea", SIS, "Avisar al cliente por correo"),
    ("tarea", CLI, "Ver el pedido ya actualizado"), ("fin", CLI)]),

"RF043": ([CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Pulsar cancelar en un pedido"),
    ("gateway", SIS, "¿El pedido sigue sin enviarse?", "No",
     "Rechazar: ya salió de la tienda"),
    ("tarea", SIS, "Marcar el pedido como cancelado"),
    ("tarea", SIS, "Devolver el stock reservado"),
    ("tarea", SIS, "Reintegrar el importe cobrado"), ("fin", SIS)]),

# ------------------------------------------------- Módulo 5: Proceso de Compra
"RF044": ([CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Confirmar la compra"),
    ("tarea", SIS, "Abrir una transacción"),
    ("tarea", SIS, "Descontar el stock y cobrar la tarjeta"),
    ("gateway", SIS, "¿Terminó todo sin error?", "No",
     "Deshacer la transacción completa"),
    ("tarea", SIS, "Confirmar los cambios"), ("fin", SIS)]),

# ------------------------------------------------------------ Módulo 6: Pagos
"RF045": ([CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Pulsar pagar dos veces o recargar la página"),
    ("tarea", SIS, "Bloquear el carrito en la base de datos"),
    ("gateway", SIS, "¿Ese carrito ya se cobró?", "Sí",
     "Rechazar el segundo intento"),
    ("tarea", SIS, "Cobrar una sola vez"),
    ("tarea", SIS, "Vaciar el carrito"), ("fin", SIS)]),

"RF053": ([CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Elegir la tarjeta y pagar"),
    ("tarea", SIS, "Enviar el cobro al medio de pago"),
    ("gateway", SIS, "¿Se aprueba el cobro?", "No",
     "Dejar constancia del pago rechazado"),
    ("tarea", SIS, "Abonar el importe a la tienda"),
    ("tarea", SIS, "Registrar el pago aprobado"), ("fin", SIS)]),

# ------------------------------------------------- Módulo 7: Gestión de Envíos
"RF046": ([CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Abrir «Mis direcciones»"),
    ("tarea", CLI, "Escribir la dirección de envío"),
    ("gateway", SIS, "¿Están todos los datos?", "No",
     "Señalar el campo que falta"),
    ("tarea", SIS, "Guardar la dirección del cliente"), ("fin", SIS)]),

# ------------------------------------------------------ Módulo 8: Facturación
"RF047": ([CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Confirmar la compra"),
    ("tarea", SIS, "Tomar los datos fiscales del cliente"),
    ("gateway", SIS, "¿Están completos?", "No", "Pedir que los complete"),
    ("tarea", SIS, "Incluirlos en el comprobante"), ("fin", SIS)]),

"RF048": ([SIS], [
    ("inicio", SIS), ("tarea", SIS, "Cerrar un pedido ya pagado"),
    ("tarea", SIS, "Reservar el siguiente número de la serie"),
    ("gateway", SIS, "¿Ese número ya existe?", "Sí",
     "Reintentar con el siguiente"),
    ("tarea", SIS, "Emitir el comprobante numerado"), ("fin", SIS)]),

# --------------------------------------- Módulo 9: Notificaciones e Integración
"RF052": ([SIS, CLI], [
    ("inicio", SIS), ("tarea", SIS, "Dar la compra por terminada"),
    ("tarea", SIS, "Componer el correo con el detalle del pedido"),
    ("gateway", SIS, "¿Salió bien el envío?", "No",
     "Anotar el fallo sin tumbar la compra"),
    ("tarea", CLI, "Recibir el correo con su pedido"), ("fin", CLI)]),

# ---------------------------------------- Módulo 10: Administración y Reportes
"RF049": ([ADM, SIS], [
    ("inicio", ADM), ("tarea", ADM, "Abrir el panel y elegir el periodo"),
    ("tarea", SIS, "Sumar las ventas del día, del mes y del año"),
    ("tarea", SIS, "Mostrar los totales y las prendas más vendidas"),
    ("fin", SIS)]),

"RF054": ([ADM, SIS], [
    ("inicio", ADM), ("tarea", ADM, "Ejecutar una operación crítica"),
    ("tarea", SIS, "Anotar quién, qué y cuándo"),
    ("tarea", SIS, "Guardar el asiento de auditoría"),
    ("tarea", ADM, "Consultar el historial cuando haga falta"), ("fin", ADM)]),

# ------------------------------------------- Módulo 11: Integridad del Sistema
"RF050": ([CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Enviar un formulario"),
    ("gateway", SIS, "¿Pasa la validación del navegador?", "No",
     "Marcar el campo en rojo"),
    ("gateway", SIS, "¿Pasa la validación del servidor?", "No",
     "Responder con el error concreto"),
    ("tarea", SIS, "Ejecutar la operación"), ("fin", SIS)]),

"RF051": ([CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Dos clientes compran la última talla a la vez"),
    ("tarea", SIS, "Bloquear la fila de esa talla"),
    ("gateway", SIS, "¿Queda stock suficiente?", "No",
     "Rechazar la segunda compra"),
    ("tarea", SIS, "Descontar y soltar el bloqueo"), ("fin", SIS)]),
}


def limpio(texto):
    """Nombre de archivo sin acentos ni caracteres raros."""
    tabla = str.maketrans("áéíóúüñÁÉÍÓÚÜÑ", "aeiouunAEIOUUN")
    t = texto.translate(tabla)
    return re.sub(r"[^A-Za-z0-9]+", "-", t).strip("-")


if __name__ == "__main__":
    destino = pathlib.Path(__file__).parent / "bpmn_modulos"
    destino.mkdir(exist_ok=True)

    faltan = [r[0] for r in POR_ID.values() if r[0] not in PROCESOS]
    if faltan:
        raise SystemExit(f"sin proceso definido: {faltan}")

    n = 0
    for num_mod, nombre_mod, rfs in MODULOS:
        for rf in rfs:
            datos = POR_ID[rf]
            actores, pasos = PROCESOS[rf]
            titulo = f"{rf} — {datos[1]}   ·   {num_mod}: {nombre_mod}"
            archivo = destino / f"{rf}_{limpio(datos[1])}.svg"
            ancho, alto = generar_bpmn(titulo, actores, pasos, archivo)
            n += 1
            print(f"  {rf}  {datos[1]:<32} {ancho:>5}x{alto:<4} {archivo.name}")

    print(f"\n{n} diagramas en {destino}")
