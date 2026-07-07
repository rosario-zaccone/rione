import { useState } from "react";
import { Button } from "../ui/Button";
import { FormField } from "../ui/FormField";

export function CityForm({ loading, onCreate }) {
  const [name, setName] = useState("");
  const [neighborhoods, setNeighborhoods] = useState("");

  async function handleSubmit(event) {
    event.preventDefault();
    const values = neighborhoods
      .split("\n")
      .map((item) => item.trim())
      .filter(Boolean);
    await onCreate({ name: name.trim(), neighborhoods: values });
    setName("");
    setNeighborhoods("");
  }

  const canSubmit = name.trim() && neighborhoods.split("\n").some((item) => item.trim());

  return (
    <form className="form-card compact-card" onSubmit={handleSubmit}>
      <div>
        <p className="eyebrow">Amministrazione</p>
        <h2>Inserisci città</h2>
      </div>
      <FormField
        label="Nome città"
        maxLength="120"
        name="cityName"
        required
        value={name}
        onChange={(event) => setName(event.target.value)}
      />
      <FormField
        help="Un quartiere per riga. Ne è richiesto almeno uno."
        label="Quartieri"
        maxLength="500"
        name="neighborhoods"
        required
        rows="5"
        textarea
        value={neighborhoods}
        onChange={(event) => setNeighborhoods(event.target.value)}
      />
      <Button disabled={!canSubmit} loading={loading} type="submit">
        Crea città
      </Button>
    </form>
  );
}
