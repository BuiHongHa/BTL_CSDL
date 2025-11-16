package view;

import dao.CheckinDao;
import model.CheckinInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import java.awt.print.PrinterException;
import java.text.SimpleDateFormat;
import java.util.Date; // java.util.Date cho việc tạo hóa đơn

/**
 * Form TRA CỨU CHECK-IN (Read-Only)
 * Hỗ trợ lọc theo ngày và xem phiếu yêu cầu
 */
public class CheckinForm extends JFrame {
    private JTextField txtSearch, txtDateFrom, txtDateTo;
    private JTable table;
    private DefaultTableModel model;

    private CheckinDao CheckinDao;

    public CheckinForm() {
        CheckinDao = new CheckinDao();

        setTitle("🚪 Tra Cứu Lượt Check-in / Yêu Cầu");
        setSize(950, 550);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5)); // Thêm khoảng cách

        // ==== PANEL TÌM KIẾM & LỌC (NORTH) ====
        JPanel searchPanel = new JPanel(new GridBagLayout()); // Dùng GridBagLayout
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm & Lọc"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dòng 1: Keyword
        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(new JLabel("Tìm (Mã/Tên NĐ/SĐT/Lý do):"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 3; // Kéo dài
        txtSearch = new JTextField(30);
        searchPanel.add(txtSearch, gbc);

        // Dòng 2: Lọc ngày
        gbc.gridwidth = 1; // Reset
        gbc.gridx = 0; gbc.gridy = 1;
        searchPanel.add(new JLabel("Lọc từ ngày:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtDateFrom = new JTextField(10);
        txtDateFrom.setToolTipText("Định dạng YYYY-MM-DD");
        searchPanel.add(txtDateFrom, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        searchPanel.add(new JLabel("Đến ngày:"), gbc);

        gbc.gridx = 3; gbc.gridy = 1;
        txtDateTo = new JTextField(10);
        txtDateTo.setToolTipText("Định dạng YYYY-MM-DD");
        searchPanel.add(txtDateTo, gbc);

        // Dòng 3: Nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSearch = new JButton("🔍 Lọc");
        JButton btnReload = new JButton("🔄 Tải lại Toàn bộ");
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnReload);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        searchPanel.add(buttonPanel, gbc);

        add(searchPanel, BorderLayout.NORTH);

        // ==== BẢNG DỮ LIỆU (CENTER) ====
        model = new DefaultTableModel();
        model.addColumn("Mã Phiếu Gửi");
        model.addColumn("Ngày Yêu Cầu");
        model.addColumn("Họ Tên Người Gửi");
        model.addColumn("SĐT");
        model.addColumn("Đơn Vị");
        model.addColumn("Lý Do Yêu Cầu");

        table = new JTable(model);
        table.setDefaultEditor(Object.class, null);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ==== PANEL BÁO CÁO (SOUTH) ====
        JPanel reportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnPreviewReport = new JButton("📄 Xem Chi Tiết Phiếu Yêu Cầu");
        reportPanel.add(btnPreviewReport);
        add(reportPanel, BorderLayout.SOUTH);

        // ==== SỰ KIỆN ====
        btnReload.addActionListener(e -> {
            txtSearch.setText("");
            txtDateFrom.setText("");
            txtDateTo.setText("");
            loadData();
        });
        btnSearch.addActionListener(e -> loadData());
        btnPreviewReport.addActionListener(e -> showInvoicePreview());

        // ==== NẠP DỮ LIỆU BAN ĐẦU ====
        loadData();
    }

    // =======================================================
    // HÀM TẢI DỮ LIỆU (Cho JTable)
    // =======================================================

    private void loadData() {
        String keyword = txtSearch.getText();
        String dateFrom = txtDateFrom.getText();
        String dateTo = txtDateTo.getText();

        // Kiểm tra định dạng ngày (đơn giản)
        if (!dateFrom.isEmpty() && !isValidDate(dateFrom)) {
            JOptionPane.showMessageDialog(this, "Sai định dạng 'Từ Ngày'. Yêu cầu: YYYY-MM-DD", "Lỗi Định Dạng", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!dateTo.isEmpty() && !isValidDate(dateTo)) {
            JOptionPane.showMessageDialog(this, "Sai định dạng 'Đến Ngày'. Yêu cầu: YYYY-MM-DD", "Lỗi Định Dạng", JOptionPane.ERROR_MESSAGE);
            return;
        }

        model.setRowCount(0); // Xóa bảng
        try {
            List<CheckinInfo> list = CheckinDao.getTraCuuCheckin(keyword, dateFrom, dateTo);

            for (CheckinInfo ci : list) {
                Vector<Object> row = new Vector<>();
                row.add(ci.getMaPhieuGui());
                row.add(ci.getNgayYeuCau());
                row.add(ci.getTenNguoiDoc());
                row.add(ci.getSdtNguoiDoc());
                row.add(ci.getDonViNguoiDoc());
                row.add(ci.getLyDo());
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu check-in: " + e.getMessage());
        }
    }

    // Hàm helper kiểm tra ngày
    private boolean isValidDate(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    // =======================================================
    // CÁC HÀM BÁO CÁO (HÓA ĐƠN)
    // =======================================================

    private void showInvoicePreview() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu để xem chi tiết.", "Chưa chọn phiếu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Lấy thông tin từ JTable (đã JOIN)
        CheckinInfo info = new CheckinInfo(
                model.getValueAt(selectedRow, 0).toString(),
                (java.sql.Date) model.getValueAt(selectedRow, 1), // Ép kiểu Date
                model.getValueAt(selectedRow, 5).toString(),
                model.getValueAt(selectedRow, 2).toString(),
                model.getValueAt(selectedRow, 3).toString(),
                model.getValueAt(selectedRow, 4).toString()
        );

        // 2. Tạo nội dung HTML
        String reportContent = generateInvoiceHTML(info);

        // 3. Hiển thị Dialog
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        editorPane.setText(reportContent);

        JDialog previewDialog = new JDialog(this, "Chi Tiết Phiếu Yêu Cầu: " + info.getMaPhieuGui(), true);
        previewDialog.setSize(500, 600);
        previewDialog.setLocationRelativeTo(this);
        previewDialog.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnPrint = new JButton("🖨️ In Phiếu");
        JButton btnClose = new JButton("Đóng");

        buttonPanel.add(btnPrint);
        buttonPanel.add(btnClose);

        previewDialog.add(new JScrollPane(editorPane), BorderLayout.CENTER);
        previewDialog.add(buttonPanel, BorderLayout.SOUTH);

        btnClose.addActionListener(e -> previewDialog.dispose());
        btnPrint.addActionListener(e -> {
            try { editorPane.print(); }
            catch (PrinterException ex) { /* Lỗi */ }
        });

        previewDialog.setVisible(true);
    }

    /**
     * HÀM 2: Tạo nội dung Phiếu Yêu Cầu (HTML)
     */
    private String generateInvoiceHTML(CheckinInfo info) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String reportDate = sdf.format(new Date());

        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><style>");
        sb.append(" body { font-family: Arial, sans-serif; margin: 20px; }");
        sb.append(" h1 { text-align: center; color: #333; }");
        sb.append(" p { margin: 10px 0; font-size: 14px; }");
        sb.append(" .header, .info { margin-bottom: 20px; }");
        sb.append(" .lydo { padding: 15px; background-color: #f9f9f9; border: 1px dashed #ccc; margin-top: 10px; }");
        sb.append(" .footer { text-align: center; margin-top: 50px; }");
        sb.append("</style></head><body>");

        // ---- TIÊU ĐỀ HÓA ĐƠN ----
        sb.append("<div class='header'>");
        sb.append("<p style='text-align:center;'><strong>THƯ VIỆN PTIT</strong></p>");
        sb.append("<p style='text-align:center;'>Đường Trần Phú, Hà Đông, Hà Nội</p>");
        sb.append("</div>");
        sb.append("<hr>");
        sb.append("<h1>PHIẾU YÊU CẦU</h1>");
        sb.append("<p style='text-align:center;'>Mã Phiếu: <strong>").append(info.getMaPhieuGui()).append("</strong></p>");
        sb.append("<p style='text-align:center;'>Ngày lập phiếu: ").append(reportDate).append("</p>");

        // ---- THÔNG TIN CHUNG ----
        sb.append("<div class='info'>");
        sb.append(" <h3>Thông tin Người Gửi:</h3>");
        sb.append(" <p><strong>Họ tên:</strong> ").append(info.getTenNguoiDoc()).append("</p>");
        sb.append(" <p><strong>SĐT:</strong> ").append(info.getSdtNguoiDoc()).append("</p>");
        sb.append(" <p><strong>Đơn vị:</strong> ").append(info.getDonViNguoiDoc()).append("</p>");
        sb.append(" <p><strong>Ngày gửi yêu cầu:</strong> ").append(info.getNgayYeuCau().toString()).append("</p>");
        sb.append("</div>");

        // ---- LÝ DO ----
        sb.append("<h3>Nội dung yêu cầu:</h3>");
        sb.append("<div class='lydo'>");
        sb.append(info.getLyDo());
        sb.append("</div>");

        // ---- CHÂN HÓA ĐƠN ----
        sb.append("<div class='footer'>");
        sb.append("<p>Xác nhận tiếp nhận yêu cầu</p>");
        sb.append("<br><br><br>");
        sb.append("<p>(Nhân viên thư viện)</p>");
        sb.append("</div>");
        sb.append("</body></html>");

        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CheckinForm().setVisible(true));
    }
}