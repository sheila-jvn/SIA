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

/**
 *
 * @author erickc
 */
public class MataPelajaran extends javax.swing.JFrame {

    private Connection conn;
    private DefaultTableModel tabmode;
    private String selectedMataPelajaranId;

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
     * Creates new form MataPelajaran
     */
    public MataPelajaran() {
        initComponents();
        lblSelectedMataPelajaran.setText("-");
        selectedMataPelajaranId = null;
        try {
            conn = Database.getConnection();
            loadGuruComboBox();
            loadTingkatComboBox();
            reset(); // Call reset before loadTable to ensure search field is clear if needed
            loadTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            // Disable components or close form if connection is critical
        }
    }

    private void loadGuruComboBox() {
        DefaultComboBoxModel<Item> model = new DefaultComboBoxModel<>();
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
        if (cmbGuru.getItemCount() > 0) {
            cmbGuru.setSelectedIndex(0);
        }
        if (cmbTingkat.getItemCount() > 0) {
            cmbTingkat.setSelectedIndex(0);
        }
        txtSearch.setText("");
        lblSelectedMataPelajaran.setText("-");
        selectedMataPelajaranId = null;
    }

    private void loadTable() {
        Object[] Baris = {"ID", "Nama Mata Pelajaran", "Guru", "Tingkat"};
        tabmode = new DefaultTableModel(null, Baris);
        String cariitem = txtSearch.getText();

        try {
            // Query to join mata_pelajaran with guru and tingkat to get names
            String sql = "SELECT mp.id, mp.nama AS nama_mapel, g.nama AS nama_guru, t.nama AS nama_tingkat "
                    + "FROM mata_pelajaran mp "
                    + "LEFT JOIN guru g ON mp.id_guru = g.id "
                    + "LEFT JOIN tingkat t ON mp.id_tingkat = t.id "
                    + "WHERE mp.nama LIKE ? OR g.nama LIKE ? OR t.nama LIKE ? "
                    + "ORDER BY mp.id ASC";
            PreparedStatement stat = conn.prepareStatement(sql);
            String searchTerm = "%" + cariitem + "%";
            stat.setString(1, searchTerm);
            stat.setString(2, searchTerm);
            stat.setString(3, searchTerm);
            ResultSet hasil = stat.executeQuery();

            while (hasil.next()) {
                tabmode.addRow(new Object[]{
                    hasil.getString("id"),
                    hasil.getString("nama_mapel"),
                    hasil.getString("nama_guru"), // Can be null if id_guru is null
                    hasil.getString("nama_tingkat")
                });
            }
            tblMataPelajaran.setModel(tabmode);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Data gagal dipanggil: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btnCreate = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btnDelete = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMataPelajaran = new javax.swing.JTable();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        lblSelectedMataPelajaran = new javax.swing.JLabel();
        cmbGuru = new javax.swing.JComboBox<Item>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cmbTingkat = new javax.swing.JComboBox<Item>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel2.setFont(new java.awt.Font("Noto Sans", 1, 24)); // NOI18N
        jLabel2.setText("Form Mata Pelajaran");

        jLabel1.setFont(new java.awt.Font("Noto Sans", 0, 14)); // NOI18N
        jLabel1.setText("Mata Pelajaran dipilih:");

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

        jLabel4.setFont(new java.awt.Font("Noto Sans", 0, 14)); // NOI18N
        jLabel4.setText("Nama");

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

        tblMataPelajaran.setModel(new javax.swing.table.DefaultTableModel(
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
        tblMataPelajaran.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMataPelajaranMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblMataPelajaran);

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchKeyPressed(evt);
            }
        });

        btnSearch.setText("CARI");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        lblSelectedMataPelajaran.setText("[PLACEHOLDER]");

        jLabel5.setFont(new java.awt.Font("Noto Sans", 0, 14)); // NOI18N
        jLabel5.setText("Guru");

