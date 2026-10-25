package com.curso.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cliente")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "telefono", nullable = false)
    private  String telefono;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fecha_nacimiento;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fecha_alta;

    public Cliente() {
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {this.id = id;}

    public String getNombre() {
        return this.nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return this.apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return this.telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {this.email = email;}

    public LocalDate getFecha_nacimiento() {return fecha_nacimiento;}
    public void setFecha_nacimiento(LocalDate fecha_nacimiento) {this.fecha_nacimiento = fecha_nacimiento;}

    public LocalDateTime getFecha_alta() {return fecha_alta;}
    public void setFecha_alta(LocalDateTime fecha_alta) {this.fecha_alta = fecha_alta;}
}
