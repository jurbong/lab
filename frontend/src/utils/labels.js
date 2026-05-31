export const ROLE_LABELS = {
  ADMIN: '시스템 관리자',
  GROUP_MANAGER: '그룹 관리자',
  SAFETY_MANAGER: '연구실 안전관리 담당자',
  EDUCATION_MANAGER: '안전교육 담당자',
  LAB_MEMBER: '연구실 구성원',
};

export const STATUS_LABELS = {
  PENDING: '승인 대기',
  APPROVED: '승인 완료',
  REJECTED: '승인 거절',
  INACTIVE: '비활성화',
};

export const ADMIN_DEPARTMENT_LABELS = {
  SAFETY_MANAGEMENT: '안전관리부서',
  EDUCATION_MANAGEMENT: '안전교육부서',
  SYSTEM_MANAGEMENT: '시스템관리부서',
};

export const RISK_LABELS = {
  LOW: '낮음',
  MEDIUM: '보통',
  HIGH: '높음',
  DANGER: '위험',
};

export const roleLabel = (value) => ROLE_LABELS[value] || value || '-';
export const statusLabel = (value) => STATUS_LABELS[value] || value || '-';
export const adminDepartmentLabel = (value) => ADMIN_DEPARTMENT_LABELS[value] || value || '-';
export const riskLabel = (value) => RISK_LABELS[value] || value || '-';

export const isAdmin = (user) => user?.role === 'ADMIN';
export const isSafetyManager = (user) => user?.role === 'SAFETY_MANAGER' || user?.role === 'ADMIN';
export const isEducationManager = (user) => user?.role === 'EDUCATION_MANAGER' || user?.role === 'ADMIN';
