package SQL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;

public class EditBookForm extends JFrame {

    private JTextField txtMaSach, txtTenSach, txtTacGia, txtNamXB;
    private JComboBox<String> cbTheLoai;
    private JLabel lblImagePreview;
    private String imagePath = null;

    public EditBookForm() {
        setTitle("Chỉnh Sửa Sách");
        setSize(650, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        Font fontLabel = new Font("Segoe UI", Font.PLAIN, 16);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 16);

        // ==== PANEL TRÁI ====
        JPanel leftPanel = new JPanel(new GridLayout(6, 2, 10, 20));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 10));

        JLabel lblMaSach = new JLabel("Mã sách:");
        JLabel lblTenSach = new JLabel("Tên sách:");
        JLabel lblTacGia = new JLabel("Tác giả:");
        JLabel lblNamXB = new JLabel("Năm xuất bản:");
        JLabel lblTheLoai = new JLabel("Thể loại:");
        JLabel lblHinhAnh = new JLabel("Hình ảnh:");

        JLabel[] labels = {lblMaSach, lblTenSach, lblTacGia, lblNamXB, lblTheLoai, lblHinhAnh};
        for (JLabel lbl : labels) lbl.setFont(fontLabel);

        txtMaSach = new JTextField();
        txtTenSach = new JTextField();
        txtTacGia = new JTextField();
        txtNamXB = new JTextField();

        txtMaSach.setFont(fontInput);
        txtTenSach.setFont(fontInput);
        txtTacGia.setFont(fontInput);
        txtNamXB.setFont(fontInput);

        cbTheLoai = new JComboBox<>(new String[]{
                "Kỹ năng", "CNTT", "Khoa học", "Tiểu thuyết", "Giáo trình", "Khác"
        });
        cbTheLoai.setFont(fontInput);

        leftPanel.add(lblMaSach);
        leftPanel.add(txtMaSach);

        leftPanel.add(lblTenSach);
        leftPanel.add(txtTenSach);

        leftPanel.add(lblTacGia);
        leftPanel.add(txtTacGia);

        leftPanel.add(lblNamXB);
        leftPanel.add(txtNamXB);

        leftPanel.add(lblTheLoai);
        leftPanel.add(cbTheLoai);

        leftPanel.add(lblHinhAnh);

        // ==== PANEL ẢNH ====
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 30));

        lblImagePreview = new JLabel();
        lblImagePreview.setPreferredSize(new Dimension(150, 200));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblImagePreview.setHorizontalAlignment(JLabel.CENTER);

        JButton btnChooseImage = new JButton("Chọn ảnh");
        btnChooseImage.setFont(fontInput);
        btnChooseImage.addActionListener(e -> chooseImage());

        imagePanel.add(lblImagePreview, BorderLayout.CENTER);
        imagePanel.add(btnChooseImage, BorderLayout.SOUTH);

        // ==== MAIN ====
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        centerPanel.add(leftPanel);
        centerPanel.add(imagePanel);

        add(centerPanel, BorderLayout.CENTER);

        // ==== NÚT ====
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        JButton btnLoad = new JButton("Tải dữ liệu");
        JButton btnSave = new JButton("Cập nhật");

        btnLoad.setFont(fontInput);
        btnSave.setFont(fontInput);

        btnLoad.addActionListener(e -> loadBook());
        btnSave.addActionListener(e -> updateBook());

        bottomPanel.add(btnLoad);
        bottomPanel.add(btnSave);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // ==== CHỌN ẢNH ====
    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            imagePath = file.getAbsolutePath();

            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);

            lblImagePreview.setIcon(new ImageIcon(img));
        }
    }

    // ==== TẢI DỮ LIỆU SÁCH ====
    private void loadBook() {
        String ma = txtMaSach.getText().trim();

        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập mã sách trước!");
            return;
        }

        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT * FROM sach WHERE ma_sach = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ma);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sách!");
                return;
            }

            txtTenSach.setText(rs.getString("ten_sach"));
            txtTacGia.setText(rs.getString("tac_gia"));
            txtNamXB.setText(String.valueOf(rs.getInt("nam_xuat_ban")));
            cbTheLoai.setSelectedItem(rs.getString("the_loai"));

            imagePath = rs.getString("hinhAnh");

            if (imagePath != null && !imagePath.isEmpty()) {
                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
                lblImagePreview.setIcon(new ImageIcon(img));
            } else {
                lblImagePreview.setIcon(null);
            }

            JOptionPane.showMessageDialog(this, "✔ Tải dữ liệu thành công!");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Lỗi tải dữ liệu!");
        }
    }

    // ==== CẬP NHẬT SÁCH ====
    private void updateBook() {

        String ma = txtMaSach.getText().trim();
        String ten = txtTenSach.getText().trim();
        String tg = txtTacGia.getText().trim();
        String nam = txtNamXB.getText().trim();
        String tl = (String) cbTheLoai.getSelectedItem();

        if (ten.isEmpty() || tg.isEmpty() || nam.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập đầy đủ dữ liệu!");
            return;
        }

        try (Connection conn = MySQLConnection.getConnection()) {

            String sql = "UPDATE sach SET ten_sach=?, tac_gia=?, nam_xuat_ban=?, the_loai=?, hinhAnh=? WHERE ma_sach=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, ten);
            ps.setString(2, tg);
            ps.setString(3, nam);
            ps.setString(4, tl);
            ps.setString(5, imagePath);
            ps.setString(6, ma);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "✔ Cập nhật thành công!");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Lỗi cập nhật dữ liệu!");
        }
    }

    public static void main(String[] args) {
        new EditBookForm();
    }
}
