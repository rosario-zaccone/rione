import { useCallback, useMemo, useState } from "react";
import * as socialApi from "../api/socialApi";

function mergeUsers(current, users) {
  const next = new Map(current);
  users
    .filter((user) => user?.id && user?.username)
    .forEach((user) => next.set(user.id, user));
  return next;
}

function profilesFromSocialState(received, sent, currentNeighbors, currentBlocks) {
  return [
    ...received.flatMap((request) => [request.sender, request.receiver]),
    ...sent.flatMap((request) => [request.sender, request.receiver]),
    ...currentNeighbors.map((neighbor) => neighbor.neighbor),
    ...currentBlocks.map((block) => block.blocked),
  ].filter(Boolean);
}

export function useNeighbours(token) {
  const [receivedRequests, setReceivedRequests] = useState([]);
  const [sentRequests, setSentRequests] = useState([]);
  const [neighbors, setNeighbors] = useState([]);
  const [blocks, setBlocks] = useState([]);
  const [searchResults, setSearchResults] = useState([]);
  const [userCache, setUserCache] = useState(() => new Map());
  const [loading, setLoading] = useState(false);
  const [searchLoading, setSearchLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadAll = useCallback(async () => {
    if (!token) {
      return;
    }

    setLoading(true);
    setError("");
    try {
      const [received, sent, currentNeighbors, currentBlocks] = await Promise.all([
        socialApi.getReceivedRequests(token),
        socialApi.getSentRequests(token),
        socialApi.getNeighbors(token),
        socialApi.getBlocks(token),
      ]);
      setReceivedRequests(received);
      setSentRequests(sent);
      setNeighbors(currentNeighbors);
      setBlocks(currentBlocks);
      setUserCache((current) =>
        mergeUsers(current, profilesFromSocialState(received, sent, currentNeighbors, currentBlocks)),
      );
    } catch (socialError) {
      setError(socialError.message);
    } finally {
      setLoading(false);
    }
  }, [token]);

  async function search(query, signal) {
    if (!query.trim()) {
      setSearchResults([]);
      return [];
    }

    setSearchLoading(true);
    setError("");
    try {
      const results = await socialApi.searchUsers(token, query, signal);
      setSearchResults(results);
      setUserCache((current) => mergeUsers(current, results));
      return results;
    } catch (searchError) {
      if (searchError.name !== "AbortError") {
        setSearchResults([]);
        setError(searchError.message);
      }
      return [];
    } finally {
      setSearchLoading(false);
    }
  }

  async function runAction(action, successMessage) {
    setLoading(true);
    setError("");
    setSuccess("");
    try {
      const result = await action();
      await loadAll();
      setSuccess(successMessage);
      return result;
    } catch (actionError) {
      setError(actionError.message);
      throw actionError;
    } finally {
      setLoading(false);
    }
  }

  const actions = {
    sendRequest(receiverId) {
      return runAction(
        () => socialApi.sendNeighborRequest(token, receiverId),
        "Neighbour request sent.",
      );
    },
    acceptRequest(requestId) {
      return runAction(
        () => socialApi.acceptNeighborRequest(token, requestId),
        "Neighbour request accepted.",
      );
    },
    declineRequest(requestId) {
      return runAction(
        () => socialApi.rejectNeighborRequest(token, requestId),
        "Neighbour request declined.",
      );
    },
    removeNeighbor(neighborId) {
      return runAction(
        () => socialApi.removeNeighbor(token, neighborId),
        "Neighbour connection removed.",
      );
    },
    blockUser(blockedId) {
      return runAction(() => socialApi.blockUser(token, blockedId), "User blocked.");
    },
    unblockUser(blockedId) {
      return runAction(() => socialApi.unblockUser(token, blockedId), "User unblocked.");
    },
  };

  const pendingReceived = useMemo(
    () => receivedRequests.filter((request) => request.status === "PENDING"),
    [receivedRequests],
  );

  const pendingSent = useMemo(
    () => sentRequests.filter((request) => request.status === "PENDING"),
    [sentRequests],
  );

  const knownUsers = useMemo(() => {
    return userCache;
  }, [userCache]);

  return {
    receivedRequests,
    sentRequests,
    neighbors,
    blocks,
    searchResults,
    pendingReceived,
    pendingSent,
    knownUsers,
    loading,
    searchLoading,
    error,
    success,
    setError,
    setSuccess,
    loadAll,
    search,
    actions,
  };
}
