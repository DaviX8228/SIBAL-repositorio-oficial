/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package frontend;

import backend.ControlProductos;
import backend.Producto;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author MISAEL JIMENEZ (frontend) y DAVID VELAZQUEZ (backend)
 */
public class frmCotizaciones extends javax.swing.JFrame {

    
      private Producto productoSeleccionado;
    private List<Producto> todosLosProductos;
    private ButtonGroup grupoRoles;

    // Constructor que recibe el producto seleccionado
    public frmCotizaciones(Producto producto) {
        initComponents();
        this.productoSeleccionado = producto;
        configurarFormulario();
        cargarProducto();
        cargarListaProductos();
    }
    
    // Constructor vacío (por si se abre sin producto)
    public frmCotizaciones() {
        initComponents();
        configurarFormulario();
        cargarListaProductos();
    }

    private void configurarFormulario() {
        // Configurar grupo de radio buttons
        grupoRoles = new ButtonGroup();
        grupoRoles.add(rdbGerente);
        grupoRoles.add(rdbUsuario);
        rdbUsuario.setSelected(true); // Usuario por defecto
        
        // Hacer el TextArea no editable
        txtaInformacion.setEditable(false);
        txtaInformacion.setLineWrap(true);
        txtaInformacion.setWrapStyleWord(true);
    }

    // Cargar el producto seleccionado en el campo
    private void cargarProducto() {
        if (productoSeleccionado != null) {
            txteProducto.setText(productoSeleccionado.getNombre());
            txteCantidad.setText("1"); // Cantidad por defecto
            mostrarInformacionProducto();
        }
    }

