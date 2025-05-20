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
import javax.swing.JSpinner;

/**
 *
 * @author sheila
 */
public class Kehadiran extends javax.swing.JFrame {

    private Connection conn;
    private DefaultTableModel tabmode;
    private String selectedKehadiranId;

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
     * Creates new form data_absensi
     */
    public Kehadiran() {
        initComponents();
        lblSelectedAbsensi.setText("-");
        selectedKehadiranId = null;
        spnTanggal.setEditor(new JSpinner.DateEditor(spnTanggal, "yyyy-MM-dd")); // Set date format

        try {
            conn = Database.getConnection();
            loadSiswaComboBox();
            loadKelasComboBox();
            loadTahunAjaranComboBox();
            loadStatusComboBox();
            resetFormFields();
            loadTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            // Disable components or close form if connection is critical
        }
    }

    private void loadSiswaComboBox() {
        DefaultComboBoxModel<Item> model = new DefaultComboBoxModel<>();
        try {
            String sql = "SELECT id, nama FROM siswa ORDER BY nama ASC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addElement(new Item(rs.getInt("id"), rs.getString("nama")));
            }
            cmbSiswa.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data siswa: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadKelasComboBox() {
        DefaultComboBoxModel<Item> model = new DefaultComboBoxModel<>();
        try {
            // Assuming kelas has a 'nama' field for display
            String sql = "SELECT id, nama FROM kelas ORDER BY nama ASC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addElement(new Item(rs.getInt("id"), rs.getString("nama")));
            }
            cmbKelas.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data kelas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void loadStatusComboBox() {
        DefaultComboBoxModel<Item> model = new DefaultComboBoxModel<>();
        try {
            String sql = "SELECT id, nama FROM kehadiran_status ORDER BY nama ASC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addElement(new Item(rs.getInt("id"), rs.getString("nama")));
            }
            cmbStatus.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data status kehadiran: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFormFields() {
        if (cmbSiswa.getItemCount() > 0) {
            cmbSiswa.setSelectedIndex(0);
        }
        if (cmbKelas.getItemCount() > 0) {
            cmbKelas.setSelectedIndex(0);
        }
        if (cmbTahunAjaran.getItemCount() > 0) {
            cmbTahunAjaran.setSelectedIndex(0);
        }
        if (cmbStatus.getItemCount() > 0) {
            cmbStatus.setSelectedIndex(0);
        }
        txtKeterangan.setText("");
        spnTanggal.setValue(new java.util.Date());
        txtSearch.setText("");
        lblSelectedAbsensi.setText("-");
        selectedKehadiranId = null;
    }

    private void resetUI() {
        loadTable();
        resetFormFields();
    }

    protected void loadTable() {
        Object[] Baris = {"ID", "Siswa", "Kelas", "Tahun Ajaran", "Status", "Tanggal", "Keterangan"};
        tabmode = new DefaultTableModel(null, Baris);
        String cariitem = txtSearch.getText();

        try {
            String sql = "SELECT k.id, s.nama AS nama_siswa, kl.nama AS nama_kelas, ta.nama AS nama_tahun_ajaran, ks.nama AS nama_status, k.tanggal, k.keterangan "
                    + "FROM kehadiran k "
                    + "JOIN siswa s ON k.id_siswa = s.id "
                    + "JOIN kelas kl ON k.id_kelas = kl.id "
                    + "JOIN tahun_ajaran ta ON k.id_tahun_ajaran = ta.id "
                    + "JOIN kehadiran_status ks ON k.id_status = ks.id "
                    + "WHERE s.nama LIKE ? OR kl.nama LIKE ? OR ta.nama LIKE ? OR ks.nama LIKE ? "
                    + "ORDER BY k.tanggal DESC, s.nama ASC";
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
                    hasil.getString("nama_siswa"),
                    hasil.getString("nama_kelas"),
                    hasil.getString("nama_tahun_ajaran"),
                    hasil.getString("nama_status"),
                    hasil.getDate("tanggal"),
                    hasil.getString("keterangan")
                });
            }
            tblKehadiran.setModel(tabmode);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Data gagal dipanggil: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cmbKelas = new javax.swing.JComboBox<>();
        spnTanggal = new javax.swing.JSpinner();
        txtSearch = new javax.swing.JTextField();
        btnDelete = new javax.swing.JButton();
        btnCreate = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKehadiran = new javax.swing.JTable();
        lblSelectedAbsensi = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cmbSiswa = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        cmbTahunAjaran = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtKeterangan = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Noto Sans", 1, 24)); // NOI18N
        jLabel2.setText("Data Kehadiran");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 16, -1, -1));

