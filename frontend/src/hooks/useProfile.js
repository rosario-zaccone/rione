import { useState } from "react";
import { toApiDate } from "../api/client";
import * as userApi from "../api/userApi";

export function useProfile(token, onUserChange) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  async function updateProfile(form) {
    setLoading(true);
    setError("");
    setSuccess("");
    try {
      const updated = await userApi.updateProfile(token, {
        name: form.name.trim(),
        surname: form.surname.trim(),
        username: form.username.trim(),
        neighborhoodId: Number(form.neighborhoodId),
        birthDate: toApiDate(form.birthDate),
        bio: form.bio.trim(),
      });
      onUserChange(updated);
      setSuccess("Profile updated.");
      return updated;
    } catch (profileError) {
      setError(profileError.message);
      throw profileError;
    } finally {
      setLoading(false);
    }
  }

  return {
    loading,
    error,
    success,
    setError,
    setSuccess,
    updateProfile,
  };
}
