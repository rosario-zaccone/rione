import { useMemo, useState } from "react";
import { toApiDate } from "../../api/client";
import { NeighborhoodSelector } from "../profile/NeighborhoodSelector";
import { Button } from "../ui/Button";
import { FormField } from "../ui/FormField";
import { Toast } from "../ui/Toast";

const initialForm = {
  mail: "",
  username: "",
  password: "",
  confirmPassword: "",
  name: "",
  surname: "",
  birthDate: "",
  bio: "",
  neighborhoodId: "",
};

function bioIsValid(value) {
  return value.replace(/\s/g, "").length >= 20;
}

export function SignUpForm({
  error,
  loading,
  locations,
  locationsError,
  onDismissError,
  onDismissLocationsError,
  onDismissSuccess,
  onSubmit,
  onSwitch,
  success,
}) {
  const [form, setForm] = useState(initialForm);

  const passwordMismatch = form.password && form.confirmPassword && form.password !== form.confirmPassword;
  const invalidBio = form.bio && !bioIsValid(form.bio);
  const canSubmit = useMemo(
    () =>
      form.mail &&
      form.username &&
      form.password &&
      form.confirmPassword &&
      !passwordMismatch &&
      form.name &&
      form.surname &&
      form.birthDate &&
      form.neighborhoodId &&
      bioIsValid(form.bio),
    [form, passwordMismatch],
  );

  function update(field, value) {
    setForm((current) => ({ ...current, [field]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    await onSubmit({
      name: form.name.trim(),
      surname: form.surname.trim(),
      username: form.username.trim(),
      mail: form.mail.trim(),
      password: form.password,
      neighborhoodId: Number(form.neighborhoodId),
      birthDate: toApiDate(form.birthDate),
      bio: form.bio.trim(),
    });
  }

  return (
    <form className="form-card" onSubmit={handleSubmit}>
      <div className="section-header">
        <p className="eyebrow">Nuovo account</p>
        <h2>Registrati</h2>
        <p className="muted">Le password restano mascherate e non vengono mai mostrate.</p>
      </div>
      <Toast message={success} tone="success" onClose={onDismissSuccess} />
      <Toast message={error} tone="error" onClose={onDismissError} />
      <div className="two-col">
        <FormField
          autoComplete="given-name"
          label="Nome"
          name="name"
          required
          value={form.name}
          onChange={(event) => update("name", event.target.value)}
        />
        <FormField
          autoComplete="family-name"
          label="Cognome"
          name="surname"
          required
          value={form.surname}
          onChange={(event) => update("surname", event.target.value)}
        />
      </div>
      <div className="two-col">
        <FormField
          autoComplete="email"
          help="Usata solo per l'accesso all'account."
          label="Email"
          name="mail"
          required
          type="email"
          value={form.mail}
          onChange={(event) => update("mail", event.target.value)}
        />
        <FormField
          help="Scegli un nome che i vicini possano riconoscere."
          label="Username"
          minLength="3"
          name="username"
          required
          value={form.username}
          onChange={(event) => update("username", event.target.value)}
        />
      </div>
      <div className="two-col">
        <FormField
          autoComplete="new-password"
          label="Password"
          name="password"
          required
          type="password"
          value={form.password}
          onChange={(event) => update("password", event.target.value)}
        />
        <FormField
          autoComplete="new-password"
          error={passwordMismatch ? "Le password non coincidono." : ""}
          label="Conferma password"
          name="confirmPassword"
          required
          type="password"
          value={form.confirmPassword}
          onChange={(event) => update("confirmPassword", event.target.value)}
        />
      </div>
      <div className="two-col">
        <FormField
          label="Data di nascita"
          name="birthDate"
          required
          type="date"
          value={form.birthDate}
          onChange={(event) => update("birthDate", event.target.value)}
        />
        <NeighborhoodSelector
          locationsError={locationsError}
          neighborhoods={locations.neighborhoods}
          value={form.neighborhoodId}
          onChange={(value) => update("neighborhoodId", value)}
          onDismissLocationsError={onDismissLocationsError}
        />
      </div>
      <FormField
        error={invalidBio ? "Racconta qualcosa in più di te ai tuoi vicini." : ""}
        label="Bio"
        maxLength="500"
        minLength="20"
        name="bio"
        required
        rows="4"
        textarea
        value={form.bio}
        onChange={(event) => update("bio", event.target.value)}
      />
      <Button disabled={!canSubmit} loading={loading} type="submit">
        Crea account
      </Button>
      <button className="link-button" type="button" onClick={onSwitch}>
        Torna al login
      </button>
    </form>
  );
}
