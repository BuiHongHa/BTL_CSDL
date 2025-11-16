package model;

// Model này chứa chi tiết sách của 1 phiếu mượn (cho hóa đơn)
public class ChiTietMuon {
    private String maSach;
    private String tenSach;
    private String tinhTrangSach;

    public ChiTietMuon(String maSach, String tenSach, String tinhTrangSach) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.tinhTrangSach = tinhTrangSach;
    }

    // Getters
    public String getMaSach() { return maSach; }
    public String getTenSach() { return tenSach; }
    public String getTinhTrangSach() { return tinhTrangSach; }
}