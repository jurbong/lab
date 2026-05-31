import { useEffect, useState } from 'react';
import { departmentApi, laboratoryApi } from '../api/api';
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
  location: '',
  labType: '',
};

function LabManagement() {
  const [filters, setFilters] = useState(initialFilters);
  const [form, setForm] = useState(initialForm);
  const [imageFile, setImageFile] = useState(null);

  const [departments, setDepartments] = useState([]);
  const [labs, setLabs] = useState([]);
  const [detail, setDetail] = useState(null);
  const [showCreate, setShowCreate] = useState(false);

  const load = async (next = filters) => {
    try {
      setLabs(await laboratoryApi.list(next));
    } catch (e) {
      alert(e.message);
    }
  };

  useEffect(() => {
    departmentApi.list().then(setDepartments).catch(() => {});
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const submitSearch = (e) => {
    e.preventDefault();
    load(filters);
  };

  const reset = () => {
    setFilters(initialFilters);
    load(initialFilters);
  };

  const create = async (e) => {
    e.preventDefault();

    try {
      const payload = {
        ...form,
        departmentId: form.departmentId ? Number(form.departmentId) : null,
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
      setForm(initialForm);
      setImageFile(null);
      setShowCreate(false);
      load();
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

  return (
    <section className="page">
      <div className="page-head">
        <h2>연구실 관리</h2>
        <button onClick={() => setShowCreate(!showCreate)}>
          {showCreate ? '등록 닫기' : '연구실 등록'}
        </button>
      </div>

      {showCreate && (
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
              label="책임자명"
              value={form.managerName}
              onChange={(e) => setForm({ ...form, managerName: e.target.value })}
            />

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

            <label className="form-field">
              <span>연구실 이미지</span>
              <input
                type="file"
                accept="image/*"
                onChange={(e) => setImageFile(e.target.files?.[0] || null)}
              />
            </label>
          </div>

          <button type="submit">등록</button>
        </form>
      )}

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
        <table>
          <thead>
            <tr>
              <th>연구실명</th>
              <th>학과/부서</th>
              <th>책임자명</th>
              <th>상세</th>
            </tr>
          </thead>

          <tbody>
            {labs.map((lab) => (
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