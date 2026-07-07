export function Button({
  children,
  className = "",
  disabled = false,
  loading = false,
  type = "button",
  variant = "primary",
  ...props
}) {
  return (
    <button
      className={`button ${variant} ${className}`.trim()}
      disabled={disabled || loading}
      type={type}
      {...props}
    >
      {loading ? "Caricamento..." : children}
    </button>
  );
}
