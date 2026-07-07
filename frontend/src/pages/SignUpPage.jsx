import { AuthLayout } from "../components/auth/AuthLayout";
import { SignUpForm } from "../components/auth/SignUpForm";

export function SignUpPage({
  error,
  loading,
  locations,
  locationsError,
  onDismissError,
  onDismissLocationsError,
  onDismissSuccess,
  onSignUp,
  onSwitch,
  success,
}) {
  return (
    <AuthLayout mode="signup">
      <SignUpForm
        error={error}
        loading={loading}
        locations={locations}
        locationsError={locationsError}
        success={success}
        onDismissError={onDismissError}
        onDismissLocationsError={onDismissLocationsError}
        onDismissSuccess={onDismissSuccess}
        onSubmit={onSignUp}
        onSwitch={onSwitch}
      />
    </AuthLayout>
  );
}