        jLabel4.setText("Tahun Ajaran");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, -1, -1));

        jLabel5.setText("Kelas");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(465, 92, -1, -1));

        jLabel6.setText("Keterangan");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(465, 134, -1, -1));

        jLabel7.setText("Tanggal");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(465, 242, -1, -1));

        getContentPane().add(cmbKelas, new org.netbeans.lib.awtextra.AbsoluteConstraints(571, 86, 249, 33));

        spnTanggal.setModel(new javax.swing.SpinnerDateModel());
        getContentPane().add(spnTanggal, new org.netbeans.lib.awtextra.AbsoluteConstraints(571, 236, 249, 33));

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchKeyPressed(evt);
            }
        });
        getContentPane().add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(31, 362, 250, 33));

        btnDelete.setText("HAPUS");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        getContentPane().add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(383, 306, 92, 33));

        btnCreate.setText("SIMPAN");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });
        getContentPane().add(btnCreate, new org.netbeans.lib.awtextra.AbsoluteConstraints(66, 306, 92, 33));

        btnExit.setText("KELUAR");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        getContentPane().add(btnExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(697, 306, 92, 33));

        btnUpdate.setText("UBAH");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        getContentPane().add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(226, 306, 92, 33));

        btnReset.setText("BATAL");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });
        getContentPane().add(btnReset, new org.netbeans.lib.awtextra.AbsoluteConstraints(538, 306, 92, 33));

        btnSearch.setText("Cari");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        getContentPane().add(btnSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(309, 362, 92, 33));

        tblKehadiran.setModel(new javax.swing.table.DefaultTableModel(
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
        tblKehadiran.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblKehadiranMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblKehadiran);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 413, 828, 346));

        lblSelectedAbsensi.setFont(new java.awt.Font("Noto Sans", 0, 14)); // NOI18N
        lblSelectedAbsensi.setText("[PLACEHOLDER]");
        getContentPane().add(lblSelectedAbsensi, new org.netbeans.lib.awtextra.AbsoluteConstraints(123, 89, -1, 26));

        jLabel8.setFont(new java.awt.Font("Noto Sans", 0, 14)); // NOI18N
        jLabel8.setText("Absensi dipilih:");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 89, -1, 26));

        getContentPane().add(cmbSiswa, new org.netbeans.lib.awtextra.AbsoluteConstraints(134, 128, 244, 33));

        jLabel9.setText("Siswa");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 134, -1, -1));

        getContentPane().add(cmbStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(134, 185, 244, 33));

        jLabel10.setText("Status");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, -1, -1));

        getContentPane().add(cmbTahunAjaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(134, 236, 244, 33));

        txtKeterangan.setColumns(20);
        txtKeterangan.setRows(5);
        jScrollPane2.setViewportView(txtKeterangan);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(571, 128, 250, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        Item selectedSiswa = (Item) cmbSiswa.getSelectedItem();
        Item selectedKelas = (Item) cmbKelas.getSelectedItem();
        Item selectedTahunAjaran = (Item) cmbTahunAjaran.getSelectedItem();
        Item selectedStatus = (Item) cmbStatus.getSelectedItem();
        java.util.Date utilDate = (java.util.Date) spnTanggal.getValue();
        String keterangan = txtKeterangan.getText();

        if (selectedSiswa == null || selectedKelas == null || selectedTahunAjaran == null || selectedStatus == null || utilDate == null) {
            JOptionPane.showMessageDialog(this, "Semua field yang wajib (Siswa, Kelas, Tahun Ajaran, Status, Tanggal) harus diisi.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        String sql = "INSERT INTO kehadiran (id_siswa, id_kelas, id_tahun_ajaran, id_status, tanggal, keterangan) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stat = conn.prepareStatement(sql);
            stat.setInt(1, selectedSiswa.getId());
            stat.setInt(2, selectedKelas.getId());
            stat.setInt(3, selectedTahunAjaran.getId());
            stat.setInt(4, selectedStatus.getId());
            stat.setDate(5, sqlDate);
            stat.setString(6, keterangan.isEmpty() ? null : keterangan);

            int rowsInserted = stat.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Data kehadiran berhasil ditambahkan!");
                resetUI();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan data kehadiran: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (selectedKehadiranId == null || selectedKehadiranId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih data kehadiran yang akan diupdate.", "Data Belum Dipilih", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Item selectedSiswa = (Item) cmbSiswa.getSelectedItem();
        Item selectedKelas = (Item) cmbKelas.getSelectedItem();
        Item selectedTahunAjaran = (Item) cmbTahunAjaran.getSelectedItem();
        Item selectedStatus = (Item) cmbStatus.getSelectedItem();
        java.util.Date utilDate = (java.util.Date) spnTanggal.getValue();
        String keterangan = txtKeterangan.getText();

        if (selectedSiswa == null || selectedKelas == null || selectedTahunAjaran == null || selectedStatus == null || utilDate == null) {
            JOptionPane.showMessageDialog(this, "Semua field yang wajib (Siswa, Kelas, Tahun Ajaran, Status, Tanggal) harus diisi.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        String sql = "UPDATE kehadiran SET id_siswa = ?, id_kelas = ?, id_tahun_ajaran = ?, id_status = ?, tanggal = ?, keterangan = ? WHERE id = ?";
        try {
            PreparedStatement stat = conn.prepareStatement(sql);
            stat.setInt(1, selectedSiswa.getId());
            stat.setInt(2, selectedKelas.getId());
            stat.setInt(3, selectedTahunAjaran.getId());
            stat.setInt(4, selectedStatus.getId());
            stat.setDate(5, sqlDate);
            stat.setString(6, keterangan.isEmpty() ? null : keterangan);
            stat.setInt(7, Integer.parseInt(selectedKehadiranId));

            int rowsUpdated = stat.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Data kehadiran berhasil diupdate!");
                resetUI();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate data. Data tidak ditemukan atau tidak berubah.", "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate data kehadiran: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID Kehadiran tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if (selectedKehadiranId == null || selectedKehadiranId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih data kehadiran yang akan dihapus.", "Data Belum Dipilih", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data kehadiran untuk: " + lblSelectedAbsensi.getText() + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM kehadiran WHERE id = ?";
        try {
            PreparedStatement stat = conn.prepareStatement(sql);
            stat.setInt(1, Integer.parseInt(selectedKehadiranId));

            int rowsDeleted = stat.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Data kehadiran berhasil dihapus!");
                resetUI();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data. Data tidak ditemukan.", "Hapus Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus data kehadiran: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID Kehadiran tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        resetUI();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // Assuming 'dashboard' is the name of your main menu or dashboard JFrame class
        new dashboard().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        loadTable();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void txtSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            loadTable();
        }
    }//GEN-LAST:event_txtSearchKeyPressed

    private void tblKehadiranMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblKehadiranMouseClicked
        int baris = tblKehadiran.getSelectedRow();
        if (baris != -1) {
            selectedKehadiranId = tblKehadiran.getValueAt(baris, 0).toString();
            String namaSiswa = tblKehadiran.getValueAt(baris, 1).toString();
            String namaKelas = tblKehadiran.getValueAt(baris, 2).toString();
            String namaTahunAjaran = tblKehadiran.getValueAt(baris, 3).toString();
            String namaStatus = tblKehadiran.getValueAt(baris, 4).toString();
            java.sql.Date sqlDate = (java.sql.Date) tblKehadiran.getValueAt(baris, 5);
            String keterangan = tblKehadiran.getValueAt(baris, 6) != null ? tblKehadiran.getValueAt(baris, 6).toString() : "";

            lblSelectedAbsensi.setText(namaSiswa + " (" + sqlDate.toString() + ")");
            txtKeterangan.setText(keterangan);
            if (sqlDate != null) {
                spnTanggal.setValue(new java.util.Date(sqlDate.getTime()));
            } else {
                spnTanggal.setValue(new java.util.Date());
            }

            // Select Siswa in ComboBox
            for (int i = 0; i < cmbSiswa.getItemCount(); i++) {
                Item siswaItem = (Item) cmbSiswa.getItemAt(i);
                if (siswaItem.getDescription().equals(namaSiswa)) {
                    cmbSiswa.setSelectedIndex(i);
                    break;
                }
            }
            // Select Kelas in ComboBox
            for (int i = 0; i < cmbKelas.getItemCount(); i++) {
                Item kelasItem = (Item) cmbKelas.getItemAt(i);
                if (kelasItem.getDescription().equals(namaKelas)) {
                    cmbKelas.setSelectedIndex(i);
                    break;
                }
            }
            // Select Tahun Ajaran in ComboBox
            for (int i = 0; i < cmbTahunAjaran.getItemCount(); i++) {
                Item taItem = (Item) cmbTahunAjaran.getItemAt(i);
                if (taItem.getDescription().equals(namaTahunAjaran)) {
                    cmbTahunAjaran.setSelectedIndex(i);
                    break;
                }
            }
            // Select Status in ComboBox
            for (int i = 0; i < cmbStatus.getItemCount(); i++) {
                Item statusItem = (Item) cmbStatus.getItemAt(i);
                if (statusItem.getDescription().equals(namaStatus)) {
                    cmbStatus.setSelectedIndex(i);
                    break;
                }
            }
        }
    }//GEN-LAST:event_tblKehadiranMouseClicked

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
            java.util.logging.Logger.getLogger(Kehadiran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Kehadiran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Kehadiran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Kehadiran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Kehadiran().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<Item> cmbKelas;
    private javax.swing.JComboBox<Item> cmbSiswa;
    private javax.swing.JComboBox<Item> cmbStatus;
    private javax.swing.JComboBox<Item> cmbTahunAjaran;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblSelectedAbsensi;
    private javax.swing.JSpinner spnTanggal;
    private javax.swing.JTable tblKehadiran;
    private javax.swing.JTextArea txtKeterangan;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
