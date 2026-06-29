import { ProfileCard } from "../components/profile/ProfileCard";
import { ProfileEditor } from "../components/profile/ProfileEditor";
import { PostCard } from "../components/feed/PostCard";
import { EmptyState } from "../components/ui/EmptyState";
import { Skeleton } from "../components/ui/Skeleton";

export function ProfilePage({
  knownUsers,
  locations,
  locationsError,
  neighborhoodLabel,
  onOpenUserProfile,
  posts,
  profile,
  user,
}) {
  const ownPosts = posts.posts.filter((post) => post.authorId === user?.id);

  return (
    <div className="page-grid">
      <ProfileCard neighborhoodLabel={neighborhoodLabel} user={user} />
      <section className="profile-posts">
        <div>
          <p className="eyebrow">Your posts</p>
          <h2>Shared by you</h2>
        </div>
        {posts.loading && ownPosts.length === 0 ? (
          <Skeleton lines={3} />
        ) : ownPosts.length === 0 ? (
          <EmptyState title="No posts on your profile yet.">
            Posts you publish from the home feed will also appear here.
          </EmptyState>
        ) : (
          <div className="post-list">
            {ownPosts.map((post) => (
              <PostCard
                currentUser={user}
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
      <ProfileEditor
        error={profile.error}
        loading={profile.loading}
        locations={locations}
        locationsError={locationsError}
        success={profile.success}
        user={user}
        onSubmit={profile.updateProfile}
      />
    </div>
  );
}
