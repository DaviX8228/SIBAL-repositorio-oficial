/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend;

/**
 *
 * @author David Velazquez
 */
import java.util.Date;

/**
 * Clase modelo para representar un Producto
 */
public class Producto {
    private int idProducto;
    private String nombre;
    private String descripcion;
    private int stock;
    private double precioUnitario;
    private int idCategoria;
    private int idProveedor;
    private Date fechaRegistro;
    
    // Constructor vacío
    public Producto() {
    }
    
    // Constructor completo
    public Producto(int idProducto, String nombre, String descripcion, int stock, 
                    double precioUnitario, int idCategoria, int idProveedor, Date fechaRegistro) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.stock = stock;
        this.precioUnitario = precioUnitario;
        this.idCategoria = idCategoria;
        this.idProveedor = idProveedor;
        this.fechaRegistro = fechaRegistro;
    }
    
    // Constructor sin ID (para nuevos productos)
    public Producto(String nombre, String descripcion, int stock, 
                    double precioUnitario, int idCategoria, int idProveedor) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.stock = stock;
        this.precioUnitario = precioUnitario;
        this.idCategoria = idCategoria;
        this.idProveedor = idProveedor;
    }
    
    // Getters y Setters
    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    @Override
    public String toString() {
        return "Producto{" +
                "idProducto=" + idProducto +
                ", nombre='" + nombre + '\'' +
                ", stock=" + stock +
                ", precioUnitario=" + precioUnitario +
                '}';
    }
}