import { AuthLayout } from "../components/auth/AuthLayout";
import { LoginForm } from "../components/auth/LoginForm";

export function LoginPage({ error, loading, onLogin, onSwitch }) {
  return (
    <AuthLayout mode="login">
      <LoginForm error={error} loading={loading} onSubmit={onLogin} onSwitch={onSwitch} />
    </AuthLayout>
  );
}
