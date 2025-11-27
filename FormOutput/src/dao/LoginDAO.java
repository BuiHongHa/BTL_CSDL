package dao;

import util.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginDAO {

    // Đăng nhập nhân viên (Quản lý)
    public static boolean loginNhanVien(String ma) {
        return checkExists("SELECT 1 FROM nhanvien WHERE ma_nhan_vien = ?", ma);
    }

    // Đăng nhập người đọc
    public static boolean loginNguoiDoc(String ma) {
        return checkExists("SELECT 1 FROM nguoidoc WHERE ma_nguoi_doc = ?", ma);
    }

    // Hàm chung kiểm tra tồn tại
    private static boolean checkExists(String sql, String ma) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ma);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
