package SQL;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class BookManagementForm extends JFrame {

    private JTextField txtMaSach, txtTenSach, txtTacGia, txtNamXB;
    private JTextField txtTheLoai; 
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnView;

    private java.awt.event.FocusAdapter focusListener; 
    private boolean isEditDeleteMode = false; 

    public BookManagementForm() {
        setTitle("📘 Quản lý Sách");
        setSize(480, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ==== TIÊU ĐỀ ====
        JLabel lblTitle = new JLabel("QUẢN LÝ SÁCH THƯ VIỆN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 100, 150));
        add(lblTitle, BorderLayout.NORTH);

        // ==== FORM NHẬP (GridLayout 5x2) ====
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        Font fontLabel = new Font("Segoe UI", Font.PLAIN, 14);

        // Mã sách
        formPanel.add(createLabel("Mã sách:", fontLabel));
        txtMaSach = new JTextField();
        formPanel.add(txtMaSach);

        // Tên sách
        formPanel.add(createLabel("Tên sách:", fontLabel));
        txtTenSach = new JTextField();
        formPanel.add(txtTenSach);

        // Tác giả
        formPanel.add(createLabel("Tác giả:", fontLabel));
        txtTacGia = new JTextField();
        formPanel.add(txtTacGia);

        // Năm xuất bản
        formPanel.add(createLabel("Năm xuất bản:", fontLabel));
        txtNamXB = new JTextField();
        formPanel.add(txtNamXB);

        // Thể loại
        formPanel.add(createLabel("Thể loại:", fontLabel));
        txtTheLoai = new JTextField();
        formPanel.add(txtTheLoai);

        add(formPanel, BorderLayout.CENTER);

        // ==== PANEL NÚT ====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));

        btnAdd = createStyledButton("➕ Thêm");
        btnUpdate = createStyledButton("✏️ Sửa");
        btnDelete = createStyledButton("🗑️ Xóa");
        btnClear = createStyledButton("🔄 Làm mới");
        btnView = createStyledButton("📄 Xem DS");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnView);

        add(buttonPanel, BorderLayout.SOUTH);

        // ==== EVENT HANDLERS ====
        btnAdd.addActionListener(e -> addBook());
        btnClear.addActionListener(e -> setAddMode());
        btnView.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng xem danh sách sách sẽ được hiển thị ở form riêng."));

        // Logic chuyển đổi chế độ
        btnUpdate.addActionListener(e -> {
            if (isEditDeleteMode) {
                updateBook();
            } else {
                setEditDeleteMode(true);
            }
        });

        btnDelete.addActionListener(e -> {
            if (isEditDeleteMode) {
                deleteBook();
            } else {
                setEditDeleteMode(true);
            }
        });

        setAddMode();
    }
    
    // Hàm trợ giúp tạo Label
    private JLabel createLabel(String text, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        return lbl;
    }

    // Hàm trợ giúp tạo Button
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(220, 235, 250));
        btn.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 150)));
        return btn;
    }
    
    // Gán FocusListener để tải dữ liệu khi mất focus (nhập xong ID)
    private void attachFocusListenerForLoad() {
        removeFocusListenerForLoad(); 
        
        focusListener = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                if (isEditDeleteMode && evt.getSource() == txtMaSach) {
                    loadBookData(txtMaSach.getText().trim());
                }
            }
        };
        txtMaSach.addFocusListener(focusListener);
    }
    
    // Xóa FocusListener
    private void removeFocusListenerForLoad() {
        if (focusListener != null) {
            txtMaSach.removeFocusListener(focusListener);
            focusListener = null;
        }
    }


    // ====== CHUYỂN CHẾ ĐỘ THÊM/LÀM MỚI ======
    private void setAddMode() {
        isEditDeleteMode = false;
        
        // CHO PHÉP NHẬP TAY MÃ SÁCH
        txtMaSach.setEditable(true); 
        clearFieldsContent();
        // generateBookID(); // <-- ĐÃ BỎ TỰ ĐỘNG TẠO ID

        txtTenSach.setEditable(true);
        txtTacGia.setEditable(true);
        txtNamXB.setEditable(true);
        txtTheLoai.setEditable(true); 
        
        btnAdd.setEnabled(true);
        btnUpdate.setText("✏️ Sửa");
        btnDelete.setText("🗑️ Xóa");
        
        removeFocusListenerForLoad(); 
    }
    
    // ====== CHUYỂN CHẾ ĐỘ SỬA/XÓA ======
    private void setEditDeleteMode(boolean shouldChange) {
        if (shouldChange) { 
            isEditDeleteMode = true;
            txtMaSach.setEditable(true); 
            clearFieldsContent();
            
            // Khóa các trường dữ liệu khác, buộc phải tải dữ liệu
            txtTenSach.setEditable(false);
            txtTacGia.setEditable(false);
            txtNamXB.setEditable(false);
            txtTheLoai.setEditable(false); 
            
            btnAdd.setEnabled(false);
            btnUpdate.setText("✅ Cập nhật");
            btnDelete.setText("❌ Xác nhận Xóa");
            JOptionPane.showMessageDialog(this, "Đã chuyển sang chế độ SỬA/XÓA. Vui lòng nhập Mã sách cần thao tác và nhấn Tab/Enter.");
            
            attachFocusListenerForLoad(); // Gán listener để tải dữ liệu
        }
    }
    
    // ====== LÀM MỚI (CHỈ XÓA NỘI DUNG) ======
    private void clearFieldsContent() {
        txtMaSach.setText("");
        txtTenSach.setText("");
        txtTacGia.setText("");
        txtNamXB.setText("");
        txtTheLoai.setText(""); 
    }
    
    // ====== TẢI DỮ LIỆU SÁCH ĐỂ SỬA/XÓA ======
    private void loadBookData(String maSach) {
        if (maSach.isEmpty()) return;
        
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT ten_sach, tac_gia, nam_xuat_ban, the_loai FROM sach WHERE ma_sach = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, maSach);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                txtTenSach.setText(rs.getString("ten_sach"));
                txtTacGia.setText(rs.getString("tac_gia"));
                txtNamXB.setText(String.valueOf(rs.getInt("nam_xuat_ban")));
                txtTheLoai.setText(rs.getString("the_loai")); 
                
                // Bật chỉnh sửa cho các trường dữ liệu
                txtTenSach.setEditable(true);
                txtTacGia.setEditable(true);
                txtNamXB.setEditable(true);
                txtTheLoai.setEditable(true); 
                
                JOptionPane.showMessageDialog(this, "✅ Đã tải thông tin sách " + maSach + ". Bạn có thể chỉnh sửa.");

            } else {
                clearFieldsContent();
                txtMaSach.setText(maSach);
                JOptionPane.showMessageDialog(this, "❌ Không tìm thấy sách với Mã: " + maSach, "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi truy vấn CSDL: " + ex.getMessage());
        }
    }

    // ====== THÊM SÁCH ======
    private void addBook() {
        if (isEditDeleteMode) return;
        
        String ma = txtMaSach.getText().trim();
        String ten = txtTenSach.getText().trim();
        String tg = txtTacGia.getText().trim();
        String nam = txtNamXB.getText().trim();
        String tl = txtTheLoai.getText().trim(); 
        
        // THÊM KIỂM TRA MÃ SÁCH KHÔNG ĐƯỢC ĐỂ TRỐNG
        if (ma.isEmpty() || ten.isEmpty() || tg.isEmpty() || nam.isEmpty() || tl.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin sách (bao gồm Mã sách)!");
            return;
        }

        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "INSERT INTO sach(ma_sach, ten_sach, tac_gia, nam_xuat_ban, the_loai) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setString(1, ma);
            ps.setString(2, ten);
            ps.setString(3, tg);
            
            try {
                ps.setInt(4, Integer.parseInt(nam));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi: Năm xuất bản phải là số nguyên hợp lệ.");
                return;
            }
            
            ps.setString(5, tl);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "✔ Thêm sách thành công với Mã: " + ma);
            setAddMode(); 

        } catch (SQLIntegrityConstraintViolationException ex) {
             JOptionPane.showMessageDialog(this, "❌ Lỗi: Mã sách '" + ma + "' đã tồn tại. Vui lòng chọn mã khác.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi khi thêm sách: " + ex.getMessage());
        }
    }

    // ====== CẬP NHẬT SÁCH ======
    private void updateBook() {
        if (!isEditDeleteMode || txtMaSach.getText().isEmpty() || !txtTenSach.isEditable()) {
             JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã sách và tải dữ liệu trước khi Cập nhật.");
             return;
        }
        
        String ma = txtMaSach.getText().trim();
        String ten = txtTenSach.getText().trim();
        String tg = txtTacGia.getText().trim();
        String nam = txtNamXB.getText().trim();
        String tl = txtTheLoai.getText().trim(); 
        
        if (ten.isEmpty() || tg.isEmpty() || nam.isEmpty() || tl.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin sách!");
            return;
        }

        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "UPDATE sach SET ten_sach=?, tac_gia=?, nam_xuat_ban=?, the_loai=? WHERE ma_sach=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, ten);
            ps.setString(2, tg);
            
            try {
                ps.setInt(3, Integer.parseInt(nam));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi: Năm xuất bản phải là số nguyên hợp lệ.");
                return;
            }
            
            ps.setString(4, tl);
            ps.setString(5, ma);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                 JOptionPane.showMessageDialog(this, "✔ Cập nhật thành công Mã: " + ma);
            } else {
                 JOptionPane.showMessageDialog(this, "Không tìm thấy Mã sách để cập nhật.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }

            setAddMode();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi cập nhật dữ liệu: " + ex.getMessage());
        }
    }
    
    // ====== KIỂM TRA PHỤ THUỘC (REFERENCED CHECK) ======
    private boolean isBookReferenced(String maSach) throws SQLException {
        // Kiểm tra trong bảng Chi Tiết Mượn (chitietmuon)
        String sqlCtm = "SELECT 1 FROM chitietmuon WHERE ma_sach = ? LIMIT 1";
        // Kiểm tra trong bảng Phiếu Phạt (phieuphat)
        String sqlPp = "SELECT 1 FROM phieuphat WHERE ma_sach = ? LIMIT 1";

        try (Connection conn = MySQLConnection.getConnection()) {
            // Check chitietmuon
            try (PreparedStatement psCtm = conn.prepareStatement(sqlCtm)) {
                psCtm.setString(1, maSach);
                try (ResultSet rsCtm = psCtm.executeQuery()) {
                    if (rsCtm.next()) return true;
                }
            }
            
            // Check phieuphat
            try (PreparedStatement psPp = conn.prepareStatement(sqlPp)) {
                psPp.setString(1, maSach);
                try (ResultSet rsPp = psPp.executeQuery()) {
                    if (rsPp.next()) return true;
                }
            }
        }
        return false;
    }


    // ====== XÓA SÁCH (HẠN CHẾ) ======
    private void deleteBook() {
        if (!isEditDeleteMode || txtMaSach.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã sách cần xóa.");
            return;
        }
        
        String maSach = txtMaSach.getText();
        
        // BƯỚC 1: KIỂM TRA PHỤ THUỘC TRƯỚC KHI XÓA
        try {
            if (isBookReferenced(maSach)) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi xóa: Sách này đang có giao dịch (Chi tiết mượn/Phiếu phạt) liên quan. Vui lòng xóa các giao dịch trước.", "Lỗi Khóa Ngoại", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi kiểm tra phụ thuộc: " + e.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sách ID: " + maSach + "?", "Xác nhận Xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // BƯỚC 2: TIẾN HÀNH XÓA (Chỉ khi không có phụ thuộc)
            try (Connection conn = MySQLConnection.getConnection()) {
                String sql = "DELETE FROM sach WHERE ma_sach=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, maSach);
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                     JOptionPane.showMessageDialog(this, "🗑️ Xóa thành công Mã: " + maSach);
                } else {
                     JOptionPane.showMessageDialog(this, "Không tìm thấy Mã sách để xóa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                }
                
                setAddMode(); 

            } catch (SQLException ex) {
                // Trường hợp này chỉ xảy ra nếu có lỗi DB khác (vì logic phụ thuộc đã được kiểm tra ở trên)
                JOptionPane.showMessageDialog(this, "❌ Lỗi xóa: " + ex.getMessage());
            }
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new BookManagementForm().setVisible(true));
//    }
}
