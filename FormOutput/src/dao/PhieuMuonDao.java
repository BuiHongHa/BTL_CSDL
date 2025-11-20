package dao;

import model.PhieuMuon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhieuMuonDao {

    public List<PhieuMuon> getTraCuuPhieuMuon(String keyword, String trangThai,
                                              String fromMuon, String toMuon,
                                              String fromTra, String toTra) throws SQLException {
        List<PhieuMuon> list = new ArrayList<>();
        Connection conn = Database.getConnection();

        StringBuilder sql = new StringBuilder(
                "SELECT pm.ma_phieu_muon, pm.ma_nguoi_doc, nd.ho_ten AS tenNguoiDoc, nd.so_dien_thoai AS sdtNguoiDoc, " +
                        "pm.ma_nhan_vien, nv.ho_ten AS tenNhanVien, pm.ngay_muon, pm.ngay_tra " +
                        "FROM phieumuon pm " +
                        "JOIN nguoidoc nd ON pm.ma_nguoi_doc = nd.ma_nguoi_doc " +
                        "JOIN nhanvien nv ON pm.ma_nhan_vien = nv.ma_nhan_vien " +
                        "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        // 1. Tìm kiếm từ khóa
        if (!keyword.isEmpty()) {
            sql.append("AND (pm.ma_phieu_muon LIKE ? OR nd.ho_ten LIKE ? OR nd.so_dien_thoai LIKE ?) ");
            String kw = "%" + keyword + "%";
            params.add(kw); params.add(kw); params.add(kw);
        }

        // 2. Lọc trạng thái
        if (!trangThai.equals("Tất cả")) {
            if (trangThai.equals("Đang mượn")) {
                sql.append("AND pm.ngay_tra IS NULL ");
            } else {
                sql.append("AND pm.ngay_tra IS NOT NULL ");
            }
        }

        // 3. Lọc ngày mượn
        if (!fromMuon.isEmpty()) {
            sql.append("AND pm.ngay_muon >= ? ");
            params.add(fromMuon);
        }
        if (!toMuon.isEmpty()) {
            sql.append("AND pm.ngay_muon <= ? ");
            params.add(toMuon);
        }

        // 4. Lọc ngày trả
        if (!fromTra.isEmpty()) {
            sql.append("AND pm.ngay_tra >= ? ");
            params.add(fromTra);
        }
        if (!toTra.isEmpty()) {
            sql.append("AND pm.ngay_tra <= ? ");
            params.add(toTra);
        }

        sql.append("ORDER BY pm.ngay_muon DESC");

        PreparedStatement stmt = conn.prepareStatement(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            list.add(new PhieuMuon(
                    rs.getString("ma_phieu_muon"),
                    rs.getString("ma_nguoi_doc"),
                    rs.getString("tenNguoiDoc"),
                    rs.getString("sdtNguoiDoc"),
                    rs.getString("ma_nhan_vien"),
                    rs.getString("tenNhanVien"),
                    rs.getDate("ngay_muon"),
                    rs.getDate("ngay_tra")
            ));
        }
        conn.close();
        return list;
    }
}