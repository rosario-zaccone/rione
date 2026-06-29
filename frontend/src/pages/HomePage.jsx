import { PostFeed } from "../components/feed/PostFeed";

export function HomePage({ currentUser, knownUsers, neighborhoodLabel, onOpenUserProfile, posts }) {
  return (
    <PostFeed
      currentUser={currentUser}
      knownUsers={knownUsers}
      loading={posts.loading}
      neighborhoodLabel={neighborhoodLabel}
      posts={posts.posts}
      onAddComment={posts.addComment}
      onCreatePost={posts.createPost}
      onDeleteComment={posts.deleteComment}
      onEditComment={posts.updateComment}
      onOpenUserProfile={onOpenUserProfile}
      onRemoveReaction={posts.removeReaction}
      onSetReaction={posts.setReaction}
    />
  );
}
