package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    // THAY ĐỔI CÁC THÔNG SỐ NÀY CHO PHÙ HỢP VỚI MÁY CỦA BẠN
    private static final String URL = "jdbc:mysql://localhost:3306/thu_vien"; // Tên database là 'qltv'
    private static final String USER = "root"; // Tên đăng nhập MySQL
    private static final String PASSWORD = "123456"; // Mật khẩu MySQL của bạn

    public static Connection getConnection() {
        try {
            // 1. Nạp driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. Tạo và trả về kết nối
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            // Ném lỗi Runtime để ứng dụng dừng lại nếu không kết nối được
            throw new RuntimeException("Không thể kết nối đến CSDL!", e);
        }
    }
}