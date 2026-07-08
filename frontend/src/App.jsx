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
    if (!auth.token) {
      return undefined;
    }

    function pollNotifications() {
      if (document.visibilityState === "visible") {
        notifications.loadNotifications({ silent: true });
      }
    }

    const interval = window.setInterval(pollNotifications, 20000);
    window.addEventListener("focus", pollNotifications);

    return () => {
      window.clearInterval(interval);
      window.removeEventListener("focus", pollNotifications);
    };
  }, [auth.token, notifications.loadNotifications]);

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

  async function handleLogin(credentials) {
    const loggedInUser = await auth.logIn(credentials);
    setActivePage(loggedInUser?.admin ? "admin" : "home");
  }

  async function handleSignUp(payload) {
    await auth.signUp(payload);
    setSignUpSuccess("Account creato. Ora puoi accedere.");
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
    locations.error ? { message: locations.error, tone: "error" } : null,
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
          onDismissError={() => auth.setError("")}
          onDismissLocationsError={() => locations.setError("")}
          onDismissSuccess={() => setSignUpSuccess("")}
          onSignUp={handleSignUp}
          onSwitch={() => setAuthMode("login")}
        />
      );
    }

    return (
      <LoginPage
        error={auth.error}
        loading={auth.loading}
        onDismissError={() => auth.setError("")}
        onLogin={handleLogin}
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
        <EmptyState title="Caricamento sessione">Verifica della sessione autenticata in corso.</EmptyState>
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
              title: "Bloccare questo utente?",
              message:
                "Il blocco impedisce le interazioni social dirette e rimuove l'utente dai flussi social attivi.",
              confirmLabel: "Blocca utente",
              action: () => neighbours.actions.blockUser(userId),
            })
          }
          onRemove={(userId) =>
            requestConfirmation({
              title: "Rimuovere il vicino?",
              message: "Potrai riconnetterti in seguito inviando una nuova richiesta.",
              confirmLabel: "Rimuovi vicino",
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
          neighbours={neighbours}
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
              title: "Sbloccare questo utente?",
              message:
                "Sbloccare rimuove il blocco ma non ripristina le precedenti connessioni con i vicini o le richieste in sospeso.",
              confirmLabel: "Sblocca",
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
          onAddNeighborhood={locations.addNeighborhood}
          onCreate={locations.createCity}
          onLoadCity={locations.loadCityById}
          onRemove={(city) =>
            requestConfirmation({
              title: `Rimuovere ${city.name}?`,
              message: "Le città con residenti attivi non possono essere rimosse.",
              confirmLabel: "Rimuovi città",
              action: () => locations.removeCity(city.id),
            })
          }
        />
      );
    }

    if (activePage === "settings") {
      return (
        <EmptyState title="Impostazioni">
          Le preferenze dell'account appariranno qui non appena saranno disponibili.
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
            title: "Vuoi uscire?",
            message: "Dovrai effettuare nuovamente l'accesso su questo dispositivo.",
            confirmLabel: "Esci",
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
