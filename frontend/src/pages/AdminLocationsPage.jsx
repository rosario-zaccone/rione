import { CityManager } from "../components/admin/CityManager";
import { EmptyState } from "../components/ui/EmptyState";

export function AdminLocationsPage({ currentUser, locations, onCreate, onLoadCity, onRemove }) {
  if (!currentUser?.admin) {
    return (
      <EmptyState title="Accesso negato">
        Solo gli utenti amministratori possono inserire o rimuovere città e quartieri gestiti dalla
        piattaforma.
      </EmptyState>
    );
  }

  return (
    <CityManager
      error={locations.error}
      loading={locations.loading}
      locations={locations}
      onCreate={onCreate}
      onDismissError={() => locations.setError("")}
      onLoadCity={onLoadCity}
      onRemove={onRemove}
    />
  );
}
