/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package seguridad;

/**
 *
 * @author David Velazquez
 */
public class Sesion {
    
    private static Sesion instancia;  
    private Usuario usuarioActivo;   
    
    private Sesion() {
        this.usuarioActivo = null;
    }
    
    /**
     * Obtener la instancia única de Sesion
     */
    public static Sesion getInstancia() {
        if (instancia == null) {
            instancia = new Sesion();
        }
        return instancia;
    }
    
    /**
     * Iniciar sesión con un usuario
     */
    public void iniciarSesion(Usuario usuario) {
        this.usuarioActivo = usuario;
        System.out.println(" Sesión iniciada para: " + usuario.getNombre() + " (" + usuario.getRol() + ")");
    }
    
    /**
     * Cerrar la sesión actual
     */
    public void cerrarSesion() {
        if (usuarioActivo != null) {
            System.out.println(" Sesión cerrada para: " + usuarioActivo.getNombre());
            this.usuarioActivo = null;
        }
    }
    
    /**
     * Obtener el usuario actualmente logueado
     */
    public Usuario getUsuarioActivo() {
        return usuarioActivo;
    }
    
    /**
     * Verificar si hay una sesión activa
     */
    public boolean hayUsuarioActivo() {
        return usuarioActivo != null;
    }
    
    /**
     * Obtener el ID del usuario activo
     */
    public int getIdUsuarioActivo() {
        return (usuarioActivo != null) ? usuarioActivo.getIdUsuario() : -1;
    }
    
    /**
     * Obtener el rol del usuario activo
     */
    public String getRolUsuarioActivo() {
        return (usuarioActivo != null) ? usuarioActivo.getRol() : null;
    }
    
    /**
     * Verificar si el usuario activo es administrador
     */
    public boolean esAdministrador() {
        return usuarioActivo != null && "Administrador".equals(usuarioActivo.getRol());
    }
    
    /**
     * Verificar si el usuario activo es encargado
     */
    public boolean esEncargado() {
        return usuarioActivo != null && "Encargado".equals(usuarioActivo.getRol());
    }
}