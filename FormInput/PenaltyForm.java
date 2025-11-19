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
import java.sql.SQLIntegrityConstraintViolationException;

// Lớp MySQLConnection sẽ được import và sử dụng từ file riêng của bạn.

public class PenaltyForm extends JFrame {
    private JTextField txtMaPhieuPhat, txtMaPhieuMuon, txtMaNguoiDoc, txtSoTienPhat, txtNgayLap;
    private JTextArea txtLyDo;
    private JComboBox<LoanDetail> cmbMaSach; 
    private JButton btnThem, btnSua, btnXoa, btnXem, btnLamMoi;
    
    private java.awt.event.FocusAdapter focusListener; // Khai báo listener

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
            return maSach + " - " + tenSach + " (TT cũ: " + tinhTrangSach + ")";
        }
    }
    
    // Class đại diện cho dữ liệu Phiếu Phạt đầy đủ
    static class PenaltyData {
        String maPhieuMuon;
        String maSach;
        String maNguoiDoc;
        String lyDoPhat;
        double soTienPhat;
        LocalDateTime ngayLap;

        public PenaltyData(String maPhieuMuon, String maSach, String maNguoiDoc, String lyDoPhat, double soTienPhat, LocalDateTime ngayLap) {
            this.maPhieuMuon = maPhieuMuon;
            this.maSach = maSach;
            this.maNguoiDoc = maNguoiDoc;
            this.lyDoPhat = lyDoPhat;
            this.soTienPhat = soTienPhat;
            this.ngayLap = ngayLap;
        }
    }


    public PenaltyForm() {
        setTitle("📘 Quản lý Phiếu Phạt");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        txtMaNguoiDoc.setEditable(false); // ID người đọc điền tự động
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
                // Chế độ Thêm: Tra cứu chi tiết phiếu mượn
                if (!isEditDeleteMode()) { 
                    String maPM = txtMaPhieuMuon.getText().trim();
                    if (!maPM.isEmpty()) {
                        lookupLoanDetails(maPM); 
                    } else {
                        txtMaNguoiDoc.setText("");
                        cmbMaSach.removeAllItems();
                    }
                } 
                // Chế độ Sửa/Xóa: Bỏ qua vì cần load từ MaPhieuPhat
            }
        });
        
        // Sự kiện cho nút
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

        setAddMode(); 
    }
    
    // Kiểm tra xem đang ở chế độ Sửa/Xóa hay không
    private boolean isEditDeleteMode() {
        return btnThem.isEnabled() == false;
    }

    // Gán FocusListener để tải dữ liệu khi mất focus (nhập xong ID)
    private void attachFocusListenerForLoad() {
        removeFocusListenerForLoad(); 
        
        focusListener = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                if (isEditDeleteMode() && evt.getSource() == txtMaPhieuPhat) {
                    loadPenaltyData(txtMaPhieuPhat.getText().trim());
                }
            }
        };
        txtMaPhieuPhat.addFocusListener(focusListener);
    }
    
    // Xóa FocusListener
    private void removeFocusListenerForLoad() {
        if (focusListener != null) {
            txtMaPhieuPhat.removeFocusListener(focusListener);
            focusListener = null;
        }
    }


    // ====== CHUYỂN CHẾ ĐỘ THÊM/LÀM MỚI ======
    private void setAddMode() {
        txtMaPhieuPhat.setEditable(false); 
        
        txtMaPhieuMuon.setEditable(true);
        txtMaNguoiDoc.setEditable(false); 
        cmbMaSach.setEnabled(true);
        txtLyDo.setEditable(true);
        txtSoTienPhat.setEditable(true);
        
        clearFieldsContent();
        generatePenaltyID(); 
        
        txtNgayLap.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        btnThem.setEnabled(true);
        btnSua.setText("✏️ Sửa");
        btnXoa.setText("🗑️ Xóa");
        
        removeFocusListenerForLoad(); 
    }

    // ====== CHUYỂN CHẾ ĐỘ SỬA/XÓA ======
    private void setEditDeleteMode() {
        if (!isEditDeleteMode()) {
            txtMaPhieuPhat.setEditable(true); 
            
            txtMaPhieuMuon.setEditable(false);
            txtMaNguoiDoc.setEditable(false);
            cmbMaSach.setEnabled(false);
            txtLyDo.setEditable(false);
            txtSoTienPhat.setEditable(false);
            txtNgayLap.setText("Không đổi khi Sửa/Xóa");
            
            clearFieldsContent();
            
            btnThem.setEnabled(false);
            btnSua.setText("✅ Cập nhật");
            btnXoa.setText("❌ Xác nhận Xóa");
            JOptionPane.showMessageDialog(this, "Đã chuyển sang chế độ SỬA/XÓA. Vui lòng nhập Mã phiếu phạt cần thao tác và nhấn Tab/Enter.");
            
            attachFocusListenerForLoad(); // Gán listener để tải dữ liệu
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
    
    // ====== TẢI DỮ LIỆU PHIẾU PHẠT ĐỂ SỬA/XÓA ======
    private void loadPenaltyData(String maPP) {
        if (maPP.isEmpty()) return;

        PenaltyData data = getPenaltyDataFromDB(maPP);

        if (data != null) {
            txtMaPhieuMuon.setText(data.maPhieuMuon);
            txtMaNguoiDoc.setText(data.maNguoiDoc);
            txtLyDo.setText(data.lyDoPhat);
            txtSoTienPhat.setText(String.valueOf(data.soTienPhat));
            txtNgayLap.setText(data.ngayLap.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            // Tải chi tiết sách cho ComboBox (chỉ cần Mã sách)
            loadBookForComboBox(data.maPhieuMuon, data.maSach); 
            
            // Mở khóa để chỉnh sửa
            txtMaPhieuMuon.setEditable(true);
            cmbMaSach.setEnabled(true);
            txtLyDo.setEditable(true);
            txtSoTienPhat.setEditable(true);
            
            JOptionPane.showMessageDialog(this, "✅ Đã tải thông tin phiếu phạt " + maPP + ". Bạn có thể chỉnh sửa.");

        } else {
            clearFieldsContent();
            txtMaPhieuPhat.setText(maPP);
            txtNgayLap.setText("Không đổi khi Sửa/Xóa");
            JOptionPane.showMessageDialog(this, "❌ Không tìm thấy phiếu phạt với Mã: " + maPP, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Hàm mới để tải Mã sách hiện tại và các sách liên quan (cho ComboBox)
    private void loadBookForComboBox(String maPM, String maSachHienTai) {
        cmbMaSach.removeAllItems();
        
        // Dùng hàm tra cứu cũ (vì nó trả về LoanDetail, tiện cho ComboBox)
        List<LoanDetail> relatedBooks = getLoanDetailsFromDB(maPM);

        if (relatedBooks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cảnh báo: Không tìm thấy sách nào cho Phiếu mượn này.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedIndex = -1;
        for (int i = 0; i < relatedBooks.size(); i++) {
            cmbMaSach.addItem(relatedBooks.get(i));
            if (relatedBooks.get(i).maSach.equals(maSachHienTai)) {
                selectedIndex = i;
            }
        }
        
        if (selectedIndex != -1) {
            cmbMaSach.setSelectedIndex(selectedIndex);
        }
    }


    // ====== TRUY VẤN DB THẬT (Load Penalty Data) ======
    private PenaltyData getPenaltyDataFromDB(String maPP) {
        String sql = "SELECT ma_phieu_muon, ma_sach, ma_nguoi_doc, ly_do_phat, so_tien_phat, ngay_lap FROM phieuphat WHERE ma_phieu_phat = ?";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPP);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("ngay_lap");
                    LocalDateTime ngayLap = ts != null ? ts.toLocalDateTime() : null;
                    
                    return new PenaltyData(
                        rs.getString("ma_phieu_muon"),
                        rs.getString("ma_sach"),
                        rs.getString("ma_nguoi_doc"),
                        rs.getString("ly_do_phat"),
                        rs.getDouble("so_tien_phat"),
                        ngayLap
                    );
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi truy vấn CSDL (Tải Phiếu Phạt): " + e.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    // ====== TRUY VẤN DB THẬT (Load Loan Details) ======
    private List<LoanDetail> getLoanDetailsFromDB(String maPhieuMuon) {
        List<LoanDetail> results = new ArrayList<>();
        // Truy vấn này cần lấy cả tình trạng sách hiện tại (để hiển thị)
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
                    // Dữ liệu ctm.tinh_trang_sach sẽ là "Đang mượn" hoặc Lý do phạt cũ
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
    //               LOGIC TRA CỨU (LOOKUP) - Ở CHẾ ĐỘ THÊM
    // ===============================================

    /**
     * Tra cứu thông tin Phiếu Mượn và Chi Tiết Mượn từ Database THẬT
     */
    private void lookupLoanDetails(String maPhieuMuon) {
        txtMaNguoiDoc.setText("");
        txtLyDo.setText("");
        cmbMaSach.removeAllItems();

        List<LoanDetail> results = getLoanDetailsFromDB(maPhieuMuon);

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
            message = String.format("Đã tải thông tin cho Mã người đọc '%s' và Mã sách '%s'. Tình trạng hiện tại: %s", maNguoiDoc, results.get(0).maSach, results.get(0).tinhTrangSach);
        } else {
            message = String.format("Đã tải thông tin cho Mã người đọc '%s' và %d cuốn sách.\nVui lòng CHỌN SÁCH cần phạt.", maNguoiDoc, results.size());
        }


        JOptionPane.showMessageDialog(this, message, "Tra Cứu Thành Công", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Cập nhật tinh_trang_sach trong bảng chitietmuon bằng Lý do phạt.
     */
    private void updateChiTietMuonStatus(Connection conn, String maSach, String maPM, String lyDoPhat) {
        String sql = "UPDATE chitietmuon SET tinh_trang_sach = ? WHERE ma_phieu_muon = ? AND ma_sach = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lyDoPhat);
            ps.setString(2, maPM);
            ps.setString(3, maSach);
            ps.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Cảnh báo: Lỗi cập nhật tình trạng sách trong Chi Tiết Mượn.\n" + e.getMessage(), "Lỗi Nghiệp Vụ", JOptionPane.WARNING_MESSAGE);
        }
    }


    // ===============================================
    //               LOGIC CRUD (Dùng DB thật)
    // ===============================================
    
    private void themPhieuPhat() {
        if (!btnThem.isEnabled()) return;
        
        LoanDetail selectedItem = (LoanDetail) cmbMaSach.getSelectedItem();
        String maPM = txtMaPhieuMuon.getText();
        String lyDo = txtLyDo.getText();

        if (txtMaPhieuPhat.getText().isEmpty() || maPM.isEmpty() || selectedItem == null || 
            txtMaNguoiDoc.getText().isEmpty() || lyDo.isEmpty() || txtSoTienPhat.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin và chọn Mã sách.", "Lỗi Thiếu Thông Tin", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "INSERT INTO phieuphat (ma_phieu_phat, ma_phieu_muon, ma_sach, ma_nguoi_doc, ly_do_phat, so_tien_phat, ngay_lap) VALUES (?, ?, ?, ?, ?, ?, NOW())";
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setString(1, txtMaPhieuPhat.getText());
            ps.setString(2, maPM);
            ps.setString(3, selectedItem.maSach); 
            ps.setString(4, txtMaNguoiDoc.getText());
            ps.setString(5, lyDo);
            ps.setDouble(6, Double.parseDouble(txtSoTienPhat.getText()));
            ps.executeUpdate();
            
            // THAO TÁC MỚI: CẬP NHẬT TÌNH TRẠNG SÁCH TRONG CTM
            updateChiTietMuonStatus(conn, selectedItem.maSach, maPM, lyDo);

            JOptionPane.showMessageDialog(this, "✅ Thêm phiếu phạt thành công và cập nhật tình trạng sách!");
            setAddMode();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi khi thêm: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi: Số tiền phạt phải là số hợp lệ.", "Lỗi Dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaPhieuPhat() {
        if (!isEditDeleteMode() || txtMaPhieuPhat.getText().isEmpty() || !txtMaPhieuMuon.isEditable()) {
             JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã phiếu phạt và tải dữ liệu trước khi Cập nhật.");
             return;
        }
        
        LoanDetail selectedItem = (LoanDetail) cmbMaSach.getSelectedItem();
        String maPM = txtMaPhieuMuon.getText();
        String lyDo = txtLyDo.getText();

        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Mã sách cần cập nhật.", "Lỗi Thiếu Thông Tin", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "UPDATE phieuphat SET ma_phieu_muon=?, ma_sach=?, ma_nguoi_doc=?, ly_do_phat=?, so_tien_phat=? WHERE ma_phieu_phat=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, maPM);
            ps.setString(2, selectedItem.maSach); 
            ps.setString(3, txtMaNguoiDoc.getText());
            ps.setString(4, lyDo);
            ps.setDouble(5, Double.parseDouble(txtSoTienPhat.getText()));
            ps.setString(6, txtMaPhieuPhat.getText());

            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                 // THAO TÁC MỚI: CẬP NHẬT TÌNH TRẠNG SÁCH TRONG CTM
                 updateChiTietMuonStatus(conn, selectedItem.maSach, maPM, lyDo);
                 JOptionPane.showMessageDialog(this, "✔ Cập nhật phiếu phạt thành công và cập nhật tình trạng sách!");
            } else {
                 JOptionPane.showMessageDialog(this, "Không tìm thấy Mã phiếu phạt để cập nhật.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
            
            setAddMode();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi khi cập nhật: " + ex.getMessage());
        } catch (NumberFormatException ex) {
             JOptionPane.showMessageDialog(this, "❌ Lỗi: Số tiền phạt phải là số hợp lệ.", "Lỗi Dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaPhieuPhat() {
        if (!isEditDeleteMode() || txtMaPhieuPhat.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã phiếu phạt cần xóa.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa phiếu phạt ID: " + txtMaPhieuPhat.getText() + "?", "Xác nhận Xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = MySQLConnection.getConnection()) {
                // TẠM THỜI KHÔNG ĐẢO NGƯỢC TÌNH TRẠNG SÁCH (vì không có thông tin tình trạng cũ)
                // Chỉ thực hiện xóa phiếu phạt
                String sql = "DELETE FROM phieuphat WHERE ma_phieu_phat=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtMaPhieuPhat.getText());

                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "🗑️ Xóa thành công!");
                } else {
                     JOptionPane.showMessageDialog(this, "Không tìm thấy Mã phiếu phạt để xóa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                }
                setAddMode();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi khi xóa: " + ex.getMessage());
            }
        }
    }
    
    // Lấy ID nhỏ nhất bị thiếu (Gap) hoặc ID lớn nhất + 1.
    private void generatePenaltyID() {
        // Tái sử dụng Mã ID bị xóa (Gap filling)
        int nextID = 1;
        try (Connection conn = MySQLConnection.getConnection()) {
            
            String check1Sql = "SELECT ma_phieu_phat FROM phieuphat WHERE ma_phieu_phat = 'PP001'";
            try (PreparedStatement ps = conn.prepareStatement(check1Sql); ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    txtMaPhieuPhat.setText("PP001");
                    return; 
                }
            }
            
            String gapSql = "SELECT MIN(t1.id) + 1 AS next_id FROM (SELECT CAST(SUBSTRING(ma_phieu_phat, 3) AS UNSIGNED) AS id FROM phieuphat) t1 " +
                                     "LEFT JOIN (SELECT CAST(SUBSTRING(ma_phieu_phat, 3) AS UNSIGNED) AS id FROM phieuphat) t2 ON t1.id + 1 = t2.id " +
                                     "WHERE t2.id IS NULL AND t1.id >= 1"; 
            
            String maxSql = "SELECT MAX(CAST(SUBSTRING(ma_phieu_phat, 3) AS UNSIGNED)) AS max_id FROM phieuphat";

            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(gapSql)) {
                if (rs.next()) {
                    int gapId = rs.getInt("next_id");
                    if (gapId > 0) {
                         nextID = gapId;
                    }
                }
            }
            
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
            txtMaPhieuPhat.setText("PP001");
            System.err.println("Lỗi tự động sinh ID: " + e.getMessage());
        }
    }

//    public static void main(String[] args) {
//        // Đặt Look and Feel hệ thống cho giao diện đẹp hơn
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        SwingUtilities.invokeLater(() -> new PenaltyForm().setVisible(true));
//    }
}
