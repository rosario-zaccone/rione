import { CityManager } from "../components/admin/CityManager";
import { EmptyState } from "../components/ui/EmptyState";

export function AdminLocationsPage({ currentUser, locations, onCreate, onLoadCity, onRemove }) {
  if (!currentUser?.admin) {
    return (
      <EmptyState title="Access denied">
        Only admin users can insert or remove platform-managed cities and neighborhoods.
      </EmptyState>
    );
  }

  return (
    <CityManager
      error={locations.error}
      loading={locations.loading}
      locations={locations}
      onCreate={onCreate}
      onLoadCity={onLoadCity}
      onRemove={onRemove}
    />
  );
}
