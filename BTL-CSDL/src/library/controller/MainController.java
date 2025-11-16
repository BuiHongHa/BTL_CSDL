package library.controller;

import library.dao.LoanDAO;
import library.dao.ReaderDAO;
import library.model.Book;
import library.model.Reader;
import library.view.MainView;
import library.view.ReportView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class MainController {

    private final MainView view;
    private final LoanDAO loanDAO;
    private final ReaderDAO readerDAO;

    public MainController(MainView view) {
        this.view = view;
        this.loanDAO = new LoanDAO();
        this.readerDAO = new ReaderDAO();

        initListeners();
        loadAllReaders();   // Load toàn bộ người đọc khi mở
    }

    private void initListeners() {
        view.getBtnXemSach().addActionListener(e -> loadBooksOfReader());
        view.getBtnXemNguoiDoc().addActionListener(e -> loadReadersByDate());
        view.getBtnReload().addActionListener(e -> loadAllReaders());
        view.getBtnExportReport().addActionListener(e -> openReportWindow());
    }

    // ============================================================
    // 1) LOAD TOÀN BỘ NGƯỜI ĐỌC
    // ============================================================
    private void loadAllReaders() {
        try {
            List<Reader> list = readerDAO.getAllReaders();

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

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view,
                    "SQL Error: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============================================================
    // 2) XEM SÁCH NGƯỜI ĐỌC ĐÃ MƯỢN
    // ============================================================
    private void loadBooksOfReader() {
        String maND = view.getTxtMaNguoiDoc().getText().trim();

        if (maND.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập mã người đọc!");
            return;
        }

        try {
            List<Book> books = loanDAO.getBooksByReader(maND);

            DefaultTableModel model = view.getTableModel();
            model.setRowCount(0);

            model.setColumnIdentifiers(new Object[]{
                    "Mã sách", "Tên sách", "Tác giả", "Thể loại", "Năm XB"
            });

            for (Book b : books) {
                model.addRow(new Object[]{
                        b.getMaSach(),
                        b.getTenSach(),
                        b.getTacGia(),
                        b.getTheLoai(),
                        b.getNamXuatBan()
                });
            }

            if (books.isEmpty()) {
                JOptionPane.showMessageDialog(view,
                        "Không tìm thấy sách đã mượn của người này.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view,
                    "Lỗi khi tải dữ liệu sách: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============================================================
    // 3) LỌC NGƯỜI MƯỢN THEO KHOẢNG NGÀY (TỪ 6 COMBOBOX)
    // ============================================================
    private void loadReadersByDate() {

        String fromStr = view.getFromYear().getSelectedItem() + "-" +
                view.getFromMonth().getSelectedItem() + "-" +
                view.getFromDay().getSelectedItem();

        String toStr = view.getToYear().getSelectedItem() + "-" +
                view.getToMonth().getSelectedItem() + "-" +
                view.getToDay().getSelectedItem();

        try {
            Date from = Date.valueOf(fromStr);
            Date to = Date.valueOf(toStr);

            List<Reader> readers = readerDAO.getReadersByBorrowDateRange(from, to);

            DefaultTableModel model = view.getTableModel();
            model.setRowCount(0);

            model.setColumnIdentifiers(new Object[]{
                    "Mã bạn đọc", "Họ tên", "Lớp/Bộ môn", "Địa chỉ", "SĐT"
            });

            for (Reader r : readers) {
                model.addRow(new Object[]{
                        r.getMaNguoiDoc(),
                        r.getHoTen(),
                        r.getDonVi(),
                        r.getDiaChi(),
                        r.getSoDienThoai()
                });
            }

            if (readers.isEmpty()) {
                JOptionPane.showMessageDialog(view,
                        "Không có người đọc nào trong khoảng thời gian này.");
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(view, "Lỗi ngày tháng! Vui lòng kiểm tra lại.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view,
                    "Lỗi SQL: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============================================================
    // 4) MỞ CỬA SỔ XEM TRƯỚC BÁO CÁO
    // ============================================================
    private void openReportWindow() {

        DefaultTableModel model = view.getTableModel();

        String footer = "Tổng số dòng: " + model.getRowCount();
        String creator = "Bùi Quang Minh";

        new ReportView(
                "BÁO CÁO TỒN KHO SÁCH",
                cloneTableModel(model),
                footer,
                creator
        ).setVisible(true);
    }

    // ============================================================
    // COPY TABLE MODEL (không mất dữ liệu)
    // ============================================================
    private DefaultTableModel cloneTableModel(DefaultTableModel original) {
        DefaultTableModel copy = new DefaultTableModel();

        for (int i = 0; i < original.getColumnCount(); i++) {
            copy.addColumn(original.getColumnName(i));
        }

        for (int r = 0; r < original.getRowCount(); r++) {
            Object[] row = new Object[original.getColumnCount()];
            for (int c = 0; c < original.getColumnCount(); c++) {
                row[c] = original.getValueAt(r, c);
            }
            copy.addRow(row);
        }

        return copy;
    }
}
