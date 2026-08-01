package com.backend.AltaPinta.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RegisterRequest {

    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @NotBlank(message = "El correo es requerido")
    @Email(message = "El correo no tiene un formato válido")
    private String correo;

    @NotBlank(message = "La contraseña es requerida")
    private String password;

    private String direccion;

    @NotBlank(message = "El DNI es requerido")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos numéricos")
    private String dni;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
}
