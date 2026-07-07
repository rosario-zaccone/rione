export function fullName(user, fallbackId) {
	if (!user) {
		return "Profilo in caricamento";
	}
	return `${user.name} ${user.surname}`;
}

export function username(user, fallbackId) {
	return user?.username ? `@${user.username}` : "Profilo in caricamento";
}

export function formatDate(value) {
  if (!value) {
    return "Data sconosciuta";
  }

  return new Date(value).toLocaleDateString("it-IT", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}
