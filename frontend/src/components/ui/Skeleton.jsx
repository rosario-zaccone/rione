export function Skeleton({ lines = 3 }) {
  return (
    <div className="skeleton" aria-label="Loading content">
      {Array.from({ length: lines }).map((_, index) => (
        <span key={index} />
      ))}
    </div>
  );
}
