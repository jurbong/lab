import { useEffect, useState } from "react";
import { inspectionApi } from "../../api/api";
import {
  DetailGrid,
  DetailModal,
  EmptyState,
  SearchPanel,
  TextArea,
  TextInput,
  SelectInput,
} from "../../components/FormControls";
import { isSafetyManager } from "../../utils/labels";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const initialFilters = {
  keyword: "",
  inspectionType: "",
};

const initialForm = {
  formName: "",
  inspectionType: "",
  description: "",
};

const inspectionTypes = ["일상점검", "정기점검", "특별점검"];

function InspectionManagement({ user, setPage, PAGES }) {
  const [filters, setFilters] = useState(initialFilters);

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
    if (!path) return "";
    if (path.startsWith("http")) return path;
    return `${API_BASE_URL}${path}`;
  };

  return (
    <section className="page">
      <div className="page-head">
        <h2>점검 양식 관리</h2>

        {isSafetyManager(user) && (
          <button onClick={() => setPage(PAGES.INSPECTION_CREATE)}>
            점검 양식 등록
          </button>
        )}
      </div>

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

        <SelectInput
          label="점검 유형"
          value={filters.inspectionType}
          onChange={(e) =>
            setFilters({ ...filters, inspectionType: e.target.value })
          }
        >
          <option value="">전체</option>
          {inspectionTypes.map((type) => (
            <option key={type} value={type}>
              {type}
            </option>
          ))}
        </SelectInput>
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
                <td>{f.inspectionType || "-"}</td>
                <td>{f.createdByName || "-"}</td>
                <td>{f.createdAt ? f.createdAt.slice(0, 10) : "-"}</td>
                <td>
                  <button
                    className="secondary"
                    onClick={() => openDetail(f.id)}
                  >
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
        <DetailModal
          title="점검 양식 상세 조회"
          onClose={() => setDetail(null)}
        >
          <DetailGrid
            rows={[
              ["양식명", detail.formName],
              ["점검 유형", detail.inspectionType],
              ["설명", detail.description],
              ["등록자", detail.createdByName],
            ]}
          />

          {detail.filePath && (
            <div className="detail-file">
              <h4>점검 양식 미리보기</h4>
              <a
                href={getFileUrl(detail.filePath)}
                target="_blank"
                rel="noreferrer"
                className="secondary"
              >
                새창에서 열기
              </a>

              <iframe
                src={getFileUrl(detail.filePath)}
                title="점검 양식 미리보기"
                className="inspection-form-preview"
              ></iframe>
            </div>
          )}
        </DetailModal>
      )}
    </section>
  );
}

export default InspectionManagement;
