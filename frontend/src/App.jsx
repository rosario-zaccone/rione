import { useEffect, useMemo, useState } from "react";
import { MainLayout } from "./components/layout/MainLayout";
import { Modal } from "./components/ui/Modal";
import { EmptyState } from "./components/ui/EmptyState";
import { useAuth } from "./hooks/useAuth";
import { useLocations } from "./hooks/useLocations";
import { useNeighbours } from "./hooks/useNeighbours";
import { useNotifications } from "./hooks/useNotifications";
import { useProfile } from "./hooks/useProfile";
import { AdminLocationsPage } from "./pages/AdminLocationsPage";
import { BlockedUsersPage } from "./pages/BlockedUsersPage";
import { FindNeighboursPage } from "./pages/FindNeighboursPage";
import { HomePage } from "./pages/HomePage";
import { LoginPage } from "./pages/LoginPage";
import { MyNeighboursPage } from "./pages/MyNeighboursPage";
import { NotificationsPage } from "./pages/NotificationsPage";
import { ProfilePage } from "./pages/ProfilePage";
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
  const profileBase = useProfile(auth.token, auth.setUser);
  const [authMode, setAuthMode] = useState("login");
  const [activePage, setActivePage] = useState("home");
  const [searchQuery, setSearchQuery] = useState("");
  const [requestTab, setRequestTab] = useState("received");
  const [signUpSuccess, setSignUpSuccess] = useState("");
  const [confirm, setConfirm] = useState(null);

  useEffect(() => {
    if (!auth.token) {
      return;
    }

    neighbours.loadAll();
    notifications.loadNotifications();
  }, [auth.token, neighbours.loadAll, notifications.loadNotifications]);

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

  function handleTopSearch(value) {
    setSearchQuery(value);
    setActivePage("find");
  }

  function dismissToast() {
    neighbours.setError("");
    neighbours.setSuccess("");
    notifications.setError("");
    notifications.setSuccess("");
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

  const toast = firstToast(
    neighbours.error ? { message: neighbours.error, tone: "error" } : null,
    notifications.error ? { message: notifications.error, tone: "error" } : null,
    profileBase.error ? { message: profileBase.error, tone: "error" } : null,
    neighbours.success ? { message: neighbours.success, tone: "success" } : null,
    notifications.success ? { message: notifications.success, tone: "success" } : null,
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
          onSendRequest={(receiverId) => neighbours.actions.sendRequest(receiverId)}
        />
      );
    }

    if (activePage === "requests") {
      return (
        <RequestsPage
          neighbours={neighbours}
          tab={requestTab}
          onAccept={(requestId) => neighbours.actions.acceptRequest(requestId)}
          onDecline={(requestId) => neighbours.actions.declineRequest(requestId)}
          onTabChange={setRequestTab}
        />
      );
    }

    if (activePage === "neighbours") {
      return (
        <MyNeighboursPage
          currentUser={auth.user}
          neighbours={neighbours}
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
              message:
                "Removing a neighbour connection does not imply deleting historical activity.",
              confirmLabel: "Remove neighbour",
              action: () => neighbours.actions.removeNeighbor(userId),
            })
          }
        />
      );
    }

    if (activePage === "notifications") {
      return (
        <NotificationsPage knownUsers={neighbours.knownUsers} notifications={notifications} />
      );
    }

    if (activePage === "profile") {
      return (
        <ProfilePage
          locations={locations}
          locationsError={locations.error}
          neighborhoodLabel={locations.neighborhoodLabel}
          profile={profile}
          user={auth.user}
        />
      );
    }

    if (activePage === "blocked") {
      return (
        <BlockedUsersPage
          neighbours={neighbours}
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
              message:
                "The backend will reject removal while any user belongs to one of this city's neighborhoods.",
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
          Account settings are currently limited to profile, neighbourhood, blocking, and logout controls.
        </EmptyState>
      );
    }

    return <HomePage />;
  }

  return (
    <>
      <MainLayout
        activePage={activePage}
        currentUser={auth.user}
        metrics={metrics}
        neighborhoodLabel={locations.neighborhoodLabel}
        searchValue={searchQuery}
        toast={toast}
        onDismissToast={dismissToast}
        onLogout={() =>
          requestConfirmation({
            title: "Log out?",
            message:
              "Logging out invalidates only the current authenticated session in this browser.",
            confirmLabel: "Log out",
            action: auth.logOut,
          })
        }
        onNavigate={setActivePage}
        onSearch={handleTopSearch}
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
    </>
  );
}

export default App;
