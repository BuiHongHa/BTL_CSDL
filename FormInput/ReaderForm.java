package SQL;

import java.awt.*;
import javax.swing.*;
import java.sql.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ReaderForm extends JFrame {
    private JTextField txtMaNguoiDoc, txtHoTen, txtDonVi, txtDiaChi, txtSoDienThoai;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    
    private java.awt.event.FocusAdapter focusListener; // Khai báo listener

    public ReaderForm() {
        setTitle("📘 Quản lý Người Đọc");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ======= FORM NHẬP =======
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        formPanel.add(new JLabel("Mã người đọc:"));
        txtMaNguoiDoc = new JTextField();
        formPanel.add(txtMaNguoiDoc);

        formPanel.add(new JLabel("Họ tên:"));
        txtHoTen = new JTextField();
        formPanel.add(txtHoTen);

        formPanel.add(new JLabel("Đơn vị (Lớp/Bộ môn):"));
        txtDonVi = new JTextField();
        formPanel.add(txtDonVi);

        formPanel.add(new JLabel("Địa chỉ:"));
        txtDiaChi = new JTextField();
        formPanel.add(txtDiaChi);

        formPanel.add(new JLabel("Số điện thoại:"));
        txtSoDienThoai = new JTextField();
        formPanel.add(txtSoDienThoai);

        // ======= PANEL NÚT =======
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        btnAdd = new JButton("➕ Thêm");
        btnUpdate = new JButton("✏️ Sửa");
        btnDelete = new JButton("🗑️ Xóa");
        btnClear = new JButton("🔄 Làm mới");

        Dimension btnSize = new Dimension(100, 30);
        for (JButton b : new JButton[]{btnAdd, btnUpdate, btnDelete, btnClear}) {
            b.setPreferredSize(btnSize);
            buttonPanel.add(b);
        }

        // ======= ADD TO FRAME =======
        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // ======= EVENT HANDLERS =======
        btnAdd.addActionListener(e -> addReader());
        btnClear.addActionListener(e -> setAddMode()); 
        
        // Logic cho nút Sửa/Xóa: Chuyển sang chế độ Sửa/Xóa trước khi thao tác
        btnUpdate.addActionListener(e -> {
            if (isEditDeleteMode()) {
                updateReader();
            } else {
                setEditDeleteMode();
            }
        });
        
        btnDelete.addActionListener(e -> {
            if (isEditDeleteMode()) {
                deleteReader();
            } else {
                setEditDeleteMode();
            }
        });

        setAddMode();
    }

    // Kiểm tra xem đang ở chế độ Sửa/Xóa hay không
    private boolean isEditDeleteMode() {
        return btnAdd.isEnabled() == false;
    }
    
    // Gán FocusListener để tải dữ liệu khi mất focus (nhập xong ID)
    private void attachFocusListenerForLoad() {
        removeFocusListenerForLoad(); 
        
        focusListener = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                if (isEditDeleteMode() && evt.getSource() == txtMaNguoiDoc) {
                    loadReaderData(txtMaNguoiDoc.getText().trim());
                }
            }
        };
        txtMaNguoiDoc.addFocusListener(focusListener);
    }
    
    // Xóa FocusListener
    private void removeFocusListenerForLoad() {
        if (focusListener != null) {
            txtMaNguoiDoc.removeFocusListener(focusListener);
            focusListener = null;
        }
    }


    // ====== CHUYỂN CHẾ ĐỘ THÊM/LÀM MỚI ======
    private void setAddMode() {
        txtMaNguoiDoc.setEditable(false); // Khóa ID
        clearFieldsContent();
        generateReaderID(); // Tạo mã mới
        
        txtHoTen.setEditable(true);
        txtDonVi.setEditable(true);
        txtDiaChi.setEditable(true);
        txtSoDienThoai.setEditable(true);
        
        btnAdd.setEnabled(true);
        btnUpdate.setText("✏️ Sửa");
        btnDelete.setText("🗑️ Xóa");
        
        removeFocusListenerForLoad(); // Đảm bảo listener không chạy ở chế độ Thêm
    }
    
    // ====== CHUYỂN CHẾ ĐỘ SỬA/XÓA ======
    private void setEditDeleteMode() {
        if (!isEditDeleteMode()) { // Chỉ chuyển đổi nếu chưa ở chế độ này
            txtMaNguoiDoc.setEditable(true); // Mở khóa ID
            clearFieldsContent();
            
            // Khóa các trường dữ liệu khác, buộc phải tải dữ liệu
            txtHoTen.setEditable(false);
            txtDonVi.setEditable(false);
            txtDiaChi.setEditable(false);
            txtSoDienThoai.setEditable(false);
            
            // Cập nhật trạng thái nút
            btnAdd.setEnabled(false);
            btnUpdate.setText("✅ Cập nhật");
            btnDelete.setText("❌ Xác nhận Xóa");
            JOptionPane.showMessageDialog(this, "Đã chuyển sang chế độ SỬA/XÓA. Vui lòng nhập Mã người đọc cần thao tác và nhấn Tab/Enter.");
            
            attachFocusListenerForLoad(); // Gán listener để tải dữ liệu
        }
    }
    
    // ====== LÀM MỚI (CHỈ XÓA NỘI DUNG) ======
    private void clearFieldsContent() {
        txtMaNguoiDoc.setText("");
        txtHoTen.setText("");
        txtDonVi.setText("");
        txtDiaChi.setText("");
        txtSoDienThoai.setText("");
    }
    
    // ====== TẢI DỮ LIỆU NGƯỜI ĐỌC ĐỂ SỬA/XÓA ======
    private void loadReaderData(String maND) {
        if (maND.isEmpty()) return;
        
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT ho_ten, don_vi, dia_chi, so_dien_thoai FROM nguoidoc WHERE ma_nguoi_doc = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, maND);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                txtHoTen.setText(rs.getString("ho_ten"));
                txtDonVi.setText(rs.getString("don_vi"));
                txtDiaChi.setText(rs.getString("dia_chi"));
                txtSoDienThoai.setText(rs.getString("so_dien_thoai"));
                
                // Bật chỉnh sửa cho các trường dữ liệu
                txtHoTen.setEditable(true);
                txtDonVi.setEditable(true);
                txtDiaChi.setEditable(true);
                txtSoDienThoai.setEditable(true);
                
                JOptionPane.showMessageDialog(this, "✅ Đã tải thông tin người đọc " + maND + ". Bạn có thể chỉnh sửa.");

            } else {
                clearFieldsContent();
                txtMaNguoiDoc.setText(maND);
                JOptionPane.showMessageDialog(this, "❌ Không tìm thấy người đọc với Mã: " + maND, "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi truy vấn CSDL: " + ex.getMessage());
        }
    }


    // ====== THÊM NGƯỜI ĐỌC ======
    private void addReader() {
        if (!btnAdd.isEnabled()) return;
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "INSERT INTO nguoidoc (ma_nguoi_doc, ho_ten, don_vi, dia_chi, so_dien_thoai) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, txtMaNguoiDoc.getText());
            stmt.setString(2, txtHoTen.getText());
            stmt.setString(3, txtDonVi.getText());
            stmt.setString(4, txtDiaChi.getText());
            stmt.setString(5, txtSoDienThoai.getText());
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Thêm người đọc thành công với ID: " + txtMaNguoiDoc.getText());

            setAddMode(); // Quay về chế độ Thêm/Làm mới
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi thêm người đọc: " + ex.getMessage());
        }
    }

    // ====== CẬP NHẬT ======
    private void updateReader() {
        if (!isEditDeleteMode() || txtMaNguoiDoc.getText().isEmpty() || !txtHoTen.isEditable()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã người đọc và tải dữ liệu trước khi Cập nhật.");
            return;
        }
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "UPDATE nguoidoc SET ho_ten=?, don_vi=?, dia_chi=?, so_dien_thoai=? WHERE ma_nguoi_doc=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, txtHoTen.getText());
            stmt.setString(2, txtDonVi.getText());
            stmt.setString(3, txtDiaChi.getText());
            stmt.setString(4, txtSoDienThoai.getText());
            stmt.setString(5, txtMaNguoiDoc.getText());
            
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                 JOptionPane.showMessageDialog(this, "✅ Cập nhật thành công ID: " + txtMaNguoiDoc.getText());
            } else {
                 JOptionPane.showMessageDialog(this, "Không tìm thấy Mã người đọc để cập nhật.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
            
            setAddMode(); // Quay về chế độ Thêm sau khi hoàn tất

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi cập nhật: " + ex.getMessage());
        }
    }

    // ====== XÓA ======
    private void deleteReader() {
        if (!isEditDeleteMode() || txtMaNguoiDoc.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã người đọc cần xóa.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa người đọc ID: " + txtMaNguoiDoc.getText() + "?", "Xác nhận Xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = MySQLConnection.getConnection()) {
                String sql = "DELETE FROM nguoidoc WHERE ma_nguoi_doc=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, txtMaNguoiDoc.getText());
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                     JOptionPane.showMessageDialog(this, "🗑️ Xóa thành công ID: " + txtMaNguoiDoc.getText());
                } else {
                     JOptionPane.showMessageDialog(this, "Không tìm thấy Mã người đọc để xóa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                }
                
                setAddMode(); // Quay về chế độ Thêm sau khi hoàn tất

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi xóa: " + ex.getMessage());
            }
        }
    }

    // ====== AUTO-INCREMENT MÃ NGƯỜI ĐỌC ======
    private void generateReaderID() {
        // Tái sử dụng Mã ID bị xóa (Gap filling)
        int nextID = 1;
        try (Connection conn = MySQLConnection.getConnection()) {
            
            // 1. KIỂM TRA ĐỘC LẬP: ND001 có bị thiếu không? 
            String check1Sql = "SELECT ma_nguoi_doc FROM nguoidoc WHERE ma_nguoi_doc = 'ND001'";
            try (PreparedStatement ps = conn.prepareStatement(check1Sql); ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    txtMaNguoiDoc.setText("ND001");
                    return; 
                }
            }
            
            // 2. TÌM GAP KHÁC HOẶC MAX + 1
            String gapSql = "SELECT MIN(t1.id) + 1 AS next_id FROM (SELECT CAST(SUBSTRING(ma_nguoi_doc, 3) AS UNSIGNED) AS id FROM nguoidoc) t1 " +
                                     "LEFT JOIN (SELECT CAST(SUBSTRING(ma_nguoi_doc, 3) AS UNSIGNED) AS id FROM nguoidoc) t2 ON t1.id + 1 = t2.id " +
                                     "WHERE t2.id IS NULL AND t1.id >= 1"; 
            
            String maxSql = "SELECT MAX(CAST(SUBSTRING(ma_nguoi_doc, 3) AS UNSIGNED)) AS max_id FROM nguoidoc";

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
            
            txtMaNguoiDoc.setText("ND" + String.format("%03d", nextID));

        } catch (SQLException e) {
            txtMaNguoiDoc.setText("ND001");
            System.err.println("Lỗi tự động sinh ID: " + e.getMessage());
        }
    }

    // ====== MAIN ======
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReaderForm().setVisible(true));
    }
}