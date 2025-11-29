/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend;

/**
 *
 * @author David Velazquez
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControlProductos {

 // Agregar producto usando objeto Producto
    public static boolean agregarProducto(Producto producto) {
        String sql = "INSERT INTO Productos (nombre, descripcion, stock, precio_unitario, id_categoria, id_proveedor) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getDescripcion());
            ps.setInt(3, producto.getStock());
            ps.setDouble(4, producto.getPrecioUnitario());
            ps.setInt(5, producto.getIdCategoria());
            ps.setInt(6, producto.getIdProveedor());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Obtener el ID generado
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    producto.setIdProducto(rs.getInt(1));
                }
                System.out.println("✓ Producto agregado correctamente.");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al agregar producto: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Obtener todos los productos (retorna objetos Producto)
    public static List<Producto> obtenerTodosLosProductos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Productos ORDER BY id_producto DESC";
        
        try (Connection conn = ConexionBD.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getInt("stock"),
                    rs.getDouble("precio_unitario"),
                    rs.getInt("id_categoria"),
                    rs.getInt("id_proveedor"),
                    rs.getDate("fecha_registro")
                );
                productos.add(p);
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al consultar productos: " + e.getMessage());
        }
        return productos;
    }
    
    // Buscar producto por ID
    public static Producto buscarProductoPorId(int idProducto) {
        String sql = "SELECT * FROM Productos WHERE id_producto = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new Producto(
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getInt("stock"),
                    rs.getDouble("precio_unitario"),
                    rs.getInt("id_categoria"),
                    rs.getInt("id_proveedor"),
                    rs.getDate("fecha_registro")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al buscar producto: " + e.getMessage());
        }
        return null;
    }
    
    // Buscar productos por nombre (búsqueda parcial)
    public static List<Producto> buscarProductosPorNombre(String nombre) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Productos WHERE nombre LIKE ? ORDER BY nombre";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getInt("stock"),
                    rs.getDouble("precio_unitario"),
                    rs.getInt("id_categoria"),
                    rs.getInt("id_proveedor"),
                    rs.getDate("fecha_registro")
                );
                productos.add(p);
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al buscar productos: " + e.getMessage());
        }
        return productos;
    }
    
    // Actualizar producto completo
    public static boolean actualizarProducto(Producto producto) {
        String sql = "UPDATE Productos SET nombre = ?, descripcion = ?, stock = ?, precio_unitario = ?, id_categoria = ?, id_proveedor = ? WHERE id_producto = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getDescripcion());
            ps.setInt(3, producto.getStock());
            ps.setDouble(4, producto.getPrecioUnitario());
            ps.setInt(5, producto.getIdCategoria());
            ps.setInt(6, producto.getIdProveedor());
            ps.setInt(7, producto.getIdProducto());
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✓ Producto actualizado correctamente.");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al actualizar producto: " + e.getMessage());
        }
        return false;
    }
    
    // Actualizar solo el stock
    public static boolean actualizarStock(int idProducto, int nuevoStock) {
        String sql = "UPDATE Productos SET stock = ? WHERE id_producto = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idProducto);
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✓ Stock actualizado correctamente.");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al actualizar stock: " + e.getMessage());
        }
        return false;
    }
    
    // Eliminar producto
    public static boolean eliminarProducto(int idProducto) {
        String sql = "DELETE FROM Productos WHERE id_producto = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idProducto);
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Producto eliminado correctamente.");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al eliminar producto: " + e.getMessage());
        }
        return false;
    }
    
    // Validar datos del producto
    public static boolean validarProducto(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            System.err.println("✗ El nombre del producto no puede estar vacío.");
            return false;
        }
        
        if (producto.getStock() < 0) {
            System.err.println("✗ El stock no puede ser negativo.");
            return false;
        }
        
        if (producto.getPrecioUnitario() <= 0) {
            System.err.println("✗ El precio debe ser mayor a 0.");
            return false;
        }
        
        return true;
    }
}