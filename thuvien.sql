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
  CONSTRAINT fk_pp_pm FOREIGN KEY (ma_phieu_muon)
      REFERENCES phieumuon(ma_phieu_muon)
      ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_pp_sach FOREIGN KEY (ma_sach)
      REFERENCES sach(ma_sach)
      ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_pp_nd FOREIGN KEY (ma_nguoi_doc)
      REFERENCES nguoidoc(ma_nguoi_doc)
      ON UPDATE CASCADE ON DELETE CASCADE 
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
INSERT INTO nhanvien VALUES
('NV001', 'Nguyễn Văn A', 'Thủ thư', '0901110001'),
('NV002', 'Trần Thị B', 'Thủ thư', '0902220002'),
('NV003', 'Lê Văn C', 'Thủ kho', '0903330003'),
('NV004', 'Phạm Thị D', 'Thủ thư', '0904440004'),
('NV005', 'Đặng Văn E', 'Thủ kho', '0905550005'),
('NV006', 'Võ Thị F', 'Thủ thư', '0906660006'),
('NV007', 'Hoàng Văn G', 'Quản lý', '0907770007'),
('NV008', 'Đỗ Thị H', 'Thủ thư', '0908880008'),
('NV009', 'Huỳnh Văn I', 'Thủ kho', '0909990009'),
('NV010', 'Bùi Thị J', 'Thủ thư', '0910000010');

-- ==================== INSERT NGƯỜI ĐỌC ====================
INSERT INTO nguoidoc VALUES
('ND001', 'Nguyễn Anh', 'CNTT', 'Hà Nội', '0987000001'),
('ND002', 'Trần Bình', 'Kế toán', 'Hà Nội', '0987000002'),
('ND003', 'Lê Chi', 'CNTT', 'Nghệ An', '0987000003'),
('ND004', 'Phạm Dũng', 'Điện tử', 'Đà Nẵng', '0987000004'),
('ND005', 'Đặng Ê', 'Du lịch', 'Cần Thơ', '0987000005'),
('ND006', 'Võ Phú', 'CNTT', 'TP.HCM', '0987000006'),
('ND007', 'Hoàng Gia', 'Y dược', 'Huế', '0987000007'),
('ND008', 'Đỗ Hiếu', 'Xây dựng', 'Hải Phòng', '0987000008'),
('ND009', 'Huỳnh K', 'CNTT', 'Thanh Hóa', '0987000009'),
('ND010', 'Bùi Lan', 'Thiết kế', 'Lào Cai', '0987000010');
INSERT INTO sach VALUES
('S001', 'Lập trình C++', 'Bjarne Stroustrup', 'CNTT', 2013),
('S002', 'Python cơ bản', 'Mark Lutz', 'CNTT', 2019),
('S003', 'Hóa đại cương', 'Nguyễn Văn H', 'Hóa học', 2012),
('S004', 'Toán cao cấp', 'Nguyễn Thị M', 'Toán học', 2015),
('S005', 'Tự học Guitar', 'Phạm Nhật', 'Năng khiếu', 2011),
('S006', 'Kinh tế vi mô', 'Trần Minh', 'Kinh tế', 2017),
('S007', 'Lịch sử Việt Nam', 'Lê Thành', 'Lịch sử', 2014),
('S008', 'Nguyên lý kế toán', 'Nguyễn Anh', 'Kế toán', 2016),
('S009', 'Tiếng Anh giao tiếp', 'John Smith', 'Ngoại ngữ', 2020),
('S010', 'Cơ sở dữ liệu', 'Elmasri', 'CNTT', 2018);

INSERT INTO phieumuon  VALUES
('PM001','ND001','NV001','2025-12-01','2025-12-05'),
('PM002','ND002','NV003','2025-12-02','2025-12-07'),
('PM003','ND003','NV001','2025-12-03','2025-12-08'),
('PM004','ND004','NV006','2025-12-04','2025-12-09'),
('PM005','ND005','NV004','2025-12-05','2025-12-10'),
('PM006','ND006','NV002','2025-12-06','2025-12-15'),
('PM007','ND007','NV007','2025-12-07','2025-12-12'),
('PM008','ND008','NV005','2025-12-08','2025-12-13'),
('PM009','ND009','NV003','2025-12-09','2025-12-20'),
('PM010','ND010','NV008','2025-12-10','2025-12-14');

-- ==================== INSERT CHI TIẾT MƯỢN ====================
INSERT INTO chitietmuon VALUES
('PM001','S001','2025-12-01','Viết vào sách'),  
('PM001','S009','2025-12-01','Đang mượn'),      
('PM002','S003','2025-12-02','Trả sách trễ'),   
('PM003','S002','2025-12-03','Trả trễ'),        
('PM003','S010','2025-12-03','Đang mượn'),      
('PM004','S010','2025-12-04','Làm hỏng sách'),  
('PM005','S005','2025-12-05','Trả trễ'),       
('PM005','S006','2025-12-05','Trả trễ'),        
('PM006','S008','2025-12-06','Trả trễ'),        
('PM007','S007','2025-12-07','Đang mượn'),
('PM007','S004','2025-12-07','Hỏng giấy'),      
('PM008','S004','2025-12-08','Dính nước'),      
('PM009','S003','2025-12-09','Mất sách'),      
('PM009','S010','2025-12-09','Đã trả'),         
('PM010','S006','2025-12-10','Đang mượn');      


INSERT INTO phieuphat  VALUES
('PP001','PM001','S001','ND001','Viết vào sách', 25000, '2025-12-03'),
('PP002','PM002','S003','ND002','Trả sách trễ', 20000, '2025-12-07'),
('PP003','PM003','S002','ND003','Trả trễ', 15000, '2025-12-06'),
('PP004','PM004','S010','ND004','Làm hỏng sách', 50000, '2025-12-09'),
('PP005','PM005','S005','ND005','Trả trễ', 10000, '2025-12-18'),
('PP006','PM005','S006','ND005','Trả trễ', 10000, '2025-12-19'),
('PP007','PM006','S008','ND006','Trả trễ', 15000, '2025-12-15'),
('PP008','PM007','S004','ND007','Hỏng giấy', 45000, '2025-12-15'),
('PP009','PM008','S004','ND008','Dính nước', 35000, '2025-12-18'),
('PP010','PM009','S003','ND009','Mất sách', 100000, '2025-12-20');
INSERT INTO checkin VALUES
('CI001','ND001','2025-12-01','Tham khảo tài liệu'),
('CI002','ND002','2025-12-02','Mượn sách chuyên ngành'),
('CI003','ND003','2025-12-03','Xem tài liệu CNTT'),
('CI004','ND004','2025-12-04','Học nhóm'),
('CI005','ND005','2025-12-05','Mượn sách du lịch'),
('CI006','ND006','2025-12-06','Tham khảo'),
('CI007','ND007','2025-12-07','Ôn thi'),
('CI008','ND008','2025-12-08','Tìm tài liệu'),
('CI009','ND009','2025-12-09','Tra cứu'),
('CI010','ND010','2025-12-10','Nghiên cứu đồ án');
