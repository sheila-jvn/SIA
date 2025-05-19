# Database Schema Overview

This document provides a brief overview of the database structure, highlighting key relationships and design considerations. The full SQL script, which includes table creation, alteration, and constraint definitions, should be referred to for complete details.

The database is designed to manage information for an academic institution, covering students, teachers, classes, attendance, grades, tuition payments, and user management.

## Key Design Points & "Less Obvious" Aspects:

1.  **Relational Design and Foreign Keys:**

    - The schema employs foreign keys to maintain relational integrity. For example, tables like `kehadiran` (attendance), `nilai` (grades), and `pembayaran_spp` (tuition payments) use foreign key columns (e.g., `id_siswa`, `id_kelas`, `id_mata_pelajaran`) to link to master tables such as `siswa`, `kelas`, and `mata_pelajaran`.
    - This approach ensures data consistency and avoids storing redundant textual information (like student names or class names) directly within these transactional tables. Instead, such information is retrieved by joining with the respective master tables.
    - This reflects a normalized design, which might be an evolution from a potentially less normalized earlier version.

2.  **Wali Kelas (Homeroom Teacher) Assignment:**

    - The `kelas` table manages class information, including assigning an `id_guru_wali` (homeroom teacher) by linking to the `guru` table's `id`.
    - A **`UNIQUE` constraint** is applied to `kelas.id_guru_wali`. This is crucial as it enforces that **a single guru can only be the homeroom teacher (`wali_kelas`) for one class at most**.
    - The `guru` table itself does not contain a specific `wali_kelas` field in the current SQL schema; the relationship is defined from the `kelas` table pointing to a `guru`. This is a robust relational approach.

3.  **Referential Integrity - Specific `ON DELETE` / `ON UPDATE` Behavior:**

    - Most foreign keys use `ON UPDATE CASCADE`, ensuring that updates to parent primary keys are propagated to child foreign keys.
    - For `ON DELETE` behavior, the schema uses a mixed approach:
        - Many foreign keys use `ON DELETE RESTRICT` as a safe default. This prevents the deletion of a parent record if dependent child records exist (e.g., deleting a `tahun_ajaran` if `kelas` records still refer to it).
        - **`ON DELETE SET NULL`** is used in specific cases to allow parent deletion while preserving child records by nullifying the link:
            - `mata_pelajaran.id_guru`: If a `guru` assigned to a `mata_pelajaran` is deleted, the `id_guru` in `mata_pelajaran` becomes `NULL`.
            - `kelas.id_guru_wali`: If a `guru` who is a homeroom teacher (`wali_kelas`) is deleted, the `id_guru_wali` in the `kelas` table is set to `NULL`. The class continues to exist but without that specific homeroom teacher assigned.
        - **`ON DELETE CASCADE`** is used where child records are intrinsically part of the parent and should not exist independently:
            - `kehadiran.id_siswa`: If a `siswa` record is deleted, all associated attendance records in `kehadiran` are also deleted.
            - `nilai.id_siswa`: If a `siswa` record is deleted, all associated grade records in `nilai` are also deleted.
    - This differentiated `ON DELETE` strategy reflects varying business rules for data lifecycle management across different entities.

4.  **Collation Strategy:**

    - All tables in the schema consistently use the `utf8mb4` character set. This is beneficial for supporting a wide range of characters, including multilingual text and emojis.
    - Most tables use the `utf8mb4_general_ci` (case-insensitive) collation.
    - The `user` table is an exception, using the `utf8mb4_0900_ai_ci` collation.
    - The schema does not use `latin1_swedish_ci` for any tables, ensuring uniform `utf8mb4` usage for broad character compatibility.

5.  **Primary and Unique Keys:**

    - All tables use a surrogate `id` column (typically `INT AUTO_INCREMENT`) as their primary key.
    - In addition to primary keys, several columns have `UNIQUE` constraints to enforce data integrity and prevent duplicate entries for critical identifiers:
        - `user.username`
        - `tingkat.nama`
        - `guru.nip` (Note: `nip` can be `NULL`, but if a value is present, it must be unique)
        - `siswa.nis` (Note: `nis` can be `NULL`, but if a value is present, it must be unique)
        - `siswa.nisn`
        - `kelas.id_guru_wali` (Note: `id_guru_wali` can be `NULL`, but if a value is present, it must be unique, ensuring a teacher is a homeroom teacher for at most one class)
        - `kehadiran_status.nama`
        - `nilai_jenis.nama`

## Core Tables:

- **`user`**: Manages user authentication details (e.g., `username`, hashed `password`).
- **`tahun_ajaran`**: Defines academic years (e.g., '2023/2024 Ganjil', `tahun_mulai`, `tahun_selesai`).
- **`tingkat`**: Represents grade levels within the institution (e.g., 'Kelas 10', 'Kelas 11').
- **`guru`**: Stores information about teachers (e.g., `nip`, `nama`, `tanggal_lahir`).
- **`siswa`**: Contains comprehensive student data (e.g., `nis`, `nisn`, `nama`, `tanggal_lahir`, family details, `alamat`).
- **`mata_pelajaran`**: Lists subjects offered, linked to a `tingkat` (grade level) and optionally to a `guru` teaching it.
- **`kelas`**: Defines individual classes, linking `tahun_ajaran`, `tingkat`, and an optional `id_guru_wali` (homeroom teacher).
- **`kehadiran_status`**: A lookup table for attendance statuses (e.g., 'Hadir', 'Sakit', 'Izin', 'Alpha').
- **`kehadiran`**: Records student attendance, linking `siswa`, `kelas`, `tahun_ajaran`, `kehadiran_status`, and the date.
- **`nilai_jenis`**: A lookup table for types of assessments or grades (e.g., 'Tugas Harian', 'UTS', 'UAS').
- **`nilai`**: Stores student grades for various assessments, linking `siswa`, `mata_pelajaran`, `kelas`, `tahun_ajaran`, `nilai_jenis`, and the grade value.
- **`pembayaran_spp`**: Tracks SPP (tuition fee) payments made by students, linked to `siswa`, `tahun_ajaran`, month, and payment details.

This overview should help in understanding the relationships and some of the rationale behind the schema design. For precise column types, lengths, and all constraints, please refer to the provided SQL script.
