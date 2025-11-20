package view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import dao.PhieuPhatDao;
import model.PhieuPhat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.print.PrinterException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PhieuPhatForm extends JFrame {

    private JTextField txtSearch, txtFrom, txtTo;
    private JTable table;
    private DefaultTableModel model;
    private PhieuPhatDao dao;

    // Currency Formatter
    private final DecimalFormat df = new DecimalFormat("#,###");
    // Primary Color (consistent with other forms)
    private final Color PRIMARY_COLOR = new Color(32, 136, 203);

    public PhieuPhatForm() {
        dao = new PhieuPhatDao();
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("Quản Lý Phiếu Phạt");
        setSize(1100, 650);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. HEADER
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. MAIN CONTENT
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(createFilterPanel(), BorderLayout.NORTH);
        contentPanel.add(createTablePanel(), BorderLayout.CENTER);
        contentPanel.add(createBottomPanel(), BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
    }

    // --- 1. HEADER ---
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("TRA CỨU PHIẾU PHẠT", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Quản lý các khoản phạt do vi phạm quy định mượn trả", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(230, 230, 230));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle);
        textPanel.add(lblSubtitle);
        header.add(textPanel, BorderLayout.CENTER);
        return header;
    }

    // --- 2. FILTER PANEL ---
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(" Bộ lọc tìm kiếm "));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Search Keyword
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Từ khóa:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mã phiếu, tên người đọc, SĐT...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        panel.add(txtSearch, gbc);

        // Search Button
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(PRIMARY_COLOR);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.0;
        panel.add(btnSearch, gbc);

        // Row 2: Date Filter
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Ngày lập:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        JPanel datePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        datePanel.setOpaque(false);

        txtFrom = new JTextField();
        txtFrom.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Từ ngày (YYYY-MM-DD)");

        txtTo = new JTextField();
        txtTo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Đến ngày (YYYY-MM-DD)");

        datePanel.add(txtFrom);
        datePanel.add(txtTo);
        panel.add(datePanel, gbc);

        // Reload Button
        JButton btnReload = new JButton("Làm mới");
        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(btnReload, gbc);

        // Events
        btnSearch.addActionListener(e -> searchData());
        btnReload.addActionListener(e -> {
            txtSearch.setText("");
            txtFrom.setText("");
            txtTo.setText("");
            loadData();
        });

        return panel;
    }

    // --- 3. TABLE PANEL ---
    private JScrollPane createTablePanel() {
        model = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        model.addColumn("Mã PP");
        model.addColumn("Tên Người Đọc");
        model.addColumn("SĐT");
        model.addColumn("Nhân Viên");
        model.addColumn("Ngày Lập");
        model.addColumn("Ngày Mượn");
        model.addColumn("Ngày Trả");
        model.addColumn("Tiền Phạt");
        model.addColumn("Lý Do");

        table = new JTable(model);
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Header Style
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(240, 240, 240));
        header.setPreferredSize(new Dimension(0, 35));

        // Alignments
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã PP
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Ngày Lập

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(7).setCellRenderer(rightRenderer); // Tiền Phạt

        return new JScrollPane(table);
    }

    // --- 4. BOTTOM PANEL ---
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);

        JButton btnInvoice = new JButton(" Xem Hóa Đơn Phạt ");
        btnInvoice.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnInvoice.setBackground(new Color(46, 204, 113)); // Green
        btnInvoice.setForeground(Color.WHITE);
        btnInvoice.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);

        btnInvoice.addActionListener(e -> showInvoice());
        panel.add(btnInvoice);

        return panel;
    }

    // ================= LOGIC =================

    private void loadData() {
        model.setRowCount(0);
        try {
            List<PhieuPhat> list = dao.getAll();
            for (PhieuPhat p : list) addRow(p);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    private void searchData() {
        model.setRowCount(0);
        try {
            List<PhieuPhat> list = dao.search(
                    txtSearch.getText().trim(),
                    txtFrom.getText().trim(),
                    txtTo.getText().trim());
            for (PhieuPhat p : list) addRow(p);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi tìm kiếm: " + e.getMessage());
        }
    }

    private void addRow(PhieuPhat p) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat dateOnly = new SimpleDateFormat("dd/MM/yyyy");

        String ngayLapStr = (p.getNgayLap() != null) ? sdf.format(p.getNgayLap()) : "";
        String ngayMuonStr = (p.getNgayMuon() != null) ? dateOnly.format(p.getNgayMuon()) : "";
        String ngayTraStr = (p.getNgayTra() != null) ? dateOnly.format(p.getNgayTra()) : "";

        model.addRow(new Object[]{
                p.getMaPhieuPhat(),
                p.getTenNguoiDoc(),
                p.getSdtNguoiDoc(),
                p.getTenNhanVien(),
                ngayLapStr,
                ngayMuonStr,
                ngayTraStr,
                df.format(p.getTienPhat()) + " đ",
                p.getLyDo()
        });
    }

    // ================= INVOICE PREVIEW =================

    private void showInvoice() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "⚠ Vui lòng chọn phiếu để xem hóa đơn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maPP = model.getValueAt(row, 0).toString();
        try {
            PhieuPhat p = dao.getById(maPP);
            if(p != null) showInvoiceDialog(p);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi tải hóa đơn: " + e.getMessage());
        }
    }

    // =======================================================
    // HÀM HIỂN THỊ HÓA ĐƠN (Đã tăng cỡ chữ Title)
    // =======================================================
    private void showInvoiceDialog(PhieuPhat p) {
        SimpleDateFormat fullDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");

        String datePrint = fullDate.format(new Date());
        String ngayLapStr = (p.getNgayLap() != null) ? fullDate.format(p.getNgayLap()) : "N/A";
        String ngayMuonStr = (p.getNgayMuon() != null) ? simpleDate.format(p.getNgayMuon()) : "N/A";
        String ngayTraStr = (p.getNgayTra() != null) ? simpleDate.format(p.getNgayTra()) : "N/A";

        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Sans-serif; padding: 20px;'>");

        // Header
        html.append("<div style='text-align:center;'>");
        html.append("<h3 style='margin:0; color:#555; font-size: 14px;'>THƯ VIỆN HỌC VIỆN CÔNG NGHỆ BƯU CHÍNH VIỄN THÔNG</h3>");

        // --- SỬA Ở ĐÂY: Tăng kích thước font-size lên 30px ---
        html.append("<h1 style='color:#c0392b; margin-top:15px; font-size: 30px; font-weight: bold;'>HÓA ĐƠN PHIẾU PHẠT</h1>");
        // -----------------------------------------------------

        html.append("<p style='font-size:12px; color:#777;'>Ngày in: ").append(datePrint).append("</p>");
        html.append("</div><hr style='border: 0.5px solid #ccc; margin-bottom: 20px;'/>");

        // Content Table
        html.append("<table style='width:100%; font-size:14px; margin-top:15px; border-collapse: collapse;'>");

        html.append("<tr><td style='padding:8px; width:40%;'><b>Mã phiếu phạt:</b></td><td style='padding:8px;'>").append(p.getMaPhieuPhat()).append("</td></tr>");
        html.append("<tr><td style='padding:8px;'><b>Ngày lập phiếu:</b></td><td style='padding:8px;'>").append(ngayLapStr).append("</td></tr>");
        html.append("<tr><td colspan='2'><hr style='border:0.5px dashed #ddd; margin: 10px 0;'/></td></tr>");

        html.append("<tr><td style='padding:8px;'><b>Người nộp:</b></td><td style='padding:8px;'>").append(p.getTenNguoiDoc()).append("</td></tr>");
        html.append("<tr><td style='padding:8px;'><b>Số điện thoại:</b></td><td style='padding:8px;'>").append(p.getSdtNguoiDoc()).append("</td></tr>");
        html.append("<tr><td style='padding:8px;'><b>Nhân viên thu:</b></td><td style='padding:8px;'>").append(p.getTenNhanVien()).append("</td></tr>");
        html.append("<tr><td colspan='2'><hr style='border:0.5px dashed #ddd; margin: 10px 0;'/></td></tr>");

        html.append("<tr><td style='padding:8px;'><b>Ngày mượn sách:</b></td><td style='padding:8px;'>").append(ngayMuonStr).append("</td></tr>");
        html.append("<tr><td style='padding:8px;'><b>Ngày trả sách:</b></td><td style='padding:8px;'>").append(ngayTraStr).append("</td></tr>");
        html.append("<tr><td style='padding:8px;'><b>Lý do phạt:</b></td><td style='padding:8px; color:#c0392b; font-weight:bold;'>").append(p.getLyDo()).append("</td></tr>");

        html.append("</table>");

        // Total Amount
        html.append("<div style='margin-top:30px; text-align:right; font-size:18px;'>");
        html.append("<b>TỔNG TIỀN PHẠT: </b><span style='color:#c0392b; font-size:24px; font-weight:bold;'>")
                .append(df.format(p.getTienPhat())).append(" VNĐ</span>");
        html.append("</div>");

        // Footer
        html.append("<div style='margin-top:50px; text-align:center; font-size:13px;'>");
        html.append("<table width='100%'><tr>");
        html.append("<td align='center'><i>(Ký và ghi rõ họ tên)</i><br/><br/><br/><br/><b>Người nộp tiền</b></td>");
        html.append("<td align='center'><i>(Ký và ghi rõ họ tên)</i><br/><br/><br/><br/><b>Người thu tiền</b></td>");
        html.append("</tr></table>");
        html.append("</div>");

        html.append("</body></html>");

        // Display Dialog
        JEditorPane pane = new JEditorPane("text/html", html.toString());
        pane.setEditable(false);
        pane.setCaretPosition(0);

        JDialog dialog = new JDialog(this, "Hóa Đơn Phiếu Phạt", true);
        dialog.setSize(650, 780); // Tăng chiều cao một chút để chứa font to
        dialog.setLocationRelativeTo(this);

        JButton btnPrint = new JButton("🖨️ In Hóa Đơn");
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPrint.setBackground(new Color(46, 204, 113));
        btnPrint.setForeground(Color.WHITE);

        // Fix lỗi nút bấm FlatLaf nếu cần
        if (btnPrint.getUI().getClass().getName().contains("FlatButtonUI")) {
            btnPrint.putClientProperty("JButton.buttonType", "roundRect");
        }

        btnPrint.addActionListener(e -> {
            try { pane.print(); } catch (PrinterException ignored) {}
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottom.add(btnPrint);

        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(pane), BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(new FlatLightLaf()); } catch (Exception ex) {}
        SwingUtilities.invokeLater(() -> new PhieuPhatForm().setVisible(true));
    }
}