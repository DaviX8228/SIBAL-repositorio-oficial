/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package seguridad;

import backend.ConexionBD;
import java.sql.*;

/**
 *
 * @author David Velazquez
 */
public class ValidarAcceso {
    
    /**
     * Valida el acceso de un usuario al sistema
     * @param usuario Nombre de usuario
     * @param contraseña Contraseña del usuario
     * @return Usuario si las credenciales son correctas, null si no
     */
    public Usuario iniciarSesion(String usuario, String contraseña) {
        if (usuario == null || usuario.trim().isEmpty() || 
            contraseña == null || contraseña.trim().isEmpty()) {
            System.err.println("Usuario o contraseña vacíos.");
            return null;
        }
        
        String sql = "SELECT * FROM Usuarios WHERE usuario = ? AND contraseña = ? AND estado = 1";
        
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, usuario);
            ps.setString(2, contraseña);  // NOTA: Deberías comparar con hash
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Usuario user = new Usuario(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre"),
                    rs.getString("usuario"),
                    rs.getString("contraseña"),
                    rs.getString("rol"),
                    rs.getBoolean("estado")
                );
                
                System.out.println("Inicio de sesión exitoso: " + user.getNombre());
                return user;
            } else {
                System.err.println("Credenciales incorrectas o usuario inactivo.");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al validar acceso: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Verifica si un usuario tiene un rol específico
     * @param usuario Usuario a verificar
     * @param rol Rol requerido ("Administrador" o "Encargado")
     * @return true si tiene el rol, false si no
     */
    public boolean tieneRol(Usuario usuario, String rol) {
        if (usuario == null) {
            return false;
        }
        return usuario.getRol().equals(rol);
    }
    
    /**
     * Verifica si un usuario es administrador
     */
    public boolean esAdministrador(Usuario usuario) {
        return tieneRol(usuario, "Administrador");
    }
    
    /**
     * Verifica si un usuario es encargado
     */
    public boolean esEncargado(Usuario usuario) {
        return tieneRol(usuario, "Encargado");
    }
}