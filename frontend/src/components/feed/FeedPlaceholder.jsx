import { EmptyState } from "../ui/EmptyState";

export function FeedPlaceholder() {
  return (
    <section className="page-grid">
      <div className="page-intro card aero-panel">
        <p className="eyebrow">Home</p>
        <h1>Neighbour posts are not available yet.</h1>
        <p>Once the post service is connected, community updates will appear here.</p>
      </div>
      <EmptyState title="No feed content">
        This product demo intentionally does not include post cards, comments, reactions, likes,
        shares, messages, or engagement metrics.
      </EmptyState>
    </section>
  );
}
