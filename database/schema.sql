-- =============================================
-- Auto Recycle Helper - MySQL DDL Script
-- =============================================

CREATE DATABASE IF NOT EXISTS auto_recycle_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE auto_recycle_db;

-- ---------------------------------------------
-- [1] device (장치 테이블)
-- ---------------------------------------------
CREATE TABLE device (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    device_code VARCHAR(50)     NOT NULL UNIQUE     COMMENT '장치 코드(고유값)',
    device_name VARCHAR(100)                        COMMENT '장치 이름',
    location    VARCHAR(255)                        COMMENT '설치 위치',
    status      VARCHAR(20)     NOT NULL DEFAULT 'OFFLINE'
                                COMMENT 'ONLINE / OFFLINE / ERROR',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME                 DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) COMMENT='장치 테이블';

-- ---------------------------------------------
-- [2] trash_type (쓰레기 타입 테이블)
-- ---------------------------------------------
CREATE TABLE trash_type (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    type_code   VARCHAR(50)     NOT NULL UNIQUE     COMMENT '코드 (PLASTIC, CAN, GLASS, GENERAL)',
    type_name   VARCHAR(100)                        COMMENT '이름 (플라스틱, 캔, 유리, 일반)',
    description VARCHAR(255)                        COMMENT '설명',
    recyclable  BOOLEAN         NOT NULL DEFAULT TRUE COMMENT '재활용 여부',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) COMMENT='쓰레기 타입 테이블';

-- ---------------------------------------------
-- [3] bin (장치 내부 분류 통 테이블)
-- ---------------------------------------------
CREATE TABLE bin (
    id            BIGINT          NOT NULL AUTO_INCREMENT,
    device_id     BIGINT          NOT NULL            COMMENT '장치 FK',
    trash_type_id BIGINT          NOT NULL            COMMENT '쓰레기 종류 FK',
    bin_code      VARCHAR(50)     NOT NULL UNIQUE     COMMENT '통 코드(고유값)',
    bin_name      VARCHAR(100)                        COMMENT '통 이름',
    capacity      INT                                 COMMENT '최대 용량',
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME                 DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_bin_device     FOREIGN KEY (device_id)     REFERENCES device(id),
    CONSTRAINT fk_bin_trash_type FOREIGN KEY (trash_type_id) REFERENCES trash_type(id)
) COMMENT='장치 내부 분류 통 테이블';

-- ---------------------------------------------
-- [4] bin_status (통별 상태/적재량 테이블)
-- ---------------------------------------------
CREATE TABLE bin_status (
    id                BIGINT      NOT NULL AUTO_INCREMENT,
    bin_id            BIGINT      NOT NULL UNIQUE     COMMENT '통 FK (1:1)',
    fill_percent      INT         NOT NULL DEFAULT 0  COMMENT '적재량 (%)',
    is_full           BOOLEAN     NOT NULL DEFAULT FALSE COMMENT '가득 찼는지 여부',
    error_flag        BOOLEAN     NOT NULL DEFAULT FALSE COMMENT '현재 오류 여부',
    last_collected_at DATETIME             DEFAULT NULL COMMENT '마지막 수거 시간',
    updated_at        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
                                           ON UPDATE CURRENT_TIMESTAMP COMMENT '상태 갱신 시간',
    PRIMARY KEY (id),
    CONSTRAINT fk_bin_status_bin FOREIGN KEY (bin_id) REFERENCES bin(id)
) COMMENT='통별 상태(적재량) 테이블';

-- ---------------------------------------------
-- [5] trash_event (쓰레기 분류 이벤트 테이블)
-- ---------------------------------------------
CREATE TABLE trash_event (
    id            BIGINT          NOT NULL AUTO_INCREMENT,
    device_id     BIGINT          NOT NULL            COMMENT '장치 FK',
    bin_id        BIGINT          NOT NULL            COMMENT '통 FK',
    trash_type_id BIGINT          NOT NULL            COMMENT '쓰레기 종류 FK',
    is_defective  BOOLEAN         NOT NULL DEFAULT FALSE COMMENT '불량 여부',
    status        VARCHAR(20)     NOT NULL DEFAULT 'PENDING'
                                  COMMENT 'PENDING / PROCESSED / ERROR',
    defect_reason VARCHAR(255)             DEFAULT NULL COMMENT '불량 이유',
    confidence    DOUBLE                   DEFAULT NULL COMMENT 'AI 신뢰도',
    image_url     TEXT                     DEFAULT NULL COMMENT '이미지 경로',
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_event_device     FOREIGN KEY (device_id)     REFERENCES device(id),
    CONSTRAINT fk_event_bin        FOREIGN KEY (bin_id)        REFERENCES bin(id),
    CONSTRAINT fk_event_trash_type FOREIGN KEY (trash_type_id) REFERENCES trash_type(id)
) COMMENT='쓰레기 분류 이벤트 테이블';

