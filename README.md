# 🗑️ Auto Recycle Helper
> AI 기반 자동 분리수거 시스템

---

## 📌 프로젝트 소개

카메라와 센서를 통해 쓰레기를 자동으로 분류하고, 실시간으로 적재량 및 오류를 모니터링하는 시스템입니다.
라즈베리파이에서 YOLO 기반 AI 분석을 수행하고, Spring Boot 서버를 통해 웹 및 앱에서 관리할 수 있습니다.

---

## 🏗️ 시스템 구조

```
[라즈베리파이]
카메라 촬영 → YOLO 분석 → 결과 JSON 생성
        ↓
  REST API POST 요청
        ↓
[Spring Boot 서버]
데이터 검증 → MySQL DB 저장 → WebSocket 브로드캐스트
        ↓
[React 웹 / Android 앱]
실시간 모니터링 화면
```

---

## 🛠️ 기술 스택

| 분류 | 기술 |
|------|------|
| AI | Python, YOLO |
| 디바이스 | Raspberry Pi |
| 백엔드 | Spring Boot 3.x (Java) |
| DB | MySQL |
| 프론트엔드 | React |
| 앱 | Android |
| 통신 | REST API + WebSocket |
| 인증 | Spring Security + JWT |
| API 문서 | Swagger UI |

---

## 📂 프로젝트 구조

```
Auto-Recycle-Server-Project/
├── src/
│   └── main/java/com/capstone/recycle/
│       ├── Controller/
│       ├── DTO/
│       │   ├── request/
│       │   └── response/
│       ├── Entity/
│       ├── Repository/
│       ├── Security/
│       ├── Service/
│       └── SwaggerConfig.java
├── database/
│   ├── schema.sql
│   └── schema_admin.sql
├── .gitignore
└── build.gradle
```

---

## 🗄️ DB 구조

| 테이블 | 설명 |
|--------|------|
| device | 장치 정보 |
| trash_type | 쓰레기 종류 (PLASTIC, CAN, GLASS, GENERAL) |
| bin | 장치 내부 분류 통 |
| bin_status | 통별 실시간 적재량 |
| trash_event | 쓰레기 분류 이벤트 기록 |
| bin_error_log | 통 오류 로그 |
| device_error_log | 장치 오류 로그 |
| admin | 관리자 계정 |

---

## 🔑 API 목록

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | /api/auth/login | 관리자 로그인 | ❌ |
| GET | /api/devices | 전체 장치 목록 | ✅ JWT |
| GET | /api/devices/{id} | 장치 상세 | ✅ JWT |
| GET | /api/bins/{deviceId} | 통 상태 조회 | ✅ JWT |
| POST | /api/bins/{id}/reset | 통 리셋 | ✅ JWT |
| POST | /api/events | 라즈베리파이 이벤트 수신 | ❌ |
| GET | /api/logs | 분류 기록 전체 | ✅ JWT |
| GET | /api/logs/{id} | 분류 기록 상세 | ✅ JWT |
| GET | /api/errors | 오류 로그 전체 | ✅ JWT |
| POST | /api/errors/bin/{id}/resolve | 통 오류 해결 | ✅ JWT |
| POST | /api/errors/device/{id}/resolve | 장치 오류 해결 | ✅ JWT |

> 전체 API 문서: `http://localhost:8080/swagger-ui/index.html`

---

## ⚙️ 로컬 환경 세팅

### 1. 레포 클론
```bash
git clone https://github.com/khi6174/Auto-Recycle-Server-Project.git
```

### 2. MySQL 스키마 생성
```bash
# MySQL Workbench에서 아래 파일 순서대로 실행
database/schema.sql
database/schema_admin.sql
```

### 3. application-local.properties 생성
`src/main/resources/` 아래에 파일 생성:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/auto_recycle_db?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=본인비밀번호
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
jwt.secret=autorecyclehelper-secret-key-must-be-at-least-32-characters
jwt.expiration=86400000
springdoc.swagger-ui.path=/swagger-ui/index.html
springdoc.api-docs.path=/v3/api-docs
```

> ⚠️ 이 파일은 `.gitignore`에 등록되어 있어 Git에 올라가지 않습니다.

### 4. 서버 실행
```bash
./gradlew bootRun
```

### 5. Swagger 접속
```
http://localhost:8080/swagger-ui/index.html
```

---

## 🔐 초기 관리자 계정

| 항목 | 값 |
|------|-----|
| username | admin |
| password | 비밀번호 |

> ⚠️ 실제 배포 시 비밀번호 변경예정.

---
