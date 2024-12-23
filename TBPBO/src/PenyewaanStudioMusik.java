import java.sql.*;
import java.text.*;
import java.util.*;

interface OperasiPenyewaan {
    void tambahPenyewaan(Penyewaan penyewaan) throws SQLException;
    Penyewaan ambilPenyewaan(int id) throws SQLException;
    void perbaruiPenyewaan(Penyewaan penyewaan) throws SQLException;
    void hapusPenyewaan(int id) throws SQLException;
    List<Penyewaan> ambilSemuaPenyewaan() throws SQLException;
}

abstract class Penyewaan {
    protected int id;
    protected String namaPelanggan;
    protected String alatMusik;
    protected java.util.Date tanggalPenyewaan;  // Menggunakan java.util.Date
    protected int durasiPenyewaan;

    public Penyewaan(int id, String namaPelanggan, String alatMusik, java.util.Date tanggalPenyewaan, int durasiPenyewaan) {
        this.id = id;
        this.namaPelanggan = namaPelanggan;
        this.alatMusik = alatMusik;
        this.tanggalPenyewaan = tanggalPenyewaan;
        this.durasiPenyewaan = durasiPenyewaan;
    }

    // Getter untuk tanggalPenyewaan
    public java.util.Date getTanggalPenyewaan() {
        return tanggalPenyewaan;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return "ID Penyewaan: " + id + ", Pelanggan: " + namaPelanggan + ", Alat Musik: " + alatMusik + 
               ", Tanggal: " + sdf.format(tanggalPenyewaan) + ", Durasi: " + durasiPenyewaan + " hari";
    }

    public int getId() {
        return id;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public String getAlatMusik() {
        return alatMusik;
    }

    public int getDurasiPenyewaan() {
        return durasiPenyewaan;
    }
}

class PenyewaanStudio extends Penyewaan {
    public PenyewaanStudio(int id, String namaPelanggan, String alatMusik, java.util.Date tanggalPenyewaan, int durasiPenyewaan) {
        super(id, namaPelanggan, alatMusik, tanggalPenyewaan, durasiPenyewaan);
    }
}

class PengelolaPenyewaan implements OperasiPenyewaan {
    private Connection koneksi;

    public PengelolaPenyewaan(String url, String pengguna, String sandi) throws SQLException {
        koneksi = DriverManager.getConnection(url, pengguna, sandi);
    }

    @Override
    public void tambahPenyewaan(Penyewaan penyewaan) throws SQLException {
        String query = "INSERT INTO penyewaan (nama_pelanggan, alat_musik, tanggal_penyewaan, durasi_penyewaan) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = koneksi.prepareStatement(query)) {
            stmt.setString(1, penyewaan.getNamaPelanggan());
            stmt.setString(2, penyewaan.getAlatMusik());
            stmt.setDate(3, new java.sql.Date(penyewaan.getTanggalPenyewaan().getTime()));  // Convert to SQL Date
            stmt.setInt(4, penyewaan.getDurasiPenyewaan());
            stmt.executeUpdate();
        }
    }

