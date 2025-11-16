package view;

// Sửa tên DAO cho đúng
import dao.NhanVienDao;
import model.NhanVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

/**
 * Đây là Form TRA CỨU (Read-Only)
 * ĐÃ CẬP NHẬT THEO CSDL MỚI (4 cột)
 */
public class NhanVienForm extends JFrame {
    private JTextField txtSearch;
    private JTable table;
    private DefaultTableModel model;

    // Sửa lại tên biến và class (NhanVienDao)
    private NhanVienDao NhanVienDao;

    public NhanVienForm() {
        NhanVienDao = new NhanVienDao(); // Sửa lại tên class

        setTitle("🧑‍💼 Tra Cứu Nhân Viên");
        setSize(800, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==== PANEL TÌM KIẾM (ĐẶT LÊN TRÊN) ====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm & Lọc"));

        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("🔍 Tìm");
        JButton btnReload = new JButton("🔄 Tải lại Toàn bộ");

        // Đã sửa nhãn tìm kiếm
        searchPanel.add(new JLabel("Tìm kiếm (Tên/SĐT):"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnReload);

        add(searchPanel, BorderLayout.NORTH);

        // ==== BẢNG DỮ LIỆU (TRUNG TÂM) ====
        model = new DefaultTableModel();
        // Đã xóa 2 cột
        model.addColumn("Mã NV");
        model.addColumn("Họ Tên");
        model.addColumn("Chức Vụ");
        model.addColumn("SĐT");

        table = new JTable(model);
        table.setDefaultEditor(Object.class, null);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ==== SỰ KIỆN ====
        btnReload.addActionListener(e -> loadData(""));
        btnSearch.addActionListener(e -> loadData(txtSearch.getText()));

        // ==== NẠP DỮ LIỆU BAN ĐẦU ====
        loadData("");
    }

    /**
     * Tải dữ liệu từ DAO lên bảng
     */
    private void loadData(String keyword) {
        model.setRowCount(0); // Xóa bảng
        try {
            // Sửa lại tên biến
            List<NhanVien> list = NhanVienDao.getAllNhanVien(keyword);

            // Đổ dữ liệu vào model
            for (NhanVien nv : list) {
                Vector<Object> row = new Vector<>();
                row.add(nv.getMaNhanVien());
                row.add(nv.getHoTen());
                row.add(nv.getChucVu());
                row.add(nv.getSdt());
                // Đã xóa 2 dòng .add()
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NhanVienForm().setVisible(true));
    }
}