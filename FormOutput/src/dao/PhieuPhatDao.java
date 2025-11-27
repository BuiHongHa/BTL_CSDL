package dao;

import model.PhieuPhat;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.Database;

public class PhieuPhatDao {

    // ================== GET ALL ==================
    public List<PhieuPhat> getAll() throws SQLException {
        List<PhieuPhat> list = new ArrayList<>();
        Connection conn = Database.getConnection();

        // SỬA SQL:
        // 1. Join nhanvien qua phieumuon (pm.ma_nhan_vien) vì phieuphat không có ma_nhan_vien
        // 2. Cập nhật tên cột: ngay_lap, so_tien_phat, ly_do_phat
        String sql =
                "SELECT pp.ma_phieu_phat, " +
                        "nd.ho_ten AS tenNguoiDoc, nd.so_dien_thoai AS sdtNguoiDoc, " +
                        "nv.ho_ten AS tenNhanVien, " +
                        "pp.ngay_lap, pm.ngay_muon, pm.ngay_tra, " +
                        "pp.so_tien_phat, pp.ly_do_phat " +
                        "FROM phieuphat pp " +
                        "LEFT JOIN nguoidoc nd ON pp.ma_nguoi_doc = nd.ma_nguoi_doc " +
                        "LEFT JOIN phieumuon pm ON pp.ma_phieu_muon = pm.ma_phieu_muon " +
                        "LEFT JOIN nhanvien nv ON pm.ma_nhan_vien = nv.ma_nhan_vien " +
                        "ORDER BY pp.ngay_lap DESC";

        PreparedStatement st = conn.prepareStatement(sql);
        ResultSet rs = st.executeQuery();

        while (rs.next()) list.add(mapRow(rs));
        conn.close();
        return list;
    }

    // ================== GET BY ID ==================
    public PhieuPhat getById(String maPP) throws SQLException {
        Connection conn = Database.getConnection();

        String sql =
                "SELECT pp.ma_phieu_phat, " +
                        "nd.ho_ten AS tenNguoiDoc, nd.so_dien_thoai AS sdtNguoiDoc, " +
                        "nv.ho_ten AS tenNhanVien, " +
                        "pp.ngay_lap, pm.ngay_muon, pm.ngay_tra, " +
                        "pp.so_tien_phat, pp.ly_do_phat " +
                        "FROM phieuphat pp " +
                        "LEFT JOIN nguoidoc nd ON pp.ma_nguoi_doc = nd.ma_nguoi_doc " +
                        "LEFT JOIN phieumuon pm ON pp.ma_phieu_muon = pm.ma_phieu_muon " +
                        "LEFT JOIN nhanvien nv ON pm.ma_nhan_vien = nv.ma_nhan_vien " +
                        "WHERE pp.ma_phieu_phat = ?";

        PreparedStatement st = conn.prepareStatement(sql);
        st.setString(1, maPP);

        ResultSet rs = st.executeQuery();
        PhieuPhat pp = null;
        if (rs.next()) pp = mapRow(rs);

        conn.close();
        return pp;
    }

    // ================== SEARCH ==================
    public List<PhieuPhat> search(String keyword, String dateFrom, String dateTo) throws SQLException {
        List<PhieuPhat> list = new ArrayList<>();
        Connection conn = Database.getConnection();

        String sql =
                "SELECT pp.ma_phieu_phat, " +
                        "nd.ho_ten AS tenNguoiDoc, nd.so_dien_thoai AS sdtNguoiDoc, " +
                        "nv.ho_ten AS tenNhanVien, " +
                        "pp.ngay_lap, pm.ngay_muon, pm.ngay_tra, " +
                        "pp.so_tien_phat, pp.ly_do_phat " +
                        "FROM phieuphat pp " +
                        "LEFT JOIN nguoidoc nd ON pp.ma_nguoi_doc = nd.ma_nguoi_doc " +
                        "LEFT JOIN phieumuon pm ON pp.ma_phieu_muon = pm.ma_phieu_muon " +
                        "LEFT JOIN nhanvien nv ON pm.ma_nhan_vien = nv.ma_nhan_vien " +
                        "WHERE 1=1 ";

        List<Object> params = new ArrayList<>();

        if (!keyword.isEmpty()) {
            sql += " AND (pp.ma_phieu_phat LIKE ? OR nd.ho_ten LIKE ? OR nd.so_dien_thoai LIKE ? OR pp.ly_do_phat LIKE ?)";
            String kw = "%" + keyword + "%";
            params.add(kw); params.add(kw); params.add(kw); params.add(kw);
        }

        if (!dateFrom.isEmpty()) {
            // Sửa tên cột trong điều kiện
            sql += " AND pp.ngay_lap >= ?";
            params.add(dateFrom);
        }
        if (!dateTo.isEmpty()) {
            sql += " AND pp.ngay_lap <= ?";
            params.add(dateTo);
        }

        sql += " ORDER BY pp.ngay_lap DESC";

        PreparedStatement st = conn.prepareStatement(sql);
        for (int i = 0; i < params.size(); i++) st.setObject(i + 1, params.get(i));

        ResultSet rs = st.executeQuery();
        while (rs.next()) list.add(mapRow(rs));

        conn.close();
        return list;
    }

    // ================== MAP ROW ==================
    private PhieuPhat mapRow(ResultSet rs) throws SQLException {
        // Mapping dữ liệu từ ResultSet vào Model mới
        return new PhieuPhat(
                rs.getString("ma_phieu_phat"),
                rs.getString("tenNguoiDoc"),
                rs.getString("sdtNguoiDoc"),
                rs.getString("tenNhanVien"),
                rs.getTimestamp("ngay_lap"), // Dùng getTimestamp cho DATETIME để lấy cả giờ phút
                rs.getDate("ngay_muon"),
                rs.getDate("ngay_tra"),
                rs.getDouble("so_tien_phat"), // Sửa getInt -> getDouble
                rs.getString("ly_do_phat")
        );
    }

    public List<PhieuPhat> getByNguoiDoc(String maND) throws SQLException {
        List<PhieuPhat> list = new ArrayList<>();
        Connection conn = Database.getConnection();

        String sql =
                "SELECT pp.ma_phieu_phat, " +
                        "nd.ho_ten AS tenNguoiDoc, nd.so_dien_thoai AS sdtNguoiDoc, " +
                        "nv.ho_ten AS tenNhanVien, " +
                        "pp.ngay_lap, pm.ngay_muon, pm.ngay_tra, " +
                        "pp.so_tien_phat, pp.ly_do_phat " +
                        "FROM phieuphat pp " +
                        "LEFT JOIN nguoidoc nd ON pp.ma_nguoi_doc = nd.ma_nguoi_doc " +
                        "LEFT JOIN phieumuon pm ON pp.ma_phieu_muon = pm.ma_phieu_muon " +
                        "LEFT JOIN nhanvien nv ON pm.ma_nhan_vien = nv.ma_nhan_vien " +
                        "WHERE pp.ma_nguoi_doc = ? " +
                        "ORDER BY pp.ngay_lap DESC";

        PreparedStatement st = conn.prepareStatement(sql);
        st.setString(1, maND);

        ResultSet rs = st.executeQuery();
        while (rs.next()) list.add(mapRow(rs));
        conn.close();
        return list;
    }



}