    @Override
    public Penyewaan ambilPenyewaan(int id) throws SQLException {
        String query = "SELECT * FROM penyewaan WHERE id = ?";
        try (PreparedStatement stmt = koneksi.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int penyewaanId = rs.getInt("id");
                String namaPelanggan = rs.getString("nama_pelanggan");
                String alatMusik = rs.getString("alat_musik");
                java.util.Date tanggalPenyewaan = rs.getDate("tanggal_penyewaan");
                int durasiPenyewaan = rs.getInt("durasi_penyewaan");
                return new PenyewaanStudio(penyewaanId, namaPelanggan, alatMusik, tanggalPenyewaan, durasiPenyewaan);
            }
        }
        return null;
    }

    @Override
    public void perbaruiPenyewaan(Penyewaan penyewaan) throws SQLException {
        String query = "UPDATE penyewaan SET nama_pelanggan = ?, alat_musik = ?, tanggal_penyewaan = ?, durasi_penyewaan = ? WHERE id = ?";
        try (PreparedStatement stmt = koneksi.prepareStatement(query)) {
            stmt.setString(1, penyewaan.getNamaPelanggan());
            stmt.setString(2, penyewaan.getAlatMusik());
            stmt.setDate(3, new java.sql.Date(penyewaan.getTanggalPenyewaan().getTime()));  // Convert to SQL Date
            stmt.setInt(4, penyewaan.getDurasiPenyewaan());
            stmt.setInt(5, penyewaan.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void hapusPenyewaan(int id) throws SQLException {
        String query = "DELETE FROM penyewaan WHERE id = ?";
        try (PreparedStatement stmt = koneksi.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Penyewaan> ambilSemuaPenyewaan() throws SQLException {
        List<Penyewaan> daftarPenyewaan = new ArrayList<>();
        String query = "SELECT * FROM penyewaan";
        try (Statement stmt = koneksi.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int penyewaanId = rs.getInt("id");
                String namaPelanggan = rs.getString("nama_pelanggan");
                String alatMusik = rs.getString("alat_musik");
                java.util.Date tanggalPenyewaan = rs.getDate("tanggal_penyewaan");
                int durasiPenyewaan = rs.getInt("durasi_penyewaan");
                daftarPenyewaan.add(new PenyewaanStudio(penyewaanId, namaPelanggan, alatMusik, tanggalPenyewaan, durasiPenyewaan));
            }
        }
        return daftarPenyewaan;
    }

    public void tutupKoneksi() throws SQLException {
        koneksi.close();
    }
}

public class PenyewaanStudioMusik {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            PengelolaPenyewaan pengelola = new PengelolaPenyewaan("jdbc:postgresql://localhost:5432/studio_musik", "postgres", "Farhan213008");

            boolean isRunning = true;
            while (isRunning) {
                System.out.println("\nMenu:");
                System.out.println("1. Tambah Penyewaan");
                System.out.println("2. Lihat Penyewaan");
                System.out.println("3. Perbarui Penyewaan");
                System.out.println("4. Hapus Penyewaan");
                System.out.println("5. Keluar");
                System.out.print("Pilih opsi: ");
                int pilihan = scanner.nextInt();
                scanner.nextLine();  // consume newline

                switch (pilihan) {
                    case 1:  // Tambah Penyewaan
                        System.out.print("Masukkan nama pelanggan: ");
                        String namaPelanggan = scanner.nextLine();
                        System.out.print("Masukkan alat musik yang disewa: ");
                        String alatMusik = scanner.nextLine();
                        java.util.Date tanggalPenyewaan = new java.util.Date();  // Tanggal saat ini
                        System.out.println("Tanggal Penyewaan: " + new SimpleDateFormat("yyyy-MM-dd").format(tanggalPenyewaan));
                        System.out.print("Masukkan durasi penyewaan (dalam hari): ");
                        int durasiPenyewaan = scanner.nextInt();
                        scanner.nextLine();  // consume newline
                        Penyewaan penyewaan1 = new PenyewaanStudio(0, namaPelanggan, alatMusik, tanggalPenyewaan, durasiPenyewaan);
                        pengelola.tambahPenyewaan(penyewaan1);
                        break;

                    case 2:  // Lihat Semua Penyewaan
                        List<Penyewaan> penyewaanList = pengelola.ambilSemuaPenyewaan();
                        for (Penyewaan penyewaan : penyewaanList) {
                            System.out.println(penyewaan);
                        }
                        break;

                    case 3:  // Perbarui Penyewaan
                        System.out.print("Masukkan ID Penyewaan yang akan diperbarui: ");
                        int idUpdate = scanner.nextInt();
                        scanner.nextLine();  // consume newline
                        Penyewaan penyewaanUpdate = pengelola.ambilPenyewaan(idUpdate);
                        if (penyewaanUpdate != null) {
                            System.out.print("Masukkan nama pelanggan baru: ");
                            String namaBaru = scanner.nextLine();
                            System.out.print("Masukkan alat musik baru: ");
                            String alatBaru = scanner.nextLine();
                            System.out.print("Masukkan durasi penyewaan baru (dalam hari): ");
                            int durasiBaru = scanner.nextInt();
                            scanner.nextLine();  // consume newline
                            penyewaanUpdate = new PenyewaanStudio(idUpdate, namaBaru, alatBaru, penyewaanUpdate.getTanggalPenyewaan(), durasiBaru);
                            pengelola.perbaruiPenyewaan(penyewaanUpdate);
                        } else {
                            System.out.println("Penyewaan tidak ditemukan.");
                        }
                        break;

                    case 4:  // Hapus Penyewaan
                        System.out.print("Masukkan ID Penyewaan yang akan dihapus: ");
                        int idDelete = scanner.nextInt();
                        pengelola.hapusPenyewaan(idDelete);
                        break;

                    case 5:  // Keluar
                        isRunning = false;
                        break;

                    default:
                        System.out.println("Opsi tidak valid.");
                        break;
                }
            }

            scanner.close();
            pengelola.tutupKoneksi();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}