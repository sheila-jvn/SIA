/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package form;

import database.Database;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author erickc
 */
public class PembayaranSPP extends javax.swing.JFrame {

    private Connection conn;
    private DefaultTableModel tabmode;
    private String selectedPembayaranSPPId;

    private static final BigDecimal SPP_PER_BULAN = new BigDecimal("650000.00");
    private static final int TOTAL_MONTHS_IN_YEAR = 12;
    private final DecimalFormat currencyFormatter = new DecimalFormat("Rp #,##0.00");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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
            return description;
        }

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
    
    public void cetak(int idSiswa, int idTahunAjaran) {
    try {
        String path = "./src/form/spp.jasper";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("idSiswa", idSiswa);
        parameters.put("idTahunAjaran", idTahunAjaran);
        JasperPrint print = JasperFillManager.fillReport(path, parameters, conn);
        JasperViewer.viewReport(print, false);

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(rootPane, "Gagal mencetak laporan: " + ex.getMessage());
        ex.printStackTrace();
    }
}


    /**
     * Creates new form PembayaranSPP
     */
    public PembayaranSPP() {
        initComponents();
        lblSelectedNilai1.setText("-"); // Will be used as lblSelectedPembayaranInfo
        lblSisaBayar.setText(currencyFormatter.format(0));
        selectedPembayaranSPPId = null;
        try {
            conn = Database.getConnection();
            loadSiswaComboBox();
            loadTahunAjaranComboBox();
            loadBulanComboBox();
            reset();
            loadTable(); // Load table initially (might be empty or show all)
            // Add action listeners to recalculate sisa bayar and reload table
            cmbSiswa.addActionListener(e -> calculateSisaBayarAndLoadTable());
            cmbTahunAjaran.addActionListener(e -> calculateSisaBayarAndLoadTable());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSiswaComboBox() {
        DefaultComboBoxModel<Item> model = new DefaultComboBoxModel<>();
        model.addElement(new Item(0, "-- Pilih Siswa --")); // Default non-selectable item
        try {
            String sql = "SELECT id, nama, nisn FROM siswa ORDER BY nama ASC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addElement(new Item(rs.getInt("id"), rs.getString("nisn") + " - " + rs.getString("nama")));
            }
            cmbSiswa.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data siswa: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTahunAjaranComboBox() {
        DefaultComboBoxModel<Item> model = new DefaultComboBoxModel<>();
        model.addElement(new Item(0, "-- Pilih Tahun Ajaran --")); // Default non-selectable item
        try {
            String sql = "SELECT id, nama FROM tahun_ajaran ORDER BY tahun_mulai DESC, nama ASC";
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

    private void loadBulanComboBox() {
        DefaultComboBoxModel<Item> model = new DefaultComboBoxModel<>();
        model.addElement(new Item(0, "-- Pilih Bulan --")); // Default non-selectable item
        String[] namaBulan = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        int idCounter = 1; // Start IDs from 1 for actual months
        for (String bulan : namaBulan) {
            model.addElement(new Item(idCounter++, bulan));
        }
        cmbBulan.setModel(model);
    }

    private void reset() {
        if (cmbSiswa.getItemCount() > 0) {
            cmbSiswa.setSelectedIndex(0);
        }
        if (cmbTahunAjaran.getItemCount() > 0) {
            cmbTahunAjaran.setSelectedIndex(0);
        }
        if (cmbBulan.getItemCount() > 0) {
            cmbBulan.setSelectedIndex(0);
        }
        txtJumlahBayar.setText("");
        spnTanggalBayar.setValue(new java.util.Date());
        txtKeterangan.setText(""); // Keterangan is not saved to DB
        txtSearch.setText("");
        lblSelectedNilai1.setText("-"); // Used as lblSelectedPembayaranInfo
        selectedPembayaranSPPId = null;
        // lblSisaBayar.setText(currencyFormatter.format(0)); // Reset by calculateSisaBayar
        calculateSisaBayar(); // Recalculate based on current (likely reset) selections
    }

    private void loadTable() {
        Object[] Baris = {"ID", "NISN", "Nama Siswa", "Tahun Ajaran", "Bulan", "Tgl Bayar", "Jumlah Bayar"};
        tabmode = new DefaultTableModel(null, Baris);
        tblPembayaranSPP.setModel(tabmode);

        String cariitem = txtSearch.getText();
        Item selectedSiswa = (Item) cmbSiswa.getSelectedItem();
        Item selectedTahunAjaran = (Item) cmbTahunAjaran.getSelectedItem();

        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT ps.id, s.nisn, s.nama AS nama_siswa, ta.nama AS nama_tahun_ajaran, ps.bulan, ps.tanggal_bayar, ps.jumlah_bayar "
                + "FROM pembayaran_spp ps "
                + "JOIN siswa s ON ps.id_siswa = s.id "
                + "JOIN tahun_ajaran ta ON ps.id_tahun_ajaran = ta.id "
        );

        boolean whereClauseAdded = false;
        if (selectedSiswa != null && selectedSiswa.getId() != 0) {
            sqlBuilder.append(whereClauseAdded ? " AND " : " WHERE ");
            sqlBuilder.append("ps.id_siswa = ?");
            whereClauseAdded = true;
        }
        if (selectedTahunAjaran != null && selectedTahunAjaran.getId() != 0) {
            sqlBuilder.append(whereClauseAdded ? " AND " : " WHERE ");
            sqlBuilder.append("ps.id_tahun_ajaran = ?");
            whereClauseAdded = true;
        }

        if (!cariitem.isEmpty()) {
            sqlBuilder.append(whereClauseAdded ? " AND " : " WHERE ");
            sqlBuilder.append("(s.nama LIKE ? OR s.nisn LIKE ? OR ta.nama LIKE ? OR ps.bulan LIKE ?)");
            whereClauseAdded = true;
        }
        sqlBuilder.append(" ORDER BY ps.tanggal_bayar DESC, s.nama ASC");

        try {
            PreparedStatement stat = conn.prepareStatement(sqlBuilder.toString());
            int paramIndex = 1;
            if (selectedSiswa != null && selectedSiswa.getId() != 0) {
                stat.setInt(paramIndex++, selectedSiswa.getId());
            }
            if (selectedTahunAjaran != null && selectedTahunAjaran.getId() != 0) {
                stat.setInt(paramIndex++, selectedTahunAjaran.getId());
            }
            if (!cariitem.isEmpty()) {
                String searchTerm = "%" + cariitem + "%";
                stat.setString(paramIndex++, searchTerm);
                stat.setString(paramIndex++, searchTerm);
                stat.setString(paramIndex++, searchTerm);
                stat.setString(paramIndex++, searchTerm);
            }

            ResultSet hasil = stat.executeQuery();
            while (hasil.next()) {
                tabmode.addRow(new Object[]{
                    hasil.getString("id"),
                    hasil.getString("nisn"),
                    hasil.getString("nama_siswa"),
                    hasil.getString("nama_tahun_ajaran"),
                    hasil.getString("bulan"),
                    dateFormat.format(hasil.getDate("tanggal_bayar")),
                    currencyFormatter.format(hasil.getBigDecimal("jumlah_bayar"))
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Data pembayaran SPP gagal dipanggil: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calculateSisaBayar() {
        Item siswaItem = (Item) cmbSiswa.getSelectedItem();
        Item tahunAjaranItem = (Item) cmbTahunAjaran.getSelectedItem();

        if (siswaItem == null || siswaItem.getId() == 0 || tahunAjaranItem == null || tahunAjaranItem.getId() == 0) {
            lblSisaBayar.setText(currencyFormatter.format(0));
            return;
        }

        int siswaId = siswaItem.getId();
        int tahunAjaranId = tahunAjaranItem.getId();
        BigDecimal totalHarusBayar = SPP_PER_BULAN.multiply(new BigDecimal(TOTAL_MONTHS_IN_YEAR));
        BigDecimal totalSudahBayar = BigDecimal.ZERO;

        String sql = "SELECT SUM(jumlah_bayar) AS total_paid FROM pembayaran_spp WHERE id_siswa = ? AND id_tahun_ajaran = ?";
        try {
            PreparedStatement stat = conn.prepareStatement(sql);
            stat.setInt(1, siswaId);
            stat.setInt(2, tahunAjaranId);
            ResultSet rs = stat.executeQuery();
            if (rs.next()) {
                BigDecimal paid = rs.getBigDecimal("total_paid");
                if (paid != null) {
                    totalSudahBayar = paid;
                }
            }
            BigDecimal sisaBayar = totalHarusBayar.subtract(totalSudahBayar);
            lblSisaBayar.setText(currencyFormatter.format(sisaBayar));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menghitung sisa bayar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            lblSisaBayar.setText("Error");
        }
    }

    private void calculateSisaBayarAndLoadTable() {
        calculateSisaBayar();
        loadTable();
    }

    private void resetUI() {
        reset();
        loadTable();
        // calculateSisaBayar is called within reset()
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel8 = new javax.swing.JLabel();
        spnTanggalBayar = new javax.swing.JSpinner();
        cmbSiswa = new javax.swing.JComboBox<>();
        txtSearch = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        btnDelete = new javax.swing.JButton();
        btnCreate = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPembayaranSPP = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblSisaBayar = new javax.swing.JLabel();
        cmbBulan = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cmbTahunAjaran = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtKeterangan = new javax.swing.JTextArea();
        btnReset = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        btnExit = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txtJumlahBayar = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        lblSelectedNilai1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel8.setFont(new java.awt.Font("Noto Sans", 0, 14)); // NOI18N
        jLabel8.setText("SPP Dipilih:");

        spnTanggalBayar.setModel(new javax.swing.SpinnerDateModel());

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchKeyPressed(evt);
            }
        });

        jLabel9.setText("Siswa");

        btnDelete.setText("HAPUS");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnCreate.setText("SIMPAN");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        tblPembayaranSPP.setModel(new javax.swing.table.DefaultTableModel(
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
        tblPembayaranSPP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPembayaranSPPMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblPembayaranSPP);

        jLabel6.setText("Keterangan");

        jLabel7.setText("Tanggal");

        lblSisaBayar.setFont(new java.awt.Font("Noto Sans", 0, 14)); // NOI18N
        lblSisaBayar.setText("[PLACEHOLDER]");

        jLabel2.setFont(new java.awt.Font("Noto Sans", 1, 24)); // NOI18N
        jLabel2.setText("Pembayaran SPP");

        jLabel4.setText("Tahun Ajaran");

        txtKeterangan.setColumns(20);
        txtKeterangan.setRows(5);
        jScrollPane2.setViewportView(txtKeterangan);

        btnReset.setText("BATAL");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        btnSearch.setText("Cari");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        jLabel5.setText("Bulan");

        btnExit.setText("KELUAR");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        btnUpdate.setText("UBAH");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        jLabel12.setText("Jumlah");

        txtJumlahBayar.setToolTipText("");

        jLabel10.setFont(new java.awt.Font("Noto Sans", 0, 14)); // NOI18N
        jLabel10.setText("Sisa Bayar:");

        lblSelectedNilai1.setFont(new java.awt.Font("Noto Sans", 0, 14)); // NOI18N
        lblSelectedNilai1.setText("[PLACEHOLDER]");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(75, 75, 75)
                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(78, 78, 78)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67)
                .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(350, 350, 350)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cmbTahunAjaran, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel9)
                                        .addComponent(jLabel12))
                                    .addGap(52, 52, 52)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtJumlahBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblSelectedNilai1)
                                        .addComponent(cmbSiswa, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(129, 129, 129)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(40, 40, 40))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jLabel5)
                                    .addGap(55, 55, 55))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jLabel6)
                                    .addGap(18, 18, 18))))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spnTanggalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(cmbBulan, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel10)
                                    .addGap(67, 67, 67)
                                    .addComponent(lblSisaBayar))
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(57, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSisaBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSelectedNilai1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(cmbSiswa, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbBulan, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel12)
                                    .addComponent(txtJumlahBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(cmbTahunAjaran, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7)
                                    .addComponent(spnTanggalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            loadTable();
        }
    }//GEN-LAST:event_txtSearchKeyPressed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if (selectedPembayaranSPPId == null || selectedPembayaranSPPId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih data pembayaran SPP yang akan dihapus.", "Data Belum Dipilih", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data pembayaran SPP: " + lblSelectedNilai1.getText() + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM pembayaran_spp WHERE id = ?";
        try {
            PreparedStatement stat = conn.prepareStatement(sql);
            stat.setInt(1, Integer.parseInt(selectedPembayaranSPPId));

            int rowsDeleted = stat.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Data pembayaran SPP berhasil dihapus!");
                resetUI(); // This will also recalculate sisa bayar
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data. Data tidak ditemukan.", "Hapus Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus data pembayaran SPP: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID Pembayaran SPP tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        Item selectedSiswa = (Item) cmbSiswa.getSelectedItem();
        Item selectedTahunAjaran = (Item) cmbTahunAjaran.getSelectedItem();
        Item selectedBulanItem = (Item) cmbBulan.getSelectedItem();
        java.util.Date utilTanggalBayar = (java.util.Date) spnTanggalBayar.getValue();
        String strJumlahBayar = txtJumlahBayar.getText();

        if (selectedSiswa == null || selectedSiswa.getId() == 0
                || selectedTahunAjaran == null || selectedTahunAjaran.getId() == 0
                || selectedBulanItem == null || selectedBulanItem.getId() == 0 // Check ID for placeholder
                || utilTanggalBayar == null || strJumlahBayar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field (Siswa, Tahun Ajaran, Bulan, Tanggal Bayar, Jumlah Bayar) harus diisi.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal jumlahBayar;
        try {
            jumlahBayar = new BigDecimal(strJumlahBayar.replace(",", "")); // Remove thousand separators if any
            if (jumlahBayar.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah bayar harus lebih besar dari 0.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Validate against SPP_PER_BULAN
            if (jumlahBayar.compareTo(SPP_PER_BULAN) < 0) {
                JOptionPane.showMessageDialog(this, "Jumlah bayar tidak boleh kurang dari " + currencyFormatter.format(SPP_PER_BULAN) + ".", "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah Bayar harus berupa angka yang valid.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check for duplicate payment (same student, year, month)
        try {
            String checkSql = "SELECT id FROM pembayaran_spp WHERE id_siswa = ? AND id_tahun_ajaran = ? AND bulan = ?";
            PreparedStatement checkStat = conn.prepareStatement(checkSql);
            checkStat.setInt(1, selectedSiswa.getId());
            checkStat.setInt(2, selectedTahunAjaran.getId());
            checkStat.setString(3, selectedBulanItem.getDescription()); // Use description for DB
            ResultSet rsCheck = checkStat.executeQuery();
            if (rsCheck.next()) {
                JOptionPane.showMessageDialog(this, "Pembayaran untuk siswa ini, tahun ajaran ini, dan bulan ini sudah ada.", "Duplikasi Data", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memeriksa duplikasi data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.sql.Date sqlTanggalBayar = new java.sql.Date(utilTanggalBayar.getTime());

        String sql = "INSERT INTO pembayaran_spp (id_siswa, id_tahun_ajaran, bulan, tanggal_bayar, jumlah_bayar) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement stat = conn.prepareStatement(sql);
            stat.setInt(1, selectedSiswa.getId());
            stat.setInt(2, selectedTahunAjaran.getId());
            stat.setString(3, selectedBulanItem.getDescription()); // Use description for DB
            stat.setDate(4, sqlTanggalBayar);
            stat.setBigDecimal(5, jumlahBayar);

            int rowsInserted = stat.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Data pembayaran SPP berhasil ditambahkan!");
                cetak(selectedSiswa.getId(), selectedTahunAjaran.getId());
                resetUI(); // This will also recalculate sisa bayar
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan data pembayaran SPP: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCreateActionPerformed

    private void tblPembayaranSPPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPembayaranSPPMouseClicked
        int baris = tblPembayaranSPP.getSelectedRow();
        if (baris != -1) {
            selectedPembayaranSPPId = tabmode.getValueAt(baris, 0).toString();
            String nisnSiswa = tabmode.getValueAt(baris, 1).toString();
            String namaSiswa = tabmode.getValueAt(baris, 2).toString();
            String namaTahunAjaran = tabmode.getValueAt(baris, 3).toString();
            String namaBulan = tabmode.getValueAt(baris, 4).toString();
            String strTanggalBayar = tabmode.getValueAt(baris, 5).toString();
            String strJumlahBayar = tabmode.getValueAt(baris, 6).toString(); // This is formatted currency

            lblSelectedNilai1.setText(nisnSiswa + " - " + namaSiswa + " (" + namaBulan + " " + namaTahunAjaran + ")");

            try {
                // Parse formatted currency string back to number for editing
                Number parsedJumlah = currencyFormatter.parse(strJumlahBayar);
                txtJumlahBayar.setText(new BigDecimal(parsedJumlah.toString()).toPlainString());
            } catch (ParseException e) {
                txtJumlahBayar.setText("0"); // Fallback
            }

            try {
                spnTanggalBayar.setValue(dateFormat.parse(strTanggalBayar));
            } catch (ParseException e) {
                spnTanggalBayar.setValue(new java.util.Date()); // Fallback
            }

            // Select Siswa
            for (int i = 0; i < cmbSiswa.getItemCount(); i++) {
                Item item = (Item) cmbSiswa.getItemAt(i);
                if (item.getDescription().equals(nisnSiswa + " - " + namaSiswa)) {
                    cmbSiswa.setSelectedIndex(i);
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
            // Select Bulan
            for (int i = 0; i < cmbBulan.getItemCount(); i++) {
                Item item = (Item) cmbBulan.getItemAt(i);
                if (item.getDescription().equals(namaBulan)) {
                    cmbBulan.setSelectedIndex(i);
                    break;
                }
            }

            // Trigger sisa bayar calculation for the selected student/year
            calculateSisaBayar();
        }
    }//GEN-LAST:event_tblPembayaranSPPMouseClicked

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        resetUI();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        loadTable();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        new dashboard().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (selectedPembayaranSPPId == null || selectedPembayaranSPPId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih data pembayaran SPP yang akan diupdate.", "Data Belum Dipilih", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Item selectedSiswa = (Item) cmbSiswa.getSelectedItem();
        Item selectedTahunAjaran = (Item) cmbTahunAjaran.getSelectedItem();
        Item selectedBulanItem = (Item) cmbBulan.getSelectedItem();
        java.util.Date utilTanggalBayar = (java.util.Date) spnTanggalBayar.getValue();
        String strJumlahBayar = txtJumlahBayar.getText();

        if (selectedSiswa == null || selectedSiswa.getId() == 0
                || selectedTahunAjaran == null || selectedTahunAjaran.getId() == 0
                || selectedBulanItem == null || selectedBulanItem.getId() == 0 // Check ID for placeholder
                || utilTanggalBayar == null || strJumlahBayar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field (Siswa, Tahun Ajaran, Bulan, Tanggal Bayar, Jumlah Bayar) harus diisi.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal jumlahBayar;
        try {
            jumlahBayar = new BigDecimal(strJumlahBayar.replace(",", ""));
            if (jumlahBayar.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah bayar harus lebih besar dari 0.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Validate against SPP_PER_BULAN
            if (jumlahBayar.compareTo(SPP_PER_BULAN) < 0) {
                JOptionPane.showMessageDialog(this, "Jumlah bayar tidak boleh kurang dari " + currencyFormatter.format(SPP_PER_BULAN) + ".", "Validasi Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah Bayar harus berupa angka yang valid.", "Validasi Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check for duplicate payment if month/student/year changed
        try {
            String checkSql = "SELECT id FROM pembayaran_spp WHERE id_siswa = ? AND id_tahun_ajaran = ? AND bulan = ? AND id != ?";
            PreparedStatement checkStat = conn.prepareStatement(checkSql);
            checkStat.setInt(1, selectedSiswa.getId());
            checkStat.setInt(2, selectedTahunAjaran.getId());
            checkStat.setString(3, selectedBulanItem.getDescription()); // Use description for DB
            checkStat.setInt(4, Integer.parseInt(selectedPembayaranSPPId));
            ResultSet rsCheck = checkStat.executeQuery();
            if (rsCheck.next()) {
                JOptionPane.showMessageDialog(this, "Pembayaran untuk siswa ini, tahun ajaran ini, dan bulan ini sudah ada (data lain).", "Duplikasi Data", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memeriksa duplikasi data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.sql.Date sqlTanggalBayar = new java.sql.Date(utilTanggalBayar.getTime());

        String sql = "UPDATE pembayaran_spp SET id_siswa = ?, id_tahun_ajaran = ?, bulan = ?, tanggal_bayar = ?, jumlah_bayar = ? WHERE id = ?";
        try {
            PreparedStatement stat = conn.prepareStatement(sql);
            stat.setInt(1, selectedSiswa.getId());
            stat.setInt(2, selectedTahunAjaran.getId());
            stat.setString(3, selectedBulanItem.getDescription()); // Use description for DB
            stat.setDate(4, sqlTanggalBayar);
            stat.setBigDecimal(5, jumlahBayar);
            stat.setInt(6, Integer.parseInt(selectedPembayaranSPPId));

            int rowsUpdated = stat.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Data pembayaran SPP berhasil diupdate!");
                resetUI(); // This will also recalculate sisa bayar
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate data. Data tidak ditemukan atau tidak berubah.", "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate data pembayaran SPP: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID Pembayaran SPP tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

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
            java.util.logging.Logger.getLogger(PembayaranSPP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PembayaranSPP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PembayaranSPP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PembayaranSPP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PembayaranSPP().setVisible(true);
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
    private javax.swing.JComboBox<Item> cmbBulan;
    private javax.swing.JComboBox<Item> cmbSiswa;
    private javax.swing.JComboBox<Item> cmbTahunAjaran;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JLabel lblSelectedNilai1;
    private javax.swing.JLabel lblSisaBayar;
    private javax.swing.JSpinner spnTanggalBayar;
    private javax.swing.JTable tblPembayaranSPP;
    private javax.swing.JTextField txtJumlahBayar;
    private javax.swing.JTextArea txtKeterangan;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
