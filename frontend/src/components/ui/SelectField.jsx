export function SelectField({ children, error, help, label, ...props }) {
  const id = props.id ?? props.name;

  return (
    <label className="form-field" htmlFor={id}>
      <span>{label}</span>
      <select id={id} {...props}>
        {children}
      </select>
      {help ? <small>{help}</small> : null}
      {error ? <strong className="field-error">{error}</strong> : null}
    </label>
  );
}
