import { useState } from "react";
import { Button } from "../ui/Button";
import { Card } from "../ui/Card";
import { EmptyState } from "../ui/EmptyState";
import { FormField } from "../ui/FormField";

function AddNeighborhoodForm({ cityId, loading, onAdd }) {
  const [name, setName] = useState("");

  async function handleSubmit(event) {
    event.preventDefault();
    if (!name.trim()) {
      return;
    }
    await onAdd(cityId, name.trim());
    setName("");
  }

  return (
    <form className="inline-form" onSubmit={handleSubmit}>
      <FormField
        label="Nuovo quartiere"
        maxLength="120"
        name={`neighborhood-${cityId}`}
        placeholder="Nome quartiere"
        required
        value={name}
        onChange={(event) => setName(event.target.value)}
      />
      <Button disabled={!name.trim()} loading={loading} type="submit">
        Aggiungi quartiere
      </Button>
    </form>
  );
}

export function CityList({ cities, loading, onAddNeighborhood, onRemove }) {
  if (cities.length === 0) {
    return (
      <EmptyState title="Nessuna città caricata">
        Le città create o caricate appariranno qui.
      </EmptyState>
    );
  }

  return (
    <div className="card-list">
      {cities.map((city) => (
        <Card className="city-card" key={city.id}>
          <div>
            <h3>{city.name}</h3>
            <p className="muted">{city.neighborhoods?.length ?? 0} quartieri</p>
          </div>
          <ul className="chip-list">
            {(city.neighborhoods ?? []).map((neighborhood) => (
              <li key={neighborhood.id}>{neighborhood.name}</li>
            ))}
          </ul>
          <AddNeighborhoodForm cityId={city.id} loading={loading} onAdd={onAddNeighborhood} />
          <Button variant="danger" loading={loading} onClick={() => onRemove(city)}>
            Rimuovi città
          </Button>
        </Card>
      ))}
    </div>
  );
}
