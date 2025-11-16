package library.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MainView extends JFrame {

    private JComboBox<String> fromDay, fromMonth, fromYear;
    private JComboBox<String> toDay, toMonth, toYear;

    private JButton btnExportReport;
    public JButton getBtnExportReport() { return btnExportReport; }


    private JButton btnReload;
    public JButton getBtnReload() { return btnReload; }

    private JTextField txtMaNguoiDoc;
    private JButton btnXemSach;
    private JButton btnXemNguoiDoc;

    private JTable tblResult;
    private DefaultTableModel tableModel;

    public MainView() {
        initUI();
    }

    private void initUI() {
        setTitle("Quản lý thư viện - Demo MVC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 600);
        setLocationRelativeTo(null);

        // PANEL CHA: BoxLayout dọc
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        // ===== HÀNG 1 =====
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        row1.add(new JLabel("Mã người đọc:"));

        txtMaNguoiDoc = new JTextField(12);
        row1.add(txtMaNguoiDoc);

        btnXemSach = new JButton("Xem sách đã mượn");
        row1.add(btnXemSach);

        btnReload = new JButton("Tải lại toàn bộ");
        row1.add(btnReload);

        top.add(row1);

        // ===== TẠO COMBO NGÀY =====
        fromDay = new JComboBox<>();
        fromMonth = new JComboBox<>();
        fromYear = new JComboBox<>();
        toDay = new JComboBox<>();
        toMonth = new JComboBox<>();
        toYear = new JComboBox<>();

        for (int d = 1; d <= 31; d++) {
            fromDay.addItem(String.format("%02d", d));
            toDay.addItem(String.format("%02d", d));
        }
        for (int m = 1; m <= 12; m++) {
            fromMonth.addItem(String.format("%02d", m));
            toMonth.addItem(String.format("%02d", m));
        }
        for (int y = 2000; y <= 2030; y++) {
            fromYear.addItem(String.valueOf(y));
            toYear.addItem(String.valueOf(y));
        }

        // ===== HÀNG 2 =====
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        row2.add(new JLabel("Từ ngày:"));
        row2.add(fromDay);
        row2.add(fromMonth);
        row2.add(fromYear);

        row2.add(new JLabel("Đến ngày:"));
        row2.add(toDay);
        row2.add(toMonth);
        row2.add(toYear);

        btnXemNguoiDoc = new JButton("Xem người mượn");
        row2.add(btnXemNguoiDoc);

        top.add(row2);

        // ===== BẢNG =====
        tableModel = new DefaultTableModel();
        tblResult = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(tblResult);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(top, BorderLayout.NORTH);
        getContentPane().add(scroll, BorderLayout.CENTER);

        btnExportReport = new JButton("Xuất báo cáo");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnExportReport);

        getContentPane().add(bottom, BorderLayout.SOUTH);

    }

    // GETTER
    public JTextField getTxtMaNguoiDoc() { return txtMaNguoiDoc; }
    public JButton getBtnXemSach() { return btnXemSach; }
    public JButton getBtnXemNguoiDoc() { return btnXemNguoiDoc; }
    public JTable getTblResult() { return tblResult; }
    public DefaultTableModel getTableModel() { return tableModel; }

    public JComboBox<String> getFromDay() { return fromDay; }
    public JComboBox<String> getFromMonth() { return fromMonth; }
    public JComboBox<String> getFromYear() { return fromYear; }

    public JComboBox<String> getToDay() { return toDay; }
    public JComboBox<String> getToMonth() { return toMonth; }
    public JComboBox<String> getToYear() { return toYear; }
}
