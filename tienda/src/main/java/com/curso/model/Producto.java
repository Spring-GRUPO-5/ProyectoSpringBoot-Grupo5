package com.curso.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "producto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre",nullable = false)
    private String nombre;

    @Column(name = "cantidad",nullable = false)
    private int cantidad;

    @Column(name = "precio",nullable = false,precision = 10, scale = 2)
    private BigDecimal precio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(optional = false)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    @JsonIgnore
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    private List<Imagen> imagenes = new ArrayList<>();

    public Producto() {
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

    public int getCantidad() {
        return this.cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return this.precio;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Categoria getCategoria() {return categoria;}
    public void setCategoria(Categoria categoria) {this.categoria = categoria;}

    public Proveedor getProveedor() {
        return this.proveedor;
    }
    public void setProveedor(Proveedor proveedor) {this.proveedor = proveedor;}

    public List<Imagen> getImagenes() {
        return imagenes;
    }
    public void setImagenes(List<Imagen> imagenes) {
        this.imagenes = imagenes;
    }
}
