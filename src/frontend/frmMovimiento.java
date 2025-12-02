/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package frontend;
 
import backend.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author MISAEL JIMENEZ (frontend) DAVID VELAZQUEZ (backend)
 */
public class frmMovimiento extends javax.swing.JFrame {
    
    private Producto productoRecibido;
    private int cantidadRecibida;
    
    // Nuevo constructor para recibir Producto y Cantidad desde frmCotizaciones
    public frmMovimiento(Producto producto, int cantidad) {
        initComponents();
        
        // Asignar los valores recibidos a las variables de instancia
        this.productoRecibido = producto;
        this.cantidadRecibida = cantidad;
        
        // Llamar al método para configurar la interfaz con los datos
        cargarDatosCotizacion(); 
        
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        txteFecha.setText(java.time.LocalDate.now().format(dtf));
        txteFecha.setEditable(false);
    }
    // Constructor vacío 
    public frmMovimiento() {
        initComponents();
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        txteFecha.setText(java.time.LocalDate.now().format(dtf));
        txteFecha.setEditable(false);
    }
    
    // Método para cargar la información del producto y cantidad de la cotización
    private void cargarDatosCotizacion() {
        if (productoRecibido != null) {
            // Muestra el nombre del producto en el campo de texto
            txteProducto.setText(productoRecibido.getNombre());
            // Haz que este campo no sea editable, ya que viene de otra ventana
            txteProducto.setEditable(false); 
            
            // Muestra la cantidad en su campo de texto
            txteCantidad.setText(String.valueOf(cantidadRecibida));
            // Haz que este campo no sea editable
            txteCantidad.setEditable(false);
            
            // Selecciona "SALIDA" por defecto, ya que viene de una cotización
            cboOperacion.setSelectedItem("SALIDA"); 
            cboOperacion.setEnabled(false); // No permitir cambiar la operación
            
            // Puedes añadir una observación por defecto
            txteMovimiento.setText("Venta registrada desde cotización.");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);

                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;

                java.awt.GradientPaint gp = new java.awt.GradientPaint(
                    getWidth(), 0, new java.awt.Color(176, 224, 255), // Azul claro
                    0, 0, new java.awt.Color(245, 250, 255)           // Casi blanco
                );

                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        }
        ;
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        txteProducto = new javax.swing.JTextField();
        txteCantidad = new javax.swing.JTextField();
        txteFecha = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txteMovimiento = new javax.swing.JTextArea();
        btnVolver = new javax.swing.JButton() {

            {
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setOpaque(false);
            }

            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;

                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                java.awt.GradientPaint gp = new java.awt.GradientPaint(
                    0, 0, new java.awt.Color(200, 0, 0),      // Rojo fuerte
                    getWidth(), 0, new java.awt.Color(255, 120, 120) // Rojo claro
                );

                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                super.paintComponent(g);
            }
        }
        ;
        lblTicket = new javax.swing.JLabel();
        btnRecibo = new javax.swing.JButton() {

            {
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setOpaque(false);
            }

            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;

                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                java.awt.GradientPaint gp = new java.awt.GradientPaint(
                    getWidth(), 0, new java.awt.Color(255, 160, 122), // Naranja claro rojizo (derecha)
                    0, 0, new java.awt.Color(220, 60, 40)             // Rojo-naranja más fuerte (izquierda)
                );

                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                super.paintComponent(g);
            }
        }
        ;
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
        txteProducto.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Producto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(0, 0, 0))); // NOI18N

        txteCantidad.setBackground(new java.awt.Color(204, 204, 204));
        txteCantidad.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cantidad", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(0, 0, 0))); // NOI18N
        txteCantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txteCantidadActionPerformed(evt);
            }
        });

        txteFecha.setBackground(new java.awt.Color(204, 204, 204));
        txteFecha.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Fecha", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(0, 0, 0))); // NOI18N
        txteFecha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txteFechaActionPerformed(evt);
            }
        });

        jScrollPane1.setBackground(new java.awt.Color(204, 204, 204));
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createTitledBorder(null, "OBSERVACIONES", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(0, 0, 0))); // NOI18N

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
        cboOperacion.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tipo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(0, 0, 0))); // NOI18N

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
    String productoNombre = txteProducto.getText().trim();
    String operacion = cboOperacion.getSelectedItem().toString();
    String cantidadStr = txteCantidad.getText().trim();
    String observacion = txteMovimiento.getText().trim();

    if(productoNombre.isEmpty() || operacion.equals("SELECCIONE") || cantidadStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor, llene todos los campos.");
        return;
    }

    int cantidad;
    try {
        cantidad = Integer.parseInt(cantidadStr);
        if(cantidad <= 0) throw new NumberFormatException();
    } catch(NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida.");
        return;
    }

    //Revisa el usuario 
    seguridad.Sesion sesion = seguridad.Sesion.getInstancia();
    if (!sesion.hayUsuarioActivo()) {
        JOptionPane.showMessageDialog(this, "No hay ningún usuario logueado.");
        return;
    }
    int idUsuario = sesion.getIdUsuarioActivo();
    String nombreUsuario = sesion.getUsuarioActivo().getNombre();

    try (Connection conn = backend.ConexionBD.conectar()) {

        //Revisa si existe el producto
        PreparedStatement psProd = conn.prepareStatement(
            "SELECT id_producto, stock FROM Productos WHERE nombre=?"
        );
        psProd.setString(1, productoNombre);
        ResultSet rs = psProd.executeQuery();

        if(!rs.next()) {
            JOptionPane.showMessageDialog(this, "El producto no existe.");
            return;
        }

        int idProducto = rs.getInt("id_producto");
        int stockActual = rs.getInt("stock");

        // Calcular nuevo stock
        int nuevoStock = stockActual;
        if(operacion.equalsIgnoreCase("ENTRADA")) {
            nuevoStock += cantidad;
            operacion = "Entrada";
        } else if(operacion.equalsIgnoreCase("SALIDA")) {
            if(cantidad > stockActual) {
                JOptionPane.showMessageDialog(this, "No hay suficiente stock para la salida.");
                return;
            }
            nuevoStock -= cantidad;
            operacion = "Salida";
        }

        //Actualiza los cambios hechos en el stock
        PreparedStatement psUpdateStock = conn.prepareStatement(
            "UPDATE Productos SET stock=? WHERE id_producto=?"
        );
        psUpdateStock.setInt(1, nuevoStock);
        psUpdateStock.setInt(2, idProducto);
        psUpdateStock.executeUpdate();

        //Registra el usuario que hizo el movimiento 
        PreparedStatement psMov = conn.prepareStatement(
            "INSERT INTO Movimientos(tipo_movimiento, cantidad, id_producto, id_usuario, observaciones) VALUES(?, ?, ?, ?, ?)",
            java.sql.Statement.RETURN_GENERATED_KEYS
        );
        psMov.setString(1, operacion);
        psMov.setInt(2, cantidad);
        psMov.setInt(3, idProducto);
        psMov.setInt(4, idUsuario); 
        psMov.setString(5, observacion);
        psMov.executeUpdate();

        ResultSet rsKey = psMov.getGeneratedKeys();
        int idMovimiento = 0;
        if(rsKey.next()) idMovimiento = rsKey.getInt(1);

        //Da la fecha actualizada :)
        String fechaActual = java.time.LocalDate.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );
        txteFecha.setText(fechaActual);

        //Generacion del ticket :)
        String ticket = "===== TICKET DE MOVIMIENTO =====\n";
        ticket += "ID Movimiento: " + idMovimiento + "\n";
        ticket += "Usuario: " + nombreUsuario + "\n"; 
        ticket += "Producto: " + productoNombre + "\n";
        ticket += "Tipo: " + operacion + "\n";
        ticket += "Cantidad: " + cantidad + "\n";
        ticket += "Fecha: " + fechaActual + "\n";
        ticket += "Observaciones: " + observacion + "\n";
        ticket += "Stock actual: " + nuevoStock + "\n";
        ticket += "===============================\n";
        lblTicket.setText("<html>" + ticket.replaceAll("\n", "<br>") + "</html>");

        JOptionPane.showMessageDialog(this, "Movimiento registrado correctamente.");

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al guardar en la base de datos: " + ex.getMessage());
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
