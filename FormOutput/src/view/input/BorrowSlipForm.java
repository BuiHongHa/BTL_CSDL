package view.input;

import util.Database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class BorrowSlipForm extends JFrame {
    private JTextField txtMaPhieuMuon, txtMaNguoiDoc, txtMaNhanVien, txtNgayMuon, txtNgayTra;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnExport;

    private JTextField txtSachDaChon;
    private String selectedMaSach = null;
    private JButton btnSelectBook;
    private JButton btnAddBook;

    private FocusAdapter focusListener;

    public BorrowSlipForm() {
        setTitle("📖 Tạo Phiếu Mượn Sách");
        setSize(600, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

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

        formPanel.add(new JLabel("Sách muốn mượn:"));
        JPanel bookSelectionPanel = new JPanel(new BorderLayout(5, 0));

        txtSachDaChon = new JTextField();
        txtSachDaChon.setEditable(false);

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

        setBookInputEnabled(false);

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

        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addBorrowSlip());
        btnClear.addActionListener(e -> setAddMode());
        btnExport.addActionListener(e -> JOptionPane.showMessageDialog(this, "Danh sách phiếu mượn sẽ hiển thị ở form riêng."));

        btnUpdate.addActionListener(e -> {
            if (isEditDeleteMode()) updateBorrowSlip();
            else setEditDeleteMode();
        });

        btnDelete.addActionListener(e -> {
            if (isEditDeleteMode()) deleteBorrowSlip();
            else setEditDeleteMode();
        });

        setAddMode();
    }

    // ================== SEARCH BOOK DIALOG =====================
    private void openBookSelectionDialog() {
        JDialog dialog = new JDialog(this, "🔍 Tìm kiếm và Chọn Sách", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField txtSearch = new JTextField(30);
        JButton btnSearch = new JButton("Tìm kiếm");
        searchPanel.add(new JLabel("Nhập tên hoặc mã sách:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        String[] columnNames = {"Mã sách", "Tên sách", "Tác giả", "Năm XB"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton btnConfirm = new JButton("✅ Chọn sách này");
        btnConfirm.setEnabled(false);
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnConfirm);

        ActionListener searchAction = e -> {
            String keyword = txtSearch.getText().trim();
            model.setRowCount(0);
            try (Connection conn = Database.getConnection()) {
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
        txtSearch.addActionListener(searchAction);

        table.getSelectionModel().addListSelectionListener(e -> btnConfirm.setEnabled(table.getSelectedRow() != -1));

        btnConfirm.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedMaSach = (String) model.getValueAt(row, 0);
                String tenSach = (String) model.getValueAt(row, 1);
                txtSachDaChon.setText(selectedMaSach + " - " + tenSach);
                dialog.dispose();
            }
        });

        searchAction.actionPerformed(null);
        dialog.add(searchPanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void setBookInputEnabled(boolean enabled) {
        btnSelectBook.setEnabled(enabled);
        btnAddBook.setEnabled(enabled);
        if (!enabled) {
            txtSachDaChon.setText("");
            selectedMaSach = null;
        }
    }

    private boolean isEditDeleteMode() {
        return !btnAdd.isEnabled();
    }

    private void attachFocusListenerForLoad() {
        removeFocusListenerForLoad();
        focusListener = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent evt) {
                if (isEditDeleteMode() && evt.getSource() == txtMaPhieuMuon)
                    loadBorrowSlipData(txtMaPhieuMuon.getText().trim());
            }
        };
        txtMaPhieuMuon.addFocusListener(focusListener);
    }

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

        txtMaNguoiDoc.setEditable(true);
        txtMaNhanVien.setEditable(true);
        txtNgayTra.setEditable(true);

        removeFocusListenerForLoad();
    }

    private void setEditDeleteMode() {
        txtMaPhieuMuon.setEditable(true);
        clearFieldsContent();
        txtNgayMuon.setText("Không đổi khi Sửa/Xóa");

        btnAdd.setEnabled(false);
        btnUpdate.setText("✅ Cập nhật");
        btnDelete.setText("❌ Xác nhận Xóa");

        JOptionPane.showMessageDialog(this, "Nhập mã phiếu mượn và nhấn Tab để tải dữ liệu.");

        setBookInputEnabled(false);

        txtMaNguoiDoc.setEditable(false);
        txtMaNhanVien.setEditable(false);
        txtNgayTra.setEditable(false);

        attachFocusListenerForLoad();
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

    // =================== DB VALIDATIONS =======================
    private boolean isNguoiDocExists(String maND) throws SQLException {
        String sql = "SELECT ma_nguoi_doc FROM nguoidoc WHERE ma_nguoi_doc = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maND);
            return ps.executeQuery().next();
        }
    }

    private boolean isNhanVienExists(String maNV) throws SQLException {
        String sql = "SELECT ma_nhan_vien FROM nhanvien WHERE ma_nhan_vien = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            return ps.executeQuery().next();
        }
    }

    private void deleteOldDetails(String maPM) {
        String sql = "DELETE FROM chitietmuon WHERE ma_phieu_muon=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPM);
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    private void loadBorrowSlipData(String maPM) {
        if (maPM.isEmpty()) return;

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT ma_nguoi_doc, ma_nhan_vien, ngay_muon, ngay_tra FROM phieumuon WHERE ma_phieu_muon=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, maPM);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                txtMaNguoiDoc.setText(rs.getString("ma_nguoi_doc"));
                txtMaNhanVien.setText(rs.getString("ma_nhan_vien"));
                txtNgayMuon.setText(rs.getDate("ngay_muon").toString());
                Date ngayTra = rs.getDate("ngay_tra");
                txtNgayTra.setText(ngayTra != null ? ngayTra.toString() : "");

                deleteOldDetails(maPM);

                txtMaNguoiDoc.setEditable(true);
                txtMaNhanVien.setEditable(true);
                txtNgayTra.setEditable(true);

                setBookInputEnabled(true);

                JOptionPane.showMessageDialog(this, "Đã tải phiếu mượn. Vui lòng thêm sách mới.");
            } else {
                clearFieldsContent();
                txtMaPhieuMuon.setText(maPM);
                txtNgayMuon.setText("Không đổi khi Sửa/Xóa");
                JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu mượn!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi truy vấn: " + ex.getMessage());
        }
    }

    // ==================== CRUD ===========================
    private void addBorrowSlip() {
        if (!btnAdd.isEnabled()) return;

        String maND = txtMaNguoiDoc.getText();
        String maNV = txtMaNhanVien.getText();

        if (maND.isEmpty() || maNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ mã!");
            return;
        }

        try {
            if (!isNguoiDocExists(maND)) {
                JOptionPane.showMessageDialog(this, "Mã người đọc không tồn tại!");
                return;
            }
            if (!isNhanVienExists(maNV)) {
                JOptionPane.showMessageDialog(this, "Mã nhân viên không tồn tại!");
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kiểm tra khóa ngoại!");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO phieumuon VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, txtMaPhieuMuon.getText());
            stmt.setString(2, maND);
            stmt.setString(3, maNV);
            stmt.setDate(4, Date.valueOf(txtNgayMuon.getText()));

            String ngayTraText = txtNgayTra.getText().trim();
            if (ngayTraText.isEmpty()) stmt.setNull(5, Types.DATE);
            else stmt.setDate(5, Date.valueOf(ngayTraText));

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Thêm phiếu mượn thành công!");
            btnAdd.setEnabled(false);
            setBookInputEnabled(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm phiếu: " + ex.getMessage());
        }
    }

    private void addBookToBorrowSlip() {
        String maPM = txtMaPhieuMuon.getText();

        if (selectedMaSach == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sách!");
            return;
        }
        if (txtNgayMuon.getText().contains("Không đổi")) {
            JOptionPane.showMessageDialog(this, "Ngày mượn không hợp lệ!");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO chitietmuon VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, maPM);
            stmt.setString(2, selectedMaSach);
            stmt.setDate(3, Date.valueOf(txtNgayMuon.getText()));
            stmt.setString(4, "Đang mượn");
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Đã thêm sách vào phiếu mượn!");
            txtSachDaChon.setText("");
            selectedMaSach = null;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm sách: " + ex.getMessage());
        }
    }

    private void updateBorrowSlip() {
        if (txtMaPhieuMuon.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập mã phiếu trước!");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE phieumuon SET ma_nguoi_doc=?, ma_nhan_vien=?, ngay_tra=? WHERE ma_phieu_muon=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, txtMaNguoiDoc.getText());
            stmt.setString(2, txtMaNhanVien.getText());

            String ngayTraText = txtNgayTra.getText().trim();
            if (ngayTraText.isEmpty()) stmt.setNull(3, Types.DATE);
            else stmt.setDate(3, Date.valueOf(ngayTraText));

            stmt.setString(4, txtMaPhieuMuon.getText());
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            setAddMode();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + ex.getMessage());
        }
    }

    private void deleteBorrowSlip() {
        if (txtMaPhieuMuon.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập mã để xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xóa phiếu này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = Database.getConnection()) {
            String sql = "DELETE FROM phieumuon WHERE ma_phieu_muon=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, txtMaPhieuMuon.getText());
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Đã xóa phiếu mượn!");
            setAddMode();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa: " + ex.getMessage());
        }
    }

    private void generateBorrowSlipID() {
        int nextID = 1;
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT MAX(CAST(SUBSTRING(ma_phieu_muon, 3) AS UNSIGNED)) FROM phieumuon";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            if (rs.next()) nextID = rs.getInt(1) + 1;
        } catch (SQLException ignored) {}
        txtMaPhieuMuon.setText("PM" + String.format("%03d", nextID));
    }
}
