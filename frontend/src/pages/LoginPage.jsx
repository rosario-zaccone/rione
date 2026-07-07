import { AuthLayout } from "../components/auth/AuthLayout";
import { LoginForm } from "../components/auth/LoginForm";

export function LoginPage({ error, loading, onDismissError, onLogin, onSwitch }) {
  return (
    <AuthLayout mode="login">
      <LoginForm
        error={error}
        loading={loading}
        onDismissError={onDismissError}
        onSubmit={onLogin}
        onSwitch={onSwitch}
      />
    </AuthLayout>
  );
}
