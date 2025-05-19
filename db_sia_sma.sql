-- Original table creations (with collation adjustments for MariaDB compatibility)
CREATE TABLE `data_absensi` (
  `id` varchar(100) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `nis` varchar(100) NOT NULL,
  `kelas` varchar(100) NOT NULL, -- This will be kept, new FK 'kelas_id' will be added
  `keterangan` varchar(100) NOT NULL,
  `tanggal` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `data_kelas` (
  `id` varchar(20) NOT NULL,
  `kelas` varchar(255) NOT NULL,
  `ruangan` varchar(255) NOT NULL,
  `wali_kelas` varchar(255) NOT NULL, -- This will be kept (name of wali_kelas), new FK 'guru_id_wali_kelas' will be added
  `ketua_kelas` varchar(255) NOT NULL,
  `tahun_ajaran` varchar(25) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci; -- Changed collation

CREATE TABLE `data_nilai` (
  `id` varchar(255) NOT NULL,
  `nama` varchar(255) NOT NULL,
  `nis` varchar(100) NOT NULL,
  `kelas` varchar(50) NOT NULL, -- This will be kept, new FK 'kelas_id' will be added
  `nilai_tugas` float NOT NULL,
  `nilai_uts` float NOT NULL,
  `nilai_uas` float NOT NULL,
  `rata_rata` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci; -- Changed collation

CREATE TABLE `data_siswa` (
  `id` int(11) NOT NULL,
  `Nama` varchar(250) NOT NULL,
  `NISN` varchar(255) NOT NULL,
  `NIK` varchar(255) NOT NULL,
  `no_kk` varchar(255) NOT NULL,
  `tgl_lahir` date NOT NULL,
  `jk` varchar(20) NOT NULL,
  `Nama_Ayah` varchar(250) NOT NULL,
  `NIK_Ayah` varchar(255) NOT NULL,
  `Nama_Ibu` varchar(250) NOT NULL,
  `NIK_Ibu` varchar(255) NOT NULL,
  `Alamat` varchar(250) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci; -- Kept original as ID is INT

CREATE TABLE `guru` (
  `id` int(11) NOT NULL,
  `nip` varchar(255) NOT NULL,
  `nama` varchar(255) NOT NULL,
  `tgl_lahir` date NOT NULL,
  `jk` varchar(20) NOT NULL,
  `telp` varchar(20) NOT NULL,
  `mapel` varchar(255) NOT NULL,
  `wali_kelas` varchar(255) NOT NULL -- This field (likely class name/ID) will be kept
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci; -- Changed collation

CREATE TABLE `pengguna` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(25) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci; -- Kept original, not directly involved in new FKs with VARCHAR keys

CREATE TABLE `spp_pembayaran` (
  `id` varchar(255) NOT NULL,
  `nama` varchar(255) NOT NULL, -- This will be kept (student name), new FK 'siswa_id' will be added
  `bulan` varchar(255) NOT NULL,
  `tgl` date NOT NULL,
  `jmlbayar` int(11) NOT NULL,
  `bayar` int(11) NOT NULL,
  `tunggakan` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci; -- Kept original, FK to data_siswa.id is INT

-- Add new columns for Foreign Keys
ALTER TABLE `spp_pembayaran`
  ADD COLUMN `siswa_id` INT NOT NULL AFTER `nama`;

ALTER TABLE `data_nilai`
  ADD COLUMN `siswa_id` INT NOT NULL AFTER `nis`,
  ADD COLUMN `kelas_id` VARCHAR(20) NOT NULL AFTER `kelas`;

ALTER TABLE `data_absensi`
  ADD COLUMN `siswa_id` INT NOT NULL AFTER `nis`,
  ADD COLUMN `kelas_id` VARCHAR(20) NOT NULL AFTER `kelas`;

ALTER TABLE `data_kelas`
  ADD COLUMN `guru_id_wali_kelas` INT NULL AFTER `wali_kelas`;

-- Add Primary Keys (from original dump)
ALTER TABLE `data_absensi`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `data_kelas`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `data_nilai`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `data_siswa`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `guru`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `pengguna`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

ALTER TABLE `spp_pembayaran`
  ADD PRIMARY KEY (`id`);

-- Add Unique Constraints for 1-to-1 relationships or FK targets if needed
-- For 1-to-1 relationship: a guru can be wali_kelas of at most one class
ALTER TABLE `data_kelas`
  ADD UNIQUE KEY `uq_guru_id_wali_kelas` (`guru_id_wali_kelas`);

-- Add Foreign Key Constraints
-- spp_pembayaran (many) to data_siswa (one)
ALTER TABLE `spp_pembayaran`
  ADD CONSTRAINT `fk_spp_pembayaran_siswa` FOREIGN KEY (`siswa_id`) REFERENCES `data_siswa` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- data_nilai (many) to data_siswa (one)
ALTER TABLE `data_nilai`
  ADD CONSTRAINT `fk_data_nilai_siswa` FOREIGN KEY (`siswa_id`) REFERENCES `data_siswa` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- data_nilai (many) to data_kelas (one)
ALTER TABLE `data_nilai`
  ADD CONSTRAINT `fk_data_nilai_kelas` FOREIGN KEY (`kelas_id`) REFERENCES `data_kelas` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- data_absensi (many) to data_siswa (one)
ALTER TABLE `data_absensi`
  ADD CONSTRAINT `fk_data_absensi_siswa` FOREIGN KEY (`siswa_id`) REFERENCES `data_siswa` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- data_absensi (many) to data_kelas (one)
ALTER TABLE `data_absensi`
  ADD CONSTRAINT `fk_data_absensi_kelas` FOREIGN KEY (`kelas_id`) REFERENCES `data_kelas` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- data_kelas (one) to guru (one) for wali_kelas
-- This makes guru_id_wali_kelas in data_kelas point to a guru.
-- The UNIQUE constraint on data_kelas.guru_id_wali_kelas ensures a guru is assigned to at most one class as wali_kelas.
ALTER TABLE `data_kelas`
  ADD CONSTRAINT `fk_data_kelas_guru_wali` FOREIGN KEY (`guru_id_wali_kelas`) REFERENCES `guru` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- Modify ID auto_increment (from original dump)
ALTER TABLE `data_siswa`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

ALTER TABLE `guru`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

ALTER TABLE `pengguna`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
