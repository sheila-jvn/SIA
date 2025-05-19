# Database Schema Overview

This document provides a brief overview of the database structure, highlighting key relationships and design considerations. The full SQL script, which includes table creation, alteration, and constraint definitions, should be referred to for complete details.

The database is designed to manage information for an academic institution, covering students, teachers, classes, attendance, grades, tuition payments, and user management.

## Key Design Points & "Less Obvious" Aspects:

1.  **Evolutionary Design & Data Redundancy for Convenience:**

    - Several tables (`data_absensi`, `data_nilai`, `spp_pembayaran`) originally stored denormalized textual information like student names (`nama`), student numbers (`nis`), and class names (`kelas`).
    - **New foreign key columns** (`siswa_id`, `kelas_id`) have been introduced to establish proper relational links to `data_siswa` and `data_kelas` respectively.
    - However, the **original textual columns have been retained**. This is a significant design choice, likely made for:
      - **Backward compatibility:** Existing applications or queries might rely on these fields.
      - **Reporting/Display performance:** For simple displays, querying these direct text fields can be faster or simpler than performing joins, especially if the original system was built that way.
      - **Transitional phase:** It might be an intermediate step towards full normalization.
    - This means there's planned data redundancy. While `siswa_id` now correctly links to the `data_siswa` table, the student's name might exist in both `data_siswa.Nama` and, for example, `data_absensi.nama`. Consistency between these would need to be managed at the application level if both are actively used for data entry/modification.

2.  **Wali Kelas (Homeroom Teacher) Assignment:**

    - The `data_kelas` table manages class information, including assigning a `wali_kelas` (homeroom teacher).
    - It contains an original `wali_kelas` field (presumably storing the teacher's name as text) and a new foreign key `guru_id_wali_kelas` which links to the `id` in the `guru` table.
    - A **`UNIQUE` constraint (`uq_guru_id_wali_kelas`)** is applied to `data_kelas.guru_id_wali_kelas`. This is crucial as it enforces that **a single guru can only be the `wali_kelas` for one class at most**.
    - The `guru` table also has a `wali_kelas` field. The nature of this field in `guru` (e.g., if it's a class name or ID) isn't explicitly defined by a constraint in this script but was likely the original way to denote this. The new FK from `data_kelas` to `guru` is the more robust relational approach.

3.  **Referential Integrity - Specific `ON DELETE` Behavior:**

    - Most foreign keys use `ON DELETE RESTRICT` and `ON UPDATE CASCADE`. This is a common and safe default, preventing deletion of a parent record if child records exist, and propagating primary key updates to foreign keys.
    - A notable exception is the `fk_data_kelas_guru_wali` constraint (linking `data_kelas` to `guru` for the homeroom teacher):
      - It uses **`ON DELETE SET NULL`**. This means if a `guru` record (who is a `wali_kelas`) is deleted, the `guru_id_wali_kelas` field in the `data_kelas` table will be set to `NULL`. The class will continue to exist but will no longer have that specific homeroom teacher assigned through this foreign key. This is a less destructive approach than `RESTRICT` (which would prevent deletion) or `CASCADE` (which would delete the class).

4.  **Collation Strategy:**

    - There's a general move towards `utf8mb4` collations (e.g., `utf8mb4_general_ci`, `utf8mb4_uca1400_ai_ci`), which is good for supporting a wide range of characters, including emojis.
    - Some tables like `data_siswa` retain `latin1_swedish_ci`, and `pengguna` retains `utf8mb4_0900_ai_ci`. This mixed approach might be due to:
      - Original data in those tables.
      - Specific performance or sorting requirements for certain columns in those tables.
      - The `id` columns in `data_siswa` and `guru` are `INT`, so their collation is less critical than `VARCHAR` columns.

5.  **Primary and Unique Keys:**

    - Most tables use a surrogate `id` column as their primary key.
    - The `pengguna` table additionally has a `UNIQUE` constraint on the `username` column, ensuring no two users can have the same username.

6.  **Auto Increment Starting Values:**
    - The `AUTO_INCREMENT` values for `data_siswa`, `guru`, and `pengguna` tables are explicitly set. This usually indicates that either some data was pre-existing, or there's a desire to start numbering from a specific point (e.g., to avoid conflicts if migrating from another system).

## Core Tables:

- **`data_siswa`**: Master table for student information (ID, Name, NISN, NIK, family details, etc.).
- **`guru`**: Master table for teacher information (ID, NIP, name, contact, subject taught, etc.).
- **`data_kelas`**: Information about classes (ID, class name, room, homeroom teacher, academic year).
- **`data_absensi`**: Tracks student attendance, linked to `data_siswa` and `data_kelas`.
- **`data_nilai`**: Stores student grades, linked to `data_siswa` and `data_kelas`.
- **`spp_pembayaran`**: Manages student tuition fee payments, linked to `data_siswa`.
- **`pengguna`**: User accounts for system access, with roles for authorization.

This overview should help in understanding the relationships and some of the rationale behind the schema design. For precise column types, lengths, and all constraints, please refer to the provided SQL script.
