package SQL;

import java.awt.*;
import javax.swing.*;
import java.sql.*;

public class ReaderForm extends JFrame {
    private JTextField txtMaNguoiDoc, txtHoTen, txtDonVi, txtDiaChi, txtSoDienThoai;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    public ReaderForm() {
        setTitle("📘 Tạo Tài Khoản");
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
        btnClear.addActionListener(e -> setAddMode()); // Làm mới
        
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

        // Mặc định ở chế độ Thêm
        setAddMode();
    }

    // Kiểm tra xem đang ở chế độ Sửa/Xóa hay không
    private boolean isEditDeleteMode() {
        return btnAdd.isEnabled() == false;
    }

    // ====== CHUYỂN CHẾ ĐỘ THÊM/LÀM MỚI ======
    private void setAddMode() {
        txtMaNguoiDoc.setEditable(false); // Khóa ID
        clearFieldsContent();
        generateReaderID(); // Tạo mã mới
        
        // Cập nhật trạng thái nút
        btnAdd.setEnabled(true);
        btnUpdate.setText("✏️ Sửa");
        btnDelete.setText("🗑️ Xóa");
    }
    
    // ====== CHUYỂN CHẾ ĐỘ SỬA/XÓA ======
    private void setEditDeleteMode() {
        if (!isEditDeleteMode()) { // Chỉ chuyển đổi nếu chưa ở chế độ này
            txtMaNguoiDoc.setEditable(true); // Mở khóa ID
            clearFieldsContent();
            
            // Cập nhật trạng thái nút
            btnAdd.setEnabled(false);
            btnUpdate.setText("✅ Cập nhật");
            btnDelete.setText("❌ Xác nhận Xóa");
            JOptionPane.showMessageDialog(this, "Đã chuyển sang chế độ SỬA/XÓA. Vui lòng nhập Mã người đọc cần thao tác.");
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
        if (txtMaNguoiDoc.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã người đọc cần cập nhật.");
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
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Cập nhật thành công ID: " + txtMaNguoiDoc.getText());
            setAddMode(); // Quay về chế độ Thêm sau khi hoàn tất

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi cập nhật: " + ex.getMessage());
        }
    }

    // ====== XÓA ======
    private void deleteReader() {
        if (txtMaNguoiDoc.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã người đọc cần xóa.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa người đọc ID: " + txtMaNguoiDoc.getText() + "?", "Xác nhận Xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = MySQLConnection.getConnection()) {
                String sql = "DELETE FROM nguoidoc WHERE ma_nguoi_doc=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, txtMaNguoiDoc.getText());
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "🗑️ Xóa thành công ID: " + txtMaNguoiDoc.getText());
                setAddMode(); // Quay về chế độ Thêm sau khi hoàn tất

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi xóa: " + ex.getMessage());
            }
        }
    }

    // ====== AUTO-INCREMENT MÃ NGƯỜI ĐỌC ======
    private void generateReaderID() {
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT MAX(CAST(SUBSTRING(ma_nguoi_doc, 3) AS UNSIGNED)) AS maxID FROM nguoidoc";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            int nextID = 1;
            if (rs.next() && rs.getInt("maxID") > 0) {
                nextID = rs.getInt("maxID") + 1;
            }

            txtMaNguoiDoc.setText("ND" + String.format("%03d", nextID));

        } catch (SQLException e) {
            txtMaNguoiDoc.setText("ND001");
        }
    }

    // ====== MAIN ======
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReaderForm().setVisible(true));
    }
}