/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package seguridad;

/**
 *
 * @author David Velazquez
 */
    public class Usuario {
        private int idUsuario;
        private String nombre;
        private String usuario;  // Este es el username/login
        private String contraseña;
        private String rol;  // "Administrador" o "Encargado"
        private boolean estado;

        public Usuario(String nombre, String usuario, String contraseña, String rol) {
            this.nombre = nombre;
            this.usuario = usuario;
            this.contraseña = contraseña;
            this.rol = rol;
            this.estado = true;  // Por defecto activo
        }

        public Usuario(int idUsuario, String nombre, String usuario, String contraseña, String rol, boolean estado) {
            this.idUsuario = idUsuario;
            this.nombre = nombre;
            this.usuario = usuario;
            this.contraseña = contraseña;
            this.rol = rol;
            this.estado = estado;
        }

        // Getters y Setters
        public int getIdUsuario() {
            return idUsuario;
        }

        public void setIdUsuario(int idUsuario) {
            this.idUsuario = idUsuario;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getUsuario() {
            return usuario;
        }

        public void setUsuario(String usuario) {
            this.usuario = usuario;
        }

        public String getContraseña() {
            return contraseña;
        }

        public void setContraseña(String contraseña) {
            this.contraseña = contraseña;
        }

        public String getRol() {
            return rol;
        }

        public void setRol(String rol) {
            this.rol = rol;
        }

        public boolean isEstado() {
            return estado;
        }

        public void setEstado(boolean estado) {
            this.estado = estado;
        }

        @Override
        public String toString() {
            return "Usuario{" +
                    "id=" + idUsuario +
                    ", nombre='" + nombre + '\'' +
                    ", usuario='" + usuario + '\'' +
                    ", rol='" + rol + '\'' +
                    ", estado=" + estado +
                    '}';
        }
    }