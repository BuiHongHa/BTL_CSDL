package model;

import java.util.Date;

public class PhieuPhat {
    private String maPhieuPhat;
    private String tenNguoiDoc;
    private String sdtNguoiDoc;
    private String tenNhanVien;
    private Date ngayLap;      // Đổi tên từ ngayLapPhieu -> ngayLap
    private Date ngayMuon;
    private Date ngayTra;
    private double tienPhat;   // Đổi int -> double cho khớp với SQL DOUBLE
    private String lyDo;       // Mapping từ ly_do_phat

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

    // Getters
    public String getMaPhieuPhat() { return maPhieuPhat; }
    public String getTenNguoiDoc() { return tenNguoiDoc; }
    public String getSdtNguoiDoc() { return sdtNguoiDoc; }
    public String getTenNhanVien() { return tenNhanVien; }
    public Date getNgayLap() { return ngayLap; }
    public Date getNgayMuon() { return ngayMuon; }
    public Date getNgayTra() { return ngayTra; }
    public double getTienPhat() { return tienPhat; }
    public String getLyDo() { return lyDo; }
}