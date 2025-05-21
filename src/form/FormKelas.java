/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package form;

import database.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author erickc
 */
public class FormKelas extends javax.swing.JFrame {

    private Connection conn;
    private DefaultTableModel tabmode;
    private String selectedKelasId;

    // Helper class for JComboBox items
    private static class Item {

        private int id;
        private String description;

        public Item(int id, String description) {
            this.id = id;
            this.description = description;
        }

        public int getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return description; // This is what JComboBox displays
        }

        // Override equals to help JComboBox select the correct item
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Item item = (Item) obj;
            return id == item.id && description.equals(item.description);
        }

        @Override
        public int hashCode() {
            int result = Integer.hashCode(id);
            result = 31 * result + description.hashCode();
            return result;
        }
    }

    /**
     * Creates new form Kelas
     */
    public FormKelas() {
        initComponents();
        setLocationRelativeTo(null);
        lblSelectedKelas.setText("-");
        selectedKelasId = null;
        try {
            conn = Database.getConnection();
            loadTahunAjaranComboBox();
            loadGuruComboBox();
            loadTingkatComboBox();
            reset(); // Call reset before loadTable to ensure search field is clear if needed
            loadTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            // Disable components or close form if connection is critical
        }
    }

    private void loadTahunAjaranComboBox() {
        DefaultComboBoxModel<Item> model = new DefaultComboBoxModel<>();
        try {
            String sql = "SELECT id, nama FROM tahun_ajaran ORDER BY nama ASC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addElement(new Item(rs.getInt("id"), rs.getString("nama")));
            }
            cmbTahunAjaran.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data tahun ajaran: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadGuruComboBox() {
        DefaultComboBoxModel<Item> model = new DefaultComboBoxModel<>();
        model.addElement(null); // Add a null item for optional selection
        try {
            String sql = "SELECT id, nama FROM guru ORDER BY nama ASC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addElement(new Item(rs.getInt("id"), rs.getString("nama")));
            }
            cmbGuru.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data guru: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTingkatComboBox() {
        DefaultComboBoxModel<Item> model = new DefaultComboBoxModel<>();
        try {
            String sql = "SELECT id, nama FROM tingkat ORDER BY nama ASC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addElement(new Item(rs.getInt("id"), rs.getString("nama")));
            }
            cmbTingkat.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data tingkat: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reset() {
        txtNama.setText("");
        if (cmbTahunAjaran.getItemCount() > 0) {
            cmbTahunAjaran.setSelectedIndex(0);
        }
        if (cmbGuru.getItemCount() > 0) {
            cmbGuru.setSelectedIndex(0); // Select the null item if present, or first actual guru
        }
        if (cmbTingkat.getItemCount() > 0) {
            cmbTingkat.setSelectedIndex(0);
        }
        txtSearch.setText("");
        lblSelectedKelas.setText("-");
        selectedKelasId = null;
    }

    private void loadTable() {
        Object[] Baris = {"ID", "Nama Kelas", "Tahun Ajaran", "Wali Kelas", "Tingkat"};
        tabmode = new DefaultTableModel(null, Baris);
        String cariitem = txtSearch.getText();

        try {
            String sql = "SELECT k.id, k.nama AS nama_kelas, ta.nama AS nama_tahun_ajaran, "
                    + "g.nama AS nama_guru_wali, t.nama AS nama_tingkat "
                    + "FROM kelas k "
                    + "JOIN tahun_ajaran ta ON k.id_tahun_ajaran = ta.id "
                    + "JOIN tingkat t ON k.id_tingkat = t.id "
                    + "LEFT JOIN guru g ON k.id_guru_wali = g.id " // LEFT JOIN for optional wali kelas
                    + "WHERE k.nama LIKE ? OR ta.nama LIKE ? OR g.nama LIKE ? OR t.nama LIKE ? "
                    + "ORDER BY k.id ASC";
            PreparedStatement stat = conn.prepareStatement(sql);
            String searchTerm = "%" + cariitem + "%";
            stat.setString(1, searchTerm);
            stat.setString(2, searchTerm);
            stat.setString(3, searchTerm);
            stat.setString(4, searchTerm);
            ResultSet hasil = stat.executeQuery();

            while (hasil.next()) {
                tabmode.addRow(new Object[]{
                    hasil.getString("id"),
                    hasil.getString("nama_kelas"),
                    hasil.getString("nama_tahun_ajaran"),
                    hasil.getString("nama_guru_wali"), // Can be null
                    hasil.getString("nama_tingkat")
                });
            }
            tblKelas.setModel(tabmode);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Data kelas gagal dipanggil: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetUI() {
        loadTable();
        reset();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtNama = new javax.swing.JTextField();
        txtSearch = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        lblSelectedKelas = new javax.swing.JLabel();
        btnCreate = new javax.swing.JButton();
        cmbGuru = new javax.swing.JComboBox<>();
        btnUpdate = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnDelete = new javax.swing.JButton();
        cmbTingkat = new javax.swing.JComboBox<>();
        btnReset = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKelas = new javax.swing.JTable();
        cmbTahunAjaran = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        btnPrint = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchKeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Noto Sans", 1, 24)); // NOI18N
        jLabel2.setText("Form Kelas");

        btnSearch.setText("CARI");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Noto Sans", 0, 14)); // NOI18N
        jLabel1.setText("Kelas dipilih:");

        lblSelectedKelas.setText("[PLACEHOLDER]");

        btnCreate.setText("SIMPAN");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        btnUpdate.setText("UBAH");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Noto Sans", 0, 14)); // NOI18N
        jLabel5.setText("Guru");

        jLabel4.setFont(new java.awt.Font("Noto Sans", 0, 14)); // NOI18N
        jLabel4.setText("Nama");

        jLabel6.setFont(new java.awt.Font("Noto Sans", 0, 14)); // NOI18N
        jLabel6.setText("Tingkat");

        btnDelete.setText("HAPUS");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnReset.setText("BATAL");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        btnExit.setText("KELUAR");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        tblKelas.setModel(new javax.swing.table.DefaultTableModel(
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
        tblKelas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblKelasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblKelas);

        jLabel7.setFont(new java.awt.Font("Noto Sans", 0, 14)); // NOI18N
        jLabel7.setText("Tahun Ajaran");

        btnPrint.setText("PRINT");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSelectedKelas))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(21, 21, 21)
                                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel5)
                                            .addComponent(jLabel6)
                                            .addComponent(jLabel7))
                                        .addGap(34, 34, 34)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cmbTahunAjaran, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(cmbGuru, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(cmbTingkat, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(84, 84, 84)
                                        .addComponent(txtNama)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(120, 120, 120)
                                .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(59, 59, 59)
                                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(68, 68, 68)
                                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(65, 65, 65)
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59)
                        .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(119, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(392, 392, 392))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblSelectedKelas))
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cmbTahunAjaran, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbGuru, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cmbTingkat, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            loadTable();
        }
    }//GEN-LAST:event_txtSearchKeyPressed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        loadTable();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        String namaKelas = txtNama.getText();
        Item selectedTahunAjaranItem = (Item) cmbTahunAjaran.getSelectedItem();
        Item selectedGuruWaliItem = (Item) cmbGuru.getSelectedItem(); // Wali kelas can be null
        Item selectedTingkatItem = (Item) cmbTingkat.getSelectedItem();

        if (namaKelas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Kelas tidak boleh kosong.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedTahunAjaranItem == null) {
            JOptionPane.showMessageDialog(this, "Tahun Ajaran harus dipilih.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedTingkatItem == null) {
            JOptionPane.showMessageDialog(this, "Tingkat harus dipilih.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String sql = "INSERT INTO kelas (id_tahun_ajaran, id_tingkat, id_guru_wali, nama) VALUES (?, ?, ?, ?)";
            PreparedStatement stat = conn.prepareStatement(sql);

            stat.setInt(1, selectedTahunAjaranItem.getId());
            stat.setInt(2, selectedTingkatItem.getId());
            if (selectedGuruWaliItem != null) {
                stat.setInt(3, selectedGuruWaliItem.getId());
            } else {
                stat.setNull(3, java.sql.Types.INTEGER);
            }
            stat.setString(4, namaKelas);

            int rowsInserted = stat.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Data Kelas berhasil ditambahkan!");
                resetUI();
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) { // Check for unique constraint violation (e.g., guru_wali)
                JOptionPane.showMessageDialog(this, "Gagal menambahkan data Kelas: " + e.getMessage() + ". Pastikan Guru Wali tidak duplikat.", "Database Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan data Kelas: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (selectedKelasId == null || selectedKelasId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih kelas yang akan diupdate.", "Kelas Belum Dipilih", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String namaKelas = txtNama.getText();
        Item selectedTahunAjaranItem = (Item) cmbTahunAjaran.getSelectedItem();
        Item selectedGuruWaliItem = (Item) cmbGuru.getSelectedItem(); // Wali kelas can be null
        Item selectedTingkatItem = (Item) cmbTingkat.getSelectedItem();

        if (namaKelas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Kelas tidak boleh kosong.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedTahunAjaranItem == null) {
            JOptionPane.showMessageDialog(this, "Tahun Ajaran harus dipilih.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedTingkatItem == null) {
            JOptionPane.showMessageDialog(this, "Tingkat harus dipilih.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String sql = "UPDATE kelas SET id_tahun_ajaran = ?, id_tingkat = ?, id_guru_wali = ?, nama = ? WHERE id = ?";
            PreparedStatement stat = conn.prepareStatement(sql);

            stat.setInt(1, selectedTahunAjaranItem.getId());
            stat.setInt(2, selectedTingkatItem.getId());
            if (selectedGuruWaliItem != null) {
                stat.setInt(3, selectedGuruWaliItem.getId());
            } else {
                stat.setNull(3, java.sql.Types.INTEGER);
            }
            stat.setString(4, namaKelas);
            stat.setString(5, selectedKelasId);

            int rowsUpdated = stat.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Data Kelas berhasil diupdate!");
                resetUI();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate data. Kelas tidak ditemukan atau data tidak berubah.", "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) { // Check for unique constraint violation (e.g., guru_wali)
                JOptionPane.showMessageDialog(this, "Gagal mengupdate data Kelas: " + e.getMessage() + ". Pastikan Guru Wali tidak duplikat.", "Database Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate data Kelas: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if (selectedKelasId == null || selectedKelasId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih kelas yang akan dihapus.", "Kelas Belum Dipilih", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus kelas: " + lblSelectedKelas.getText() + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            String sql = "DELETE FROM kelas WHERE id = ?";
            PreparedStatement stat = conn.prepareStatement(sql);
            stat.setString(1, selectedKelasId);

            int rowsDeleted = stat.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Data Kelas berhasil dihapus!");
                resetUI();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data. Kelas tidak ditemukan.", "Hapus Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data Kelas: Kelas ini mungkin digunakan di tabel lain (misalnya Siswa, Kehadiran, Nilai). Hapus data terkait terlebih dahulu.", "Error Hapus Data", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data Kelas: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        resetUI();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // Assuming 'dashboard' is the name of your main menu or dashboard JFrame class
        // Replace 'dashboard.class' with your actual dashboard class if different
        // Example: new MainMenu().setVisible(true);
        // For now, using a placeholder if 'dashboard' class is not defined or accessible here.
        // You might need to import 'dashboard' class.
        // Example:
        // dashboard mainDashboard = new dashboard();
        // mainDashboard.setVisible(true);
        // this.dispose();
        // Fallback to just disposing if dashboard class is an issue for this context
        // For the provided structure, it seems 'dashboard' is a known class.
        // If 'dashboard' is in the same package, no import needed.
        // If 'dashboard' is in another package, e.g., 'mainmenu.dashboard', then import it.
        new dashboard().setVisible(true); // Assuming dashboard is a class in the same or accessible package
        this.dispose();
    }//GEN-LAST:event_btnExitActionPerformed

    private void tblKelasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblKelasMouseClicked
        int baris = tblKelas.getSelectedRow();
        if (baris != -1) {
            selectedKelasId = tblKelas.getValueAt(baris, 0).toString();
            String namaKelas = tblKelas.getValueAt(baris, 1).toString();
            String namaTahunAjaran = tblKelas.getValueAt(baris, 2) != null ? tblKelas.getValueAt(baris, 2).toString() : null;
            String namaGuruWali = tblKelas.getValueAt(baris, 3) != null ? tblKelas.getValueAt(baris, 3).toString() : null;
            String namaTingkat = tblKelas.getValueAt(baris, 4) != null ? tblKelas.getValueAt(baris, 4).toString() : null;

            txtNama.setText(namaKelas);
            lblSelectedKelas.setText(namaKelas);

            // Select Tahun Ajaran in ComboBox
            if (namaTahunAjaran != null) {
                for (int i = 0; i < cmbTahunAjaran.getItemCount(); i++) {
                    Item item = (Item) cmbTahunAjaran.getItemAt(i);
                    if (item.getDescription().equals(namaTahunAjaran)) {
                        cmbTahunAjaran.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                cmbTahunAjaran.setSelectedItem(null);
            }

            // Select Guru Wali in ComboBox
            if (namaGuruWali != null) {
                boolean found = false;
                for (int i = 0; i < cmbGuru.getItemCount(); i++) {
                    Item item = (Item) cmbGuru.getItemAt(i);
                    if (item != null && item.getDescription().equals(namaGuruWali)) {
                        cmbGuru.setSelectedIndex(i);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    cmbGuru.setSelectedItem(null); // If not found, select the null/default item
                }
            } else {
                cmbGuru.setSelectedItem(null); // Select the null/default item if no guru wali
            }

            // Select Tingkat in ComboBox
            if (namaTingkat != null) {
                for (int i = 0; i < cmbTingkat.getItemCount(); i++) {
                    Item item = (Item) cmbTingkat.getItemAt(i);
                    if (item.getDescription().equals(namaTingkat)) {
                        cmbTingkat.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                cmbTingkat.setSelectedItem(null);
            }
        }
    }//GEN-LAST:event_tblKelasMouseClicked

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        try {
            String path = "./src/form/LapKelas.jasper";
            HashMap parameter = new HashMap();
            JasperPrint print = JasperFillManager.fillReport(path, parameter,conn);
            JasperViewer.viewReport(print, false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(rootPane, "Dokumen Tidak Ada " + ex.getMessage());
        }
    }//GEN-LAST:event_btnPrintActionPerformed

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
            java.util.logging.Logger.getLogger(FormKelas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormKelas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormKelas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormKelas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormKelas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<Item> cmbGuru;
    private javax.swing.JComboBox<Item> cmbTahunAjaran;
    private javax.swing.JComboBox<Item> cmbTingkat;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblSelectedKelas;
    private javax.swing.JTable tblKelas;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
