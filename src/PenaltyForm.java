package SQL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Lớp MySQLConnection sẽ được import và sử dụng từ file riêng của bạn.
// Đã xóa lớp MySQLConnection giả (mock class) ở đây.

public class PenaltyForm extends JFrame {
    private JTextField txtMaPhieuPhat, txtMaPhieuMuon, txtMaNguoiDoc, txtSoTienPhat, txtNgayLap;
    private JTextArea txtLyDo;
    private JComboBox<LoanDetail> cmbMaSach; 
    private JButton btnThem, btnSua, btnXoa, btnXem, btnLamMoi;

    // Class đại diện cho kết quả tra cứu (từ SQL)
    static class LoanDetail {
        String maNguoiDoc;
        String maSach;
        String tenSach;
        String tinhTrangSach;

        public LoanDetail(String maNguoiDoc, String maSach, String tenSach, String tinhTrangSach) {
            this.maNguoiDoc = maNguoiDoc;
            this.maSach = maSach;
            this.tenSach = tenSach;
            this.tinhTrangSach = tinhTrangSach;
        }
        
        @Override
        public String toString() {
            // Hiển thị cả mã và tên sách trong ComboBox
            return maSach + " - " + tenSach + " (" + tinhTrangSach + ")";
        }
    }

    public PenaltyForm() {
        setTitle("📘 Quản lý Phiếu Phạt");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("TẠO PHIẾU PHẠT", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 70, 160));
        add(lblTitle, BorderLayout.NORTH);

        // Panel nhập liệu
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin phiếu phạt"));

        formPanel.add(new JLabel("Mã phiếu phạt:"));
        txtMaPhieuPhat = new JTextField();
        formPanel.add(txtMaPhieuPhat);

        formPanel.add(new JLabel("Mã phiếu mượn:"));
        txtMaPhieuMuon = new JTextField();
        formPanel.add(txtMaPhieuMuon);

        formPanel.add(new JLabel("Mã sách:"));
        cmbMaSach = new JComboBox<>(); 
        formPanel.add(cmbMaSach);

        formPanel.add(new JLabel("Mã người đọc:"));
        txtMaNguoiDoc = new JTextField();
        formPanel.add(txtMaNguoiDoc);

        formPanel.add(new JLabel("Lý do phạt:"));
        txtLyDo = new JTextArea(3, 20);
        formPanel.add(new JScrollPane(txtLyDo));

        formPanel.add(new JLabel("Số tiền phạt:"));
        txtSoTienPhat = new JTextField();
        formPanel.add(txtSoTienPhat);
        
        // Thêm trường Ngày lập
        formPanel.add(new JLabel("Ngày lập:"));
        txtNgayLap = new JTextField();
        txtNgayLap.setEditable(false); 
        formPanel.add(txtNgayLap);

        add(formPanel, BorderLayout.CENTER);

        // Panel nút thao tác (Giữ nguyên style của bạn)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnThem = new JButton("➕ Thêm");
        btnSua = new JButton("✏️ Sửa");
        btnXoa = new JButton("🗑️ Xóa");
        btnXem = new JButton("📄 Xem danh sách");
        btnLamMoi = new JButton("🔄 Làm mới");

