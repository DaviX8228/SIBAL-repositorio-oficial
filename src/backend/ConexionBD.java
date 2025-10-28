/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 *
 * @author viceg
 */
public class ConexionBD {
    
    private static final String URL = "jdbc:mysql://localhost:3306/sibal";
    private static final String USER = "root"; // cambia si tienes otro usuario
    private static final String PASSWORD = ""; // pon tu contraseña si tienes

    public static Connection conectar() {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexión exitosa a la base de datos SIBAL.");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("❌ Error al conectar: " + e.getMessage());
        }
        return conexion;
    }
    
}
