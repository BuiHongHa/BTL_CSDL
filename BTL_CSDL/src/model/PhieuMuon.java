package model;

import java.sql.Date;

// Model này chứa dữ liệu JOIN cho JTable (danh sách phiếu mượn)
public class PhieuMuon {
    private String maPhieuMuon;
    private String tenNguoiDoc;
    private String sdtNguoiDoc;
    private String tenNhanVien;
    private Date ngayMuon;
    private Date ngayTra;
    private String trangThai;

    public PhieuMuon(String maPhieuMuon, String tenNguoiDoc, String sdtNguoiDoc, String tenNhanVien, Date ngayMuon, Date ngayTra) {
        this.maPhieuMuon = maPhieuMuon;
        this.tenNguoiDoc = tenNguoiDoc;
        this.sdtNguoiDoc = sdtNguoiDoc;
        this.tenNhanVien = tenNhanVien;
        this.ngayMuon = ngayMuon;
        this.ngayTra = ngayTra;
        // Tự động tính trạng thái
        this.trangThai = (ngayTra == null) ? "Đang mượn" : "Đã trả";
    }

    // Getters
    public String getMaPhieuMuon() { return maPhieuMuon; }
    public String getTenNguoiDoc() { return tenNguoiDoc; }
    public String getSdtNguoiDoc() { return sdtNguoiDoc; }
    public String getTenNhanVien() { return tenNhanVien; }
    public Date getNgayMuon() { return ngayMuon; }
    public Date getNgayTra() { return ngayTra; }
    public String getTrangThai() { return trangThai; }
}