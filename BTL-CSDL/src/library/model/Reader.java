
package library.model;

public class Reader {
    private String maNguoiDoc;
    private String hoTen;
    private String donVi;
    private String diaChi;
    private String soDienThoai;

    public Reader(String maNguoiDoc, String hoTen,
                  String donVi, String diaChi, String soDienThoai) {
        this.maNguoiDoc = maNguoiDoc;
        this.hoTen = hoTen;
        this.donVi = donVi;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
    }

    public String getMaNguoiDoc() { return maNguoiDoc; }
    public String getHoTen() { return hoTen; }
    public String getDonVi() { return donVi; }
    public String getDiaChi() { return diaChi; }
    public String getSoDienThoai() { return soDienThoai; }
}
