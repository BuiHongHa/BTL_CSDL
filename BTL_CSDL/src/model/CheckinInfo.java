package model;

import java.sql.Date;

// Model này chứa dữ liệu JOIN cho JTable (danh sách check-in)
public class CheckinInfo {
    private String maPhieuGui;
    private Date ngayYeuCau;
    private String lyDo;
    private String tenNguoiDoc;
    private String sdtNguoiDoc;
    private String donViNguoiDoc;

    public CheckinInfo(String maPhieuGui, Date ngayYeuCau, String lyDo, String tenNguoiDoc, String sdtNguoiDoc, String donViNguoiDoc) {
        this.maPhieuGui = maPhieuGui;
        this.ngayYeuCau = ngayYeuCau;
        this.lyDo = lyDo;
        this.tenNguoiDoc = tenNguoiDoc;
        this.sdtNguoiDoc = sdtNguoiDoc;
        this.donViNguoiDoc = donViNguoiDoc;
    }

    // Getters
    public String getMaPhieuGui() { return maPhieuGui; }
    public Date getNgayYeuCau() { return ngayYeuCau; }
    public String getLyDo() { return lyDo; }
    public String getTenNguoiDoc() { return tenNguoiDoc; }
    public String getSdtNguoiDoc() { return sdtNguoiDoc; }
    public String getDonViNguoiDoc() { return donViNguoiDoc; }
}