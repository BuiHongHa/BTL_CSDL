package dao;

import model.CheckinInfo;
import util.Database;   // 🔥 THÊM DÒNG NÀY
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CheckinDao {

    /**
     * Tìm kiếm và lọc danh sách Check-in
     */
    public List<CheckinInfo> getTraCuuCheckin(String keyword, String dateFrom, String dateTo) throws SQLException {
        List<CheckinInfo> list = new ArrayList<>();

        // 🔗 Kết nối CSDL từ Database trong util
        Connection conn = Database.getConnection();

        String sql = """
            SELECT
                c.ma_phieu_gui,
                c.ngay_yeu_cau,
                c.ly_do,
                nd.ho_ten,
                nd.so_dien_thoai,
                nd.don_vi
            FROM checkin c
            JOIN nguoidoc nd ON c.ma_nguoi_doc = nd.ma_nguoi_doc
            """;

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = "%" + keyword.trim() + "%";
            conditions.add("""
                (nd.ho_ten LIKE ? 
                 OR nd.so_dien_thoai LIKE ? 
                 OR nd.don_vi LIKE ? 
                 OR c.ly_do LIKE ? 
                 OR c.ma_phieu_gui LIKE ?)
            """);
            for (int i = 0; i < 5; i++) params.add(kw);
        }

        if (isValidDate(dateFrom)) {
            conditions.add("c.ngay_yeu_cau >= ?");
            params.add(dateFrom);
        }
        if (isValidDate(dateTo)) {
            conditions.add("c.ngay_yeu_cau <= ?");
            params.add(dateTo);
        }

        if (!conditions.isEmpty()) {
            sql += " WHERE " + String.join(" AND ", conditions);
        }

        sql += " ORDER BY c.ngay_yeu_cau DESC";

        PreparedStatement stmt = conn.prepareStatement(sql);
        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            list.add(new CheckinInfo(
                    rs.getString("ma_phieu_gui"),
                    rs.getDate("ngay_yeu_cau"),
                    rs.getString("ly_do"),
                    rs.getString("ho_ten"),
                    rs.getString("so_dien_thoai"),
                    rs.getString("don_vi")
            ));
        }
        conn.close();
        return list;
    }

    private boolean isValidDate(String date) {
        return date != null && date.matches("\\d{4}-\\d{2}-\\d{2}");
    }
}
