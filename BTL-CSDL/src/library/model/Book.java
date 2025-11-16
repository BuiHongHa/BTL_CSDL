
package library.model;

public class Book {
    private String maSach;
    private String tenSach;
    private String tacGia;
    private String theLoai;
    private int namXuatBan;

    public Book(String maSach, String tenSach, String tacGia,
                String theLoai, int namXuatBan) {
        this.maSach = maSach;
        this.tenSach = tenSach;
        this.tacGia = tacGia;
        this.theLoai = theLoai;
        this.namXuatBan = namXuatBan;
    }

    public String getMaSach() { return maSach; }
    public String getTenSach() { return tenSach; }
    public String getTacGia() { return tacGia; }
    public String getTheLoai() { return theLoai; }
    public int getNamXuatBan() { return namXuatBan; }
}
