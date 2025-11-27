package view.output;

import com.formdev.flatlaf.FlatClientProperties;
import dao.PhieuMuonDao;
import dao.ChiTietMuonDao;
import model.PhieuMuon;
import model.ChiTietMuon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PhieuMuonForm extends JFrame {

    private JTextField txtSearch;
    private JComboBox<String> cmbTrangThai;
    private JTable table;
    private DefaultTableModel model;

    private JPanel filterPanel, bottomPanel;

    private PhieuMuonDao phieuMuonDAO;
    private ChiTietMuonDao chiTietMuonDAO;

    private final Color PRIMARY_COLOR = new Color(32, 136, 203);

    // ===== QUẢN LÝ =====
    public PhieuMuonForm() {
        phieuMuonDAO = new PhieuMuonDao();
        chiTietMuonDAO = new ChiTietMuonDao();
        initUI();
        loadData();
    }

    // ===== NGƯỜI ĐỌC =====
    public PhieuMuonForm(String maNguoiDoc) {
        phieuMuonDAO = new PhieuMuonDao();
        chiTietMuonDAO = new ChiTietMuonDao();
        initUI();
        hideFiltersForReader();
        loadDataOfReader(maNguoiDoc);
    }

    // ================= UI =================
    private void initUI() {
        setTitle("Quản Lý Mượn Trả");
        setSize(1050, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        filterPanel = createFilterPanel();
        bottomPanel = createBottomPanel();

        contentPanel.add(filterPanel, BorderLayout.NORTH);
        contentPanel.add(createTablePanel(), BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lbl = new JLabel("TRA CỨU PHIẾU MƯỢN", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(Color.WHITE);

        header.add(lbl, BorderLayout.CENTER);
        return header;
    }

    // ================= FILTER CHO QUẢN LÝ =================
    private JPanel createFilterPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(" Bộ lọc "));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0;
        p.add(new JLabel("Từ khóa:"), g);

        g.gridx = 1; g.gridy = 0; g.weightx = 1.0;
        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mã phiếu, tên bạn đọc, SĐT...");
        p.add(txtSearch, g);

        g.gridx = 2; g.gridy = 0; g.weightx = 0;
        p.add(new JLabel("Trạng thái:"), g);

        g.gridx = 3; g.gridy = 0;
        cmbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đang mượn", "Đã trả"});
        p.add(cmbTrangThai, g);

        JButton b = new JButton("Tìm kiếm");
        b.setBackground(PRIMARY_COLOR);
        b.setForeground(Color.WHITE);
        g.gridx = 4; g.gridy = 0;
        p.add(b, g);

        b.addActionListener(e -> loadData());
        cmbTrangThai.addActionListener(e -> loadData());

        return p;
    }

    // ================= TABLE =================
    private JScrollPane createTablePanel() {
        model = new DefaultTableModel() {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        model.setColumnIdentifiers(new Object[]{"Mã PM", "Tên Bạn Đọc", "SĐT", "Nhân Viên", "Ngày Mượn", "Ngày Trả", "Trạng Thái"});

        table = new JTable(model);
        table.setRowHeight(30);
        return new JScrollPane(table);
    }

    // ================= BOTTOM =================
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);

        JButton btnInvoice = new JButton(" Xem Phiếu Mượn ");
        btnInvoice.setBackground(new Color(46, 204, 113));
        btnInvoice.setForeground(Color.WHITE);
        btnInvoice.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnInvoice.addActionListener(e -> showInvoice());

        panel.add(btnInvoice);
        return panel;
    }

    // ================= LOAD DỮ LIỆU =================
    private void loadData() {
        model.setRowCount(0);
        try {
            List<PhieuMuon> list = phieuMuonDAO.getTraCuuPhieuMuon(
                    txtSearch.getText().trim(),
                    cmbTrangThai.getSelectedItem().toString(),
                    "", "", "", ""
            );
            for (PhieuMuon p : list) addRow(p);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    private void loadDataOfReader(String maND) {
        model.setRowCount(0);
        try {
            for (PhieuMuon p : phieuMuonDAO.getPhieuMuonTheoNguoiDoc(maND)) addRow(p);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu người đọc!");
        }
    }

    private void addRow(PhieuMuon p) {
        model.addRow(new Object[]{
                p.getMaPhieuMuon(),
                p.getTenNguoiDoc(), p.getSdtNguoiDoc(), p.getTenNhanVien(),
                p.getNgayMuon(),
                p.getNgayTra() == null ? "Chưa trả" : p.getNgayTra(),
                p.getTrangThai()
        });
    }

    private void hideFiltersForReader() {
        setTitle("📌 Lịch Sử Mượn Sách");
        if (filterPanel != null) filterPanel.setVisible(false);
        if (bottomPanel != null) bottomPanel.setVisible(true);
    }

    // ================= HÓA ĐƠN HTML =================
    private void showInvoice() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "⚠ Vui lòng chọn một phiếu để xem hóa đơn!");
            return;
        }

        String maPM = model.getValueAt(row, 0).toString();

        try {
            PhieuMuon pm = phieuMuonDAO.getById(maPM);
            List<ChiTietMuon> list = chiTietMuonDAO.getChiTietCuaPhieu(maPM);

            JEditorPane htmlPane = new JEditorPane();
            htmlPane.setContentType("text/html");
            htmlPane.setText(renderInvoiceHTML(pm, list));
            htmlPane.setEditable(false);

            JButton btnPrint = new JButton("🖨️ In Phiếu");
            btnPrint.setBackground(new Color(46, 204, 113));
            btnPrint.setForeground(Color.WHITE);
            btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnPrint.addActionListener(e -> {
                try { htmlPane.print(); } catch (Exception ignored) {}
            });

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottom.add(btnPrint);

            JDialog dialog = new JDialog(this, " Phiếu Mượn", true);
            dialog.setSize(650, 850);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            dialog.add(new JScrollPane(htmlPane), BorderLayout.CENTER);
            dialog.add(bottom, BorderLayout.SOUTH);
            dialog.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi tải hóa đơn: " + ex.getMessage());
        }
    }

    private String renderInvoiceHTML(PhieuMuon p, List<ChiTietMuon> list) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        String ngayMuon  = (p.getNgayMuon() != null ? sdf.format(p.getNgayMuon()) : "—");
        String ngayTra   = (p.getNgayTra()  != null ? sdf.format(p.getNgayTra())  : "<i>Không có (chưa xác định)</i>");
        String ngayTraTT = (p.getNgayTra()  != null ? sdf.format(p.getNgayTra())  : "<i>Chưa trả</i>");

        StringBuilder html = new StringBuilder();

        html.append("<html><body style='font-family:Segoe UI; width:600px;'>");

        // Header
        html.append("<h2 style='text-align:center;margin:0;'>THƯ VIỆN PTIT</h2>");
        html.append("<p style='text-align:center;margin:0;'>Đường Trần Phú, Hà Đông, Hà Nội</p>");
        html.append("<hr>");
        html.append("<h2 style='text-align:center;margin-top:10px;'>PHIẾU MƯỢN SÁCH</h2>");
        html.append("<p style='text-align:center;'>Mã Phiếu: <b>").append(p.getMaPhieuMuon()).append("</b></p>");
        html.append("<p style='text-align:center;'>Ngày lập: ").append(new Date()).append("</p>");

        // Info người mượn
        html.append("<h3>Thông tin Người mượn:</h3>");
        html.append("<p>Họ tên: <b>").append(p.getTenNguoiDoc()).append("</b></p>");
        html.append("<p>SĐT: ").append(p.getSdtNguoiDoc()).append("</p>");

        // Info phiếu
        html.append("<h3>Thông tin Phiếu:</h3>");
        html.append("<p>Nhân viên lập phiếu: ").append(p.getTenNhanVien()).append("</p>");
        html.append("<p>Ngày mượn: ").append(ngayMuon).append("</p>");
        html.append("<p>Ngày trả (hẹn trả): ").append(ngayTra).append("</p>");
        html.append("<p>Ngày trả thực tế: ").append(ngayTraTT).append("</p>");
        html.append("<p>Trạng thái: <b>").append(p.getTrangThai()).append("</b></p>");

        // Table Sách
        html.append("<h3>Chi Tiết Sách Đã Mượn:</h3>");
        html.append("<table border='1' cellspacing='0' cellpadding='5' style='border-collapse:collapse;width:100%;text-align:center;'>");
        html.append("<tr style='background:#f2f2f2;'><th>Mã Sách</th><th>Tên Sách</th></tr>");

        for (ChiTietMuon ct : list) {
            html.append("<tr>")
                    .append("<td>").append(ct.getMaSach()).append("</td>")
                    .append("<td>").append(ct.getTenSach()).append("</td>")
                    .append("</tr>");
        }

        html.append("</table><br><br>");


        // Chữ ký
        html.append("<table style='width:100%;text-align:center;'>")
                .append("<tr><td><b>Người Mượn</b></td><td><b>Người Lập Phiếu</b></td></tr>")
                .append("<tr><td>(Ký tên)</td><td>(Ký tên)</td></tr>")
                .append("<tr><td><br><br></td><td></td></tr>")
                .append("<tr><td>").append(p.getTenNguoiDoc()).append("</td>")
                .append("<td>").append(p.getTenNhanVien()).append("</td></tr>")
                .append("</table>");

        html.append("</body></html>");
        return html.toString();
    }

}
