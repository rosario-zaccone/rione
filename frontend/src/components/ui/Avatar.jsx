function initials(user) {
  const first = user?.name?.charAt(0) ?? "U";
  const second = user?.surname?.charAt(0) ?? "";
  return `${first}${second}`.toUpperCase();
}

export function Avatar({ user, label, size = "md" }) {
  return (
    <span className={`avatar ${size}`} aria-label={label ?? "User avatar"} role="img">
      {initials(user)}
    </span>
  );
}
