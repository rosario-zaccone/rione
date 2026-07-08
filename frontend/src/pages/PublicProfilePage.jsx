import { useEffect, useState } from "react";
import * as userApi from "../api/userApi";
import { PostCard } from "../components/feed/PostCard";
import { Avatar } from "../components/ui/Avatar";
import { Badge } from "../components/ui/Badge";
import { Button } from "../components/ui/Button";
import { Card } from "../components/ui/Card";
import { EmptyState } from "../components/ui/EmptyState";
import { Skeleton } from "../components/ui/Skeleton";

function findCounterpartConnection(items, userId) {
  return items.find(
    (item) => Number(item.userId) === Number(userId) || Number(item.neighborId) === Number(userId),
  );
}

function findRequestByCounterpart(items, userId) {
  return items.find(
    (item) => Number(item.senderId) === Number(userId) || Number(item.receiverId) === Number(userId),
  );
}

export function PublicProfilePage({
  currentUser,
  knownUsers,
  neighborhoodLabel,
  neighbours,
  onOpenUserProfile,
  posts,
  token,
  userId,
}) {
  const [profile, setProfile] = useState(null);
  const [publicPosts, setPublicPosts] = useState([]);
  const [loading, setLoading] = useState(false);
  const isSelf = Number(currentUser?.id) === Number(userId);
  const neighbor = findCounterpartConnection(neighbours.neighbors, userId);
  const sentRequest = findRequestByCounterpart(neighbours.pendingSent, userId);
  const receivedRequest = findRequestByCounterpart(neighbours.pendingReceived, userId);
  const blocked = neighbours.blocks.find((block) => Number(block.blockedId) === Number(userId));

  useEffect(() => {
    let ignore = false;
    setLoading(true);
    Promise.all([userApi.getPublicProfile(token, userId), posts.loadPublicPostsByAuthor(userId)])
      .then(([loadedProfile, loadedPosts]) => {
        if (!ignore) {
          setProfile(loadedProfile);
          setPublicPosts(loadedPosts.filter((post) => post.visibility !== "PRIVATE"));
        }
      })
      .finally(() => {
        if (!ignore) {
          setLoading(false);
        }
      });
    return () => {
      ignore = true;
    };
  }, [token, userId]);

  return (
    <section className="page-grid">
      {loading && !profile ? (
        <Skeleton lines={4} />
      ) : profile ? (
        <div className="profile-public-header card aero-panel">
          <Avatar user={profile} size="lg" />
          <div>
            <p className="eyebrow">Profilo pubblico</p>
            <h1>
              {profile.name} {profile.surname}
            </h1>
            <p>@{profile.username}</p>
            <p className="muted">{neighborhoodLabel(profile.neighborhoodId)}</p>
            {profile.bio ? <p>{profile.bio}</p> : null}
            {!isSelf ? (
              <div className="profile-social-actions">
                {neighbor ? <Badge tone="success">Siete vicini</Badge> : null}
                {blocked ? <Badge tone="warning">Utente bloccato</Badge> : null}
                {sentRequest ? <Badge tone="warning">Richiesta inviata</Badge> : null}
                {receivedRequest ? <Badge tone="warning">Ti ha inviato una richiesta</Badge> : null}
                {!neighbor && !blocked && !sentRequest && !receivedRequest ? (
                  <Button loading={neighbours.loading} onClick={() => neighbours.actions.sendRequest(userId)}>
                    Invia richiesta
                  </Button>
                ) : null}
                {receivedRequest ? (
                  <div className="button-row">
                    <Button loading={neighbours.loading} onClick={() => neighbours.actions.acceptRequest(receivedRequest.id)}>
                      Accetta
                    </Button>
                    <Button
                      variant="ghost"
                      loading={neighbours.loading}
                      onClick={() => neighbours.actions.declineRequest(receivedRequest.id)}
                    >
                      Rifiuta
                    </Button>
                  </div>
                ) : null}
              </div>
            ) : null}
          </div>
        </div>
      ) : (
        <EmptyState title="Profilo non disponibile.">Non è stato possibile caricare questo profilo utente.</EmptyState>
      )}

      <section className="profile-posts">
        <Card as="div" className="section-header">
          <p className="eyebrow">Post pubblici</p>
          <h2>Condivisi pubblicamente</h2>
        </Card>
        {loading && publicPosts.length === 0 ? (
          <Skeleton lines={3} />
        ) : publicPosts.length === 0 ? (
          <EmptyState title="Nessun post pubblico.">Questo profilo non ha post pubblici da mostrare.</EmptyState>
        ) : (
          <div className="post-list">
            {publicPosts.map((post) => (
              <PostCard
                currentUser={currentUser}
                key={post.id}
                knownUsers={knownUsers}
                neighborhoodLabel={neighborhoodLabel}
                post={post}
                onAddComment={posts.addComment}
                onDeleteComment={posts.deleteComment}
                onEditComment={posts.updateComment}
                onOpenUserProfile={onOpenUserProfile}
                onRemoveReaction={posts.removeReaction}
                onSetReaction={posts.setReaction}
              />
            ))}
          </div>
        )}
      </section>
    </section>
  );
}
