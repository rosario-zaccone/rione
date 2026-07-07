import { Button } from "../ui/Button";

export function LogoutButton({ loading, onLogout }) {
  return (
    <Button variant="ghost" loading={loading} onClick={onLogout}>
      Esci
    </Button>
  );
}
