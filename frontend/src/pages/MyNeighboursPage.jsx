import { useState } from "react";
import { NeighbourConnections } from "../components/neighbours/NeighbourConnections";
import { NeighbourRequests } from "../components/neighbours/NeighbourRequests";

export function MyNeighboursPage({
  currentUser,
  knownUsers,
  neighbours,
  onAccept,
  onBlock,
  onDecline,
  onOpenUserProfile,
  onRemove,
  onRequestTabChange,
  requestTab,
}) {
  const [tab, setTab] = useState("list");
  const receivedCount = neighbours.receivedRequests.length;

  return (
    <section className="page-grid">
      <h2 className="section-title">Vicini</h2>
      <div className="tabs primary-tabs" role="tablist" aria-label="Visualizzazioni vicini">
        <button className={tab === "list" ? "tab active" : "tab"} onClick={() => setTab("list")} type="button">
          I miei vicini
        </button>
        <button className={tab === "requests" ? "tab active" : "tab"} onClick={() => setTab("requests")} type="button">
          Richieste
          {receivedCount > 0 ? <span className="tab-badge">{receivedCount}</span> : null}
        </button>
      </div>
      {tab === "list" ? (
        <NeighbourConnections
          currentUser={currentUser}
          knownUsers={knownUsers}
          loading={neighbours.loading}
          neighbors={neighbours.neighbors}
          onBlock={onBlock}
          onOpenUserProfile={onOpenUserProfile}
          onRemove={onRemove}
        />
      ) : (
        <NeighbourRequests
          knownUsers={knownUsers}
          loading={neighbours.loading}
          received={neighbours.receivedRequests}
          sent={neighbours.sentRequests}
          tab={requestTab}
          onAccept={onAccept}
          onDecline={onDecline}
          onOpenUserProfile={onOpenUserProfile}
          onTabChange={onRequestTabChange}
        />
      )}
    </section>
  );
}
