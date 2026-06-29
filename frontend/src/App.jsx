import { useEffect, useMemo, useState } from "react";
import { MainLayout } from "./components/layout/MainLayout";
import { UserProfileModal } from "./components/profile/UserProfileModal";
import { Modal } from "./components/ui/Modal";
import { EmptyState } from "./components/ui/EmptyState";
import { useAuth } from "./hooks/useAuth";
import { useLocations } from "./hooks/useLocations";
import { useNeighbours } from "./hooks/useNeighbours";
import { useNotifications } from "./hooks/useNotifications";
import { usePosts } from "./hooks/usePosts";
import { useProfile } from "./hooks/useProfile";
import { AdminLocationsPage } from "./pages/AdminLocationsPage";
import { BlockedUsersPage } from "./pages/BlockedUsersPage";
import { FindNeighboursPage } from "./pages/FindNeighboursPage";
import { HomePage } from "./pages/HomePage";
import { LoginPage } from "./pages/LoginPage";
import { MyNeighboursPage } from "./pages/MyNeighboursPage";
import { NotificationsPage } from "./pages/NotificationsPage";
import { PostDetailPage } from "./pages/PostDetailPage";
import { ProfilePage } from "./pages/ProfilePage";
import { PublicProfilePage } from "./pages/PublicProfilePage";
import { RequestsPage } from "./pages/RequestsPage";
import { SignUpPage } from "./pages/SignUpPage";

function firstToast(...sources) {
  const source = sources.find((item) => item?.message);
  return source ?? null;
}

