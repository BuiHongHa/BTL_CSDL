package library.controller;

import library.dao.ReaderDAO;
import library.model.Reader;
import library.view.ReaderListView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ReaderListController {

    private final ReaderListView view;
    private final ReaderDAO readerDAO;

    public ReaderListController(ReaderListView view) {
        this.view = view;
        this.readerDAO = new ReaderDAO();
        init();
    }

    private void init() {
        loadAllReaders();
        loadFilterOptions();

        view.getBtnSearch().addActionListener(e -> searchReaders());
        view.getBtnReload().addActionListener(e -> loadAllReaders());
    }

    private void loadAllReaders() {
        try {
            List<Reader> list = readerDAO.getAllReaders();
            fillTable(list);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi tải dữ liệu");
        }
    }

    private void loadFilterOptions() {
        try {
            List<String> units = readerDAO.getAllUnits();
            for (String u : units)
                view.getCboFilter().addItem(u);
        } catch (Exception e) {}
    }

    private void searchReaders() {
        String keyword = view.getTxtSearch().getText().trim();
        String filter = (String) view.getCboFilter().getSelectedItem();

        try {
            List<Reader> list;

            if (filter.equals("Tất cả"))
                list = readerDAO.searchReaders(keyword);
            else
                list = readerDAO.searchReadersWithFilter(keyword, filter);

            fillTable(list);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi tìm kiếm");
        }
    }

    private void fillTable(List<Reader> list) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        model.setColumnIdentifiers(new Object[]{
                "Mã bạn đọc", "Họ tên", "Lớp/Bộ môn", "Địa chỉ", "SĐT"
        });

        for (Reader r : list) {
            model.addRow(new Object[]{
                    r.getMaNguoiDoc(),
                    r.getHoTen(),
                    r.getDonVi(),
                    r.getDiaChi(),
                    r.getSoDienThoai()
            });
        }
    }
}
