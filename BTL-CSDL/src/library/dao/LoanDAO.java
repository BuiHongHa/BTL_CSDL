
package library.dao;

import library.model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {

    private static final String SQL_BOOKS_BY_READER = 
        "SELECT s.ma_sach, s.ten_sach, s.tac_gia, s.the_loai, s.nam_xuat_ban " +
        "FROM Sach s " +
        "JOIN ChiTietMuon ctm ON s.ma_sach = ctm.ma_sach " +
        "JOIN PhieuMuon pm ON ctm.ma_phieu_muon = pm.ma_phieu_muon " +
        "WHERE pm.ma_nguoi_doc = ?";

    public List<Book> getBooksByReader(String maNguoiDoc) throws SQLException {
        List<Book> result = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_BOOKS_BY_READER)) {

            ps.setString(1, maNguoiDoc);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book b = new Book(
                            rs.getString("ma_sach"),
                            rs.getString("ten_sach"),
                            rs.getString("tac_gia"),
                            rs.getString("the_loai"),
                            rs.getInt("nam_xuat_ban")
                    );
                    result.add(b);
                }
            }
        }
        return result;
    }
}
