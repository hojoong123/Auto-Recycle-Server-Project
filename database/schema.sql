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

-- =============================================
-- 기본 데이터 (trash_type)
-- =============================================
INSERT INTO trash_type (type_code, type_name, description, recyclable) VALUES
('PLASTIC', '플라스틱', '페트병, 플라스틱 용기 등', TRUE),
('CAN',     '캔',      '음료캔, 금속캔 등',         TRUE),
('GLASS',   '유리',    '유리병, 유리용기 등',        TRUE),
('GENERAL', '일반쓰레기', '재활용 불가 쓰레기',      FALSE);
