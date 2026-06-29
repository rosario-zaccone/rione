import { request } from "./client";

export function getCurrentUser(token) {
  return request("/users/me", { token });
}

export function getPublicProfile(token, userId) {
  return request(`/users/${userId}/public-profile`, { token });
}

export function updateProfile(token, payload) {
  return request("/users/me/profile", {
    method: "PUT",
    token,
    body: payload,
  });
}
