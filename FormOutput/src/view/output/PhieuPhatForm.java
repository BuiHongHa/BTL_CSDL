package view.output;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import dao.PhieuPhatDao;
import model.PhieuPhat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class PhieuPhatForm extends JFrame {

    private JTextField txtSearch, txtFrom, txtTo;
    private JTable table;
    private DefaultTableModel model;
    private PhieuPhatDao dao;

    private JPanel filterPanel, bottomPanel; // 👉 Để ẩn với người đọc

    private final DecimalFormat df = new DecimalFormat("#,###");
    private final Color PRIMARY_COLOR = new Color(32, 136, 203);

    // ================== QUẢN LÝ ==================
    public PhieuPhatForm() {
        dao = new PhieuPhatDao();
        initUI();
        loadData();
    }

    // ================== NGƯỜI ĐỌC ==================
    public PhieuPhatForm(String maNguoiDoc) {
        dao = new PhieuPhatDao();
        initUI();
        hideForReader();
        loadDataForReader(maNguoiDoc);
    }

    // =============== UI CHUNG ====================
    private void initUI() {
        setTitle("Quản Lý Phiếu Phạt");
        setSize(1100, 650);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);

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

    // === HEADER ===
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("TRA CỨU PHIẾU PHẠT", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        header.add(lblTitle, BorderLayout.CENTER);
        return header;
    }

    // === FILTER (QUẢN LÝ) ===
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(" Bộ lọc tìm kiếm "));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Từ khóa
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Từ khóa:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mã phiếu, tên bạn đọc, SĐT...");
        panel.add(txtSearch, gbc);

        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(PRIMARY_COLOR);
        btnSearch.setForeground(Color.WHITE);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(btnSearch, gbc);

        // Ngày lập
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Ngày lập:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        JPanel datePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        txtFrom = new JTextField(); txtTo = new JTextField();
        txtFrom.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Từ ngày (YYYY-MM-DD)");
        txtTo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Đến ngày");
        datePanel.setOpaque(false);
        datePanel.add(txtFrom); datePanel.add(txtTo);
        panel.add(datePanel, gbc);

        JButton btnReload = new JButton("Làm mới");
        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(btnReload, gbc);

        btnSearch.addActionListener(e -> searchData());
        btnReload.addActionListener(e -> refresh());

        return panel;
    }

    // === TABLE ===
    private JScrollPane createTablePanel() {
        model = new DefaultTableModel() { @Override public boolean isCellEditable(int r, int c) { return false; }};
        model.setColumnIdentifiers(new Object[]{"Mã PP", "Tên Người Đọc", "SĐT", "Nhân Viên", "Ngày Lập", "Ngày Mượn", "Ngày Trả", "Tiền Phạt", "Lý Do"});
        table = new JTable(model);
        table.setRowHeight(28);
        return new JScrollPane(table);
    }

    // === BOTTOM (QUẢN LÝ) ===
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);
        JButton btnView = new JButton(" Xem Phiếu Phạt ");
        btnView.setBackground(new Color(46, 204, 113));
        btnView.setForeground(Color.WHITE);
        btnView.addActionListener(e -> showInvoice());
        panel.add(btnView);
        return panel;
    }

    // ================== LOAD (QUẢN LÝ) ====================
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
            for (PhieuPhat p : dao.search(txtSearch.getText(), txtFrom.getText(), txtTo.getText()))
                addRow(p);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void refresh() {
        txtSearch.setText(""); txtFrom.setText(""); txtTo.setText(""); loadData();
    }

    // === LOAD DÀNH CHO NGƯỜI ĐỌC ===
    private void loadDataForReader(String maND) {
        model.setRowCount(0);
        try {
            List<PhieuPhat> list = dao.getByNguoiDoc(maND);

            // 👉 Nếu không có dữ liệu -> báo thông báo
            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "🎉 Bạn không có phiếu phạt nào!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Nếu có -> load vào bảng
            for (PhieuPhat p : list) addRow(p);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi tải dữ liệu người đọc!");
        }
    }


    private void hideForReader() {
        setTitle("🔍 Lịch Sử Phiếu Phạt Của Bạn");
        if (filterPanel != null) filterPanel.setVisible(false);
        if (bottomPanel != null) bottomPanel.setVisible(false);
    }

    // === FORMAT DÒNG ===
    private void addRow(PhieuPhat p) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        model.addRow(new Object[]{
                p.getMaPhieuPhat(),
                p.getTenNguoiDoc(), p.getSdtNguoiDoc(), p.getTenNhanVien(),
                sdf.format(p.getNgayLap()), sdf.format(p.getNgayMuon()), sdf.format(p.getNgayTra()),
                df.format(p.getTienPhat()) + " đ", p.getLyDo()
        });
    }

    // === HÓA ĐƠN QUẢN LÝ (ĐỂ NGUYÊN) ===
    private void showInvoice() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "⚠ Vui lòng chọn 1 phiếu phạt để xem/in!");
            return;
        }

        String maPP = model.getValueAt(row, 0).toString();

        try {
            PhieuPhat p = dao.getById(maPP);

            JEditorPane htmlPane = new JEditorPane();
            htmlPane.setContentType("text/html");
            htmlPane.setText(renderInvoiceHTML(p));
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

            JDialog d = new JDialog(this, " Phiếu Phạt", true);
            d.setSize(600, 820);
            d.setLocationRelativeTo(this);
            d.setLayout(new BorderLayout());
            d.add(new JScrollPane(htmlPane), BorderLayout.CENTER);
            d.add(bottom, BorderLayout.SOUTH);
            d.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Lỗi mở hóa đơn: " + e.getMessage());
        }
    }


    private String renderInvoiceHTML(PhieuPhat p) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family:Segoe UI; width:550px;'>");

        // HEADER
        html.append("<h2 style='text-align:center;margin:0;'>THƯ VIỆN PTIT</h2>");
        html.append("<p style='text-align:center;margin:0;'>Đường Trần Phú, Hà Đông, Hà Nội</p>");
        html.append("<hr>");
        html.append("<h2 style='text-align:center;margin-top:10px;'>PHIẾU THU TIỀN PHẠT</h2>");
        html.append("<p style='text-align:center;'>Mã Phiếu: <b>").append(p.getMaPhieuPhat()).append("</b></p>");
        html.append("<p style='text-align:center;'>Ngày lập: ").append(sdf1.format(p.getNgayLap())).append("</p>");

        // NGƯỜI NỘP
        html.append("<h3>Thông tin Người Nộp Phạt:</h3>");
        html.append("<p>Họ tên: <b>").append(p.getTenNguoiDoc()).append("</b></p>");
        html.append("<p>SĐT: ").append(p.getSdtNguoiDoc()).append("</p>");

        // THÔNG TIN MƯỢN / TRẢ
        html.append("<h3>Thông Tin Mượn - Trả:</h3>");
        html.append("<p>Nhân viên thu: ").append(p.getTenNhanVien()).append("</p>");
        html.append("<p>Ngày mượn: ").append(sdf2.format(p.getNgayMuon())).append("</p>");
        html.append("<p>Ngày trả: ").append(p.getNgayTra() != null ? sdf2.format(p.getNgayTra()) : "<i>Chưa trả</i>").append("</p>");

        // TIỀN PHẠT
        html.append("<h3>Thông Tin Phạt:</h3>");
        html.append("<p>Lý Do: <b>").append(p.getLyDo()).append("</b></p>");
        html.append("<p>Số Tiền: <b>").append(String.format("%,.0f VNĐ", p.getTienPhat())).append("</b></p>");

        // CHỮ KÝ
        html.append("<br><br><table style='width:100%;text-align:center;'>")
                .append("<tr><td><b>Người Nộp</b></td><td><b>Người Thu</b></td></tr>")
                .append("<tr><td>(Ký tên)</td><td>(Ký tên)</td></tr>")
                .append("<tr><td><br><br></td><td></td></tr>")
                .append("<tr><td>").append(p.getTenNguoiDoc()).append("</td>")
                .append("<td>").append(p.getTenNhanVien()).append("</td></tr>")
                .append("</table>");

        html.append("</body></html>");
        return html.toString();
    }


}
