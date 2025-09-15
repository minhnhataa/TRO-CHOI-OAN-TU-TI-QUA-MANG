
CREATE DATABASE IF NOT EXISTS oantuti_game
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE oantuti_game;


DROP TABLE IF EXISTS GameMatch;
DROP TABLE IF EXISTS Room;
DROP TABLE IF EXISTS `Transaction`;
DROP TABLE IF EXISTS User;

-- Bảng User
CREATE TABLE User (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    balance BIGINT DEFAULT 0,
    total_win INT DEFAULT 0,
    total_lose INT DEFAULT 0,
    rank_point INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Bảng Transaction (lịch sử nạp tiền)
CREATE TABLE `Transaction` (
    trans_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    amount BIGINT NOT NULL,
    trans_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    note VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES User(user_id)
) ENGINE=InnoDB;

-- Bảng Room (các phòng chơi)
CREATE TABLE Room (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_name VARCHAR(50) NOT NULL,
    bet_amount BIGINT NOT NULL,
    status ENUM('WAITING','PLAYING') DEFAULT 'WAITING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Bảng GameMatch (lưu các ván đấu)
CREATE TABLE GameMatch (
    match_id INT AUTO_INCREMENT PRIMARY KEY,
    room_id INT NOT NULL,
    player1_id INT NOT NULL,
    player2_id INT NOT NULL,
    winner_id INT,
    played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    bet_amount BIGINT NOT NULL,
    FOREIGN KEY (room_id) REFERENCES Room(room_id),
    FOREIGN KEY (player1_id) REFERENCES User(user_id),
    FOREIGN KEY (player2_id) REFERENCES User(user_id),
    FOREIGN KEY (winner_id) REFERENCES User(user_id)
) ENGINE=InnoDB;

-- (Không bắt buộc) Tạo sẵn 5 phòng mặc định
INSERT INTO Room (room_name, bet_amount, status) VALUES
 ('Phòng 5tr',  5000000,  'WAITING'),
 ('Phòng 10tr', 10000000, 'WAITING'),
 ('Phòng 15tr', 15000000, 'WAITING'),
 ('Phòng 20tr', 20000000, 'WAITING'),
 ('Phòng 50tr', 50000000, 'WAITING');
