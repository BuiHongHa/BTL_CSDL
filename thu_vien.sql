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
SELECT
    pm.ma_nguoi_doc,
    ctm.ma_sach,
    s.ten_sach,
    ctm.tinh_trang_sach
FROM
    phieumuon pm
JOIN
    chitietmuon ctm ON pm.ma_phieu_muon = ctm.ma_phieu_muon
JOIN
    sach s ON ctm.ma_sach = s.ma_sach
WHERE
    pm.ma_phieu_muon = 'PM_USER_INPUT';
