"""
Los 32 diagramas BPMN de AltaPinta, uno por requisito funcional.

Los carriles son los actores reales del sistema: Visitante, Cliente,
Administrador y Sistema. El flujo baja de carril cuando cambia quién
ejecuta, que es la información que aporta un diagrama con carriles frente a
un diagrama de flujo corriente.

Los requisitos no implementados van marcados en el título: RF018 (opcional
en el pliego) y RF023 (parcial). El diagrama refleja el diseño previsto, no
lo que el código hace hoy.
"""

import pathlib
import re
from generar_bpmn import generar_bpmn

CLI, ADM, SIS, VIS = "Cliente", "Administrador", "Sistema", "Visitante"

RF = [
# ------------------------------------------- 1. Gestión de productos
("RF001", "Registrar nuevos productos de ropa", [ADM, SIS], [
    ("inicio", ADM), ("tarea", ADM, "Abrir el mantenimiento de productos"),
    ("tarea", ADM, "Completar los datos de la prenda"),
    ("gateway", SIS, "¿Supera la validación?", "No", "Señalar el campo inválido"),
    ("tarea", SIS, "Guardar el producto"),
    ("tarea", SIS, "Registrar en auditoría"), ("fin", SIS)]),

("RF002", "Editar productos existentes", [ADM, SIS], [
    ("inicio", ADM), ("tarea", ADM, "Elegir un producto de la tabla"),
    ("tarea", SIS, "Rellenar el formulario con sus datos"),
    ("tarea", ADM, "Modificar los campos necesarios"),
    ("gateway", SIS, "¿Supera la validación?", "No", "Señalar el campo inválido"),
    ("tarea", SIS, "Actualizar y registrar en auditoría"), ("fin", SIS)]),

("RF003", "Eliminar productos del catálogo", [ADM, SIS], [
    ("inicio", ADM), ("tarea", ADM, "Pulsar eliminar en una fila"),
    ("gateway", ADM, "¿Confirma el borrado?", "No", "Cancelar la operación"),
    ("tarea", SIS, "Eliminar el producto"),
    ("tarea", SIS, "Recargar tabla y auditar"), ("fin", SIS)]),

("RF004", "Listar todos los productos registrados", [ADM, SIS], [
    ("inicio", ADM), ("tarea", ADM, "Abrir la tabla de productos"),
    ("tarea", SIS, "Pedir solo la página visible"),
    ("tarea", SIS, "Mostrar precio, clasificación y stock"),
    ("gateway", ADM, "¿Cambia de página?", "No", "Permanecer en la actual"),
    ("tarea", SIS, "Pedir la nueva página"), ("fin", SIS)]),

("RF005", "Completar los datos del producto", [ADM, SIS], [
    ("inicio", ADM), ("tarea", ADM, "Nombre, descripción y precio"),
    ("tarea", ADM, "Categoría, tipo de prenda y deporte"),
    ("tarea", ADM, "Marcar tallas y su stock"),
    ("gateway", SIS, "¿Falta algún dato obligatorio?", "Sí", "Rechazar señalando el campo"),
    ("tarea", SIS, "Guardar el producto completo"), ("fin", SIS)]),

("RF006", "Gestionar categorías y tipos de prenda", [ADM, SIS], [
    ("inicio", ADM), ("tarea", ADM, "Abrir el panel de mantenimiento"),
    ("gateway", SIS, "¿Tiene rol administrador?", "No", "Responder 403 y denegar"),
    ("tarea", ADM, "Crear o editar la clasificación"),
    ("tarea", SIS, "Guardar el cambio"), ("fin", SIS)]),

# ------------------------------------------- 2. Clasificación y filtros
("RF007", "Organizar los productos por secciones", [CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Elegir mujer, varón, niños o bebé"),
    ("tarea", SIS, "Preseleccionar esa categoría"),
    ("tarea", SIS, "Consultar el catálogo filtrado"),
    ("tarea", CLI, "Ver la primera página de resultados"), ("fin", CLI)]),

("RF008", "Filtrar los productos por tipo de prenda", [CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Pulsar un tipo de prenda"),
    ("tarea", SIS, "Sumar el filtro a los ya activos"),
    ("tarea", SIS, "Volver a la primera página y consultar"),
    ("gateway", SIS, "¿Hay resultados?", "No", "Avisar de que no hay coincidencias"),
    ("tarea", CLI, "Ver la cuadrícula filtrada"), ("fin", CLI)]),

("RF009", "Filtrar los productos por talla", [CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Elegir una talla del desplegable"),
    ("tarea", SIS, "Buscar productos con esa talla en stock"),
    ("gateway", SIS, "¿Hay resultados?", "No", "Avisar de que no hay coincidencias"),
    ("tarea", CLI, "Ver la cuadrícula filtrada"), ("fin", CLI)]),

# ------------------------------------------- 3. Carrito
("RF010", "Agregar productos al carrito", [CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Abrir la ficha del producto"),
    ("tarea", CLI, "Seleccionar una talla"),
    ("gateway", SIS, "¿Queda stock de esa talla?", "No", "Impedir la selección"),
    ("tarea", SIS, "Añadir la línea al carrito"),
    ("tarea", SIS, "Actualizar el contador"), ("fin", SIS)]),

("RF011", "Modificar cantidades en el carrito", [CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Ajustar la cantidad de una línea"),
    ("gateway", SIS, "¿La cantidad es menor que 1?", "Sí", "Eliminar la línea"),
    ("tarea", SIS, "Actualizar la cantidad"),
    ("tarea", SIS, "Recalcular el total"), ("fin", SIS)]),

("RF012", "Eliminar productos del carrito", [CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Pulsar eliminar en una línea"),
    ("tarea", SIS, "Borrar la línea del carrito"),
    ("tarea", SIS, "Recargar y recalcular el total"), ("fin", SIS)]),

("RF013", "Visualizar el carrito antes de comprar", [CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Abrir el carrito"),
    ("tarea", SIS, "Recuperar producto, talla y cantidad"),
    ("gateway", SIS, "¿El carrito está vacío?", "Sí", "Invitar a volver al catálogo"),
    ("tarea", CLI, "Ver las líneas y el total"), ("fin", CLI)]),

# ------------------------------------------- 4. Proceso de compra
("RF014", "Revisar el resumen completo de la compra", [CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Acceder al proceso de pago"),
    ("tarea", SIS, "Calcular el subtotal de las líneas"),
    ("tarea", SIS, "Sumar el envío si eligió domicilio"),
    ("tarea", CLI, "Ver el total y sus tarjetas"), ("fin", CLI)]),

("RF015", "Validar la disponibilidad del stock", [CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Confirmar la compra"),
    ("tarea", SIS, "Recorrer cada línea del carrito"),
    ("gateway", SIS, "¿Hay stock de esa talla?", "No", "Detener e informar del producto"),
    ("tarea", SIS, "Continuar con el cobro"), ("fin", SIS)]),

("RF016", "Confirmar la transacción tras el pago", [CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "El cobro se realiza con éxito"),
    ("tarea", SIS, "Crear el pedido en estado PAGADO"),
    ("tarea", SIS, "Descontar el stock por talla"),
    ("tarea", SIS, "Vaciar el carrito e ingresar el cobro"), ("fin", SIS)]),

