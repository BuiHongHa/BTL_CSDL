package model;

import java.util.Date;

public class ChiTietMuon {
    private String maSach;
    private String tenSach;       // Thêm trường này
    private Date ngayMuon;        // Thêm trường này
    private Date ngayTra;         // Thêm trường này
    private String tinhTrangSach;
    private String trangThaiSach; // "Đang mượn" hoặc "Đã trả"

    // Constructor đầy đủ cho báo cáo lịch sử
    public ChiTietMuon(String maSach, String tenSach, Date ngayMuon, Date ngayTra, String tinhTrangSach, String trangThaiSach) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.ngayMuon = ngayMuon;
        this.ngayTra = ngayTra;
        this.tinhTrangSach = tinhTrangSach;
        this.trangThaiSach = trangThaiSach;
    }

    // Constructor cũ (nếu cần giữ lại cho các chức năng khác)
    public ChiTietMuon(String maSach, String tenSach, String tinhTrangSach) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.tinhTrangSach = tinhTrangSach;
    }

    // Getters
    public String getMaSach() { return maSach; }
    public String getTenSach() { return tenSach; }
    public Date getNgayMuon() { return ngayMuon; }
    public Date getNgayTra() { return ngayTra; }
    public String getTinhTrangSach() { return tinhTrangSach; }
    public String getTrangThaiSach() { return trangThaiSach; }
}