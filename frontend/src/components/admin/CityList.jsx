import { Button } from "../ui/Button";
import { Card } from "../ui/Card";
import { EmptyState } from "../ui/EmptyState";

export function CityList({ cities, loading, onRemove }) {
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
          <Button variant="danger" loading={loading} onClick={() => onRemove(city)}>
            Rimuovi città
          </Button>
        </Card>
      ))}
    </div>
  );
}
