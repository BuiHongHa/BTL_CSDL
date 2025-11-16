package library.dao;

import library.model.Reader;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReaderDAO {

    // ==========================================================
    // 1) Lấy toàn bộ người đọc
    // ==========================================================
    public List<Reader> getAllReaders() throws SQLException {
        List<Reader> list = new ArrayList<>();

        String sql = "SELECT ma_nguoi_doc, ho_ten, don_vi, dia_chi, so_dien_thoai FROM nguoidoc";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Reader(
                        rs.getString("ma_nguoi_doc"),
                        rs.getString("ho_ten"),
                        rs.getString("don_vi"),
                        rs.getString("dia_chi"),
                        rs.getString("so_dien_thoai")
                ));
            }
        }
        return list;
    }

    // ==========================================================
    // 2) Lấy danh sách lớp/bộ môn
    // ==========================================================
    public List<String> getAllUnits() throws SQLException {
        List<String> list = new ArrayList<>();

        String sql = "SELECT DISTINCT don_vi FROM nguoidoc WHERE don_vi IS NOT NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(rs.getString(1));
        }

        return list;
    }

    // ==========================================================
    // 3) Tìm kiếm theo tên
    // ==========================================================
    public List<Reader> searchReaders(String keyword) throws SQLException {
        List<Reader> list = new ArrayList<>();

        String sql = "SELECT * FROM nguoidoc WHERE ho_ten LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Reader(
                        rs.getString("ma_nguoi_doc"),
                        rs.getString("ho_ten"),
                        rs.getString("don_vi"),
                        rs.getString("dia_chi"),
                        rs.getString("so_dien_thoai")
                ));
            }
        }
        return list;
    }

    // ==========================================================
    // 4) Tìm kiếm theo tên + đơn vị
    // ==========================================================
    public List<Reader> searchReadersWithFilter(String keyword, String unit) throws SQLException {
        List<Reader> list = new ArrayList<>();

        String sql = "SELECT * FROM nguoidoc WHERE ho_ten LIKE ? AND don_vi = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, unit);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Reader(
                        rs.getString("ma_nguoi_doc"),
                        rs.getString("ho_ten"),
                        rs.getString("don_vi"),
                        rs.getString("dia_chi"),
                        rs.getString("so_dien_thoai")
                ));
            }
        }
        return list;
    }

    // ==========================================================
    // 5) Lấy người đọc theo khoảng ngày mượn
    // ==========================================================
    private static final String SQL_READERS_BY_BORROW_DATE =
            "SELECT DISTINCT nd.ma_nguoi_doc, nd.ho_ten, nd.don_vi, nd.dia_chi, nd.so_dien_thoai " +
                    "FROM nguoidoc nd " +
                    "JOIN PhieuMuon pm ON nd.ma_nguoi_doc = pm.ma_nguoi_doc " +
                    "WHERE pm.ngay_muon BETWEEN ? AND ?";

    public List<Reader> getReadersByBorrowDateRange(Date from, Date to) throws SQLException {
        List<Reader> result = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_READERS_BY_BORROW_DATE)) {

            ps.setDate(1, from);
            ps.setDate(2, to);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Reader(
                            rs.getString("ma_nguoi_doc"),
                            rs.getString("ho_ten"),
                            rs.getString("don_vi"),
                            rs.getString("dia_chi"),
                            rs.getString("so_dien_thoai")
                    ));
                }
            }
        }
        return result;
    }
}
