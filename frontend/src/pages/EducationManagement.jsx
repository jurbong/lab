import { useEffect, useState } from 'react';
import { educationApi } from '../api/api';
import { DetailGrid, DetailModal, EmptyState, SearchPanel, TextArea, TextInput } from '../components/FormControls';
import { isEducationManager } from '../utils/labels';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const initialFilters = {
  keyword: '',
  educationType: '',
};

const initialForm = {
  title: '',
  educationType: '',
  description: '',
};

function EducationManagement({ user }) {
  const [filters, setFilters] = useState(initialFilters);
  const [form, setForm] = useState(initialForm);
  const [videoFile, setVideoFile] = useState(null);

  const [items, setItems] = useState([]);
  const [detail, setDetail] = useState(null);
  const [showCreate, setShowCreate] = useState(false);

  const load = async (next = filters) => {
    try {
      setItems(await educationApi.list(next));
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

      if (videoFile) {
        formData.append('video', videoFile);
      }

      await educationApi.create(formData);

      alert('교육 동영상 등록 완료');
      setForm(initialForm);
      setVideoFile(null);
      setShowCreate(false);
      load();
    } catch (error) {
      alert(error.message);
    }
  };

  const openDetail = async (id) => {
    try {
      setDetail(await educationApi.detail(id));
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
        <h2>안전교육 관리</h2>

        {isEducationManager(user) && (
          <button onClick={() => setShowCreate(!showCreate)}>
            {showCreate ? '등록 닫기' : '교육 동영상 등록'}
          </button>
        )}
      </div>

      {isEducationManager(user) && showCreate && (
        <form className="create-card" onSubmit={create}>
          <h3>교육 동영상 등록</h3>

          <div className="form-grid">
            <TextInput
              label="제목"
              value={form.title}
              onChange={(e) => setForm({ ...form, title: e.target.value })}
              required
            />

            <TextInput
              label="교육 유형"
              value={form.educationType}
              onChange={(e) => setForm({ ...form, educationType: e.target.value })}
              placeholder="예: 신규교육, 정기교육"
            />

            <TextInput
              label="교육 동영상"
              type="file"
              accept="video/mp4,video/webm,video/quicktime,video/x-msvideo"
              buttonText="동영상 선택"
              helperText="MP4, WEBM, MOV, AVI 형식의 교육 동영상을 업로드하세요."
              onChange={(e) => setVideoFile(e.target.files?.[0] || null)}
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
          placeholder="제목, 설명 검색"
        />

        <TextInput
          label="교육 유형"
          value={filters.educationType}
          onChange={(e) => setFilters({ ...filters, educationType: e.target.value })}
          placeholder="예: 정기교육"
        />
      </SearchPanel>

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>제목</th>
              <th>교육 유형</th>
              <th>등록자</th>
              <th>등록일</th>
              <th>상세</th>
            </tr>
          </thead>

          <tbody>
            {items.map((v) => (
              <tr key={v.id}>
                <td>{v.title}</td>
                <td>{v.educationType || '-'}</td>
                <td>{v.createdByName || '-'}</td>
                <td>{v.createdAt ? v.createdAt.slice(0, 10) : '-'}</td>
                <td>
                  <button className="secondary" onClick={() => openDetail(v.id)}>
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
        <DetailModal title="안전교육 상세 조회" onClose={() => setDetail(null)}>
          <DetailGrid
            rows={[
              ['제목', detail.title],
              ['교육 유형', detail.educationType],
              ['설명', detail.description],
              ['등록자', detail.createdByName],
            ]}
          />

          {detail.filePath && (
            <div className="detail-file">
              <h4>교육 동영상</h4>

              <video
                src={getFileUrl(detail.filePath)}
                controls
                style={{
                  width: '100%',
                  maxHeight: '420px',
                  borderRadius: '12px',
                  border: '1px solid #ddd',
                  background: '#000',
                }}
              />

              <div style={{ marginTop: '10px' }}>
                <a
                  href={getFileUrl(detail.filePath)}
                  target="_blank"
                  rel="noreferrer"
                  className="secondary"
                >
                  동영상 새 창에서 열기
                </a>
              </div>
            </div>
          )}
        </DetailModal>
      )}
    </section>
  );
}

export default EducationManagement;