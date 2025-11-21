package SQL;


import javax.swing.*;
import java.awt.*;

public class MainMenuStaff extends JFrame {
    public MainMenuStaff() {
        setTitle("Menu Quản Lý");
        setSize(360, 360);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 1, 10, 10));
        getContentPane().setBackground(new Color(240,244,247));

        JLabel header = new JLabel("MENU QUẢN LÝ", SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(0,102,204));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(header);

        JButton btnBook = new JButton("Quản lý sách");
        JButton btnNV = new JButton("Quản lý nhân viên");
        JButton btnMuon = new JButton("Tạo phiếu mượn");
        JButton btnPhat = new JButton("Tạo phiếu phạt");
        JButton btnExit = new JButton("Thoát");

        btnBook.addActionListener(e -> new BookManagementForm().setVisible(true));
        btnNV.addActionListener(e -> new StaffForm().setVisible(true));
        btnMuon.addActionListener(e -> new BorrowSlipForm().setVisible(true));
        btnPhat.addActionListener(e -> new PenaltyForm().setVisible(true));
        btnExit.addActionListener(e -> System.exit(0));

        add(btnBook);
        add(btnNV);
        add(btnMuon);
        add(btnPhat);
        add(btnExit);
    }
}
