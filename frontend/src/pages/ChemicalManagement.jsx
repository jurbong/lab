import { useEffect, useState } from 'react';
import { chemicalApi, departmentApi, optionApi } from '../api/api';
import { DetailGrid, DetailModal, EmptyState, SearchPanel, SelectInput, TextInput } from '../components/FormControls';
import LabSelector from '../components/LabSelector';
import { isAdmin, riskLabel } from '../utils/labels';

const initialFilters = { keyword: '', riskLevel: '', labId: '', departmentId: '', storageLocation: '' };
const initialForm = { chemicalName: '', quantity: '', unit: 'kg', riskLevel: 'LOW', storageLocation: '', labId: '' };

function ChemicalManagement({ user }) {
  const [filters, setFilters] = useState(initialFilters);
  const [form, setForm] = useState(initialForm);
  const [items, setItems] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [units, setUnits] = useState([]);
  const [riskLevels, setRiskLevels] = useState([]);
  const [detail, setDetail] = useState(null);
  const [showCreate, setShowCreate] = useState(false);

  const load = async (next = filters) => { try { setItems(await chemicalApi.list(next)); } catch (e) { alert(e.message); } };
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
      await chemicalApi.create({ ...form, quantity: Number(form.quantity), labId: Number(form.labId) });
      alert('화학물질 등록 완료');
      setForm(initialForm); setShowCreate(false); load();
    } catch (error) { alert(error.message); }
  };
  const openDetail = async (id) => { try { setDetail(await chemicalApi.detail(id)); } catch (e) { alert(e.message); } };
  const reset = () => { setFilters(initialFilters); load(initialFilters); };

  return (
    <section className="page">
      <div className="page-head"><h2>화학물질 관리</h2>{isAdmin(user) && <button onClick={() => setShowCreate(!showCreate)}>{showCreate ? '등록 닫기' : '화학물질 등록'}</button>}</div>
      {isAdmin(user) && showCreate && <form className="create-card" onSubmit={create}>
        <h3>화학물질 등록</h3>
        <div className="form-grid">
          <TextInput label="화학물질명" value={form.chemicalName} onChange={(e) => setForm({ ...form, chemicalName: e.target.value })} required />
          <TextInput label="보유량" type="number" min="0" step="0.01" value={form.quantity} onChange={(e) => setForm({ ...form, quantity: e.target.value })} required />
          <SelectInput label="단위" value={form.unit} onChange={(e) => setForm({ ...form, unit: e.target.value })}>
            {units.map((u) => <option key={u.value} value={u.value}>{u.label}</option>)}
          </SelectInput>
          <SelectInput label="위험 등급" value={form.riskLevel} onChange={(e) => setForm({ ...form, riskLevel: e.target.value })}>
            {riskLevels.map((r) => <option key={r.value} value={r.value}>{r.label}</option>)}
          </SelectInput>
          <TextInput label="보관 위치" value={form.storageLocation} onChange={(e) => setForm({ ...form, storageLocation: e.target.value })} />
          <LabSelector value={form.labId} onChange={(value) => setForm({ ...form, labId: value })} required />
        </div>
        <button>등록</button>
      </form>}

      <SearchPanel onSubmit={(e) => { e.preventDefault(); load(filters); }} onReset={reset}>
        <TextInput label="검색" value={filters.keyword} onChange={(e) => setFilters({ ...filters, keyword: e.target.value })} placeholder="화학물질명, 보관 위치 검색" />
        <SelectInput label="위험 등급" value={filters.riskLevel} onChange={(e) => setFilters({ ...filters, riskLevel: e.target.value })}>
          <option value="">전체</option>{riskLevels.map((r) => <option key={r.value} value={r.value}>{r.label}</option>)}
        </SelectInput>
        <SelectInput label="학과/부서" value={filters.departmentId} onChange={(e) => setFilters({ ...filters, departmentId: e.target.value })}>
          <option value="">전체</option>{departments.map((d) => <option key={d.id} value={d.id}>{d.displayName || d.name}</option>)}
        </SelectInput>
        <TextInput label="보관 위치" value={filters.storageLocation} onChange={(e) => setFilters({ ...filters, storageLocation: e.target.value })} />
        <LabSelector label="연구실 필터" value={filters.labId} onChange={(value) => setFilters({ ...filters, labId: value })} departmentId={filters.departmentId} />
      </SearchPanel>

      <div className="table-wrap"><table>
        <thead><tr><th>화학물질명</th><th>보유량</th><th>위험 등급</th><th>연구실</th><th>학과</th><th>상세</th></tr></thead>
        <tbody>{items.map((c) => <tr key={c.id}>
          <td>{c.chemicalName}</td><td>{c.quantity ?? '-'} {c.unit || ''}</td><td>{riskLabel(c.riskLevel)}</td><td>{c.labName || '-'}</td><td>{c.departmentName || '-'}</td>
          <td><button className="secondary" onClick={() => openDetail(c.id)}>상세 조회</button></td>
        </tr>)}</tbody>
      </table>{items.length === 0 && <EmptyState />}</div>

      {detail && <DetailModal title="화학물질 상세 조회" onClose={() => setDetail(null)}>
        <DetailGrid rows={[
          ['화학물질명', detail.chemicalName], ['보유량', `${detail.quantity ?? '-'} ${detail.unit || ''}`], ['위험 등급', riskLabel(detail.riskLevel)],
          ['보관 위치', detail.storageLocation], ['연구실', detail.labName], ['학과', detail.departmentName], ['책임자', detail.managerName], ['등록자', detail.createdByName],
        ]} />
      </DetailModal>}
    </section>
  );
}
export default ChemicalManagement;