-- ---------------------------------------------
-- [6] bin_error_log (통/분류 오류 로그 테이블)
-- ---------------------------------------------
CREATE TABLE bin_error_log (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    bin_id      BIGINT          NOT NULL            COMMENT '통 FK',
    error_type  VARCHAR(50)     NOT NULL            COMMENT '오류 유형',
    message     VARCHAR(255)             DEFAULT NULL COMMENT '오류 내용',
    resolved    BOOLEAN         NOT NULL DEFAULT FALSE COMMENT '해결 여부',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at DATETIME                 DEFAULT NULL COMMENT '해결 시간',
    PRIMARY KEY (id),
    CONSTRAINT fk_bin_error_bin FOREIGN KEY (bin_id) REFERENCES bin(id)
) COMMENT='통 오류 로그 테이블';

-- ---------------------------------------------
-- [7] device_error_log (장치 오류 로그 테이블)
-- ---------------------------------------------
CREATE TABLE device_error_log (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    device_id   BIGINT          NOT NULL            COMMENT '장치 FK',
    error_type  VARCHAR(50)     NOT NULL            COMMENT '오류 유형',
    message     VARCHAR(255)             DEFAULT NULL COMMENT '오류 내용',
    resolved    BOOLEAN         NOT NULL DEFAULT FALSE COMMENT '해결 여부',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at DATETIME                 DEFAULT NULL COMMENT '해결 시간',
    PRIMARY KEY (id),
    CONSTRAINT fk_device_error_device FOREIGN KEY (device_id) REFERENCES device(id)
) COMMENT='장치 오류 로그 테이블';

-- [8] admin (관리자 계정 테이블)
CREATE TABLE admin (
                       id           BIGINT       NOT NULL AUTO_INCREMENT,
                       username     VARCHAR(50)  NOT NULL UNIQUE     COMMENT '로그인 아이디',
                       password     VARCHAR(255) NOT NULL             COMMENT '비밀번호 (BCrypt 암호화)',
                       name         VARCHAR(100)                      COMMENT '관리자 이름',
                       role         VARCHAR(20)  NOT NULL DEFAULT 'ADMIN'
                           COMMENT 'ADMIN / SUPER_ADMIN',
                       is_active    BOOLEAN      NOT NULL DEFAULT TRUE COMMENT '계정 활성화 여부',
                       last_login_at DATETIME             DEFAULT NULL COMMENT '마지막 로그인 시간',
                       created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at   DATETIME              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                       PRIMARY KEY (id)
) COMMENT='관리자 계정 테이블';

-- 기본 관리자 계정 (비밀번호: admin1234 → BCrypt)
INSERT INTO admin (username, password, name, role) VALUES
    ('admin', '$2a$10$7QJ9Z1Z1Z1Z1Z1Z1Z1Z1ZuKQJ9Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1', '관리자', 'SUPER_ADMIN');


-- =============================================
-- 기본 데이터 (trash_type)
-- =============================================
INSERT INTO trash_type (type_code, type_name, description, recyclable) VALUES
('PLASTIC', '플라스틱', '페트병, 플라스틱 용기 등', TRUE),
('CAN',     '캔',      '음료캔, 금속캔 등',         TRUE),
('GLASS',   '유리',    '유리병, 유리용기 등',        TRUE),
('GENERAL', '일반쓰레기', '재활용 불가 쓰레기',      FALSE);


-- =============================================
-- 추가 데이터 (admin, device, bin, bin_status)
-- =============================================
UPDATE auto_recycle_db.admin
SET password = '$2b$10$N53PfUm1erhCc5LI/RcGe.CYIGj99yTfCB.Ka3NzhNpsl7/9IDT0m'
WHERE username = 'admin';

USE auto_recycle_db;

-- device 더미 데이터
INSERT INTO device (device_code, device_name, location, status) VALUES
('DEVICE_001', '1호기', '1층 로비', 'ONLINE'),
('DEVICE_002', '2호기', '2층 복도', 'OFFLINE');

-- bin 더미 데이터
INSERT INTO bin (device_id, trash_type_id, bin_code, bin_name, capacity) VALUES
(1, 1, 'BIN_001_PLASTIC', '1호기 플라스틱통', 100),
(1, 2, 'BIN_001_CAN',     '1호기 캔통',       100),
(1, 3, 'BIN_001_GLASS',   '1호기 유리통',     100),
(1, 4, 'BIN_001_GENERAL', '1호기 일반쓰레기통', 100),
(2, 1, 'BIN_002_PLASTIC', '2호기 플라스틱통', 100);

INSERT INTO bin (device_id, trash_type_id, bin_code, bin_name, capacity) VALUES
(2, 2, 'BIN_002_CAN', 	  '2호기 캔통', 	  100),
(2, 3, 'BIN_002_GLASS',   '2호기 유리통',    100),
(2, 4, 'BIN_002_GENERAL', '2호기 일반쓰레기통', 100);


-- bin_status 더미 데이터
INSERT INTO bin_status (bin_id, fill_percent, is_full, error_flag) VALUES
(1, 65, false, false),
(2, 30, false, false),
(3, 90, false, false),
(4, 10, false, false),
(5, 0,  false, false);

