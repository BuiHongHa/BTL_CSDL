package view.input;

import util.Database;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ReaderForm extends JFrame {

    private JTextField txtMaNguoiDoc, txtHoTen, txtDonVi, txtDiaChi, txtSoDienThoai;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private FocusAdapter focusListener;

    public ReaderForm() {
        setTitle("📘 Quản lý Người Đọc");
        setSize(450, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnAdd = new JButton("➕ Thêm");
        btnUpdate = new JButton("✏️ Sửa");
        btnDelete = new JButton("🗑️ Xóa");
        btnClear = new JButton("🔄 Làm mới");

        Dimension size = new Dimension(100, 30);
        for (JButton b : new JButton[]{btnAdd, btnUpdate, btnDelete, btnClear}) {
            b.setPreferredSize(size);
            buttonPanel.add(b);
        }

        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // ===== EVENT =====
        btnAdd.addActionListener(e -> addReader());
        btnClear.addActionListener(e -> setAddMode());

        btnUpdate.addActionListener(e -> {
            if (isEditMode()) updateReader();
            else setEditMode();
        });

        btnDelete.addActionListener(e -> {
            if (isEditMode()) deleteReader();
            else setEditMode();
        });

        setAddMode();
    }

    // ===================== MODE =====================

    private boolean isEditMode() {
        return !btnAdd.isEnabled();
    }

    private void attachFocusListenerForLoad() {
        removeFocusListenerForLoad();
        focusListener = new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (isEditMode() && e.getSource() == txtMaNguoiDoc) {
                    loadReaderData(txtMaNguoiDoc.getText().trim());
                }
            }
        };
        txtMaNguoiDoc.addFocusListener(focusListener);
    }

    private void removeFocusListenerForLoad() {
        if (focusListener != null) {
            txtMaNguoiDoc.removeFocusListener(focusListener);
            focusListener = null;
        }
    }

    private void setAddMode() {
        txtMaNguoiDoc.setEditable(false);
        clearFields();
        generateReaderID();

        txtHoTen.setEditable(true);
        txtDonVi.setEditable(true);
        txtDiaChi.setEditable(true);
        txtSoDienThoai.setEditable(true);

        btnAdd.setEnabled(true);
        btnUpdate.setText("✏️ Sửa");
        btnDelete.setText("🗑️ Xóa");

        removeFocusListenerForLoad();
    }

    private void setEditMode() {
        txtMaNguoiDoc.setEditable(true);
        clearFields();

        txtHoTen.setEditable(false);
        txtDonVi.setEditable(false);
        txtDiaChi.setEditable(false);
        txtSoDienThoai.setEditable(false);

        btnAdd.setEnabled(false);
        btnUpdate.setText("✔ Lưu sửa");
        btnDelete.setText("❌ Xóa ngay");

        JOptionPane.showMessageDialog(this, "Nhập Mã người đọc và nhấn Tab để tải thông tin.");
        attachFocusListenerForLoad();
    }

    private void clearFields() {
        txtMaNguoiDoc.setText("");
        txtHoTen.setText("");
        txtDonVi.setText("");
        txtDiaChi.setText("");
        txtSoDienThoai.setText("");
    }

    // ===================== CRUD =====================

    private void loadReaderData(String maND) {
        if (maND.isEmpty()) return;

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM nguoidoc WHERE ma_nguoi_doc=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, maND);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "⚠ Không tồn tại người đọc!");
                clearFields(); txtMaNguoiDoc.setText(maND);
                return;
            }

            txtHoTen.setText(rs.getString("ho_ten"));
            txtDonVi.setText(rs.getString("don_vi"));
            txtDiaChi.setText(rs.getString("dia_chi"));
            txtSoDienThoai.setText(rs.getString("so_dien_thoai"));

            txtHoTen.setEditable(true);
            txtDonVi.setEditable(true);
            txtDiaChi.setEditable(true);
            txtSoDienThoai.setEditable(true);

            JOptionPane.showMessageDialog(this, "✔ Đã tải thông tin!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi: " + ex.getMessage());
        }
    }

    private boolean hasForeignKey(String maND) throws SQLException {
        try (Connection c = Database.getConnection()) {
            String sql = "SELECT 1 FROM phieumuon WHERE ma_nguoi_doc=? LIMIT 1";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, maND);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    private void addReader() {
        try (Connection c = Database.getConnection()) {
            String sql = "INSERT INTO nguoidoc VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, txtMaNguoiDoc.getText());
            ps.setString(2, txtHoTen.getText());
            ps.setString(3, txtDonVi.getText());
            ps.setString(4, txtDiaChi.getText());
            ps.setString(5, txtSoDienThoai.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            setAddMode();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi: " + ex.getMessage());
        }
    }

    private void updateReader() {
        try (Connection c = Database.getConnection()) {
            String sql = "UPDATE nguoidoc SET ho_ten=?, don_vi=?, dia_chi=?, so_dien_thoai=? WHERE ma_nguoi_doc=?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, txtHoTen.getText());
            ps.setString(2, txtDonVi.getText());
            ps.setString(3, txtDiaChi.getText());
            ps.setString(4, txtSoDienThoai.getText());
            ps.setString(5, txtMaNguoiDoc.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✔ Cập nhật thành công!");
            setAddMode();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi: " + ex.getMessage());
        }
    }

    private void deleteReader() {
        String id = txtMaNguoiDoc.getText();
        try {
            if (hasForeignKey(id)) {
                JOptionPane.showMessageDialog(this, "⚠ Không thể xoá! Người đọc còn lịch sử mượn/phạt.");
                return;
            }
        } catch (Exception ignored) {}

        int cf = JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xoá?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (cf != JOptionPane.YES_OPTION) return;

        try (Connection c = Database.getConnection()) {
            String sql = "DELETE FROM nguoidoc WHERE ma_nguoi_doc=?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "🗑 Đã xoá!");
            setAddMode();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi: " + ex.getMessage());
        }
    }

    // ===================== AUTO ID =====================

    private void generateReaderID() {
        try (Connection c = Database.getConnection()) {
            String sql = "SELECT ma_nguoi_doc FROM nguoidoc ORDER BY ma_nguoi_doc DESC LIMIT 1";
            ResultSet rs = c.prepareStatement(sql).executeQuery();
            if (!rs.next()) { txtMaNguoiDoc.setText("ND001"); return; }
            int num = Integer.parseInt(rs.getString(1).substring(2)) + 1;
            txtMaNguoiDoc.setText("ND" + String.format("%03d", num));
        } catch (Exception e) { txtMaNguoiDoc.setText("ND001"); }
    }
}
