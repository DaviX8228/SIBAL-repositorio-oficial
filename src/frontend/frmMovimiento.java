/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package frontend;

import backend.ConexionBD;
import backend.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;

/**
 *
 * @author MISAEL JIMENEZ (frontend) DAVID VELAZQUEZ (backend)
 */
public class frmMovimiento extends javax.swing.JFrame {
    
    private Producto productoSeleccionado;
    private int cantidadCotizada;
    
    // Constructor que recibe producto y cantidad desde frmCotizaciones
    public frmMovimiento(Producto producto, int cantidad) {
        initComponents();
        this.productoSeleccionado = producto;
        this.cantidadCotizada = cantidad;
        configurarFormulario();
        cargarDatosProducto();
    }
    
    // Constructor vacío (por si se abre directamente)
    public frmMovimiento() {
        initComponents();
        configurarFormulario();
    }

    private void configurarFormulario() {
        // Configurar fecha actual
        java.util.Date fecha = new java.util.Date();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        txteFecha.setText(formato.format(fecha));
        txteFecha.setEditable(false);
        
        // Configurar combo de operaciones
        cboOperacion.setSelectedIndex(0);
    }
    
    // Cargar datos del producto en el formulario
    private void cargarDatosProducto() {
        if (productoSeleccionado != null) {
            txteProducto.setText(productoSeleccionado.getNombre());
            txteCantidad.setText(String.valueOf(cantidadCotizada));
            
            // Agregar información adicional en observaciones
            txteMovimiento.setText(
                "Producto: " + productoSeleccionado.getNombre() + "\n" +
                "Descripción: " + productoSeleccionado.getDescripcion() + "\n" +
                "Stock actual: " + productoSeleccionado.getStock() + "\n" +
                "Precio unitario: $" + productoSeleccionado.getPrecioUnitario()
            );
        }
    }
    
    private boolean validarCampos() {
        if (txteProducto.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre del producto", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (cboOperacion.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione el tipo de operación", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (txteCantidad.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cantidad", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            int cantidad = Integer.parseInt(txteCantidad.getText().trim());
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0", "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private int guardarMovimiento() {
        if (!validarCampos()) {
            return -1;
        }
        
        String producto = txteProducto.getText().trim();
        String tipo = cboOperacion.getSelectedItem().toString();
        String fechaTxt = txteFecha.getText();
        int cantidad = Integer.parseInt(txteCantidad.getText().trim());
        String observaciones = txteMovimiento.getText().trim();

        int idGenerado = -1;
        Connection con = null;

        try {
            con = ConexionBD.conectar();
            
            // Convertir fecha dd/MM/yyyy → yyyy-MM-dd (MySQL)
            java.util.Date fechaUsuario = new SimpleDateFormat("dd/MM/yyyy").parse(fechaTxt);
            String fechaSQL = new SimpleDateFormat("yyyy-MM-dd").format(fechaUsuario);

            // Obtener ID del producto
            String sqlProd = "SELECT id_producto, stock FROM Productos WHERE nombre = ?";
            PreparedStatement psProd = con.prepareStatement(sqlProd);
            psProd.setString(1, producto);
            ResultSet rsProd = psProd.executeQuery();

            if (!rsProd.next()) {
                JOptionPane.showMessageDialog(this, "El producto '" + producto + "' no existe en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                return -1;
            }

            int idProducto = rsProd.getInt("id_producto");
            int stockActual = rsProd.getInt("stock");
            psProd.close();
            
            // Validar stock si es salida
            if (tipo.equals("SALIDA") && cantidad > stockActual) {
                JOptionPane.showMessageDialog(this, 
                    "Stock insuficiente. Disponible: " + stockActual + " unidades", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return -1;
            }

            // Insertar movimiento (sin id_usuario por ahora, puedes agregarlo después)
            String sql = "INSERT INTO Movimientos " +
                         "(tipo_movimiento, cantidad, id_producto, fecha, observaciones) " +
                         "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, tipo);
            ps.setInt(2, cantidad);
            ps.setInt(3, idProducto);
            ps.setString(4, fechaSQL);
            ps.setString(5, observaciones);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }
            ps.close();
            
            // Actualizar stock del producto
            int nuevoStock = tipo.equals("ENTRADA") ? 
                stockActual + cantidad : stockActual - cantidad;
            
            String sqlUpdate = "UPDATE Productos SET stock = ? WHERE id_producto = ?";
            PreparedStatement psUpdate = con.prepareStatement(sqlUpdate);
            psUpdate.setInt(1, nuevoStock);
            psUpdate.setInt(2, idProducto);
            psUpdate.executeUpdate();
            psUpdate.close();
            
            System.out.println("✓ Movimiento guardado y stock actualizado");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al guardar movimiento: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (Exception e) { //Aquí dbería de ir SQLException pero bn, no sé porque no me dejó.
                e.printStackTrace();
            }
        }

        return idGenerado;
    }
    
    private void generarTicket(int id) {
        String tipo = cboOperacion.getSelectedItem().toString();
        String cantidad = txteCantidad.getText();
        String fecha = txteFecha.getText();
        String observaciones = txteMovimiento.getText();
        
        // Calcular total si hay precio
        String totalStr = "";
        if (productoSeleccionado != null) {
            double total = productoSeleccionado.getPrecioUnitario() * Integer.parseInt(cantidad);
            totalStr = "\nTotal: $" + String.format("%.2f", total);
        }
        
        String ticket =
                
                "╔══════════════════════════════╗\n" +
                "║       * S I B A L *       ║\n" +
                "║   TICKET DE MOVIMIENTO    ║\n" +
                "╚══════════════════════════════╝\n\n" +
                "ID Movimiento: " + id + "\n" +
                "────────────────────────────────\n" +
                "Producto: " + txteProducto.getText() + "\n" +
                "Tipo: " + tipo + "\n" +
                "Cantidad: " + cantidad + " unidades\n" +
                "Fecha: " + fecha + totalStr + "\n\n" +
                "Observaciones:\n" +
                observaciones + "\n" +
                "────────────────────────────────\n" +
                "   Movimiento registrado   \n" +
                "════════════════════════════════";

        lblTicket.setText("<html><pre style='font-family:monospace;'>" + 
                         ticket.replace("\n", "<br>") + "</pre></html>");
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        txteProducto = new javax.swing.JTextField();
        txteCantidad = new javax.swing.JTextField();
        txteFecha = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txteMovimiento = new javax.swing.JTextArea();
        btnVolver = new javax.swing.JButton();
        lblTicket = new javax.swing.JLabel();
        btnRecibo = new javax.swing.JButton();
        cboOperacion = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("SIBAL - Registro de Movimientos");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addComponent(jLabel2)
                .addContainerGap(83, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel2)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 550, -1));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        txteProducto.setBackground(new java.awt.Color(204, 204, 204));
        txteProducto.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Producto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12), new java.awt.Color(0, 0, 0))); // NOI18N

