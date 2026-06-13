import { useMemo } from 'react';
import { roleLabel } from '../utils/labels';

const menus = [
  { key: 'dashboard', label: '대시보드' },
  { key: 'users', label: '사용자 관리', roles: ['ADMIN', 'GROUP_MANAGER'] },
  { key: 'labs', label: '연구실 관리' },
  { key: 'chemicals', label: '화학물질 관리' },
  { key: 'wastes', label: '폐기물 관리' },
  { key: 'inspections', label: '점검 양식 관리', roles: ['ADMIN', 'SAFETY_MANAGER'] },
  { key: 'education', label: '안전교육 관리', roles: ['ADMIN', 'EDUCATION_MANAGER', 'LAB_MEMBER'] },
];

function Layout({ user, page, setPage, onLogout, children }) {
  const visibleMenus = useMemo(() => {
    return menus.filter((menu) => !menu.roles || menu.roles.includes(user?.role));
  }, [user]);

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <strong>연구실 안전관리</strong>
          <span>등록/조회 시스템</span>
        </div>
        <nav>
          {visibleMenus.map((menu) => (
            <button
              key={menu.key}
              className={page === menu.key ? 'active' : ''}
              onClick={() => setPage(menu.key)}
            >
              {menu.label}
            </button>
          ))}
        </nav>
      </aside>
      <main className="main">
        <header className="topbar">
          <div>
            <strong>{user?.name}</strong>
            <span>{roleLabel(user?.role)}</span>
          </div>
          <button className="ghost" onClick={onLogout}>로그아웃</button>
        </header>
        {children}
      </main>
    </div>
  );
}

export default Layout;
