package dao;

// Giả sử file kết nối của bạn là 'Database.java'
// import dao.Database;
import model.NhanVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Đổi tên file 'NhanVienDao' (chữ 'd' thường) thành 'NhanVienDao' (chuẩn Java)
public class NhanVienDao {

    /**
     * Lấy danh sách nhân viên (hỗ trợ tìm kiếm)
     * Đã cập nhật theo CSDL mới.
     */
    public List<NhanVien> getAllNhanVien(String keyword) throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        // Thay 'Database.getConnection()' bằng tên lớp kết nối của bạn
        Connection conn = Database.getConnection();

        // Chỉ chọn 4 cột
        String sql = "SELECT ma_nhan_vien, ho_ten, chuc_vu, so_dien_thoai FROM nhanvien";

        if (!keyword.isEmpty()) {
            // Chỉ tìm theo tên và SĐT
            sql += " WHERE ho_ten LIKE ? OR so_dien_thoai LIKE ?";
        }

        PreparedStatement stmt = conn.prepareStatement(sql);

        if (!keyword.isEmpty()) {
            String kw = "%" + keyword + "%";
            stmt.setString(1, kw);
            stmt.setString(2, kw);
        }

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            NhanVien nv = new NhanVien(
                    rs.getString("ma_nhan_vien"), // Sửa thành getString
                    rs.getString("ho_ten"),
                    rs.getString("chuc_vu"),
                    rs.getString("so_dien_thoai")
            );
            list.add(nv);
        }
        conn.close();
        return list;
    }
}