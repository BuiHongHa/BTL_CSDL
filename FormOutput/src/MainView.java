package view;

import com.formdev.flatlaf.FlatIntelliJLaf;
import view.output.MainForm;
import view.input.MainMenuStaff;
import view.input.MainMenuReader;
import view.input.LoginForm;
import model.Session;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {

    private JButton btnInput;
    private JButton btnReport;
    private JButton btnLogout;

    public MainView() {
        initUI();
    }

    private void initUI() {
        setTitle("HỆ THỐNG QUẢN LÝ THƯ VIỆN PTIT");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);

        // ===== NÚT =====
        btnInput = new JButton("NHẬP DỮ LIỆU");
        btnReport = new JButton("XUẤT BÁO CÁO - THỐNG KÊ");
        btnLogout = new JButton("ĐĂNG XUẤT");

        // Style cho nút đăng xuất
        btnLogout.setBackground(new Color(255, 92, 92));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // ===== SỰ KIỆN =====
        btnInput.addActionListener(e -> {
            if ("Quản lý".equals(Session.role)) {
                new MainMenuStaff().setVisible(true);
                dispose();
            } else if ("Người đọc".equals(Session.role)) {
                new MainMenuReader().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Bạn chưa đăng nhập!");
            }
        });

        btnReport.addActionListener(e -> new MainForm().setVisible(true));

        btnLogout.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn đăng xuất?",
                    "Đăng xuất",
                    JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                Session.role = null;
                Session.ma = null;
                dispose();
                new LoginForm().setVisible(true);
            }
        });

        // ===== LAYOUT =====
        setLayout(new GridLayout(3, 1, 20, 20));
        add(btnInput);
        add(btnReport);
        add(btnLogout);
    }

    public static void main(String[] args) {
        FlatIntelliJLaf.setup();
        JOptionPane.showMessageDialog(null, "Không chạy MainView trực tiếp!\nHãy chạy LoginForm trước!");
    }
}
