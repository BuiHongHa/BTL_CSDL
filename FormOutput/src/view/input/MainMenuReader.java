package view.input;

import model.Session;
import javax.swing.*;
import java.awt.*;

public class MainMenuReader extends JFrame {

    public MainMenuReader() {
        setTitle("MENU NGƯỜI ĐỌC");
        setSize(650, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(32, 136, 203));
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ THƯ VIỆN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Xin chào, " + Session.ma, SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSub.setForeground(Color.WHITE);

        header.add(lblTitle, BorderLayout.NORTH);
        header.add(lblSub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // ===== MENU BUTTONS =====
        JPanel panel = new JPanel(new GridLayout(3, 1, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));
        panel.setBackground(Color.WHITE);

        JButton btnPM = menuButton("📚  Lịch sử mượn");
        JButton btnPP = menuButton("⚠️  Phiếu phạt");
        JButton btnExit = menuButton("⬅️  Thoát");

        // ==== SỰ KIỆN ====
        btnPM.addActionListener(e -> new view.output.PhieuMuonForm(Session.ma).setVisible(true));

        btnPP.addActionListener(e -> {
            dao.PhieuPhatDao dao = new dao.PhieuPhatDao();
            try {
                java.util.List<model.PhieuPhat> list = dao.getByNguoiDoc(Session.ma);
                if (list.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "🎉 Bạn không có phiếu phạt nào!");
                } else {
                    new view.output.PhieuPhatForm(Session.ma).setVisible(true);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi kiểm tra phiếu phạt: " + ex.getMessage());
            }
        });

        btnExit.addActionListener(e -> {
            dispose();
            new view.input.LoginForm().setVisible(true);
        });

        panel.add(btnPM);
        panel.add(btnPP);
        panel.add(btnExit);

        add(panel, BorderLayout.CENTER);

        // ===== FOOTER =====
        JLabel footer = new JLabel("© 2025 Hệ Thống Quản Lý Thư Viện - Phiên bản 1.0", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footer.setForeground(new Color(120, 120, 120));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(footer, BorderLayout.SOUTH);
    }

    private JButton menuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 190, 190), 2),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
