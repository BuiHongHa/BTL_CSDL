package view;

// 1. Import FlatLaf
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;

// 2. Import DAO & Model
import dao.PhieuMuonDao;
import dao.ChiTietMuonDao;
import model.PhieuMuon;
import model.ChiTietMuon;

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

public class PhieuMuonForm extends JFrame {

    private JTextField txtSearch, txtFromMuon, txtToMuon, txtFromTra, txtToTra;
    private JComboBox<String> cmbTrangThai;
    private JTable table;
    private DefaultTableModel model;

    private PhieuMuonDao phieuMuonDAO;
    private ChiTietMuonDao chiTietMuonDAO;

    // Màu chủ đạo (Xanh dương)
    private final Color PRIMARY_COLOR = new Color(32, 136, 203);

    public PhieuMuonForm() {
        phieuMuonDAO = new PhieuMuonDao();
        chiTietMuonDAO = new ChiTietMuonDao();
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("Quản Lý Mượn Trả Sách");
        setSize(1100, 750);
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

    // --- HEADER ---
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("TRA CỨU PHIẾU MƯỢN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Theo dõi tình trạng mượn trả và xuất hóa đơn chi tiết", SwingConstants.CENTER);
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
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hàng 1: Từ khóa & Trạng thái
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Từ khóa:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mã phiếu, Tên bạn đọc, SĐT...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        panel.add(txtSearch, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.0;
        panel.add(new JLabel("Trạng thái:"), gbc);

        gbc.gridx = 3; gbc.gridy = 0;
        cmbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đang mượn", "Đã trả"});
        panel.add(cmbTrangThai, gbc);

        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(PRIMARY_COLOR);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        gbc.gridx = 4; gbc.gridy = 0;
        panel.add(btnSearch, gbc);

        // Hàng 2: Ngày mượn
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Ngày mượn:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 3;
        JPanel pnlDateMuon = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlDateMuon.setOpaque(false);

        txtFromMuon = new JTextField();
        txtFromMuon.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Từ ngày (YYYY-MM-DD)");

        txtToMuon = new JTextField();
        txtToMuon.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Đến ngày");

        pnlDateMuon.add(txtFromMuon);
        pnlDateMuon.add(txtToMuon);
        panel.add(pnlDateMuon, gbc);

        // Hàng 3: Ngày trả
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(new JLabel("Ngày trả:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3;
        JPanel pnlDateTra = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlDateTra.setOpaque(false);

        txtFromTra = new JTextField();
        txtFromTra.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Từ ngày (YYYY-MM-DD)");

        txtToTra = new JTextField();
        txtToTra.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Đến ngày");

        pnlDateTra.add(txtFromTra);
        pnlDateTra.add(txtToTra);
        panel.add(pnlDateTra, gbc);

        JButton btnReload = new JButton("Làm mới");
        gbc.gridx = 4; gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(btnReload, gbc);

        // Events
        btnSearch.addActionListener(e -> loadData());
        cmbTrangThai.addActionListener(e -> loadData());
        btnReload.addActionListener(e -> {
            txtSearch.setText("");
            txtFromMuon.setText(""); txtToMuon.setText("");
            txtFromTra.setText(""); txtToTra.setText("");
            cmbTrangThai.setSelectedIndex(0);
            loadData();
        });

        return panel;
    }

    // --- TABLE ---
    private JScrollPane createTablePanel() {
        model = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        model.addColumn("Mã PM");
        model.addColumn("Tên Bạn Đọc");
        model.addColumn("SĐT");
        model.addColumn("Nhân Viên");
        model.addColumn("Ngày Mượn");
        model.addColumn("Ngày Trả");
        model.addColumn("Trạng Thái");

        table = new JTable(model);
        table.setRowHeight(32);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(240, 240, 240));
        header.setPreferredSize(new Dimension(0, 35));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Ngày mượn
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Ngày trả

        return new JScrollPane(table);
    }

    // --- BOTTOM ---
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);

        JButton btnReport = new JButton(" Xem Hóa Đơn Chi Tiết ");
        btnReport.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReport.setBackground(new Color(46, 204, 113)); // Màu xanh lá
        btnReport.setForeground(Color.WHITE);
        btnReport.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);

        btnReport.addActionListener(e -> showInvoicePreview());
        panel.add(btnReport);

