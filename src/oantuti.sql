-- Xóa database cũ (cẩn thận sẽ xóa hết dữ liệu!)
DROP DATABASE IF EXISTS oantuti_game;

-- Tạo database mới
CREATE DATABASE oantuti_game CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE oantuti_game;

-- ========================
-- Bảng người dùng
-- ========================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    balance INT DEFAULT 0,
    total_win INT DEFAULT 0,
    total_lose INT DEFAULT 0,
    rank_point INT DEFAULT 0
);

-- ========================
-- Bảng phòng chơi
-- ========================
CREATE TABLE rooms (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_name VARCHAR(50) NOT NULL,
    bet_amount BIGINT NOT NULL,
    status ENUM('WAITING','PLAYING','FULL') DEFAULT 'WAITING',
    current_players INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ========================
-- Bảng lịch sử trận đấu
-- ========================
CREATE TABLE match_history (
    match_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    opponent VARCHAR(50) NOT NULL,
    result ENUM('WIN','LOSE','DRAW') NOT NULL,
    bet_amount BIGINT DEFAULT 0,
    played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)  -- sửa lại ở đây
) ENGINE=InnoDB;

-- ========================
-- Bảng yêu cầu nạp tiền
-- ========================
CREATE TABLE recharge_requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    amount BIGINT NOT NULL,
    status ENUM('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) -- sửa lại ở đây
) ENGINE=InnoDB;

-- ========================
-- Bảng xếp hạng
-- ========================
CREATE TABLE ranking (
    rank_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    rank_point INT NOT NULL,
    last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) -- sửa lại ở đây
) ENGINE=InnoDB;

-- ========================
-- Thêm admin mặc định
INSERT INTO users (username, password, balance, total_win, total_lose, rank_point)
VALUES ('admin', 'admin', 999999, 0, 0, 1000)
ON DUPLICATE KEY UPDATE username=username;

-- Đồng bộ xếp hạng ban đầu
INSERT INTO ranking (user_id, rank_point)
SELECT id, rank_point FROM users;
