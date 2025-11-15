package SQL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.sql.*;

public class AddBookForm extends JFrame {

    private JTextField txtMaSach, txtTenSach, txtTacGia, txtNamXB;
    private JComboBox<String> cbTheLoai;
    private JLabel lblImagePreview;
    private String imagePath = null;

    public AddBookForm() {
        setTitle("Thêm Sách Mới");
        setSize(650, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        Font fontLabel = new Font("Segoe UI", Font.PLAIN, 16);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 16);

        // Tạo mã sách tự động
        String generatedID = generateBookID();

        // ==== PANEL TRÁI ====
        JPanel leftPanel = new JPanel(new GridLayout(6, 2, 10, 20));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 10));

        JLabel lblMaSach = new JLabel("Mã sách:");
        JLabel lblTenSach = new JLabel("Tên sách:");
        JLabel lblTacGia = new JLabel("Tác giả:");
        JLabel lblNamXB = new JLabel("Năm xuất bản:");
        JLabel lblTheLoai = new JLabel("Thể loại:");
        JLabel lblHinhAnh = new JLabel("Hình ảnh:");

        for (JLabel lbl : new JLabel[]{lblMaSach, lblTenSach, lblTacGia, lblNamXB, lblTheLoai, lblHinhAnh}) {
            lbl.setFont(fontLabel);
        }

        txtMaSach = new JTextField(generatedID);
        txtMaSach.setEditable(false);
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

        JButton btnSave = new JButton("Lưu");
        JButton btnReset = new JButton("Thêm mới");

        btnSave.setFont(fontInput);
        btnReset.setFont(fontInput);

        btnSave.addActionListener(e -> saveBook());
        btnReset.addActionListener(e -> resetForm());

        bottomPanel.add(btnReset);
        bottomPanel.add(btnSave);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

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
    // 🔹 Generate MÃ SÁCH: SA001, SA002...
    private String generateBookID() {
        try (Connection conn = MySQLConnection.getConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT ma_sach FROM sach ORDER BY ma_sach DESC LIMIT 1");

            if (rs.next()) {
                String lastID = rs.getString(1).substring(2);
                int next = Integer.parseInt(lastID) + 1;
                return "SA" + String.format("%03d", next);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "SA001";
    }


    // 🔹 Lưu vào MySQL
    private void saveBook() {
        String ma = txtMaSach.getText().trim();
        String ten = txtTenSach.getText().trim();
        String tg = txtTacGia.getText().trim();
        String nam = txtNamXB.getText().trim();
        String tl = (String) cbTheLoai.getSelectedItem();

        if (ten.isEmpty() || tg.isEmpty() || nam.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try (Connection conn = MySQLConnection.getConnection()) {

            String sql = "INSERT INTO sach(ma_sach, ten_sach, tac_gia, nam_xuat_ban, the_loai, hinhAnh) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ma);
            ps.setString(2, ten);
            ps.setString(3, tg);
            ps.setString(4, nam);
            ps.setString(5, tl);
            ps.setString(6, imagePath);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "✔ Lưu thành công!");

            resetForm();
            txtMaSach.setText(generateBookID());

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Lỗi khi lưu dữ liệu!");
        }
    }


    // 🔹 Reset form
    private void resetForm() {
        txtTenSach.setText("");
        txtTacGia.setText("");
        txtNamXB.setText("");
        cbTheLoai.setSelectedIndex(0);
        lblImagePreview.setIcon(null);
        imagePath = null;
    }

    public static void main(String[] args) {
        new AddBookForm();
    }
}
