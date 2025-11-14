/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package seguridad;

/**
 *
 * @author David Velazquez
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import backend.ConexionBD;
import seguridad.Usuario;
import java.sql.*;

public class ControlRegistro {
    
    // Registrar un nuevo usuario
    public boolean registrarUsuario(Usuario usuario) {
        // Validaciones previas
        if (!validarDatos(usuario)) {
            return false;
        }
        
        // Verificar si el usuario ya existe
        if (existeUsuario(usuario.getUsuario())) {
            System.err.println(" El usuario '" + usuario.getUsuario() + "' ya existe.");
            return false;
        }
        
        String sql = "INSERT INTO Usuarios(nombre, usuario, contraseña, rol, estado) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getUsuario());
            ps.setString(3, usuario.getContraseña());  // NOTA: Deberías hashearla con SHA-256 o BCrypt
            ps.setString(4, usuario.getRol());
            ps.setBoolean(5, usuario.isEstado());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Obtener el ID generado
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    usuario.setIdUsuario(rs.getInt(1));
                }
                System.out.println(" Usuario '" + usuario.getUsuario() + "' registrado correctamente.");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println(" Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Verificar si un usuario ya existe
    private boolean existeUsuario(String nombreUsuario) {
        String sql = "SELECT COUNT(*) FROM Usuarios WHERE usuario = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nombreUsuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println(" Error al verificar existencia de usuario: " + e.getMessage());
        }
        return false;
    }
    
    // Validar datos del usuario antes de registrar
    private boolean validarDatos(Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            System.err.println(" El nombre no puede estar vacío.");
            return false;
        }
        
        if (usuario.getUsuario() == null || usuario.getUsuario().trim().isEmpty()) {
            System.err.println(" El nombre de usuario no puede estar vacío.");
            return false;
        }
        
        if (usuario.getContraseña() == null || usuario.getContraseña().length() < 4) {
            System.err.println(" La contraseña debe tener al menos 4 caracteres.");
            return false;
        }
        
        if (!usuario.getRol().equals("Administrador") && !usuario.getRol().equals("Encargado")) {
            System.err.println(" El rol debe ser 'Administrador' o 'Encargado'.");
            return false;
        }
        
        return true;
    }
    
    // Obtener usuario por nombre de usuario (para login después)
    public Usuario obtenerUsuarioPorNombre(String nombreUsuario) {
        String sql = "SELECT * FROM Usuarios WHERE usuario = ? AND estado = 1";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nombreUsuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new Usuario(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre"),
                    rs.getString("usuario"),
                    rs.getString("contraseña"),
                    rs.getString("rol"),
                    rs.getBoolean("estado")
                );
            }
            
        } catch (SQLException e) {
            System.err.println(" Error al obtener usuario: " + e.getMessage());
        }
        return null;
    }
}