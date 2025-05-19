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
