import { request } from "./client";

export function searchUsers(token, query, signal) {
  return request(`/social/users?query=${encodeURIComponent(query)}`, { token, signal });
}

export function getReceivedRequests(token) {
  return request("/social/me/neighbor-requests/received", { token });
}

export function getSentRequests(token) {
  return request("/social/me/neighbor-requests/sent", { token });
}

export function sendNeighborRequest(token, receiverId) {
  return request("/social/neighbor-requests", {
    method: "POST",
    token,
    body: { receiverId },
  });
}

export function acceptNeighborRequest(token, requestId) {
  return request(`/social/neighbor-requests/${requestId}/acceptance`, {
    method: "POST",
    token,
  });
}

export function rejectNeighborRequest(token, requestId) {
  return request(`/social/neighbor-requests/${requestId}/rejection`, {
    method: "POST",
    token,
  });
}

export function getNeighbors(token) {
  return request("/social/me/neighborships", { token });
}

export function removeNeighbor(token, neighborId) {
  return request(`/social/me/neighborships/${neighborId}`, {
    method: "DELETE",
    token,
  });
}

export function getBlocks(token) {
  return request("/social/me/blocks", { token });
}

export function blockUser(token, blockedId) {
  return request(`/social/me/blocks/${blockedId}`, {
    method: "PUT",
    token,
  });
}

export function unblockUser(token, blockedId) {
  return request(`/social/me/blocks/${blockedId}`, {
    method: "DELETE",
    token,
  });
}
