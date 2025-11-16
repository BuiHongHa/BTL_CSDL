package SQL;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class BookForm extends JFrame {

    private JTextField txtMa, txtTen, txtTacGia, txtTheLoai, txtNam;
    private JTable table;
    private DefaultTableModel model;

    public BookForm() {
        setTitle("Quản lý sách");
        setSize(900, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel header = new JLabel("QUẢN LÝ SÁCH", JLabel.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(0, 102, 204));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(header, BorderLayout.NORTH);

        JPanel main = new JPanel(new GridLayout(1, 2));
        add(main, BorderLayout.CENTER);

        JPanel left = new JPanel(new GridLayout(12, 1, 6, 6));
        left.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        left.setBackground(new Color(240, 244, 247));

        left.add(new JLabel("Mã sách:"));
        txtMa = new JTextField();
        left.add(txtMa);

        left.add(new JLabel("Tên sách:"));
        txtTen = new JTextField();
        left.add(txtTen);

        left.add(new JLabel("Tác giả:"));
        txtTacGia = new JTextField();
        left.add(txtTacGia);

        left.add(new JLabel("Thể loại:"));
        txtTheLoai = new JTextField();
        left.add(txtTheLoai);

        left.add(new JLabel("Năm xuất bản:"));
        txtNam = new JTextField();
        left.add(txtNam);

        main.add(left);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Mã sách", "Tên sách", "Tác giả", "Thể loại", "Năm xuất bản"});
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        main.add(scroll);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = table.getSelectedRow();
                if (i >= 0) {
                    txtMa.setText(String.valueOf(model.getValueAt(i, 0)));
                    txtTen.setText(String.valueOf(model.getValueAt(i, 1)));
                    txtTacGia.setText(String.valueOf(model.getValueAt(i, 2)));
                    txtTheLoai.setText(String.valueOf(model.getValueAt(i, 3)));
                    txtNam.setText(String.valueOf(model.getValueAt(i, 4)));
                }
            }
        });

        JPanel bottom = new JPanel();
        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Cập nhật");
        JButton btnDelete = new JButton("Xóa");
        JButton btnRefresh = new JButton("Làm mới");

        btnAdd.addActionListener(e -> addBook());
        btnUpdate.addActionListener(e -> updateBook());
        btnDelete.addActionListener(e -> deleteBook());
        btnRefresh.addActionListener(e -> loadData());

        bottom.add(btnAdd);
        bottom.add(btnUpdate);
        bottom.add(btnDelete);
        bottom.add(btnRefresh);

        add(bottom, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT ma_sach, ten_sach, tac_gia, the_loai, nam_xuat_ban FROM sach");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("ma_sach"),
                        rs.getString("ten_sach"),
                        rs.getString("tac_gia"),
                        rs.getString("the_loai"),
                        rs.getInt("nam_xuat_ban")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi load dữ liệu sách: " + ex.getMessage());
        }
    }

    private void addBook() {
        String ma = txtMa.getText().trim();
        String ten = txtTen.getText().trim();
        String tacgia = txtTacGia.getText().trim();
        String theloai = txtTheLoai.getText().trim();
        String namStr = txtNam.getText().trim();

        if (ma.isEmpty() || ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã sách và Tên sách là bắt buộc.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO sach (ma_sach, ten_sach, tac_gia, the_loai, nam_xuat_ban) VALUES (?,?,?,?,?)")) {

            ps.setString(1, ma);
            ps.setString(2, ten);
            ps.setString(3, tacgia);
            ps.setString(4, theloai);
            ps.setInt(5, namStr.isEmpty() ? 0 : Integer.parseInt(namStr));

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thêm sách thành công.");
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi thêm sách: " + ex.getMessage());
        }
    }

    private void updateBook() {
        String ma = txtMa.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập mã sách để cập nhật.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE sach SET ten_sach=?, tac_gia=?, the_loai=?, nam_xuat_ban=? WHERE ma_sach=?")) {

            ps.setString(1, txtTen.getText().trim());
            ps.setString(2, txtTacGia.getText().trim());
            ps.setString(3, txtTheLoai.getText().trim());
            ps.setInt(4, txtNam.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtNam.getText().trim()));
            ps.setString(5, ma);

            int r = ps.executeUpdate();
            if (r > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công.");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Mã sách không tồn tại.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật sách: " + ex.getMessage());
        }
    }

    private void deleteBook() {
        String ma = txtMa.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập mã sách để xóa.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM sach WHERE ma_sach=?")) {

            ps.setString(1, ma);
            int r = ps.executeUpdate();
            if (r > 0) {
                JOptionPane.showMessageDialog(this, "Xóa thành công.");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Mã sách không tồn tại.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi xóa sách: " + ex.getMessage());
        }
    }
}
