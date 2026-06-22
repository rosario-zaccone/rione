const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(options.token ? { Authorization: `Bearer ${options.token}` } : {}),
      ...(options.headers ?? {}),
    },
    method: options.method ?? "GET",
    body: options.body ? JSON.stringify(options.body) : undefined,
    signal: options.signal,
  });

  if (response.status === 204) {
    return null;
  }

  const text = await response.text();
  const data = text ? JSON.parse(text) : null;

  if (!response.ok) {
    const message =
      data?.message ?? data?.detail ?? data?.error ?? `Request failed with status ${response.status}`;
    const error = new Error(message);
    error.status = response.status;
    throw error;
  }

  return data;
}

export const api = {
  signUp(payload) {
    return request("/users", { method: "POST", body: payload });
  },
  logIn(payload) {
    return request("/users/login", { method: "POST", body: payload });
  },
  logOut(token) {
    return request("/users/logout", { method: "POST", token });
  },
  getCurrentUser(token) {
    return request("/users/me", { token });
  },
  updateProfile(token, payload) {
    return request("/users/me/profile", { method: "PUT", token, body: payload });
  },
  searchUsers(token, query, signal) {
    return request(`/social/users?query=${encodeURIComponent(query)}`, { token, signal });
  },
  getReceivedRequests(token) {
    return request("/social/me/neighbor-requests/received", { token });
  },
  getSentRequests(token) {
    return request("/social/me/neighbor-requests/sent", { token });
  },
  sendNeighborRequest(token, receiverId) {
    return request("/social/neighbor-requests", {
      method: "POST",
      token,
      body: { receiverId },
    });
  },
  acceptNeighborRequest(token, requestId) {
    return request(`/social/neighbor-requests/${requestId}/acceptance`, {
      method: "POST",
      token,
    });
  },
  rejectNeighborRequest(token, requestId) {
    return request(`/social/neighbor-requests/${requestId}/rejection`, {
      method: "POST",
      token,
    });
  },
  getNeighbors(token) {
    return request("/social/me/neighborships", { token });
  },
  removeNeighbor(token, neighborId) {
    return request(`/social/me/neighborships/${neighborId}`, {
      method: "DELETE",
      token,
    });
  },
  getBlocks(token) {
    return request("/social/me/blocks", { token });
  },
  blockUser(token, blockedId) {
    return request(`/social/me/blocks/${blockedId}`, {
      method: "PUT",
      token,
    });
  },
  unblockUser(token, blockedId) {
    return request(`/social/me/blocks/${blockedId}`, {
      method: "DELETE",
      token,
    });
  },
};