# ------------------------------------------- 5. Métodos de pago
("RF017", "Pagar con tarjeta simulada", [CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Elegir una tarjeta registrada"),
    ("gateway", SIS, "¿La tarjeta es del cliente?", "No", "Rechazar por tarjeta ajena"),
    ("tarea", SIS, "Descontar el importe del saldo"), ("fin", SIS)]),

("RF018", "Pagar con Yape u otro medio digital   [NO IMPLEMENTADO]", [CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Elegir un medio de pago digital"),
    ("tarea", SIS, "Generar el cobro contra la pasarela"),
    ("gateway", SIS, "¿La pasarela confirma?", "No", "Registrar el pago rechazado"),
    ("tarea", SIS, "Continuar con el pedido"), ("fin", SIS)]),

("RF019", "Validar que la tarjeta tenga fondos", [CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Confirmar la compra"),
    ("tarea", SIS, "Calcular el total con el envío"),
    ("gateway", SIS, "¿El saldo cubre el total?", "No", "Rechazar por saldo insuficiente"),
    ("tarea", SIS, "Autorizar el cobro"), ("fin", SIS)]),

("RF020", "Registrar el resultado del pago", [CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Intentar el cobro"),
    ("gateway", SIS, "¿El cobro prospera?", "No", "Registrar el pago RECHAZADO"),
    ("tarea", SIS, "Registrar el pago APROBADO"),
    ("tarea", SIS, "Guardar motivo, importe y fecha"), ("fin", SIS)]),

# ------------------------------------------- 6. Envíos
("RF021", "Elegir entre envío y recojo en tienda", [CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Abrir el carrito"),
    ("gateway", CLI, "¿Elige envío a domicilio?", "No", "Recojo en tienda sin costo"),
    ("tarea", CLI, "Seleccionar el destino"),
    ("tarea", SIS, "Guardar la modalidad elegida"), ("fin", SIS)]),

("RF022", "Calcular si la compra incluye costo de envío", [CLI, SIS], [
    ("inicio", CLI), ("tarea", SIS, "Calcular el subtotal de las líneas"),
    ("gateway", SIS, "¿La modalidad es envío?", "No", "Total igual al subtotal"),
    ("tarea", SIS, "Sumar el costo del destino"),
    ("tarea", CLI, "Ver el desglose"), ("fin", CLI)]),

("RF023", "Costo de envío según la dirección   [PARCIAL]", [CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Elegir un destino de la lista"),
    ("tarea", SIS, "Aplicar el costo asociado al destino"),
    ("gateway", SIS, "¿Coincide con su dirección guardada?", "No",
     "Hoy lo elige el cliente a mano"),
    ("tarea", SIS, "Aplicar el costo correcto"), ("fin", SIS)]),

