package SQL;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowSlipForm extends JFrame {
    private JTextField txtMaPhieuMuon, txtMaNguoiDoc, txtMaNhanVien, txtNgayMuon, txtNgayTra;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnExport;
    
    // Thay ComboBox bằng TextField hiển thị sách đã chọn và nút mở Dialog tìm kiếm
    private JTextField txtSachDaChon; 
    private String selectedMaSach = null; // Lưu mã sách đã chọn từ Dialog
    private JButton btnSelectBook; // Nút mở Dialog chọn sách
    private JButton btnAddBook; // Nút thêm sách vào phiếu
    
    private java.awt.event.FocusAdapter focusListener; 

    public BorrowSlipForm() {
        setTitle("📖 Tạo Phiếu Mượn Sách");
        setSize(600, 500); // Tăng kích thước một chút
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        txtNgayMuon.setEditable(false); 
        formPanel.add(txtNgayMuon);

        formPanel.add(new JLabel("Ngày trả (YYYY-MM-DD):"));
        txtNgayTra = new JTextField();
        formPanel.add(txtNgayTra);
        
        // --- HÀNG CUỐI: CHỌN SÁCH THÔNG MINH ---
        formPanel.add(new JLabel("Sách muốn mượn:"));
        
        JPanel bookSelectionPanel = new JPanel(new BorderLayout(5, 0));
        
        txtSachDaChon = new JTextField();
        txtSachDaChon.setEditable(false); // Không cho nhập tay, phải chọn
        txtSachDaChon.setBackground(Color.WHITE);
        
        btnSelectBook = new JButton("🔍 Tìm & Chọn");
        btnSelectBook.addActionListener(e -> openBookSelectionDialog());
        
        btnAddBook = new JButton("📝 Thêm vào phiếu");
        btnAddBook.setBackground(new Color(173, 216, 230));
        btnAddBook.addActionListener(e -> addBookToBorrowSlip());

        JPanel actionBookPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        actionBookPanel.add(btnSelectBook);
        actionBookPanel.add(btnAddBook);
        
        bookSelectionPanel.add(txtSachDaChon, BorderLayout.CENTER);
        bookSelectionPanel.add(actionBookPanel, BorderLayout.EAST);

        formPanel.add(bookSelectionPanel); 

        // Khóa các trường sách và nút thêm sách mặc định
        setBookInputEnabled(false);

        // ======= PANEL NÚT =======
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        btnAdd = new JButton("➕ Thêm Phiếu Mượn");
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
    
    // ====== HỘP THOẠI TÌM KIẾM VÀ CHỌN SÁCH ======
    private void openBookSelectionDialog() {
        JDialog dialog = new JDialog(this, "🔍 Tìm kiếm và Chọn Sách", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        // Panel Tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField txtSearch = new JTextField(30);
        JButton btnSearch = new JButton("Tìm kiếm");
        searchPanel.add(new JLabel("Nhập tên hoặc mã sách:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        
        // Bảng kết quả
        String[] columnNames = {"Mã sách", "Tên sách", "Tác giả", "Năm XB"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Nút Chọn
        JButton btnConfirm = new JButton("✅ Chọn sách này");
        btnConfirm.setEnabled(false);
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnConfirm);
        
        // Sự kiện Tìm kiếm
        ActionListener searchAction = e -> {
            String keyword = txtSearch.getText().trim();
            model.setRowCount(0); // Xóa dữ liệu cũ
            try (Connection conn = MySQLConnection.getConnection()) {
                String sql = "SELECT ma_sach, ten_sach, tac_gia, nam_xuat_ban FROM sach WHERE ten_sach LIKE ? OR ma_sach LIKE ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + keyword + "%");
                ps.setString(2, "%" + keyword + "%");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("ma_sach"),
                        rs.getString("ten_sach"),
                        rs.getString("tac_gia"),
                        rs.getInt("nam_xuat_ban")
                    });
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        };
        
        btnSearch.addActionListener(searchAction);
        txtSearch.addActionListener(searchAction); // Cho phép nhấn Enter để tìm
        
        // Sự kiện chọn dòng trong bảng
        table.getSelectionModel().addListSelectionListener(e -> {
            btnConfirm.setEnabled(table.getSelectedRow() != -1);
        });
        
        // Sự kiện Xác nhận chọn
        btnConfirm.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedMaSach = (String) model.getValueAt(row, 0);
                String tenSach = (String) model.getValueAt(row, 1);
                txtSachDaChon.setText(selectedMaSach + " - " + tenSach);
                dialog.dispose();
            }
        });
        
        // Tải dữ liệu ban đầu (tất cả sách)
        searchAction.actionPerformed(null);
        
        dialog.add(searchPanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }


    // Bật/Tắt khả năng nhập sách
    private void setBookInputEnabled(boolean enabled) {
        btnSelectBook.setEnabled(enabled);
        btnAddBook.setEnabled(enabled);
        if (!enabled) {
            txtSachDaChon.setText("");
            selectedMaSach = null;
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
        
        removeFocusListenerForLoad(); 
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
        txtSachDaChon.setText("");
        selectedMaSach = null;
    }
    
    // ====== HÀM XÓA CHI TIẾT CŨ TRONG DB ======
    private void deleteOldDetails(String maPM) {
        String sql = "DELETE FROM chitietmuon WHERE ma_phieu_muon = ?";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPM);
            ps.executeUpdate();
            // Không cần thông báo thành công, chỉ cần chạy ngầm
        } catch (SQLException e) {
            // Trường hợp lỗi khóa ngoại (nếu có bảng khác trỏ vào CTM)
            JOptionPane.showMessageDialog(this, "Cảnh báo: Không thể xóa chi tiết mượn cũ. Kiểm tra các bảng phụ thuộc.\n" + e.getMessage(), "Lỗi Xóa Chi Tiết", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    // ====== TẢI DỮ LIỆU PHIẾU MƯỢN ĐỂ SỬA (VÀ BẬT CHỨC NĂNG THÊM SÁCH) ======
    private void loadBorrowSlipData(String maPM) {
        if (maPM.isEmpty()) return;

        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT ma_nguoi_doc, ma_nhan_vien, ngay_muon, ngay_tra FROM phieumuon WHERE ma_phieu_muon = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, maPM);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // 1. Tải thông tin Header
                txtMaNguoiDoc.setText(rs.getString("ma_nguoi_doc"));
                txtMaNhanVien.setText(rs.getString("ma_nhan_vien"));
                txtNgayMuon.setText(rs.getDate("ngay_muon").toString()); 
                java.sql.Date ngayTra = rs.getDate("ngay_tra");
                txtNgayTra.setText(ngayTra != null ? ngayTra.toString() : "");
                
                // 2. XÓA TẤT CẢ CHI TIẾT SÁCH CŨ LIÊN QUAN TRONG DB
                deleteOldDetails(maPM); 
                
                // 3. Bật chỉnh sửa cho các trường dữ liệu cần thiết
                txtMaNguoiDoc.setEditable(true);
                txtMaNhanVien.setEditable(true);
                txtNgayTra.setEditable(true);
                
                // 4. BẬT CHỨC NĂNG THÊM SÁCH MỚI
                setBookInputEnabled(true); 
                
                JOptionPane.showMessageDialog(this, "✅ Đã tải phiếu mượn " + maPM + ". Chi tiết sách cũ đã được xóa. Vui lòng chọn và thêm sách mới.", "Thành Công", JOptionPane.INFORMATION_MESSAGE);

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
            
            // FIX LỖI: Sử dụng java.sql.Date.valueOf(String)
            try {
                 stmt.setDate(4, java.sql.Date.valueOf(txtNgayMuon.getText()));
            } catch (IllegalArgumentException e) {
                 JOptionPane.showMessageDialog(this, "❌ Lỗi định dạng ngày mượn: Ngày phải theo format YYYY-MM-DD.", "Lỗi Định dạng", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            
            String ngayTraText = txtNgayTra.getText().trim();
            if (ngayTraText.isEmpty()) {
                stmt.setNull(5, Types.DATE);
            } else {
                // FIX LỖI: Sử dụng java.sql.Date.valueOf(String)
                 try {
                     stmt.setDate(5, java.sql.Date.valueOf(ngayTraText));
                 } catch (IllegalArgumentException e) {
                     JOptionPane.showMessageDialog(this, "❌ Lỗi định dạng ngày trả: Ngày phải theo format YYYY-MM-DD.", "Lỗi Định dạng", JOptionPane.ERROR_MESSAGE);
                     return;
                 }
            }
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Thêm phiếu mượn thành công! Vui lòng chọn và thêm sách vào phiếu.", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            
            // --- KHÓA CHỨC NĂNG THÊM PHIẾU VÀ BẬT CHỨC NĂNG THÊM SÁCH ---
            btnAdd.setEnabled(false); 
            setBookInputEnabled(true);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi thêm phiếu mượn: " + ex.getMessage());
        }
    }
    
    private void addBookToBorrowSlip() {
        String maPM = txtMaPhieuMuon.getText();
        
        if (selectedMaSach == null || selectedMaSach.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhấn 'Tìm & Chọn' để chọn sách trước.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Lấy ngày mượn từ trường Ngày Mượn (đã được điền ở chế độ Thêm/Sửa)
        String ngayMuonText = txtNgayMuon.getText();
        if (ngayMuonText.contains("Không đổi")) {
            JOptionPane.showMessageDialog(this, "❌ Không thể thêm sách. Vui lòng tải lại dữ liệu phiếu mượn để có Ngày mượn hợp lệ.", "Lỗi Dữ liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "INSERT INTO chitietmuon (ma_phieu_muon, ma_sach, ngay_muon, tinh_trang_sach) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, maPM);
            stmt.setString(2, selectedMaSach);
            
            // FIX LỖI: Sử dụng java.sql.Date.valueOf(String)
            stmt.setDate(3, java.sql.Date.valueOf(ngayMuonText)); 
            
            // Tình trạng Sách: "Đang mượn"
            stmt.setString(4, "Đang mượn"); // <-- GÁN GIÁ TRỊ CỐ ĐỊNH THEO YÊU CẦU MỚI
            
            int rowsAffected = stmt.executeUpdate(); 

            if (rowsAffected > 0) {
                 JOptionPane.showMessageDialog(this, "✅ Đã thêm sách " + selectedMaSach + " vào phiếu mượn " + maPM + " với tình trạng: Đang mượn.", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                 // Reset lựa chọn sách sau khi thêm thành công
                 txtSachDaChon.setText("");
                 selectedMaSach = null;
            } else {
                 JOptionPane.showMessageDialog(this, "❌ Thao tác thêm chi tiết mượn thất bại (Không có dòng nào được thêm).", "Lỗi DB", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi thêm chi tiết mượn: Mã sách (" + selectedMaSach + ") đã được thêm vào phiếu mượn này rồi.", "Lỗi Trùng Lặp", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi SQL khi thêm chi tiết mượn: " + ex.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi định dạng ngày: Vui lòng kiểm tra lại định dạng ngày mượn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
                // FIX LỖI: Sử dụng java.sql.Date.valueOf(String)
                 try {
                     stmt.setDate(3, java.sql.Date.valueOf(ngayTraText));
                 } catch (IllegalArgumentException e) {
                     JOptionPane.showMessageDialog(this, "❌ Lỗi định dạng ngày trả: Ngày phải theo format YYYY-MM-DD.", "Lỗi Định dạng", JOptionPane.ERROR_MESSAGE);
                     return;
                 }
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

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new BorrowSlipForm().setVisible(true));
//    }
}
