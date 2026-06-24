import { useState } from "react";
import { CityForm } from "./CityForm";
import { CityList } from "./CityList";
import { Button } from "../ui/Button";
import { Card } from "../ui/Card";
import { FormField } from "../ui/FormField";

export function CityManager({ error, locations, loading, onCreate, onLoadCity, onRemove }) {
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
        <p className="eyebrow">Admin Locations</p>
        <h1>City and neighborhood management</h1>
        <p>
          Only admin users can create or remove platform-managed locations. Removal is rejected by
          the backend while users still belong to the city neighborhoods.
        </p>
        {error ? <p className="form-error">{error}</p> : null}
      </Card>
      <div className="two-col align-start">
        <CityForm loading={loading} onCreate={onCreate} />
        <form className="form-card compact-card" onSubmit={handleLoad}>
          <div>
            <p className="eyebrow">Real lookup</p>
            <h2>Load city by ID</h2>
            <p className="muted">
              The current backend does not expose a full GET /cities catalog endpoint.
            </p>
          </div>
          <FormField
            label="City ID"
            min="1"
            name="cityId"
            required
            type="number"
            value={cityId}
            onChange={(event) => setCityId(event.target.value)}
          />
          <Button loading={loading} type="submit">
            Load city
          </Button>
        </form>
      </div>
      <CityList cities={locations.cities} loading={loading} onRemove={onRemove} />
    </div>
  );
}
