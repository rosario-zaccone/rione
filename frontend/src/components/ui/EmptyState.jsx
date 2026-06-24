export function EmptyState({ action, children, title }) {
  return (
    <div className="empty-state">
      {title ? <h3>{title}</h3> : null}
      <p>{children}</p>
      {action}
    </div>
  );
}
