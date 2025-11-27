package view.input;

import view.components.MenuTemplate;
import javax.swing.*;
import java.awt.*;

public class MainMenuStaff extends MenuTemplate {

    public MainMenuStaff() {
        super("NHẬP DỮ LIỆU - QUẢN LÝ");

        // ========= Tạo nút =========
        JButton btnReader = createMenuButton("QUẢN LÝ NGƯỜI ĐỌC", new ImageIcon("icons/user.png"));
        JButton btnBook   = createMenuButton("QUẢN LÝ SÁCH", new ImageIcon("icons/book.png"));
        JButton btnMuon   = createMenuButton("QUẢN LÝ PHIẾU MƯỢN", new ImageIcon("icons/borrow.png"));
        JButton btnPhat   = createMenuButton("QUẢN LÝ PHIẾU PHẠT", new ImageIcon("icons/warn.png"));
        JButton btnCheck  = createMenuButton("TRA CỨU CHECK-IN", new ImageIcon("icons/search.png"));
        JButton btnStaff  = createMenuButton("QUẢN LÝ NHÂN VIÊN", new ImageIcon("icons/staff.png"));

        // ========= Gán sự kiện =========
        btnReader.addActionListener(e -> new ReaderForm().setVisible(true));
        btnBook.addActionListener(e -> new BookManagementForm().setVisible(true));
        btnMuon.addActionListener(e -> new BorrowSlipForm().setVisible(true));
        btnPhat.addActionListener(e -> new PenaltyForm().setVisible(true));
        btnCheck.addActionListener(e -> new CheckinForm().setVisible(true));
        btnStaff.addActionListener(e -> new StaffForm().setVisible(true));

        // ========= Thêm vào layout =========
        contentPanel.add(btnReader);
        contentPanel.add(btnBook);
        contentPanel.add(btnMuon);
        contentPanel.add(btnPhat);
        contentPanel.add(btnCheck);
        contentPanel.add(btnStaff);
    }
}
