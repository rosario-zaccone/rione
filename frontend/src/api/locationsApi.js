import { request, USER_API_BASE_URL } from "./client";

export function listCities(token, signal) {
  // TODO: the backend currently exposes POST /cities, GET /cities/{cityId},
  // and DELETE /cities/{cityId}, but not a complete GET /cities catalog route.
  // Keep this as a real backend call so the UI starts working when that endpoint
  // is added or routed through the gateway.
  return request("/cities", {
    baseUrl: USER_API_BASE_URL,
    token,
    signal,
  });
}

export function getCity(token, cityId) {
  return request(`/cities/${cityId}`, {
    baseUrl: USER_API_BASE_URL,
    token,
  });
}

export function createCity(token, payload) {
  return request("/cities", {
    baseUrl: USER_API_BASE_URL,
    method: "POST",
    token,
    body: payload,
  });
}

export function removeCity(token, cityId) {
  return request(`/cities/${cityId}`, {
    baseUrl: USER_API_BASE_URL,
    method: "DELETE",
    token,
  });
}
