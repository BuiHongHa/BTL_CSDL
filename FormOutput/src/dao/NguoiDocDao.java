package dao;

import model.NguoiDoc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.Database;

public class NguoiDocDao {

    /**
     * Lấy danh sách người đọc (hỗ trợ tìm kiếm)
     */
    public List<NguoiDoc> getAllNguoiDoc(String keyword) throws SQLException {
        List<NguoiDoc> list = new ArrayList<>();
        Connection conn = Database.getConnection();

        String sql = "SELECT ma_nguoi_doc, ho_ten, don_vi, dia_chi, so_dien_thoai FROM nguoidoc";

        if (!keyword.isEmpty()) {
            sql += " WHERE ma_nguoi_doc LIKE ?"
                    + " OR ho_ten LIKE ?"
                    + " OR so_dien_thoai LIKE ?"
                    + " OR don_vi LIKE ?"
                    + " OR dia_chi LIKE ?";
        }

        PreparedStatement ps = conn.prepareStatement(sql);

        if (!keyword.isEmpty()) {
            for (int i = 1; i <= 5; i++) {
                ps.setString(i, "%" + keyword + "%");
            }
        }

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new NguoiDoc(
                    rs.getString("ma_nguoi_doc"),
                    rs.getString("ho_ten"),
                    rs.getString("don_vi"),
                    rs.getString("dia_chi"),
                    rs.getString("so_dien_thoai")
            ));
        }
        conn.close();
        return list;
    }


    // ================================
    // 📌 LẤY SÁCH NGƯỜI ĐỌC ĐANG MƯỢN
    // ================================
    public List<String> getSachNguoiDocDaMuon(String maNguoiDoc) throws SQLException {
        List<String> list = new ArrayList<>();
        Connection conn = Database.getConnection();

        String sql = """
            SELECT s.ten_sach 
            FROM chitiethmuon ctm
            JOIN sach s ON ctm.ma_sach = s.ma_sach
            JOIN phieumuon pm ON ctm.ma_phieu_muon = pm.ma_phieu_muon
            WHERE pm.ma_nguoi_doc = ? AND pm.ngay_tra IS NULL
        """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, maNguoiDoc);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) list.add(rs.getString(1));

        conn.close();
        return list;
    }

    // ================================
    // 📌 LẤY SÁCH NGƯỜI ĐỌC ĐÃ TRẢ
    // ================================
    public List<String> getSachNguoiDocDaTra(String maNguoiDoc) throws SQLException {
        List<String> list = new ArrayList<>();
        Connection conn = Database.getConnection();

        String sql = """
            SELECT s.ten_sach 
            FROM chitiethmuon ctm
            JOIN sach s ON ctm.ma_sach = s.ma_sach
            JOIN phieumuon pm ON ctm.ma_phieu_muon = pm.ma_phieu_muon
            WHERE pm.ma_nguoi_doc = ? AND pm.ngay_tra IS NOT NULL
        """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, maNguoiDoc);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) list.add(rs.getString(1));

        conn.close();
        return list;
    }
}
