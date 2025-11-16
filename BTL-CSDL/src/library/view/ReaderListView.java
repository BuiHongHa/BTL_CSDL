package library.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReaderListView extends JFrame {

    private JTextField txtSearch;
    private JButton btnSearch, btnReload, btnExport;
    private JTable tblReaders;
    private JComboBox<String> cboFilter;
    private DefaultTableModel tableModel;

    public ReaderListView() {

        setTitle("Tra Cứu & Báo Cáo Người Đọc");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        // ---- Tìm kiếm ----
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Tìm (Họ tên):"), gbc);

        txtSearch = new JTextField(15);
        gbc.gridx = 1;
        topPanel.add(txtSearch, gbc);

        btnSearch = new JButton("Tìm");
        gbc.gridx = 2;
        topPanel.add(btnSearch, gbc);

        // ---- Filter lớp/bộ môn ----
        gbc.gridx = 3;
        topPanel.add(new JLabel("Lọc theo Lớp/Bộ môn:"), gbc);

        cboFilter = new JComboBox<>();
        cboFilter.addItem("Tất cả");
        gbc.gridx = 4;
        topPanel.add(cboFilter, gbc);

        btnReload = new JButton("Tải lại toàn bộ");
        gbc.gridx = 5;
        topPanel.add(btnReload, gbc);

        // ---- Table ----
        tableModel = new DefaultTableModel();
        tblReaders = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(tblReaders);

        // ---- Button Export ----
        btnExport = new JButton("Xuất Báo Cáo");
        JPanel bottom = new JPanel();
        bottom.add(btnExport);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(scroll, BorderLayout.CENTER);
        getContentPane().add(bottom, BorderLayout.SOUTH);
    }

    public JTextField getTxtSearch() { return txtSearch; }
    public JButton getBtnSearch() { return btnSearch; }
    public JButton getBtnReload() { return btnReload; }
    public JButton getBtnExport() { return btnExport; }
    public JTable getTblReaders() { return tblReaders; }
    public JComboBox<String> getCboFilter() { return cboFilter; }
    public DefaultTableModel getTableModel() { return tableModel; }
}
