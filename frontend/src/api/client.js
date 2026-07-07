export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";
export const USER_API_BASE_URL = import.meta.env.VITE_USER_API_BASE_URL ?? API_BASE_URL;

export class ApiError extends Error {
  constructor(message, status, data) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.data = data;
  }
}

function parseBody(text) {
  if (!text) {
    return null;
  }

  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

function errorMessage(data, status) {
  if (typeof data === "string") {
    return data;
  }

  return (
    data?.message ??
    data?.detail ??
    data?.error ??
    data?.title ??
    `Richiesta fallita con stato ${status}`
  );
}

export async function request(path, options = {}) {
  const baseUrl = options.baseUrl ?? API_BASE_URL;
  const headers = {
    ...(options.body ? { "Content-Type": "application/json" } : {}),
    ...(options.token ? { Authorization: `Bearer ${options.token}` } : {}),
    ...(options.headers ?? {}),
  };

  const response = await fetch(`${baseUrl}${path}`, {
    method: options.method ?? "GET",
    headers,
    body: options.body ? JSON.stringify(options.body) : undefined,
    signal: options.signal,
  });

  if (response.status === 204) {
    return null;
  }

  const data = parseBody(await response.text());

  if (!response.ok) {
    throw new ApiError(errorMessage(data, response.status), response.status, data);
  }

  return data;
}

export function toApiDate(dateValue) {
  return `${dateValue}T00:00:00`;
}
