package library.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;

public class ReportView extends JFrame {

    public ReportView(String title, DefaultTableModel model, String footer, String creator) {

        setTitle("Bản Xem Trước Báo Cáo");
        setSize(950, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===================== CONTENT PANEL =====================
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        add(contentPanel, BorderLayout.CENTER);

        // ===========================================================
        // ---------------------- HEADER 2 CỘT ------------------------
        // ===========================================================
        JPanel headerPanel = new JPanel(new BorderLayout());

        // --- Left: Thư viện + địa chỉ ---
        JPanel leftHeader = new JPanel();
        leftHeader.setLayout(new BoxLayout(leftHeader, BoxLayout.Y_AXIS));
        leftHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblLibrary = new JLabel("THƯ VIỆN PTIT");
        lblLibrary.setFont(new Font("Serif", Font.BOLD, 20));

        JLabel lblAddress = new JLabel("123 Đường Trần Phú, Hà Đông, Hà Nội");
        lblAddress.setFont(new Font("Serif", Font.PLAIN, 14));

        leftHeader.add(lblLibrary);
        leftHeader.add(lblAddress);

        // --- Right: Ngày lập ---
        String today = new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
        JLabel lblDateTop = new JLabel("Ngày lập: " + today);
        lblDateTop.setFont(new Font("Serif", Font.PLAIN, 14));
        lblDateTop.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel rightHeader = new JPanel(new BorderLayout());
        rightHeader.add(lblDateTop, BorderLayout.NORTH);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        contentPanel.add(headerPanel);

        // ===========================================================
        // -------------------- TIÊU ĐỀ BÁO CÁO -----------------------
        // ===========================================================
        contentPanel.add(Box.createVerticalStrut(20));

        JLabel lblTitle = new JLabel("BÁO CÁO", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Serif", Font.BOLD, 30));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(lblTitle);
        contentPanel.add(Box.createVerticalStrut(25));  // cách 1 khoảng xuống bảng

        // ===========================================================
        // ------------------------ BẢNG DỮ LIỆU ----------------------
        // ===========================================================
        JTable table = new JTable(model);
        table.setEnabled(false);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(850, 350));
        contentPanel.add(scroll);

        // ===========================================================
// --------------------------- FOOTER -------------------------
// ===========================================================

        JPanel footerOuter = new JPanel(new BorderLayout());
        footerOuter.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40)); // căn lề đẹp hơn

        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

// căn phải tuyệt đối bằng BorderLayout.EAST
        footerOuter.add(footerPanel, BorderLayout.EAST);

        JLabel lblReporter = new JLabel("Người báo cáo", SwingConstants.RIGHT);
        lblReporter.setFont(new Font("Serif", Font.BOLD, 15));
        lblReporter.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel lblSign = new JLabel("(Ký và ghi rõ họ tên)", SwingConstants.RIGHT);
        lblSign.setFont(new Font("Serif", Font.ITALIC, 13));
        lblSign.setAlignmentX(Component.RIGHT_ALIGNMENT);

        footerPanel.add(lblReporter);
        footerPanel.add(lblSign);

        contentPanel.add(Box.createVerticalStrut(35)); // cách bảng 1 khoảng
        contentPanel.add(footerOuter);

        // ===========================================================
        // -------------------------- BUTTON --------------------------
        // ===========================================================
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnPrint = new JButton("In ra giấy");
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());

        bottomPanel.add(btnPrint);
        bottomPanel.add(btnClose);

        add(bottomPanel, BorderLayout.SOUTH);
    }
}
