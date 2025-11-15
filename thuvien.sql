-- 6.1 Tạo CSDL
CREATE DATABASE IF NOT EXISTS thu_vien
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE thu_vien;

-- 6.2 Bảng gốc
CREATE TABLE NhanVien (
  ma_nhan_vien VARCHAR(10) PRIMARY KEY,
  ho_ten VARCHAR(100) NOT NULL,
  chuc_vu VARCHAR(50),
  so_dien_thoai VARCHAR(15)
) ENGINE=InnoDB;

CREATE TABLE NguoiDoc (
  ma_nguoi_doc VARCHAR(10) PRIMARY KEY,
  ho_ten VARCHAR(100) NOT NULL,
  don_vi VARCHAR(100),
  dia_chi VARCHAR(200),
  so_dien_thoai VARCHAR(15)
) ENGINE=InnoDB;

CREATE TABLE Sach (
  ma_sach VARCHAR(10) PRIMARY KEY,
  ten_sach VARCHAR(100) NOT NULL,
  tac_gia VARCHAR(100),
  the_loai VARCHAR(50),
  nam_xuat_ban INT,
  hinhAnh TEXT
) ENGINE=InnoDB;

-- 6.3 Bảng phụ thuộc
CREATE TABLE PhieuMuon (
  ma_phieu_muon VARCHAR(10) PRIMARY KEY,
  ma_nguoi_doc VARCHAR(10) NOT NULL,
  ma_nhan_vien VARCHAR(10) NOT NULL,
  ngay_muon DATE NOT NULL,
  ngay_tra DATE,
  CONSTRAINT fk_pm_nd FOREIGN KEY (ma_nguoi_doc)
    REFERENCES NguoiDoc(ma_nguoi_doc)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_pm_nv FOREIGN KEY (ma_nhan_vien)
    REFERENCES NhanVien(ma_nhan_vien)
    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE ChiTietMuon (
  ma_phieu_muon VARCHAR(10) NOT NULL,
  ma_sach VARCHAR(10) NOT NULL,
  ngay_muon DATE,
  tinh_trang_sach VARCHAR(100),
  PRIMARY KEY (ma_phieu_muon, ma_sach),
  CONSTRAINT fk_ctm_pm FOREIGN KEY (ma_phieu_muon)
    REFERENCES PhieuMuon(ma_phieu_muon)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_ctm_sach FOREIGN KEY (ma_sach)
    REFERENCES Sach(ma_sach)
    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE PhieuPhat (
  ma_phieu_phat VARCHAR(10) PRIMARY KEY,
  ma_nguoi_doc VARCHAR(10) NOT NULL,
  ma_nhan_vien VARCHAR(10),
  ngay_lap_phieu DATE,
  ly_do VARCHAR(255),
  CONSTRAINT fk_pp_nd FOREIGN KEY (ma_nguoi_doc)
    REFERENCES NguoiDoc(ma_nguoi_doc)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_pp_nv FOREIGN KEY (ma_nhan_vien)
    REFERENCES NhanVien(ma_nhan_vien)
    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE Checkin (
    ma_gui_yeu_cau VARCHAR(10) PRIMARY KEY,
    ma_nguoi_doc VARCHAR(10) NOT NULL,
    ngay_yeu_cau DATE,
    ly_do VARCHAR(255),
    CONSTRAINT fk_checkin_nguoidoc FOREIGN KEY (ma_nguoi_doc)
        REFERENCES NguoiDoc(ma_nguoi_doc)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 6.4 Chỉ mục gợi ý (tăng tốc tra cứu phổ biến)
CREATE INDEX idx_pm_ma_nguoi_doc ON PhieuMuon(ma_nguoi_doc);
CREATE INDEX idx_ctm_ma_sach ON ChiTietMuon(ma_sach);
CREATE INDEX idx_pp_ma_nguoi_doc ON PhieuPhat(ma_nguoi_doc);

