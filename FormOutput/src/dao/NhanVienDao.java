package dao;

import model.NhanVien;
import util.Database;   // 🔥 THÊM DÒNG NÀY (cực quan trọng)

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDao {

    /**
     * Lấy danh sách nhân viên (hỗ trợ tìm kiếm)
     * Đã cập nhật theo CSDL mới.
     */
    public List<NhanVien> getAllNhanVien(String keyword) throws SQLException {
        List<NhanVien> list = new ArrayList<>();

        // 🔗 Dùng kết nối chung từ util.Database
        Connection conn = Database.getConnection();

        String sql = "SELECT ma_nhan_vien, ho_ten, chuc_vu, so_dien_thoai FROM nhanvien";

        if (keyword != null && !keyword.trim().isEmpty()) {   // 🛠 Fix null-safe
            sql += " WHERE ho_ten LIKE ? OR so_dien_thoai LIKE ?";
        }

        PreparedStatement stmt = conn.prepareStatement(sql);

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = "%" + keyword.trim() + "%";
            stmt.setString(1, kw);
            stmt.setString(2, kw);
        }

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            list.add(new NhanVien(
                    rs.getString("ma_nhan_vien"),
                    rs.getString("ho_ten"),
                    rs.getString("chuc_vu"),
                    rs.getString("so_dien_thoai")
            ));
        }
        conn.close();
        return list;
    }
}
