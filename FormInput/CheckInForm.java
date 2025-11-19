package SQL;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CheckinForm extends JFrame {
    private JTextField txtMaPhieuGui, txtMaNguoiDoc, txtLyDo; // Bỏ txtMaNhanVien
    private JButton btnGui, btnLamMoi, btnXem;

    public CheckinForm() {
        setTitle("🪪 Gửi yêu cầu Check-in");
        setSize(450, 350); // Giảm chiều cao form
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("GỬI YÊU CẦU CHECK-IN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 120, 80));
        add(lblTitle, BorderLayout.NORTH);

        // Panel nhập liệu (Chỉ còn 3 hàng)
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

        // Panel nút
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

        // Sự kiện nút
        btnGui.addActionListener(e -> guiYeuCau());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnXem.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng xem danh sách được xử lý ở form khác."));

        // Tự sinh mã phiếu
        generateCheckinID();
    }
    
    // ====== KIỂM TRA KHÓA NGOẠI ======
    private boolean isNguoiDocExists(String maND) throws SQLException {
        String sql = "SELECT ma_nguoi_doc FROM nguoidoc WHERE ma_nguoi_doc = ?";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maND);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }


    private void guiYeuCau() {
        String maND = txtMaNguoiDoc.getText();
        
        // KIỂM TRA KHÓA NGOẠI TRƯỚC KHI THÊM
        try {
            if (!isNguoiDocExists(maND)) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi: Mã người đọc (" + maND + ") không tồn tại trong hệ thống.", "Lỗi Khóa Ngoại", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(this, "❌ Lỗi kiểm tra khóa ngoại: " + e.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
             return;
        }
        
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "INSERT INTO checkin (ma_phieu_gui, ma_nguoi_doc, ngay_yeu_cau, ly_do) VALUES (?, ?, NOW(), ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtMaPhieuGui.getText());
            ps.setString(2, maND);
            ps.setString(3, txtLyDo.getText()); 
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Gửi yêu cầu thành công!");

            lamMoi(); 

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi khi gửi: " + ex.getMessage());
        }
    }

    private void lamMoi() {
        txtMaNguoiDoc.setText("");
        txtLyDo.setText("");
        generateCheckinID();
    }

    // ====== TỰ ĐỘNG SINH ID (GAP FILLING) ======
    private void generateCheckinID() {
        int nextID = 1;
        try (Connection conn = MySQLConnection.getConnection()) {
            
            // 1. KIỂM TRA ĐỘC LẬP: CI001 có bị thiếu không?
            String check1Sql = "SELECT ma_phieu_gui FROM checkin WHERE ma_phieu_gui = 'CI001'";
            try (PreparedStatement ps = conn.prepareStatement(check1Sql); ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    txtMaPhieuGui.setText("CI001");
                    return; 
                }
            }
            
            // 2. TÌM GAP KHÁC HOẶC MAX + 1
            String gapSql = "SELECT MIN(t1.id) + 1 AS next_id FROM (SELECT CAST(SUBSTRING(ma_phieu_gui, 3) AS UNSIGNED) AS id FROM checkin) t1 " +
                                     "LEFT JOIN (SELECT CAST(SUBSTRING(ma_phieu_gui, 3) AS UNSIGNED) AS id FROM checkin) t2 ON t1.id + 1 = t2.id " +
                                     "WHERE t2.id IS NULL AND t1.id >= 1"; 
            
            String maxSql = "SELECT MAX(CAST(SUBSTRING(ma_phieu_gui, 3) AS UNSIGNED)) AS max_id FROM checkin";

            // Tìm GAP > 1
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(gapSql)) {
                if (rs.next()) {
                    int gapId = rs.getInt("next_id");
                    if (gapId > 0) {
                         nextID = gapId;
                    }
                }
            }
            
            // Nếu không tìm thấy gap, lấy MAX ID + 1
            if (nextID == 1) {
                 try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(maxSql)) {
                    if (rs.next()) {
                        int maxId = rs.getInt("max_id");
                        if (maxId > 0) {
                             nextID = maxId + 1;
                        }
                    }
                }
            }
            
            txtMaPhieuGui.setText("CI" + String.format("%03d", nextID));

        } catch (SQLException e) {
            txtMaPhieuGui.setText("CI001");
            System.err.println("Lỗi tự động sinh ID: " + e.getMessage());
        }
    }

//    public static void main(String[] args) {
//        new CheckinForm().setVisible(true);
//    }
}
