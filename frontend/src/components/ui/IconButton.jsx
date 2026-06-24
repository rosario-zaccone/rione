export function IconButton({ children, className = "", label, ...props }) {
  return (
    <button className={`icon-button ${className}`.trim()} aria-label={label} type="button" {...props}>
      {children}
    </button>
  );
}
