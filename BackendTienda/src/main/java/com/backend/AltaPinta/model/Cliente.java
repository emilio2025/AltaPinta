package com.backend.AltaPinta.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(nullable = false, unique = true)
    private String correo;

    private String direccion;
    private String dni;
    private String password;

    // RF047: Datos fiscales opcionales (para factura a nombre de empresa)
    private String ruc;
    private String razonSocial;

    private boolean verificado = false;

    private String tokenVerificacion;
    private LocalDateTime tokenExpira;

    private String tokenResetPassword;
    private LocalDateTime tokenResetExpira;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @OneToMany(mappedBy = "cliente")
    @JsonManagedReference
    private List<Tarjeta> tarjetas;

    @OneToMany(mappedBy = "cliente")
    @JsonIgnore
    private List<Pedido> pedidos;

    public List<Tarjeta> getTarjetas() {
        return tarjetas;
    }

    public void setTarjetas(List<Tarjeta> tarjetas) {
        this.tarjetas = tarjetas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isVerificado() { return verificado; }
    public void setVerificado(boolean verificado) { this.verificado = verificado; }

    public String getTokenVerificacion() { return tokenVerificacion; }
    public void setTokenVerificacion(String tokenVerificacion) { this.tokenVerificacion = tokenVerificacion; }

    public LocalDateTime getTokenExpira() { return tokenExpira; }
    public void setTokenExpira(LocalDateTime tokenExpira) { this.tokenExpira = tokenExpira; }

    public String getTokenResetPassword() { return tokenResetPassword; }
    public void setTokenResetPassword(String tokenResetPassword) { this.tokenResetPassword = tokenResetPassword; }

    public LocalDateTime getTokenResetExpira() { return tokenResetExpira; }
    public void setTokenResetExpira(LocalDateTime tokenResetExpira) { this.tokenResetExpira = tokenResetExpira; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }
}
