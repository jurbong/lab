import { useEffect, useState } from 'react';
import { departmentApi, laboratoryApi, userApi } from '../api/api';
import { DetailGrid, DetailModal, EmptyState, SearchPanel, SelectInput, TextInput } from '../components/FormControls';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const initialFilters = {
  keyword: '',
  departmentId: '',
  labType: '',
};

const initialForm = {
  labName: '',
  departmentId: '',
  managerName: '',
  managerId: '',
  location: '',
  labType: '',
  memberIds: [],
};

function LabManagement({ user }) {
  const [filters, setFilters] = useState(initialFilters);
  const [form, setForm] = useState(initialForm);
  const [imageFile, setImageFile] = useState(null);

  const [departments, setDepartments] = useState([]);
  const [labs, setLabs] = useState([]);
  const [myLabs, setMyLabs] = useState([]);
  const [detail, setDetail] = useState(null);
  const [showCreate, setShowCreate] = useState(false);

  const [managerKeyword, setManagerKeyword] = useState('');
  const [managerResults, setManagerResults] = useState([]);
  const [selectedManager, setSelectedManager] = useState(null);

  const [memberKeyword, setMemberKeyword] = useState('');
  const [memberResults, setMemberResults] = useState([]);
  const [selectedMembers, setSelectedMembers] = useState([]);

  const isSafetyOrEducationDepartment =
    user?.adminDepartment === 'SAFETY' ||
    user?.adminDepartment === 'SAFETY_MANAGEMENT' ||
    user?.adminDepartment === 'SAFETY_DEPARTMENT' ||
    user?.adminDepartment === 'EDUCATION' ||
    user?.adminDepartment === 'EDUCATION_MANAGEMENT' ||
    user?.adminDepartment === 'EDUCATION_DEPARTMENT';

  const canCreateLab =
    ['ADMIN', 'GROUP_MANAGER', 'LAB_MEMBER'].includes(user?.role) &&
    !isSafetyOrEducationDepartment;

  const load = async (next = filters) => {
    try {
      setLabs(await laboratoryApi.list(next));
    } catch (e) {
      alert(e.message);
    }
  };

  const loadMyLabs = async () => {
    try {
      setMyLabs(await laboratoryApi.my());
    } catch (e) {
      console.warn(e.message);
    }
  };

  useEffect(() => {
    departmentApi.list().then(setDepartments).catch(() => {});
    load();
    loadMyLabs();
  }, []);

  const submitSearch = (e) => {
    e.preventDefault();
    load(filters);
  };

  const reset = () => {
    setFilters(initialFilters);
    load(initialFilters);
  };

  const searchManagers = async () => {
    try {
      const result = await userApi.options({
        keyword: managerKeyword,
        departmentId: form.departmentId || undefined,
      });
      setManagerResults(result);
    } catch (e) {
      alert(e.message);
    }
  };

  const searchMembers = async () => {
    try {
      const result = await userApi.options({
        keyword: memberKeyword,
        departmentId: form.departmentId || undefined,
      });
      setMemberResults(result);
    } catch (e) {
      alert(e.message);
    }
  };

  const selectManager = (targetUser) => {
    setSelectedManager(targetUser);

    setForm((prev) => ({
      ...prev,
      managerId: targetUser.id,
      managerName: targetUser.name,
      memberIds: Array.from(new Set([...prev.memberIds, targetUser.id])),
    }));

    setSelectedMembers((prev) => {
      const exists = prev.some((m) => m.id === targetUser.id);
      if (exists) return prev;
      return [...prev, targetUser];
    });
  };

  const addMember = (targetUser) => {
    setSelectedMembers((prev) => {
      const exists = prev.some((m) => m.id === targetUser.id);
      if (exists) return prev;
      return [...prev, targetUser];
    });

    setForm((prev) => ({
      ...prev,
      memberIds: Array.from(new Set([...prev.memberIds, targetUser.id])),
    }));
  };

  const removeMember = (targetUserId) => {
    if (selectedManager?.id === targetUserId) {
      alert('책임자는 구성원에서 제거할 수 없습니다.');
      return;
    }

    setSelectedMembers((prev) => prev.filter((m) => m.id !== targetUserId));

    setForm((prev) => ({
      ...prev,
      memberIds: prev.memberIds.filter((id) => id !== targetUserId),
    }));
  };

  const resetCreateForm = () => {
    setForm(initialForm);
    setImageFile(null);
    setManagerKeyword('');
    setManagerResults([]);
    setSelectedManager(null);
    setMemberKeyword('');
    setMemberResults([]);
    setSelectedMembers([]);
  };

  const create = async (e) => {
    e.preventDefault();

    if (!canCreateLab) {
      alert('연구실 등록 권한이 없습니다.');
      return;
    }

    try {
      const payload = {
        ...form,
        departmentId: form.departmentId ? Number(form.departmentId) : null,
        managerId: form.managerId ? Number(form.managerId) : null,
        memberIds: form.memberIds.map((id) => Number(id)),
      };

      const formData = new FormData();

      formData.append(
        'data',
        new Blob([JSON.stringify(payload)], {
          type: 'application/json',
        })
      );

      if (imageFile) {
        formData.append('image', imageFile);
      }

      await laboratoryApi.create(formData);

      alert('연구실 등록 완료');
      resetCreateForm();
      setShowCreate(false);
      load();
      loadMyLabs();
    } catch (error) {
      alert(error.message);
    }
  };

  const openDetail = async (id) => {
    try {
      setDetail(await laboratoryApi.detail(id));
    } catch (e) {
      alert(e.message);
    }
  };

  const getFileUrl = (path) => {
    if (!path) return '';
    if (path.startsWith('http')) return path;
    return `${API_BASE_URL}${path}`;
  };

  const userDisplay = (targetUser) => {
    return `${targetUser.name || '-'} / ${targetUser.userId || '-'} / ${targetUser.departmentDisplayName || targetUser.departmentName || '-'}`;
  };

  const renderLabRows = (targetLabs) => (
    <tbody>
      {targetLabs.map((lab) => (
        <tr key={lab.id}>
          <td>{lab.labName}</td>
          <td>{lab.departmentDisplayName || lab.departmentName || '-'}</td>
          <td>{lab.managerName || '-'}</td>
          <td>
            <button className="secondary" onClick={() => openDetail(lab.id)}>
              상세 조회
            </button>
          </td>
        </tr>
      ))}
    </tbody>
  );

  return (
    <section className="page">
      <div className="page-head">
        <h2>연구실 관리</h2>

        {canCreateLab && (
          <button onClick={() => setShowCreate(!showCreate)}>
            {showCreate ? '등록 닫기' : '연구실 등록'}
          </button>
        )}
      </div>

      {showCreate && canCreateLab && (
        <form className="create-card" onSubmit={create}>
          <h3>연구실 등록</h3>

          <div className="form-grid">
            <TextInput
              label="연구실명"
              value={form.labName}
              onChange={(e) => setForm({ ...form, labName: e.target.value })}
              required
            />

            <SelectInput
              label="학과/부서"
              value={form.departmentId}
              onChange={(e) => setForm({ ...form, departmentId: e.target.value })}
              required
            >
              <option value="">선택</option>
              {departments.map((d) => (
                <option key={d.id} value={d.id}>
                  {d.displayName || d.name}
                </option>
              ))}
            </SelectInput>

            <TextInput
              label="위치"
              value={form.location}
              onChange={(e) => setForm({ ...form, location: e.target.value })}
            />

            <TextInput
              label="연구실 타입"
              value={form.labType}
              onChange={(e) => setForm({ ...form, labType: e.target.value })}
              placeholder="예: 실험실, 일반연구실"
            />

            <TextInput
              label="연구실 이미지"
              type="file"
              accept="image/png,image/jpeg,image/jpg,image/webp"
              buttonText="이미지 선택"
              helperText="PNG, JPG, JPEG, WEBP 형식의 이미지를 업로드하세요."
              onChange={(e) => setImageFile(e.target.files?.[0] || null)}
            />
          </div>

          <div className="create-card" style={{ marginTop: '16px' }}>
            <h4>책임자 선택</h4>

            <div className="form-grid">
              <TextInput
                label="책임자 검색"
                value={managerKeyword}
                onChange={(e) => setManagerKeyword(e.target.value)}
                placeholder="이름, 아이디, 학과/부서 검색"
              />

              <label className="form-field">
                <span>&nbsp;</span>
                <button type="button" onClick={searchManagers}>
                  검색
                </button>
              </label>
            </div>

            <div className="table-wrap" style={{ marginTop: '8px' }}>
              <table>
                <thead>
                  <tr>
                    <th>이름</th>
                    <th>아이디</th>
                    <th>학과/부서</th>
                    <th>선택</th>
                  </tr>
                </thead>

                <tbody>
                  {managerResults.map((targetUser) => (
                    <tr key={targetUser.id}>
                      <td>{targetUser.name}</td>
                      <td>{targetUser.userId}</td>
                      <td>{targetUser.departmentDisplayName || targetUser.departmentName || '-'}</td>
                      <td>
                        <button type="button" onClick={() => selectManager(targetUser)}>
                          선택
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>

              {managerResults.length === 0 && <EmptyState />}
            </div>

            <div style={{ marginTop: '12px' }}>
              <b>선택된 책임자</b>
              <p>{selectedManager ? userDisplay(selectedManager) : '선택된 책임자가 없습니다.'}</p>
            </div>
          </div>

          <div className="create-card" style={{ marginTop: '16px' }}>
            <h4>연구실 구성원 선택</h4>

            <div className="form-grid">
              <TextInput
                label="구성원 검색"
                value={memberKeyword}
                onChange={(e) => setMemberKeyword(e.target.value)}
                placeholder="이름, 아이디, 학과/부서 검색"
              />

              <label className="form-field">
                <span>&nbsp;</span>
                <button type="button" onClick={searchMembers}>
                  검색
                </button>
              </label>
            </div>

            <div className="table-wrap" style={{ marginTop: '8px' }}>
              <table>
                <thead>
                  <tr>
                    <th>이름</th>
                    <th>아이디</th>
                    <th>학과/부서</th>
                    <th>추가</th>
                  </tr>
                </thead>

                <tbody>
                  {memberResults.map((targetUser) => (
                    <tr key={targetUser.id}>
                      <td>{targetUser.name}</td>
                      <td>{targetUser.userId}</td>
                      <td>{targetUser.departmentDisplayName || targetUser.departmentName || '-'}</td>
                      <td>
                        <button type="button" onClick={() => addMember(targetUser)}>
                          추가
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>

              {memberResults.length === 0 && <EmptyState />}
            </div>

            <div style={{ marginTop: '12px' }}>
              <b>선택된 구성원</b>

              {selectedMembers.length === 0 ? (
                <p>선택된 구성원이 없습니다.</p>
              ) : (
                <div className="table-wrap" style={{ marginTop: '8px' }}>
                  <table>
                    <thead>
                      <tr>
                        <th>이름</th>
                        <th>아이디</th>
                        <th>학과/부서</th>
                        <th>관리</th>
                      </tr>
                    </thead>

                    <tbody>
                      {selectedMembers.map((member) => (
                        <tr key={member.id}>
                          <td>
                            {member.name}
                            {selectedManager?.id === member.id ? ' (책임자)' : ''}
                          </td>
                          <td>{member.userId}</td>
                          <td>{member.departmentDisplayName || member.departmentName || '-'}</td>
                          <td>
                            <button
                              type="button"
                              className="danger"
                              onClick={() => removeMember(member.id)}
                              disabled={selectedManager?.id === member.id}
                            >
                              삭제
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>

          <button type="submit">등록</button>
        </form>
      )}

      <div style={{ marginTop: '16px' }}>
        <h3>내 연구실</h3>

        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>연구실명</th>
                <th>학과/부서</th>
                <th>책임자명</th>
                <th>상세</th>
              </tr>
            </thead>

            {renderLabRows(myLabs)}
          </table>

          {myLabs.length === 0 && <EmptyState />}
        </div>
      </div>

      <SearchPanel onSubmit={submitSearch} onReset={reset}>
        <TextInput
          label="검색"
          value={filters.keyword}
          onChange={(e) => setFilters({ ...filters, keyword: e.target.value })}
          placeholder="연구실명, 책임자명 검색"
        />

        <SelectInput
          label="학과/부서"
          value={filters.departmentId}
          onChange={(e) => setFilters({ ...filters, departmentId: e.target.value })}
        >
          <option value="">전체</option>
          {departments.map((d) => (
            <option key={d.id} value={d.id}>
              {d.displayName || d.name}
            </option>
          ))}
        </SelectInput>

        <TextInput
          label="연구실 타입"
          value={filters.labType}
          onChange={(e) => setFilters({ ...filters, labType: e.target.value })}
          placeholder="예: 실험실"
        />
      </SearchPanel>

      <div className="table-wrap">
        <h3>전체 연구실</h3>

        <table>
          <thead>
            <tr>
              <th>연구실명</th>
              <th>학과/부서</th>
              <th>책임자명</th>
              <th>상세</th>
            </tr>
          </thead>

          {renderLabRows(labs)}
        </table>

        {labs.length === 0 && <EmptyState />}
      </div>

      {detail && (
        <DetailModal title="연구실 상세 조회" onClose={() => setDetail(null)}>
          <DetailGrid
            rows={[
              ['연구실명', detail.labName],
              ['학과/부서', detail.departmentDisplayName || detail.departmentName],
              ['책임자명', detail.managerName],
              ['위치', detail.location],
              ['연구실 타입', detail.labType],
              ['등록자', detail.createdByName],
            ]}
          />

          <div style={{ marginTop: '16px' }}>
            <h4>연구실 구성원</h4>

            {detail.members && detail.members.length > 0 ? (
              <div className="table-wrap" style={{ marginTop: '8px' }}>
                <table>
                  <thead>
                    <tr>
                      <th>이름</th>
                      <th>아이디</th>
                      <th>학과/부서</th>
                      <th>구분</th>
                    </tr>
                  </thead>

                  <tbody>
                    {detail.members.map((member) => (
                      <tr key={member.id}>
                        <td>
                          {member.name || '-'}
                          {detail.managerName && member.name === detail.managerName ? ' (책임자)' : ''}
                        </td>
                        <td>{member.loginId || '-'}</td>
                        <td>{member.departmentDisplayName || member.departmentName || '-'}</td>
                        <td>{member.memberRole || '-'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <p>등록된 연구실 구성원이 없습니다.</p>
            )}
          </div>

          {detail.imageUrl && (
            <div className="detail-file">
              <h4>연구실 이미지</h4>
              <img
                src={getFileUrl(detail.imageUrl)}
                alt="연구실 이미지"
                style={{
                  maxWidth: '100%',
                  maxHeight: '300px',
                  borderRadius: '12px',
                  border: '1px solid #ddd',
                  objectFit: 'cover',
                }}
              />
            </div>
          )}
        </DetailModal>
      )}
    </section>
  );
}

export default LabManagement;