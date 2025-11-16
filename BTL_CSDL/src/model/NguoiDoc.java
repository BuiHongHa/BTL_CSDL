package model;

public class NguoiDoc {
    private String maNguoiDoc; // Đã sửa thành String
    private String hoTen;
    private String donVi; // Cột mới
    private String diaChi;
    private String soDienThoai; // Đổi tên từ sdt

    public NguoiDoc(String maNguoiDoc, String hoTen, String donVi, String diaChi, String soDienThoai) {
        this.maNguoiDoc = maNguoiDoc;
        this.hoTen = hoTen;
        this.donVi = donVi;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
    }

    // Getters
    public String getMaNguoiDoc() { return maNguoiDoc; }
    public String getHoTen() { return hoTen; }
    public String getDonVi() { return donVi; }
    public String getDiaChi() { return diaChi; }
    public String getSoDienThoai() { return soDienThoai; }
}