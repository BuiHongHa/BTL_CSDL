package view.output;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import dao.SachDao;
import model.Sach;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.print.PrinterException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SachForm extends JFrame {
    private JTextField txtSearch;
    private JComboBox<String> cmbTheLoai;
    private JTable table;
    private DefaultTableModel model;
    private SachDao sachDao;

    // Màu chủ đạo (Xanh dương)
    private final Color PRIMARY_COLOR = new Color(32, 136, 203);

    public SachForm() {
        sachDao = new SachDao();
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("Quản Lý Sách");
        setSize(1000, 650);
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

    // --- 1. HEADER UI ---
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("TRA CỨU SÁCH", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Quản lý danh mục sách và tài liệu trong thư viện", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(230, 230, 230));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle);
        textPanel.add(lblSubtitle);
        header.add(textPanel, BorderLayout.CENTER);

        return header;
    }

    // --- 2. FILTER UI ---
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(" Bộ lọc tìm kiếm "));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hàng 1: Tìm kiếm
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Từ khóa:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên sách, tác giả...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        panel.add(txtSearch, gbc);

        // Hàng 1: Thể loại
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.0;
        panel.add(new JLabel("   Thể loại: "), gbc);

        gbc.gridx = 3; gbc.gridy = 0;
        cmbTheLoai = new JComboBox<>();
        populateTheLoaiFilter();
        panel.add(cmbTheLoai, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(PRIMARY_COLOR);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearch.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);

        JButton btnReload = new JButton("Làm mới");

        btnPanel.add(btnSearch);
        btnPanel.add(btnReload);

        gbc.gridx = 4; gbc.gridy = 0;
        panel.add(btnPanel, gbc);

        // Events
        btnSearch.addActionListener(e -> loadData());
        btnReload.addActionListener(e -> {
            txtSearch.setText("");
            cmbTheLoai.setSelectedIndex(0);
            loadData();
        });
        cmbTheLoai.addActionListener(e -> loadData());

        return panel;
    }

    // --- 3. TABLE UI ---
    private JScrollPane createTablePanel() {
        model = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        model.addColumn("Mã Sách");
        model.addColumn("Tên Sách");
        model.addColumn("Tác Giả");
        model.addColumn("Năm XB");
        model.addColumn("Thể Loại");

        table = new JTable(model);
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(new Color(50, 50, 50));
        header.setPreferredSize(new Dimension(0, 35));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Năm XB

        return new JScrollPane(table);
    }

    // --- 4. BOTTOM UI ---
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);

        JButton btnReport = new JButton(" Xuất Báo Cáo ");
        btnReport.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReport.setBackground(new Color(46, 204, 113)); // Green
        btnReport.setForeground(Color.WHITE);
        btnReport.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);

        btnReport.addActionListener(e -> showPrintPreview());
        panel.add(btnReport);

        return panel;
    }

    // ================= LOGIC =================

    private void populateTheLoaiFilter() {
        try {
            List<String> list = sachDao.getDistinctTheLoai();
            cmbTheLoai.addItem("Tất cả Thể Loại");
            for (String tl : list) {
                cmbTheLoai.addItem(tl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        String keyword = txtSearch.getText();
        String theLoai = "";
        if (cmbTheLoai.getSelectedItem() != null) {
            theLoai = cmbTheLoai.getSelectedItem().toString();
        }

        model.setRowCount(0);
        try {
            List<Sach> list = sachDao.getAllSach(keyword, theLoai);
            for (Sach s : list) {
                model.addRow(new Object[]{
                        s.getMaSach(),
                        s.getTenSach(),
                        s.getTacGia(),
                        s.getNamXuatBan(),
                        s.getTheLoai()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    // ================= BÁO CÁO (PROFESSIONAL UI) =================

    private void showPrintPreview() {
        String html = generateReportHTML();

        JEditorPane editor = new JEditorPane("text/html", html);
        editor.setEditable(false);
        editor.setCaretPosition(0);

        JDialog dialog = new JDialog(this, "Báo Cáo Danh Mục Sách", true);
        dialog.setSize(900, 750);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnPrint = new JButton("🖨️ In báo cáo");
        btnPrint.setBackground(new Color(46, 204, 113));
        btnPrint.setForeground(Color.WHITE);

        JButton btnClose = new JButton("Đóng");

        btnPrint.addActionListener(e -> {
            try { editor.print(); } catch (PrinterException ex) {}
        });
        btnClose.addActionListener(e -> dialog.dispose());

        btnPanel.add(btnPrint);
        btnPanel.add(btnClose);

        dialog.add(new JScrollPane(editor), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private String generateReportHTML() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateStr = sdf.format(new Date());
        String headerColor = "#2c3e50"; // Xanh đậm chuyên nghiệp

        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><style>");
        sb.append("body { font-family: Sans-serif; font-size: 12px; color: #333; padding: 10px; }");

        // Table CSS
        sb.append(".data-table { width: 100%; border-collapse: collapse; margin-top: 15px; }");
        sb.append(".data-table th { background-color: " + headerColor + "; color: white; padding: 10px; text-align: left; border: 1px solid " + headerColor + "; }");
        sb.append(".data-table td { padding: 8px; border: 1px solid #ddd; }");
        sb.append(".data-table tr:nth-child(even) { background-color: #f9f9f9; }"); // Zebra striping

        // Footer CSS
        sb.append(".footer { margin-top: 30px; text-align: right; font-size: 13px; }");
        sb.append("</style></head><body>");

        // HEADER: Dùng bảng để chia 2 cột (Logo trái - Tiêu đề phải)
        sb.append("<table width='100%'><tr>");
        sb.append("<td width='60%' valign='top'>");
        sb.append("<h3 style='margin:0; color:" + headerColor + ";'>THƯ VIỆN HỌC VIỆN CÔNG NGHỆ BƯU CHÍNH VIỄN THÔNG</h3>");
        sb.append("<p style='margin:5px 0; font-size:11px;'>Đường Trần Phú, Hà Đông, Hà Nội</p>");
        sb.append("</td>");
        sb.append("<td width='40%' align='right' valign='top'>");
        sb.append("<h2 style='margin:0; color:" + headerColor + ";'>DANH MỤC SÁCH</h2>");
        sb.append("<p style='margin:5px 0; font-size:11px;'>Ngày xuất: ").append(dateStr).append("</p>");
        sb.append("</td></tr></table>");

        sb.append("<hr style='border-top: 2px solid " + headerColor + "; margin-bottom: 20px;'/>");

        // TABLE DATA
        sb.append("<table class='data-table'>");
        sb.append("<thead><tr>")
                .append("<th width='10%'>Mã Sách</th>")
                .append("<th width='35%'>Tên Sách</th>")
                .append("<th width='20%'>Tác Giả</th>")
                .append("<th width='10%' style='text-align:center;'>Năm XB</th>")
                .append("<th width='15%'>Thể Loại</th></tr></thead><tbody>");

        for (int i = 0; i < model.getRowCount(); i++) {
            sb.append("<tr>")
                    .append("<td style='text-align:center;'><b>").append(model.getValueAt(i, 0)).append("</b></td>")
                    .append("<td>").append(model.getValueAt(i, 1)).append("</td>")
                    .append("<td>").append(model.getValueAt(i, 2)).append("</td>")
                    .append("<td style='text-align:center;'>").append(model.getValueAt(i, 3)).append("</td>")
                    .append("<td>").append(model.getValueAt(i, 4)).append("</td>")
                    .append("</tr>");
        }
        sb.append("</tbody></table>");

        // FOOTER
        sb.append("<div class='footer'>");
        sb.append("<p>Tổng số đầu sách: <b>").append(model.getRowCount()).append("</b></p>");
        sb.append("<br/><br/><p>Người lập báo cáo</p><br/><br/><br/><p>(Ký và ghi rõ họ tên)</p>");
        sb.append("</div>");

        sb.append("</body></html>");
        return sb.toString();
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(new FlatLightLaf()); } catch (Exception ex) {}
        SwingUtilities.invokeLater(() -> new SachForm().setVisible(true));
    }
}