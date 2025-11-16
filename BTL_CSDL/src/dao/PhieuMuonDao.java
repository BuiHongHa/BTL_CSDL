package dao;

import model.PhieuMuon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhieuMuonDao {

    /**
     * Lấy danh sách phiếu mượn (JOIN với NguoiDoc và NhanVien)
     * Hỗ trợ tìm kiếm và lọc theo trạng thái
     */
    public List<PhieuMuon> getTraCuuPhieuMuon(String keyword, String trangThaiFilter) throws SQLException {
        List<PhieuMuon> list = new ArrayList<>();
        Connection conn = Database.getConnection(); // Trỏ đến CSDL 'thu_vien'

        // JOIN 3 bảng
        String sql = """
            SELECT
                pm.ma_phieu_muon,
                nd.ho_ten AS ten_nguoi_doc,
                nd.so_dien_thoai AS sdt_nguoi_doc,
                nv.ho_ten AS ten_nhan_vien,
                pm.ngay_muon,
                pm.ngay_tra
            FROM phieumuon pm
            JOIN nguoidoc nd ON pm.ma_nguoi_doc = nd.ma_nguoi_doc
            JOIN nhanvien nv ON pm.ma_nhan_vien = nv.ma_nhan_vien
            """;

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        // 1. Lọc theo Keyword (Tìm theo tên NĐ, SĐT, hoặc Mã PM)
        if (!keyword.isEmpty()) {
            conditions.add("(nd.ho_ten LIKE ? OR nd.so_dien_thoai LIKE ? OR pm.ma_phieu_muon LIKE ?)");
            String kw = "%" + keyword + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        // 2. Lọc theo Trạng Thái
        if (trangThaiFilter.equals("Đang mượn")) {
            conditions.add("pm.ngay_tra IS NULL");
        } else if (trangThaiFilter.equals("Đã trả")) {
            conditions.add("pm.ngay_tra IS NOT NULL");
        }
        // (Nếu là "Tất cả" thì không thêm điều kiện)

        if (!conditions.isEmpty()) {
            sql += " WHERE " + String.join(" AND ", conditions);
        }

        sql += " ORDER BY pm.ngay_muon DESC"; // Sắp xếp

        PreparedStatement stmt = conn.prepareStatement(sql);
        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            PhieuMuon pmi = new PhieuMuon(
                    rs.getString("ma_phieu_muon"),
                    rs.getString("ten_nguoi_doc"),
                    rs.getString("sdt_nguoi_doc"),
                    rs.getString("ten_nhan_vien"),
                    rs.getDate("ngay_muon"),
                    rs.getDate("ngay_tra")
            );
            list.add(pmi);
        }
        conn.close();
        return list;
    }
}