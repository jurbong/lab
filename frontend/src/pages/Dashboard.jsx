import { roleLabel } from '../utils/labels';

function Dashboard({ user }) {
  return (
    <section className="page">
      <h2>대시보드</h2>
      <div className="cards three">
        <div className="card"><strong>{user?.name}</strong><span>로그인 사용자</span></div>
        <div className="card"><strong>{roleLabel(user?.role)}</strong><span>현재 권한</span></div>
      </div>
    </section>
  );
}

export default Dashboard;