        jLabel6.setFont(new java.awt.Font("Noto Sans", 0, 14)); // NOI18N
        jLabel6.setText("Tingkat");

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
                        .addComponent(lblSelectedMataPelajaran))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(120, 120, 120)
                        .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45)
                        .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 876, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(308, 308, 308)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(21, 21, 21)
                                .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6))
                                .addGap(34, 34, 34)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmbGuru, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmbTingkat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel2)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblSelectedMataPelajaran))
                .addGap(68, 68, 68)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jLabel4)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbGuru, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cmbTingkat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        String namaMapel = txtNama.getText();
        Item selectedGuruItem = (Item) cmbGuru.getSelectedItem();
        Item selectedTingkatItem = (Item) cmbTingkat.getSelectedItem();

        if (namaMapel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Mata Pelajaran tidak boleh kosong.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedTingkatItem == null) {
            JOptionPane.showMessageDialog(this, "Tingkat harus dipilih.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Guru can be optional (NULL in DB)

        try {
            String sql = "INSERT INTO mata_pelajaran (id_tingkat, id_guru, nama) VALUES (?, ?, ?)";
            PreparedStatement stat = conn.prepareStatement(sql);

            stat.setInt(1, selectedTingkatItem.getId());
            if (selectedGuruItem != null) {
                stat.setInt(2, selectedGuruItem.getId());
            } else {
                stat.setNull(2, java.sql.Types.INTEGER);
            }
            stat.setString(3, namaMapel);

            int rowsInserted = stat.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Data Mata Pelajaran berhasil ditambahkan!");
                resetUI();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan data Mata Pelajaran: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (selectedMataPelajaranId == null || selectedMataPelajaranId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih mata pelajaran yang akan diupdate.", "Mata Pelajaran Belum Dipilih", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String namaMapel = txtNama.getText();
        Item selectedGuruItem = (Item) cmbGuru.getSelectedItem();
        Item selectedTingkatItem = (Item) cmbTingkat.getSelectedItem();

        if (namaMapel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Mata Pelajaran tidak boleh kosong.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedTingkatItem == null) {
            JOptionPane.showMessageDialog(this, "Tingkat harus dipilih.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String sql = "UPDATE mata_pelajaran SET id_tingkat = ?, id_guru = ?, nama = ? WHERE id = ?";
            PreparedStatement stat = conn.prepareStatement(sql);

            stat.setInt(1, selectedTingkatItem.getId());
            if (selectedGuruItem != null) {
                stat.setInt(2, selectedGuruItem.getId());
            } else {
                stat.setNull(2, java.sql.Types.INTEGER);
            }
            stat.setString(3, namaMapel);
            stat.setString(4, selectedMataPelajaranId);

            int rowsUpdated = stat.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Data Mata Pelajaran berhasil diupdate!");
                resetUI();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate data. Mata Pelajaran tidak ditemukan atau data tidak berubah.", "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate data Mata Pelajaran: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if (selectedMataPelajaranId == null || selectedMataPelajaranId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih mata pelajaran yang akan dihapus.", "Mata Pelajaran Belum Dipilih", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus mata pelajaran: " + lblSelectedMataPelajaran.getText() + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            String sql = "DELETE FROM mata_pelajaran WHERE id = ?";
            PreparedStatement stat = conn.prepareStatement(sql);
            stat.setString(1, selectedMataPelajaranId);

            int rowsDeleted = stat.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Data Mata Pelajaran berhasil dihapus!");
                resetUI();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data. Mata Pelajaran tidak ditemukan.", "Hapus Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            // Check for foreign key constraint violation (e.g., SQLState "23000")
            if (e.getSQLState().startsWith("23")) { // General class for integrity constraint violation
                JOptionPane.showMessageDialog(this, "Gagal menghapus data Mata Pelajaran: Mata pelajaran ini mungkin digunakan di tabel lain (misalnya Nilai). Hapus data terkait terlebih dahulu.", "Error Hapus Data", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data Mata Pelajaran: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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

    private void tblMataPelajaranMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMataPelajaranMouseClicked
        int baris = tblMataPelajaran.getSelectedRow();
        if (baris != -1) {
            selectedMataPelajaranId = tblMataPelajaran.getValueAt(baris, 0).toString();
            String namaMapel = tblMataPelajaran.getValueAt(baris, 1).toString();
            String namaGuru = tblMataPelajaran.getValueAt(baris, 2) != null ? tblMataPelajaran.getValueAt(baris, 2).toString() : null;
            String namaTingkat = tblMataPelajaran.getValueAt(baris, 3).toString();

            txtNama.setText(namaMapel);
            lblSelectedMataPelajaran.setText(namaMapel);

            // Select Guru in ComboBox
            if (namaGuru != null) {
                for (int i = 0; i < cmbGuru.getItemCount(); i++) {
                    Item guruItem = (Item) cmbGuru.getItemAt(i);
                    if (guruItem.getDescription().equals(namaGuru)) {
                        cmbGuru.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                cmbGuru.setSelectedItem(null); // Or set to a default "None" item if you add one
            }

            // Select Tingkat in ComboBox
            for (int i = 0; i < cmbTingkat.getItemCount(); i++) {
                Item tingkatItem = (Item) cmbTingkat.getItemAt(i);
                if (tingkatItem.getDescription().equals(namaTingkat)) {
                    cmbTingkat.setSelectedIndex(i);
                    break;
                }
            }
        }
    }//GEN-LAST:event_tblMataPelajaranMouseClicked

    private void txtSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            loadTable();
        }
    }//GEN-LAST:event_txtSearchKeyPressed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        loadTable();
    }//GEN-LAST:event_btnSearchActionPerformed

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
            java.util.logging.Logger.getLogger(MataPelajaran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MataPelajaran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MataPelajaran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MataPelajaran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MataPelajaran().setVisible(true);
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
    private javax.swing.JComboBox<Item> cmbGuru;
    private javax.swing.JComboBox<Item> cmbTingkat;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblSelectedMataPelajaran;
    private javax.swing.JTable tblMataPelajaran;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
