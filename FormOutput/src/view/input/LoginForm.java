package view.input;

import dao.LoginDAO;
import dao.PhieuPhatDao;
import model.Session;
import view.MainView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

public class LoginForm extends JFrame {
    private JTextField txtMa;
    private JComboBox<String> cbLoai;

    public LoginForm() {
        setTitle("Đăng nhập hệ thống thư viện");
        setSize(420, 260);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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

        // ================= QUẢN LÝ =================
        if (loai.equals("Quản lý")) {
            if (LoginDAO.loginNhanVien(ma)) {
                Session.role = "Quản lý";
                Session.ma = ma;
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
                new MainView().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Sai mã nhân viên!");
            }
        }

        // ================= NGƯỜI ĐỌC =================
        else { // Người đọc
            if (LoginDAO.loginNguoiDoc(ma)) {
                Session.role = "Người đọc";
                Session.ma = ma;

                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");

                // 👉 Mở menu Người đọc
                new view.input.MainMenuReader().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Mã người đọc không tồn tại!");
            }
        }
    }
}
