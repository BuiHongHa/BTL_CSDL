package dao;

// import dao.Database; // Đảm bảo bạn đã có file Database.java
import model.Sach;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SachDao { // Đổi tên file 'SachDao' thành 'SachDao'

    /**
     * Lấy danh sách sách (hỗ trợ cả tìm kiếm VÀ lọc)
     * Đã cập nhật theo CSDL mới (bỏ so_luong)
     */
    public List<Sach> getAllSach(String keyword, String theLoai) throws SQLException {
        List<Sach> list = new ArrayList<>();
        Connection conn = Database.getConnection(); // File kết nối trỏ đến 'thu_vien'

        // Bỏ 'so_luong', thêm 'hinhAnh'
        String sql = "SELECT ma_sach, ten_sach, tac_gia, the_loai, nam_xuat_ban, hinhAnh FROM sach";

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        // 1. Lọc theo Keyword
        if (!keyword.isEmpty()) {
            conditions.add("(ten_sach LIKE ? OR tac_gia LIKE ?)");
            String kw = "%" + keyword + "%";
            params.add(kw);
            params.add(kw);
        }

        // 2. Lọc theo Thể Loại
        if (theLoai != null && !theLoai.isEmpty() && !theLoai.equals("Tất cả Thể Loại")) {
            conditions.add("the_loai = ?");
            params.add(theLoai);
        }

        if (!conditions.isEmpty()) {
            sql += " WHERE " + String.join(" AND ", conditions);
        }

        PreparedStatement stmt = conn.prepareStatement(sql);

        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Sach s = new Sach(
                    rs.getString("ma_sach"), // Sửa thành getString
                    rs.getString("ten_sach"),
                    rs.getString("tac_gia"),
                    rs.getString("the_loai"),
                    rs.getInt("nam_xuat_ban"),
                    rs.getString("hinhAnh")
            );
            list.add(s);
        }
        conn.close();
        return list;
    }

    /**
     * Lấy danh sách các thể loại duy nhất (Vẫn hoạt động)
     */
    public List<String> getDistinctTheLoai() throws SQLException {
        List<String> list = new ArrayList<>();
        Connection conn = Database.getConnection();
        String sql = "SELECT DISTINCT the_loai FROM sach WHERE the_loai IS NOT NULL ORDER BY the_loai";

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            list.add(rs.getString("the_loai"));
        }
        conn.close();
        return list;
    }
}