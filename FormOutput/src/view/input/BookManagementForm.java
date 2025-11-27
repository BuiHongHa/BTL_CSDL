package view.input;

import util.Database;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class BookManagementForm extends JFrame {

    private JTextField txtMaSach, txtTenSach, txtTacGia, txtNamXB;
    private JTextField txtTheLoai;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnView;

    private FocusAdapter focusListener;
    private boolean isEditDeleteMode = false;

    public BookManagementForm() {
        setTitle("📘 Quản lý Sách");
        setSize(480, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ==== TIÊU ĐỀ ====
        JLabel lblTitle = new JLabel("QUẢN LÝ SÁCH THƯ VIỆN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 100, 150));
        add(lblTitle, BorderLayout.NORTH);

        // ==== FORM NHẬP ====
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        Font fontLabel = new Font("Segoe UI", Font.PLAIN, 14);

        formPanel.add(createLabel("Mã sách:", fontLabel));
        txtMaSach = new JTextField();
        formPanel.add(txtMaSach);

        formPanel.add(createLabel("Tên sách:", fontLabel));
        txtTenSach = new JTextField();
        formPanel.add(txtTenSach);

        formPanel.add(createLabel("Tác giả:", fontLabel));
        txtTacGia = new JTextField();
        formPanel.add(txtTacGia);

        formPanel.add(createLabel("Năm xuất bản:", fontLabel));
        txtNamXB = new JTextField();
        formPanel.add(txtNamXB);

        formPanel.add(createLabel("Thể loại:", fontLabel));
        txtTheLoai = new JTextField();
        formPanel.add(txtTheLoai);

        add(formPanel, BorderLayout.CENTER);

        // ==== BUTTON PANEL ====
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

        // ==== EVENT LISTENERS ====
        btnAdd.addActionListener(e -> addBook());
        btnClear.addActionListener(e -> setAddMode());
        btnView.addActionListener(e -> JOptionPane.showMessageDialog(this, "Danh sách sách sẽ hiển thị ở form riêng."));

        btnUpdate.addActionListener(e -> {
            if (isEditDeleteMode) updateBook();
            else setEditDeleteMode(true);
        });

        btnDelete.addActionListener(e -> {
            if (isEditDeleteMode) deleteBook();
            else setEditDeleteMode(true);
        });

        setAddMode();
    }

    // ===== Utilities =====
    private JLabel createLabel(String text, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        return lbl;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(220, 235, 250));
        btn.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 150)));
        return btn;
    }

    private void attachFocusListenerForLoad() {
        removeFocusListenerForLoad();
        focusListener = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                if (isEditDeleteMode && evt.getSource() == txtMaSach)
                    loadBookData(txtMaSach.getText().trim());
            }
        };
        txtMaSach.addFocusListener(focusListener);
    }

    private void removeFocusListenerForLoad() {
        if (focusListener != null) {
            txtMaSach.removeFocusListener(focusListener);
            focusListener = null;
        }
    }

    // ===== Modes =====
    private void setAddMode() {
        isEditDeleteMode = false;
        clearFieldsContent();

        txtMaSach.setEditable(true);
        txtTenSach.setEditable(true);
        txtTacGia.setEditable(true);
        txtNamXB.setEditable(true);
        txtTheLoai.setEditable(true);

        btnAdd.setEnabled(true);
        btnUpdate.setText("✏️ Sửa");
        btnDelete.setText("🗑️ Xóa");

        removeFocusListenerForLoad();
    }

    private void setEditDeleteMode(boolean change) {
        if (!change) return;

        isEditDeleteMode = true;
        clearFieldsContent();
        txtMaSach.setEditable(true);

        txtTenSach.setEditable(false);
        txtTacGia.setEditable(false);
        txtNamXB.setEditable(false);
        txtTheLoai.setEditable(false);

        btnAdd.setEnabled(false);
        btnUpdate.setText("✅ Cập nhật");
        btnDelete.setText("❌ Xác nhận Xóa");

        JOptionPane.showMessageDialog(this, "Nhập mã sách và nhấn Tab để tải dữ liệu.");
        attachFocusListenerForLoad();
    }

    private void clearFieldsContent() {
        txtMaSach.setText("");
        txtTenSach.setText("");
        txtTacGia.setText("");
        txtNamXB.setText("");
        txtTheLoai.setText("");
    }

    // ===== Database Methods =====
    private void loadBookData(String maSach) {
        if (maSach.isEmpty()) return;
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT ten_sach, tac_gia, nam_xuat_ban, the_loai FROM sach WHERE ma_sach=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, maSach);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                txtTenSach.setText(rs.getString(1));
                txtTacGia.setText(rs.getString(2));
                txtNamXB.setText(rs.getString(3));
                txtTheLoai.setText(rs.getString(4));

                txtTenSach.setEditable(true);
                txtTacGia.setEditable(true);
                txtNamXB.setEditable(true);
                txtTheLoai.setEditable(true);

                JOptionPane.showMessageDialog(this, "Đã tải dữ liệu sách!");
            } else {
                clearFieldsContent();
                txtMaSach.setText(maSach);
                JOptionPane.showMessageDialog(this, "Không tìm thấy sách!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }

    private void addBook() {
        if (isEditDeleteMode) return;

        String ma = txtMaSach.getText().trim();
        String ten = txtTenSach.getText().trim();
        String tg = txtTacGia.getText().trim();
        String nam = txtNamXB.getText().trim();
        String tl = txtTheLoai.getText().trim();

        if (ma.isEmpty() || ten.isEmpty() || tg.isEmpty() || nam.isEmpty() || tl.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO sach VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ma);
            ps.setString(2, ten);
            ps.setString(3, tg);

            try {
                ps.setInt(4, Integer.parseInt(nam));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi: Năm phải là số!");
                return;
            }

            ps.setString(5, tl);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "✔ Thêm sách thành công!");
            setAddMode();

        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Mã sách đã tồn tại!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm sách: " + ex.getMessage());
        }
    }

    // === 🔥 FIXED: UPDATE BOOK ===
    private void updateBook() {
        if (!isEditDeleteMode || txtMaSach.getText().isEmpty() || !txtTenSach.isEditable()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã và tải dữ liệu trước khi cập nhật.");
            return;
        }

        String ma = txtMaSach.getText().trim();
        String ten = txtTenSach.getText().trim();
        String tg = txtTacGia.getText().trim();
        String nam = txtNamXB.getText().trim();
        String tl = txtTheLoai.getText().trim();

        if (ten.isEmpty() || tg.isEmpty() || nam.isEmpty() || tl.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE sach SET ten_sach=?, tac_gia=?, nam_xuat_ban=?, the_loai=? WHERE ma_sach=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, ten);
            ps.setString(2, tg);

            try {
                ps.setInt(3, Integer.parseInt(nam));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi: Năm phải là số!");
                return;
            }

            ps.setString(4, tl);
            ps.setString(5, ma);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "✔ Cập nhật thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy mã để cập nhật!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            }

            setAddMode();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi cập nhật: " + ex.getMessage());
        }
    }

    private boolean isBookReferenced(String maSach) throws SQLException {
        String sql1 = "SELECT 1 FROM chitietmuon WHERE ma_sach=? LIMIT 1";
        String sql2 = "SELECT 1 FROM phieuphat WHERE ma_sach=? LIMIT 1";

        try (Connection conn = Database.getConnection()) {
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            ps1.setString(1, maSach);
            if (ps1.executeQuery().next()) return true;

            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ps2.setString(1, maSach);
            return ps2.executeQuery().next();
        }
    }

    private void deleteBook() {
        if (!isEditDeleteMode || txtMaSach.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập mã sách trước!");
            return;
        }

        String ma = txtMaSach.getText().trim();

        try {
            if (isBookReferenced(ma)) {
                JOptionPane.showMessageDialog(this, "Không thể xóa, sách đang liên quan giao dịch!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kiểm tra tham chiếu!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xóa sách này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = Database.getConnection()) {
            String sql = "DELETE FROM sach WHERE ma_sach=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ma);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "🗑️ Xóa thành công!");
            setAddMode();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa: " + ex.getMessage());
        }
    }
}
