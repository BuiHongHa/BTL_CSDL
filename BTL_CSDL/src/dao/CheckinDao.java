package dao;

// import dao.Database; // Đảm bảo bạn có file Database.java
import model.CheckinInfo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CheckinDao {

    /**
     * Lấy danh sách check-in (JOIN với NguoiDoc)
     * Hỗ trợ tìm kiếm (keyword) và lọc theo khoảng ngày (dateFrom, dateTo)
     */
    public List<CheckinInfo> getTraCuuCheckin(String keyword, String dateFrom, String dateTo) throws SQLException {
        List<CheckinInfo> list = new ArrayList<>();
        Connection conn = Database.getConnection(); // Trỏ đến CSDL 'thu_vien'

        // JOIN 2 bảng
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

        // 1. Lọc theo Keyword (Tìm theo tên NĐ, SĐT, Lý do, Mã phiếu)
        if (!keyword.isEmpty()) {
            conditions.add("(nd.ho_ten LIKE ? OR nd.so_dien_thoai LIKE ? OR c.ly_do LIKE ? OR c.ma_phieu_gui LIKE ?)");
            String kw = "%" + keyword + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        // 2. Lọc theo Ngày Bắt Đầu (Yêu cầu định dạng YYYY-MM-DD)
        if (isValidDate(dateFrom)) {
            conditions.add("c.ngay_yeu_cau >= ?");
            params.add(dateFrom);
        }

        // 3. Lọc theo Ngày Kết Thúc (Yêu cầu định dạng YYYY-MM-DD)
        if (isValidDate(dateTo)) {
            conditions.add("c.ngay_yeu_cau <= ?");
            params.add(dateTo);
        }

        if (!conditions.isEmpty()) {
            sql += " WHERE " + String.join(" AND ", conditions);
        }

        sql += " ORDER BY c.ngay_yeu_cau DESC"; // Sắp xếp

        PreparedStatement stmt = conn.prepareStatement(sql);
        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            CheckinInfo ci = new CheckinInfo(
                    rs.getString("ma_phieu_gui"),
                    rs.getDate("ngay_yeu_cau"),
                    rs.getString("ly_do"),
                    rs.getString("ho_ten"),
                    rs.getString("so_dien_thoai"),
                    rs.getString("don_vi")
            );
            list.add(ci);
        }
        conn.close();
        return list;
    }

    // Hàm helper đơn giản để kiểm tra định dạng ngày
    private boolean isValidDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return false;
        }
        // Kiểm tra cơ bản, bạn có thể dùng SimpleDateFormat.parse nếu muốn chặt chẽ hơn
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }
}