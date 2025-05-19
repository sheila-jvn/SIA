-- ### User Authentication ###
CREATE TABLE `user` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(100) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL COMMENT 'Passwords should be hashed'
);

-- ### Academic Year Information ###
CREATE TABLE `tahun_ajaran` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nama` VARCHAR(100) NOT NULL COMMENT 'e.g., 2023/2024 Ganjil',
  `tahun_mulai` YEAR NOT NULL,
  `tahun_selesai` YEAR NOT NULL
);

-- ### Grade Level Information ###
CREATE TABLE `tingkat` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nama` VARCHAR(50) NOT NULL UNIQUE COMMENT 'e.g., Kelas 10, Kelas 11, Kelas 12'
);

-- ### Teacher Information ###
CREATE TABLE `guru` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nip` VARCHAR(50) NULL UNIQUE COMMENT 'Nomor Induk Pegawai, can be NULL if not applicable',
  `nama` VARCHAR(255) NOT NULL,
  `tanggal_lahir` DATE NULL,
  `jenis_kelamin` BOOLEAN NULL COMMENT '0 for Perempuan (Female), 1 for Laki-laki (Male)',
  `no_telpon` VARCHAR(20) NULL
);

-- ### Student Information ###
CREATE TABLE `siswa` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nis` VARCHAR(50) NULL UNIQUE COMMENT 'Nomor Induk Siswa',
  `nisn` VARCHAR(50) NOT NULL UNIQUE COMMENT 'Nomor Induk Siswa Nasional',
  `nama` VARCHAR(255) NOT NULL,
  `no_kk` VARCHAR(50) NULL COMMENT 'Nomor Kartu Keluarga',
  `tanggal_lahir` DATE NULL,
  `jenis_kelamin` BOOLEAN NULL COMMENT '0 for Perempuan (Female), 1 for Laki-laki (Male)',
  `nama_ayah` VARCHAR(255) NULL,
  `nama_ibu` VARCHAR(255) NULL,
  `nik_ayah` VARCHAR(50) NULL,
  `nik_ibu` VARCHAR(50) NULL,
  `alamat` TEXT NULL
);

-- ### Subject Information ###
CREATE TABLE `mata_pelajaran` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `id_tingkat` INT NOT NULL,
  `id_guru` INT NULL COMMENT 'Teacher teaching this subject at this grade level, can be NULL if unassigned',
  `nama` VARCHAR(100) NOT NULL,
  FOREIGN KEY (`id_tingkat`) REFERENCES `tingkat`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (`id_guru`) REFERENCES `guru`(`id`) ON DELETE SET NULL ON UPDATE CASCADE
);

-- ### Class Information ###
CREATE TABLE `kelas` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `id_tahun_ajaran` INT NOT NULL,
  `id_tingkat` INT NOT NULL,
  `id_guru_wali` INT NULL UNIQUE COMMENT 'Homeroom teacher, a teacher can only be a homeroom teacher for one class',
  `nama` VARCHAR(100) NOT NULL COMMENT 'e.g., 10-A, 11-IPA-1',
  FOREIGN KEY (`id_tahun_ajaran`) REFERENCES `tahun_ajaran`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (`id_tingkat`) REFERENCES `tingkat`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (`id_guru_wali`) REFERENCES `guru`(`id`) ON DELETE SET NULL ON UPDATE CASCADE -- If a guru is deleted, the class wali is set to NULL
);

-- ### Attendance Status ###
CREATE TABLE `kehadiran_status` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nama` VARCHAR(50) NOT NULL UNIQUE COMMENT 'e.g., Hadir, Sakit, Izin, Alpha'
);

-- ### Attendance Records ###
CREATE TABLE `kehadiran` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `id_siswa` INT NOT NULL,
  `id_kelas` INT NOT NULL,
  `id_tahun_ajaran` INT NOT NULL,
  `id_status` INT NOT NULL,
  `tanggal` DATE NOT NULL,
  `keterangan` TEXT NULL,
  FOREIGN KEY (`id_siswa`) REFERENCES `siswa`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`id_kelas`) REFERENCES `kelas`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (`id_tahun_ajaran`) REFERENCES `tahun_ajaran`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (`id_status`) REFERENCES `kehadiran_status`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- ### Grade Types ###
CREATE TABLE `nilai_jenis` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nama` VARCHAR(100) NOT NULL UNIQUE COMMENT 'e.g., Tugas Harian, Ulangan Harian, UTS, UAS, Praktikum'
);

-- ### Grade Records ###
CREATE TABLE `nilai` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `id_siswa` INT NOT NULL,
  `id_mata_pelajaran` INT NOT NULL,
  `id_kelas` INT NOT NULL,
  `id_tahun_ajaran` INT NOT NULL,
  `id_jenis_nilai` INT NOT NULL,
  `nilai` FLOAT NOT NULL,
  `tanggal_penilaian` DATE NOT NULL,
  `keterangan` TEXT NULL,
  FOREIGN KEY (`id_siswa`) REFERENCES `siswa`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`id_mata_pelajaran`) REFERENCES `mata_pelajaran`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (`id_kelas`) REFERENCES `kelas`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (`id_tahun_ajaran`) REFERENCES `tahun_ajaran`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (`id_jenis_nilai`) REFERENCES `nilai_jenis`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- ### SPP Payment Records ###
