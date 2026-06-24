export function fullName(user, fallbackId) {
  if (!user) {
    return `User ${fallbackId}`;
  }
  return `${user.name} ${user.surname}`;
}

export function username(user, fallbackId) {
  return user?.username ? `@${user.username}` : `ID ${fallbackId}`;
}

export function formatDate(value) {
  if (!value) {
    return "Unknown date";
  }

  return new Date(value).toLocaleDateString("en-GB", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}
