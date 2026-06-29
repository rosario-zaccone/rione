import { useCallback, useState } from "react";
import * as postApi from "../api/postApi";

async function hydratePost(token, post) {
  const [comments, myReaction] = await Promise.all([
    postApi.getComments(token, post.id),
    postApi.getMyReaction(token, post.id).catch(() => null),
  ]);

  return { ...post, comments, myReaction };
}

export function usePosts(token) {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadFeed = useCallback(async () => {
    if (!token) {
      setPosts([]);
      return;
    }

    setLoading(true);
    setError("");
    try {
      const feed = await postApi.getFeed(token);
      const hydrated = await Promise.all(feed.map((post) => hydratePost(token, post)));
      setPosts(hydrated);
    } catch (postError) {
      setError(postError.message);
    } finally {
      setLoading(false);
    }
  }, [token]);

  async function createPost(form) {
    setError("");
    setSuccess("");
    try {
      await postApi.createPost(token, {
        neighborhoodId: Number(form.neighborhoodId),
        content: form.content.trim(),
        type: form.type,
        visibility: form.visibility,
        longitude: form.longitude === "" ? null : Number(form.longitude),
        latitude: form.latitude === "" ? null : Number(form.latitude),
      });
      setSuccess("Post published.");
      await loadFeed();
    } catch (postError) {
      setError(postError.message);
      throw postError;
    }
  }

  async function addComment(postId, content) {
    setError("");
    setSuccess("");
    try {
      const comment = await postApi.addComment(token, postId, content.trim());
      setPosts((items) =>
        items.map((post) =>
          post.id === postId ? { ...post, comments: [...(post.comments ?? []), comment] } : post,
        ),
      );
      setSuccess("Comment added.");
      return comment;
    } catch (postError) {
      setError(postError.message);
      throw postError;
    }
  }

  async function updateComment(postId, commentId, content) {
    setError("");
    setSuccess("");
    try {
      const updated = await postApi.updateComment(token, postId, commentId, content.trim());
      setPosts((items) =>
        items.map((post) =>
          post.id === postId
            ? {
                ...post,
                comments: (post.comments ?? []).map((item) =>
                  item.id === commentId ? updated : item,
                ),
              }
            : post,
        ),
      );
      setSuccess("Comment updated.");
      return updated;
    } catch (postError) {
      setError(postError.message);
      throw postError;
    }
  }

  async function deleteComment(postId, commentId) {
    setError("");
    setSuccess("");
    try {
      await postApi.deleteComment(token, postId, commentId);
      setPosts((items) =>
        items.map((post) =>
          post.id === postId
            ? {
                ...post,
                comments: (post.comments ?? []).filter((item) => item.id !== commentId),
              }
            : post,
        ),
      );
      setSuccess("Comment deleted.");
    } catch (postError) {
      setError(postError.message);
      throw postError;
    }
  }

  async function loadPost(postId) {
    setError("");
    setSuccess("");
    try {
      const post = await postApi.getPost(token, postId);
      const hydrated = await hydratePost(token, post);
      setPosts((items) => {
        const exists = items.some((item) => item.id === hydrated.id);
        if (exists) {
          return items.map((item) => (item.id === hydrated.id ? hydrated : item));
        }
        return [hydrated, ...items];
      });
      return hydrated;
    } catch (postError) {
      setError(postError.message);
      throw postError;
    }
  }

  async function loadPublicPostsByAuthor(authorId) {
    setError("");
    setSuccess("");
    try {
      const authorPosts = await postApi.getPublicPostsByAuthor(token, authorId);
      return Promise.all(authorPosts.map((post) => hydratePost(token, post)));
    } catch (postError) {
      setError(postError.message);
      throw postError;
    }
  }

  async function setReaction(postId, type) {
    setError("");
    setSuccess("");
    try {
      await postApi.upsertReaction(token, postId, type);
      await loadFeed();
    } catch (postError) {
      setError(postError.message);
      throw postError;
    }
  }

  async function removeReaction(postId) {
    setError("");
    setSuccess("");
    try {
      await postApi.deleteReaction(token, postId);
      await loadFeed();
    } catch (postError) {
      setError(postError.message);
      throw postError;
    }
  }

  return {
    posts,
    loading,
    error,
    success,
    setError,
    setSuccess,
    loadFeed,
    loadPost,
    loadPublicPostsByAuthor,
    createPost,
    addComment,
    updateComment,
    deleteComment,
    setReaction,
    removeReaction,
  };
}
