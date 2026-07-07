export function Toast({ message, tone = "info", onClose }) {
  if (!message) {
    return null;
  }

  return (
    <div className={`toast ${tone}`} role={tone === "error" ? "alert" : "status"}>
      <span>{message}</span>
      {onClose ? (
        <button aria-label="Chiudi messaggio" type="button" onClick={onClose}>
          x
        </button>
      ) : null}
    </div>
  );
}
