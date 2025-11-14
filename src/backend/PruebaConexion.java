/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend;
import java.sql.Connection;
/**
 *
 * @author viceg
 */
public class PruebaConexion {
    
    public static void main(String[] args) {
        Connection conn = ConexionBD.conectar();
        if (conn != null) {
            System.out.println("Conexión establecida correctamente.");
        } else {
            System.out.println("No se pudo conectar.");
        }
    }
    
}
