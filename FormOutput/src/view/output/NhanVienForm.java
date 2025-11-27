package view.output;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import dao.NhanVienDao; // Đảm bảo tên class DAO đúng (NhanVienDao hoặc NhanVienDao)
import model.NhanVien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NhanVienForm extends JFrame {
    private JTextField txtSearch;
    private JTable table;
    private DefaultTableModel model;
    private NhanVienDao NhanVienDao;

    // Màu chủ đạo (Xanh dương)
    private final Color PRIMARY_COLOR = new Color(32, 136, 203);

    public NhanVienForm() {
        NhanVienDao = new NhanVienDao();
        initUI();
        loadData("");
    }

    private void initUI() {
        setTitle("Quản Lý Nhân Sự");
        setSize(1000, 650);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. HEADER
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. NỘI DUNG CHÍNH
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

        JLabel lblTitle = new JLabel("DANH SÁCH NHÂN VIÊN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Quản lý thông tin hồ sơ nhân sự thư viện", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(230, 230, 230));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle);
        textPanel.add(lblSubtitle);
        header.add(textPanel, BorderLayout.CENTER);

        return header;
    }

    // --- 2. FILTER ---
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(" Bộ lọc tìm kiếm "));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Từ khóa:"), gbc);

        // TextField
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mã nhân viên, họ tên hoặc số điện thoại...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        panel.add(txtSearch, gbc);

        // Button Panel
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

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(btnPanel, gbc);

        // Events
        btnSearch.addActionListener(e -> loadData(txtSearch.getText()));
        btnReload.addActionListener(e -> {
            txtSearch.setText("");
            loadData("");
        });

        return panel;
    }

    // --- 3. TABLE ---
    private JScrollPane createTablePanel() {
        model = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        model.addColumn("Mã NV");
        model.addColumn("Họ và Tên");
        model.addColumn("Chức Vụ");
        model.addColumn("Số Điện Thoại");

        table = new JTable(model);
        table.setRowHeight(35); // Dòng cao thoáng
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Header Style
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(new Color(50, 50, 50));
        header.setPreferredSize(new Dimension(0, 40));

        // Center Alignment for specific columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // SĐT

        return new JScrollPane(table);
    }

    // --- 4. BOTTOM ---
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);

        JButton btnReport = new JButton(" Xuất Danh Sách ");
        btnReport.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReport.setBackground(new Color(46, 204, 113)); // Green
        btnReport.setForeground(Color.WHITE);
        btnReport.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);

        btnReport.addActionListener(e -> showReportPreview());
        panel.add(btnReport);

        return panel;
    }

    // =======================================================
    // LOGIC TẢI DỮ LIỆU
    // =======================================================
    private void loadData(String keyword) {
        model.setRowCount(0);
        try {
            List<NhanVien> list = NhanVienDao.getAllNhanVien(keyword);
            for (NhanVien nv : list) {
                model.addRow(new Object[]{
                        nv.getMaNhanVien(),
                        nv.getHoTen(),
                        nv.getChucVu(),
                        nv.getSdt()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =======================================================
    // BÁO CÁO (HTML PREVIEW)
    // =======================================================
    // --- SỬA HÀM NÀY ---
    private void showReportPreview() {
        try {
            List<NhanVien> list = NhanVienDao.getAllNhanVien(txtSearch.getText());
            if (list.isEmpty()) { JOptionPane.showMessageDialog(this, "Không có dữ liệu!"); return; }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String color = "#3288cb";

            StringBuilder sb = new StringBuilder();
            sb.append("<html><body style='font-family: Sans-serif; padding: 20px;'>");

            // Header
            sb.append("<div style='text-align:center; margin-bottom:20px;'>");
            sb.append("<h2 style='margin:0; color:").append(color).append(";'>DANH SÁCH NHÂN VIÊN</h2>");
            sb.append("<p style='font-size:12px; color:#666;'>Ngày lập: ").append(sdf.format(new Date())).append("</p>");
            sb.append("</div>");

            // Table
            sb.append("<table style='width:100%; border-collapse: collapse; font-size:12px;'>");
            sb.append("<tr style='background-color: #f2f2f2; border-bottom: 2px solid ").append(color).append(";'>")
                    .append("<th style='padding:10px; text-align:left;'>MÃ NV</th>")
                    .append("<th style='padding:10px; text-align:left;'>HỌ VÀ TÊN</th>")
                    .append("<th style='padding:10px; text-align:left;'>CHỨC VỤ</th>")
                    .append("<th style='padding:10px; text-align:right;'>SỐ ĐIỆN THOẠI</th></tr>");

            for (NhanVien nv : list) {
                sb.append("<tr>")
                        .append("<td style='padding:8px; border-bottom:1px solid #eee;'><b>").append(nv.getMaNhanVien()).append("</b></td>")
                        .append("<td style='padding:8px; border-bottom:1px solid #eee;'>").append(nv.getHoTen()).append("</td>")
                        .append("<td style='padding:8px; border-bottom:1px solid #eee;'>").append(nv.getChucVu()).append("</td>")
                        .append("<td style='padding:8px; border-bottom:1px solid #eee; text-align:right;'>").append(nv.getSdt()).append("</td>")
                        .append("</tr>");
            }
            sb.append("</table>");
            sb.append("<p style='text-align:right; margin-top:15px;'>Tổng số: <b>").append(list.size()).append(" nhân viên</b></p>");
            sb.append("</body></html>");

            // Show Dialog
            JEditorPane ep = new JEditorPane("text/html", sb.toString());
            ep.setEditable(false); ep.setCaretPosition(0);
            JDialog dialog = new JDialog(this, "Danh Sách Nhân Viên", true);
            dialog.setSize(800, 600);
            dialog.setLocationRelativeTo(this);
            dialog.add(new JScrollPane(ep));
            dialog.setVisible(true);

        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(new FlatLightLaf()); } catch (Exception ex) {}
        SwingUtilities.invokeLater(() -> new NhanVienForm().setVisible(true));
    }
}