import { useEffect, useState } from "react";
import * as userApi from "../api/userApi";
import { PostCard } from "../components/feed/PostCard";
import { Avatar } from "../components/ui/Avatar";
import { EmptyState } from "../components/ui/EmptyState";
import { Skeleton } from "../components/ui/Skeleton";

export function PublicProfilePage({
  currentUser,
  knownUsers,
  neighborhoodLabel,
  onOpenUserProfile,
  posts,
  token,
  userId,
}) {
  const [profile, setProfile] = useState(null);
  const [publicPosts, setPublicPosts] = useState([]);
  const [loading, setLoading] = useState(false);

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
          </div>
        </div>
      ) : (
        <EmptyState title="Profilo non disponibile.">Non è stato possibile caricare questo profilo utente.</EmptyState>
      )}

      <section className="profile-posts">
        <div>
          <p className="eyebrow">Post pubblici</p>
          <h2>Condivisi pubblicamente</h2>
        </div>
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
