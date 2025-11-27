package model;

import java.util.Date;

public class CheckinInfo {
    private String maPhieuGui; // Khớp với cột 'ma_phieu_gui'
    private Date ngayYeuCau;
    private String lyDo;
    private String hoTen;      // Lấy từ bảng nguoidoc
    private String soDienThoai;
    private String donVi;

    public CheckinInfo(String maPhieuGui, Date ngayYeuCau, String lyDo,
                       String hoTen, String soDienThoai, String donVi) {
        this.maPhieuGui = maPhieuGui;
        this.ngayYeuCau = ngayYeuCau;
        this.lyDo = lyDo;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.donVi = donVi;
    }

    // Getters
    public String getMaPhieuGui() { return maPhieuGui; }
    public Date getNgayYeuCau() { return ngayYeuCau; }
    public String getLyDo() { return lyDo; }
    public String getHoTen() { return hoTen; }
    public String getSoDienThoai() { return soDienThoai; }
    public String getDonVi() { return donVi; }
}