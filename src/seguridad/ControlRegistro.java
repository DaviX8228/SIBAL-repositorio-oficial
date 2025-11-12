/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package seguridad;

/**
 *
 * @author davhe
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import backend.ConexionBD;
import seguridad.Usuario;

public class ControlRegistro {

    public boolean registrarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios(nombre, correo, contraseña) VALUES (?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getCorreo());
            ps.setString(3, usuario.getContraseña());
            ps.executeUpdate();
            System.out.println("✅ Usuario registrado correctamente.");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }
}
