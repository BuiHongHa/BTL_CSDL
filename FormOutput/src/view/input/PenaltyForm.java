package view.input;

import util.Database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PenaltyForm extends JFrame {

    private JTextField txtMaPhieuPhat, txtMaPhieuMuon, txtMaNguoiDoc, txtSoTienPhat, txtNgayLap;
    private JTextArea txtLyDo;
    private JComboBox<LoanDetail> cmbMaSach;
    private JButton btnThem, btnSua, btnXoa, btnXem, btnLamMoi;

    private FocusAdapter focusListener;

    static class LoanDetail {
        String maNguoiDoc, maSach, tenSach, tinhTrangSach;
        public LoanDetail(String maNguoiDoc, String maSach, String tenSach, String tinhTrangSach) {
            this.maNguoiDoc = maNguoiDoc;
            this.maSach = maSach;
            this.tenSach = tenSach;
            this.tinhTrangSach = tinhTrangSach;
        }
        @Override
        public String toString() {
            return maSach + " - " + tenSach + " (TT: " + tinhTrangSach + ")";
        }
    }

    static class PenaltyData {
        String maPhieuMuon, maSach, maNguoiDoc, lyDoPhat;
        double soTienPhat;
        LocalDateTime ngayLap;
        public PenaltyData(String maPhieuMuon, String maSach, String maNguoiDoc, String lyDoPhat, double soTienPhat, LocalDateTime ngayLap) {
            this.maPhieuMuon = maPhieuMuon;
            this.maSach = maSach;
            this.maNguoiDoc = maNguoiDoc;
            this.lyDoPhat = lyDoPhat;
            this.soTienPhat = soTienPhat;
            this.ngayLap = ngayLap;
        }
    }

    public PenaltyForm() {
        setTitle("📘 Quản lý Phiếu Phạt");
        setSize(500, 550);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("TẠO PHIẾU PHẠT", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 70, 160));
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin phiếu phạt"));

        txtMaPhieuPhat = new JTextField();
        txtMaPhieuMuon = new JTextField();
        cmbMaSach = new JComboBox<>();
        txtMaNguoiDoc = new JTextField();
        txtMaNguoiDoc.setEditable(false);
        txtLyDo = new JTextArea(3, 20);
        txtSoTienPhat = new JTextField();
        txtNgayLap = new JTextField();
        txtNgayLap.setEditable(false);

        formPanel.add(new JLabel("Mã phiếu phạt:")); formPanel.add(txtMaPhieuPhat);
        formPanel.add(new JLabel("Mã phiếu mượn:")); formPanel.add(txtMaPhieuMuon);
        formPanel.add(new JLabel("Mã sách:")); formPanel.add(cmbMaSach);
        formPanel.add(new JLabel("Mã người đọc:")); formPanel.add(txtMaNguoiDoc);
        formPanel.add(new JLabel("Lý do phạt:")); formPanel.add(new JScrollPane(txtLyDo));
        formPanel.add(new JLabel("Số tiền phạt:")); formPanel.add(txtSoTienPhat);
        formPanel.add(new JLabel("Ngày lập:")); formPanel.add(txtNgayLap);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnThem = new JButton("➕ Thêm");
        btnSua = new JButton("✏️ Sửa");
        btnXoa = new JButton("🗑️ Xóa");
        btnXem = new JButton("📄 Xem DS");
        btnLamMoi = new JButton("🔄 Làm mới");

        for (JButton btn : new JButton[]{btnThem, btnSua, btnXoa, btnXem, btnLamMoi}) {
            btn.setFocusPainted(false);
            btn.setBackground(new Color(220, 235, 250));
            btn.setBorder(BorderFactory.createLineBorder(new Color(0, 70, 160)));
            buttonPanel.add(btn);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        // Sự kiện
        txtMaPhieuMuon.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!isEditDeleteMode()) loadLoanDetails(txtMaPhieuMuon.getText().trim());
            }
        });

        btnThem.addActionListener(e -> themPhieuPhat());
        btnLamMoi.addActionListener(e -> setAddMode());
        btnSua.addActionListener(e -> { if (isEditDeleteMode()) suaPhieuPhat(); else setEditDeleteMode(); });
        btnXoa.addActionListener(e -> { if (isEditDeleteMode()) xoaPhieuPhat(); else setEditDeleteMode(); });
        btnXem.addActionListener(e -> JOptionPane.showMessageDialog(this, "Danh sách sẽ có ở form khác!"));

        setAddMode();
    }

    private boolean isEditDeleteMode() { return !btnThem.isEnabled(); }

    private void setAddMode() {
        txtMaPhieuPhat.setEditable(false);
        txtMaPhieuMuon.setEditable(true);
        cmbMaSach.setEnabled(true);
        txtLyDo.setEditable(true);
        txtSoTienPhat.setEditable(true);

        clearFields();
        generatePenaltyID();

        txtNgayLap.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        btnThem.setEnabled(true);
        btnSua.setText("✏️ Sửa");
        btnXoa.setText("🗑️ Xóa");
    }

    private void setEditDeleteMode() {
        txtMaPhieuPhat.setEditable(true);
        txtMaPhieuMuon.setEditable(false);
        cmbMaSach.setEnabled(false);
        txtLyDo.setEditable(false);
        txtSoTienPhat.setEditable(false);

        clearFields();
        btnThem.setEnabled(false);
        btnSua.setText("✅ Cập nhật");
        btnXoa.setText("❌ Xóa ngay");
        attachFocusListenerForLoad();
        JOptionPane.showMessageDialog(this, "Nhập Mã phiếu phạt và nhấn TAB để tải dữ liệu!");
    }

    private void clearFields() {
        txtMaPhieuPhat.setText("");
        txtMaPhieuMuon.setText("");
        txtMaNguoiDoc.setText("");
        txtLyDo.setText("");
        txtSoTienPhat.setText("");
        txtNgayLap.setText("");
        cmbMaSach.removeAllItems();
    }

    private void attachFocusListenerForLoad() {
        focusListener = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) { loadPenaltyData(txtMaPhieuPhat.getText().trim()); }
        };
        txtMaPhieuPhat.addFocusListener(focusListener);
    }

    private void loadLoanDetails(String maPM) {
        cmbMaSach.removeAllItems();
        txtMaNguoiDoc.setText("");

        List<LoanDetail> list = getLoanDetailsFromDB(maPM);
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phiếu mượn không tồn tại hoặc không có sách!");
            return;
        }

        txtMaNguoiDoc.setText(list.get(0).maNguoiDoc);
        for (LoanDetail d : list) cmbMaSach.addItem(d);
    }

    private List<LoanDetail> getLoanDetailsFromDB(String maPM) {
        List<LoanDetail> list = new ArrayList<>();
        String sql = """
                SELECT pm.ma_nguoi_doc, ctm.ma_sach, s.ten_sach, ctm.tinh_trang_sach
                FROM phieumuon pm
                JOIN chitietmuon ctm ON pm.ma_phieu_muon = ctm.ma_phieu_muon
                JOIN sach s ON ctm.ma_sach = s.ma_sach WHERE pm.ma_phieu_muon = ?
                """;
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maPM);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new LoanDetail(
                    rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)
            ));
        } catch (Exception ignored) { }
        return list;
    }

    private void loadPenaltyData(String maPP) {
        if (maPP.isEmpty()) return;
        String sql = "SELECT * FROM phieuphat WHERE ma_phieu_phat=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, maPP);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) { JOptionPane.showMessageDialog(this, "Không tìm thấy!"); return; }

            txtMaPhieuMuon.setText(rs.getString("ma_phieu_muon"));
            txtMaNguoiDoc.setText(rs.getString("ma_nguoi_doc"));
            txtLyDo.setText(rs.getString("ly_do_phat"));
            txtSoTienPhat.setText(rs.getString("so_tien_phat"));
            txtNgayLap.setText(rs.getString("ngay_lap"));
            loadLoanDetails(rs.getString("ma_phieu_muon"));
        } catch (Exception ignored) { }
    }

    private void updateCTM(String maSach, String maPM, String lyDo) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE chitietmuon SET tinh_trang_sach=? WHERE ma_sach=? AND ma_phieu_muon=?")) {
            ps.setString(1, lyDo);
            ps.setString(2, maSach);
            ps.setString(3, maPM);
            ps.executeUpdate();
        } catch (Exception ignored) { }
    }

    private void themPhieuPhat() {
        if (cmbMaSach.getSelectedItem() == null) { JOptionPane.showMessageDialog(this, "Chọn sách!"); return; }
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO phieuphat VALUES (?, ?, ?, ?, ?, ?, NOW())")) {
            LoanDetail d = (LoanDetail) cmbMaSach.getSelectedItem();
            ps.setString(1, txtMaPhieuPhat.getText());
            ps.setString(2, txtMaPhieuMuon.getText());
            ps.setString(3, d.maSach);
            ps.setString(4, txtMaNguoiDoc.getText());
            ps.setString(5, txtLyDo.getText());
            ps.setDouble(6, Double.parseDouble(txtSoTienPhat.getText()));
            ps.executeUpdate();
            updateCTM(d.maSach, txtMaPhieuMuon.getText(), txtLyDo.getText());
            JOptionPane.showMessageDialog(this, "Thêm và cập nhật tình trạng sách thành công!");
            setAddMode();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex); }
    }

    private void suaPhieuPhat() {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement("""
                UPDATE phieuphat SET ma_phieu_muon=?, ma_sach=?, ma_nguoi_doc=?, ly_do_phat=?, so_tien_phat=? WHERE ma_phieu_phat=?
                """)) {
            LoanDetail d = (LoanDetail) cmbMaSach.getSelectedItem();
            ps.setString(1, txtMaPhieuMuon.getText());
            ps.setString(2, d.maSach);
            ps.setString(3, txtMaNguoiDoc.getText());
            ps.setString(4, txtLyDo.getText());
            ps.setDouble(5, Double.parseDouble(txtSoTienPhat.getText()));
            ps.setString(6, txtMaPhieuPhat.getText());
            ps.executeUpdate();
            updateCTM(d.maSach, txtMaPhieuMuon.getText(), txtLyDo.getText());
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            setAddMode();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex); }
    }

    private void xoaPhieuPhat() {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM phieuphat WHERE ma_phieu_phat=?")) {
            ps.setString(1, txtMaPhieuPhat.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Đã xóa!");
            setAddMode();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex); }
    }

    private void generatePenaltyID() {
        int next = 1;
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT ma_phieu_phat FROM phieuphat")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) next++;
        } catch (Exception ignored) { }
        txtMaPhieuPhat.setText("PP" + String.format("%03d", next));
    }
}
