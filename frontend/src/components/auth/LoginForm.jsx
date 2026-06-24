import { useState } from "react";
import { Button } from "../ui/Button";
import { FormField } from "../ui/FormField";

export function LoginForm({ error, loading, onSubmit, onSwitch }) {
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
        <p className="eyebrow">Account access</p>
        <h2>Log in</h2>
        <p className="muted">Use your registered email and password.</p>
      </div>
      {error ? <p className="form-error">{error}</p> : null}
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
        Log in
      </Button>
      <button className="link-button" type="button" onClick={onSwitch}>
        Create an account
      </button>
    </form>
  );
}
