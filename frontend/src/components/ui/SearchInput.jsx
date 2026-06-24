export function SearchInput({ label = "Search", value, onChange, placeholder, ...props }) {
  return (
    <label className="search-field">
      <span className="sr-only">{label}</span>
      <input
        aria-label={label}
        placeholder={placeholder}
        type="search"
        value={value}
        onChange={(event) => onChange(event.target.value)}
        {...props}
      />
    </label>
  );
}
