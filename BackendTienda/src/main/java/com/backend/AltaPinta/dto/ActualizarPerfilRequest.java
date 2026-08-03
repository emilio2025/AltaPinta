package com.backend.AltaPinta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ActualizarPerfilRequest {

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 100, message = "El nombre no puede pasar de 100 caracteres")
    private String nombre;

    @Size(max = 255, message = "La dirección no puede pasar de 255 caracteres")
    private String direccion;

    // Los patrones admiten la cadena vacia a proposito: la pantalla de perfil
    // manda el cliente entero, y quien no tiene RUC o razon social los envia
    // en blanco. Exigirlos aqui impediria guardar cualquier otro cambio del
    // perfil. Lo que si se rechaza es un valor con el formato equivocado, que
    // es el error que de verdad importa.
    @Pattern(regexp = "^(\\d{8})?$", message = "El DNI debe tener 8 dígitos numéricos")
    private String dni;

    @Pattern(regexp = "^(\\d{11})?$", message = "El RUC debe tener 11 dígitos numéricos")
    private String ruc;

    @Size(max = 150, message = "La razón social no puede pasar de 150 caracteres")
    private String razonSocial;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
}
