package SQL;

import java.awt.*;
import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import javax.swing.table.DefaultTableModel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class BorrowSlipForm extends JFrame {
    private JTextField txtMaPhieuMuon, txtMaNguoiDoc, txtMaNhanVien, txtNgayMuon, txtNgayTra;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnExport;
    private JLabel lblMaSach; 
    private JTextField txtMaSachMuon; 
    private JButton btnAddBook; 
    
    // Khai báo listener để kiểm soát việc tải dữ liệu
    private java.awt.event.FocusAdapter focusListener; 

    public BorrowSlipForm() {
        setTitle("📖 Tạo Phiếu Mượn Sách");
        setSize(550, 450); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ======= FORM NHẬP (GridLayout 6x2) =======
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        formPanel.add(new JLabel("Mã phiếu mượn:"));
        txtMaPhieuMuon = new JTextField();
        formPanel.add(txtMaPhieuMuon);

        formPanel.add(new JLabel("Mã người đọc:"));
        txtMaNguoiDoc = new JTextField();
        formPanel.add(txtMaNguoiDoc);

        formPanel.add(new JLabel("Mã nhân viên:"));
        txtMaNhanVien = new JTextField();
        formPanel.add(txtMaNhanVien);

        formPanel.add(new JLabel("Ngày mượn (YYYY-MM-DD):"));
        txtNgayMuon = new JTextField();
        txtNgayMuon.setEditable(false); // Ngày mượn không cho phép sửa
        formPanel.add(txtNgayMuon);

        formPanel.add(new JLabel("Ngày trả (YYYY-MM-DD):"));
        txtNgayTra = new JTextField();
        formPanel.add(txtNgayTra);
        
        // --- Ô NHẬP SÁCH VÀ NÚT THÊM SÁCH ---
        lblMaSach = new JLabel("Mã sách mượn:");
        txtMaSachMuon = new JTextField();
        btnAddBook = new JButton("📝 Thêm sách vào phiếu");
        btnAddBook.setBackground(new Color(173, 216, 230));

        JPanel bookPanel = new JPanel(new BorderLayout(5, 0));
        bookPanel.add(txtMaSachMuon, BorderLayout.CENTER);
        bookPanel.add(btnAddBook, BorderLayout.EAST);

        formPanel.add(lblMaSach);
        formPanel.add(bookPanel);

        // Khóa các trường sách và nút thêm sách mặc định
        setBookInputEnabled(false);

        // ======= PANEL NÚT =======
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        btnAdd = new JButton("➕ Thêm");
        btnUpdate = new JButton("✏️ Sửa");
        btnDelete = new JButton("🗑️ Xóa");
        btnClear = new JButton("🔄 Làm mới");
        btnExport = new JButton("📄 Xem Danh sách"); 

        Dimension btnSize = new Dimension(140, 30); 
        for (JButton b : new JButton[]{btnAdd, btnUpdate, btnDelete, btnClear, btnExport}) {
            b.setPreferredSize(btnSize);
            buttonPanel.add(b);
        }

        // ======= ADD TO FRAME =======
        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // ======= EVENT HANDLERS =======
        btnAdd.addActionListener(e -> addBorrowSlip());
        btnClear.addActionListener(e -> setAddMode()); 
        btnAddBook.addActionListener(e -> addBookToBorrowSlip()); 
        btnExport.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng xem danh sách sẽ được tải từ form quản lý danh sách phiếu mượn.")); 
        
        btnUpdate.addActionListener(e -> {
            if (isEditDeleteMode()) {
                updateBorrowSlip();
            } else {
                setEditDeleteMode();
            }
        });
        
        btnDelete.addActionListener(e -> {
            if (isEditDeleteMode()) {
                deleteBorrowSlip();
            } else {
                setEditDeleteMode();
            }
        });

        setAddMode(); 
    }
    
    // Bật/Tắt khả năng nhập sách
    private void setBookInputEnabled(boolean enabled) {
        lblMaSach.setEnabled(enabled);
        txtMaSachMuon.setEnabled(enabled); 
        txtMaSachMuon.setEditable(enabled); 
        btnAddBook.setEnabled(enabled);
        if (!enabled) {
            txtMaSachMuon.setText("");
        }
    }

    private boolean isEditDeleteMode() {
        return btnAdd.isEnabled() == false;
    }
    
    // Gán FocusListener để tải dữ liệu khi mất focus (nhập xong ID)
    private void attachFocusListenerForLoad() {
        removeFocusListenerForLoad(); 
        
        focusListener = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                if (isEditDeleteMode() && evt.getSource() == txtMaPhieuMuon) {
                    loadBorrowSlipData(txtMaPhieuMuon.getText().trim());
                }
            }
        };
        txtMaPhieuMuon.addFocusListener(focusListener);
    }
    
    // Xóa FocusListener
    private void removeFocusListenerForLoad() {
        if (focusListener != null) {
            txtMaPhieuMuon.removeFocusListener(focusListener);
            focusListener = null;
        }
    }


    private void setAddMode() {
        txtMaPhieuMuon.setEditable(false); 
        clearFieldsContent();
        generateBorrowSlipID(); 
        txtNgayMuon.setText(LocalDate.now().toString()); 
        
        btnAdd.setEnabled(true);
        btnUpdate.setText("✏️ Sửa");
        btnDelete.setText("🗑️ Xóa");

        setBookInputEnabled(false); 
        
        // Reset editability cho chế độ Thêm
        txtMaNguoiDoc.setEditable(true);
        txtMaNhanVien.setEditable(true);
        txtNgayTra.setEditable(true);
        
        removeFocusListenerForLoad(); // Đảm bảo listener không chạy ở chế độ Thêm
    }

    private void setEditDeleteMode() {
        if (!isEditDeleteMode()) {
            txtMaPhieuMuon.setEditable(true); 
            clearFieldsContent();
            txtNgayMuon.setText("Không đổi khi Sửa/Xóa");
            
            btnAdd.setEnabled(false);
            btnUpdate.setText("✅ Cập nhật");
            btnDelete.setText("❌ Xác nhận Xóa");
            JOptionPane.showMessageDialog(this, "Đã chuyển sang chế độ SỬA/XÓA. Vui lòng nhập Mã phiếu mượn cần thao tác và nhấn Tab/Enter.");
            
            setBookInputEnabled(false); 
            
            // Khóa các trường dữ liệu ban đầu, buộc người dùng phải tải dữ liệu
            txtMaNguoiDoc.setEditable(false);
            txtMaNhanVien.setEditable(false);
            txtNgayTra.setEditable(false);
            
            // Gán listener để tải dữ liệu
            attachFocusListenerForLoad();
        }
    }

    private void clearFieldsContent() {
        txtMaPhieuMuon.setText("");
        txtMaNguoiDoc.setText("");
        txtMaNhanVien.setText("");
        txtNgayMuon.setText("");
        txtNgayTra.setText("");
        txtMaSachMuon.setText("");
    }
    
    // ====== TẢI DỮ LIỆU PHIẾU MƯỢN ĐỂ SỬA ======
    private void loadBorrowSlipData(String maPM) {
        if (maPM.isEmpty()) return;

        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT ma_nguoi_doc, ma_nhan_vien, ngay_muon, ngay_tra FROM phieumuon WHERE ma_phieu_muon = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, maPM);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                txtMaNguoiDoc.setText(rs.getString("ma_nguoi_doc"));
                txtMaNhanVien.setText(rs.getString("ma_nhan_vien"));
                txtNgayMuon.setText(rs.getDate("ngay_muon").toString()); 
                Date ngayTra = rs.getDate("ngay_tra");
                txtNgayTra.setText(ngayTra != null ? ngayTra.toString() : "");
                
                // Bật chỉnh sửa cho các trường dữ liệu cần thiết
                txtMaNguoiDoc.setEditable(true);
                txtMaNhanVien.setEditable(true);
                txtNgayTra.setEditable(true);
                
                JOptionPane.showMessageDialog(this, "✅ Đã tải thông tin phiếu mượn " + maPM + ". Bạn có thể chỉnh sửa.");

            } else {
                // Xóa các trường dữ liệu nếu không tìm thấy
                txtMaNguoiDoc.setText("");
                txtMaNhanVien.setText("");
                txtNgayTra.setText("");
                
                txtMaPhieuMuon.setText(maPM); // Giữ lại ID đã nhập
                txtNgayMuon.setText("Không đổi khi Sửa/Xóa"); 
                JOptionPane.showMessageDialog(this, "❌ Không tìm thấy phiếu mượn với Mã: " + maPM, "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi truy vấn CSDL: " + ex.getMessage());
        }
    }


    // ====== KIỂM TRA KHÓA NGOẠI (USERS/EMPLOYEES/BOOKS) ======
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
    
    private boolean isNhanVienExists(String maNV) throws SQLException {
        String sql = "SELECT ma_nhan_vien FROM nhanvien WHERE ma_nhan_vien = ?";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    private boolean isSachExists(String maSach) throws SQLException {
        String sql = "SELECT ma_sach FROM sach WHERE ma_sach = ?";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSach);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }


    // ======= CRUD FUNCTIONS =======
    private void addBorrowSlip() {
        if (!btnAdd.isEnabled()) return;
        
        String maND = txtMaNguoiDoc.getText();
        String maNV = txtMaNhanVien.getText();
        
        // KIỂM TRA VALIDATION (Ngày mượn, Mã ND, Mã NV)
        if (maND.isEmpty() || maNV.isEmpty() || txtNgayMuon.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã người đọc và Mã nhân viên.", "Lỗi Thiếu Thông Tin", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            if (!isNguoiDocExists(maND)) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi: Mã người đọc (" + maND + ") không tồn tại trong hệ thống.", "Lỗi Khóa Ngoại", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!isNhanVienExists(maNV)) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi: Mã nhân viên (" + maNV + ") không tồn tại trong hệ thống.", "Lỗi Khóa Ngoại", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(this, "❌ Lỗi kiểm tra khóa ngoại: " + e.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
             return;
        }


        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "INSERT INTO phieumuon (ma_phieu_muon, ma_nguoi_doc, ma_nhan_vien, ngay_muon, ngay_tra) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, txtMaPhieuMuon.getText());
            stmt.setString(2, maND);
            stmt.setString(3, maNV);
            stmt.setDate(4, Date.valueOf(txtNgayMuon.getText()));
            
            String ngayTraText = txtNgayTra.getText().trim();
            if (ngayTraText.isEmpty()) {
                stmt.setNull(5, Types.DATE);
            } else {
                stmt.setDate(5, Date.valueOf(ngayTraText));
            }
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Thêm phiếu mượn thành công! Vui lòng thêm sách vào phiếu.", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            
            // --- KHÓA CHỨC NĂNG THÊM PHIẾU VÀ BẬT CHỨC NĂNG THÊM SÁCH ---
            btnAdd.setEnabled(false); 
            setBookInputEnabled(true);
            txtMaSachMuon.requestFocus();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi thêm phiếu mượn: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
             JOptionPane.showMessageDialog(this, "❌ Lỗi định dạng ngày trả: Vui lòng nhập ngày trả theo format YYYY-MM-DD.");
        }
    }
    
    private void addBookToBorrowSlip() {
        String maPM = txtMaPhieuMuon.getText();
        String maSach = txtMaSachMuon.getText().trim();
        
        if (maSach.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã sách cần mượn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (!isSachExists(maSach)) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi: Mã sách (" + maSach + ") không tồn tại trong thư viện.", "Lỗi Khóa Ngoại", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(this, "❌ Lỗi kiểm tra sách: " + e.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
             return;
        }
        
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "INSERT INTO chitietmuon (ma_phieu_muon, ma_sach, ngay_muon, tinh_trang_sach) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, maPM);
            stmt.setString(2, maSach);
            stmt.setDate(3, Date.valueOf(txtNgayMuon.getText()));
            stmt.setString(4, "Tốt"); // Giả định sách luôn tốt khi mượn
            stmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "✅ Đã thêm sách " + maSach + " vào phiếu mượn " + maPM + ". Tiếp tục thêm sách khác hoặc nhấn Làm mới.", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            txtMaSachMuon.setText("");
            txtMaSachMuon.requestFocus(); // Giữ focus để thêm sách khác

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi thêm chi tiết mượn: Mã sách này có thể đã được thêm vào phiếu mượn này rồi.\n" + ex.getMessage());
        }
    }

    private void updateBorrowSlip() {
        if (txtMaPhieuMuon.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã phiếu mượn cần cập nhật.");
            return;
        }
        
        String maND = txtMaNguoiDoc.getText();
        String maNV = txtMaNhanVien.getText();
        
        // Kiểm tra xem dữ liệu đã được tải lên chưa (qua việc kiểm tra các trường khác ID)
        if (maND.isEmpty() || maNV.isEmpty() || txtNgayMuon.getText().contains("Không đổi")) {
             JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã phiếu mượn và nhấn Tab/Enter để tải thông tin trước khi Cập nhật.");
             return;
        }
        
        try {
            if (!isNguoiDocExists(maND)) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi: Mã người đọc (" + maND + ") không tồn tại.", "Lỗi Khóa Ngoại", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!isNhanVienExists(maNV)) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi: Mã nhân viên (" + maNV + ") không tồn tại.", "Lỗi Khóa Ngoại", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(this, "❌ Lỗi kiểm tra khóa ngoại: " + e.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
             return;
        }
        
        try (Connection conn = MySQLConnection.getConnection()) {
            // Không cập nhật ngay_muon
            String sql = "UPDATE phieumuon SET ma_nguoi_doc=?, ma_nhan_vien=?, ngay_tra=? WHERE ma_phieu_muon=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, maND);
            stmt.setString(2, maNV);
            
            String ngayTraText = txtNgayTra.getText().trim();
            if (ngayTraText.isEmpty()) {
                stmt.setNull(3, Types.DATE);
            } else {
                stmt.setDate(3, Date.valueOf(ngayTraText));
            }
            
            stmt.setString(4, txtMaPhieuMuon.getText());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                 JOptionPane.showMessageDialog(this, "✅ Cập nhật thành công!");
            } else {
                 JOptionPane.showMessageDialog(this, "Không tìm thấy Mã phiếu mượn để cập nhật.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
            
            setAddMode();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi cập nhật: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
             JOptionPane.showMessageDialog(this, "❌ Lỗi định dạng ngày trả: Vui lòng nhập ngày trả theo format YYYY-MM-DD.");
        }
    }

    private void deleteBorrowSlip() {
        if (txtMaPhieuMuon.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã phiếu mượn cần xóa.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa phiếu mượn ID: " + txtMaPhieuMuon.getText() + "?", "Xác nhận Xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = MySQLConnection.getConnection()) {
                String sql = "DELETE FROM phieumuon WHERE ma_phieu_muon=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, txtMaPhieuMuon.getText());
                
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "🗑️ Xóa thành công! (Chi tiết mượn cũng đã được xóa theo CASCADE).");
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy Mã phiếu mượn để xóa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                }

                setAddMode();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi xóa: " + ex.getMessage());
            }
        }
    }

    // ====== TỰ ĐỘNG SINH ID (GAP FILLING) ======
    private void generateBorrowSlipID() {
        int nextID = 1;
        try (Connection conn = MySQLConnection.getConnection()) {
            
            String check1Sql = "SELECT ma_phieu_muon FROM phieumuon WHERE ma_phieu_muon = 'PM001'";
            try (PreparedStatement ps = conn.prepareStatement(check1Sql); ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    txtMaPhieuMuon.setText("PM001");
                    return; 
                }
            }
            
            String gapSql = "SELECT MIN(t1.id) + 1 AS next_id FROM (SELECT CAST(SUBSTRING(ma_phieu_muon, 3) AS UNSIGNED) AS id FROM phieumuon) t1 " +
                                     "LEFT JOIN (SELECT CAST(SUBSTRING(ma_phieu_muon, 3) AS UNSIGNED) AS id FROM phieumuon) t2 ON t1.id + 1 = t2.id " +
                                     "WHERE t2.id IS NULL AND t1.id >= 1"; 
            
            String maxSql = "SELECT MAX(CAST(SUBSTRING(ma_phieu_muon, 3) AS UNSIGNED)) AS max_id FROM phieumuon";

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
            
            txtMaPhieuMuon.setText("PM" + String.format("%03d", nextID));

        } catch (SQLException e) {
            txtMaPhieuMuon.setText("PM001");
            System.err.println("Lỗi tự động sinh ID: " + e.getMessage());
        }
    }
    
    private void showBorrowList() {
         JOptionPane.showMessageDialog(this, "Chức năng xem danh sách sẽ được tải từ form quản lý danh sách phiếu mượn.", "Xem Danh sách", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BorrowSlipForm().setVisible(true));
    }
}