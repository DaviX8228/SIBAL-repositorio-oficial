/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package frontend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import backend.ConexionBD;
import seguridad.Usuario;
import seguridad.ControlRegistro;

/**
 *
 * @author MISAEL JIMENEZ (frontend) David Velazquez (Backend)
 */
public class frmUsuarios extends javax.swing.JFrame {

    private ButtonGroup grupoRoles;
    private ControlRegistro controlRegistro;

    /**
     * Creates new form frmUsuarios
     */
    public frmUsuarios() {
        initComponents();
        controlRegistro = new ControlRegistro();
        configurarRadioButtons();
        cargarUsuarios();
        configurarTabla();
    }
    
     private void configurarRadioButtons() {
        grupoRoles = new ButtonGroup();
        grupoRoles.add(rdbAdmin);
        grupoRoles.add(rdbEncargado);
    }

    // Método para configurar la tabla
    private void configurarTabla() {
        tblUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblUsuarios.getSelectedRow() != -1) {
                cargarDatosEnFormulario();
            }
        });
    }

    // Método para cargar usuarios en la tabla
    private void cargarUsuarios() {
        DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Usuario", "Contraseña", "Rol", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        Connection conn = ConexionBD.conectar();
        if (conn != null) {
            try {
                String sql = "SELECT * FROM Usuarios ORDER BY id_usuario";
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    Object[] fila = {
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("usuario"),
                        rs.getString("contraseña"),
                        rs.getString("rol"),
                        rs.getBoolean("estado") ? "Activo" : "Inactivo"
                    };
                    modelo.addRow(fila);
                }
                
                tblUsuarios.setModel(modelo);
                rs.close();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al cargar usuarios: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Método para cargar datos de la tabla al formulario
    private void cargarDatosEnFormulario() {
        int fila = tblUsuarios.getSelectedRow();
        if (fila != -1) {
            txteIdU.setText(tblUsuarios.getValueAt(fila, 0).toString());
            txteNombreU.setText(tblUsuarios.getValueAt(fila, 1).toString());
            txteUsua.setText(tblUsuarios.getValueAt(fila, 2).toString());
            txtpContraU.setText(tblUsuarios.getValueAt(fila, 3).toString());
            
            String rol = tblUsuarios.getValueAt(fila, 4).toString();
            if (rol.equals("Administrador")) {
                rdbAdmin.setSelected(true);
            } else {
                rdbEncargado.setSelected(true);
            }
            
            String estado = tblUsuarios.getValueAt(fila, 5).toString();
            txteEstudioU.setText(estado);
        }
    }

    // Método para limpiar el formulario
    private void limpiarFormulario() {
        txteIdU.setText("");
        txteNombreU.setText("");
        txteUsua.setText("");
        txtpContraU.setText("");
        txteEstudioU.setText("");
        grupoRoles.clearSelection();
        tblUsuarios.clearSelection();
    }

    // Método para validar campos obligatorios
    private boolean validarCampos() {
        if (txteNombreU.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "El nombre es obligatorio", 
                "Validación", JOptionPane.WARNING_MESSAGE);
            txteNombreU.requestFocus();
            return false;
        }
        
        if (txteUsua.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "El usuario es obligatorio", 
                "Validación", JOptionPane.WARNING_MESSAGE);
            txteUsua.requestFocus();
            return false;
        }
        
        if (String.valueOf(txtpContraU.getPassword()).trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "La contraseña es obligatoria", 
                "Validación", JOptionPane.WARNING_MESSAGE);
            txtpContraU.requestFocus();
            return false;
        }
        
        if (String.valueOf(txtpContraU.getPassword()).trim().length() < 4) {
            JOptionPane.showMessageDialog(this, 
                "La contraseña debe tener al menos 4 caracteres", 
                "Validación", JOptionPane.WARNING_MESSAGE);
            txtpContraU.requestFocus();
            return false;
        }
        
        if (!rdbAdmin.isSelected() && !rdbEncargado.isSelected()) {
            JOptionPane.showMessageDialog(this, 
                "Debe seleccionar un rol", 
                "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }

    // Método para obtener el rol seleccionado
    private String obtenerRolSeleccionado() {
        if (rdbAdmin.isSelected()) {
            return "Administrador";
        } else {
            return "Encargado";
        }
    }

    // Método para obtener el estado
    private boolean obtenerEstado() {
        String estado = txteEstudioU.getText().trim();
        return estado.equalsIgnoreCase("Activo") || estado.equals("1") || estado.isEmpty();
    }

    // Método para verificar si el usuario ya existe (excepto el actual en edición)
    private boolean usuarioExiste(String usuario, int idActual) {
        Connection conn = ConexionBD.conectar();
        if (conn != null) {
            try {
                String sql = "SELECT COUNT(*) FROM Usuarios WHERE usuario = ? AND id_usuario != ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, usuario);
                ps.setInt(2, idActual);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                
                rs.close();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al verificar usuario: " + e.getMessage());
            }
        }
        return false;
    }
    
    // Verificar si el usuario ya existe (para edición)
    private boolean existeUsuario(String nombreUsuario, int idActual) {
        Connection conn = ConexionBD.conectar();
        if (conn != null) {
            try {
                String sql = "SELECT COUNT(*) FROM Usuarios WHERE usuario = ? AND id_usuario != ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, nombreUsuario);
                ps.setInt(2, idActual);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                
                rs.close();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al verificar usuario: " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        panInfU = new javax.swing.JPanel();
        txteIdU = new javax.swing.JTextField();
        txteNombreU = new javax.swing.JTextField();
        txtpContraU = new javax.swing.JPasswordField();
        txteUsua = new javax.swing.JTextField();
        txteEstudioU = new javax.swing.JPasswordField();
        btnEditarU = new javax.swing.JButton();
        btnBorrarU = new javax.swing.JButton();
        btnAgregarU = new javax.swing.JButton();
        btnGuardarU = new javax.swing.JButton();
        panRolU = new javax.swing.JPanel();
        rdbAdmin = new javax.swing.JRadioButton();
        rdbEncargado = new javax.swing.JRadioButton();
        btnVolver = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblUsuarios = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 51));
        jLabel2.setText("Gestión de Usuarios");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(172, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(157, 157, 157))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 700, 70));

        panInfU.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txteIdU.setBorder(javax.swing.BorderFactory.createTitledBorder("ID"));

        txteNombreU.setBorder(javax.swing.BorderFactory.createTitledBorder("NOMBRE"));

        txtpContraU.setBorder(javax.swing.BorderFactory.createTitledBorder("CONTRASEÑA"));

        txteUsua.setBorder(javax.swing.BorderFactory.createTitledBorder("USUARIO"));

        txteEstudioU.setBorder(javax.swing.BorderFactory.createTitledBorder("ESTUDIO"));

        btnEditarU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarUActionPerformed(evt);
            }
        });

        btnBorrarU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarUActionPerformed(evt);
            }
        });

        btnAgregarU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarUActionPerformed(evt);
            }
        });

        btnGuardarU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarUActionPerformed(evt);
            }
        });

        panRolU.setBorder(javax.swing.BorderFactory.createTitledBorder("ROL"));

        rdbAdmin.setText("Administrador");

        rdbEncargado.setText("Encargado");

        javax.swing.GroupLayout panRolULayout = new javax.swing.GroupLayout(panRolU);
        panRolU.setLayout(panRolULayout);
        panRolULayout.setHorizontalGroup(
            panRolULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panRolULayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(panRolULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdbEncargado)
                    .addComponent(rdbAdmin))
                .addContainerGap(48, Short.MAX_VALUE))
        );
        panRolULayout.setVerticalGroup(
            panRolULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panRolULayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rdbAdmin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rdbEncargado)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panInfULayout = new javax.swing.GroupLayout(panInfU);
        panInfU.setLayout(panInfULayout);
        panInfULayout.setHorizontalGroup(
            panInfULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panInfULayout.createSequentialGroup()
                .addGroup(panInfULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panInfULayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(panInfULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtpContraU, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panInfULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txteIdU, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                                .addComponent(txteNombreU))))
                    .addGroup(panInfULayout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addComponent(panRolU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(panInfULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txteEstudioU, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .addComponent(txteUsua)
                    .addGroup(panInfULayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(btnAgregarU, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(panInfULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnGuardarU, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panInfULayout.createSequentialGroup()
                                .addComponent(btnEditarU, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnBorrarU, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        panInfULayout.setVerticalGroup(
            panInfULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panInfULayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(panInfULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txteUsua, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                    .addComponent(txteNombreU))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panInfULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txteEstudioU, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                    .addComponent(txtpContraU))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panInfULayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panInfULayout.createSequentialGroup()
                        .addComponent(txteIdU, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(panRolU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panInfULayout.createSequentialGroup()
                        .addComponent(btnEditarU, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnGuardarU, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnBorrarU, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgregarU, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        getContentPane().add(panInfU, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, 650, 360));

        btnVolver.setBackground(new java.awt.Color(51, 51, 255));
        btnVolver.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnVolver.setText("VOLVER");
        btnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverActionPerformed(evt);
            }
        });
        getContentPane().add(btnVolver, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 630, -1, -1));

        tblUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblUsuarios);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 650, 170));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
        // TODO add your handling code here:
        frmPrincipal pri = new frmPrincipal();
        pri.setVisible(true);
        this.dispose();


    }//GEN-LAST:event_btnVolverActionPerformed

    private void btnAgregarUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarUActionPerformed
        
        limpiarFormulario();
        txteNombreU.requestFocus();
    }//GEN-LAST:event_btnAgregarUActionPerformed

    private void btnEditarUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarUActionPerformed
         if (txteIdU.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un usuario de la tabla para editar", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validarCampos()) {
            return;
        }
        
        // Verificar si el nombre de usuario ya existe (excepto el actual)
        int idActual = Integer.parseInt(txteIdU.getText());
        if (existeUsuario(txteUsua.getText().trim(), idActual)) {
            JOptionPane.showMessageDialog(this, 
                "El nombre de usuario ya existe", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Connection conn = ConexionBD.conectar();
        if (conn != null) {
            try {
                String sql = "UPDATE Usuarios SET nombre=?, usuario=?, contraseña=?, rol=?, estado=? WHERE id_usuario=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txteNombreU.getText().trim());
                ps.setString(2, txteUsua.getText().trim());
                ps.setString(3, String.valueOf(txtpContraU.getPassword()));
                ps.setString(4, obtenerRolSeleccionado());
                ps.setBoolean(5, obtenerEstado());
                ps.setInt(6, idActual);
                
                int resultado = ps.executeUpdate();
                
                if (resultado > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Usuario actualizado exitosamente", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarUsuarios();
                    limpiarFormulario();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "No se pudo actualizar el usuario", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
                
                ps.close();
                conn.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al actualizar: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnEditarUActionPerformed

    private void btnGuardarUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarUActionPerformed
         if (!validarCampos()) {
            return;
        }
        
        // Crear objeto Usuario
        Usuario nuevoUsuario = new Usuario(
            txteNombreU.getText().trim(),
            txteUsua.getText().trim(),
            String.valueOf(txtpContraU.getPassword()),
            obtenerRolSeleccionado()
        );
        nuevoUsuario.setEstado(obtenerEstado());
        
        // Registrar usando ControlRegistro
        boolean registrado = controlRegistro.registrarUsuario(nuevoUsuario);
        
        if (registrado) {
            JOptionPane.showMessageDialog(this, 
                "Usuario registrado exitosamente", 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarUsuarios();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, 
                "No se pudo registrar el usuario.\nVerifique que el nombre de usuario no exista.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnGuardarUActionPerformed

    private void btnBorrarUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarUActionPerformed
         if (txteIdU.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un usuario de la tabla para eliminar", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar el usuario: " + txteNombreU.getText() + "?\n" +
            "Esta acción no se puede deshacer.", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            Connection conn = ConexionBD.conectar();
            if (conn != null) {
                try {
                    String sql = "DELETE FROM Usuarios WHERE id_usuario=?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, Integer.parseInt(txteIdU.getText()));
                    
                    int resultado = ps.executeUpdate();
                    
                    if (resultado > 0) {
                        JOptionPane.showMessageDialog(this, 
                            "Usuario eliminado exitosamente", 
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarUsuarios();
                        limpiarFormulario();
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "No se pudo eliminar el usuario", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    
                    ps.close();
                    conn.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Error al eliminar: " + e.getMessage() + 
                        "\n\nNota: Si el usuario tiene movimientos registrados, no se puede eliminar.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_btnBorrarUActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frmUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmUsuarios().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarU;
    private javax.swing.JButton btnBorrarU;
    private javax.swing.JButton btnEditarU;
    private javax.swing.JButton btnGuardarU;
    private javax.swing.JButton btnVolver;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel panInfU;
    private javax.swing.JPanel panRolU;
    private javax.swing.JRadioButton rdbAdmin;
    private javax.swing.JRadioButton rdbEncargado;
    private javax.swing.JTable tblUsuarios;
    private javax.swing.JPasswordField txteEstudioU;
    private javax.swing.JTextField txteIdU;
    private javax.swing.JTextField txteNombreU;
    private javax.swing.JTextField txteUsua;
    private javax.swing.JPasswordField txtpContraU;
    // End of variables declaration//GEN-END:variables
}
