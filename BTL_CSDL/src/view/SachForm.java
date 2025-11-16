package view;

import dao.SachDao; // Sửa tên DAO (từ SachDao -> SachDao)
import model.Sach;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import java.awt.print.PrinterException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Form TRA CỨU SÁCH (Read-Only)
 * ĐÃ CẬP NHẬT theo CSDL 'thu_vien' (bỏ cột Số Lượng)
 */
public class SachForm extends JFrame {
    private JTextField txtSearch;
    private JComboBox<String> cmbTheLoai;
    private JTable table;
    private DefaultTableModel model;

    // Sửa tên biến và Class (chuẩn Java)
    private SachDao SachDao;

    public SachForm() {
        SachDao = new SachDao(); // Sửa tên Class

        setTitle("📚 Tra Cứu & Báo Cáo Sách");
        setSize(900, 550);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==== PANEL TÌM KIẾM & LỌC (NORTH) ====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm & Lọc"));

        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("🔍 Tìm");
        cmbTheLoai = new JComboBox<>();
        JButton btnReload = new JButton("🔄 Tải lại Toàn bộ");

        searchPanel.add(new JLabel("Tìm (Tên/Tác giả):"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(new JSeparator(SwingConstants.VERTICAL));
        searchPanel.add(new JLabel("Lọc theo Thể Loại:"));
        searchPanel.add(cmbTheLoai);
        searchPanel.add(new JSeparator(SwingConstants.VERTICAL));
        searchPanel.add(btnReload);
        add(searchPanel, BorderLayout.NORTH);

        // ==== BẢNG DỮ LIỆU (CENTER) ====
        model = new DefaultTableModel();
        model.addColumn("Mã Sách");
        model.addColumn("Tên Sách");
        model.addColumn("Tác Giả");
        model.addColumn("Năm XB");
        model.addColumn("Thể Loại");
        // ĐÃ XÓA CỘT "Số Lượng"

        table = new JTable(model);
        table.setDefaultEditor(Object.class, null);
        add(new JScrollPane(table), BorderLayout.CENTER);


        // ==== PANEL BÁO CÁO (SOUTH) ====
        JPanel reportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        reportPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JButton btnPreviewReport = new JButton("📄 Xem Báo Cáo (Dạng Hóa Đơn)");
        reportPanel.add(btnPreviewReport);
        add(reportPanel, BorderLayout.SOUTH);

        // ==== SỰ KIỆN ====
        btnReload.addActionListener(e -> {
            txtSearch.setText("");
            cmbTheLoai.setSelectedIndex(0);
            loadData();
        });
        btnSearch.addActionListener(e -> loadData());
        cmbTheLoai.addActionListener(e -> loadData());
        btnPreviewReport.addActionListener(e -> showPrintPreview());

        // ==== NẠP DỮ LIỆU BAN ĐẦU ====
        populateTheLoaiFilter();
        loadData();
    }

    // =======================================================
    // HÀM TẢI DỮ LIỆU
    // =======================================================

    private void populateTheLoaiFilter() {
        try {
            List<String> listTheLoai = SachDao.getDistinctTheLoai(); // Sửa tên biến
            cmbTheLoai.addItem("Tất cả Thể Loại");
            for (String theLoai : listTheLoai) {
                cmbTheLoai.addItem(theLoai);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách thể loại: " + e.getMessage());
        }
    }

    private void loadData() {
        String keyword = txtSearch.getText();
        String theLoai = "";
        if (cmbTheLoai.getSelectedItem() != null) {
            theLoai = cmbTheLoai.getSelectedItem().toString();
        }

        model.setRowCount(0); // Xóa bảng
        try {
            List<Sach> list = SachDao.getAllSach(keyword, theLoai); // Sửa tên biến
            for (Sach s : list) {
                Vector<Object> row = new Vector<>();
                row.add(s.getMaSach());
                row.add(s.getTenSach());
                row.add(s.getTacGia());
                row.add(s.getNamXuatBan());
                row.add(s.getTheLoai());
                // ĐÃ XÓA DÒNG .add(s.getSoLuong())
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    // =======================================================
    // CÁC HÀM BÁO CÁO (ĐÃ NÂNG CẤP HTML)
    // =======================================================

    private void showPrintPreview() {
        String reportContent = generateReportHTML();

        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        editorPane.setText(reportContent);

        JDialog previewDialog = new JDialog(this, "Bản Xem Trước Báo Cáo", true);
        previewDialog.setSize(650, 700);
        previewDialog.setLocationRelativeTo(this);
        previewDialog.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnPrint = new JButton("🖨️ In ra giấy");
        JButton btnClose = new JButton("Đóng");

        buttonPanel.add(btnPrint);
        buttonPanel.add(btnClose);

        previewDialog.add(new JScrollPane(editorPane), BorderLayout.CENTER);
        previewDialog.add(buttonPanel, BorderLayout.SOUTH);

        btnClose.addActionListener(e -> previewDialog.dispose());

        btnPrint.addActionListener(e -> {
            try {
                editorPane.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(previewDialog, "Lỗi khi in: " + ex.getMessage(), "Lỗi In", JOptionPane.ERROR_MESSAGE);
            }
        });

        previewDialog.setVisible(true);
    }

    /**
     * HÀM TẠO BÁO CÁO HTML (Đã cập nhật)
     */
    private String generateReportHTML() {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String reportDate = sdf.format(new Date());

        // ---- BẮT ĐẦU HTML ----
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<style>");
        sb.append(" body { font-family: Arial, sans-serif; margin: 20px; }");
        sb.append(" h1 { text-align: center; color: #333; }");
        sb.append(" p { text-align: center; margin: 0; }");
        sb.append(" .header { margin-bottom: 20px; }");
        sb.append(" table { width: 100%; border-collapse: collapse; margin-top: 15px; }");
        sb.append(" th, td { border: 1px solid #999; padding: 8px; text-align: left; }");
        sb.append(" th { background-color: #f2f2f2; }");
        sb.append(" .footer { margin-top: 20px; }");
        sb.append(" .signature { text-align: center; margin-top: 50px; }");
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body>");

        // ---- TIÊU ĐỀ HÓA ĐƠN (Giữ nguyên từ code của bạn) ----
        sb.append("<div class='header'>");
        sb.append("<p><strong>THƯ VIỆN PTIT</strong></p>");
        sb.append("<p>Đường Trần Phú, Hà Đông, Hà Nội</p>");
        sb.append("</div>");
        sb.append("<hr>");
        sb.append("<h1>BÁO CÁO DANH MỤC SÁCH</h1>"); // Sửa tên
        sb.append("<p>Ngày lập: ").append(reportDate).append("</p>");

        // ---- BẢNG DỮ LIỆU ----
        sb.append("<table>");
        sb.append("<thead>");
        sb.append("<tr>");
        sb.append("<th>Mã</th>");
        sb.append("<th>Tên Sách</th>");
        sb.append("<th>Tác Giả</th>");
        sb.append("<th>Năm</th>");
        sb.append("<th>Thể Loại</th>");
        // ĐÃ XÓA CỘT "SL"
        sb.append("</tr>");
        sb.append("</thead>");
        sb.append("<tbody>");

        for (int i = 0; i < model.getRowCount(); i++) {
            sb.append("<tr>");
            sb.append("<td>").append(model.getValueAt(i, 0)).append("</td>");
            sb.append("<td>").append(model.getValueAt(i, 1)).append("</td>");
            sb.append("<td>").append(model.getValueAt(i, 2)).append("</td>");
            sb.append("<td>").append(model.getValueAt(i, 3)).append("</td>");
            sb.append("<td>").append(model.getValueAt(i, 4)).append("</td>");
            // ĐÃ XÓA DÒNG getValueAt(i, 5)
            sb.append("</tr>");
        }

        sb.append("</tbody>");
        sb.append("</table>");

        // ---- CHÂN HÓA ĐƠN ----
        sb.append("<div class='footer'>");
        // Chỉ còn tổng số đầu sách
        sb.append("<p><strong>Tổng số đầu sách:</strong> ").append(model.getRowCount()).append("</p>");
        sb.append("</div>");

        // Giữ nguyên chữ ký
        sb.append("<div class='signature'>");
        sb.append("<p>(Người lập báo cáo)</p>");
        sb.append("<br><br><br>");
        sb.append("<p>-----------</p>");
        sb.append("</div>");

        // ---- KẾT THÚC HTML ----
        sb.append("</body></html>");

        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SachForm().setVisible(true));
    }
}