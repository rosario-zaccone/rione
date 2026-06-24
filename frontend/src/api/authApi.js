import { request } from "./client";

export function signUp(payload) {
  return request("/users", {
    method: "POST",
    body: payload,
  });
}

export function logIn(payload) {
  return request("/users/login", {
    method: "POST",
    body: payload,
  });
}

export function logOut(token) {
  return request("/users/logout", {
    method: "POST",
    token,
  });
}
