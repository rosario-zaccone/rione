import { useEffect, useState } from "react";
import { PostCard } from "../components/feed/PostCard";
import { Button } from "../components/ui/Button";
import { EmptyState } from "../components/ui/EmptyState";
import { Skeleton } from "../components/ui/Skeleton";

export function PostDetailPage({
  currentUser,
  knownUsers,
  neighborhoodLabel,
  onBack,
  onOpenUserProfile,
  postId,
  posts,
}) {
  const [post, setPost] = useState(() => posts.posts.find((item) => item.id === postId) ?? null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    let ignore = false;
    setLoading(true);
    posts
      .loadPost(postId)
      .then((loaded) => {
        if (!ignore) {
          setPost(loaded);
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
  }, [postId]);

  const currentPost = posts.posts.find((item) => item.id === postId) ?? post;

  return (
    <section className="page-grid">
      <div className="page-intro card aero-panel">
        <p className="eyebrow">Post</p>
        <h1>Conversation</h1>
        <Button variant="ghost" onClick={onBack}>
          Back
        </Button>
      </div>
      {loading && !currentPost ? (
        <Skeleton lines={4} />
      ) : currentPost ? (
        <PostCard
          currentUser={currentUser}
          knownUsers={knownUsers}
          neighborhoodLabel={neighborhoodLabel}
          post={currentPost}
          onAddComment={posts.addComment}
          onDeleteComment={posts.deleteComment}
          onEditComment={posts.updateComment}
          onOpenUserProfile={onOpenUserProfile}
          onRemoveReaction={posts.removeReaction}
          onSetReaction={posts.setReaction}
        />
      ) : (
        <EmptyState title="Post unavailable.">
          The post may have been deleted or you may not be authorized to view it.
        </EmptyState>
      )}
    </section>
  );
}
