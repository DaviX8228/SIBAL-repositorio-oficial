/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend;

/**
 *
 * @author DAVID VELAZQUEZ
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControlCategorias {
    
    // Agregar categoría
    public static boolean agregarCategoria(Categoria categoria) {
        String sql = "INSERT INTO Categorias (nombre, descripcion) VALUES (?, ?)";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Obtener el ID generado
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    categoria.setIdCategoria(rs.getInt(1));
                }
                System.out.println(" Categoría agregada correctamente: " + categoria.getNombre());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println(" Error al agregar categoría: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Obtener todas las categorías
    public static List<Categoria> obtenerTodasLasCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM Categorias ORDER BY id_categoria DESC";
        
        try (Connection conn = ConexionBD.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                Categoria c = new Categoria(
                    rs.getInt("id_categoria"),
                    rs.getString("nombre"),
                    rs.getString("descripcion")
                );
                categorias.add(c);
            }
            
        } catch (SQLException e) {
            System.err.println(" Error al consultar categorías: " + e.getMessage());
        }
        return categorias;
    }
    
    // Buscar categoría por ID
    public static Categoria buscarCategoriaPorId(int idCategoria) {
        String sql = "SELECT * FROM Categorias WHERE id_categoria = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idCategoria);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new Categoria(
                    rs.getInt("id_categoria"),
                    rs.getString("nombre"),
                    rs.getString("descripcion")
                );
            }
            
        } catch (SQLException e) {
            System.err.println(" Error al buscar categoría: " + e.getMessage());
        }
        return null;
    }
    
    // Buscar categorías por nombre (búsqueda parcial)
    public static List<Categoria> buscarCategoriasPorNombre(String nombre) {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM Categorias WHERE nombre LIKE ? ORDER BY nombre";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Categoria c = new Categoria(
                    rs.getInt("id_categoria"),
                    rs.getString("nombre"),
                    rs.getString("descripcion")
                );
                categorias.add(c);
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al buscar categorías: " + e.getMessage());
        }
        return categorias;
    }
    
    // Actualizar categoría
    public static boolean actualizarCategoria(Categoria categoria) {
        String sql = "UPDATE Categorias SET nombre = ?, descripcion = ? WHERE id_categoria = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.setInt(3, categoria.getIdCategoria());
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println(" Categoría actualizada correctamente.");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println(" Error al actualizar categoría: " + e.getMessage());
        }
        return false;
    }
    
    // Eliminar categoría
    public static boolean eliminarCategoria(int idCategoria) {
        // Primero verificar si hay productos con esta categoría
        if (tieneProductosAsociados(idCategoria)) {
            System.err.println("✗ No se puede eliminar: hay productos asociados a esta categoría");
            return false;
        }
        
        String sql = "DELETE FROM Categorias WHERE id_categoria = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idCategoria);
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Categoría eliminada correctamente.");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al eliminar categoría: " + e.getMessage());
        }
        return false;
    }
    
    // Verificar si una categoría tiene productos asociados
    private static boolean tieneProductosAsociados(int idCategoria) {
        String sql = "SELECT COUNT(*) FROM Productos WHERE id_categoria = ?";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idCategoria);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al verificar productos asociados: " + e.getMessage());
        }
        return false;
    }
    
    // Validar datos de la categoría
    public static boolean validarCategoria(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            System.err.println("✗ El nombre de la categoría no puede estar vacío.");
            return false;
        }
        
        if (categoria.getNombre().trim().length() > 100) {
            System.err.println("✗ El nombre es demasiado largo (máx. 100 caracteres).");
            return false;
        }
        
        return true;
    }
    
    // Verificar si existe una categoría con ese nombre
    public static boolean existeCategoria(String nombre) {
        String sql = "SELECT COUNT(*) FROM Categorias WHERE LOWER(nombre) = LOWER(?)";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nombre.trim());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al verificar existencia de categoría: " + e.getMessage());
        }
        return false;
    }
}