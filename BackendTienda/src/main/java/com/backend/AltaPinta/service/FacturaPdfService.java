package com.backend.AltaPinta.service;

import java.math.BigDecimal;

import com.backend.AltaPinta.model.Pedido;
import com.backend.AltaPinta.model.PedidoDetalle;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.List;
import java.io.File;

@Service
public class FacturaPdfService {

    private static final String FACTURA_DIR = "facturas/";

    public String generarFactura(Pedido pedido, List<PedidoDetalle> detalles) {
        try {
            File dir = new File(FACTURA_DIR);
            if (!dir.exists()) dir.mkdirs();

            String nombreArchivo = FACTURA_DIR + "FAC-" + pedido.getId() + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(nombreArchivo));
            document.open();

            // Título
            document.add(new Paragraph("Factura de Compra\n\n"));

            // Datos del pedido
            document.add(new Paragraph("Pedido ID: " + pedido.getId()));
            document.add(new Paragraph("Cliente: " + pedido.getCliente().getNombre()));
            document.add(new Paragraph("Fecha: " + pedido.getFecha()));

            // RF047: Datos fiscales opcionales (factura a nombre de empresa)
            String ruc = pedido.getCliente().getRuc();
            String razonSocial = pedido.getCliente().getRazonSocial();
            if (ruc != null && !ruc.isBlank()) {
                document.add(new Paragraph("RUC: " + ruc));
            }
            if (razonSocial != null && !razonSocial.isBlank()) {
                document.add(new Paragraph("Razón social: " + razonSocial));
            }
            document.add(new Paragraph("\n"));

            // Tabla de detalles
            PdfPTable table = new PdfPTable(4);
            table.addCell("Producto");
            table.addCell("Cantidad");
            table.addCell("Precio Unitario");
            table.addCell("Subtotal");

            for (PedidoDetalle d : detalles) {
                table.addCell(d.getProducto().getNombre());
                table.addCell(String.valueOf(d.getCantidad()));
                table.addCell(String.format("S/ %.2f", d.getPrecioUnitario()));
                table.addCell(String.format("S/ %.2f", d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad()))));
            }

            document.add(table);

            // Totales
            document.add(new Paragraph("\nSubtotal: S/ " + pedido.getTotal()));
            if (pedido.getEnvio() != null) {
                document.add(new Paragraph("Costo de envío: S/ " + pedido.getEnvio().getCosto()));
            }
            document.add(new Paragraph("Total: S/ " + pedido.getTotal()));

            document.close();
            return nombreArchivo;

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de factura", e);
        }
    }
}
