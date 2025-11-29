/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package frontend;

/**
 *
 * @author MISAEL JIMENEZ (frontend) DAVID VELAZQUEZ (backend)
 */
import backend.Categoria;
import backend.ControlCategorias;
import backend.ControlProductos;
import backend.Producto;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.List;
import seguridad.Sesion;


public class frmProducto extends javax.swing.JFrame {

     private DefaultTableModel modeloTablaProductos;
    private DefaultTableModel modeloTablaCategorias;
    private boolean modoEdicionProducto = false;
    private boolean modoEdicionCategoria = false;
    private int idProductoSeleccionado = -1;
    private int idCategoriaSeleccionada = -1;
    private Sesion sesion;

    public frmProducto() {
        initComponents();
        sesion = Sesion.getInstancia();
        configurarTablaProductos();
        configurarTablaCategorias();
        cargarProductos();
        cargarCategorias();
        limpiarCamposProductos();
        limpiarCamposCategorias();
        panAgregaP.setVisible(false);
        configurarPermisosCategorias();
    }

    // ===== CONFIGURACIÓN DE PERMISOS =====
    private void configurarPermisosCategorias() {
        if (!sesion.esAdministrador()) {
            // Deshabilitar todos los botones de categorías para Encargados
            btnAgregarCateg.setEnabled(false);
            btnEditarCateg.setEnabled(false);
            btnEliminarCateg.setEnabled(false);
            txteNombreCategoria.setEditable(false);
            txtaDescripcionCategoria.setEditable(false);
            
            System.out.println("⚠ Gestión de categorías restringida para: " + sesion.getRolUsuarioActivo());
        }
    }

    // ===== MÉTODOS PARA PRODUCTOS =====
    private void configurarTablaProductos() {
        String[] columnas = {"ID", "Nombre", "Descripción", "Stock", "Precio", "Categoría", "Fecha"};
        modeloTablaProductos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblProducto.setModel(modeloTablaProductos);
        
        tblProducto.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblProducto.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblProducto.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblProducto.getColumnModel().getColumn(3).setPreferredWidth(70);
        tblProducto.getColumnModel().getColumn(4).setPreferredWidth(80);
        tblProducto.getColumnModel().getColumn(5).setPreferredWidth(80);
        tblProducto.getColumnModel().getColumn(6).setPreferredWidth(100);
    }

    private void cargarProductos() {
        modeloTablaProductos.setRowCount(0);
        List<Producto> productos = ControlProductos.obtenerTodosLosProductos();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        
        for (Producto p : productos) {
            Object[] fila = {
                p.getIdProducto(),
                p.getNombre(),
                p.getDescripcion(),
                p.getStock(),
                String.format("$%.2f", p.getPrecioUnitario()),
                p.getIdCategoria(),
                p.getFechaRegistro() != null ? formato.format(p.getFechaRegistro()) : ""
            };
            modeloTablaProductos.addRow(fila);
        }
    }

    private void limpiarCamposProductos() {
        txteIdPr.setText("");
        txteNombrePr.setText("");
        txteCantidadPr.setText("");
        txtePrecioU.setText("");
        txteDescripcionPr.setText("");
        txteFechaPr.setText("");
        txteCategoria.setText("");
        
        txteIdPr.setEditable(false);
        txteFechaPr.setEditable(false);
        
        modoEdicionProducto = false;
        idProductoSeleccionado = -1;
        btnGuardarPR.setText("GUARDAR");
    }

