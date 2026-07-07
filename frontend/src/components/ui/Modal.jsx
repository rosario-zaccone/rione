import { Button } from "./Button";

export function Modal({ cancelLabel = "Annulla", confirmLabel = "Conferma", message, onCancel, onConfirm, title }) {
  if (!title) {
    return null;
  }

  return (
    <div className="modal-backdrop" role="presentation">
      <section className="modal-card" aria-modal="true" role="dialog" aria-labelledby="modal-title">
        <h2 id="modal-title">{title}</h2>
        <p>{message}</p>
        <div className="button-row end">
          <Button variant="ghost" onClick={onCancel}>
            {cancelLabel}
          </Button>
          <Button variant="danger" onClick={onConfirm}>
            {confirmLabel}
          </Button>
        </div>
      </section>
    </div>
  );
}
