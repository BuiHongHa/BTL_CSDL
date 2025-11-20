package dao;

import model.Sach;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SachDao {

    public List<Sach> getAllSach(String keyword, String theLoai) throws SQLException {
        List<Sach> list = new ArrayList<>();
        Connection conn = Database.getConnection();

        // CHỈ lấy 5 cột đúng như database
        String sql = "SELECT ma_sach, ten_sach, tac_gia, the_loai, nam_xuat_ban FROM sach";

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        // Lọc keyword
        if (!keyword.isEmpty()) {
            conditions.add("(ten_sach LIKE ? OR tac_gia LIKE ?)");
            String kw = "%" + keyword + "%";
            params.add(kw);
            params.add(kw);
        }

        // Lọc thể loại
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
                    rs.getString("ma_sach"),
                    rs.getString("ten_sach"),
                    rs.getString("tac_gia"),
                    rs.getString("the_loai"),
                    rs.getInt("nam_xuat_ban")
            );
            list.add(s);
        }

        conn.close();
        return list;
    }

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
