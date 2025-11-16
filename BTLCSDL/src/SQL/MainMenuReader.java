package SQL;


import javax.swing.*;
import java.awt.*;

public class MainMenuReader extends JFrame {
    public MainMenuReader() {
        setTitle("Menu Người Dùng");
        setSize(320, 260);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 1, 10, 10));
        getContentPane().setBackground(new Color(240,244,247));

        JLabel header = new JLabel("MENU NGƯỜI DÙNG", SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(0,102,204));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(header);

        JButton btnAcc = new JButton("Tạo tài khoản");
        JButton btnCheck = new JButton("Yêu cầu check-in");
        JButton btnExit = new JButton("Thoát");

        btnAcc.addActionListener(e -> new ReaderAccountForm().setVisible(true));
        btnCheck.addActionListener(e -> new CheckinForm().setVisible(true));
        btnExit.addActionListener(e -> System.exit(0));

        add(btnAcc);
        add(btnCheck);
        add(btnExit);
    }
}
