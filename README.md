# 연구실 안전관리 시스템 (Lab Safety Management System)

Spring Boot 3.4.4 (Java 17) + React 18 (Vite) + MySQL 8

## 폴더 구조
- `backend/`  : Spring Boot REST API (포트 8080)
- `frontend/` : React + Vite (포트 5173)
- API 기본 주소: `http://localhost:8080/api`

---

## 1. 데이터베이스 준비 (MySQL)
DBeaver 또는 MySQL CLI에서 실행:

    CREATE DATABASE lab DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

`backend/src/main/resources/application.properties` 의 datasource username/password 를
본인 환경에 맞게 수정하세요. (기본값: root / 1234)

---

## 2. 백엔드 실행
1. IntelliJ IDEA 에서 `backend` 폴더의 `build.gradle` 을 열어 프로젝트로 import (Gradle 자동 설정)
2. `LabSafetyBackendApplication` 실행
3. 최초 실행 시 관리자 계정이 자동 생성됩니다.
   - 아이디: `admin`
   - 비밀번호: `1234`
4. 테이블은 `spring.jpa.hibernate.ddl-auto=update` 설정으로 자동 생성됩니다.

---

## 3. 프론트엔드 실행
    cd frontend
    npm install
    npm run dev

브라우저에서 http://localhost:5173 접속

---

## 4. 사용 흐름
1. `admin / 1234` 로 로그인하면 모든 메뉴 사용 가능
2. 일반 사용자: 회원가입 신청(PENDING) → 관리자가 [가입승인]에서 권한 부여 + 승인(APPROVED) → 로그인
3. 권한 요약
   - 사용자관리 / 가입승인: 관리자(SYSTEM_ADMIN, LAB_ADMIN)만
   - 연구실 등록 / 목록: 로그인 사용자 누구나
   - 연구실 상세: 권한별 접근 제한 (관리자 전체, 학과관리자 같은 학과, 책임자/구성원 본인 연구실)
   - 화학물질 / 폐기물 등록: 해당 연구실 책임자 또는 소속 사용자만
   - 점검양식 / 교육영상 등록: 관리자만 (조회는 로그인 사용자 누구나)

---

## 5. 주요 API
- POST `/api/auth/signup`, POST `/api/auth/login`
- GET `/api/users`, GET `/api/users/pending`, GET `/api/users/options`, POST `/api/users/{id}/approve`
- GET/POST `/api/laboratories`, GET `/api/laboratories/{id}`
- GET/POST `/api/chemicals`, GET `/api/chemicals/{id}`
- GET/POST `/api/wastes`, GET `/api/wastes/{id}`
- GET/POST `/api/inspection-forms`, GET `/api/inspection-forms/{id}`
- GET/POST `/api/education-videos`, GET `/api/education-videos/{id}`

로그인 후 발급된 accessToken 은 프론트에서 localStorage 에 저장되고
모든 요청의 `Authorization: Bearer <token>` 헤더로 자동 전송됩니다.
