/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package form;

import database.Database;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author erickc
 */
public class Nilai extends javax.swing.JFrame {

    private Connection conn;
    private DefaultTableModel tabmode;
    private String selectedNilaiId;

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
     * Creates new form Nilai
     */
    public Nilai() {
        initComponents();
        setLocationRelativeTo(null);
        lblSelectedNilai.setText("-");
        selectedNilaiId = null;
        try {
            conn = Database.getConnection();
            loadSiswaComboBox();
            loadMataPelajaranComboBox();
            loadKelasComboBox();
            loadTahunAjaranComboBox();
            loadJenisNilaiComboBox();
            reset();
            loadTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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

    private void loadMataPelajaranComboBox() {
        DefaultComboBoxModel<Item> model = new DefaultComboBoxModel<>();
        try {
            String sql = "SELECT id, nama FROM mata_pelajaran ORDER BY nama ASC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addElement(new Item(rs.getInt("id"), rs.getString("nama")));
            }
            cmbMataPelajaran.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data mata pelajaran: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadKelasComboBox() {
        DefaultComboBoxModel<Item> model = new DefaultComboBoxModel<>();
        try {
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

    private void loadJenisNilaiComboBox() {
        DefaultComboBoxModel<Item> model = new DefaultComboBoxModel<>();
        try {
            String sql = "SELECT id, nama FROM nilai_jenis ORDER BY nama ASC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addElement(new Item(rs.getInt("id"), rs.getString("nama")));
            }
            cmbJenisNilai.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data jenis nilai: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reset() {
        if (cmbSiswa.getItemCount() > 0) {
            cmbSiswa.setSelectedIndex(0);
        }
        if (cmbMataPelajaran.getItemCount() > 0) {
            cmbMataPelajaran.setSelectedIndex(0);
        }
        if (cmbKelas.getItemCount() > 0) {
            cmbKelas.setSelectedIndex(0);
        }
        if (cmbTahunAjaran.getItemCount() > 0) {
            cmbTahunAjaran.setSelectedIndex(0);
        }
        if (cmbJenisNilai.getItemCount() > 0) {
            cmbJenisNilai.setSelectedIndex(0);
        }

        txtNilai.setText("");
        spnTanggal.setValue(new java.util.Date()); // Current date
        txtKeterangan.setText("");
        txtSearch.setText("");
        lblSelectedNilai.setText("-");
        selectedNilaiId = null;
    }

    private void loadTable() {
        Object[] Baris = {"ID", "Siswa", "Mata Pelajaran", "Kelas", "Tahun Ajaran", "Jenis Nilai", "Nilai", "Tanggal", "Keterangan"};
        tabmode = new DefaultTableModel(null, Baris);
        String cariitem = txtSearch.getText();

        try {
            String sql = "SELECT n.id, s.nama AS nama_siswa, mp.nama AS nama_mapel, k.nama AS nama_kelas, "
                    + "ta.nama AS nama_tahun_ajaran, nj.nama AS nama_jenis_nilai, "
                    + "n.nilai, n.tanggal_penilaian, n.keterangan "
                    + "FROM nilai n "
                    + "JOIN siswa s ON n.id_siswa = s.id "
                    + "JOIN mata_pelajaran mp ON n.id_mata_pelajaran = mp.id "
                    + "JOIN kelas k ON n.id_kelas = k.id "
                    + "JOIN tahun_ajaran ta ON n.id_tahun_ajaran = ta.id "
                    + "JOIN nilai_jenis nj ON n.id_jenis_nilai = nj.id "
                    + "WHERE s.nama LIKE ? OR mp.nama LIKE ? OR k.nama LIKE ? OR nj.nama LIKE ? OR n.nilai LIKE ? "
                    + "ORDER BY n.id ASC";
            PreparedStatement stat = conn.prepareStatement(sql);
            String searchTerm = "%" + cariitem + "%";
            stat.setString(1, searchTerm);
            stat.setString(2, searchTerm);
            stat.setString(3, searchTerm);
            stat.setString(4, searchTerm);
            stat.setString(5, searchTerm);
            ResultSet hasil = stat.executeQuery();

            while (hasil.next()) {
                tabmode.addRow(new Object[]{
                    hasil.getString("id"),
                    hasil.getString("nama_siswa"),
                    hasil.getString("nama_mapel"),
                    hasil.getString("nama_kelas"),
                    hasil.getString("nama_tahun_ajaran"),
                    hasil.getString("nama_jenis_nilai"),
                    hasil.getFloat("nilai"),
                    hasil.getDate("tanggal_penilaian"),
                    hasil.getString("keterangan")
                });
            }
            tblNilai.setModel(tabmode);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Data nilai gagal dipanggil: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        cmbKelas = new javax.swing.JComboBox<>();
        lblSelectedNilai = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblNilai = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtKeterangan = new javax.swing.JTextArea();
        cmbTahunAjaran = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        btnCreate = new javax.swing.JButton();
        cmbJenisNilai = new javax.swing.JComboBox<>();
        btnDelete = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        cmbSiswa = new javax.swing.JComboBox<>();
        spnTanggal = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        cmbMataPelajaran = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        btnPrint = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txtNilai = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        getContentPane().add(cmbKelas, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 170, 249, 33));

        lblSelectedNilai.setFont(new java.awt.Font("Noto Sans", 0, 14)); // NOI18N
        lblSelectedNilai.setText("[PLACEHOLDER]");
        getContentPane().add(lblSelectedNilai, new org.netbeans.lib.awtextra.AbsoluteConstraints(117, 77, -1, 26));

        jLabel7.setText("Tanggal");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(499, 287, -1, -1));

        jLabel6.setText("Keterangan");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(499, 216, -1, -1));

        tblNilai.setModel(new javax.swing.table.DefaultTableModel(
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
        tblNilai.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblNilaiMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblNilai);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 462, 854, 346));

        jLabel5.setText("Kelas");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(499, 176, -1, -1));

        btnSearch.setText("Cari");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        getContentPane().add(btnSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(309, 411, 92, 33));

        btnReset.setText("BATAL");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });
        getContentPane().add(btnReset, new org.netbeans.lib.awtextra.AbsoluteConstraints(565, 355, 92, 33));

        txtKeterangan.setColumns(20);
        txtKeterangan.setRows(5);
        jScrollPane2.setViewportView(txtKeterangan);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 216, 249, 56));

        getContentPane().add(cmbTahunAjaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(118, 233, 243, 33));

        jLabel4.setText("Tahun Ajaran");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 239, -1, -1));

        jLabel2.setFont(new java.awt.Font("Noto Sans", 1, 24)); // NOI18N
        jLabel2.setText("Data Nilai");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 16, -1, -1));

        btnUpdate.setText("UBAH");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        getContentPane().add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(253, 355, 92, 33));

        btnExit.setText("KELUAR");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        getContentPane().add(btnExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(724, 355, 92, 33));

        jLabel10.setText("Jenis Nilai");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 184, -1, -1));

        btnCreate.setText("SIMPAN");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });
        getContentPane().add(btnCreate, new org.netbeans.lib.awtextra.AbsoluteConstraints(66, 355, 92, 33));

        getContentPane().add(cmbJenisNilai, new org.netbeans.lib.awtextra.AbsoluteConstraints(117, 178, 244, 33));

        btnDelete.setText("HAPUS");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        getContentPane().add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 355, 92, 33));

        jLabel9.setText("Siswa");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 121, -1, -1));

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchKeyPressed(evt);
            }
        });
        getContentPane().add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(31, 411, 250, 33));

        getContentPane().add(cmbSiswa, new org.netbeans.lib.awtextra.AbsoluteConstraints(117, 121, 244, 33));

        spnTanggal.setModel(new javax.swing.SpinnerDateModel());
        getContentPane().add(spnTanggal, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 281, 249, 33));

        jLabel8.setFont(new java.awt.Font("Noto Sans", 0, 14)); // NOI18N
        jLabel8.setText("Nilai dipilih:");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 77, -1, 26));

        getContentPane().add(cmbMataPelajaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 121, 249, 33));

        jLabel11.setText("Mata Pelajaran");
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(499, 127, -1, -1));

        btnPrint.setText("Print");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        getContentPane().add(btnPrint, new org.netbeans.lib.awtextra.AbsoluteConstraints(434, 411, 92, 33));

        jLabel12.setText("Nilai");
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 287, -1, -1));
        getContentPane().add(txtNilai, new org.netbeans.lib.awtextra.AbsoluteConstraints(118, 281, 243, 33));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblNilaiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblNilaiMouseClicked
        int baris = tblNilai.getSelectedRow();
        if (baris != -1) {
            selectedNilaiId = tblNilai.getValueAt(baris, 0).toString();
            String namaSiswa = tblNilai.getValueAt(baris, 1).toString();
            String namaMataPelajaran = tblNilai.getValueAt(baris, 2).toString();
            String namaKelas = tblNilai.getValueAt(baris, 3).toString();
            String namaTahunAjaran = tblNilai.getValueAt(baris, 4).toString();
            String namaJenisNilai = tblNilai.getValueAt(baris, 5).toString();
            float nilai = Float.parseFloat(tblNilai.getValueAt(baris, 6).toString());
            java.sql.Date sqlDate = (java.sql.Date) tblNilai.getValueAt(baris, 7);
            String keterangan = tblNilai.getValueAt(baris, 8) != null ? tblNilai.getValueAt(baris, 8).toString() : "";

            lblSelectedNilai.setText(namaSiswa + " - " + namaMataPelajaran + " (" + sqlDate.toString() + ")");
            txtNilai.setText(String.valueOf(nilai));
            txtKeterangan.setText(keterangan);

            if (sqlDate != null) {
                spnTanggal.setValue(new java.util.Date(sqlDate.getTime()));
            } else {
                spnTanggal.setValue(new java.util.Date());
            }

            // Select Siswa
            for (int i = 0; i < cmbSiswa.getItemCount(); i++) {
                Item item = (Item) cmbSiswa.getItemAt(i);
                if (item.getDescription().equals(namaSiswa)) {
                    cmbSiswa.setSelectedIndex(i);
                    break;
                }
            }
            // Select Mata Pelajaran
            for (int i = 0; i < cmbMataPelajaran.getItemCount(); i++) {
                Item item = (Item) cmbMataPelajaran.getItemAt(i);
                if (item.getDescription().equals(namaMataPelajaran)) {
                    cmbMataPelajaran.setSelectedIndex(i);
                    break;
                }
            }
            // Select Kelas
            for (int i = 0; i < cmbKelas.getItemCount(); i++) {
                Item item = (Item) cmbKelas.getItemAt(i);
                if (item.getDescription().equals(namaKelas)) {
                    cmbKelas.setSelectedIndex(i);
                    break;
                }
            }
            // Select Tahun Ajaran
            for (int i = 0; i < cmbTahunAjaran.getItemCount(); i++) {
                Item item = (Item) cmbTahunAjaran.getItemAt(i);
                if (item.getDescription().equals(namaTahunAjaran)) {
                    cmbTahunAjaran.setSelectedIndex(i);
                    break;
                }
            }
            // Select Jenis Nilai
            for (int i = 0; i < cmbJenisNilai.getItemCount(); i++) {
                Item item = (Item) cmbJenisNilai.getItemAt(i);
                if (item.getDescription().equals(namaJenisNilai)) {
                    cmbJenisNilai.setSelectedIndex(i);
                    break;
                }
            }
        }
    }//GEN-LAST:event_tblNilaiMouseClicked

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        loadTable();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        resetUI();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (selectedNilaiId == null || selectedNilaiId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih data nilai yang akan diupdate.", "Data Belum Dipilih", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Item selectedSiswa = (Item) cmbSiswa.getSelectedItem();
        Item selectedMataPelajaran = (Item) cmbMataPelajaran.getSelectedItem();
        Item selectedKelas = (Item) cmbKelas.getSelectedItem();
        Item selectedTahunAjaran = (Item) cmbTahunAjaran.getSelectedItem();
        Item selectedJenisNilai = (Item) cmbJenisNilai.getSelectedItem();
        java.util.Date utilDate = (java.util.Date) spnTanggal.getValue();
        String keterangan = txtKeterangan.getText();
        String nilaiStr = txtNilai.getText();

        if (selectedSiswa == null || selectedMataPelajaran == null || selectedKelas == null
                || selectedTahunAjaran == null || selectedJenisNilai == null || utilDate == null || nilaiStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field (Siswa, Mata Pelajaran, Kelas, Tahun Ajaran, Jenis Nilai, Nilai, Tanggal) harus diisi.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        float nilai;
        try {
            nilai = Float.parseFloat(nilaiStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nilai harus berupa angka.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        String sql = "UPDATE nilai SET id_siswa = ?, id_mata_pelajaran = ?, id_kelas = ?, id_tahun_ajaran = ?, id_jenis_nilai = ?, nilai = ?, tanggal_penilaian = ?, keterangan = ? WHERE id = ?";
        try {
            PreparedStatement stat = conn.prepareStatement(sql);
            stat.setInt(1, selectedSiswa.getId());
            stat.setInt(2, selectedMataPelajaran.getId());
            stat.setInt(3, selectedKelas.getId());
            stat.setInt(4, selectedTahunAjaran.getId());
            stat.setInt(5, selectedJenisNilai.getId());
            stat.setFloat(6, nilai);
            stat.setDate(7, sqlDate);
            stat.setString(8, keterangan.isEmpty() ? null : keterangan);
            stat.setInt(9, Integer.parseInt(selectedNilaiId));

            int rowsUpdated = stat.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Data nilai berhasil diupdate!");
                resetUI();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate data. Data tidak ditemukan atau tidak berubah.", "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate data nilai: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID Nilai tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        new dashboard().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        Item selectedSiswa = (Item) cmbSiswa.getSelectedItem();
        Item selectedMataPelajaran = (Item) cmbMataPelajaran.getSelectedItem();
        Item selectedKelas = (Item) cmbKelas.getSelectedItem();
        Item selectedTahunAjaran = (Item) cmbTahunAjaran.getSelectedItem();
        Item selectedJenisNilai = (Item) cmbJenisNilai.getSelectedItem();
        java.util.Date utilDate = (java.util.Date) spnTanggal.getValue();
        String keterangan = txtKeterangan.getText();
        String nilaiStr = txtNilai.getText();

        if (selectedSiswa == null || selectedMataPelajaran == null || selectedKelas == null
                || selectedTahunAjaran == null || selectedJenisNilai == null || utilDate == null || nilaiStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field (Siswa, Mata Pelajaran, Kelas, Tahun Ajaran, Jenis Nilai, Nilai, Tanggal) harus diisi.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        float nilai;
        try {
            nilai = Float.parseFloat(nilaiStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nilai harus berupa angka.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        String sql = "INSERT INTO nilai (id_siswa, id_mata_pelajaran, id_kelas, id_tahun_ajaran, id_jenis_nilai, nilai, tanggal_penilaian, keterangan) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stat = conn.prepareStatement(sql);
            stat.setInt(1, selectedSiswa.getId());
            stat.setInt(2, selectedMataPelajaran.getId());
            stat.setInt(3, selectedKelas.getId());
            stat.setInt(4, selectedTahunAjaran.getId());
            stat.setInt(5, selectedJenisNilai.getId());
            stat.setFloat(6, nilai);
            stat.setDate(7, sqlDate);
            stat.setString(8, keterangan.isEmpty() ? null : keterangan);

            int rowsInserted = stat.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Data nilai berhasil ditambahkan!");
                resetUI();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan data nilai: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if (selectedNilaiId == null || selectedNilaiId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih data nilai yang akan dihapus.", "Data Belum Dipilih", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data nilai untuk: " + lblSelectedNilai.getText() + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM nilai WHERE id = ?";
        try {
            PreparedStatement stat = conn.prepareStatement(sql);
            stat.setInt(1, Integer.parseInt(selectedNilaiId));

            int rowsDeleted = stat.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Data nilai berhasil dihapus!");
                resetUI();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data. Data tidak ditemukan.", "Hapus Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data Nilai: Data ini mungkin digunakan di tabel lain. Hapus data terkait terlebih dahulu.", "Error Hapus Data", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data nilai: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID Nilai tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void txtSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            loadTable();
        }
    }//GEN-LAST:event_txtSearchKeyPressed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        try {
            String path = "./src/form/LapNilai.jasper";
            HashMap parameter = new HashMap();
            JasperPrint print = JasperFillManager.fillReport(path, parameter, conn);
            JasperViewer.viewReport(print, false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(rootPane, "Dokumen Tidak Ada " + ex.getMessage());
            ex.printStackTrace();
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
            java.util.logging.Logger.getLogger(Nilai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Nilai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Nilai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Nilai.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Nilai().setVisible(true);
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
    private javax.swing.JComboBox<Item> cmbJenisNilai;
    private javax.swing.JComboBox<Item> cmbKelas;
    private javax.swing.JComboBox<Item> cmbMataPelajaran;
    private javax.swing.JComboBox<Item> cmbSiswa;
    private javax.swing.JComboBox<Item> cmbTahunAjaran;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblSelectedNilai;
    private javax.swing.JSpinner spnTanggal;
    private javax.swing.JTable tblNilai;
    private javax.swing.JTextArea txtKeterangan;
    private javax.swing.JTextField txtNilai;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
