package com.backend.AltaPinta.dto;

public class ActualizarPerfilRequest {

    private String nombre;
    private String direccion;
    private String dni;
    private String ruc;
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