CREATE TABLE `pembayaran_spp` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `id_siswa` INT NOT NULL,
  `id_tahun_ajaran` INT NOT NULL,
  `bulan` VARCHAR(20) NOT NULL COMMENT 'e.g., Januari, Februari. Consider using DATE type for first day of month if more precision/sorting is needed.',
  `tanggal_bayar` DATE NOT NULL,
  `jumlah_bayar` DECIMAL(10,2) NOT NULL,
  FOREIGN KEY (`id_siswa`) REFERENCES `siswa`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE, -- RESTRICT delete if payments exist
  FOREIGN KEY (`id_tahun_ajaran`) REFERENCES `tahun_ajaran`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- ### User Authentication ###
INSERT INTO `user` (`username`, `password`) VALUES
('admin', 'admin'),
('username', 'password');

-- ### Academic Year Information ###
INSERT INTO `tahun_ajaran` (`nama`, `tahun_mulai`, `tahun_selesai`) VALUES
('2023/2024 Ganjil', 2023, 2024),
('2023/2024 Genap', 2023, 2024),
('2024/2025 Ganjil', 2024, 2025);

-- ### Grade Level Information ###
INSERT INTO `tingkat` (`nama`) VALUES
('Kelas 10'),
('Kelas 11'),
('Kelas 12');

-- ### Teacher Information ###
INSERT INTO `guru` (`nip`, `nama`, `tanggal_lahir`, `jenis_kelamin`, `no_telpon`) VALUES
('198001012005011001', 'Udin Perkasa', '1980-01-01', 1, '081234567890'),
('198505052008012002', 'Tono Jaya', '1985-05-05', 0, '081234567891'),
(NULL, 'Budi Sejahtera', '1990-10-10', 0, '081234567892');

-- ### Student Information ###
INSERT INTO `siswa` (`nis`, `nisn`, `nama`, `no_kk`, `tanggal_lahir`, `jenis_kelamin`, `nama_ayah`, `nama_ibu`, `nik_ayah`, `nik_ibu`, `alamat`) VALUES
('1001', '0012345678', 'Joko Pintar', '3201010101000001', '2007-08-17', 1, 'Bambang Perkasa', 'Udin Lestari', '3201010101700001', '3201010101750002', 'Jl. Merdeka No. 1'),
('1002', '0023456789', 'Bambang Ceria', '3201010101000002', '2007-05-20', 0, 'Udin Susilo', 'Tono Marlina', '3201010101680003', '3201010101720004', 'Jl. Pahlawan No. 10'),
('1003', '0034567890', 'Udin Bahagia', '3201010101000003', '2006-11-10', 0, 'Tono Irawan', 'Budi Kartika', '3201010101650005', '3201010101690006', 'Jl. Kemerdekaan No. 5');

-- ### Subject Information ###
-- Assuming id_tingkat: 1 for Kelas 10, 2 for Kelas 11, 3 for Kelas 12
-- Assuming id_guru: 1 for Udin Perkasa, 2 for Tono Jaya, 3 for Budi Sejahtera
INSERT INTO `mata_pelajaran` (`id_tingkat`, `id_guru`, `nama`) VALUES
(1, 1, 'Matematika Wajib'),
(1, 2, 'Bahasa Indonesia'),
(1, 3, 'Bahasa Inggris'),
(2, 1, 'Fisika'),
(2, 2, 'Kimia'),
(3, 3, 'Biologi');

-- ### Class Information ###
-- Assuming id_tahun_ajaran: 1 for 2023/2024 Ganjil, 2 for 2023/2024 Genap, 3 for 2024/2025 Ganjil
-- Assuming id_tingkat: 1 for Kelas 10, 2 for Kelas 11, 3 for Kelas 12
-- Assuming id_guru_wali: 1 for Udin Perkasa, 2 for Tono Jaya (ensure guru id_guru_wali is unique if you add more)
INSERT INTO `kelas` (`id_tahun_ajaran`, `id_tingkat`, `id_guru_wali`, `nama`) VALUES
(1, 1, 1, '10-A'),
(1, 1, 2, '10-B'),
(3, 2, 3, '11-IPA-1');


-- ### Attendance Status ###
INSERT INTO `kehadiran_status` (`nama`) VALUES
('Hadir'),
('Sakit'),
('Izin'),
('Alpha');

-- ### Grade Types ###
INSERT INTO `nilai_jenis` (`nama`) VALUES
('Tugas Harian'),
('Ulangan Harian'),
('UTS'),
('UAS'),
('Praktikum');