        txteCantidad.setBackground(new java.awt.Color(204, 204, 204));
        txteCantidad.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Cantidad", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12), new java.awt.Color(0, 0, 0))); // NOI18N
        txteCantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txteCantidadActionPerformed(evt);
            }
        });

        txteFecha.setBackground(new java.awt.Color(204, 204, 204));
        txteFecha.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Fecha", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12), new java.awt.Color(0, 0, 0))); // NOI18N
        txteFecha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txteFechaActionPerformed(evt);
            }
        });

        jScrollPane1.setBackground(new java.awt.Color(204, 204, 204));
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "OBSERVACIONES", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12), new java.awt.Color(0, 0, 0))); // NOI18N

        txteMovimiento.setEditable(false);
        txteMovimiento.setBackground(new java.awt.Color(204, 204, 204));
        txteMovimiento.setColumns(20);
        txteMovimiento.setRows(5);
        jScrollPane1.setViewportView(txteMovimiento);

        btnVolver.setBackground(new java.awt.Color(51, 51, 255));
        btnVolver.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnVolver.setForeground(new java.awt.Color(0, 0, 0));
        btnVolver.setText("VOLVER");
        btnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverActionPerformed(evt);
            }
        });

        lblTicket.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnRecibo.setBackground(new java.awt.Color(204, 0, 0));
        btnRecibo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRecibo.setForeground(new java.awt.Color(0, 0, 0));
        btnRecibo.setText("RECIBO");
        btnRecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReciboActionPerformed(evt);
            }
        });

        cboOperacion.setBackground(new java.awt.Color(255, 255, 255));
        cboOperacion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cboOperacion.setForeground(new java.awt.Color(0, 0, 0));
        cboOperacion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SELECCIONE", "ENTRADA", "SALIDA" }));
        cboOperacion.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Tipo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12), new java.awt.Color(0, 0, 0))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(txteCantidad)
                        .addGap(18, 18, 18)
                        .addComponent(txteFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txteProducto)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 12, Short.MAX_VALUE))
                    .addComponent(cboOperacion, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(lblTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addComponent(btnRecibo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnVolver)
                .addGap(111, 111, 111))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(txteProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboOperacion, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txteFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txteCantidad, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(lblTicket, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVolver)
                    .addComponent(btnRecibo))
                .addGap(29, 29, 29))
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 550, 460));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txteCantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txteCantidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txteCantidadActionPerformed

    private void txteFechaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txteFechaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txteFechaActionPerformed

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
        // TODO add your handling code here:
        dispose();
        new frmPrincipal().setVisible(true);
    }//GEN-LAST:event_btnVolverActionPerformed

    private void btnReciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReciboActionPerformed
        int id = guardarMovimiento();

        if (id > 0) {
            generarTicket(id);
            JOptionPane.showMessageDialog(this, 
                "Movimiento registrado correctamente.\nID: " + id, 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "No se pudo registrar el movimiento.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnReciboActionPerformed

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
            java.util.logging.Logger.getLogger(frmMovimiento.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmMovimiento.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmMovimiento.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmMovimiento.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmMovimiento().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRecibo;
    private javax.swing.JButton btnVolver;
    private javax.swing.JComboBox<String> cboOperacion;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTicket;
    private javax.swing.JTextField txteCantidad;
    private javax.swing.JTextField txteFecha;
    private javax.swing.JTextArea txteMovimiento;
    private javax.swing.JTextField txteProducto;
    // End of variables declaration//GEN-END:variables
}
