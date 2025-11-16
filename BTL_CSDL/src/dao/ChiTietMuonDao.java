package dao;

import model.ChiTietMuon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietMuonDao {

    /**
     * Lấy chi tiết các sách thuộc 1 phiếu mượn (cho hóa đơn)
     * (Dùng câu SQL bạn đã cung cấp)
     */
    public List<ChiTietMuon> getChiTietCuaPhieu(String maPhieuMuon) throws SQLException {
        List<ChiTietMuon> list = new ArrayList<>();
        Connection conn = Database.getConnection();

        String sql = """
            SELECT
                s.ma_sach,
                s.ten_sach,
                ctm.tinh_trang_sach
            FROM chitietmuon ctm
            JOIN sach s ON ctm.ma_sach = s.ma_sach
            WHERE ctm.ma_phieu_muon = ?
            """;

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, maPhieuMuon);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            ChiTietMuon cti = new ChiTietMuon(
                    rs.getString("ma_sach"),
                    rs.getString("ten_sach"),
                    rs.getString("tinh_trang_sach")
            );
            list.add(cti);
        }
        conn.close();
        return list;
    }
}