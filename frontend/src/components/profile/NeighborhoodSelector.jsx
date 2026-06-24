import { SelectField } from "../ui/SelectField";

export function NeighborhoodSelector({
  disabled = false,
  error,
  label = "Neighborhood",
  locationsError,
  neighborhoods,
  onChange,
  value,
}) {
  return (
    <div className="field-stack">
      <SelectField
        disabled={disabled || neighborhoods.length === 0}
        error={error}
        help={
          neighborhoods.length === 0
            ? "A real location catalog is required before choosing a neighborhood."
            : "You can belong to one neighborhood at a time."
        }
        label={label}
        name="neighborhoodId"
        required
        value={value}
        onChange={(event) => onChange(event.target.value)}
      >
        <option value="">Select a managed neighborhood</option>
        {neighborhoods.map((neighborhood) => (
          <option key={neighborhood.id} value={neighborhood.id}>
            {neighborhood.name}, {neighborhood.cityName}
          </option>
        ))}
      </SelectField>
      {locationsError ? <p className="inline-warning">{locationsError}</p> : null}
    </div>
  );
}
