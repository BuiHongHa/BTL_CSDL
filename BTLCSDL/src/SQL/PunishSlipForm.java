package SQL;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class PunishSlipForm extends JFrame {
    private JTextField txtMaPhieuPhat, txtMaPhieuMuon, txtMaSach, txtMaNguoiDoc, txtLyDo, txtSoTien;

    public PunishSlipForm() {
        setTitle("Tạo Phiếu Phạt");
        setSize(520, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel header = new JLabel("TẠO PHIẾU PHẠT", SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(0,102,204));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(header, BorderLayout.NORTH);

        JPanel p = new JPanel(new GridLayout(7,1,6,6));
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        p.setBackground(new Color(240,244,247));

        p.add(new JLabel("Mã phiếu phạt:"));
        txtMaPhieuPhat = new JTextField(); p.add(txtMaPhieuPhat);

        p.add(new JLabel("Mã phiếu mượn:"));
        txtMaPhieuMuon = new JTextField(); p.add(txtMaPhieuMuon);

        p.add(new JLabel("Mã sách:"));
        txtMaSach = new JTextField(); p.add(txtMaSach);

        p.add(new JLabel("Mã người đọc:"));
        txtMaNguoiDoc = new JTextField(); p.add(txtMaNguoiDoc);

        p.add(new JLabel("Lý do phạt:"));
        txtLyDo = new JTextField(); p.add(txtLyDo);

        p.add(new JLabel("Số tiền phạt:"));
        txtSoTien = new JTextField(); p.add(txtSoTien);

        add(p, BorderLayout.CENTER);

        JButton btnSave = new JButton("Lưu phiếu phạt");
        btnSave.setBackground(new Color(0,102,204)); btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> savePunish());
        add(btnSave, BorderLayout.SOUTH);

        setVisible(true); // fix logic: JFrame phải hiển thị
    }

    private void savePunish() {
        String mpp = txtMaPhieuPhat.getText().trim();
        String mpm = txtMaPhieuMuon.getText().trim();
        String ms = txtMaSach.getText().trim();
        String md = txtMaNguoiDoc.getText().trim();
        String ly = txtLyDo.getText().trim();
        String tienStr = txtSoTien.getText().trim();


        if (mpp.isEmpty() || mpm.isEmpty() || ms.isEmpty() || md.isEmpty() || ly.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        double tien = 0;
        try {
            tien = Double.parseDouble(tienStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tiền phạt phải là số.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO phieuphat (ma_phieu_phat, ma_phieu_muon, ma_sach, ma_nguoi_doc, ly_do_phat, so_tien_phat, ngay_lap) VALUES (?,?,?,?,?,?,?)")) {

            ps.setString(1, mpp);
            ps.setString(2, mpm);
            ps.setString(3, ms);
            ps.setString(4, md);
            ps.setString(5, ly);
            ps.setDouble(6, tien);
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Lưu phiếu phạt thành công.");
            this.dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi lưu phiếu phạt: " + ex.getMessage());
        }
    }
}
