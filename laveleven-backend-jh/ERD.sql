-- ============================================
-- MariaDB DDL for Label Conversion System
-- RDB: MariaDB, VectorDB: Qdrant (별도 관리)
-- ERD 기준: USERS, ITEMS, HISTORY, SCAN, SCHEMA_DATA, TRANSLATE, SKETCH, VIOLATIONS
-- ============================================

-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS labelai 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE labelai;

-- ============================================
-- 1. USERS 테이블
-- 목적: 시스템 로그인 사용자 및 권한/소속 관리
-- ============================================
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,           -- PK, 사용자 고유 ID
    username      VARCHAR(100) NOT NULL UNIQUE,                -- 로그인 ID
    email         VARCHAR(255) NOT NULL UNIQUE,                -- 이메일 주소
    password      VARCHAR(255) NOT NULL,                       -- 암호화된 비밀번호
    role          VARCHAR(50)  NOT NULL DEFAULT 'USER',        -- 역할: SUPER_ADMIN / ADMIN / USER
    department_id VARCHAR(50),                                 -- 소속 부서 코드
    is_active     TINYINT(1)   NOT NULL DEFAULT 1,             -- 계정 활성 여부 (1=활성)
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 2. ITEMS 테이블
-- 목적: 라벨 변환 대상 아이템 마스터 정보
-- ============================================
DROP TABLE IF EXISTS items;
CREATE TABLE items (
    id            VARCHAR(36) PRIMARY KEY,                     -- PK, UUID 문자열
    item_name     VARCHAR(255) NOT NULL,                       -- 아이템 이름
    item_type     VARCHAR(100),                                -- 아이템 유형
    department_id VARCHAR(50),                                 -- 담당 부서
    version_major INT NOT NULL DEFAULT 1,                      -- 버전 major
    version_minor INT NOT NULL DEFAULT 0,                      -- 버전 minor
    version_patch INT NOT NULL DEFAULT 0,                      -- 버전 patch
    description   TEXT,                                        -- 설명
    created_by    BIGINT NOT NULL,                             -- 생성자 FK
    updated_by    BIGINT,                                      -- 수정자 FK
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_items_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_items_updated_by FOREIGN KEY (updated_by) REFERENCES users(id),
    INDEX idx_items_department (department_id),
    INDEX idx_items_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 3. HISTORY 테이블
-- 목적: ITEMS 변경 이력 기록
-- ============================================
DROP TABLE IF EXISTS history;
CREATE TABLE history (
    id          VARCHAR(36) PRIMARY KEY,                       -- PK, UUID 문자열
    item_id     VARCHAR(36) NOT NULL,                          -- FK to items
    step_name   VARCHAR(50)  NOT NULL,                         -- 단계: SCAN/SCHEMA/TRANSLATE/SKETCH/VIOLATIONS
    field_name  VARCHAR(100) NOT NULL,                         -- 변경 필드명
    action_type VARCHAR(50)  NOT NULL,                         -- 액션: SAVE/UPDATE/ROLLBACK
    payload     JSON,                                          -- 변경 내용 JSON
    changed_by  BIGINT NOT NULL,                               -- 변경자 FK
    changed_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_history_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    CONSTRAINT fk_history_changed_by FOREIGN KEY (changed_by) REFERENCES users(id),
    INDEX idx_history_item_step (item_id, step_name),
    INDEX idx_history_changed_at (changed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 4. SCAN 테이블
-- 목적: SCAN 단계 스냅샷 저장
-- ============================================
DROP TABLE IF EXISTS scan;
CREATE TABLE scan (
    id             VARCHAR(36) PRIMARY KEY,                    -- PK, UUID 문자열
    item_id        VARCHAR(36) NOT NULL,                       -- FK to items
    scan_image_url TEXT NOT NULL,                              -- 스캔 이미지 URL
    scan_meta      JSON,                                       -- 메타데이터 JSON
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_scan_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    INDEX idx_scan_item (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 5. SCHEMA_DATA 테이블 (schema는 예약어)
-- 목적: 구조화된 라벨 데이터 저장
-- ============================================
DROP TABLE IF EXISTS schema_data;
CREATE TABLE schema_data (
    id         VARCHAR(36) PRIMARY KEY,                        -- PK, UUID 문자열
    item_id    VARCHAR(36) NOT NULL,                           -- FK to items
    data       JSON NOT NULL,                                  -- 구조화 데이터 JSON
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_schema_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    INDEX idx_schema_item (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 6. TRANSLATE 테이블
-- 목적: 번역 결과 저장
-- ============================================
DROP TABLE IF EXISTS translate;
CREATE TABLE translate (
    id         VARCHAR(36) PRIMARY KEY,                        -- PK, UUID 문자열
    item_id    VARCHAR(36) NOT NULL,                           -- FK to items
    data       JSON NOT NULL,                                  -- 번역 결과 JSON
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_translate_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    INDEX idx_translate_item (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 7. SKETCH 테이블
-- 목적: 레이아웃/HTML 저장
-- ============================================
DROP TABLE IF EXISTS sketch;
CREATE TABLE sketch (
    id         VARCHAR(36) PRIMARY KEY,                        -- PK, UUID 문자열
    item_id    VARCHAR(36) NOT NULL,                           -- FK to items
    data       LONGTEXT NOT NULL,                              -- HTML/레이아웃 데이터
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sketch_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    INDEX idx_sketch_item (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 8. VIOLATIONS 테이블
-- 목적: 규정 위반 검증 결과 저장
-- ============================================
DROP TABLE IF EXISTS violations;
CREATE TABLE violations (
    id         VARCHAR(36) PRIMARY KEY,                        -- PK, UUID 문자열
    item_id    VARCHAR(36) NOT NULL,                           -- FK to items
    data       JSON NOT NULL,                                  -- 위반 결과 JSON
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_violations_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    INDEX idx_violations_item (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;