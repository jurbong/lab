import { roleLabel } from '../utils/labels';

function Dashboard({ user }) {
  return (
    <section className="page">
      <h2>대시보드</h2>
      <div className="cards three">
        <div className="card"><strong>로그인 사용자</strong><span>{user?.name}</span></div>
        <div className="card"><strong>현재 권한</strong><span>{roleLabel(user?.role)}</span></div>
      </div>
    </section>
  );
}

export default Dashboard;
