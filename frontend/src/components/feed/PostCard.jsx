import { useState } from "react";
import { Avatar } from "../ui/Avatar";
import { Badge } from "../ui/Badge";
import { Button } from "../ui/Button";
import { FormField } from "../ui/FormField";

const reactions = [
  { label: "Useful", type: "UPVOTE" },
  { label: "Not for me", type: "DOWNVOTE" },
];

function formatDate(value) {
  if (!value) {
    return "";
  }

  return new Intl.DateTimeFormat(undefined, {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(value));
}

function authorFor(post, currentUser, knownUsers) {
  if (post.authorId === currentUser?.id) {
    return post.author ?? currentUser;
  }

  return post.author ?? knownUsers.get(post.authorId) ?? null;
}

export function PostCard({
  currentUser,
  knownUsers,
  neighborhoodLabel,
  onAddComment,
  onDeleteComment,
  onEditComment,
  onOpenUserProfile,
  onRemoveReaction,
  onSetReaction,
  post,
}) {
  const [comment, setComment] = useState("");
  const [editingCommentId, setEditingCommentId] = useState(null);
  const [editingContent, setEditingContent] = useState("");
  const [busy, setBusy] = useState(false);
  const author = authorFor(post, currentUser, knownUsers);
  const ownReaction = post.myReaction?.type;

  async function submitComment(event) {
    event.preventDefault();
    setBusy(true);
    try {
      await onAddComment(post.id, comment);
      setComment("");
    } finally {
      setBusy(false);
    }
  }

  async function toggleReaction(type) {
    setBusy(true);
    try {
      if (ownReaction === type) {
        await onRemoveReaction(post.id);
      } else {
        await onSetReaction(post.id, type);
      }
    } finally {
      setBusy(false);
    }
  }

  function startEditingComment(item) {
    setEditingCommentId(item.id);
    setEditingContent(item.content);
  }

  async function submitEditedComment(event, item) {
    event.preventDefault();
    setBusy(true);
    try {
      await onEditComment(post.id, item.id, editingContent);
      setEditingCommentId(null);
      setEditingContent("");
    } finally {
      setBusy(false);
    }
  }

  async function deleteComment(item) {
    setBusy(true);
    try {
      await onDeleteComment(post.id, item.id);
    } finally {
      setBusy(false);
    }
  }

  return (
    <article className="card post-card">
      <header className="post-header">
        <Avatar user={author} />
        <div>
          {author ? (
            <button className="profile-link" type="button" onClick={() => onOpenUserProfile(author)}>
              @{author.username}
            </button>
          ) : (
            <h3>Profilo in caricamento</h3>
          )}
          <p>{neighborhoodLabel(post.neighborhoodId)} / {formatDate(post.createdAt)}</p>
        </div>
        <div className="post-badges">
          <Badge tone="aqua">{post.type}</Badge>
          <Badge tone={post.visibility === "PRIVATE" ? "warning" : "neutral"}>
            {post.visibility === "PRIVATE" ? "Neighbours" : "Public"}
          </Badge>
        </div>
      </header>

      <p className="post-content">{post.content}</p>

      <div className="reaction-row" aria-label="Reactions">
        {reactions.map((reaction) => (
          <button
            className={`reaction-button ${ownReaction === reaction.type ? "active" : ""}`}
            disabled={busy}
            key={reaction.type}
            type="button"
            onClick={() => toggleReaction(reaction.type)}
          >
            {reaction.label}
          </button>
        ))}
        <span className="reaction-count">{post.score} points</span>
      </div>

      <section className="comment-list" aria-label="Comments">
        {(post.comments ?? []).map((item) => (
          <div className="comment-item" key={item.id}>
            {item.author ?? knownUsers.get(item.authorId) ? (
              <button className="profile-link small" type="button" onClick={() => onOpenUserProfile(item.author ?? knownUsers.get(item.authorId))}>
                @{(item.author ?? knownUsers.get(item.authorId)).username}
              </button>
            ) : (
              <strong>Profilo in caricamento</strong>
            )}
            {editingCommentId === item.id ? (
              <form className="comment-edit-form" onSubmit={(event) => submitEditedComment(event, item)}>
                <FormField
                  label="Edit comment"
                  name={`edit-comment-${item.id}`}
                  value={editingContent}
                  onChange={(event) => setEditingContent(event.target.value)}
                />
                <div className="button-row">
                  <Button disabled={editingContent.trim().replaceAll(/\s/g, "").length < 10} loading={busy} type="submit">
                    Save
                  </Button>
                  <Button
                    variant="ghost"
                    type="button"
                    onClick={() => {
                      setEditingCommentId(null);
                      setEditingContent("");
                    }}
                  >
                    Cancel
                  </Button>
                </div>
              </form>
            ) : (
              <>
                <p>{item.content}</p>
                {item.authorId === currentUser?.id ? (
                  <div className="comment-actions">
                    <Button variant="ghost" type="button" onClick={() => startEditingComment(item)}>
                      Edit
                    </Button>
                    <Button variant="ghost" loading={busy} type="button" onClick={() => deleteComment(item)}>
                      Delete
                    </Button>
                  </div>
                ) : null}
              </>
            )}
          </div>
        ))}
      </section>

      <form className="comment-form" onSubmit={submitComment}>
        <FormField
          label="Comment"
          name={`comment-${post.id}`}
          placeholder="Add a comment"
          value={comment}
          onChange={(event) => setComment(event.target.value)}
        />
        <Button disabled={comment.trim().replaceAll(/\s/g, "").length < 10} loading={busy} type="submit">
          Comment
        </Button>
      </form>
    </article>
  );
}
