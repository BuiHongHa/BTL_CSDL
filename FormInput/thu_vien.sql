DROP DATABASE IF EXISTS thu_vien;
CREATE DATABASE thu_vien CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE thu_vien;

-- Bảng Nhân Viên (Giữ nguyên)
CREATE TABLE nhanvien (
  ma_nhan_vien VARCHAR(10) PRIMARY KEY,
  ho_ten VARCHAR(100) NOT NULL,
  chuc_vu VARCHAR(50),
  so_dien_thoai VARCHAR(15)
) ENGINE=InnoDB;

-- Bảng Người Đọc (Giữ nguyên - Bảng cha)
CREATE TABLE nguoidoc ( 
  ma_nguoi_doc VARCHAR(10) PRIMARY KEY,
  ho_ten VARCHAR(100) NOT NULL,
  don_vi VARCHAR(100),
  dia_chi VARCHAR(200),
  so_dien_thoai VARCHAR(15)
) ENGINE=InnoDB;

-- Bảng Sách (Giữ nguyên - Bảng cha)
CREATE TABLE sach (
  ma_sach VARCHAR(10) PRIMARY KEY,
  ten_sach VARCHAR(100) NOT NULL,
  tac_gia VARCHAR(100),
  the_loai VARCHAR(50),
  nam_xuat_ban INT
) ENGINE=InnoDB;

