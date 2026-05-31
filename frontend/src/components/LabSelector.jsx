import { useEffect, useMemo, useState } from 'react';
import { laboratoryApi } from '../api/api';

function LabSelector({ value, onChange, departmentId, label = '연구실 선택', required = false }) {
  const [keyword, setKeyword] = useState('');
  const [labs, setLabs] = useState([]);
  const [loading, setLoading] = useState(false);

  const selected = useMemo(() => labs.find((lab) => String(lab.id) === String(value)), [labs, value]);

  const loadLabs = async () => {
    setLoading(true);
    try {
      const data = await laboratoryApi.options({ keyword, departmentId });
      setLabs(data || []);
    } catch (e) {
      alert(e.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadLabs();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [departmentId]);

  return (
    <div className="field lab-selector">
      <span>{label}</span>
      <div className="inline-search">
        <input
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === 'Enter') {
              e.preventDefault();
              loadLabs();
            }
          }}
          placeholder="연구실명, 학과, 책임자명 검색"
        />
        <button type="button" className="secondary" onClick={loadLabs}>{loading ? '검색 중' : '검색'}</button>
      </div>
      <select value={value || ''} onChange={(e) => onChange(e.target.value)} required={required}>
        <option value="">연구실 선택</option>
        {labs.map((lab) => (
          <option key={lab.id} value={lab.id}>{lab.label || `${lab.labName} / ${lab.departmentName || '-'} / ${lab.managerName || '-'}`}</option>
        ))}
      </select>
      {selected && <small>선택됨: {selected.labName} / {selected.departmentName || '-'} / {selected.managerName || '-'}</small>}
    </div>
  );
}

export default LabSelector;
