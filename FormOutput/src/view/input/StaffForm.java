package view.input;

import util.Database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class StaffForm extends JFrame {

    private JTextField txtMa, txtTen, txtChucVu, txtSDT;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JTable table;
    private DefaultTableModel model;

    private boolean isEditDeleteMode = false;

    public StaffForm() {
        setTitle("Quản lý Nhân Viên");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // HEADER
        JLabel header = new JLabel("QUẢN LÝ NHÂN VIÊN", JLabel.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(0, 102, 204));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setPreferredSize(new Dimension(100, 50));
        add(header, BorderLayout.NORTH);

        // SPLIT PANE
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.4);

        // LEFT FORM
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Thông tin chi tiết"));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 20));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);

        formPanel.add(createLabel("Mã nhân viên:", labelFont));
        txtMa = new JTextField(); formPanel.add(txtMa);

        formPanel.add(createLabel("Họ và tên:", labelFont));
        txtTen = new JTextField(); formPanel.add(txtTen);

        formPanel.add(createLabel("Chức vụ:", labelFont));
        txtChucVu = new JTextField(); formPanel.add(txtChucVu);

        formPanel.add(createLabel("Số điện thoại:", labelFont));
        txtSDT = new JTextField(); formPanel.add(txtSDT);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdd = createStyledButton("➕ Thêm");
        btnUpdate = createStyledButton("✏️ Sửa");
        btnDelete = createStyledButton("🗑️ Xóa");
        btnClear = createStyledButton("🔄 Làm mới");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);

        // RIGHT TABLE
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Danh sách Nhân viên"));

        String[] cols = {"Mã NV", "Họ tên", "Chức vụ", "SĐT"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        JScrollPane scrollPane = new JScrollPane(table);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        // EVENTS
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    txtMa.setText(model.getValueAt(row, 0).toString());
                    txtTen.setText(model.getValueAt(row, 1).toString());
                    txtChucVu.setText(model.getValueAt(row, 2).toString());
                    txtSDT.setText(model.getValueAt(row, 3).toString());
                    setEditDeleteMode();
                }
            }
        });

        btnAdd.addActionListener(e -> addStaff());
        btnClear.addActionListener(e -> setAddMode());
        btnUpdate.addActionListener(e -> { if (isEditDeleteMode) updateStaff(); });
        btnDelete.addActionListener(e -> { if (isEditDeleteMode) deleteStaff(); });

        // INIT
        setAddMode();
        loadTableData();
    }

    // ================= UI HELPER =================
    private JLabel createLabel(String text, Font font) {
        JLabel lbl = new JLabel(text); lbl.setFont(font); return lbl;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(220, 235, 250));
        btn.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204)));
        btn.setPreferredSize(new Dimension(90, 35));
        return btn;
    }

    // ================== STATE ==================
    private void setAddMode() {
        isEditDeleteMode = false;
        txtMa.setEditable(false);
        clearFields();
        generateStaffID();
        table.clearSelection();
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void setEditDeleteMode() {
        isEditDeleteMode = true;
        txtMa.setEditable(false);
        btnAdd.setEnabled(false);
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    private void clearFields() {
        txtMa.setText("");
        txtTen.setText("");
        txtChucVu.setText("");
        txtSDT.setText("");
    }

    // ============= DATABASE LOGIC =============

    private void loadTableData() {
        model.setRowCount(0);
        try (Connection c = Database.getConnection()) {
            ResultSet rs = c.createStatement().executeQuery("SELECT * FROM nhanvien ORDER BY ma_nhan_vien");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("ma_nhan_vien"),
                        rs.getString("ho_ten"),
                        rs.getString("chuc_vu"),
                        rs.getString("so_dien_thoai")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    private void generateStaffID() {
        try (Connection c = Database.getConnection()) {
            ResultSet rs = c.createStatement().executeQuery(
                    "SELECT ma_nhan_vien FROM nhanvien ORDER BY ma_nhan_vien DESC LIMIT 1");
            if (!rs.next()) { txtMa.setText("NV001"); return; }
            int num = Integer.parseInt(rs.getString(1).substring(2)) + 1;
            txtMa.setText("NV" + String.format("%03d", num));
        } catch (Exception e) {
            txtMa.setText("NV001");
        }
    }

    private void addStaff() {
        try (Connection c = Database.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO nhanvien VALUES (?,?,?,?)");
            ps.setString(1, txtMa.getText());
            ps.setString(2, txtTen.getText());
            ps.setString(3, txtChucVu.getText());
            ps.setString(4, txtSDT.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✔ Thêm thành công!");
            loadTableData();
            setAddMode();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi thêm: " + e.getMessage());
        }
    }

    private void updateStaff() {
        try (Connection c = Database.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "UPDATE nhanvien SET ho_ten=?, chuc_vu=?, so_dien_thoai=? WHERE ma_nhan_vien=?");
            ps.setString(1, txtTen.getText());
            ps.setString(2, txtChucVu.getText());
            ps.setString(3, txtSDT.getText());
            ps.setString(4, txtMa.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"✔ Cập nhật thành công!");
            loadTableData();
            setAddMode();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi cập nhật: " + e.getMessage());
        }
    }

    private boolean isStaffReferenced(String ma) throws SQLException {
        try (Connection c = Database.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "SELECT 1 FROM phieumuon WHERE ma_nhan_vien=? LIMIT 1");
            ps.setString(1, ma);
            return ps.executeQuery().next();
        }
    }

    private void deleteStaff() {
        String ma = txtMa.getText();
        try {
            if (isStaffReferenced(ma)) {
                JOptionPane.showMessageDialog(this,
                        "⚠ Không thể xóa! Nhân viên đã lập phiếu mượn.",
                        "Ràng buộc dữ liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (SQLException ignored) {}

        if (JOptionPane.showConfirmDialog(this, "Xóa nhân viên " + ma + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        try (Connection c = Database.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "DELETE FROM nhanvien WHERE ma_nhan_vien=?");
            ps.setString(1, ma);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"🗑 Xóa thành công!");
            loadTableData();
            setAddMode();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi xóa: " + e.getMessage());
        }
    }
}
