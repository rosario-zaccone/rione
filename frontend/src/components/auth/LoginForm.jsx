import { useState } from "react";
import { Button } from "../ui/Button";
import { FormField } from "../ui/FormField";
import { Toast } from "../ui/Toast";

export function LoginForm({ error, loading, onDismissError, onSubmit, onSwitch }) {
  const [form, setForm] = useState({ mail: "", password: "" });

  function update(field, value) {
    setForm((current) => ({ ...current, [field]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    await onSubmit(form);
  }

  return (
    <form className="form-card" onSubmit={handleSubmit}>
      <div>
        <p className="eyebrow">Accesso all'account</p>
        <h2>Accedi</h2>
        <p className="muted">Usa la tua email e password registrate.</p>
      </div>
      <Toast message={error} tone="error" onClose={onDismissError} />
      <FormField
        autoComplete="email"
        label="Email"
        name="mail"
        required
        type="email"
        value={form.mail}
        onChange={(event) => update("mail", event.target.value)}
      />
      <FormField
        autoComplete="current-password"
        label="Password"
        name="password"
        required
        type="password"
        value={form.password}
        onChange={(event) => update("password", event.target.value)}
      />
      <Button loading={loading} type="submit">
        Accedi
      </Button>
      <button className="link-button" type="button" onClick={onSwitch}>
        Crea un account
      </button>
    </form>
  );
}
