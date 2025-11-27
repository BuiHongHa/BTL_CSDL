package view.input;

import util.Database;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CheckinForm extends JFrame {
    private JTextField txtMaPhieuGui, txtMaNguoiDoc, txtLyDo;
    private JButton btnGui, btnLamMoi, btnXem;

    public CheckinForm() {
        setTitle("🪪 Gửi yêu cầu Check-in");
        setSize(450, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("GỬI YÊU CẦU CHECK-IN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 120, 80));
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin yêu cầu"));

        formPanel.add(new JLabel("Mã phiếu gửi:"));
        txtMaPhieuGui = new JTextField();
        txtMaPhieuGui.setEditable(false);
        formPanel.add(txtMaPhieuGui);

        formPanel.add(new JLabel("Mã người đọc:"));
        txtMaNguoiDoc = new JTextField();
        formPanel.add(txtMaNguoiDoc);

        formPanel.add(new JLabel("Lý do vào thư viện:"));
        txtLyDo = new JTextField();
        formPanel.add(txtLyDo);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnGui = new JButton("📩 Gửi yêu cầu");
        btnLamMoi = new JButton("🔄 Làm mới");
        btnXem = new JButton("📜 Xem danh sách");

        for (JButton btn : new JButton[]{btnGui, btnLamMoi, btnXem}) {
            btn.setFocusPainted(false);
            btn.setBackground(new Color(230, 250, 240));
            btn.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 80)));
        }

        btnPanel.add(btnGui);
        btnPanel.add(btnLamMoi);
        btnPanel.add(btnXem);

        add(btnPanel, BorderLayout.SOUTH);

        btnGui.addActionListener(e -> guiYeuCau());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnXem.addActionListener(e -> JOptionPane.showMessageDialog(this, "Danh sách được hiển thị ở form quản lý của nhân viên."));

        generateCheckinID();
    }

    // ====== KIỂM TRA KHÓA NGOẠI ======
    private boolean isNguoiDocExists(String maND) throws SQLException {
        String sql = "SELECT ma_nguoi_doc FROM nguoidoc WHERE ma_nguoi_doc = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maND);
            return ps.executeQuery().next();
        }
    }

    private void guiYeuCau() {
        String maND = txtMaNguoiDoc.getText().trim();
        String lyDo = txtLyDo.getText().trim();

        if (maND.isEmpty() || lyDo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!");
            return;
        }

        try {
            if (!isNguoiDocExists(maND)) {
                JOptionPane.showMessageDialog(this, "❌ Mã người đọc không tồn tại!", "Lỗi khóa ngoại", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kiểm tra mã người đọc!");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO checkin (ma_phieu_gui, ma_nguoi_doc, ngay_yeu_cau, ly_do) VALUES (?, ?, NOW(), ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, txtMaPhieuGui.getText());
            ps.setString(2, maND);
            ps.setString(3, lyDo);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "📩 Gửi yêu cầu thành công!");
            lamMoi();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi gửi yêu cầu: " + ex.getMessage());
        }
    }

    private void lamMoi() {
        txtMaNguoiDoc.setText("");
        txtLyDo.setText("");
        generateCheckinID();
    }

    // ======= AUTO ID CHECKIN =======
    private void generateCheckinID() {
        int nextID = 1;
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT MAX(CAST(SUBSTRING(ma_phieu_gui, 3) AS UNSIGNED)) FROM checkin";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            if (rs.next()) nextID = rs.getInt(1) + 1;
        } catch (SQLException ignored) {}

        txtMaPhieuGui.setText("CI" + String.format("%03d", nextID));
    }
}
