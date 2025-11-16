package model;

public class NhanVien {
    private String maNhanVien; // Đã sửa thành String
    private String hoTen;
    private String chucVu;
    private String sdt; // Đổi tên 'so_dien_thoai' thành 'sdt' cho ngắn gọn

    public NhanVien(String maNhanVien, String hoTen, String chucVu, String sdt) {
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.chucVu = chucVu;
        this.sdt = sdt;
    }

    // Getters
    public String getMaNhanVien() { return maNhanVien; }
    public String getHoTen() { return hoTen; }
    public String getChucVu() { return chucVu; }
    public String getSdt() { return sdt; }
}