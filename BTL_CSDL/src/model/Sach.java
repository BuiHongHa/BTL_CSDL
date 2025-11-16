package model;

public class Sach {
    private String maSach; // Đã sửa thành String
    private String tenSach;
    private String tacGia;
    private String theLoai;
    private int namXuatBan;
    private String hinhAnh; // Thêm cột mới

    // Constructor để lấy dữ liệu (cho DAO)
    public Sach(String maSach, String tenSach, String tacGia, String theLoai, int namXuatBan, String hinhAnh) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.tacGia = tacGia;
        this.theLoai = theLoai;
        this.namXuatBan = namXuatBan;
        this.hinhAnh = hinhAnh;
    }

    // Getters
    public String getMaSach() { return maSach; }
    public String getTenSach() { return tenSach; }
    public String getTacGia() { return tacGia; }
    public String getTheLoai() { return theLoai; }
    public int getNamXuatBan() { return namXuatBan; }
    public String getHinhAnh() { return hinhAnh; }
}