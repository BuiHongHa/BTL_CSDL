package view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import dao.ChiTietMuonDao;
import dao.NguoiDocDao; // Import đúng tên class DAO bạn đã tạo (NguoiDocDao hoặc NguoiDocDao)
import model.ChiTietMuon;
import model.NguoiDoc;

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

public class NguoiDocForm extends JFrame {
    private JTextField txtSearch;
    private JTable table;
    private DefaultTableModel model;
    private NguoiDocDao NguoiDocDao; // Sửa tên biến cho đúng

    // Màu chủ đạo (Xanh dương)
    private final Color PRIMARY_COLOR = new Color(32, 136, 203);

    public NguoiDocForm() {
        NguoiDocDao = new NguoiDocDao();
        initUI();
        loadData("");
    }

    private void initUI() {
        setTitle("Quản Lý Bạn Đọc & Lịch Sử Mượn");
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

    // --- HEADER ---
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("TRA CỨU BẠN ĐỌC", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Quản lý thông tin bạn đọc và xem lịch sử mượn trả", SwingConstants.CENTER);
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
        panel.setBorder(BorderFactory.createTitledBorder(" Bộ lọc dữ liệu "));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tìm kiếm:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mã, tên, đơn vị, SĐT...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        panel.add(txtSearch, gbc);

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

        btnSearch.addActionListener(e -> loadData(txtSearch.getText()));
        btnReload.addActionListener(e -> { txtSearch.setText(""); loadData(""); });

        return panel;
    }

    // --- TABLE ---
    private JScrollPane createTablePanel() {
        model = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        model.addColumn("Mã BĐ");
        model.addColumn("Họ Tên");
        model.addColumn("Đơn Vị");
        model.addColumn("Địa Chỉ");
        model.addColumn("SĐT");

        table = new JTable(model);
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(240, 240, 240));
        header.setPreferredSize(new Dimension(0, 35));

        return new JScrollPane(table);
    }

    // --- BOTTOM ---
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);

        JButton btnHistory = new JButton(" Xem Lịch Sử Mượn Sách ");
        btnHistory.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnHistory.setBackground(new Color(46, 204, 113));
        btnHistory.setForeground(Color.WHITE);
        btnHistory.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);

        btnHistory.addActionListener(e -> showBorrowReport());
        panel.add(btnHistory);

        return panel;
    }

    // --- LOGIC ---
    private void loadData(String keyword) {
        model.setRowCount(0);
        try {
            List<NguoiDoc> list = NguoiDocDao.getAllNguoiDoc(keyword);
            for (NguoiDoc nd : list) {
                model.addRow(new Object[]{
                        nd.getMaNguoiDoc(), nd.getHoTen(), nd.getDonVi(), nd.getDiaChi(), nd.getSoDienThoai()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    // --- BÁO CÁO HTML ---
    private void showBorrowReport() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một bạn đọc để xem lịch sử!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maND = model.getValueAt(row, 0).toString();
        String hoTen = model.getValueAt(row, 1).toString();
        String donVi = model.getValueAt(row, 2).toString();
        String sdt = model.getValueAt(row, 4).toString();

        try {
            ChiTietMuonDao ctmDao = new ChiTietMuonDao();
            List<ChiTietMuon> list = ctmDao.getLichSuMuonTheoNguoiDoc(maND);

            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bạn đọc này chưa từng mượn sách nào.", "Thông tin", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            showHTMLReportDialog(hoTen, donVi, sdt, list);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi truy vấn lịch sử: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- THAY THẾ HÀM CŨ ---
    private void showHTMLReportDialog(String hoTen, String donVi, String sdt, List<ChiTietMuon> list) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String now = sdf.format(new Date());
        String color = "#3288cb";

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: Sans-serif; padding: 20px;'>");

        // Header & Info Box
        sb.append("<div style='border-left: 5px solid ").append(color).append("; padding-left: 10px; background-color: #f9f9f9; margin-bottom: 20px;'>");
        sb.append("<h2 style='margin:0; color:").append(color).append(";'>LỊCH SỬ MƯỢN SÁCH</h2>");
        sb.append("<p style='font-size:11px; margin-top:5px;'>Ngày xuất: ").append(now).append("</p>");
        sb.append("<table width='100%'><tr>");
        sb.append("<td><b>Bạn đọc:</b> ").append(hoTen).append("</td>");
        sb.append("<td><b>Đơn vị:</b> ").append(donVi).append("</td>");
        sb.append("<td><b>SĐT:</b> ").append(sdt).append("</td>");
        sb.append("</tr></table></div>");

        // Table
        sb.append("<table style='width:100%; border-collapse: collapse; font-size:12px;'>");
        sb.append("<tr style='background-color: ").append(color).append("; color: white;'>")
                .append("<th style='padding:8px;'>Mã Sách</th>")
                .append("<th style='padding:8px;'>Tên Sách</th>")
                .append("<th style='padding:8px;'>Ngày Mượn</th>")
                .append("<th style='padding:8px;'>Ngày Trả</th>")
                .append("<th style='padding:8px;'>Trạng Thái</th></tr>");

        SimpleDateFormat dFmt = new SimpleDateFormat("dd/MM/yyyy");
        for (ChiTietMuon ct : list) {
            String statusColor = "Đang mượn".equals(ct.getTrangThaiSach()) ? "#e67e22" : "#27ae60";
            String ngayTra = (ct.getNgayTra() != null) ? dFmt.format(ct.getNgayTra()) : "-";

            sb.append("<tr>")
                    .append("<td style='padding:6px; border-bottom:1px solid #ddd; text-align:center;'>").append(ct.getMaSach()).append("</td>")
                    .append("<td style='padding:6px; border-bottom:1px solid #ddd;'>").append(ct.getTenSach()).append("</td>")
                    .append("<td style='padding:6px; border-bottom:1px solid #ddd; text-align:center;'>").append(dFmt.format(ct.getNgayMuon())).append("</td>")
                    .append("<td style='padding:6px; border-bottom:1px solid #ddd; text-align:center;'>").append(ngayTra).append("</td>")
                    .append("<td style='padding:6px; border-bottom:1px solid #ddd; text-align:center; font-weight:bold; color:").append(statusColor).append(";'>").append(ct.getTrangThaiSach()).append("</td>")
                    .append("</tr>");
        }
        sb.append("</table></body></html>");

        // Phần hiển thị Dialog giữ nguyên code cũ của bạn
        JEditorPane ep = new JEditorPane("text/html", sb.toString());
        ep.setEditable(false); ep.setCaretPosition(0);
        JDialog dialog = new JDialog(this, "Lịch Sử: " + hoTen, true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.add(new JScrollPane(ep));
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Chưa cài FlatLaf");
        }
        SwingUtilities.invokeLater(() -> new NguoiDocForm().setVisible(true));
    }
}