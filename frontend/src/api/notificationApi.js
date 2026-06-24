import { request } from "./client";

export function getNotifications(token) {
  return request("/notifications/me", { token });
}

export function markNotificationRead(token, notificationId) {
  return request(`/notifications/me/${notificationId}/read`, {
    method: "PATCH",
    token,
  });
}
