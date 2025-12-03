/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package frontend;

import backend.ConexionBD;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
/**
 *
 * @author DAVID VELAZQUEZ
 */
public class InicializadorBD {
     public static void inicializar() {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = ConexionBD.conectar();
            stmt = conn.createStatement();
            
            // TABLA: USUARIOS
            stmt.execute("CREATE TABLE IF NOT EXISTS Usuarios (" +
                "id_usuario INT AUTO_INCREMENT PRIMARY KEY, " +
                "nombre VARCHAR(100) NOT NULL, " +
                "usuario VARCHAR(50) NOT NULL UNIQUE, " +
                "contraseña VARCHAR(255) NOT NULL, " +
                "rol VARCHAR(20) NOT NULL, " +
                "estado BOOLEAN DEFAULT TRUE)");
            
            // TABLA: PROVEEDORES
            stmt.execute("CREATE TABLE IF NOT EXISTS Proveedores (" +
                "id_proveedor INT AUTO_INCREMENT PRIMARY KEY, " +
                "nombre VARCHAR(100) NOT NULL, " +
                "contacto VARCHAR(100), " +
                "telefono VARCHAR(20), " +
                "correo VARCHAR(100), " +
                "direccion VARCHAR(255))");
            
            // TABLA: CATEGORIAS
            stmt.execute("CREATE TABLE IF NOT EXISTS Categorias (" +
                "id_categoria INT AUTO_INCREMENT PRIMARY KEY, " +
                "nombre VARCHAR(100) NOT NULL, " +
                "descripcion VARCHAR(255))");
            
            // TABLA: PRODUCTOS
            stmt.execute("CREATE TABLE IF NOT EXISTS Productos (" +
                "id_producto INT AUTO_INCREMENT PRIMARY KEY, " +
                "nombre VARCHAR(100) NOT NULL, " +
                "descripcion VARCHAR(255), " +
                "stock INT DEFAULT 0, " +
                "precio_unitario DECIMAL(10,2) NOT NULL, " +
                "id_categoria INT, " +
                "id_proveedor INT, " +
                "fecha_registro DATE DEFAULT CURRENT_DATE, " +
                "FOREIGN KEY (id_categoria) REFERENCES Categorias(id_categoria) " +
                    "ON UPDATE CASCADE ON DELETE SET NULL, " +
                "FOREIGN KEY (id_proveedor) REFERENCES Proveedores(id_proveedor) " +
                    "ON UPDATE CASCADE ON DELETE SET NULL)");
            
            // TABLA: MOVIMIENTOS
            stmt.execute("CREATE TABLE IF NOT EXISTS Movimientos (" +
                "id_movimiento INT AUTO_INCREMENT PRIMARY KEY, " +
                "tipo_movimiento VARCHAR(20) NOT NULL, " +
                "cantidad INT NOT NULL, " +
                "fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "id_producto INT, " +
                "id_usuario INT, " +
                "observaciones VARCHAR(255), " +
                "FOREIGN KEY (id_producto) REFERENCES Productos(id_producto) " +
                    "ON UPDATE CASCADE ON DELETE CASCADE, " +
                "FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario) " +
                    "ON UPDATE CASCADE ON DELETE CASCADE)");
            
            System.out.println("Base de datos inicializada correctamente.");
            
        } catch (SQLException e) {
            System.out.println("Error al inicializar BD: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
