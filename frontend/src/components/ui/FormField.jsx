export function FormField({ error, help, label, textarea = false, ...props }) {
  const Field = textarea ? "textarea" : "input";
  const id = props.id ?? props.name;

  return (
    <label className="form-field" htmlFor={id}>
      <span>{label}</span>
      <Field id={id} {...props} />
      {help ? <small>{help}</small> : null}
      {error ? <strong className="field-error">{error}</strong> : null}
    </label>
  );
}