("RF024", "Estimar el tiempo de entrega según la ubicación", [CLI, SIS], [
    ("inicio", CLI), ("tarea", SIS, "Leer el destino del pedido"),
    ("gateway", SIS, "¿El destino es Lima?", "No", "Otras zonas: 3 a 5 días"),
    ("tarea", SIS, "Lima: 24 a 48 horas"),
    ("tarea", CLI, "Ver el plazo en la respuesta y el correo"), ("fin", CLI)]),

# ------------------------------------------- 7. Notificaciones
("RF025", "Enviar correo al finalizar la compra", [CLI, SIS], [
    ("inicio", CLI), ("tarea", SIS, "Componer productos, monto y plazo"),
    ("gateway", SIS, "¿El envío del correo falla?", "Sí", "Registrar el fallo y continuar"),
    ("tarea", CLI, "Recibir el correo de confirmación"), ("fin", CLI)]),

("RF026", "Notificar el estado del pedido al actualizarse", [ADM, SIS, CLI], [
    ("inicio", ADM), ("tarea", ADM, "Cambiar el estado del pedido"),
    ("tarea", SIS, "Guardar el nuevo estado"),
    ("gateway", SIS, "¿Fue una cancelación?", "Sí", "Restaurar stock y saldo"),
    ("tarea", SIS, "Enviar el correo del estado actual"),
    ("tarea", CLI, "Recibir la notificación"), ("fin", CLI)]),

# ------------------------------------------- 8. Facturación
("RF027", "Generar la factura o comprobante digital", [CLI, SIS], [
    ("inicio", CLI), ("tarea", SIS, "Calcular subtotal, envío y total"),
    ("tarea", SIS, "Generar el PDF con el desglose"),
    ("gateway", SIS, "¿La generación falla?", "Sí", "Registrar el fallo y continuar"),
    ("tarea", SIS, "Guardar la factura del pedido"), ("fin", SIS)]),

("RF028", "Descargar el comprobante", [CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Abrir su listado de pedidos"),
    ("tarea", CLI, "Solicitar la factura de un pedido"),
    ("gateway", SIS, "¿Existe el archivo PDF?", "No", "Informar de que no está disponible"),
    ("tarea", CLI, "Descargar el comprobante"), ("fin", CLI)]),

# ------------------------------------------- 9. Usuarios
("RF029", "Registrarse en la tienda", [VIS, SIS], [
    ("inicio", VIS), ("tarea", VIS, "Completar el formulario de registro"),
    ("tarea", SIS, "Validar correo, DNI y contraseña"),
    ("gateway", SIS, "¿Correo o DNI ya registrados?", "Sí", "Rechazar el alta"),
    ("tarea", SIS, "Crear la cuenta y enviar el código"),
    ("tarea", VIS, "Introducir el código recibido"),
    ("tarea", SIS, "Activar la cuenta"), ("fin", SIS)]),

("RF030", "Iniciar sesión", [CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Introducir correo y contraseña"),
    ("tarea", SIS, "Comparar con el hash almacenado"),
    ("gateway", SIS, "¿Las credenciales coinciden?", "No", "Responder credenciales inválidas"),
    ("tarea", SIS, "Emitir el token JWT"),
    ("tarea", CLI, "Usar el token en cada petición"), ("fin", CLI)]),

("RF031", "Diferenciar los roles cliente y administrador", [CLI, SIS], [
    ("inicio", CLI), ("tarea", CLI, "Enviar la petición con su token"),
    ("tarea", SIS, "Releer el rol desde la base de datos"),
    ("gateway", SIS, "¿El rol permite la operación?", "No", "Responder 403"),
    ("tarea", SIS, "Ejecutar la petición"), ("fin", SIS)]),

("RF032", "Acceso exclusivo del administrador", [ADM, SIS], [
    ("inicio", ADM), ("tarea", ADM, "Entrar al panel de administración"),
    ("gateway", SIS, "¿Su rol es ADMIN?", "No", "Devolver al inicio"),
    ("tarea", ADM, "Catálogo, reportes y auditoría"),
    ("tarea", SIS, "Comprobar el rol en cada llamada"), ("fin", SIS)]),
]


def _slug(codigo, nombre):
    limpio = nombre.split("[")[0].strip().lower()
    limpio = (limpio.replace("á", "a").replace("é", "e").replace("í", "i")
                    .replace("ó", "o").replace("ú", "u").replace("ñ", "n"))
    limpio = re.sub(r"[^a-z0-9]+", "-", limpio).strip("-")[:44]
    return f"{codigo}-{limpio}"


if __name__ == "__main__":
    salida = pathlib.Path(__file__).parent / "bpmn"
    salida.mkdir(exist_ok=True)
    for viejo in salida.glob("*.svg"):
        viejo.unlink()

    for codigo, nombre, actores, pasos in RF:
        archivo = salida / f"{_slug(codigo, nombre)}.svg"
        w, h = generar_bpmn(f"{codigo} — {nombre}", actores, pasos, archivo)
        print(f"  {archivo.name[:50]:50s} {w:5d}x{h}")

    print(f"\n{len(RF)} diagramas BPMN en {salida}")
