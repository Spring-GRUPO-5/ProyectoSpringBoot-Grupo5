package com.curso.model;

import jakarta.persistence.*;

@Entity
@Table(name = "imagen")
public class Imagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "imagen_url", nullable = false, length = 500)
    private String imagenUrl;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // Constructor vacío requerido por JPA
    public Imagen() {
    }

    // Getters y Setters
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getImagenUrl() {return imagenUrl;}
    public void setImagenUrl(String imagenUrl) {this.imagenUrl = imagenUrl;}

    public Producto getProducto() {return producto;}
    public void setProducto(Producto producto) {this.producto = producto;}
}
