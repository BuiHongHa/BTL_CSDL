package model;

import java.util.Date;

public class PhieuMuon {
    private String maPhieuMuon;
    private String maNguoiDoc;
    private String tenNguoiDoc;   // Dùng để hiển thị
    private String sdtNguoiDoc;   // Dùng để hiển thị
    private String maNhanVien;
    private String tenNhanVien;   // Dùng để hiển thị
    private Date ngayMuon;
    private Date ngayTra;
    private String trangThai;     // Đang mượn / Đã trả

    public PhieuMuon() {
    }

    // Constructor đầy đủ (dùng cho tra cứu tổng quát)
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

    // Constructor dùng cho getPhieuMuonTheoNguoiDoc (không cần mã ND/NV)
    public PhieuMuon(String maPhieuMuon,
                     String tenNguoiDoc,
                     String sdtNguoiDoc,
                     String tenNhanVien,
                     Date ngayMuon,
                     Date ngayTra,
                     String trangThai) {
        this.maPhieuMuon = maPhieuMuon;
        this.tenNguoiDoc = tenNguoiDoc;
        this.sdtNguoiDoc = sdtNguoiDoc;
        this.tenNhanVien = tenNhanVien;
        this.ngayMuon = ngayMuon;
        this.ngayTra = ngayTra;
        this.trangThai = trangThai;
    }

    // ================= GETTER / SETTER =================

    public String getMaPhieuMuon() {
        return maPhieuMuon;
    }

    public void setMaPhieuMuon(String maPhieuMuon) {
        this.maPhieuMuon = maPhieuMuon;
    }

    public String getMaNguoiDoc() {
        return maNguoiDoc;
    }

    public void setMaNguoiDoc(String maNguoiDoc) {
        this.maNguoiDoc = maNguoiDoc;
    }

    public String getTenNguoiDoc() {
        return tenNguoiDoc;
    }

    public void setTenNguoiDoc(String tenNguoiDoc) {
        this.tenNguoiDoc = tenNguoiDoc;
    }

    public String getSdtNguoiDoc() {
        return sdtNguoiDoc;
    }

    public void setSdtNguoiDoc(String sdtNguoiDoc) {
        this.sdtNguoiDoc = sdtNguoiDoc;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public Date getNgayMuon() {
        return ngayMuon;
    }

    public void setNgayMuon(Date ngayMuon) {
        this.ngayMuon = ngayMuon;
    }

    public Date getNgayTra() {
        return ngayTra;
    }

    public void setNgayTra(Date ngayTra) {
        this.ngayTra = ngayTra;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
