import { useEffect, useState } from "react";
import { NeighborhoodSelector } from "./NeighborhoodSelector";
import { Button } from "../ui/Button";
import { FormField } from "../ui/FormField";
import { Toast } from "../ui/Toast";

function toDateInput(value) {
  return value ? value.slice(0, 10) : "";
}

function bioIsValid(value) {
  return value.replace(/\s/g, "").length >= 20;
}

export function ProfileEditor({
  error,
  loading,
  locations,
  locationsError,
  onDismissError,
  onDismissLocationsError,
  onDismissSuccess,
  onSubmit,
  success,
  user,
}) {
  const [form, setForm] = useState({
    name: "",
    surname: "",
    username: "",
    birthDate: "",
    bio: "",
    neighborhoodId: "",
  });

  useEffect(() => {
    if (!user) {
      return;
    }

    setForm({
      name: user.name ?? "",
      surname: user.surname ?? "",
      username: user.username ?? "",
      birthDate: toDateInput(user.birthDate),
      bio: user.bio ?? "",
      neighborhoodId: String(user.neighborhoodId ?? ""),
    });
  }, [user]);

  function update(field, value) {
    setForm((current) => ({ ...current, [field]: value }));
  }

  function handleSubmit(event) {
    event.preventDefault();
    if (!bioIsValid(form.bio)) {
      return;
    }
    onSubmit(form);
  }

  const neighborhoodChanged =
    form.neighborhoodId && Number(form.neighborhoodId) !== Number(user?.neighborhoodId);
  const invalidBio = form.bio && !bioIsValid(form.bio);

  return (
    <form className="form-card" onSubmit={handleSubmit}>
      <div>
        <p className="eyebrow">Gestione profilo</p>
        <h2>Modifica profilo</h2>
      </div>
      <Toast message={success} tone="success" onClose={onDismissSuccess} />
      <Toast message={error} tone="error" onClose={onDismissError} />
      {neighborhoodChanged ? (
        <p className="inline-warning">
          Cambiare quartiere potrebbe rimuovere le connessioni con i vicini e le richieste in
          sospeso con utenti al di fuori del nuovo quartiere.
        </p>
      ) : null}
      <div className="two-col">
        <FormField
          label="Nome"
          name="name"
          required
          value={form.name}
          onChange={(event) => update("name", event.target.value)}
        />
        <FormField
          label="Cognome"
          name="surname"
          required
          value={form.surname}
          onChange={(event) => update("surname", event.target.value)}
        />
      </div>
      <div className="two-col">
        <FormField
          help="È così che i vicini ti troveranno."
          label="Username"
          minLength="3"
          name="username"
          required
          value={form.username}
          onChange={(event) => update("username", event.target.value)}
        />
        <FormField
          label="Data di nascita"
          name="birthDate"
          required
          type="date"
          value={form.birthDate}
          onChange={(event) => update("birthDate", event.target.value)}
        />
      </div>
      <NeighborhoodSelector
        locationsError={locationsError}
        neighborhoods={locations.neighborhoods}
        value={form.neighborhoodId}
        onChange={(value) => update("neighborhoodId", value)}
        onDismissLocationsError={onDismissLocationsError}
      />
      <FormField
        error={invalidBio ? "Racconta qualcosa in più di te ai tuoi vicini." : ""}
        label="Bio"
        maxLength="500"
        name="bio"
        required
        rows="4"
        textarea
        value={form.bio}
        onChange={(event) => update("bio", event.target.value)}
      />
      <Button disabled={invalidBio || !form.neighborhoodId} loading={loading} type="submit">
        Salva profilo
      </Button>
    </form>
  );
}
