package view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainForm extends JFrame {

    // Màu chủ đạo (Xanh dương PTIT)
    private final Color PRIMARY_COLOR = new Color(32, 136, 203);
    private final Color BG_COLOR = new Color(245, 245, 245);

    public MainForm() {
        initDatabaseDriver();
        initUI();
    }

    private void initDatabaseDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded successfully.");
        } catch (Exception e) {
            // Không hiện lỗi ngay lúc mở app để tránh phiền,
            // lỗi sẽ hiện khi bấm vào các nút chức năng nếu chưa kết nối được.
            e.printStackTrace();
        }
    }

    private void initUI() {
        setTitle("Hệ Thống Quản Lý Thư Viện PTIT");
        setSize(1000, 650); // Tăng kích thước một chút
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. HEADER
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. MENU DASHBOARD
        add(createDashboardPanel(), BorderLayout.CENTER);

        // 3. FOOTER
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    // --- HEADER ---
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ THƯ VIỆN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Học Viện Công Nghệ Bưu Chính Viễn Thông - PTIT", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSubtitle.setForeground(new Color(230, 230, 230));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle);
        textPanel.add(lblSubtitle);

        header.add(textPanel, BorderLayout.CENTER);
        return header;
    }

    // --- DASHBOARD (GRID BUTTONS) ---
    private JPanel createDashboardPanel() {
        // Sử dụng GridLayout: 3 hàng, 2 cột, khoảng cách (gap) 20px
        JPanel panel = new JPanel(new GridLayout(3, 2, 20, 20));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(30, 50, 30, 50)); // Căn lề 4 phía

        // Thêm các nút chức năng
        panel.add(createMenuButton("QUẢN LÝ NGƯỜI ĐỌC", "📘", e -> new NguoiDocForm().setVisible(true)));
        panel.add(createMenuButton("QUẢN LÝ SÁCH", "📚", e -> new SachForm().setVisible(true)));

        panel.add(createMenuButton("QUẢN LÝ PHIẾU MƯỢN", "📄", e -> new PhieuMuonForm().setVisible(true)));
        panel.add(createMenuButton("QUẢN LÝ PHIẾU PHẠT", "⚠", e -> new PhieuPhatForm().setVisible(true)));

        panel.add(createMenuButton("TRA CỨU CHECK-IN", "🔍", e -> new CheckinForm().setVisible(true)));
        panel.add(createMenuButton("QUẢN LÝ NHÂN VIÊN", "👨‍💼", e -> new NhanVienForm().setVisible(true)));

        return panel;
    }

    // --- HÀM TẠO NÚT BẤM (ĐÃ SỬA HTML) ---
    private JButton createMenuButton(String text, String iconEmoji, ActionListener action) {
        // SỬA LỖI Ở ĐÂY:
        // 1. Giảm khoảng cách icon: Dùng 1 thẻ <br> và style margin
        // 2. Tăng font icon: 32px -> 40px cho cân đối
        // 3. Font chữ: In đậm (font-weight:bold) và kích thước 14px
        String html = "<html>"
                + "<center>"
                + "<span style='font-size: 40px;'>" + iconEmoji + "</span>"
                + "<br>" // Xuống dòng 1 lần thôi
                + "<span style='font-size: 14px; font-weight: bold; color: #333;'>" + text + "</span>"
                + "</center>"
                + "</html>";

        JButton btn = new JButton(html);
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(50, 50, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Thêm con trỏ tay khi di chuột

        // Style FlatLaf: Bo tròn góc (arc: 20) + Viền mỏng
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 20; borderWidth: 1; borderColor: #dddddd; background: #ffffff");

        btn.addActionListener(action);

        // Hiệu ứng hover đơn giản
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Khi di chuột vào: Nền xanh rất nhạt, viền xanh dương
                btn.setBackground(new Color(235, 248, 255));
                // Cập nhật lại style viền màu xanh
                btn.putClientProperty(FlatClientProperties.STYLE, "arc: 20; borderWidth: 2; borderColor: #2088cb; background: #ebf8ff");
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Khi chuột rời ra: Trở về màu trắng
                btn.setBackground(Color.WHITE);
                btn.putClientProperty(FlatClientProperties.STYLE, "arc: 20; borderWidth: 1; borderColor: #dddddd; background: #ffffff");
            }
        });

        return btn;
    }

    // --- FOOTER ---
    private JPanel createFooterPanel() {
        JPanel footer = new JPanel();
        footer.setBackground(BG_COLOR);
        footer.setBorder(new EmptyBorder(10, 0, 15, 0));

        JLabel lblFooter = new JLabel("© 2025 Hệ Thống Quản Lý Thư Viện - Phiên bản 1.0");
        lblFooter.setForeground(Color.GRAY);
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        footer.add(lblFooter);
        return footer;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            // Set font mặc định toàn app to hơn một chút cho dễ đọc
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new MainForm().setVisible(true));
    }
}