    // Cargar lista visual de productos disponibles
    private void cargarListaProductos() {
        todosLosProductos = ControlProductos.obtenerTodosLosProductos();
        
        // Crear un panel con scroll para mostrar productos
        JPanel panelProductos = new JPanel();
        panelProductos.setLayout(new BoxLayout(panelProductos, BoxLayout.Y_AXIS));
        panelProductos.setBackground(Color.WHITE);
        
        for (Producto p : todosLosProductos) {
            JLabel lblProducto = new JLabel(
                String.format("<html><b>%s</b><br>Stock: %d | Precio: $%.2f</html>", 
                    p.getNombre(), p.getStock(), p.getPrecioUnitario())
            );
            lblProducto.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            lblProducto.setOpaque(true);
            lblProducto.setBackground(Color.WHITE);
            lblProducto.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Click para seleccionar producto
            lblProducto.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    productoSeleccionado = p;
                    txteProducto.setText(p.getNombre());
                    txteCantidad.setText("1");
                    limpiarInformacion();
                }
                
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    lblProducto.setBackground(new Color(230, 230, 230));
                }
                
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    lblProducto.setBackground(Color.WHITE);
                }
            });
            
            panelProductos.add(lblProducto);
            panelProductos.add(Box.createVerticalStrut(2));
        }
        
        JScrollPane scrollProductos = new JScrollPane(panelProductos);
        scrollProductos.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollProductos.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Limpiar el label y agregar el scroll
        lblProductos.setLayout(new BorderLayout());
        lblProductos.removeAll();
        lblProductos.add(scrollProductos, BorderLayout.CENTER);
        lblProductos.revalidate();
        lblProductos.repaint();
    }

    // Mostrar información del producto
    private void mostrarInformacionProducto() {
        if (productoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un producto de la lista", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validar cantidad
        int cantidad;
        try {
            cantidad = Integer.parseInt(txteCantidad.getText().trim());
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "La cantidad debe ser mayor a 0", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Ingrese una cantidad válida", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Verificar stock disponible
        if (cantidad > productoSeleccionado.getStock()) {
            JOptionPane.showMessageDialog(this, 
                "Stock insuficiente. Disponible: " + productoSeleccionado.getStock(), 
                "Advertencia", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Calcular precios según rol
        double precioUnitario = productoSeleccionado.getPrecioUnitario();
        double descuento = 0;
        String rol = rdbGerente.isSelected() ? "Gerente" : "Usuario";
        
        // Aplicar descuento si es gerente (ejemplo: 10%)
        if (rdbGerente.isSelected()) {
            descuento = 0.10;
        }
        
        double subtotal = precioUnitario * cantidad;
        double montoDescuento = subtotal * descuento;
        double total = subtotal - montoDescuento;
        
        // Formatear fecha
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = productoSeleccionado.getFechaRegistro() != null ? 
            formato.format(productoSeleccionado.getFechaRegistro()) : "N/A";
        
        // Mostrar información
        StringBuilder info = new StringBuilder();
        info.append("═══════════════════════════════════════\n");
        info.append("                 * S I B A L *         \n");
        info.append("═══════════════════════════════════════\n");
        info.append("         INFORMACIÓN DEL PRODUCTO\n");
        info.append("═══════════════════════════════════════\n\n");
        info.append("ID: ").append(productoSeleccionado.getIdProducto()).append("\n");
        info.append("Nombre: ").append(productoSeleccionado.getNombre()).append("\n");
        info.append("Descripción: ").append(productoSeleccionado.getDescripcion()).append("\n");
        info.append("Stock disponible: ").append(productoSeleccionado.getStock()).append(" unidades\n");
        info.append("Categoría ID: ").append(productoSeleccionado.getIdCategoria()).append("\n");
        info.append("Fecha de registro: ").append(fecha).append("\n\n");
        
        info.append("───────────────────────────────────────\n");
        info.append("         DETALLES DE COTIZACIÓN\n");
        info.append("───────────────────────────────────────\n\n");
        info.append("Rol: ").append(rol).append("\n");
        info.append("Precio unitario: $").append(String.format("%.2f", precioUnitario)).append("\n");
        info.append("Cantidad solicitada: ").append(cantidad).append("\n");
        info.append("Subtotal: $").append(String.format("%.2f", subtotal)).append("\n");
        
        if (descuento > 0) {
            info.append("Descuento (").append((int)(descuento * 100)).append("%): -$")
                .append(String.format("%.2f", montoDescuento)).append("\n");
        }
        
        info.append("\n═══════════════════════════════════════\n");
        info.append("TOTAL A PAGAR: $").append(String.format("%.2f", total)).append("\n");
        info.append("═══════════════════════════════════════\n");
        
        txtaInformacion.setText(info.toString());
        txtaInformacion.setCaretPosition(0); // Scroll al inicio
    }

    // Limpiar información
    private void limpiarInformacion() {
        txtaInformacion.setText("");
    }

// Generar ticket - Pasar a frmMovimiento
    private void generarTicket() {
        if (txtaInformacion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Primero debe ver la información del producto", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (productoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, 
                "No hay producto seleccionado", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Obtener cantidad
        int cantidad;
        try {
            cantidad = Integer.parseInt(txteCantidad.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Cantidad inválida", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Confirmar y pasar a frmMovimiento
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Desea generar el ticket y registrar el movimiento?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            // Pasar producto y cantidad a frmMovimiento
            this.dispose();
            new frmMovimiento(productoSeleccionado, cantidad).setVisible(true);
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
        jLabel2 = new javax.swing.JLabel();
        lblProductos = new javax.swing.JLabel();
        panCotizacion = new javax.swing.JPanel();
        btnVer = new javax.swing.JButton() {

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
                    0, 0, new java.awt.Color(120, 72, 30),    // Café oscuro
                    getWidth(), 0, new java.awt.Color(200, 150, 100) // Café claro
                );

                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                super.paintComponent(g);
            }
        }
        ;
        txteProducto = new javax.swing.JTextField();
        txteCantidad = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtaInformacion = new javax.swing.JTextArea();
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
        lblRol = new javax.swing.JPanel();
        rdbGerente = new javax.swing.JRadioButton();
        rdbUsuario = new javax.swing.JRadioButton();
        btnTicket = new javax.swing.JButton() {

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
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("COTIZACIÓN");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(151, 151, 151)
                .addComponent(jLabel2)
                .addContainerGap(180, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 490, -1));

        lblProductos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(lblProductos, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 100, 190, 240));

        panCotizacion.setBackground(new java.awt.Color(255, 255, 255));

        btnVer.setBackground(new java.awt.Color(51, 0, 0));
        btnVer.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnVer.setText("VER");
        btnVer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerActionPerformed(evt);
            }
        });

        txteProducto.setBackground(new java.awt.Color(204, 204, 204));
        txteProducto.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Producto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(0, 0, 0))); // NOI18N

        txteCantidad.setBackground(new java.awt.Color(204, 204, 204));
        txteCantidad.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cantidad", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(0, 0, 0))); // NOI18N
        txteCantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txteCantidadActionPerformed(evt);
            }
        });

        jScrollPane1.setBackground(new java.awt.Color(204, 204, 204));
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createTitledBorder(null, "INFORMACION DEL PRODUCTO", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(0, 0, 0))); // NOI18N

        txtaInformacion.setEditable(false);
        txtaInformacion.setBackground(new java.awt.Color(204, 204, 204));
        txtaInformacion.setColumns(20);
        txtaInformacion.setRows(5);
        jScrollPane1.setViewportView(txtaInformacion);

        btnVolver.setBackground(new java.awt.Color(51, 51, 255));
        btnVolver.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnVolver.setForeground(new java.awt.Color(0, 0, 0));
        btnVolver.setText("VOLVER");
        btnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverActionPerformed(evt);
            }
        });

        lblRol.setBackground(new java.awt.Color(204, 204, 204));
        lblRol.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Rol", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(0, 0, 0))); // NOI18N

        rdbGerente.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        rdbGerente.setForeground(new java.awt.Color(0, 0, 0));
        rdbGerente.setText("Administrador");
        rdbGerente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbGerenteActionPerformed(evt);
            }
        });

        rdbUsuario.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        rdbUsuario.setForeground(new java.awt.Color(0, 0, 0));
        rdbUsuario.setText("Encargado");

        javax.swing.GroupLayout lblRolLayout = new javax.swing.GroupLayout(lblRol);
        lblRol.setLayout(lblRolLayout);
        lblRolLayout.setHorizontalGroup(
            lblRolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lblRolLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lblRolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdbGerente)
                    .addComponent(rdbUsuario))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        lblRolLayout.setVerticalGroup(
            lblRolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lblRolLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rdbGerente)
                .addGap(18, 18, 18)
                .addComponent(rdbUsuario)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        btnTicket.setBackground(new java.awt.Color(204, 0, 0));
        btnTicket.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTicket.setForeground(new java.awt.Color(0, 0, 0));
        btnTicket.setText("TICKET");
        btnTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTicketActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panCotizacionLayout = new javax.swing.GroupLayout(panCotizacion);
        panCotizacion.setLayout(panCotizacionLayout);
        panCotizacionLayout.setHorizontalGroup(
            panCotizacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panCotizacionLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(panCotizacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panCotizacionLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 458, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panCotizacionLayout.createSequentialGroup()
                        .addGroup(panCotizacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txteProducto)
                            .addGroup(panCotizacionLayout.createSequentialGroup()
                                .addGroup(panCotizacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txteCantidad)
                                    .addComponent(lblRol, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(panCotizacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnTicket)
                                    .addComponent(btnVer, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(211, 211, 211))))
            .addGroup(panCotizacionLayout.createSequentialGroup()
                .addGap(201, 201, 201)
                .addComponent(btnVolver)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panCotizacionLayout.setVerticalGroup(
            panCotizacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panCotizacionLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(txteProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panCotizacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panCotizacionLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(btnVer, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47)
                        .addComponent(btnTicket))
                    .addGroup(panCotizacionLayout.createSequentialGroup()
                        .addComponent(txteCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnVolver)
                .addContainerGap(54, Short.MAX_VALUE))
        );

        getContentPane().add(panCotizacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 490, 470));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 490, 540));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txteCantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txteCantidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txteCantidadActionPerformed

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
        this.dispose();
        new frmProducto().setVisible(true);
    }//GEN-LAST:event_btnVolverActionPerformed

    private void btnVerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerActionPerformed
        // TODO add your handling code here:
        mostrarInformacionProducto();
    }//GEN-LAST:event_btnVerActionPerformed

    private void btnTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTicketActionPerformed
        generarTicket();
    }//GEN-LAST:event_btnTicketActionPerformed

    private void rdbGerenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbGerenteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdbGerenteActionPerformed

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
            java.util.logging.Logger.getLogger(frmCotizaciones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmCotizaciones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmCotizaciones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmCotizaciones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmCotizaciones().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnTicket;
    private javax.swing.JButton btnVer;
    private javax.swing.JButton btnVolver;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblProductos;
    private javax.swing.JPanel lblRol;
    private javax.swing.JPanel panCotizacion;
    private javax.swing.JRadioButton rdbGerente;
    private javax.swing.JRadioButton rdbUsuario;
    private javax.swing.JTextArea txtaInformacion;
    private javax.swing.JTextField txteCantidad;
    private javax.swing.JTextField txteProducto;
    // End of variables declaration//GEN-END:variables
}