    private boolean validarCamposProductos() {
        if (txteNombrePr.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre del producto", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (txteCantidadPr.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la cantidad/stock", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (txtePrecioU.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el precio unitario", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (txteCategoria.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID de categoría", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            Integer.parseInt(txteCantidadPr.getText().trim());
            Double.parseDouble(txtePrecioU.getText().trim());
            Integer.parseInt(txteCategoria.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valores numéricos inválidos", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    private void guardarProducto() {
        if (!validarCamposProductos()) return;
        
        String nombre = txteNombrePr.getText().trim();
        String descripcion = txteDescripcionPr.getText().trim();
        int stock = Integer.parseInt(txteCantidadPr.getText().trim());
        double precio = Double.parseDouble(txtePrecioU.getText().trim());
        int idCategoria = Integer.parseInt(txteCategoria.getText().trim());
        int idProveedor = 1;
        
        if (modoEdicionProducto && idProductoSeleccionado > 0) {
            Producto producto = new Producto(idProductoSeleccionado, nombre, descripcion, stock, precio, idCategoria, idProveedor, null);
            
            if (ControlProductos.actualizarProducto(producto)) {
                JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarProductos();
                limpiarCamposProductos();
                panAgregaP.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el producto", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            Producto producto = new Producto(nombre, descripcion, stock, precio, idCategoria, idProveedor);
            
            if (ControlProductos.validarProducto(producto) && ControlProductos.agregarProducto(producto)) {
                JOptionPane.showMessageDialog(this, "Producto agregado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarProductos();
                limpiarCamposProductos();
                panAgregaP.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Error al agregar el producto", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscarProducto() {
        String criterio = JOptionPane.showInputDialog(this, "Ingrese el nombre del producto a buscar:", "Buscar Producto", JOptionPane.QUESTION_MESSAGE);
        
        if (criterio != null && !criterio.trim().isEmpty()) {
            List<Producto> productos = ControlProductos.buscarProductosPorNombre(criterio);
            
            if (productos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron productos con ese nombre", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
                cargarProductos();
            } else {
                modeloTablaProductos.setRowCount(0);
                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                
                for (Producto p : productos) {
                    Object[] fila = {
                        p.getIdProducto(),
                        p.getNombre(),
                        p.getDescripcion(),
                        p.getStock(),
                        String.format("$%.2f", p.getPrecioUnitario()),
                        p.getIdCategoria(),
                        p.getFechaRegistro() != null ? formato.format(p.getFechaRegistro()) : ""
                    };
                    modeloTablaProductos.addRow(fila);
                }
            }
        }
    }

    private void cargarProductoSeleccionado() {
        int filaSeleccionada = tblProducto.getSelectedRow();
        
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        idProductoSeleccionado = (int) modeloTablaProductos.getValueAt(filaSeleccionada, 0);
        Producto producto = ControlProductos.buscarProductoPorId(idProductoSeleccionado);
        
        if (producto != null) {
            txteIdPr.setText(String.valueOf(producto.getIdProducto()));
            txteNombrePr.setText(producto.getNombre());
            txteDescripcionPr.setText(producto.getDescripcion());
            txteCantidadPr.setText(String.valueOf(producto.getStock()));
            txtePrecioU.setText(String.valueOf(producto.getPrecioUnitario()));
            txteCategoria.setText(String.valueOf(producto.getIdCategoria()));
            
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            txteFechaPr.setText(producto.getFechaRegistro() != null ? formato.format(producto.getFechaRegistro()) : "");
            
            modoEdicionProducto = true;
            btnGuardarPR.setText("ACTUALIZAR");
            panAgregaP.setVisible(true);
        }
    }

    // ===== MÉTODOS PARA CATEGORÍAS =====
    private void configurarTablaCategorias() {
        String[] columnas = {"ID", "Nombre", "Descripción"};
        modeloTablaCategorias = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCategorias.setModel(modeloTablaCategorias);
        
        tblCategorias.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblCategorias.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblCategorias.getColumnModel().getColumn(2).setPreferredWidth(200);
    }

    private void cargarCategorias() {
        modeloTablaCategorias.setRowCount(0);
        List<Categoria> categorias = ControlCategorias.obtenerTodasLasCategorias();
        
        for (Categoria c : categorias) {
            Object[] fila = {
                c.getIdCategoria(),
                c.getNombre(),
                c.getDescripcion()
            };
            modeloTablaCategorias.addRow(fila);
        }
    }
    
    private void agregarCategoria() {
        // VERIFICAR PERMISOS
        if (!sesion.esAdministrador()) {
            JOptionPane.showMessageDialog(this, 
                "Acceso denegado.\nSolo los Administradores pueden crear categorías.", 
                "Permiso denegado", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validarCamposCategorias()) return;
        
        String nombre = txteNombreCategoria.getText().trim();
        String descripcion = txtaDescripcionCategoria.getText().trim();
        
        // Si está en modo edición, guardar cambios
        if (modoEdicionCategoria && idCategoriaSeleccionada > 0) {
            guardarCambiosCategoria();
            return;
        }
        
        // Verificar si ya existe
        if (ControlCategorias.existeCategoria(nombre)) {
            JOptionPane.showMessageDialog(this, 
                "Ya existe una categoría con ese nombre", 
                "Categoría duplicada", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Categoria categoria = new Categoria(nombre, descripcion);
        
        if (ControlCategorias.validarCategoria(categoria) && ControlCategorias.agregarCategoria(categoria)) {
            JOptionPane.showMessageDialog(this, "Categoría agregada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarCategorias();
            limpiarCamposCategorias();
        } else {
            JOptionPane.showMessageDialog(this, "Error al agregar la categoría", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarCategoriaSeleccionada() {
        // VERIFICAR PERMISOS
        if (!sesion.esAdministrador()) {
            JOptionPane.showMessageDialog(this, 
                "Acceso denegado.\nSolo los Administradores pueden editar categorías.", 
                "Permiso denegado", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaSeleccionada = tblCategorias.getSelectedRow();
        
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una categoría de la tabla", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        idCategoriaSeleccionada = (int) modeloTablaCategorias.getValueAt(filaSeleccionada, 0);
        Categoria categoria = ControlCategorias.buscarCategoriaPorId(idCategoriaSeleccionada);
        
        if (categoria != null) {
            txteNombreCategoria.setText(categoria.getNombre());
            txtaDescripcionCategoria.setText(categoria.getDescripcion());
            modoEdicionCategoria = true;
            
            JOptionPane.showMessageDialog(this, 
                "Categoría cargada.\nEdite los campos y presione 'Agregar' para guardar los cambios.", 
                "Modo Edición", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void guardarCambiosCategoria() {
        if (!validarCamposCategorias()) return;
        
        String nombre = txteNombreCategoria.getText().trim();
        String descripcion = txtaDescripcionCategoria.getText().trim();
        
        Categoria categoria = new Categoria(idCategoriaSeleccionada, nombre, descripcion);
        
        if (ControlCategorias.actualizarCategoria(categoria)) {
            JOptionPane.showMessageDialog(this, "Categoría actualizada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarCategorias();
            limpiarCamposCategorias();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar la categoría", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCamposCategorias() {
        txteNombreCategoria.setText("");
        txtaDescripcionCategoria.setText("");
        modoEdicionCategoria = false;
        idCategoriaSeleccionada = -1;
    }

    private boolean validarCamposCategorias() {
        if (txteNombreCategoria.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre de la categoría", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }


    private void editarCategoria() {
        // VERIFICAR PERMISOS
        if (!sesion.esAdministrador()) {
            JOptionPane.showMessageDialog(this, 
                "Acceso denegado.\nSolo los Administradores pueden editar categorías.", 
                "Permiso denegado", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaSeleccionada = tblCategorias.getSelectedRow();
        
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una categoría de la tabla", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        idCategoriaSeleccionada = (int) modeloTablaCategorias.getValueAt(filaSeleccionada, 0);
        Categoria categoria = ControlCategorias.buscarCategoriaPorId(idCategoriaSeleccionada);
        
        if (categoria != null) {
            txteNombreCategoria.setText(categoria.getNombre());
            txtaDescripcionCategoria.setText(categoria.getDescripcion());
            modoEdicionCategoria = true;
            
            // Confirmar cambios
            int confirmacion = JOptionPane.showConfirmDialog(this, 
                "Edite los campos y presione Aceptar para guardar los cambios", 
                "Editar Categoría", 
                JOptionPane.OK_CANCEL_OPTION);
            
            if (confirmacion == JOptionPane.OK_OPTION) {
                if (!validarCamposCategorias()) return;
                
                categoria.setNombre(txteNombreCategoria.getText().trim());
                categoria.setDescripcion(txtaDescripcionCategoria.getText().trim());
                
                if (ControlCategorias.actualizarCategoria(categoria)) {
                    JOptionPane.showMessageDialog(this, "Categoría actualizada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarCategorias();
                    limpiarCamposCategorias();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar la categoría", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                limpiarCamposCategorias();
            }
        }
    }

    private void buscarCategoria() {
        String criterio = JOptionPane.showInputDialog(this, "Ingrese el nombre de la categoría a buscar:", "Buscar Categoría", JOptionPane.QUESTION_MESSAGE);
        
        if (criterio != null && !criterio.trim().isEmpty()) {
            List<Categoria> categorias = ControlCategorias.buscarCategoriasPorNombre(criterio);
            
            if (categorias.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron categorías con ese nombre", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
                cargarCategorias();
            } else {
                modeloTablaCategorias.setRowCount(0);
                
                for (Categoria c : categorias) {
                    Object[] fila = {
                        c.getIdCategoria(),
                        c.getNombre(),
                        c.getDescripcion()
                    };
                    modeloTablaCategorias.addRow(fila);
                }
            }
        }
    }

    private void eliminarCategoria() {
        // VERIFICAR PERMISOS
        if (!sesion.esAdministrador()) {
            JOptionPane.showMessageDialog(this, 
                "Acceso denegado.\nSolo los Administradores pueden eliminar categorías.", 
                "Permiso denegado", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaSeleccionada = tblCategorias.getSelectedRow();
        
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una categoría de la tabla", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idCategoria = (int) modeloTablaCategorias.getValueAt(filaSeleccionada, 0);
        String nombreCategoria = (String) modeloTablaCategorias.getValueAt(filaSeleccionada, 1);
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de eliminar la categoría '" + nombreCategoria + "'?\n" +
                "Nota: No se puede eliminar si tiene productos asociados.", 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (ControlCategorias.eliminarCategoria(idCategoria)) {
                JOptionPane.showMessageDialog(this, "Categoría eliminada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarCategorias();
                limpiarCamposCategorias();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No se puede eliminar la categoría.\nVerifique que no tenga productos asociados.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
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
        panProductos = new javax.swing.JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);

                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;

                java.awt.GradientPaint gp = new java.awt.GradientPaint(
                    0, 0, new java.awt.Color(200, 200, 200),  // Gris suave (arriba)
                    0, getHeight(), new java.awt.Color(240, 240, 240) // Gris más claro (abajo)
                );

                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        }
        ;
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProducto = new javax.swing.JTable();
        btnAgregarPR = new javax.swing.JButton();
        btnBuscarPR = new javax.swing.JButton();
        btnEditarPR = new javax.swing.JButton();
        btnEliminarPR = new javax.swing.JButton();
        panAgregaP = new javax.swing.JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);

                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;

                java.awt.GradientPaint gp = new java.awt.GradientPaint(
                    0, 0, new java.awt.Color(200, 200, 200),  // Gris suave (arriba)
                    0, getHeight(), new java.awt.Color(240, 240, 240) // Gris más claro (abajo)
                );

                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        }
        ;
        txteIdPr = new javax.swing.JTextField();
        txteNombrePr = new javax.swing.JTextField();
        txteCantidadPr = new javax.swing.JTextField();
        txtePrecioU = new javax.swing.JTextField();
        txteDescripcionPr = new javax.swing.JTextField();
        txteFechaPr = new javax.swing.JTextField();
        btnGuardarPR = new javax.swing.JButton();
        txteCategoria = new javax.swing.JTextField();
        btnCancelar = new javax.swing.JButton() {

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
                    getWidth(), 0, new java.awt.Color(255, 160, 122), // Naranja claro rojizo (derecha)
                    0, 0, new java.awt.Color(220, 60, 40)             // Rojo-naranja más fuerte (izquierda)
                );

                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                super.paintComponent(g);
            }
        }
        ;
        btnCotizacion = new javax.swing.JButton() {

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
        panProductos1 = new javax.swing.JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);

                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;

                java.awt.GradientPaint gp = new java.awt.GradientPaint(
                    0, 0, new java.awt.Color(200, 200, 200),  // Gris suave (arriba)
                    0, getHeight(), new java.awt.Color(240, 240, 240) // Gris más claro (abajo)
                );

                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        }
        ;
        lblCatgeorias = new javax.swing.JLabel();
        btnEliminarCateg = new javax.swing.JButton();
        btnEditarCateg = new javax.swing.JButton();
        btnAgregarCateg = new javax.swing.JButton();
        btnBuscarCateg = new javax.swing.JButton();
        txteNombreCategoria = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtaDescripcionCategoria = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCategorias = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(1187, 635));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 51));
        jLabel2.setText("PRODUCTOS");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(524, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(450, 450, 450))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1210, 70));

        panProductos.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tblProducto.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblProducto);

        btnAgregarPR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/btnagregarp.png"))); // NOI18N
        btnAgregarPR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarPRActionPerformed(evt);
            }
        });

        btnBuscarPR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/btnBuscar.png"))); // NOI18N
        btnBuscarPR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarPRActionPerformed(evt);
            }
        });

        btnEditarPR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/btneditar.png"))); // NOI18N
        btnEditarPR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarPRActionPerformed(evt);
            }
        });

        btnEliminarPR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/btnborrar.png"))); // NOI18N
        btnEliminarPR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarPRActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panProductosLayout = new javax.swing.GroupLayout(panProductos);
        panProductos.setLayout(panProductosLayout);
        panProductosLayout.setHorizontalGroup(
            panProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panProductosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBuscarPR, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgregarPR, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnEditarPR, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminarPR, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panProductosLayout.setVerticalGroup(
            panProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panProductosLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(panProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panProductosLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(panProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAgregarPR, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEditarPR, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnBuscarPR, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEliminarPR, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        getContentPane().add(panProductos, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 800, 260));

        panAgregaP.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txteIdPr.setBorder(javax.swing.BorderFactory.createTitledBorder("ID"));

        txteNombrePr.setBorder(javax.swing.BorderFactory.createTitledBorder("NOMBRE"));

        txteCantidadPr.setBorder(javax.swing.BorderFactory.createTitledBorder("CANTIDAD"));

        txtePrecioU.setBorder(javax.swing.BorderFactory.createTitledBorder("PRECIO UNITARIO"));

        txteDescripcionPr.setBorder(javax.swing.BorderFactory.createTitledBorder("DESCRIPCION"));

        txteFechaPr.setBorder(javax.swing.BorderFactory.createTitledBorder("FECHA"));

        btnGuardarPR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/btnguardar.png"))); // NOI18N
        btnGuardarPR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarPRActionPerformed(evt);
            }
        });

        txteCategoria.setBorder(javax.swing.BorderFactory.createTitledBorder("CATEGORIA"));

        javax.swing.GroupLayout panAgregaPLayout = new javax.swing.GroupLayout(panAgregaP);
        panAgregaP.setLayout(panAgregaPLayout);
        panAgregaPLayout.setHorizontalGroup(
            panAgregaPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panAgregaPLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(panAgregaPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panAgregaPLayout.createSequentialGroup()
                        .addComponent(txteIdPr, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txteNombrePr))
                    .addGroup(panAgregaPLayout.createSequentialGroup()
                        .addComponent(txteCantidadPr, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txteDescripcionPr))
                    .addGroup(panAgregaPLayout.createSequentialGroup()
                        .addComponent(txtePrecioU, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txteFechaPr, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(panAgregaPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panAgregaPLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(txteCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(14, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panAgregaPLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnGuardarPR, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46))))
        );
        panAgregaPLayout.setVerticalGroup(
            panAgregaPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panAgregaPLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(panAgregaPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txteIdPr, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txteNombrePr, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txteCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(panAgregaPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panAgregaPLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(panAgregaPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txteCantidadPr, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txteDescripcionPr, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28)
                        .addGroup(panAgregaPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtePrecioU, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txteFechaPr, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(8, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panAgregaPLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnGuardarPR, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41))))
        );

        getContentPane().add(panAgregaP, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 360, 540, 240));

        btnCancelar.setBackground(new java.awt.Color(0, 102, 102));
        btnCancelar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setText("CANCELAR");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });
        getContentPane().add(btnCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 470, 180, -1));

        btnVolver.setBackground(new java.awt.Color(51, 51, 255));
        btnVolver.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnVolver.setForeground(new java.awt.Color(0, 0, 0));
        btnVolver.setText("VOLVER");
        btnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverActionPerformed(evt);
            }
        });
        getContentPane().add(btnVolver, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 540, 180, -1));

        btnCotizacion.setBackground(new java.awt.Color(49, 19, 6));
        btnCotizacion.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCotizacion.setForeground(new java.awt.Color(255, 255, 255));
        btnCotizacion.setText("COTIZACION");
        btnCotizacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCotizacionActionPerformed(evt);
            }
        });
        getContentPane().add(btnCotizacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 400, 180, -1));

        panProductos1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblCatgeorias.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblCatgeorias.setText("CATEGORÍAS");

        btnEliminarCateg.setText("eliminar");
        btnEliminarCateg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarCategActionPerformed(evt);
            }
        });

        btnEditarCateg.setText("editar");
        btnEditarCateg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarCategActionPerformed(evt);
            }
        });

        btnAgregarCateg.setText("agregar / actualizar");
        btnAgregarCateg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarCategActionPerformed(evt);
            }
        });

        btnBuscarCateg.setText("buscar");
        btnBuscarCateg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarCategActionPerformed(evt);
            }
        });

        txteNombreCategoria.setBorder(javax.swing.BorderFactory.createTitledBorder("NOMBRE"));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("DESCRIPCIÓN BREVE"));

        txtaDescripcionCategoria.setColumns(20);
        txtaDescripcionCategoria.setRows(5);
        jScrollPane1.setViewportView(txtaDescripcionCategoria);

        tblCategorias.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tblCategorias);

        javax.swing.GroupLayout panProductos1Layout = new javax.swing.GroupLayout(panProductos1);
        panProductos1.setLayout(panProductos1Layout);
        panProductos1Layout.setHorizontalGroup(
            panProductos1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panProductos1Layout.createSequentialGroup()
                .addGroup(panProductos1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panProductos1Layout.createSequentialGroup()
                        .addGap(109, 109, 109)
                        .addComponent(lblCatgeorias))
                    .addGroup(panProductos1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(panProductos1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panProductos1Layout.createSequentialGroup()
                                .addGap(0, 12, Short.MAX_VALUE)
                                .addGroup(panProductos1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnBuscarCateg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnAgregarCateg, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panProductos1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnEditarCateg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnEliminarCateg, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(9, 9, 9))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panProductos1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panProductos1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(txteNombreCategoria, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        panProductos1Layout.setVerticalGroup(
            panProductos1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panProductos1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCatgeorias)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                .addGap(24, 24, 24)
                .addComponent(txteNombreCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addGroup(panProductos1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnEditarCateg, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgregarCateg))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panProductos1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnBuscarCateg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEliminarCateg))
                .addGap(18, 18, 18))
        );

        getContentPane().add(panProductos1, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 80, 360, 520));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
         limpiarCamposProductos();
        panAgregaP.setVisible(false);
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
        // TODO add your handling code here:
        dispose();
        new frmPrincipal().setVisible(true);
    }//GEN-LAST:event_btnVolverActionPerformed

    private void btnAgregarPRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarPRActionPerformed
          limpiarCamposProductos();
        panAgregaP.setVisible(true);
    }//GEN-LAST:event_btnAgregarPRActionPerformed

    private void btnBuscarPRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarPRActionPerformed
        buscarProducto();
    }//GEN-LAST:event_btnBuscarPRActionPerformed

    private void btnEditarPRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarPRActionPerformed
        
        cargarProductoSeleccionado();
    }//GEN-LAST:event_btnEditarPRActionPerformed

    private void btnEliminarPRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarPRActionPerformed
                int filaSeleccionada = tblProducto.getSelectedRow();
        
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idProducto = (int) modeloTablaProductos.getValueAt(filaSeleccionada, 0);
        String nombreProducto = (String) modeloTablaProductos.getValueAt(filaSeleccionada, 1);
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de eliminar el producto '" + nombreProducto + "'?", 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (ControlProductos.eliminarProducto(idProducto)) {
                JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarProductos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el producto", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnEliminarPRActionPerformed

    private void btnGuardarPRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarPRActionPerformed
        // TODO add your handling code here:
         guardarProducto();
    }//GEN-LAST:event_btnGuardarPRActionPerformed

    private void btnCotizacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCotizacionActionPerformed
        int filaSeleccionada = tblProducto.getSelectedRow();
        
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un producto de la tabla para cotizar", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idProducto = (int) modeloTablaProductos.getValueAt(filaSeleccionada, 0);
        Producto productoSeleccionado = ControlProductos.buscarProductoPorId(idProducto);
        
        if (productoSeleccionado != null) {
            this.dispose();
            new frmCotizaciones(productoSeleccionado).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar el producto", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCotizacionActionPerformed

    private void btnAgregarCategActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarCategActionPerformed
         agregarCategoria();
    }//GEN-LAST:event_btnAgregarCategActionPerformed

    private void btnEditarCategActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarCategActionPerformed
        cargarCategoriaSeleccionada();
    }//GEN-LAST:event_btnEditarCategActionPerformed

    private void btnBuscarCategActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarCategActionPerformed
        buscarCategoria();
    }//GEN-LAST:event_btnBuscarCategActionPerformed

    private void btnEliminarCategActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarCategActionPerformed
        eliminarCategoria();
    }//GEN-LAST:event_btnEliminarCategActionPerformed

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
            java.util.logging.Logger.getLogger(frmProducto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmProducto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmProducto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmProducto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmProducto().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarCateg;
    private javax.swing.JButton btnAgregarPR;
    private javax.swing.JButton btnBuscarCateg;
    private javax.swing.JButton btnBuscarPR;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnCotizacion;
    private javax.swing.JButton btnEditarCateg;
    private javax.swing.JButton btnEditarPR;
    private javax.swing.JButton btnEliminarCateg;
    private javax.swing.JButton btnEliminarPR;
    private javax.swing.JButton btnGuardarPR;
    private javax.swing.JButton btnVolver;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblCatgeorias;
    private javax.swing.JPanel panAgregaP;
    private javax.swing.JPanel panProductos;
    private javax.swing.JPanel panProductos1;
    private javax.swing.JTable tblCategorias;
    private javax.swing.JTable tblProducto;
    private javax.swing.JTextArea txtaDescripcionCategoria;
    private javax.swing.JTextField txteCantidadPr;
    private javax.swing.JTextField txteCategoria;
    private javax.swing.JTextField txteDescripcionPr;
    private javax.swing.JTextField txteFechaPr;
    private javax.swing.JTextField txteIdPr;
    private javax.swing.JTextField txteNombreCategoria;
    private javax.swing.JTextField txteNombrePr;
    private javax.swing.JTextField txtePrecioU;
    // End of variables declaration//GEN-END:variables
}
