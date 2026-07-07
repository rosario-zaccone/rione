import { useCallback, useEffect, useMemo, useState } from "react";
import * as authApi from "../api/authApi";
import * as userApi from "../api/userApi";

const SESSION_KEY = "rione-demo-session";

function readSession() {
  try {
    const value = window.localStorage.getItem(SESSION_KEY);
    return value ? JSON.parse(value) : null;
  } catch {
    return null;
  }
}

function writeSession(session) {
  if (!session) {
    window.localStorage.removeItem(SESSION_KEY);
    return;
  }

  window.localStorage.setItem(SESSION_KEY, JSON.stringify(session));
}

export function useAuth() {
  const [session, setSession] = useState(() => readSession());
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(Boolean(readSession()?.token));
  const [error, setError] = useState("");

  const clearSession = useCallback(() => {
    setSession(null);
    setUser(null);
    writeSession(null);
  }, []);

  const refreshUser = useCallback(async () => {
    if (!session?.token) {
      setLoading(false);
      setUser(null);
      return null;
    }

    setLoading(true);
    setError("");
    try {
      const currentUser = await userApi.getCurrentUser(session.token);
      setUser(currentUser);
      return currentUser;
    } catch (currentError) {
      clearSession();
      setError(currentError.message);
      return null;
    } finally {
      setLoading(false);
    }
  }, [clearSession, session?.token]);

  useEffect(() => {
    refreshUser();
  }, [refreshUser]);

  async function logIn(credentials) {
    setLoading(true);
    setError("");
    try {
      const response = await authApi.logIn(credentials);
      const nextSession = { token: response.token };
      setSession(nextSession);
      setUser(response.user);
      writeSession(nextSession);
      return response.user;
    } catch (loginError) {
      setError("Credenziali non valide o account non attivo.");
      throw loginError;
    } finally {
      setLoading(false);
    }
  }

  async function signUp(payload) {
    setLoading(true);
    setError("");
    try {
      return await authApi.signUp(payload);
    } catch (signUpError) {
      setError(signUpError.message);
      throw signUpError;
    } finally {
      setLoading(false);
    }
  }

  async function logOut() {
    const token = session?.token;
    setLoading(true);
    setError("");
    try {
      if (token) {
        await authApi.logOut(token);
      }
    } catch (logoutError) {
      if (logoutError.status !== 401) {
        setError(logoutError.message);
        throw logoutError;
      }
    } finally {
      clearSession();
      setLoading(false);
    }
  }

  const value = useMemo(
    () => ({
      token: session?.token ?? null,
      user,
      setUser,
      isAuthenticated: Boolean(session?.token),
      loading,
      error,
      setError,
      logIn,
      signUp,
      logOut,
      refreshUser,
      clearSession,
    }),
    [clearSession, error, loading, session?.token, user],
  );

  return value;
}
