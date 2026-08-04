# -*- coding: utf-8 -*-
"""
RF033 a RF054 y los 11 modulos, transcritos del documento
"Nuevos requerimientos Alta Pinta.docx" del Drive del equipo.

Fuente unica de verdad para los diagramas y la documentacion. Los codigos
RS060-RS069 que aparecieron antes en este proyecto NO existen en el pliego:
fueron un invento y deben descartarse.
"""

# (id, nombre, dependencia, area, stakeholder, prioridad, seguridad, descripcion)
REQUISITOS = [
    ("RF033", "Cierre de sesión", "RF030", "Seguridad", "Cliente", "Alta", "Alta",
     "El sistema debe permitir al usuario cerrar su sesión de forma segura."),
    ("RF034", "Recuperación de contraseña", "RF029", "Seguridad", "Cliente", "Alta", "Alta",
     "El sistema debe permitir recuperar la contraseña mediante correo electrónico."),
    ("RF035", "Validación de contraseña", "RF029", "Seguridad", "Sistema", "Alta", "Alta",
     "El sistema debe validar que las contraseñas cumplan criterios de seguridad."),
    ("RF036", "Búsqueda de productos", "RF004", "Catálogo", "Cliente", "Media", "Baja",
     "El sistema debe permitir buscar productos por nombre."),
    ("RF037", "Ordenamiento de productos", "RF004", "Catálogo", "Cliente", "Media", "Baja",
     "El sistema debe permitir ordenar productos por precio o relevancia."),
    ("RF038", "Paginación", "RF004", "Catálogo", "Cliente", "Media", "Baja",
     "El sistema debe mostrar productos en páginas para mejorar rendimiento."),
    ("RF039", "Persistencia del carrito", "RF010", "Carrito", "Cliente", "Alta", "Media",
     "El carrito debe mantenerse entre sesiones del usuario."),
    ("RF040", "Creación de pedido", "RF013", "Pedido", "Sistema", "Alta", "Alta",
     "El sistema debe generar un pedido a partir del carrito."),
    ("RF041", "Historial de pedidos", "RF008", "Pedido", "Cliente", "Alta", "Media",
     "El sistema debe almacenar y mostrar pedidos realizados."),
    ("RF042", "Estados de pedido", "RF008", "Pedido", "Sistema", "Alta", "Media",
     "El pedido debe tener estados como pendiente, pagado, enviado, etc."),
    ("RF043", "Cancelación de pedidos", "RF010", "Pedido", "Cliente", "Alta", "Media",
     "El sistema debe permitir cancelar pedidos antes del envío."),
    ("RF044", "Control de errores (rollback)", "RF016", "Compra", "Sistema", "Alta", "Alta",
     "El sistema debe revertir operaciones en caso de error en la compra."),
    ("RF045", "Evitar duplicación de pagos", "RF020", "Pagos", "Sistema", "Alta", "Alta",
     "El sistema debe evitar transacciones duplicadas."),
    ("RF046", "Registro de direcciones", "RF021", "Envíos", "Cliente", "Alta", "Media",
     "El sistema debe permitir registrar direcciones de envío."),
    ("RF047", "Datos fiscales", "RF027", "Facturación", "Cliente", "Media", "Alta",
     "El comprobante debe incluir datos fiscales del cliente."),
    ("RF048", "Numeración de comprobantes", "RF027", "Facturación", "Sistema", "Alta", "Media",
     "El sistema debe generar numeración única para comprobantes."),
    ("RF049", "Reportes", "RF032", "Administración", "Administrador", "Media", "Media",
     "El sistema debe generar reportes de ventas y productos."),
    ("RF050", "Validación de datos", "General", "Sistema", "Sistema", "Alta", "Alta",
     "El sistema debe validar datos en todas las operaciones."),
    ("RF051", "Consistencia de stock", "RF015", "Inventario", "Sistema", "Alta", "Alta",
     "El sistema debe evitar inconsistencias en el stock."),
    ("RF052", "Integración correo", "RF025", "Integración", "Sistema", "Media", "Media",
     "El sistema debe integrarse con servicios de correo."),
    ("RF053", "Integración pagos", "RF017", "Integración", "Sistema", "Alta", "Alta",
     "El sistema debe integrarse con servicios de pago."),
    ("RF054", "Auditoría", "General", "Sistema", "Administrador", "Media", "Alta",
     "El sistema debe registrar operaciones críticas."),
]

MODULOS = [
    ("Módulo 1", "Seguridad de Autenticación", ["RF033", "RF034", "RF035"]),
    ("Módulo 2", "Catálogo de Productos",       ["RF036", "RF037", "RF038"]),
    ("Módulo 3", "Carrito de Compras",          ["RF039"]),
    ("Módulo 4", "Gestión de Pedidos",          ["RF040", "RF041", "RF042", "RF043"]),
    ("Módulo 5", "Proceso de Compra",           ["RF044"]),
    ("Módulo 6", "Pagos",                       ["RF045", "RF053"]),
    ("Módulo 7", "Gestión de Envíos",           ["RF046"]),
    ("Módulo 8", "Facturación",                 ["RF047", "RF048"]),
    ("Módulo 9", "Notificaciones e Integración",["RF052"]),
    ("Módulo 10", "Administración y Reportes",  ["RF049", "RF054"]),
    ("Módulo 11", "Integridad del Sistema",     ["RF050", "RF051"]),
]

POR_ID = {r[0]: r for r in REQUISITOS}

if __name__ == "__main__":
    asignados = [rf for _, _, rfs in MODULOS for rf in rfs]
    print(f"requisitos: {len(REQUISITOS)}  (RF033-RF054)")
    print(f"modulos:    {len(MODULOS)}")
    print(f"asignados:  {len(asignados)}  sin duplicados: {len(set(asignados)) == len(asignados)}")
    faltan = set(POR_ID) - set(asignados)
    print(f"sin modulo: {sorted(faltan) if faltan else 'ninguno'}")
