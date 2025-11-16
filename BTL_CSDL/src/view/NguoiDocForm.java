package view;

import dao.NguoiDocDao; // Sửa tên DAO (từ NguoiDocDao -> NguoiDocDao)
import model.NguoiDoc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

/**
 * Đây là Form TRA CỨU (Read-Only)
 * ĐÃ CẬP NHẬT theo CSDL 'thu_vien'
 */
public class NguoiDocForm extends JFrame {
    private JTextField txtSearch;
    private JTable table;
    private DefaultTableModel model;

    // Sửa lại tên biến và class (NguoiDocDao)
    private NguoiDocDao NguoiDocDao;

    public NguoiDocForm() {
        NguoiDocDao = new NguoiDocDao(); // Sửa lại tên class

        setTitle("👥 Tra Cứu Bạn Đọc");
        setSize(850, 450);
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
        searchPanel.add(new JLabel("Tìm (Tên/SĐT/Đơn vị):"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnReload);

        add(searchPanel, BorderLayout.NORTH);

        // ==== BẢNG DỮ LIỆU (TRUNG TÂM) ====
        model = new DefaultTableModel();
        model.addColumn("Mã BĐ");
        model.addColumn("Họ Tên");
        model.addColumn("Đơn Vị"); // Sửa cột
        model.addColumn("Địa Chỉ");
        model.addColumn("SĐT");
        // Đã xóa các cột cũ

        table = new JTable(model);
        table.setDefaultEditor(Object.class, null); // Không cho sửa

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
            List<NguoiDoc> list = NguoiDocDao.getAllNguoiDoc(keyword);

            // Đổ dữ liệu vào model
            for (NguoiDoc nd : list) {
                Vector<Object> row = new Vector<>();
                row.add(nd.getMaNguoiDoc()); // Sửa getter
                row.add(nd.getHoTen());
                row.add(nd.getDonVi()); // Sửa getter
                row.add(nd.getDiaChi());
                row.add(nd.getSoDienThoai()); // Sửa getter
                // Đã xóa các .add() cũ
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NguoiDocForm().setVisible(true));
    }
}