package model;

import java.util.Date;

public class PhieuPhat {
    private String maPhieuPhat;
    private String tenNguoiDoc;
    private String sdtNguoiDoc;
    private String tenNhanVien;
    private Date ngayLap;
    private Date ngayMuon;
    private Date ngayTra;
    private double tienPhat;
    private String lyDo;

    public PhieuPhat() {} // 👉 Constructor rỗng bắt buộc cho JTable, DAO, JSON…

    public PhieuPhat(String maPhieuPhat, String tenNguoiDoc, String sdtNguoiDoc,
                     String tenNhanVien, Date ngayLap, Date ngayMuon,
                     Date ngayTra, double tienPhat, String lyDo) {
        this.maPhieuPhat = maPhieuPhat;
        this.tenNguoiDoc = tenNguoiDoc;
        this.sdtNguoiDoc = sdtNguoiDoc;
        this.tenNhanVien = tenNhanVien;
        this.ngayLap = ngayLap;
        this.ngayMuon = ngayMuon;
        this.ngayTra = ngayTra;
        this.tienPhat = tienPhat;
        this.lyDo = lyDo;
    }

    // ===== GETTERS =====
    public String getMaPhieuPhat() { return maPhieuPhat; }
    public String getTenNguoiDoc() { return tenNguoiDoc; }
    public String getSdtNguoiDoc() { return sdtNguoiDoc; }
    public String getTenNhanVien() { return tenNhanVien; }
    public Date getNgayLap() { return ngayLap; }
    public Date getNgayMuon() { return ngayMuon; }
    public Date getNgayTra() { return ngayTra; }
    public double getTienPhat() { return tienPhat; }
    public String getLyDo() { return lyDo; }

    // ===== SETTERS (DÙNG KHI CẦN SỬA/IMPORT) =====
    public void setMaPhieuPhat(String maPhieuPhat) { this.maPhieuPhat = maPhieuPhat; }
    public void setTenNguoiDoc(String tenNguoiDoc) { this.tenNguoiDoc = tenNguoiDoc; }
    public void setSdtNguoiDoc(String sdtNguoiDoc) { this.sdtNguoiDoc = sdtNguoiDoc; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }
    public void setNgayLap(Date ngayLap) { this.ngayLap = ngayLap; }
    public void setNgayMuon(Date ngayMuon) { this.ngayMuon = ngayMuon; }
    public void setNgayTra(Date ngayTra) { this.ngayTra = ngayTra; }
    public void setTienPhat(double tienPhat) { this.tienPhat = tienPhat; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
}
