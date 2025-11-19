package SQL;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginForm extends JFrame {
    private JTextField txtMa;
    private JComboBox<String> cbLoai;

    public LoginForm() {
        setTitle("Đăng nhập hệ thống thư viện");
        setSize(420, 260);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(0, 102, 204));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(header, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        panel.setBackground(new Color(240, 244, 247));

        panel.add(new JLabel("Mã đăng nhập:"));
        txtMa = new JTextField();
        panel.add(txtMa);

        panel.add(new JLabel("Loại tài khoản:"));
        cbLoai = new JComboBox<>(new String[]{"Quản lý", "Người đọc"});
        panel.add(cbLoai);

        add(panel, BorderLayout.CENTER);

        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.setBackground(new Color(0, 102, 204));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.addActionListener(this::loginAction);

        add(btnLogin, BorderLayout.SOUTH);
    }

    private void loginAction(ActionEvent e) {
        String ma = txtMa.getText().trim();
        String loai = cbLoai.getSelectedItem().toString();

        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã!");
            return;
        }

        if (loai.equals("Quản lý")) {
            if (kiemTraNhanVien(ma)) {
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
                new MainMenuStaff().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Sai mã nhân viên!");
            }
        } else {
            if (kiemTraNguoiDoc(ma)) {
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
                new MainMenuReader().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Mã người đọc không tồn tại!");
            }
        }
    }

    private boolean kiemTraNhanVien(String ma) {
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT * FROM nhanvien WHERE ma_nhan_vien = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ma);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean kiemTraNguoiDoc(String ma) {
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT * FROM nguoidoc WHERE ma_nguoi_doc = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ma);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
