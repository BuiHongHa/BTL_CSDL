package view;

// Import 2 DAO và 2 Model
import dao.PhieuMuonDao;
import dao.ChiTietMuonDao;
import model.PhieuMuon;
import model.ChiTietMuon;

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
 * Form TRA CỨU PHIẾU MƯỢN (Read-Only)
 * Hỗ trợ lọc và xem hóa đơn chi tiết
 */
public class PhieuMuonForm extends JFrame {
    private JTextField txtSearch;
    private JComboBox<String> cmbTrangThai;
    private JTable table;
    private DefaultTableModel model;

    // Cần 2 DAO
    private PhieuMuonDao phieuMuonDAO;
    private ChiTietMuonDao chiTietMuonDAO;

    public PhieuMuonForm() {
        phieuMuonDAO = new PhieuMuonDao();
        chiTietMuonDAO = new ChiTietMuonDao();

        setTitle("🧾 Tra Cứu Phiếu Mượn");
        setSize(950, 550);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==== PANEL TÌM KIẾM & LỌC (NORTH) ====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm & Lọc"));

        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("🔍 Tìm");

        // Lọc theo trạng thái
        cmbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đang mượn", "Đã trả"});

        JButton btnReload = new JButton("🔄 Tải lại Toàn bộ");

        searchPanel.add(new JLabel("Tìm (Mã PM/Tên NĐ/SĐT):"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(new JSeparator(SwingConstants.VERTICAL));
        searchPanel.add(new JLabel("Lọc theo Trạng Thái:"));
        searchPanel.add(cmbTrangThai);
        searchPanel.add(new JSeparator(SwingConstants.VERTICAL));
        searchPanel.add(btnReload);
        add(searchPanel, BorderLayout.NORTH);

        // ==== BẢNG DỮ LIỆU (CENTER) ====
        model = new DefaultTableModel();
        model.addColumn("Mã PM");
        model.addColumn("Tên Người Đọc");
        model.addColumn("SĐT Người Đọc");
        model.addColumn("Tên Nhân Viên");
        model.addColumn("Ngày Mượn");
        model.addColumn("Ngày Trả");
        model.addColumn("Trạng Thái");

        table = new JTable(model);
        table.setDefaultEditor(Object.class, null);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ==== PANEL BÁO CÁO (SOUTH) ====
        JPanel reportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        reportPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JButton btnPreviewReport = new JButton("📄 Xem Hóa Đơn Phiếu Mượn");
        reportPanel.add(btnPreviewReport);
        add(reportPanel, BorderLayout.SOUTH);

        // ==== SỰ KIỆN ====
        btnReload.addActionListener(e -> {
            txtSearch.setText("");
            cmbTrangThai.setSelectedIndex(0);
            loadData();
        });
        btnSearch.addActionListener(e -> loadData());
        cmbTrangThai.addActionListener(e -> loadData());
        // Sửa: Nút này giờ là "Xem Hóa Đơn"
        btnPreviewReport.addActionListener(e -> showInvoicePreview());

        // ==== NẠP DỮ LIỆU BAN ĐẦU ====
        loadData();
    }

    // =======================================================
    // HÀM TẢI DỮ LIỆU (Cho JTable)
    // =======================================================

    private void loadData() {
        String keyword = txtSearch.getText();
        String trangThai = cmbTrangThai.getSelectedItem().toString();

        model.setRowCount(0); // Xóa bảng
        try {
            List<PhieuMuon> list = phieuMuonDAO.getTraCuuPhieuMuon(keyword, trangThai);

            for (PhieuMuon pmi : list) {
                Vector<Object> row = new Vector<>();
                row.add(pmi.getMaPhieuMuon());
                row.add(pmi.getTenNguoiDoc());
                row.add(pmi.getSdtNguoiDoc());
                row.add(pmi.getTenNhanVien());
                row.add(pmi.getNgayMuon());
                row.add(pmi.getNgayTra());
                row.add(pmi.getTrangThai());
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu phiếu mượn: " + e.getMessage());
        }
    }

    // =======================================================
    // CÁC HÀM BÁO CÁO (HÓA ĐƠN)
    // =======================================================

    /**
     * HÀM 1: Hiển thị Hóa Đơn (Preview) của dòng đang chọn
     */
    private void showInvoicePreview() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu mượn để xem hóa đơn.", "Chưa chọn phiếu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Lấy thông tin cơ bản từ JTable
        String maPM = model.getValueAt(selectedRow, 0).toString();
        String tenND = model.getValueAt(selectedRow, 1).toString();
        String sdtND = model.getValueAt(selectedRow, 2).toString();
        String tenNV = model.getValueAt(selectedRow, 3).toString();
        String ngayMuon = model.getValueAt(selectedRow, 4).toString();
        Object ngayTraObj = model.getValueAt(selectedRow, 5); // Có thể là NULL
        String trangThai = model.getValueAt(selectedRow, 6).toString();

        // 2. Lấy chi tiết sách từ CSDL (dùng DAO thứ 2)
        List<ChiTietMuon> chiTietList;
        try {
            chiTietList = chiTietMuonDAO.getChiTietCuaPhieu(maPM);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi lấy chi tiết phiếu: " + e.getMessage());
            return;
        }

        // 3. Tạo nội dung HTML
        String reportContent = generateInvoiceHTML(maPM, tenND, sdtND, tenNV, ngayMuon, ngayTraObj, trangThai, chiTietList);

        // 4. Hiển thị Dialog
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        editorPane.setText(reportContent);

        JDialog previewDialog = new JDialog(this, "Hóa Đơn Phiếu Mượn: " + maPM, true);
        previewDialog.setSize(650, 700);
        previewDialog.setLocationRelativeTo(this);
        previewDialog.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnPrint = new JButton("🖨️ In Hóa Đơn");
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
     * HÀM 2: Tạo nội dung Hóa Đơn (HTML)
     */
    private String generateInvoiceHTML(String maPM, String tenND, String sdtND, String tenNV, String ngayMuon, Object ngayTraObj, String trangThai, List<ChiTietMuon> chiTietList) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String reportDate = sdf.format(new Date());

        String ngayTraStr = (ngayTraObj != null) ? ngayTraObj.toString() : "Chưa trả";

        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><style>");
        sb.append(" body { font-family: Arial, sans-serif; margin: 20px; }");
        sb.append(" h1 { text-align: center; color: #333; }");
        sb.append(" p { margin: 5px 0; }");
        sb.append(" .header, .info { margin-bottom: 20px; }");
        sb.append(" .info-grid { display: grid; grid-template-columns: 1fr 1fr; }");
        sb.append(" table { width: 100%; border-collapse: collapse; margin-top: 15px; }");
        sb.append(" th, td { border: 1px solid #999; padding: 8px; text-align: left; }");
        sb.append(" th { background-color: #f2f2f2; }");
        sb.append(" .footer { text-align: center; margin-top: 50px; }");
        sb.append("</style></head><body>");

        // ---- TIÊU ĐỀ HÓA ĐƠN ----
        sb.append("<div class='header'>");
        sb.append("<p style='text-align:center;'><strong>THƯ VIỆN PTIT</strong></p>");
        sb.append("<p style='text-align:center;'>Đường Trần Phú, Hà Đông, Hà Nội</p>");
        sb.append("</div>");
        sb.append("<hr>");
        sb.append("<h1>PHIẾU MƯỢN SÁCH</h1>");
        sb.append("<p style='text-align:center;'>Mã Phiếu: <strong>").append(maPM).append("</strong></p>");
        sb.append("<p style='text-align:center;'>Ngày lập: ").append(reportDate).append("</p>");

        // ---- THÔNG TIN CHUNG ----
        sb.append("<div class='info'>");
        sb.append(" <h3>Thông tin Người mượn:</h3>");
        sb.append(" <p><strong>Họ tên:</strong> ").append(tenND).append("</p>");
        sb.append(" <p><strong>SĐT:</strong> ").append(sdtND).append("</p>");
        sb.append(" <h3>Thông tin Phiếu:</h3>");
        sb.append(" <p><strong>Nhân viên lập phiếu:</strong> ").append(tenNV).append("</p>");
        sb.append(" <p><strong>Ngày mượn:</strong> ").append(ngayMuon).append("</p>");

// ---- DÒNG BẠN BẢO BỊ LỖI ----
// Nếu bạn có biến 'ngayHenTra', thay trực tiếp ở đây
        String ngayHenTra = "Không có (chưa có trường trong DB)";

        sb.append(" <p><strong>Ngày trả (hẹn trả):</strong> ").append(ngayHenTra).append("</p>");

        sb.append(" <p><strong>Ngày trả thực tế:</strong> ").append(ngayTraStr).append("</p>");
        sb.append(" <p><strong>Trạng thái:</strong> ").append(trangThai).append("</p>");
        sb.append("</div>");


        // ---- BẢNG CHI TIẾT SÁCH ----
        sb.append("<h3>Chi Tiết Sách Đã Mượn:</h3>");
        sb.append("<table>");
        sb.append("<thead><tr>");
        sb.append("<th>Mã Sách</th>");
        sb.append("<th>Tên Sách</th>");
        sb.append("<th>Tình Trạng Khi Mượn</th>");
        sb.append("</tr></thead>");
        sb.append("<tbody>");

        for (ChiTietMuon cti : chiTietList) {
            sb.append("<tr>");
            sb.append("<td>").append(cti.getMaSach()).append("</td>");
            sb.append("<td>").append(cti.getTenSach()).append("</td>");
            sb.append("<td>").append(cti.getTinhTrangSach()).append("</td>");
            sb.append("</tr>");
        }

        sb.append("</tbody></table>");

        // ---- CHÂN HÓA ĐƠN ----
        sb.append("<div class='footer'>");
        sb.append("<p>Cảm ơn đã sử dụng dịch vụ!</p>");
        sb.append("</div>");
        sb.append("</body></html>");

        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PhieuMuonForm().setVisible(true));
    }
}