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
          Meet people nearby, share useful updates, and keep neighbourhood conversations close to home.
        </p>
      </section>
      <section className="auth-card">{children}</section>
    </main>
  );
}
