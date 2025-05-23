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
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author erickc
 */
public class TahunAjaran extends javax.swing.JFrame {

    private Connection conn;
    private DefaultTableModel tableMode;
    private String selectedTahunAjaranId;

    /**
     * Creates new form TahunAjaran
     */
    public TahunAjaran() {
        initComponents();
        lblSelectedTahunAjar.setText("-"); // Set to empty initially
        // Set spinner to display only year
        spnTahunMulai.setEditor(new JSpinner.DateEditor(spnTahunMulai, "yyyy"));
        spnTahunSelesai.setEditor(new JSpinner.DateEditor(spnTahunSelesai, "yyyy"));
        try {
            conn = Database.getConnection();
            loadTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            // Optionally, disable UI components or close the form if connection is critical
        }
    }

    private void loadTable() {
        Object[] columns = {"ID", "Nama", "Tahun Mulai", "Tahun Selesai"};
        tableMode = new DefaultTableModel(null, columns);

        try {
            String sql = "SELECT id, nama, tahun_mulai, tahun_selesai FROM tahun_ajaran ORDER BY id ASC";
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(sql);

            while (result.next()) {
                tableMode.addRow(new Object[]{
                    result.getString("id"),
                    result.getString("nama"),
                    result.getInt("tahun_mulai"),
                    result.getInt("tahun_selesai")
                });
            }
            tblTahunAjaran.setModel(tableMode);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Data gagal dipanggil: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFormFields() {
        txtNama.setText("");
        // Set spinners to current year
        Calendar cal = Calendar.getInstance();
        spnTahunMulai.setValue(cal.getTime());
        cal.add(Calendar.YEAR, 1); // Default end year to start year + 1
        spnTahunSelesai.setValue(cal.getTime());
    }

    private void resetUI() {
        loadTable(); // Refresh the table
        resetFormFields(); // Clear input fields
        lblSelectedTahunAjar.setText("-"); // Reset selected label
        selectedTahunAjaranId = null; // Clear selected ID
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCreate = new javax.swing.JButton();
        _lblUserDiplih = new javax.swing.JLabel();
        lblSelectedTahunAjar = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTahunAjaran = new javax.swing.JTable();
        btnReset = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        lblUsername = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        spnTahunMulai = new javax.swing.JSpinner();
        lblUsername1 = new javax.swing.JLabel();
        lblUsername2 = new javax.swing.JLabel();
        spnTahunSelesai = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnCreate.setText("Create");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        _lblUserDiplih.setText("Tahun Ajaran dipilih");

        lblSelectedTahunAjar.setText("[PLACEHOLDER]");

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        tblTahunAjaran.setModel(new javax.swing.table.DefaultTableModel(
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
        tblTahunAjaran.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTahunAjaranMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblTahunAjaran);

        btnReset.setText("Reset");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        btnExit.setText("Exit");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        lblUsername.setText("Nama");

        spnTahunMulai.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.YEAR));

        lblUsername1.setText("Tahun Mulai");

        lblUsername2.setText("Tahun Selesai");

        spnTahunSelesai.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.YEAR));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 686, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(_lblUserDiplih)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblSelectedTahunAjar)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtNama)
                                        .addComponent(spnTahunMulai, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                                        .addComponent(spnTahunSelesai, javax.swing.GroupLayout.Alignment.TRAILING))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(lblUsername2)
                                        .addComponent(lblUsername, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblUsername1, javax.swing.GroupLayout.Alignment.LEADING))
                                    .addComponent(btnCreate)
                                    .addComponent(btnExit))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnUpdate)
                                        .addGap(51, 51, 51)
                                        .addComponent(btnDelete))
                                    .addComponent(btnReset))))))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(_lblUserDiplih)
                    .addComponent(lblSelectedTahunAjar))
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUsername))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnTahunMulai, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUsername1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spnTahunSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUsername2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(64, 64, 64))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        String nama = txtNama.getText();
        Date tahunMulaiDate = (Date) spnTahunMulai.getValue();
        Date tahunSelesaiDate = (Date) spnTahunSelesai.getValue();

        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Tahun Ajaran cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(tahunMulaiDate);
        int tahunMulai = cal.get(Calendar.YEAR);
        cal.setTime(tahunSelesaiDate);
        int tahunSelesai = cal.get(Calendar.YEAR);

        if (tahunMulai >= tahunSelesai) {
            JOptionPane.showMessageDialog(this, "Tahun Mulai must be less than Tahun Selesai", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String sql = "INSERT INTO tahun_ajaran (nama, tahun_mulai, tahun_selesai) VALUES (?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, nama);
            statement.setInt(2, tahunMulai);
            statement.setInt(3, tahunSelesai);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Tahun Ajaran added successfully!");
                resetUI(); // Refresh the table and clear input fields
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to add Tahun Ajaran: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (selectedTahunAjaranId == null || selectedTahunAjaranId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a Tahun Ajaran to update.", "No Tahun Ajaran Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nama = txtNama.getText();
        Date tahunMulaiDate = (Date) spnTahunMulai.getValue();
        Date tahunSelesaiDate = (Date) spnTahunSelesai.getValue();

        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Tahun Ajaran cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(tahunMulaiDate);
        int tahunMulai = cal.get(Calendar.YEAR);
        cal.setTime(tahunSelesaiDate);
        int tahunSelesai = cal.get(Calendar.YEAR);

        if (tahunMulai >= tahunSelesai) {
            JOptionPane.showMessageDialog(this, "Tahun Mulai must be less than Tahun Selesai", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String sql = "UPDATE tahun_ajaran SET nama = ?, tahun_mulai = ?, tahun_selesai = ? WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, nama);
            statement.setInt(2, tahunMulai);
            statement.setInt(3, tahunSelesai);
            statement.setString(4, selectedTahunAjaranId);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Tahun Ajaran updated successfully!");
                resetUI();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update Tahun Ajaran. Data not found or unchanged.", "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update Tahun Ajaran: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if (selectedTahunAjaranId == null || selectedTahunAjaranId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a Tahun Ajaran to delete.", "No Tahun Ajaran Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Tahun Ajaran: " + lblSelectedTahunAjar.getText() + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            String sql = "DELETE FROM tahun_ajaran WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, selectedTahunAjaranId);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Tahun Ajaran deleted successfully!");
                resetUI();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete Tahun Ajaran. Data not found.", "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to delete Tahun Ajaran: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void tblTahunAjaranMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTahunAjaranMouseClicked
        int baris = tblTahunAjaran.getSelectedRow();
        if (baris != -1) {
            selectedTahunAjaranId = tblTahunAjaran.getValueAt(baris, 0).toString();
            String nama = tblTahunAjaran.getValueAt(baris, 1).toString();
            int tahunMulai = (int) tblTahunAjaran.getValueAt(baris, 2);
            int tahunSelesai = (int) tblTahunAjaran.getValueAt(baris, 3);

            txtNama.setText(nama);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, tahunMulai);
            spnTahunMulai.setValue(cal.getTime());

            cal.set(Calendar.YEAR, tahunSelesai);
            spnTahunSelesai.setValue(cal.getTime());

            lblSelectedTahunAjar.setText(nama + " (" + tahunMulai + "/" + tahunSelesai + ")");
        }
    }//GEN-LAST:event_tblTahunAjaranMouseClicked

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        resetUI();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        dashboard Dashboard = new dashboard();
        Dashboard.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnExitActionPerformed

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
            java.util.logging.Logger.getLogger(TahunAjaran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TahunAjaran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TahunAjaran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TahunAjaran.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TahunAjaran().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel _lblUserDiplih;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblSelectedTahunAjar;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JLabel lblUsername1;
    private javax.swing.JLabel lblUsername2;
    private javax.swing.JSpinner spnTahunMulai;
    private javax.swing.JSpinner spnTahunSelesai;
    private javax.swing.JTable tblTahunAjaran;
    private javax.swing.JTextField txtNama;
    // End of variables declaration//GEN-END:variables
}
