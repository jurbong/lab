import { useEffect, useState } from 'react';
import { departmentApi, optionApi, wasteApi } from '../api/api';
import { DetailGrid, DetailModal, EmptyState, SearchPanel, SelectInput, TextInput } from '../components/FormControls';
import LabSelector from '../components/LabSelector';
import { riskLabel } from '../utils/labels';

const initialFilters = { keyword: '', wasteType: '', hazardLevel: '', labId: '', departmentId: '', unit: '' };
const initialForm = { wasteType: '', quantity: '', unit: 'kg', generatedDate: '', storageLocation: '', hazardLevel: 'LOW', labId: '' };

function WasteManagement({ user }) {
  const [filters, setFilters] = useState(initialFilters);
  const [form, setForm] = useState(initialForm);
  const [items, setItems] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [units, setUnits] = useState([]);
  const [riskLevels, setRiskLevels] = useState([]);
  const [detail, setDetail] = useState(null);
  const [showCreate, setShowCreate] = useState(false);

  const canCreateWaste = ['ADMIN', 'LAB_MEMBER'].includes(user?.role);

  const load = async (next = filters) => { try { setItems(await wasteApi.list(next)); } catch (e) { alert(e.message); } };
  useEffect(() => {
    departmentApi.list().then(setDepartments).catch(() => {});
    optionApi.units().then(setUnits).catch(() => {});
    optionApi.riskLevels().then(setRiskLevels).catch(() => {});
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const create = async (e) => {
    e.preventDefault();
    try {
      await wasteApi.create({
        ...form,
        quantity: Number(form.quantity),
        labId: Number(form.labId),
        generatedDate: form.generatedDate || null,
      });
      alert('폐기물 등록 완료');
      setForm(initialForm); setShowCreate(false); load();
    } catch (error) { alert(error.message); }
  };
  const openDetail = async (id) => { try { setDetail(await wasteApi.detail(id)); } catch (e) { alert(e.message); } };
  const reset = () => { setFilters(initialFilters); load(initialFilters); };

  return (
    <section className="page">
      <div className="page-head">
        <h2>폐기물 관리</h2>
        {canCreateWaste && (
          <button onClick={() => setShowCreate(!showCreate)}>
            {showCreate ? '등록 닫기' : '폐기물 등록'}
          </button>
        )}
      </div>

      {showCreate && <form className="create-card" onSubmit={create}>
        <h3>폐기물 등록</h3>
        <p>담당자 ID는 입력하지 않습니다. 등록 시 로그인한 사용자가 담당자로 저장됩니다.</p>
        <div className="form-grid">
          <TextInput label="폐기물 종류" value={form.wasteType} onChange={(e) => setForm({ ...form, wasteType: e.target.value })} required />
          <TextInput label="수량" type="number" min="0" step="0.01" value={form.quantity} onChange={(e) => setForm({ ...form, quantity: e.target.value })} required />
          <SelectInput label="단위" value={form.unit} onChange={(e) => setForm({ ...form, unit: e.target.value })}>
            {units.map((u) => <option key={u.value} value={u.value}>{u.label}</option>)}
          </SelectInput>
          <TextInput label="발생일" type="date" value={form.generatedDate} onChange={(e) => setForm({ ...form, generatedDate: e.target.value })} />
          <TextInput label="보관 위치" value={form.storageLocation} onChange={(e) => setForm({ ...form, storageLocation: e.target.value })} />
          <SelectInput label="유해성 등급" value={form.hazardLevel} onChange={(e) => setForm({ ...form, hazardLevel: e.target.value })}>
            {riskLevels.map((r) => <option key={r.value} value={r.value}>{r.label}</option>)}
          </SelectInput>
          <LabSelector value={form.labId} onChange={(value) => setForm({ ...form, labId: value })} label="내 권한 연구실 선택" required />
        </div>
        <button>등록</button>
      </form>}

      <SearchPanel onSubmit={(e) => { e.preventDefault(); load(filters); }} onReset={reset}>
        <TextInput label="검색" value={filters.keyword} onChange={(e) => setFilters({ ...filters, keyword: e.target.value })} placeholder="폐기물 종류, 보관 위치 검색" />
        <TextInput label="폐기물 종류" value={filters.wasteType} onChange={(e) => setFilters({ ...filters, wasteType: e.target.value })} />
        <SelectInput label="유해성 등급" value={filters.hazardLevel} onChange={(e) => setFilters({ ...filters, hazardLevel: e.target.value })}>
          <option value="">전체</option>{riskLevels.map((r) => <option key={r.value} value={r.value}>{r.label}</option>)}
        </SelectInput>
        <SelectInput label="단위" value={filters.unit} onChange={(e) => setFilters({ ...filters, unit: e.target.value })}>
          <option value="">전체</option>{units.map((u) => <option key={u.value} value={u.value}>{u.label}</option>)}
        </SelectInput>
        <SelectInput label="학과/부서" value={filters.departmentId} onChange={(e) => setFilters({ ...filters, departmentId: e.target.value })}>
          <option value="">전체</option>{departments.map((d) => <option key={d.id} value={d.id}>{d.displayName || d.name}</option>)}
        </SelectInput>
        <LabSelector label="연구실 필터" value={filters.labId} onChange={(value) => setFilters({ ...filters, labId: value })} departmentId={filters.departmentId} />
      </SearchPanel>

      <div className="table-wrap"><table>
        <thead><tr><th>폐기물 종류</th><th>수량</th><th>유해성 등급</th><th>연구실</th><th>발생일</th><th>담당자</th><th>상세</th></tr></thead>
        <tbody>{items.map((w) => <tr key={w.id}>
          <td>{w.wasteType}</td><td>{w.quantity ?? '-'} {w.unit || ''}</td><td>{riskLabel(w.hazardLevel)}</td><td>{w.labName || '-'}</td><td>{w.generatedDate || '-'}</td><td>{w.handlerName || '-'}</td>
          <td><button className="secondary" onClick={() => openDetail(w.id)}>상세 조회</button></td>
        </tr>)}</tbody>
      </table>{items.length === 0 && <EmptyState />}</div>

      {detail && <DetailModal title="폐기물 상세 조회" onClose={() => setDetail(null)}>
        <DetailGrid rows={[
          ['폐기물 종류', detail.wasteType], ['수량', `${detail.quantity ?? '-'} ${detail.unit || ''}`], ['발생일', detail.generatedDate], ['보관 위치', detail.storageLocation],
          ['유해성 등급', riskLabel(detail.hazardLevel)], ['연구실', detail.labName], ['담당자', detail.handlerName],
        ]} />
      </DetailModal>}
    </section>
  );
}
export default WasteManagement;