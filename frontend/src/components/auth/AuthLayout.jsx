export function AuthLayout({ children, mode }) {
  return (
    <main className="auth-shell">
      <section className="auth-hero" aria-labelledby="auth-title">
        <span className="brand-orb">R</span>
        <p className="eyebrow">Rione</p>
        <h1 id="auth-title">
          {mode === "signup" ? "Join your neighbourhood with clarity." : "Welcome back to Rione."}
        </h1>
        <p>
          Manage your profile, discover eligible neighbours, handle requests, and stay informed
          without public feeds or noisy engagement features.
        </p>
      </section>
      <section className="auth-card">{children}</section>
    </main>
  );
}
