package SQL;


import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ReaderAccountForm extends JFrame {
    private JTextField txtMa, txtHoTen, txtDonVi, txtDiaChi, txtSDT;

    public ReaderAccountForm() {
        setTitle("Tạo tài khoản người đọc");
        setSize(520, 380);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel header = new JLabel("TẠO TÀI KHOẢN NGƯỜI ĐỌC", SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(0,102,204));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(header, BorderLayout.NORTH);

        JPanel p = new JPanel(new GridLayout(6,1,6,6));
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        p.setBackground(new Color(240,244,247));

        p.add(new JLabel("Mã người đọc:"));
        txtMa = new JTextField(); p.add(txtMa);

        p.add(new JLabel("Họ tên:"));
        txtHoTen = new JTextField(); p.add(txtHoTen);

        p.add(new JLabel("Đơn vị (Lớp / Bộ môn):"));
        txtDonVi = new JTextField(); p.add(txtDonVi);

        p.add(new JLabel("Địa chỉ:"));
        txtDiaChi = new JTextField(); p.add(txtDiaChi);

        p.add(new JLabel("Số điện thoại:"));
        txtSDT = new JTextField(); p.add(txtSDT);

        add(p, BorderLayout.CENTER);

        JButton btnSave = new JButton("Tạo tài khoản");
        btnSave.setBackground(new Color(0,102,204)); btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> saveReader());
        add(btnSave, BorderLayout.SOUTH);
    }

    private void saveReader() {
        String ma = txtMa.getText().trim();
        if (ma.isEmpty() || txtHoTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã người đọc và Họ tên là bắt buộc.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO nguoidoc (ma_nguoi_doc, ho_ten, don_vi, dia_chi, so_dien_thoai) VALUES (?,?,?,?,?)")) {

            ps.setString(1, ma);
            ps.setString(2, txtHoTen.getText().trim());
            ps.setString(3, txtDonVi.getText().trim());
            ps.setString(4, txtDiaChi.getText().trim());
            ps.setString(5, txtSDT.getText().trim());

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Tạo tài khoản thành công.");
            this.dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tạo tài khoản: " + ex.getMessage());
        }
    }
}
