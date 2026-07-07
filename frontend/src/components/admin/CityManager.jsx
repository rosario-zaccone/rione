import { useState } from "react";
import { CityForm } from "./CityForm";
import { CityList } from "./CityList";
import { Button } from "../ui/Button";
import { Card } from "../ui/Card";
import { FormField } from "../ui/FormField";
import { Toast } from "../ui/Toast";

export function CityManager({ error, locations, loading, onCreate, onDismissError, onLoadCity, onRemove }) {
  const [cityId, setCityId] = useState("");

  function handleLoad(event) {
    event.preventDefault();
    if (cityId) {
      onLoadCity(cityId);
    }
  }

  return (
    <div className="page-grid">
      <Card className="page-intro">
        <p className="eyebrow">Gestione località</p>
        <h1>Gestione città e quartieri</h1>
        <p>Gestisci le città e i quartieri disponibili per i residenti.</p>
        <Toast message={error} tone="error" onClose={onDismissError} />
      </Card>
      <div className="two-col align-start">
        <CityForm loading={loading} onCreate={onCreate} />
        <form className="form-card compact-card" onSubmit={handleLoad}>
          <div>
            <p className="eyebrow">Ricerca città</p>
            <h2>Carica città per ID</h2>
            <p className="muted">
              Cerca per ID città per rivedere una località esistente.
            </p>
          </div>
          <FormField
            label="ID città"
            min="1"
            name="cityId"
            required
            type="number"
            value={cityId}
            onChange={(event) => setCityId(event.target.value)}
          />
          <Button loading={loading} type="submit">
            Carica città
          </Button>
        </form>
      </div>
      <CityList cities={locations.cities} loading={loading} onRemove={onRemove} />
    </div>
  );
}
