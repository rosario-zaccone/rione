import { request } from "./client";

export function getFeed(token) {
  return request("/posts", { token });
}

export function getPost(token, postId) {
  return request(`/posts/${postId}`, { token });
}

export function getPublicPostsByAuthor(token, authorId) {
  return request(`/users/${authorId}/public-posts`, { token });
}

export function createPost(token, payload) {
  return request("/posts", {
    method: "POST",
    token,
    body: payload,
  });
}

export function getComments(token, postId) {
  return request(`/posts/${postId}/comments`, { token });
}

export function addComment(token, postId, content) {
  return request(`/posts/${postId}/comments`, {
    method: "POST",
    token,
    body: { content },
  });
}

export function updateComment(token, postId, commentId, content) {
  return request(`/posts/${postId}/comments/${commentId}`, {
    method: "PATCH",
    token,
    body: { content },
  });
}

export function deleteComment(token, postId, commentId) {
  return request(`/posts/${postId}/comments/${commentId}`, {
    method: "DELETE",
    token,
  });
}

export function getMyReaction(token, postId) {
  return request(`/posts/${postId}/reactions/me`, { token });
}

export function upsertReaction(token, postId, type) {
  return request(`/posts/${postId}/reactions`, {
    method: "POST",
    token,
    body: { type },
  });
}

export function deleteReaction(token, postId) {
  return request(`/posts/${postId}/reactions`, {
    method: "DELETE",
    token,
  });
}
