package dao;

// import dao.Database; // Đảm bảo bạn đã có file Database.java
import model.NguoiDoc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NguoiDocDao { // Đổi tên file

    /**
     * Lấy danh sách người đọc (hỗ trợ tìm kiếm)
     * Đã cập nhật theo CSDL mới.
     */
    public List<NguoiDoc> getAllNguoiDoc(String keyword) throws SQLException {
        List<NguoiDoc> list = new ArrayList<>();
        Connection conn = Database.getConnection(); // File kết nối trỏ đến 'thu_vien'

        // Chỉ chọn 5 cột
        String sql = "SELECT ma_nguoi_doc, ho_ten, don_vi, dia_chi, so_dien_thoai FROM nguoidoc";

        if (!keyword.isEmpty()) {
            // Tìm theo tên, SĐT hoặc đơn vị
            sql += " WHERE ho_ten LIKE ? OR so_dien_thoai LIKE ? OR don_vi LIKE ?";
        }

        PreparedStatement stmt = conn.prepareStatement(sql);

        if (!keyword.isEmpty()) {
            String kw = "%" + keyword + "%";
            stmt.setString(1, kw);
            stmt.setString(2, kw);
            stmt.setString(3, kw);
        }

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            NguoiDoc nd = new NguoiDoc(
                    rs.getString("ma_nguoi_doc"), // Sửa thành getString
                    rs.getString("ho_ten"),
                    rs.getString("don_vi"),
                    rs.getString("dia_chi"),
                    rs.getString("so_dien_thoai")
            );
            list.add(nd);
        }
        conn.close();
        return list;
    }
}