        return panel;
    }

    // ================== LOGIC ==================

    private void loadData() {
        model.setRowCount(0);
        try {
            List<PhieuMuon> list = phieuMuonDAO.getTraCuuPhieuMuon(
                    txtSearch.getText(),
                    cmbTrangThai.getSelectedItem().toString(),
                    txtFromMuon.getText(), txtToMuon.getText(),
                    txtFromTra.getText(), txtToTra.getText()
            );

            for (PhieuMuon p : list) {
                model.addRow(new Object[]{
                        p.getMaPhieuMuon(),
                        p.getTenNguoiDoc(),
                        p.getSdtNguoiDoc(),
                        p.getTenNhanVien(),
                        p.getNgayMuon(),
                        p.getNgayTra(),
                        p.getTrangThai()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    // ================== BÁO CÁO (HTML CHUYÊN NGHIỆP) ==================

    private void showInvoicePreview() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu để xem hóa đơn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maPM = model.getValueAt(row, 0).toString();
        String tenND = model.getValueAt(row, 1).toString();
        String sdtND = model.getValueAt(row, 2).toString();
        String tenNV = model.getValueAt(row, 3).toString();
        Object ngayMuon = model.getValueAt(row, 4);
        Object ngayTra = model.getValueAt(row, 5);
        String trangThai = model.getValueAt(row, 6).toString();

        try {
            List<ChiTietMuon> listCT = chiTietMuonDAO.getChiTietCuaPhieu(maPM);
            String html = generateInvoiceHTML(maPM, tenND, sdtND, tenNV, ngayMuon, ngayTra, trangThai, listCT);

            JEditorPane editor = new JEditorPane("text/html", html);
            editor.setEditable(false);
            editor.setCaretPosition(0);

            JDialog dialog = new JDialog(this, "Hóa Đơn: " + maPM, true);
            dialog.setSize(800, 750);
            dialog.setLocationRelativeTo(this);
            dialog.add(new JScrollPane(editor));
            dialog.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải chi tiết phiếu: " + e.getMessage());
        }
    }

    private String generateInvoiceHTML(String maPM, String tenND, String sdtND,
                                       String tenNV, Object ngayMuon, Object ngayTra,
                                       String trangThai, List<ChiTietMuon> listCT) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String now = sdf.format(new Date());
        String headerColor = "#2c3e50";

        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><style>");
        sb.append("body { font-family: Sans-serif; font-size: 12px; color: #333; padding: 10px; }");
        sb.append(".info-box { width: 100%; background-color: #f8f9fa; border-left: 5px solid " + headerColor + "; padding: 10px; margin-bottom: 20px; }");
        sb.append(".data-table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }");
        sb.append(".data-table th { background-color: " + headerColor + "; color: white; padding: 10px; text-align: left; }");
        sb.append(".data-table td { padding: 8px; border-bottom: 1px solid #ddd; }");
        sb.append(".footer { text-align: center; font-style: italic; color: #777; margin-top: 30px; font-size: 11px; }");
        sb.append("</style></head><body>");

        // Header
        sb.append("<table width='100%'><tr>");
        sb.append("<td width='60%'><h2 style='margin:0; color:" + headerColor + ";'>THƯ VIỆN PTIT</h2>");
        sb.append("<p style='margin:5px 0; font-size:11px;'>97 Man Thiện, P. Hiệp Phú, TP. Thủ Đức</p></td>");
        sb.append("<td width='40%' align='right'><h1 style='margin:0; color:" + headerColor + ";'>PHIẾU MƯỢN</h1>");
        sb.append("<p style='margin:0;'>Số: <b>").append(maPM).append("</b></p></td></tr></table>");
        sb.append("<hr style='border-top: 2px solid " + headerColor + "; margin-bottom: 20px;'>");

        // Info Box
        sb.append("<div class='info-box'><table width='100%'><tr>");
        sb.append("<td width='50%' valign='top'><b>BÊN MƯỢN:</b><br/>Họ tên: ").append(tenND).append("<br/>SĐT: ").append(sdtND).append("</td>");
        sb.append("<td width='50%' valign='top'><b>THÔNG TIN:</b><br/>NV lập: ").append(tenNV).append("<br/>Trạng thái: <b>").append(trangThai).append("</b></td>");
        sb.append("</tr></table></div>");

        // Dates
        sb.append("<p>Thời gian mượn: <b>").append(ngayMuon).append("</b> &nbsp;|&nbsp; Hạn trả/Ngày trả: <b>").append((ngayTra != null ? ngayTra : "Chưa trả")).append("</b></p>");

        // Data Table
        sb.append("<table class='data-table'><thead><tr><th width='15%'>MÃ SÁCH</th><th width='50%'>TÊN SÁCH</th><th width='35%'>TÌNH TRẠNG</th></tr></thead><tbody>");
        for (ChiTietMuon ct : listCT) {
            sb.append("<tr><td><b>").append(ct.getMaSach()).append("</b></td><td>").append(ct.getTenSach()).append("</td><td>").append(ct.getTinhTrangSach()).append("</td></tr>");
        }
        if(listCT.size() < 3) sb.append("<tr><td colspan='3' style='height: 30px;'></td></tr>");
        sb.append("</tbody></table>");

        // Signatures
        sb.append("<table width='100%' style='text-align:center; margin-top:40px;'><tr>");
        sb.append("<td width='33%'><b>Người lập phiếu</b><br/><i>(Ký tên)</i><br/><br/><br/>").append(tenNV).append("</td>");
        sb.append("<td width='33%'><b>Thủ thư</b><br/><i>(Đóng dấu)</i><br/><br/><br/>&nbsp;</td>");
        sb.append("<td width='33%'><b>Người mượn</b><br/><i>(Ký tên)</i><br/><br/><br/>").append(tenND).append("</td>");
        sb.append("</tr></table>");

        sb.append("<div class='footer'>Cảm ơn bạn đã sử dụng dịch vụ của Thư viện PTIT.</div>");
        sb.append("</body></html>");
        return sb.toString();
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(new FlatLightLaf()); } catch (Exception ex) {}
        SwingUtilities.invokeLater(() -> new PhieuMuonForm().setVisible(true));
    }
}