        Dimension btnSize = new Dimension(120, 30);
        for (JButton btn : new JButton[]{btnThem, btnSua, btnXoa, btnXem, btnLamMoi}) {
            btn.setFocusPainted(false);
            btn.setBackground(new Color(220, 235, 250));
            btn.setBorder(BorderFactory.createLineBorder(new Color(0, 70, 160)));
            btn.setPreferredSize(btnSize);
            buttonPanel.add(btn);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        // --- LOGIC TRA CỨU TỰ ĐỘNG (FocusListener) ---
        txtMaPhieuMuon.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String maPM = txtMaPhieuMuon.getText().trim();
                if (!maPM.isEmpty()) {
                    // Đã thay thế hàm mock bằng hàm tra cứu DB thật
                    lookupLoanDetails(maPM); 
                } else {
                    txtMaNguoiDoc.setText("");
                    cmbMaSach.removeAllItems();
                }
            }
        });
        
        // Sự kiện (Giữ nguyên logic chế độ của bạn)
        btnThem.addActionListener(e -> themPhieuPhat());
        btnLamMoi.addActionListener(e -> setAddMode());
        btnXem.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Chức năng xem danh sách sẽ được thực hiện ở form khác.")
        );
        
        btnSua.addActionListener(e -> {
            if (isEditDeleteMode()) {
                suaPhieuPhat();
            } else {
                setEditDeleteMode();
            }
        });
        
        btnXoa.addActionListener(e -> {
            if (isEditDeleteMode()) {
                xoaPhieuPhat();
            } else {
                setEditDeleteMode();
            }
        });

        setAddMode(); // Mặc định ở chế độ Thêm
    }
    
    // Kiểm tra xem đang ở chế độ Sửa/Xóa hay không
    private boolean isEditDeleteMode() {
        return btnThem.isEnabled() == false;
    }

    // ====== CHUYỂN CHẾ ĐỘ THÊM/LÀM MỚI ======
    private void setAddMode() {
        txtMaPhieuPhat.setEditable(false); 
        txtMaPhieuMuon.setEditable(true);
        txtMaNguoiDoc.setEditable(true);
        cmbMaSach.setEnabled(true);
        
        clearFieldsContent();
        generatePenaltyID(); // Tự động sinh ID mới (Gap filling dùng SQL)
        
        // Ngày lập là thời gian hiện tại
        txtNgayLap.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // Cập nhật trạng thái nút
        btnThem.setEnabled(true);
        btnSua.setText("✏️ Sửa");
        btnXoa.setText("🗑️ Xóa");
    }

    // ====== CHUYỂN CHẾ ĐỘ SỬA/XÓA ======
    private void setEditDeleteMode() {
        if (!isEditDeleteMode()) {
            txtMaPhieuPhat.setEditable(true); 
            txtMaPhieuMuon.setEditable(true); 
            txtMaNguoiDoc.setEditable(true);
            cmbMaSach.setEnabled(true);
            
            clearFieldsContent();
            txtNgayLap.setText("Không đổi khi Sửa/Xóa");
            
            // Cập nhật trạng thái nút
            btnThem.setEnabled(false);
            btnSua.setText("✅ Cập nhật");
            btnXoa.setText("❌ Xác nhận Xóa");
            JOptionPane.showMessageDialog(this, "Đã chuyển sang chế độ SỬA/XÓA. Vui lòng nhập Mã phiếu phạt cần thao tác.");
        }
    }
    
    // ====== LÀM MỚI (CHỈ XÓA NỘI DUNG) ======
    private void clearFieldsContent() {
        txtMaPhieuPhat.setText("");
        txtMaPhieuMuon.setText("");
        txtMaNguoiDoc.setText("");
        txtLyDo.setText("");
        txtSoTienPhat.setText("");
        txtNgayLap.setText("");
        cmbMaSach.removeAllItems(); 
    }

    // ===============================================
    //               LOGIC TRA CỨU (LOOKUP) - Dùng DB thật
    // ===============================================

    /**
     * Tra cứu thông tin Phiếu Mượn và Chi Tiết Mượn từ Database THẬT
     */
    private void lookupLoanDetails(String maPhieuMuon) {
        txtMaNguoiDoc.setText("");
        txtLyDo.setText("");
        cmbMaSach.removeAllItems();

        List<LoanDetail> results = getLoanDetailsFromDB(maPhieuMuon); // <-- Dùng hàm truy vấn DB thật

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy Phiếu mượn: " + maPhieuMuon + " hoặc phiếu không có chi tiết mượn.", "Lỗi Tra Cứu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maNguoiDoc = results.get(0).maNguoiDoc;
        txtMaNguoiDoc.setText(maNguoiDoc);
        
        for (LoanDetail detail : results) {
            cmbMaSach.addItem(detail);
        }
        
        String message;
        if (results.size() == 1) {
            LoanDetail singleBook = results.get(0);
            if (!singleBook.tinhTrangSach.equalsIgnoreCase("Tốt") && !singleBook.tinhTrangSach.equalsIgnoreCase("Bình thường")) {
                txtLyDo.setText(String.format("Sách '%s' bị phạt do trả sách trong tình trạng: %s", singleBook.tenSach, singleBook.tinhTrangSach));
            }
             message = String.format("Đã tải thông tin cho Mã người đọc '%s' và Mã sách '%s'.", maNguoiDoc, singleBook.maSach);
        } else {
            message = String.format("Đã tải thông tin cho Mã người đọc '%s' và %d cuốn sách.\nVui lòng CHỌN SÁCH cần phạt.", maNguoiDoc, results.size());
        }


        JOptionPane.showMessageDialog(this, message, "Tra Cứu Thành Công", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * THỰC HIỆN TRUY VẤN DB THẬT: Lấy chi tiết mượn, sách và người đọc theo Mã phiếu mượn.
     */
    private List<LoanDetail> getLoanDetailsFromDB(String maPhieuMuon) {
        List<LoanDetail> results = new ArrayList<>();
        // Câu lệnh SQL lấy chi tiết phiếu mượn (JOIN phieumuon, chitietmuon, sach)
        String sql = "SELECT pm.ma_nguoi_doc, ctm.ma_sach, s.ten_sach, ctm.tinh_trang_sach " +
                     "FROM phieumuon pm " +
                     "JOIN chitietmuon ctm ON pm.ma_phieu_muon = ctm.ma_phieu_muon " +
                     "JOIN sach s ON ctm.ma_sach = s.ma_sach " +
                     "WHERE pm.ma_phieu_muon = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maPhieuMuon);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new LoanDetail(
                        rs.getString("ma_nguoi_doc"),
                        rs.getString("ma_sach"),
                        rs.getString("ten_sach"),
                        rs.getString("tinh_trang_sach")
                    ));
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi truy vấn tra cứu: " + e.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
        }
        return results;
    }


    // ===============================================
    //               LOGIC CRUD VÀ TẠO ID MỚI
    // ===============================================
    
    private void themPhieuPhat() {
        if (!btnThem.isEnabled()) return;
        
        LoanDetail selectedItem = (LoanDetail) cmbMaSach.getSelectedItem();

        if (txtMaPhieuPhat.getText().isEmpty() || txtMaPhieuMuon.getText().isEmpty() || selectedItem == null || 
            txtMaNguoiDoc.getText().isEmpty() || txtLyDo.getText().isEmpty() || txtSoTienPhat.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin và chọn Mã sách.");
            return;
        }
        
        // Bắt đầu dùng kết nối DB thật
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "INSERT INTO phieuphat (ma_phieu_phat, ma_phieu_muon, ma_sach, ma_nguoi_doc, ly_do_phat, so_tien_phat, ngay_lap) VALUES (?, ?, ?, ?, ?, ?, NOW())";
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setString(1, txtMaPhieuPhat.getText());
            ps.setString(2, txtMaPhieuMuon.getText());
            ps.setString(3, selectedItem.maSach); 
            ps.setString(4, txtMaNguoiDoc.getText());
            ps.setString(5, txtLyDo.getText());
            ps.setDouble(6, Double.parseDouble(txtSoTienPhat.getText()));
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Thêm phiếu phạt thành công!");
            setAddMode();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi khi thêm: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi: Số tiền phạt phải là số hợp lệ.", "Lỗi Dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaPhieuPhat() {
        if (txtMaPhieuPhat.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã phiếu phạt cần cập nhật.");
            return;
        }
        LoanDetail selectedItem = (LoanDetail) cmbMaSach.getSelectedItem();
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Mã sách cần cập nhật.", "Lỗi Thiếu Thông Tin", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Bắt đầu dùng kết nối DB thật
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "UPDATE phieuphat SET ma_phieu_muon=?, ma_sach=?, ma_nguoi_doc=?, ly_do_phat=?, so_tien_phat=? WHERE ma_phieu_phat=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, txtMaPhieuMuon.getText());
            ps.setString(2, selectedItem.maSach); 
            ps.setString(3, txtMaNguoiDoc.getText());
            ps.setString(4, txtLyDo.getText());
            ps.setDouble(5, Double.parseDouble(txtSoTienPhat.getText()));
            ps.setString(6, txtMaPhieuPhat.getText());

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✔ Cập nhật phiếu phạt thành công!");
            setAddMode();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi khi cập nhật: " + ex.getMessage());
        } catch (NumberFormatException ex) {
             JOptionPane.showMessageDialog(this, "❌ Lỗi: Số tiền phạt phải là số hợp lệ.", "Lỗi Dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaPhieuPhat() {
        if (txtMaPhieuPhat.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã phiếu phạt cần xóa.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa phiếu phạt ID: " + txtMaPhieuPhat.getText() + "?", "Xác nhận Xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Bắt đầu dùng kết nối DB thật
            try (Connection conn = MySQLConnection.getConnection()) {
                String sql = "DELETE FROM phieuphat WHERE ma_phieu_phat=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtMaPhieuPhat.getText());

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "🗑️ Xóa thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy Mã phiếu phạt để xóa.", "Lỗi Xóa", JOptionPane.WARNING_MESSAGE);
                }
                setAddMode();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi khi xóa: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Lấy ID nhỏ nhất bị thiếu (Gap) hoặc ID lớn nhất + 1.
     */
    private void generatePenaltyID() {
        // Tái sử dụng Mã ID bị xóa (Gap filling)
        int nextID = 1;
        try (Connection conn = MySQLConnection.getConnection()) {
            
            // 1. KIỂM TRA ĐỘC LẬP: PP001 có bị thiếu không? (Thủ thuật cho gap đầu tiên)
            String check1Sql = "SELECT ma_phieu_phat FROM phieuphat WHERE ma_phieu_phat = 'PP001'";
            try (PreparedStatement ps = conn.prepareStatement(check1Sql); ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    // PP001 bị thiếu. Điền ngay PP001.
                    txtMaPhieuPhat.setText("PP001");
                    return; 
                }
            }
            
            // 2. TÌM GAP KHÁC HOẶC MAX + 1 (áp dụng nếu PP001 đã tồn tại)
            
            // Truy vấn tìm ID nhỏ nhất bị thiếu (Gap > 1)
            String gapSql = "SELECT MIN(t1.id) + 1 AS next_id FROM (SELECT CAST(SUBSTRING(ma_phieu_phat, 3) AS UNSIGNED) AS id FROM phieuphat) t1 " +
                                     "LEFT JOIN (SELECT CAST(SUBSTRING(ma_phieu_phat, 3) AS UNSIGNED) AS id FROM phieuphat) t2 ON t1.id + 1 = t2.id " +
                                     "WHERE t2.id IS NULL AND t1.id >= 1"; 
            
            // Truy vấn tìm MAX ID
            String maxSql = "SELECT MAX(CAST(SUBSTRING(ma_phieu_phat, 3) AS UNSIGNED)) AS max_id FROM phieuphat";

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
            
            txtMaPhieuPhat.setText("PP" + String.format("%03d", nextID));

        } catch (SQLException e) {
             // Trường hợp dự phòng nếu kết nối lỗi (kết nối thất bại ngay từ đầu)
             txtMaPhieuPhat.setText("PP001");
             System.err.println("Lỗi tự động sinh ID: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Đặt Look and Feel hệ thống cho giao diện đẹp hơn
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new PenaltyForm().setVisible(true));
    }
}