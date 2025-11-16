package SQL;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;

public class CheckinForm extends JFrame {
    private JTextField txtMaPhieu, txtMaNguoiDoc;
    private JTextArea txtLyDo;

    public CheckinForm() {
        setTitle("Gửi yêu cầu Check-in");
        setSize(520, 360);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel header = new JLabel("GỬI YÊU CẦU CHECK-IN", SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(0,102,204));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(header, BorderLayout.NORTH);

        JPanel p = new JPanel(new GridLayout(4,1,6,6));
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        p.setBackground(new Color(240,244,247));

        p.add(new JLabel("Mã phiếu gửi"));
        txtMaPhieu = new JTextField();
        p.add(txtMaPhieu);

        p.add(new JLabel("Mã người đọc"));
        txtMaNguoiDoc = new JTextField();
        p.add(txtMaNguoiDoc);

        p.add(new JLabel("Lý do"));
        txtLyDo = new JTextArea(4,20);
        JScrollPane sp = new JScrollPane(txtLyDo);
        p.add(sp);

        add(p, BorderLayout.CENTER);

        JButton btnSend = new JButton("Gửi yêu cầu");
        btnSend.setBackground(new Color(0,102,204));
        btnSend.setForeground(Color.WHITE);
        btnSend.addActionListener(e -> sendRequest()); // fix lỗi lambda
        add(btnSend, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void sendRequest() {
        String mp = txtMaPhieu.getText().trim();
        String md = txtMaNguoiDoc.getText().trim();
        String ly = txtLyDo.getText().trim();


        if (mp.isEmpty() || md.isEmpty() || ly.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO checkin (ma_phieu_gui, ma_nguoi_doc, ngay_yeu_cau, ly_do) VALUES (?, ?, ?, ?)")) {

            ps.setString(1, mp);
            ps.setString(2, md);
            ps.setDate(3, new Date(System.currentTimeMillis())); // ngày hiện tại
            ps.setString(4, ly);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Gửi yêu cầu thành công.");
            this.dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi gửi yêu cầu: " + ex.getMessage());
        }
    }
}
