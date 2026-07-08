import { request, USER_API_BASE_URL } from "./client";

export function listCities(_token, signal) {
  return request("/cities", {
    baseUrl: USER_API_BASE_URL,
    signal,
  });
}

export function getCity(_token, cityId) {
  return request(`/cities/${cityId}`, {
    baseUrl: USER_API_BASE_URL,
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

export function addNeighborhood(token, cityId, payload) {
  return request(`/cities/${cityId}/neighborhoods`, {
    baseUrl: USER_API_BASE_URL,
    method: "POST",
    token,
    body: payload,
  });
}