INSERT INTO trash_type (type_code, type_name, description, recyclable)
VALUES ('BEVERAGE', '음료', '잔여 음료 및 액체류', FALSE);

INSERT INTO bin (device_id, trash_type_id, bin_code, bin_name, capacity)
VALUES
    (1, 5, 'BIN_001_BEVERAGE', '1호기 음료통', 100),
    (2, 5, 'BIN_002_BEVERAGE', '2호기 음료통', 100);

CREATE TABLE beverage_fill_log (
                                   id              BIGINT      NOT NULL AUTO_INCREMENT,
                                   bin_id          BIGINT      NOT NULL COMMENT '음료통 FK',
                                   fill_percent    INT         NOT NULL DEFAULT 0 COMMENT '음료 적재량 (%)',
                                   estimated_ml    INT                  DEFAULT NULL COMMENT '추정 음료량 ml',
                                   sensor_value    DOUBLE               DEFAULT NULL COMMENT '센서 원본값',
                                   measured_at     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '측정 시간',
                                   PRIMARY KEY (id),
                                   CONSTRAINT fk_beverage_fill_log_bin
                                       FOREIGN KEY (bin_id) REFERENCES bin(id)
) COMMENT='음료 적재량 측정 이력 테이블';

CREATE TABLE inspection_notification (
                                         id                BIGINT       NOT NULL AUTO_INCREMENT,
                                         device_id          BIGINT                DEFAULT NULL COMMENT '장치 FK',
                                         bin_id             BIGINT                DEFAULT NULL COMMENT '통 FK',
                                         sender_admin_id    BIGINT       NOT NULL COMMENT '알림 보낸 관리자',
                                         receiver_admin_id  BIGINT       NOT NULL COMMENT '알림 받는 관리자',
                                         floor              INT                  DEFAULT NULL COMMENT '층 정보',
                                         title              VARCHAR(100) NOT NULL COMMENT '알림 제목',
                                         message            TEXT                 DEFAULT NULL COMMENT '알림 내용',
                                         notification_type  VARCHAR(30)  NOT NULL DEFAULT 'INSPECTION_COMPLETE'
                                             COMMENT 'INSPECTION_COMPLETE / FULL_ALERT / ERROR_ALERT',
                                         status             VARCHAR(20)  NOT NULL DEFAULT 'SENT'
                                             COMMENT 'SENT / READ / CONFIRMED / CANCELED',
                                         sent_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         read_at            DATETIME              DEFAULT NULL,
                                         confirmed_at       DATETIME              DEFAULT NULL,
                                         created_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         updated_at         DATETIME              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                                         PRIMARY KEY (id),
                                         CONSTRAINT fk_inspection_device
                                             FOREIGN KEY (device_id) REFERENCES device(id),
                                         CONSTRAINT fk_inspection_bin
                                             FOREIGN KEY (bin_id) REFERENCES bin(id),
                                         CONSTRAINT fk_inspection_sender_admin
                                             FOREIGN KEY (sender_admin_id) REFERENCES admin(id),
                                         CONSTRAINT fk_inspection_receiver_admin
                                             FOREIGN KEY (receiver_admin_id) REFERENCES admin(id)
) COMMENT='점검 완료 알림 및 관리자 커뮤니케이션 테이블';

SET @can_id      = (SELECT id FROM trash_type WHERE type_code = 'CAN');
SET @glass_id    = (SELECT id FROM trash_type WHERE type_code = 'GLASS');
SET @general_id  = (SELECT id FROM trash_type WHERE type_code = 'GENERAL');
SET @beverage_id = (SELECT id FROM trash_type WHERE type_code = 'BEVERAGE');


INSERT INTO bin (id, device_id, trash_type_id, bin_code, bin_name, capacity)
VALUES
    (9, 1, @beverage_id, 'BIN_001_BEVERAGE', '1호기 음료통', 100),
    (10, 2, @beverage_id, 'BIN_002_BEVERAGE', '2호기 음료통', 100);


INSERT INTO bin_status (bin_id, fill_percent, is_full, error_flag)
VALUES
    (6, 0, FALSE, FALSE),
    (7, 0, FALSE, FALSE),
    (8, 0, FALSE, FALSE),
    (9, 0, FALSE, FALSE),
    (10, 0, FALSE, FALSE);


INSERT INTO admin (username, password, name, role)
VALUES
    ('floor1', '$2b$10$N53PfUm1erhCc5LI/RcGe.CYIGj99yTfCB.Ka3NzhNpsl7/9IDT0m', '1층 관리자', 'ADMIN'),
    ('floor2', '$2b$10$N53PfUm1erhCc5LI/RcGe.CYIGj99yTfCB.Ka3NzhNpsl7/9IDT0m', '2층 관리자', 'ADMIN');

ALTER TABLE trash_event
    ADD COLUMN event_type VARCHAR(20) NOT NULL DEFAULT 'CLASSIFY'
    COMMENT 'CLASSIFY / RESET' AFTER trash_type_id;

