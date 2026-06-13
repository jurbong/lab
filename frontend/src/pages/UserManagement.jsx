import { useEffect, useState } from 'react';
import { departmentApi, optionApi, userApi } from '../api/api';
import http, { unwrap } from '../api/http';
import { DetailGrid, DetailModal, EmptyState, SelectInput, TextInput } from '../components/FormControls';
import { adminDepartmentLabel, roleLabel, statusLabel } from '../utils/labels';

const initialCreateForm = {
  userId: '',
  password: '',
  name: '',
  gender: '',
  departmentId: '',
  email: '',
  phone: '',
  role: 'LAB_MEMBER',
  adminDepartment: '',
};

function UserManagement({ user }) {
  const isSystemAdmin = user?.role === 'ADMIN';

  const [users, setUsers] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [roles, setRoles] = useState([]);
  const [adminDepartments, setAdminDepartments] = useState([]);
  const [detail, setDetail] = useState(null);
  const [showCreate, setShowCreate] = useState(false);
  const [createForm, setCreateForm] = useState(initialCreateForm);

  const load = async () => {
    try {
      setUsers(await userApi.list());
    } catch (e) {
      alert(e.message);
    }
  };

  useEffect(() => {
    departmentApi.list().then(setDepartments).catch(() => {});
    optionApi.roles().then(setRoles).catch(() => {});
    optionApi.adminDepartments().then(setAdminDepartments).catch(() => {});
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const changeCreateForm = (e) => setCreateForm({ ...createForm, [e.target.name]: e.target.value });

  const submit = (e) => {
    e.preventDefault();
    load();
  };

  const openDetail = async (id) => {
    try {
      setDetail(await userApi.detail(id));
    } catch (e) {
      alert(e.message);
    }
  };

  const createUser = async (e) => {
    e.preventDefault();
    try {
      await unwrap(await http.post('/api/users', {
        ...createForm,
        departmentId: createForm.departmentId ? Number(createForm.departmentId) : null,
        adminDepartment: createForm.adminDepartment || null,
      }));

      alert('사용자 등록 완료');
      setCreateForm(initialCreateForm);
      setShowCreate(false);
      load();
    } catch (e) {
      alert(e.message);
    }
  };

  return (
    <section className="page">
      <div className="page-head">
        <h2>사용자 관리</h2>

        {isSystemAdmin && (
          <button onClick={() => setShowCreate(!showCreate)}>
            {showCreate ? '등록 닫기' : '사용자 등록'}
          </button>
        )}
      </div>

      {showCreate && isSystemAdmin && (
        <form className="create-card" onSubmit={createUser}>
          <h3>사용자 등록</h3>

          <div className="form-grid">
            <TextInput
              label="아이디"
              name="userId"
              value={createForm.userId}
              onChange={changeCreateForm}
              required
            />

            <TextInput
              label="비밀번호"
              name="password"
              type="password"
              value={createForm.password}
              onChange={changeCreateForm}
              required
            />

            <TextInput
              label="이름"
              name="name"
              value={createForm.name}
              onChange={changeCreateForm}
              required
            />

            <SelectInput
              label="성별"
              name="gender"
              value={createForm.gender}
              onChange={changeCreateForm}
            >
              <option value="">선택 안 함</option>
              <option value="남성">남성</option>
              <option value="여성">여성</option>
            </SelectInput>

            <SelectInput
              label="학과/부서"
              name="departmentId"
              value={createForm.departmentId}
              onChange={changeCreateForm}
            >
              <option value="">선택 안 함</option>
              {departments.map((d) => (
                <option key={d.id} value={d.id}>
                  {d.displayName || d.name}
                </option>
              ))}
            </SelectInput>

            <TextInput
              label="이메일"
              name="email"
              type="email"
              value={createForm.email}
              onChange={changeCreateForm}
              required
            />

            <TextInput
              label="전화번호"
              name="phone"
              value={createForm.phone}
              onChange={changeCreateForm}
              placeholder="010-0000-0000"
            />

            <SelectInput
              label="권한"
              name="role"
              value={createForm.role}
              onChange={changeCreateForm}
              required
            >
              {roles.map((r) => (
                <option key={r.value} value={r.value}>
                  {r.label}
                </option>
              ))}
            </SelectInput>

            <SelectInput
              label="관리 부서"
              name="adminDepartment"
              value={createForm.adminDepartment}
              onChange={changeCreateForm}
            >
              <option value="">해당 없음</option>
              {adminDepartments.map((d) => (
                <option key={d.value} value={d.value}>
                  {d.label}
                </option>
              ))}
            </SelectInput>
          </div>

          <button type="submit">등록</button>
        </form>
      )}

      <form className="search-panel" onSubmit={submit}>
        <div className="filter-actions">
          <button type="submit">조회</button>
        </div>
      </form>

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>이름</th>
              <th>아이디</th>
              <th>학과/부서</th>
              <th>권한</th>
              <th>상태</th>
              <th>관리 부서</th>
              <th>관리</th>
            </tr>
          </thead>

          <tbody>
            {users.map((u) => (
              <tr key={u.id}>
                <td>{u.name}</td>
                <td>{u.userId}</td>
                <td>{u.departmentDisplayName || u.departmentName || '-'}</td>
                <td>{u.roleLabel || roleLabel(u.role)}</td>
                <td>
                  <span className={`badge ${u.status}`}>
                    {u.statusLabel || statusLabel(u.status)}
                  </span>
                </td>
                <td>{u.adminDepartmentLabel || adminDepartmentLabel(u.adminDepartment)}</td>
                <td className="actions">
                  <button
                    className="secondary"
                    onClick={() => openDetail(u.id)}
                  >
                    상세
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {users.length === 0 && <EmptyState />}
      </div>

      {detail && (
        <DetailModal
          title="사용자 상세 조회"
          onClose={() => setDetail(null)}
        >
          <DetailGrid
            rows={[
              ['이름', detail.name],
              ['아이디', detail.userId],
              ['성별', detail.gender],
              ['이메일', detail.email],
              ['전화번호', detail.phone],
              ['학과/부서', detail.departmentDisplayName || detail.departmentName],
              ['권한', detail.roleLabel || roleLabel(detail.role)],
              ['상태', detail.statusLabel || statusLabel(detail.status)],
              ['관리 부서', detail.adminDepartmentLabel || adminDepartmentLabel(detail.adminDepartment)],
            ]}
          />
        </DetailModal>
      )}
    </section>
  );
}

export default UserManagement;
