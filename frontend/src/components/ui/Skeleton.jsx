export function Skeleton({ lines = 3 }) {
  return (
    <div className="skeleton" aria-label="Caricamento contenuto">
      {Array.from({ length: lines }).map((_, index) => (
        <span key={index} />
      ))}
    </div>
  );
}
