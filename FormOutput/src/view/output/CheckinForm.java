package view.output;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import dao.CheckinDao;
import model.CheckinInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class CheckinForm extends JFrame {
    private JTextField txtSearch, txtDateFrom, txtDateTo;
    private JTable table;
    private DefaultTableModel model;
    private CheckinDao checkinDao;

    // Màu chủ đạo (Xanh dương hiện đại)
    private final Color PRIMARY_COLOR = new Color(32, 136, 203);

    public CheckinForm() {
        checkinDao = new CheckinDao();
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("Quản Lý Thư Viện - Check-in");
        setSize(1100, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. HEADER (ĐÃ CĂN GIỮA)
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

    // --- HEADER (SỬA ĐỔI: CĂN GIỮA) ---
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Thêm SwingConstants.CENTER để căn giữa
        JLabel lblTitle = new JLabel("TRA CỨU LỊCH SỬ CHECK-IN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        // Thêm SwingConstants.CENTER để căn giữa
        JLabel lblSubtitle = new JLabel("Quản lý thông tin người ra vào và các yêu cầu gửi đến thư viện", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(230, 230, 230));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle);
        textPanel.add(lblSubtitle);

        header.add(textPanel, BorderLayout.CENTER);
        return header;
    }

    // --- FILTER ---
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(" Bộ lọc tìm kiếm "));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hàng 1: Từ khóa
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tìm kiếm:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên, mã phiếu, SĐT...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        panel.add(txtSearch, gbc);

        // Nút bấm
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(PRIMARY_COLOR);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearch.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);

        JButton btnReload = new JButton("Tải lại");

        btnPanel.add(btnSearch);
        btnPanel.add(btnReload);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(btnPanel, gbc);

        // Hàng 2: Ngày tháng
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Thời gian:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        JPanel datePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        datePanel.setOpaque(false);

        txtDateFrom = new JTextField();
        txtDateFrom.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Từ ngày (YYYY-MM-DD)");

        txtDateTo = new JTextField();
        txtDateTo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Đến ngày (YYYY-MM-DD)");

        datePanel.add(txtDateFrom);
        datePanel.add(txtDateTo);

        panel.add(datePanel, gbc);

        // Sự kiện
        btnSearch.addActionListener(e -> loadData());
        btnReload.addActionListener(e -> {
            txtSearch.setText(""); txtDateFrom.setText(""); txtDateTo.setText("");
            loadData();
        });

        return panel;
    }

    // --- TABLE ---
    private JScrollPane createTablePanel() {
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        model.addColumn("Mã Phiếu");
        model.addColumn("Ngày Yêu Cầu");
        model.addColumn("Họ Tên");
        model.addColumn("SĐT");
        model.addColumn("Đơn Vị");
        model.addColumn("Nội Dung / Lý Do");

        table = new JTable(model);
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(240, 240, 240));
        header.setPreferredSize(new Dimension(0, 35));

        // Căn giữa các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Ngày
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // SĐT

        return new JScrollPane(table);
    }

    // --- BOTTOM ---
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);

        JButton btnReport = new JButton(" Xuất Báo Cáo ");
        btnReport.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReport.setBackground(new Color(46, 204, 113)); // Màu xanh lá
        btnReport.setForeground(Color.WHITE);
        btnReport.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);

        btnReport.addActionListener(e -> showCheckinListReport());
        panel.add(btnReport);

        return panel;
    }

    // --- LOGIC ---
    private void loadData() {
        String keyword = txtSearch.getText().trim();
        String dateFrom = txtDateFrom.getText().trim();
        String dateTo = txtDateTo.getText().trim();

        if (!dateFrom.isEmpty() && !isValidDate(dateFrom)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu sai định dạng (YYYY-MM-DD)"); return;
        }
        if (!dateTo.isEmpty() && !isValidDate(dateTo)) {
            JOptionPane.showMessageDialog(this, "Ngày kết thúc sai định dạng (YYYY-MM-DD)"); return;
        }

        model.setRowCount(0);
        try {
            List<CheckinInfo> list = checkinDao.getTraCuuCheckin(keyword, dateFrom, dateTo);
            for (CheckinInfo ci : list) {
                model.addRow(new Object[]{
                        ci.getMaPhieuGui(), ci.getNgayYeuCau(), ci.getHoTen(),
                        ci.getSoDienThoai(), ci.getDonVi(), ci.getLyDo()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    private boolean isValidDate(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    // --- BÁO CÁO HTML ---
    // --- SỬA HÀM NÀY ---
    private void showCheckinListReport() {
        try {
            List<CheckinInfo> list = checkinDao.getTraCuuCheckin(txtSearch.getText(), txtDateFrom.getText(), txtDateTo.getText());
            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu!"); return;
            }

            // Gọi hàm tạo HTML mới
            String html = generateCheckinHTML(list);

            JEditorPane editor = new JEditorPane("text/html", html);
            editor.setEditable(false);
            editor.setCaretPosition(0);

            JDialog dialog = new JDialog(this, "Báo Cáo Check-in", true);
            dialog.setSize(850, 700);
            dialog.setLocationRelativeTo(this);
            dialog.add(new JScrollPane(editor));
            dialog.setVisible(true);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // --- THÊM HÀM NÀY ---
    private String generateCheckinHTML(List<CheckinInfo> list) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String now = sdf.format(new java.util.Date());
        String color = "#3288cb"; // Màu chủ đạo FlatLaf Blue

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: Sans-serif; padding: 20px;'>");

        // Header
        sb.append("<table width='100%'><tr>");
        sb.append("<td><h3 style='margin:0; color:").append(color).append(";'>THƯ VIỆN PTIT</h3><p style='font-size:11px; margin:5px 0;'>Đường Trần Phú, Hà Đông, Hà Nội</p></td>");
        sb.append("<td align='right'><h2 style='margin:0; color:#333;'>DANH SÁCH CHECK-IN</h2><p style='font-size:11px;'>Xuất ngày: ").append(now).append("</p></td>");
        sb.append("</tr></table><hr style='border-top: 2px solid ").append(color).append(";'/>");

        // Table
        sb.append("<table style='width:100%; border-collapse: collapse; margin-top:15px; font-size:12px;'>");
        sb.append("<tr style='background-color: ").append(color).append("; color: white;'>")
                .append("<th style='padding:8px;'>Mã</th>")
                .append("<th style='padding:8px;'>Họ Tên</th>")
                .append("<th style='padding:8px;'>Đơn Vị</th>")
                .append("<th style='padding:8px;'>Ngày Yêu Cầu</th>")
                .append("<th style='padding:8px;'>Nội Dung</th></tr>");

        for (CheckinInfo c : list) {
            sb.append("<tr>")
                    .append("<td style='padding:6px; border-bottom:1px solid #ddd; text-align:center;'>").append(c.getMaPhieuGui()).append("</td>")
                    .append("<td style='padding:6px; border-bottom:1px solid #ddd;'>").append(c.getHoTen()).append("</td>")
                    .append("<td style='padding:6px; border-bottom:1px solid #ddd; text-align:center;'>").append(c.getDonVi()).append("</td>")
                    .append("<td style='padding:6px; border-bottom:1px solid #ddd; text-align:center;'>").append(c.getNgayYeuCau()).append("</td>")
                    .append("<td style='padding:6px; border-bottom:1px solid #ddd;'>").append(c.getLyDo()).append("</td>")
                    .append("</tr>");
        }
        sb.append("</table>");

        // Footer
        sb.append("<div style='margin-top:20px; text-align:right;'><i>Tổng số lượt: <b>").append(list.size()).append("</b></i></div>");
        sb.append("</body></html>");
        return sb.toString();
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(new FlatLightLaf()); } catch (Exception ex) {}
        SwingUtilities.invokeLater(() -> new CheckinForm().setVisible(true));
    }
}