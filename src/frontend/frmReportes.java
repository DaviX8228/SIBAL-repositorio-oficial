/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package frontend;

import backend.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.sql.SQLException;

/**
 *
 * @author crist
 */
public class frmReportes extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(frmReportes.class.getName());

    /**
     * Creates new form frmReportes
     */
    public frmReportes() {
        initComponents();
    }
    
    private void cargarInventario() {
        String sql = "SELECT p.id_producto, p.nombre, p.descripcion, p.stock, p.precio_unitario, " +
                     "c.nombre AS categoria, pr.nombre AS proveedor " +
                     "FROM productos p " +
                     "LEFT JOIN categorias c ON p.id_categoria = c.id_categoria " +
                     "LEFT JOIN proveedores pr ON p.id_proveedor = pr.id_proveedor";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Descripción", "Stock", "Precio", "Categoría", "Proveedor"}, 0
            );

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getInt("stock"),
                        rs.getDouble("precio_unitario"),
                        rs.getString("categoria"),
                        rs.getString("proveedor")
                });
            }

            tblReporte.setModel(model);

        } catch (Exception e) {
            System.out.println("Error al cargar inventario: " + e.getMessage());
        }
    }
    
    public void refrescarReporte() {
    String opcion = chboxTipo.getSelectedItem().toString();
    switch (opcion) {
        case "Inventario completo": cargarInventario(); break;
        case "Productos con stock bajo": cargarProductosBajos(); break;
        case "Entradas del día": cargarEntradasDelDia(); break;
        case "Salidas del día": cargarSalidasDelDia(); break;
    }
}
    
    private boolean hayProductosConBajoStock() {

        String sql = "SELECT COUNT(*) AS total FROM productos WHERE stock < 10";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

        } catch (Exception e) {
            System.out.println("Error al verificar bajo stock: " + e.getMessage());
        }

        return false;
    }
    
    private void cargarProductosBajos() {
        String sql = "SELECT id_producto, nombre, stock FROM productos WHERE stock < 10";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Stock"}, 0
            );

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getInt("stock")
                });
            }

            tblReporte.setModel(model);

        } catch (Exception e) {
            System.out.println("Error al cargar productos bajos: " + e.getMessage());
        }

        actualizarAlerta(hayProductosConBajoStock());
    }
    
   private void cargarEntradasDelDia() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"ID", "Producto", "Cantidad", "Fecha", "Usuario", "Observaciones"});
        tblReporte.setModel(modelo);

        String sql =
         "SELECT m.id_movimiento, p.nombre AS producto, m.cantidad, m.fecha, u.nombre AS usuario, m.observaciones " +
         "FROM Movimientos m " +
         "INNER JOIN Productos p ON m.id_producto = p.id_producto " +
         "INNER JOIN Usuarios u ON m.id_usuario = u.id_usuario " +
         "WHERE UPPER(m.tipo_movimiento) = 'ENTRADA' " +
         "AND m.fecha >= CURDATE() " +
         "AND m.fecha < CURDATE() + INTERVAL 1 DAY";


        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_movimiento"),
                    rs.getString("producto"),
                    rs.getInt("cantidad"),
                    rs.getString("fecha"),
                    rs.getString("usuario"),
                    rs.getString("observaciones")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar entradas del día: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cargarSalidasDelDia() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"ID", "Producto", "Cantidad", "Fecha", "Usuario", "Observaciones"});
        tblReporte.setModel(modelo);

        String sql =
        "SELECT m.id_movimiento, p.nombre AS producto, m.cantidad, m.fecha, u.nombre AS usuario, m.observaciones " +
        "FROM Movimientos m " +
        "INNER JOIN Productos p ON m.id_producto = p.id_producto " +
        "INNER JOIN Usuarios u ON m.id_usuario = u.id_usuario " +
        "WHERE UPPER(m.tipo_movimiento) = 'SALIDA' " +
        "AND m.fecha >= CURDATE() " +
        "AND m.fecha < CURDATE() + INTERVAL 1 DAY";


        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_movimiento"),
                    rs.getString("producto"),
                    rs.getInt("cantidad"),
                    rs.getString("fecha"),
                    rs.getString("usuario"),
                    rs.getString("observaciones")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar salidas del día: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    
    // Alerta que se pronunciara si hay bajo stock o si todo esta bien :)
    private void actualizarAlerta(boolean bajo) {
        if (bajo) {
            lblAlerta.setText(" PRODUCTOS CON BAJO STOCK ");
            lblAlerta.setForeground(new java.awt.Color(255, 51, 51));
        } else {
            lblAlerta.setText("Todo en orden");
            lblAlerta.setForeground(new java.awt.Color(0, 204, 0));
        }
    }

        /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
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
        jLabel1 = new javax.swing.JLabel();
        panTReportes = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        chboxTipo = new javax.swing.JComboBox<>();
        btnGenerar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReporte = new javax.swing.JTable();
        panAlerta = new javax.swing.JPanel();
        lblAlerta = new javax.swing.JLabel();
        btnExportar = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 51));
        jLabel1.setText("REPORTES");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(290, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(285, 285, 285))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 770, -1));

        panTReportes.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 51));
        jLabel2.setText("TIPO DE REPORTE :");

        chboxTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TIPO DE REPORTE", "Inventario completo", "Productos con stock bajo", "Entradas del día", "Salidas del día" }));
        chboxTipo.setToolTipText("");
        chboxTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chboxTipoActionPerformed(evt);
            }
        });

        btnGenerar.setText("Generar Reporte");
        btnGenerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panTReportesLayout = new javax.swing.GroupLayout(panTReportes);
        panTReportes.setLayout(panTReportesLayout);
        panTReportesLayout.setHorizontalGroup(
            panTReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTReportesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(138, 138, 138)
                .addComponent(chboxTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61)
                .addComponent(btnGenerar)
                .addContainerGap(24, Short.MAX_VALUE))
        );
        panTReportesLayout.setVerticalGroup(
            panTReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTReportesLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(panTReportesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(chboxTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGenerar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(panTReportes, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 100, 630, 50));

        tblReporte.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Producto", "Stock", "Usuario", "Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblReporte);
        if (tblReporte.getColumnModel().getColumnCount() > 0) {
            tblReporte.getColumnModel().getColumn(0).setResizable(false);
        }

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 170, 630, 210));

        panAlerta.setBackground(new java.awt.Color(51, 51, 51));
        panAlerta.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblAlerta.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        lblAlerta.setForeground(new java.awt.Color(153, 0, 0));
        lblAlerta.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout panAlertaLayout = new javax.swing.GroupLayout(panAlerta);
        panAlerta.setLayout(panAlertaLayout);
        panAlertaLayout.setHorizontalGroup(
            panAlertaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panAlertaLayout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(lblAlerta, javax.swing.GroupLayout.PREFERRED_SIZE, 592, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );
        panAlertaLayout.setVerticalGroup(
            panAlertaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panAlertaLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(lblAlerta, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        getContentPane().add(panAlerta, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 400, 630, 70));

        btnExportar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/btnPdf.jpg"))); // NOI18N
        btnExportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarActionPerformed(evt);
            }
        });
        getContentPane().add(btnExportar, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 480, 80, 80));

        btnCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/btnVolver.jpg"))); // NOI18N
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });
        getContentPane().add(btnCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 480, 80, 70));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("GENERAR PDF");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 570, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("VOLVER");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 560, -1, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        // TODO add your handling code here:
        dispose();
        new frmPrincipal().setVisible(true);
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void chboxTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chboxTipoActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_chboxTipoActionPerformed

    private void btnGenerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarActionPerformed
        // TODO add your handling code here:
        String opcion = chboxTipo.getSelectedItem().toString();

    switch (opcion) {

        case "Inventario completo":
            cargarInventario();
            break;

        case "Productos con stock bajo":
            cargarProductosBajos();
            break;

        case "Entradas del día":
            cargarEntradasDelDia();
            break;

        case "Salidas del día":
            cargarSalidasDelDia();
            break;

        default:
            JOptionPane.showMessageDialog(null, "Seleccione un reporte válido");
            break;
    }
    }//GEN-LAST:event_btnGenerarActionPerformed

    private void btnExportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarActionPerformed
        // TODO add your handling code here:
     try {
        //Identifica el tipo de reporte que sera :)
        String reporte = chboxTipo.getSelectedItem().toString();

        if (reporte.equals("TIPO DE REPORTE")) {
            JOptionPane.showMessageDialog(null, "Primero genere un reporte.");
            return;
        }

        //Ruta que tomara el archivo :)
        String rutaCarpeta = System.getProperty("user.home") + "/Desktop/Reportes/";

        //En caso de no existir la carpeta
        java.io.File carpeta = new java.io.File(rutaCarpeta);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        // Colocacion del nombre para el archivo que se generara :)
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        String fechaHoraArchivo = sdf.format(new java.util.Date());
        String nombreArchivo = rutaCarpeta 
        + reporte.replace(" ", "_") 
        + "_" + fechaHoraArchivo + ".pdf";

        // Creacion del documento pdf :)
        com.itextpdf.text.Document documento = new com.itextpdf.text.Document();
        com.itextpdf.text.pdf.PdfWriter.getInstance(documento, new java.io.FileOutputStream(nombreArchivo));

        documento.open();

        // Colocacion del titulo del reporte :)
        com.itextpdf.text.Paragraph titulo = new com.itextpdf.text.Paragraph(
        "REPORTE: " + reporte,
        new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 
                18, 
                com.itextpdf.text.Font.BOLD
            )
        );
        titulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        documento.add(titulo);
        
        //Fecha y hora para el pdf :)
        java.text.SimpleDateFormat sdfPDF = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
        String fechaHoraPDF = sdfPDF.format(new java.util.Date());
        documento.add(new com.itextpdf.text.Paragraph("\nFecha y Hora: " + fechaHoraPDF + "\n\n"));

        // Tabla que se generara en el pdf :)
        com.itextpdf.text.pdf.PdfPTable pdfTable = 
                new com.itextpdf.text.pdf.PdfPTable(tblReporte.getColumnCount());

        // Encabezados
        for (int i = 0; i < tblReporte.getColumnCount(); i++) {
            pdfTable.addCell(new com.itextpdf.text.Phrase(tblReporte.getColumnName(i)));
        }

        // Datos
        for (int i = 0; i < tblReporte.getRowCount(); i++) {
            for (int j = 0; j < tblReporte.getColumnCount(); j++) {
                Object valor = tblReporte.getValueAt(i, j);
                pdfTable.addCell(valor != null ? valor.toString() : "");
            }
        }

        documento.add(pdfTable);
        documento.close();

        JOptionPane.showMessageDialog(null,
                "PDF generado correctamente en:\n" + nombreArchivo);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al generar PDF: " + e.getMessage());
        e.printStackTrace();
    }
    }//GEN-LAST:event_btnExportarActionPerformed

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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new frmReportes().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnExportar;
    private javax.swing.JButton btnGenerar;
    private javax.swing.JComboBox<String> chboxTipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAlerta;
    private javax.swing.JPanel panAlerta;
    private javax.swing.JPanel panTReportes;
    private javax.swing.JTable tblReporte;
    // End of variables declaration//GEN-END:variables
}
