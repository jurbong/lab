import { useEffect, useState } from 'react';
import { departmentApi, optionApi, userApi } from '../api/api';
import { DetailGrid, DetailModal, EmptyState, SearchPanel, SelectInput, TextInput } from '../components/FormControls';
import { adminDepartmentLabel, roleLabel, statusLabel } from '../utils/labels';

const initialFilters = { keyword: '', status: '', role: '', departmentId: '', adminDepartment: '' };

function UserManagement({ user }) {
  const isSystemAdmin = user?.role === 'ADMIN';

  const [filters, setFilters] = useState(initialFilters);
  const [users, setUsers] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [roles, setRoles] = useState([]);
  const [statuses, setStatuses] = useState([]);
  const [adminDepartments, setAdminDepartments] = useState([]);
  const [detail, setDetail] = useState(null);
  const [approveTarget, setApproveTarget] = useState(null);
  const [approveForm, setApproveForm] = useState({ role: 'LAB_MEMBER', adminDepartment: '' });

  const load = async (next = filters) => {
    try {
      setUsers(await userApi.list(next));
    } catch (e) {
      alert(e.message);
    }
  };

  useEffect(() => {
    departmentApi.list().then(setDepartments).catch(() => {});
    optionApi.roles().then(setRoles).catch(() => {});
    optionApi.statuses().then(setStatuses).catch(() => {});
    optionApi.adminDepartments().then(setAdminDepartments).catch(() => {});
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const change = (e) => setFilters({ ...filters, [e.target.name]: e.target.value });

  const reset = () => {
    setFilters(initialFilters);
    load(initialFilters);
  };

  const submit = (e) => {
    e.preventDefault();
    load(filters);
  };

  const openDetail = async (id) => {
    try {
      setDetail(await userApi.detail(id));
    } catch (e) {
      alert(e.message);
    }
  };

  const approve = async () => {
    if (!approveTarget) return;

    try {
      await userApi.approve(approveTarget.id, {
        role: approveForm.role,
        adminDepartment: approveForm.adminDepartment || null,
      });

      alert('승인 완료');
      setApproveTarget(null);
      load();
    } catch (e) {
      alert(e.message);
    }
  };

  const reject = async (id) => {
    if (!confirm('해당 사용자를 승인 거절할까요?')) return;

    try {
      await userApi.reject(id);
      alert('거절 완료');
      load();
    } catch (e) {
      alert(e.message);
    }
  };

  return (
    <section className="page">
      <h2>사용자 관리</h2>

      <SearchPanel onSubmit={submit} onReset={reset}>
        <TextInput
          label="검색"
          name="keyword"
          value={filters.keyword}
          onChange={change}
          placeholder="아이디, 이름, 이메일 검색"
        />

        <SelectInput
          label="상태"
          name="status"
          value={filters.status}
          onChange={change}
        >
          <option value="">전체</option>
          {statuses.map((s) => (
            <option key={s.value} value={s.value}>
              {s.label}
            </option>
          ))}
        </SelectInput>

        <SelectInput
          label="권한"
          name="role"
          value={filters.role}
          onChange={change}
        >
          <option value="">전체</option>
          {roles.map((r) => (
            <option key={r.value} value={r.value}>
              {r.label}
            </option>
          ))}
        </SelectInput>

        <SelectInput
          label="학과/부서"
          name="departmentId"
          value={filters.departmentId}
          onChange={change}
        >
          <option value="">전체</option>
          {departments.map((d) => (
            <option key={d.id} value={d.id}>
              {d.displayName || d.name}
            </option>
          ))}
        </SelectInput>

        <SelectInput
          label="관리 부서"
          name="adminDepartment"
          value={filters.adminDepartment}
          onChange={change}
        >
          <option value="">전체</option>
          {adminDepartments.map((d) => (
            <option key={d.value} value={d.value}>
              {d.label}
            </option>
          ))}
        </SelectInput>
      </SearchPanel>

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

                  {isSystemAdmin && u.status === 'PENDING' && (
                    <button onClick={() => setApproveTarget(u)}>
                      승인
                    </button>
                  )}

                  {isSystemAdmin && u.status === 'PENDING' && (
                    <button
                      className="danger"
                      onClick={() => reject(u.id)}
                    >
                      거절
                    </button>
                  )}
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

      {approveTarget && (
        <DetailModal
          title="사용자 승인"
          onClose={() => setApproveTarget(null)}
        >
          <p>
            <b>{approveTarget.name}</b> 사용자에게 부여할 권한을 선택하세요.
          </p>

          <div className="form-grid one">
            <SelectInput
              label="권한"
              value={approveForm.role}
              onChange={(e) =>
                setApproveForm({ ...approveForm, role: e.target.value })
              }
            >
              {roles.map((r) => (
                <option key={r.value} value={r.value}>
                  {r.label}
                </option>
              ))}
            </SelectInput>

            <SelectInput
              label="관리 부서"
              value={approveForm.adminDepartment}
              onChange={(e) =>
                setApproveForm({ ...approveForm, adminDepartment: e.target.value })
              }
            >
              <option value="">해당 없음</option>
              {adminDepartments.map((d) => (
                <option key={d.value} value={d.value}>
                  {d.label}
                </option>
              ))}
            </SelectInput>
          </div>

          <button onClick={approve}>승인 처리</button>
        </DetailModal>
      )}
    </section>
  );
}

export default UserManagement;