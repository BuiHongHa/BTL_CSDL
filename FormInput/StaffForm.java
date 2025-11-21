package SQL;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class StaffForm extends JFrame {
    // Components
    private JTextField txtMa, txtTen, txtChucVu, txtSDT;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JTable table;
    private DefaultTableModel model;
    
    // State
    private boolean isEditDeleteMode = false;

    public StaffForm() {
        setTitle("Quản lý Nhân Viên");
        setSize(1000, 600); // Tăng kích thước để chứa cả bảng
        setLocationRelativeTo(null);
        // Dùng DISPOSE để chỉ đóng form này
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
        setLayout(new BorderLayout(10, 10));

        // ==== HEADER ====
        JLabel header = new JLabel("QUẢN LÝ NHÂN VIÊN", JLabel.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(0, 102, 204));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setPreferredSize(new Dimension(100, 50));
        add(header, BorderLayout.NORTH);

        // ==== MAIN PANEL (Chia đôi: Trái Form - Phải Table) ====
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400); // Form chiếm 400px
        splitPane.setResizeWeight(0.4); // Form chiếm 40%
        
        // --- LEFT: INPUT FORM ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Thông tin chi tiết"));
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 20));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        Font fontLabel = new Font("Segoe UI", Font.PLAIN, 14);
        
        formPanel.add(createLabel("Mã nhân viên:", fontLabel));
        txtMa = new JTextField();
        formPanel.add(txtMa);

        formPanel.add(createLabel("Họ và tên:", fontLabel));
        txtTen = new JTextField();
        formPanel.add(txtTen);

        formPanel.add(createLabel("Chức vụ:", fontLabel));
        txtChucVu = new JTextField();
        formPanel.add(txtChucVu);

        formPanel.add(createLabel("Số điện thoại:", fontLabel));
        txtSDT = new JTextField();
        formPanel.add(txtSDT);
        
        // Button Panel (Nằm dưới Form)
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
        
        // --- RIGHT: TABLE LIST ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Danh sách Nhân viên"));
        
        String[] columns = {"Mã NV", "Họ tên", "Chức vụ", "SĐT"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp trên bảng
            }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        JScrollPane scrollPane = new JScrollPane(table);
        
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Thêm vào SplitPane
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        // ==== EVENT HANDLERS ====
        
        // 1. Sự kiện chọn dòng trong bảng -> Đổ dữ liệu vào Form
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    String ma = model.getValueAt(row, 0).toString();
                    String ten = model.getValueAt(row, 1).toString();
                    String cv = model.getValueAt(row, 2).toString();
                    String sdt = model.getValueAt(row, 3).toString();
                    
                    // Điền vào form
                    txtMa.setText(ma);
                    txtTen.setText(ten);
                    txtChucVu.setText(cv);
                    txtSDT.setText(sdt);
                    
                    // Chuyển sang chế độ Sửa/Xóa
                    setEditDeleteMode();
                }
            }
        });

        // 2. Các nút chức năng
        btnAdd.addActionListener(e -> addStaff());
        btnClear.addActionListener(e -> setAddMode());
        
        btnUpdate.addActionListener(e -> {
            if (isEditDeleteMode) updateStaff();
        });

        btnDelete.addActionListener(e -> {
            if (isEditDeleteMode) deleteStaff();
        });

        // Khởi động
        setAddMode(); 
        loadTableData(); // Tải dữ liệu ngay khi mở
    }

    // --- HELPER UI METHODS ---
    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(220, 235, 250));
        btn.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204)));
        btn.setPreferredSize(new Dimension(90, 35));
        return btn;
    }
    
    // --- STATE MANAGEMENT ---
    
    private void setAddMode() {
        isEditDeleteMode = false;
        txtMa.setEditable(false); 
        clearFields();
        generateStaffID(); 
        table.clearSelection(); // Bỏ chọn trên bảng

        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false); // Khóa nút Sửa
        btnDelete.setEnabled(false); // Khóa nút Xóa
    }
    
    private void setEditDeleteMode() {
        isEditDeleteMode = true;
        txtMa.setEditable(false); // Không cho sửa Mã khi đang update

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

    // --- DATABASE OPERATIONS ---

    // 1. Tải dữ liệu vào bảng
    private void loadTableData() {
        model.setRowCount(0);
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT * FROM nhanvien ORDER BY ma_nhan_vien";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("ma_nhan_vien"),
                    rs.getString("ho_ten"),
                    rs.getString("chuc_vu"),
                    rs.getString("so_dien_thoai")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    // 2. Tự động sinh ID (Gap Filling)
    private void generateStaffID() {
        int nextID = 1;
        try (Connection conn = MySQLConnection.getConnection()) {
            String gapSql = "SELECT MIN(t1.id) + 1 AS next_id FROM (SELECT CAST(SUBSTRING(ma_nhan_vien, 3) AS UNSIGNED) AS id FROM nhanvien) t1 " +
                            "LEFT JOIN (SELECT CAST(SUBSTRING(ma_nhan_vien, 3) AS UNSIGNED) AS id FROM nhanvien) t2 ON t1.id + 1 = t2.id " +
                            "WHERE t2.id IS NULL AND t1.id >= 1";
            
            String maxSql = "SELECT MAX(CAST(SUBSTRING(ma_nhan_vien, 3) AS UNSIGNED)) AS max_id FROM nhanvien";
            
            String check1 = "SELECT ma_nhan_vien FROM nhanvien WHERE ma_nhan_vien = 'NV001'";
            try(Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(check1)) {
                 if(!rs.next()) {
                     txtMa.setText("NV001");
                     return;
                 }
            }

            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(gapSql)) {
                if (rs.next()) {
                    int gapId = rs.getInt("next_id");
                    if (gapId > 0) nextID = gapId;
                }
            }

            if (nextID == 1) { 
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(maxSql)) {
                    if (rs.next()) {
                        int maxId = rs.getInt("max_id");
                        if (maxId > 0) nextID = maxId + 1;
                    }
                }
            }
            
            txtMa.setText("NV" + String.format("%03d", nextID));

        } catch (Exception e) {
            txtMa.setText("NV001"); 
        }
    }

    // 3. Thêm Nhân Viên
    private void addStaff() {
        String ma = txtMa.getText().trim();
        String ten = txtTen.getText().trim();
        String cv = txtChucVu.getText().trim();
        String sdt = txtSDT.getText().trim();

        if (ma.isEmpty() || ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã nhân viên và Họ tên là bắt buộc.");
            return;
        }

        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "INSERT INTO nhanvien (ma_nhan_vien, ho_ten, chuc_vu, so_dien_thoai) VALUES (?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ma);
            ps.setString(2, ten);
            ps.setString(3, cv);
            ps.setString(4, sdt);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Thêm nhân viên thành công!");
            
            loadTableData(); // Cập nhật bảng ngay lập tức
            setAddMode();    // Quay về chế độ thêm

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi thêm nhân viên: " + ex.getMessage());
        }
    }

    // 4. Cập nhật Nhân Viên
    private void updateStaff() {
        String ma = txtMa.getText().trim();
        if (ma.isEmpty()) return;

        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "UPDATE nhanvien SET ho_ten=?, chuc_vu=?, so_dien_thoai=? WHERE ma_nhan_vien=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtTen.getText().trim());
            ps.setString(2, txtChucVu.getText().trim());
            ps.setString(3, txtSDT.getText().trim());
            ps.setString(4, ma);

            int r = ps.executeUpdate();
            if (r > 0) {
                JOptionPane.showMessageDialog(this, "✅ Cập nhật thành công.");
                loadTableData(); // Cập nhật bảng
                setAddMode();    // Quay về chế độ thêm (reset form)
            } else {
                JOptionPane.showMessageDialog(this, "❌ Mã nhân viên không tồn tại.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + ex.getMessage());
        }
    }

    // 5. Kiểm tra phụ thuộc trước khi xóa
    private boolean isStaffReferenced(String maNV) throws SQLException {
        String sqlPM = "SELECT 1 FROM phieumuon WHERE ma_nhan_vien = ? LIMIT 1";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlPM)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return true;
            }
        }
        return false;
    }

    // 6. Xóa Nhân Viên
    private void deleteStaff() {
        String ma = txtMa.getText().trim();
        if (ma.isEmpty()) return;
        
        try {
            if (isStaffReferenced(ma)) {
                JOptionPane.showMessageDialog(this, "❌ Không thể xóa: Nhân viên này đã lập các phiếu mượn trong hệ thống.\nVui lòng xóa các phiếu liên quan trước.", "Ràng buộc dữ liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kiểm tra ràng buộc: " + e.getMessage());
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nhân viên " + ma + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "DELETE FROM nhanvien WHERE ma_nhan_vien=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ma);
            
            int r = ps.executeUpdate();
            if (r > 0) {
                JOptionPane.showMessageDialog(this, "🗑️ Xóa thành công.");
                loadTableData(); // Cập nhật bảng
                setAddMode();    // Quay về chế độ thêm
            } else {
                JOptionPane.showMessageDialog(this, "❌ Mã nhân viên không tồn tại.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa: " + ex.getMessage());
        }
    }
}
