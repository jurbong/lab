import { useEffect, useState } from 'react';
import { inspectionApi } from '../api/api';
import { DetailGrid, DetailModal, EmptyState, SearchPanel, TextArea, TextInput } from '../components/FormControls';
import { isSafetyManager } from '../utils/labels';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const initialFilters = {
  keyword: '',
  inspectionType: '',
};

const initialForm = {
  formName: '',
  inspectionType: '',
  description: '',
};

function InspectionManagement({ user }) {
  const [filters, setFilters] = useState(initialFilters);
  const [form, setForm] = useState(initialForm);
  const [formFile, setFormFile] = useState(null);

  const [items, setItems] = useState([]);
  const [detail, setDetail] = useState(null);
  const [showCreate, setShowCreate] = useState(false);

  const load = async (next = filters) => {
    try {
      setItems(await inspectionApi.list(next));
    } catch (e) {
      alert(e.message);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const create = async (e) => {
    e.preventDefault();

    try {
      const formData = new FormData();

      formData.append(
        'data',
        new Blob([JSON.stringify(form)], {
          type: 'application/json',
        })
      );

      if (formFile) {
        formData.append('file', formFile);
      }

      await inspectionApi.create(formData);

      alert('점검 양식 등록 완료');
      setForm(initialForm);
      setFormFile(null);
      setShowCreate(false);
      load();
    } catch (error) {
      alert(error.message);
    }
  };

  const openDetail = async (id) => {
    try {
      setDetail(await inspectionApi.detail(id));
    } catch (e) {
      alert(e.message);
    }
  };

  const reset = () => {
    setFilters(initialFilters);
    load(initialFilters);
  };

  const getFileUrl = (path) => {
    if (!path) return '';
    if (path.startsWith('http')) return path;
    return `${API_BASE_URL}${path}`;
  };

  return (
    <section className="page">
      <div className="page-head">
        <h2>점검 양식 관리</h2>

        {isSafetyManager(user) && (
          <button onClick={() => setShowCreate(!showCreate)}>
            {showCreate ? '등록 닫기' : '점검 양식 등록'}
          </button>
        )}
      </div>

      {isSafetyManager(user) && showCreate && (
        <form className="create-card" onSubmit={create}>
          <h3>점검 양식 등록</h3>

          <div className="form-grid">
            <TextInput
              label="양식명"
              value={form.formName}
              onChange={(e) => setForm({ ...form, formName: e.target.value })}
              required
            />

            <TextInput
              label="점검 유형"
              value={form.inspectionType}
              onChange={(e) => setForm({ ...form, inspectionType: e.target.value })}
              placeholder="예: 정기점검, 특별점검"
            />

            <TextInput
              label="점검 양식 파일"
              type="file"
              accept=".pdf,.hwp,.hwpx,.doc,.docx,.xls,.xlsx"
              buttonText="양식 파일 선택"
              helperText="PDF, HWP, DOCX, XLSX 형식의 점검 양식을 업로드하세요."
              onChange={(e) => setFormFile(e.target.files?.[0] || null)}
            />

            <TextArea
              label="설명"
              value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
            />
          </div>

          <button type="submit">등록</button>
        </form>
      )}

      <SearchPanel
        onSubmit={(e) => {
          e.preventDefault();
          load(filters);
        }}
        onReset={reset}
      >
        <TextInput
          label="검색"
          value={filters.keyword}
          onChange={(e) => setFilters({ ...filters, keyword: e.target.value })}
          placeholder="양식명, 설명 검색"
        />

        <TextInput
          label="점검 유형"
          value={filters.inspectionType}
          onChange={(e) => setFilters({ ...filters, inspectionType: e.target.value })}
          placeholder="예: 정기점검"
        />
      </SearchPanel>

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>양식명</th>
              <th>점검 유형</th>
              <th>등록자</th>
              <th>등록일</th>
              <th>상세</th>
            </tr>
          </thead>

          <tbody>
            {items.map((f) => (
              <tr key={f.id}>
                <td>{f.formName}</td>
                <td>{f.inspectionType || '-'}</td>
                <td>{f.createdByName || '-'}</td>
                <td>{f.createdAt ? f.createdAt.slice(0, 10) : '-'}</td>
                <td>
                  <button className="secondary" onClick={() => openDetail(f.id)}>
                    상세 조회
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {items.length === 0 && <EmptyState />}
      </div>

      {detail && (
        <DetailModal title="점검 양식 상세 조회" onClose={() => setDetail(null)}>
          <DetailGrid
            rows={[
              ['양식명', detail.formName],
              ['점검 유형', detail.inspectionType],
              ['설명', detail.description],
              ['등록자', detail.createdByName],
            ]}
          />

          {detail.filePath && (
            <div className="detail-file">
              <h4>점검 양식 파일</h4>
              <a
                href={getFileUrl(detail.filePath)}
                target="_blank"
                rel="noreferrer"
                className="secondary"
              >
                파일 열기 / 다운로드
              </a>
            </div>
          )}
        </DetailModal>
      )}
    </section>
  );
}

export default InspectionManagement;