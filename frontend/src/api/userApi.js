import { request } from "./client";

export function getCurrentUser(token) {
  return request("/users/me", { token });
}

export function updateProfile(token, payload) {
  return request("/users/me/profile", {
    method: "PUT",
    token,
    body: payload,
  });
}