function App() {
  const auth = useAuth();
  const locations = useLocations(auth.token);
  const neighbours = useNeighbours(auth.token);
  const notifications = useNotifications(auth.token);
  const posts = usePosts(auth.token);
  const profileBase = useProfile(auth.token, auth.setUser);
  const [authMode, setAuthMode] = useState("login");
  const [activePage, setActivePage] = useState("home");
  const [searchQuery, setSearchQuery] = useState("");
  const [requestTab, setRequestTab] = useState("received");
  const [signUpSuccess, setSignUpSuccess] = useState("");
  const [confirm, setConfirm] = useState(null);
  const [selectedProfile, setSelectedProfile] = useState(null);
  const [publicProfileUserId, setPublicProfileUserId] = useState(null);
  const [selectedPostId, setSelectedPostId] = useState(null);

  useEffect(() => {
    if (!auth.token) {
      return;
    }

    neighbours.loadAll();
    notifications.loadNotifications();
    posts.loadFeed();
  }, [auth.token, neighbours.loadAll, notifications.loadNotifications, posts.loadFeed]);

  useEffect(() => {
    if (!auth.token || activePage !== "find") {
      return undefined;
    }

    const controller = new AbortController();
    const timeout = window.setTimeout(() => {
      neighbours.search(searchQuery, controller.signal);
    }, 250);

    return () => {
      window.clearTimeout(timeout);
      controller.abort();
    };
  }, [activePage, auth.token, searchQuery]);

  async function handleSignUp(payload) {
    await auth.signUp(payload);
    setSignUpSuccess("Account created. You can log in now.");
    setAuthMode("login");
  }

  async function handleProfileUpdate(form) {
    const updated = await profileBase.updateProfile(form);
    await neighbours.loadAll();
    await notifications.loadNotifications();
    return updated;
  }

  function requestConfirmation(config) {
    setConfirm(config);
  }

  async function runConfirmed() {
    const action = confirm?.action;
    setConfirm(null);
    if (action) {
      await action();
    }
  }

  function openPublicProfile(user) {
    const userId = typeof user === "object" ? user?.id : user;
    if (!userId) {
      return;
    }
    setPublicProfileUserId(userId);
    setSelectedProfile(null);
    setSelectedPostId(null);
    setActivePage("public-profile");
  }

  async function openPost(postId) {
    if (!postId) {
      return;
    }
    setSelectedPostId(Number(postId));
    setPublicProfileUserId(null);
    setActivePage("post-detail");
  }

  function openRequest(notification) {
    if (notification.type === "REQUEST_ACCEPTED") {
      setRequestTab("sent");
    } else {
      setRequestTab("received");
    }
    setActivePage("requests");
  }

  function dismissToast() {
    neighbours.setError("");
    neighbours.setSuccess("");
    notifications.setError("");
    notifications.setSuccess("");
    posts.setError("");
    posts.setSuccess("");
    profileBase.setError("");
    profileBase.setSuccess("");
    locations.setError("");
  }

  const profile = {
    ...profileBase,
    updateProfile: handleProfileUpdate,
  };

  const metrics = useMemo(
    () => ({
      received: neighbours.pendingReceived.length,
      sent: neighbours.pendingSent.length,
      neighbors: neighbours.neighbors.length,
      unread: notifications.unreadCount,
      blocks: neighbours.blocks.length,
    }),
    [
      neighbours.blocks.length,
      neighbours.neighbors.length,
      neighbours.pendingReceived.length,
      neighbours.pendingSent.length,
      notifications.unreadCount,
    ],
  );

  const knownUsers = useMemo(() => {
    const users = new Map(neighbours.knownUsers);
    if (auth.user?.id) {
      users.set(auth.user.id, auth.user);
    }
    return users;
  }, [auth.user, neighbours.knownUsers]);

  const neighborhoodLabel = useMemo(() => {
    return (neighborhoodId) => {
      if (
        auth.user?.neighborhoodName &&
        Number(auth.user.neighborhoodId) === Number(neighborhoodId)
      ) {
        return [auth.user.neighborhoodName, auth.user.city].filter(Boolean).join(", ");
      }
      return locations.neighborhoodLabel(neighborhoodId);
    };
  }, [auth.user, locations]);

  const toast = firstToast(
    neighbours.error ? { message: neighbours.error, tone: "error" } : null,
    notifications.error ? { message: notifications.error, tone: "error" } : null,
    posts.error ? { message: posts.error, tone: "error" } : null,
    profileBase.error ? { message: profileBase.error, tone: "error" } : null,
    neighbours.success ? { message: neighbours.success, tone: "success" } : null,
    notifications.success ? { message: notifications.success, tone: "success" } : null,
    posts.success ? { message: posts.success, tone: "success" } : null,
    profileBase.success ? { message: profileBase.success, tone: "success" } : null,
  );

  if (!auth.isAuthenticated) {
    if (authMode === "signup") {
      return (
        <SignUpPage
          error={auth.error}
          loading={auth.loading}
          locations={locations}
          locationsError={locations.error}
          success={signUpSuccess}
          onSignUp={handleSignUp}
          onSwitch={() => setAuthMode("login")}
        />
      );
    }

    return (
      <LoginPage
        error={auth.error}
        loading={auth.loading}
        onLogin={auth.logIn}
        onSwitch={() => {
          setSignUpSuccess("");
          setAuthMode("signup");
        }}
      />
    );
  }

  if (auth.loading && !auth.user) {
    return (
      <div className="center-shell">
        <EmptyState title="Loading session">Checking your authenticated session.</EmptyState>
      </div>
    );
  }

  function renderPage() {
    if (activePage === "find") {
      return (
        <FindNeighboursPage
          currentUser={auth.user}
          neighbours={neighbours}
          query={searchQuery}
          onQueryChange={setSearchQuery}
          onOpenUserProfile={openPublicProfile}
          onSendRequest={(receiverId) => neighbours.actions.sendRequest(receiverId)}
        />
      );
    }

    if (activePage === "requests") {
      return (
        <RequestsPage
          knownUsers={knownUsers}
          neighbours={neighbours}
          tab={requestTab}
          onAccept={(requestId) => neighbours.actions.acceptRequest(requestId)}
          onDecline={(requestId) => neighbours.actions.declineRequest(requestId)}
          onOpenUserProfile={openPublicProfile}
          onTabChange={setRequestTab}
        />
      );
    }

    if (activePage === "neighbours") {
      return (
        <MyNeighboursPage
          currentUser={auth.user}
          knownUsers={knownUsers}
          neighbours={neighbours}
          requestTab={requestTab}
          onAccept={(requestId) => neighbours.actions.acceptRequest(requestId)}
          onOpenUserProfile={openPublicProfile}
          onDecline={(requestId) => neighbours.actions.declineRequest(requestId)}
          onRequestTabChange={setRequestTab}
          onBlock={(userId) =>
            requestConfirmation({
              title: "Block this user?",
              message:
                "Blocking prevents direct social interactions and removes the user from active social flows.",
              confirmLabel: "Block user",
              action: () => neighbours.actions.blockUser(userId),
            })
          }
          onRemove={(userId) =>
            requestConfirmation({
              title: "Remove neighbour?",
              message: "You can reconnect later by sending a new request.",
              confirmLabel: "Remove neighbour",
              action: () => neighbours.actions.removeNeighbor(userId),
            })
          }
        />
      );
    }

    if (activePage === "notifications") {
      return (
        <NotificationsPage
          knownUsers={knownUsers}
          notifications={notifications}
          onOpenPost={openPost}
          onOpenRequest={openRequest}
          onOpenUserProfile={openPublicProfile}
        />
      );
    }

    if (activePage === "post-detail" && selectedPostId) {
      return (
        <PostDetailPage
          currentUser={auth.user}
          knownUsers={knownUsers}
          neighborhoodLabel={neighborhoodLabel}
          postId={selectedPostId}
          posts={posts}
          onBack={() => setActivePage("notifications")}
          onOpenUserProfile={openPublicProfile}
        />
      );
    }

    if (activePage === "public-profile" && publicProfileUserId) {
      return (
        <PublicProfilePage
          currentUser={auth.user}
          knownUsers={knownUsers}
          neighborhoodLabel={neighborhoodLabel}
          posts={posts}
          token={auth.token}
          userId={publicProfileUserId}
          onOpenUserProfile={openPublicProfile}
        />
      );
    }

    if (activePage === "profile") {
      return (
        <ProfilePage
          knownUsers={knownUsers}
          locations={locations}
          locationsError={locations.error}
          neighborhoodLabel={neighborhoodLabel}
          onOpenUserProfile={openPublicProfile}
          posts={posts}
          profile={profile}
          user={auth.user}
        />
      );
    }

    if (activePage === "blocked") {
      return (
        <BlockedUsersPage
          knownUsers={knownUsers}
          neighbours={neighbours}
          onOpenUserProfile={openPublicProfile}
          onUnblock={(userId) =>
            requestConfirmation({
              title: "Unblock this user?",
              message:
                "Unblocking removes the block but does not restore previous neighbour connections or pending requests.",
              confirmLabel: "Unblock",
              action: () => neighbours.actions.unblockUser(userId),
            })
          }
        />
      );
    }

    if (activePage === "admin") {
      return (
        <AdminLocationsPage
          currentUser={auth.user}
          locations={locations}
          onCreate={locations.createCity}
          onLoadCity={locations.loadCityById}
          onRemove={(city) =>
            requestConfirmation({
              title: `Remove ${city.name}?`,
              message: "Cities with active residents cannot be removed.",
              confirmLabel: "Remove city",
              action: () => locations.removeCity(city.id),
            })
          }
        />
      );
    }

    if (activePage === "settings") {
      return (
        <EmptyState title="Settings">
          Account preferences will appear here as they become available.
        </EmptyState>
      );
    }

    return (
      <HomePage
        currentUser={auth.user}
        knownUsers={knownUsers}
        neighborhoodLabel={neighborhoodLabel}
        onOpenUserProfile={openPublicProfile}
        posts={posts}
      />
    );
  }

  return (
    <>
      <MainLayout
        activePage={activePage}
        currentUser={auth.user}
        metrics={metrics}
        toast={toast}
        onDismissToast={dismissToast}
        onLogout={() =>
          requestConfirmation({
            title: "Log out?",
            message: "You will need to log in again on this device.",
            confirmLabel: "Log out",
            action: auth.logOut,
          })
        }
        onNavigate={setActivePage}
      >
        {renderPage()}
      </MainLayout>
      <Modal
        confirmLabel={confirm?.confirmLabel}
        message={confirm?.message}
        title={confirm?.title}
        onCancel={() => setConfirm(null)}
        onConfirm={runConfirmed}
      />
      <UserProfileModal
        neighborhoodLabel={neighborhoodLabel}
        user={selectedProfile}
        onClose={() => setSelectedProfile(null)}
      />
    </>
  );
}

export default App;
