import { useEffect, useState } from "react";
import { NeighborhoodSelector } from "./NeighborhoodSelector";
import { Button } from "../ui/Button";
import { FormField } from "../ui/FormField";

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
        <p className="eyebrow">Profile management</p>
        <h2>Edit profile</h2>
      </div>
      {success ? <p className="form-success">{success}</p> : null}
      {error ? <p className="form-error">{error}</p> : null}
      {neighborhoodChanged ? (
        <p className="inline-warning">
          Changing your neighborhood may remove neighbour connections and pending requests with
          users outside your new neighborhood.
        </p>
      ) : null}
      <div className="two-col">
        <FormField
          label="Name"
          name="name"
          required
          value={form.name}
          onChange={(event) => update("name", event.target.value)}
        />
        <FormField
          label="Surname"
          name="surname"
          required
          value={form.surname}
          onChange={(event) => update("surname", event.target.value)}
        />
      </div>
      <div className="two-col">
        <FormField
          help="The backend validates username uniqueness."
          label="Username"
          minLength="3"
          name="username"
          required
          value={form.username}
          onChange={(event) => update("username", event.target.value)}
        />
        <FormField
          label="Birth date"
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
      />
      <FormField
        error={invalidBio ? "Biography must contain at least 20 non-whitespace characters." : ""}
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
        Save profile
      </Button>
    </form>
  );
}
