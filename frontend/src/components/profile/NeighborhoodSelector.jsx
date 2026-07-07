import { SelectField } from "../ui/SelectField";
import { Toast } from "../ui/Toast";

export function NeighborhoodSelector({
  disabled = false,
  error,
  label = "Quartiere",
  locationsError,
  neighborhoods,
  onChange,
  onDismissLocationsError,
  value,
}) {
  return (
    <div className="field-stack">
      <SelectField
        disabled={disabled || neighborhoods.length === 0}
        error={error}
        help={
          neighborhoods.length === 0
            ? "È necessario un catalogo di località reale prima di scegliere un quartiere."
            : "Puoi appartenere a un solo quartiere alla volta."
        }
        label={label}
        name="neighborhoodId"
        required
        value={value}
        onChange={(event) => onChange(event.target.value)}
      >
        <option value="">Seleziona un quartiere gestito</option>
        {neighborhoods.map((neighborhood) => (
          <option key={neighborhood.id} value={neighborhood.id}>
            {neighborhood.name}, {neighborhood.cityName}
          </option>
        ))}
      </SelectField>
      <Toast message={locationsError} tone="warning" onClose={onDismissLocationsError} />
    </div>
  );
}