-- Bảng Phiếu Mượn
CREATE TABLE phieumuon ( 
  ma_phieu_muon VARCHAR(10) PRIMARY KEY,
  ma_nguoi_doc VARCHAR(10) NOT NULL,
  ma_nhan_vien VARCHAR(10) NOT NULL,
  ngay_muon DATE NOT NULL,
  ngay_tra DATE,
  -- ĐÃ SỬA: ON DELETE CASCADE (Nếu xóa người đọc, xóa luôn phiếu mượn)
  CONSTRAINT fk_pm_nd FOREIGN KEY (ma_nguoi_doc)
      REFERENCES nguoidoc(ma_nguoi_doc)
      ON UPDATE CASCADE ON DELETE CASCADE, 
  CONSTRAINT fk_pm_nv FOREIGN KEY (ma_nhan_vien)
      REFERENCES nhanvien(ma_nhan_vien)
      ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Bảng Chi Tiết Mượn
CREATE TABLE chitietmuon ( 
  ma_phieu_muon VARCHAR(10) NOT NULL,
  ma_sach VARCHAR(10) NOT NULL,
  ngay_muon DATE,
  tinh_trang_sach VARCHAR(100),
  PRIMARY KEY (ma_phieu_muon, ma_sach),
  -- ĐÃ CÓ: ON DELETE CASCADE (Nếu xóa phiếu mượn, xóa chi tiết mượn)
  CONSTRAINT fk_ctm_pm FOREIGN KEY (ma_phieu_muon)
      REFERENCES phieumuon(ma_phieu_muon)
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_ctm_sach FOREIGN KEY (ma_sach)
      REFERENCES sach(ma_sach)
      ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Bảng Phiếu Phạt
CREATE TABLE phieuphat ( 
  ma_phieu_phat VARCHAR(10) PRIMARY KEY,
  ma_phieu_muon VARCHAR(10) NOT NULL,
  ma_sach VARCHAR(10) NOT NULL,
  ma_nguoi_doc VARCHAR(10) NOT NULL,
  ly_do_phat VARCHAR(255),
  so_tien_phat DOUBLE,
  ngay_lap DATETIME,
  -- ĐÃ SỬA: ON DELETE CASCADE (Nếu xóa người đọc, xóa luôn phiếu phạt)
  CONSTRAINT fk_pp_pm FOREIGN KEY (ma_phieu_muon)
      REFERENCES phieumuon(ma_phieu_muon)
      ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_pp_sach FOREIGN KEY (ma_sach)
      REFERENCES sach(ma_sach)
      ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_pp_nd FOREIGN KEY (ma_nguoi_doc)
      REFERENCES nguoidoc(ma_nguoi_doc)
      ON UPDATE CASCADE ON DELETE CASCADE -- SỬA THÀNH CASCADE
) ENGINE=InnoDB;

-- Bảng Check-in
CREATE TABLE checkin (
    ma_phieu_gui VARCHAR(10) PRIMARY KEY,
    ma_nguoi_doc VARCHAR(10) NOT NULL,
    ngay_yeu_cau DATE,
    ly_do VARCHAR(255),
    -- ĐÃ SỬA: ON DELETE CASCADE (Nếu xóa người đọc, xóa luôn check-in)
    CONSTRAINT fk_checkin_nguoidoc FOREIGN KEY (ma_nguoi_doc)
        REFERENCES nguoidoc(ma_nguoi_doc)
        ON UPDATE CASCADE ON DELETE CASCADE -- SỬA THÀNH CASCADE
);

-- Index (Giữ nguyên)
CREATE INDEX idx_pm_ma_nguoi_doc ON phieumuon(ma_nguoi_doc);
CREATE INDEX idx_ctm_ma_sach ON chitietmuon(ma_sach);
CREATE INDEX idx_pp_ma_nguoi_doc ON phieuphat(ma_nguoi_doc);
INSERT INTO nhanvien (ma_nhan_vien, ho_ten, chuc_vu, so_dien_thoai) VALUES
('NV001', 'Nguyễn Văn A', 'Thủ kho', '0901123456'),
('NV002', 'Lê Thị B', 'Quản lý', '0912234567'),
('NV003', 'Trần Minh C', 'Nhân viên trực', '0983345678');

-- 2. Bảng nguoidoc (Người Đọc)
INSERT INTO nguoidoc (ma_nguoi_doc, ho_ten, don_vi, dia_chi, so_dien_thoai) VALUES
('ND001', 'Phạm Hoàng D', 'Khoa CNTT', '123 Đường Sáng, Quận 1', '0774455666'),
('ND002', 'Võ Thanh E', 'Khoa Kinh Tế', '456 Phố Trăng, Quận 3', '0885566777'),
('ND003', 'Đinh Tuấn F', 'Viện Nghiên Cứu', '789 Ngõ Mây, Huyện X', '0996677888'),
('ND004', 'Hoàng Thu G', 'Khoa Luật', '101 Đại Lộ, Quận 10', '0337788999'),
('ND005', 'Bùi Văn H', 'Sinh viên', 'Ký túc xá khu A', '0551122333');

-- 3. Bảng sach (Sách)
INSERT INTO sach (ma_sach, ten_sach, tac_gia, the_loai, nam_xuat_ban) VALUES
('S001', 'Lập trình Python cơ bản', 'Nguyễn Tùng', 'Khoa học máy tính', 2022),
('S002', 'Lược sử thời gian', 'Stephen Hawking', 'Khoa học phổ thông', 1988),
('S003', 'Kinh tế học vi mô', 'Paul Samuelson', 'Kinh tế', 2018),
('S004', 'Đắc Nhân Tâm', 'Dale Carnegie', 'Kỹ năng sống', 1936),
('S005', 'Tâm lý học đám đông', 'Gustave Le Bon', 'Tâm lý học', 1895),
('S006', 'Dữ liệu lớn và Trí tuệ nhân tạo', 'Lê Văn Khải', 'Công nghệ', 2023),
('S007', 'Văn học Việt Nam hiện đại', 'Nhiều tác giả', 'Văn học', 2020);

-- 4. Bảng phieumuon (Phiếu Mượn)
-- PM001: ND001 mượn, Ngày mượn: 2025-11-01, Ngày trả: 2025-11-10 (Đã trả)
-- PM002: ND002 mượn, Ngày mượn: 2025-11-05, Ngày trả: NULL (Đang mượn)
-- PM003: ND004 mượn, Ngày mượn: 2025-10-20, Ngày trả: 2025-11-15 (Trả trễ)
INSERT INTO phieumuon (ma_phieu_muon, ma_nguoi_doc, ma_nhan_vien, ngay_muon, ngay_tra) VALUES
('PM001', 'ND001', 'NV003', '2025-11-01', '2025-11-10'),
('PM002', 'ND002', 'NV003', '2025-11-05', NULL),
('PM003', 'ND004', 'NV001', '2025-10-20', '2025-11-15');

-- 5. Bảng chitietmuon (Chi Tiết Mượn)
INSERT INTO chitietmuon (ma_phieu_muon, ma_sach, ngay_muon, tinh_trang_sach) VALUES
('PM001', 'S001', '2025-11-01', 'Đang mượn'),
('PM001', 'S002', '2025-11-01', 'Đang mượn'),
('PM002', 'S003', '2025-11-05', 'Đang mượn'), -- Sách đang mượn
('PM003', 'S004', '2025-10-20', 'Đang mượn'); -- Sách trả trễ + hỏng

-- 6. Bảng phieuphat (Phiếu Phạt)
-- Phạt cho PM003 (ND004) vì trả trễ S004 và làm hỏng sách
INSERT INTO phieuphat (ma_phieu_phat, ma_phieu_muon, ma_sach, ma_nguoi_doc, ly_do_phat, so_tien_phat, ngay_lap) VALUES
('PP001', 'PM003', 'S004', 'ND004', 'Trả sách trễ 5 ngày và sách bị rách bìa.', 50000, '2025-11-15 10:30:00');

-- 7. Bảng checkin (Phiếu Gửi/Yêu Cầu Check-in)
-- ND005 yêu cầu kiểm tra tình trạng sách
INSERT INTO checkin (ma_phieu_gui, ma_nguoi_doc, ngay_yeu_cau, ly_do) VALUES
('CI001', 'ND005', '2025-11-14', 'Yêu cầu kiểm tra sách S006 có sẵn không.'),
('CI002', 'ND003', '2025-11-12', 'Đăng ký nhận thông báo sách mới về chủ đề công nghệ.');
