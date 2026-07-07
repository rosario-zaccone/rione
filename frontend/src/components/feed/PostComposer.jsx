import { useState } from "react";
import { Avatar } from "../ui/Avatar";
import { Button } from "../ui/Button";
import { FormField } from "../ui/FormField";
import { SelectField } from "../ui/SelectField";

const initialForm = {
  content: "",
  longitude: "",
  latitude: "",
  type: "DISCUSSION",
  visibility: "PUBLIC",
};

export function PostComposer({ currentUser, loading, neighborhoodLabel, onCreate }) {
  const [form, setForm] = useState(initialForm);
  const [open, setOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  function update(field, value) {
    setForm((current) => ({ ...current, [field]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setSubmitting(true);
    try {
      await onCreate({ ...form, neighborhoodId: currentUser.neighborhoodId });
      setForm(initialForm);
      setOpen(false);
    } finally {
      setSubmitting(false);
    }
  }

  const hasLongitude = form.longitude !== "";
  const hasLatitude = form.latitude !== "";
  const longitude = Number(form.longitude);
  const latitude = Number(form.latitude);
  const validPlace =
    (!hasLongitude && !hasLatitude) ||
    (hasLongitude &&
      hasLatitude &&
      Number.isFinite(longitude) &&
      Number.isFinite(latitude) &&
      longitude >= -180 &&
      longitude <= 180 &&
      latitude >= -90 &&
      latitude <= 90);
  const canSubmit = form.content.trim().replaceAll(/\s/g, "").length >= 20 && validPlace;
  const locationName = neighborhoodLabel(currentUser?.neighborhoodId);

  return (
    <form className={`card form-card post-composer ${open ? "expanded" : ""}`} onSubmit={handleSubmit}>
      <div className="composer-prompt">
        <Avatar user={currentUser} />
        <button className="composer-trigger" type="button" onClick={() => setOpen(true)}>
          Cosa succede a {locationName}?
        </button>
      </div>

      {open ? (
        <div className="composer-body">
          <FormField
            label="Contenuto del post"
            name="content"
            placeholder="Condividi un aggiornamento, una richiesta, un evento o un avviso con il tuo quartiere."
            rows={4}
            textarea
            value={form.content}
            onChange={(event) => update("content", event.target.value)}
          />
          <div className="composer-controls">
            <SelectField
              label="Tipo di post"
              name="postType"
              value={form.type}
              onChange={(event) => update("type", event.target.value)}
            >
              <option value="DISCUSSION">Discussione</option>
              <option value="HELP">Aiuto</option>
              <option value="EVENT">Evento</option>
              <option value="WARNING">Avviso</option>
            </SelectField>
            <SelectField
              label="Visibilità"
              name="visibility"
              value={form.visibility}
              onChange={(event) => update("visibility", event.target.value)}
            >
              <option value="PUBLIC">Stesso quartiere</option>
              <option value="PRIVATE">Solo vicini</option>
            </SelectField>
          </div>
          <details className="composer-location">
            <summary>Dettagli posizione</summary>
            <div className="two-col">
              <FormField
                help="Facoltativo."
                label="Longitudine"
                name="longitude"
                placeholder="12.4964"
                step="0.000001"
                type="number"
                value={form.longitude}
                onChange={(event) => update("longitude", event.target.value)}
              />
              <FormField
                help="Facoltativo."
                label="Latitudine"
                name="latitude"
                placeholder="41.9028"
                step="0.000001"
                type="number"
                value={form.latitude}
                onChange={(event) => update("latitude", event.target.value)}
              />
            </div>
          </details>
          {!validPlace ? (
            <p className="form-error">Inserisci sia longitudine sia latitudine, con valori validi.</p>
          ) : null}
          <div className="button-row end">
            <Button
              variant="ghost"
              type="button"
              onClick={() => {
                setForm(initialForm);
                setOpen(false);
              }}
            >
              Annulla
            </Button>
            <Button disabled={!canSubmit} loading={loading || submitting} type="submit">
              Pubblica
            </Button>
          </div>
        </div>
      ) : null}
    </form>
  );
}
