package SQL;


import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;

public class BorrowSlipForm extends JFrame {
    private JTextField txtMaPhieu, txtMaNguoiDoc, txtMaNhanVien, txtNgayMuon, txtNgayTra, txtMaSach, txtTinhTrang;

    public BorrowSlipForm() {
        setTitle("Tạo Phiếu Mượn");
        setSize(520, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel header = new JLabel("TẠO PHIẾU MƯỢN", SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(0,102,204));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(header, BorderLayout.NORTH);

        JPanel p = new JPanel(new GridLayout(8,1,6,6));
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        p.setBackground(new Color(240,244,247));

        p.add(new JLabel("Mã phiếu mượn:"));
        txtMaPhieu = new JTextField(); p.add(txtMaPhieu);

        p.add(new JLabel("Mã người đọc:"));
        txtMaNguoiDoc = new JTextField(); p.add(txtMaNguoiDoc);

        p.add(new JLabel("Mã nhân viên (lập phiếu):"));
        txtMaNhanVien = new JTextField(); p.add(txtMaNhanVien);

        p.add(new JLabel("Ngày mượn (YYYY-MM-DD):"));
        txtNgayMuon = new JTextField(); p.add(txtNgayMuon);

        p.add(new JLabel("Ngày trả dự kiến (YYYY-MM-DD):"));
        txtNgayTra = new JTextField(); p.add(txtNgayTra);

        p.add(new JLabel("Mã sách:"));
        txtMaSach = new JTextField(); p.add(txtMaSach);

        p.add(new JLabel("Tình trạng sách khi mượn:"));
        txtTinhTrang = new JTextField("Tốt"); p.add(txtTinhTrang);

        add(p, BorderLayout.CENTER);

        JButton btnSave = new JButton("Lưu phiếu mượn");
        btnSave.setBackground(new Color(0,102,204)); btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> saveBorrow());
        add(btnSave, BorderLayout.SOUTH);
    }

    private void saveBorrow() {
        String mp = txtMaPhieu.getText().trim();
        String md = txtMaNguoiDoc.getText().trim();
        String mnv = txtMaNhanVien.getText().trim();
        String nm = txtNgayMuon.getText().trim();
        String nt = txtNgayTra.getText().trim();
        String ms = txtMaSach.getText().trim();
        String tt = txtTinhTrang.getText().trim();

        if (mp.isEmpty() || md.isEmpty() || mnv.isEmpty() || ms.isEmpty() || nm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ: mã phiếu, mã người đọc, mã nhân viên, mã sách và ngày mượn.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Insert to phieumuon
            String sql1 = "INSERT INTO phieumuon (ma_phieu_muon, ma_nguoi_doc, ma_nhan_vien, ngay_muon, ngay_tra) VALUES (?,?,?,?,?)";
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            ps1.setString(1, mp);
            ps1.setString(2, md);
            ps1.setString(3, mnv);
            ps1.setDate(4, Date.valueOf(nm));
            if (nt.isEmpty()) ps1.setNull(5, java.sql.Types.DATE); else ps1.setDate(5, Date.valueOf(nt));
            ps1.executeUpdate();

            // Insert to chitietmuon (single book)
            String sql2 = "INSERT INTO chitietmuon (ma_phieu_muon, ma_sach, ngay_muon, tinh_trang_sach) VALUES (?,?,?,?)";
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ps2.setString(1, mp);
            ps2.setString(2, ms);
            ps2.setDate(3, Date.valueOf(nm));
            ps2.setString(4, tt);
            ps2.executeUpdate();

            JOptionPane.showMessageDialog(this, "Lưu phiếu mượn thành công.");
            this.dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi lưu phiếu mượn: " + ex.getMessage());
        }
    }
}
