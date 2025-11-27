package view.components;

import javax.swing.*;
import java.awt.*;

public abstract class MenuTemplate extends JFrame {

    protected JPanel contentPanel;
    protected JLabel title;

    public MenuTemplate(String headerText) {
        setTitle("HỆ THỐNG QUẢN LÝ THƯ VIỆN PTIT");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(32, 136, 203));
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        title = new JLabel(headerText, SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ===== CENTER =====
        contentPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        add(contentPanel, BorderLayout.CENTER);

        // ===== FOOTER (BACK) =====
        JButton back = new JButton("⬅ Quay lại");
        back.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        back.setFocusPainted(false);
        back.setBackground(new Color(240, 244, 255));
        back.setBorder(BorderFactory.createLineBorder(new Color(32, 136, 203)));
        back.addActionListener(e -> {
            dispose();
            new view.MainView().setVisible(true);
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(Color.WHITE);
        footer.add(back);
        add(footer, BorderLayout.SOUTH);
    }

    // ===== STYLE BUTTON =====
    protected JButton createMenuButton(String text, Icon icon) {
        JButton btn = new JButton(text, icon);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(250, 250, 250));
        btn.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
