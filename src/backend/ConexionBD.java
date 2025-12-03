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
    
  private static final String URL = "jdbc:h2:./basedatos/sibal;AUTO_SERVER=TRUE";
    private static final String USER = "sa"; 
    private static final String PASSWORD = ""; 
    
    public static Connection conectar() {
        Connection conexion = null;
        try {
            Class.forName("org.h2.Driver");
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✓ Conexión exitosa a la base de datos SIBAL.");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("✗ Error al conectar: " + e.getMessage());
            e.printStackTrace();
        }
        return conexion;
    }
    
}
