/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend;

/**
 *
 * @author davhe
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControlProductos {

    // ➕ Agregar producto
    public static boolean agregarProducto(String nombre, String descripcion, int stock, double precio, int idCategoria, int idProveedor) {
        Connection conexion = ConexionBD.conectar();
        boolean exito = false;

        try {
            String sql = "INSERT INTO Productos (nombre, descripcion, stock, precio_unitario, id_categoria, id_proveedor) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setInt(3, stock);
            ps.setDouble(4, precio);
            ps.setInt(5, idCategoria);
            ps.setInt(6, idProveedor);
            ps.executeUpdate();
            exito = true;
            System.out.println("Producto agregado correctamente.");
            conexion.close();
        } catch (SQLException e) {
            System.out.println("Error al agregar producto: " + e.getMessage());
        }
        return exito;
    }

    // 🔄 Actualizar stock
    public static boolean actualizarStock(int idProducto, int nuevoStock) {
        Connection conexion = ConexionBD.conectar();
        boolean exito = false;

        try {
            String sql = "UPDATE Productos SET stock = ? WHERE id_producto = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
            exito = true;
            System.out.println("Stock actualizado correctamente.");
            conexion.close();
        } catch (SQLException e) {
            System.out.println("Error al actualizar stock: " + e.getMessage());
        }
        return exito;
    }

    // 🗑️ Eliminar producto
    public static boolean eliminarProducto(int idProducto) {
        Connection conexion = ConexionBD.conectar();
        boolean exito = false;

        try {
            String sql = "DELETE FROM Productos WHERE id_producto = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, idProducto);
            ps.executeUpdate();
            exito = true;
            System.out.println("Producto eliminado correctamente.");
            conexion.close();
        } catch (SQLException e) {
            System.out.println("Error al eliminar producto: " + e.getMessage());
        }
        return exito;
    }

    // 🔍 Consultar todos los productos
    public static List<String> obtenerProductos() {
        Connection conexion = ConexionBD.conectar();
        List<String> productos = new ArrayList<>();

        try {
            String sql = "SELECT nombre, stock, precio_unitario FROM Productos";
            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                String linea = rs.getString("nombre") + " | Stock: " + rs.getInt("stock") + " | Precio: $" + rs.getDouble("precio_unitario");
                productos.add(linea);
            }
            conexion.close();
        } catch (SQLException e) {
            System.out.println("Error al consultar productos: " + e.getMessage());
        }
        return productos;
    }
}