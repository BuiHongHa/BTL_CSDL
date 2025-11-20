package model;

import java.util.Date;

public class PhieuMuon {
    private String maPhieuMuon;
    private String maNguoiDoc;
    private String tenNguoiDoc; // Thêm để hiển thị
    private String sdtNguoiDoc; // Thêm để hiển thị
    private String maNhanVien;
    private String tenNhanVien; // Thêm để hiển thị
    private Date ngayMuon;
    private Date ngayTra;
    private String trangThai;   // Tự tính toán: Đang mượn / Đã trả

    public PhieuMuon() {
    }

    // Constructor đầy đủ
    public PhieuMuon(String maPhieuMuon, String maNguoiDoc, String tenNguoiDoc, String sdtNguoiDoc,
                     String maNhanVien, String tenNhanVien, Date ngayMuon, Date ngayTra) {
        this.maPhieuMuon = maPhieuMuon;
        this.maNguoiDoc = maNguoiDoc;
        this.tenNguoiDoc = tenNguoiDoc;
        this.sdtNguoiDoc = sdtNguoiDoc;
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.ngayMuon = ngayMuon;
        this.ngayTra = ngayTra;
        this.trangThai = (ngayTra != null) ? "Đã trả" : "Đang mượn";
    }

    // Getters & Setters
    public String getMaPhieuMuon() { return maPhieuMuon; }
    public void setMaPhieuMuon(String maPhieuMuon) { this.maPhieuMuon = maPhieuMuon; }

    public String getTenNguoiDoc() { return tenNguoiDoc; }
    public void setTenNguoiDoc(String tenNguoiDoc) { this.tenNguoiDoc = tenNguoiDoc; }

    public String getSdtNguoiDoc() { return sdtNguoiDoc; }
    public void setSdtNguoiDoc(String sdtNguoiDoc) { this.sdtNguoiDoc = sdtNguoiDoc; }

    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }

    public Date getNgayMuon() { return ngayMuon; }
    public void setNgayMuon(Date ngayMuon) { this.ngayMuon = ngayMuon; }

    public Date getNgayTra() { return ngayTra; }
    public void setNgayTra(Date ngayTra) { this.ngayTra = ngayTra; }

    public String getTrangThai() { return trangThai; }
}