# 백엔드 수정 요약

## 확정 설계 반영
- Role은 시스템 전체 역할만 관리합니다.
  - ADMIN
  - GROUP_MANAGER
  - SAFETY_MANAGER
  - EDUCATION_MANAGER
  - LAB_MEMBER
- 가입 승인 대기는 Role이 아니라 UserStatus로 관리합니다.
  - PENDING
  - APPROVED
  - REJECTED
  - INACTIVE
- 연구실 책임자는 Role이 아니라 Laboratory의 managerName 정보로만 저장합니다.
- 학과는 Department 엔티티로 관리합니다.
- 관리 부서는 User의 adminDepartment enum으로 관리합니다.
- User와 Laboratory는 LaboratoryMember로 연결합니다.

## UI 워크플로우 대응 API
- `/api/departments`: 학과 선택 목록
- `/api/options/roles`: 권한 옵션
- `/api/options/statuses`: 사용자 상태 옵션
- `/api/options/admin-departments`: 관리 부서 옵션
- `/api/options/units`: 단위 옵션
- `/api/options/risk-levels`: 위험/유해성 등급 옵션
- `/api/laboratories/options`: 연구실 자동완성/선택 목록

## 검색/필터 추가
- 사용자: keyword, status, role, departmentId, adminDepartment
- 연구실: keyword, departmentId, labType
- 화학물질: keyword, riskLevel, labId, departmentId, storageLocation
- 폐기물: keyword, wasteType, hazardLevel, labId, departmentId, unit
- 점검 양식: keyword, inspectionType
- 안전교육 동영상: keyword, educationType

## 권한 반영
- 화학물질 등록: ADMIN만 가능
- 화학물질 조회: 승인된 사용자 역할 기준 조회 가능
- 폐기물 등록/조회: 해당 연구실 LaboratoryMember 또는 ADMIN만 가능
- 점검 양식 등록/조회: SAFETY_MANAGER 또는 ADMIN만 가능
- 안전교육 동영상 등록/관리: EDUCATION_MANAGER 또는 ADMIN만 가능
- 안전교육 동영상 조회: LAB_MEMBER, EDUCATION_MANAGER, ADMIN 가능
