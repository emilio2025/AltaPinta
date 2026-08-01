package com.backend.AltaPinta.service;

import com.backend.AltaPinta.model.Pedido;
import com.backend.AltaPinta.model.PedidoDetalle;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sistemaEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Enviar código de verificación
    public void sendVerificationCode(String to, String codigo) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(sistemaEmail);
        msg.setTo(to);
        msg.setSubject("Código de verificación - Tienda");
        msg.setText(
                "Tu código de verificación es: " + codigo +
                        "\n\nEste código expira en 15 minutos."
        );
        mailSender.send(msg);
    }

    // Enviar código de recuperación
    public void sendPasswordResetCode(String to, String codigo) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(sistemaEmail);
        msg.setTo(to);
        msg.setSubject("Recuperación de contraseña");
        msg.setText(
                "Tu código de recuperación es: " + codigo +
                        "\n\nEste código expira en 10 minutos."
        );
        mailSender.send(msg);
    }

    // Enviar detalle de compra con PDF adjunto
    public void sendCompraDetalle(String to, Pedido pedido, List<PedidoDetalle> detalles) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true = multipart

            helper.setFrom(sistemaEmail);
            helper.setTo(to);
            helper.setSubject("Confirmación de compra - Alta Pinta");

            StringBuilder cuerpo = new StringBuilder();
            cuerpo.append("¡Gracias por tu compra en Alta Pinta!\n\n");
            cuerpo.append("DETALLE DE TU COMPRA\n");
            cuerpo.append("-----------------------------------\n");

            for (PedidoDetalle d : detalles) {
                cuerpo.append("Producto: ").append(d.getProducto().getNombre()).append("\n");
                cuerpo.append("Cantidad: ").append(d.getCantidad()).append("\n");
                cuerpo.append("Precio unitario: S/ ").append(d.getPrecioUnitario()).append("\n");
                cuerpo.append("Subtotal: S/ ")
                        .append(d.getCantidad() * d.getPrecioUnitario())
                        .append("\n\n");
            }

            cuerpo.append("-----------------------------------\n");
            cuerpo.append("Costo de envío: S/ ")
                    .append(pedido.getEnvio() != null ? pedido.getEnvio().getCosto() : 0)
                    .append("\n");
            cuerpo.append("TOTAL PAGADO: S/ ").append(pedido.getTotal()).append("\n\n");
            cuerpo.append("Fecha de compra: ").append(pedido.getFecha().toLocalDate()).append("\n\n");
            cuerpo.append("Gracias por confiar en Alta Pinta.\n");
            cuerpo.append("— Equipo Alta Pinta");

            helper.setText(cuerpo.toString());

            // Adjuntar PDF si existe
            File facturaPdf = new File("facturas/FAC-" + pedido.getId() + ".pdf");
            if (facturaPdf.exists()) {
                FileSystemResource file = new FileSystemResource(facturaPdf);
                helper.addAttachment("Factura.pdf", file);
            }

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error enviando correo de compra", e);
        }
    }

    public void sendPedidoEstado(String to, Pedido pedido) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(sistemaEmail);
            helper.setTo(to);
            helper.setSubject("Actualización de estado de tu pedido - Alta Pinta");

            StringBuilder cuerpo = new StringBuilder();
            cuerpo.append("Hola ").append(pedido.getCliente().getNombre()).append(",\n\n");
            cuerpo.append("El estado de tu pedido #").append(pedido.getId()).append(" ha sido actualizado a: ")
                    .append(pedido.getEstado()).append("\n\n");
            cuerpo.append("Gracias por confiar en Alta Pinta.\n");
            cuerpo.append("— Equipo Alta Pinta");

            helper.setText(cuerpo.toString());

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error enviando correo de actualización de estado", e);
        }
    }
}
