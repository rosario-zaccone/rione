import { useCallback, useMemo, useState } from "react";
import * as notificationApi from "../api/notificationApi";

export function useNotifications(token) {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadNotifications = useCallback(async () => {
    if (!token) {
      return;
    }

    setLoading(true);
    setError("");
    try {
      const result = await notificationApi.getNotifications(token);
      setNotifications(result);
    } catch (notificationError) {
      setError(notificationError.message);
    } finally {
      setLoading(false);
    }
  }, [token]);

  async function markRead(notificationId) {
    setError("");
    setSuccess("");
    try {
      const updated = await notificationApi.markNotificationRead(token, notificationId);
      setNotifications((items) =>
        items.map((item) => (item.id === updated.id ? updated : item)),
      );
      setSuccess("Notification marked as read.");
      return updated;
    } catch (notificationError) {
      setError(notificationError.message);
      throw notificationError;
    }
  }

  const unreadCount = useMemo(
    () => notifications.filter((notification) => !notification.readAt).length,
    [notifications],
  );

  return {
    notifications,
    unreadCount,
    loading,
    error,
    success,
    setError,
    setSuccess,
    loadNotifications,
    markRead,
  };
}
