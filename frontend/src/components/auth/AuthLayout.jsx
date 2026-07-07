export function AuthLayout({ children, mode }) {
  return (
    <main className="auth-shell">
      <section className="auth-hero" aria-labelledby="auth-title">
        <span className="brand-orb">R</span>
        <p className="eyebrow">Rione</p>
        <h1 id="auth-title">
          {mode === "signup" ? "Entra nel tuo quartiere, in modo chiaro." : "Bentornato su Rione."}
        </h1>
        <p>
          Incontra le persone vicine a te, condividi aggiornamenti utili e mantieni le conversazioni
          del quartiere vicine a casa.
        </p>
      </section>
      <section className="auth-card">{children}</section>
    </main>
  );
}
