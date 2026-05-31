export function TextInput({ label, ...props }) {
  return (
    <label className="field">
      <span>{label}</span>
      <input {...props} />
    </label>
  );
}

export function SelectInput({ label, children, ...props }) {
  return (
    <label className="field">
      <span>{label}</span>
      <select {...props}>{children}</select>
    </label>
  );
}

export function TextArea({ label, ...props }) {
  return (
    <label className="field full">
      <span>{label}</span>
      <textarea rows="4" {...props} />
    </label>
  );
}

export function SearchPanel({ children, onSubmit, onReset }) {
  return (
    <form className="search-panel" onSubmit={onSubmit}>
      {children}
      <div className="filter-actions">
        <button type="submit">검색</button>
        {onReset && <button type="button" className="secondary" onClick={onReset}>초기화</button>}
      </div>
    </form>
  );
}

export function EmptyState({ message = '조회 결과가 없습니다.' }) {
  return <div className="empty">{message}</div>;
}

export function DetailModal({ title, onClose, children }) {
  return (
    <div className="modal-backdrop" onMouseDown={onClose}>
      <section className="modal" onMouseDown={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>{title}</h3>
          <button className="ghost" onClick={onClose}>닫기</button>
        </div>
        <div className="modal-body">{children}</div>
      </section>
    </div>
  );
}

export function DetailGrid({ rows }) {
  return (
    <dl className="detail-grid">
      {rows.map(([label, value]) => (
        <div key={label}>
          <dt>{label}</dt>
          <dd>{value || '-'}</dd>
        </div>
      ))}
    </dl>
  );
}
