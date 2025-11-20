package dao;

import model.ChiTietMuon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietMuonDao {

    // === 1. LẤY CHI TIẾT THEO MÃ PHIẾU (Dùng cho Hóa Đơn Phiếu Mượn) ===
    public List<ChiTietMuon> getChiTietCuaPhieu(String maPhieuMuon) throws SQLException {
        List<ChiTietMuon> list = new ArrayList<>();
        Connection conn = Database.getConnection();

        String sql = """
            SELECT s.ma_sach, s.ten_sach, ctm.tinh_trang_sach 
            FROM chitietmuon ctm
            JOIN sach s ON ctm.ma_sach = s.ma_sach
            WHERE ctm.ma_phieu_muon = ?
            """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, maPhieuMuon);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            list.add(new ChiTietMuon(
                    rs.getString("ma_sach"),
                    rs.getString("ten_sach"),
                    rs.getString("tinh_trang_sach")
            ));
        }
        conn.close();
        return list;
    }

    // === 2. LẤY LỊCH SỬ MƯỢN THEO NGƯỜI ĐỌC (Dùng cho Báo Cáo Bạn Đọc) ===
    public List<ChiTietMuon> getLichSuMuonTheoNguoiDoc(String maNguoiDoc) throws SQLException {
        List<ChiTietMuon> list = new ArrayList<>();
        Connection conn = Database.getConnection();

        // JOIN 3 bảng để lấy thông tin đầy đủ
        String sql = """
            SELECT 
                ctm.ma_sach, 
                s.ten_sach, 
                pm.ngay_muon, 
                pm.ngay_tra, 
                ctm.tinh_trang_sach,
                CASE 
                    WHEN pm.ngay_tra IS NOT NULL THEN 'Đã trả'
                    ELSE 'Đang mượn'
                END AS trang_thai_muon
            FROM chitietmuon ctm
            JOIN phieumuon pm ON ctm.ma_phieu_muon = pm.ma_phieu_muon
            JOIN sach s ON ctm.ma_sach = s.ma_sach
            WHERE pm.ma_nguoi_doc = ?
            ORDER BY pm.ngay_muon DESC
            """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, maNguoiDoc);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            list.add(new ChiTietMuon(
                    rs.getString("ma_sach"),
                    rs.getString("ten_sach"),
                    rs.getDate("ngay_muon"),
                    rs.getDate("ngay_tra"),
                    rs.getString("tinh_trang_sach"),
                    rs.getString("trang_thai_muon")
            ));
        }
        conn.close();
        return list;
    }
}