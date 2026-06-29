import { EmptyState } from "../ui/EmptyState";
import { Skeleton } from "../ui/Skeleton";
import { PostCard } from "./PostCard";
import { PostComposer } from "./PostComposer";

export function PostFeed({
  currentUser,
  knownUsers,
  loading,
  neighborhoodLabel,
  onAddComment,
  onCreatePost,
  onDeleteComment,
  onEditComment,
  onOpenUserProfile,
  onRemoveReaction,
  onSetReaction,
  posts,
}) {
  return (
    <section className="feed-view">
      <PostComposer
        currentUser={currentUser}
        loading={loading}
        neighborhoodLabel={neighborhoodLabel}
        onCreate={onCreatePost}
      />

      {loading && posts.length === 0 ? (
        <Skeleton lines={4} />
      ) : posts.length === 0 ? (
        <EmptyState title="No posts yet.">Be the first to share something with your neighbourhood.</EmptyState>
      ) : (
        <div className="post-list">
          {posts.map((post) => (
            <PostCard
              currentUser={currentUser}
              key={post.id}
              knownUsers={knownUsers}
              neighborhoodLabel={neighborhoodLabel}
              post={post}
              onAddComment={onAddComment}
              onDeleteComment={onDeleteComment}
              onEditComment={onEditComment}
              onOpenUserProfile={onOpenUserProfile}
              onRemoveReaction={onRemoveReaction}
              onSetReaction={onSetReaction}
            />
          ))}
        </div>
      )}
    </section>
  );
}
