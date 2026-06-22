import { useEffect, useMemo, useState } from "react";
import { api } from "./api";
import { demoPeople, neighborhoods } from "./demoData";

const SESSION_KEY = "rione-demo-session";

function formatDate(dateTime) {
  if (!dateTime) {
    return "Unknown";
  }

  return new Date(dateTime).toLocaleDateString("en-GB", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}

function toApiDate(dateValue) {
  return `${dateValue}T00:00:00`;
}

function readSession() {
  try {
    const value = window.localStorage.getItem(SESSION_KEY);
    return value ? JSON.parse(value) : null;
  } catch {
    return null;
  }
}

function writeSession(session) {
  if (!session) {
    window.localStorage.removeItem(SESSION_KEY);
    return;
  }

  window.localStorage.setItem(SESSION_KEY, JSON.stringify(session));
}

function App() {
  const [session, setSession] = useState(() => readSession());
  const [authMode, setAuthMode] = useState("login");
  const [status, setStatus] = useState({ type: "idle", message: "" });
  const [loading, setLoading] = useState(Boolean(readSession()));
  const [user, setUser] = useState(null);
  const [search, setSearch] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [searchLoading, setSearchLoading] = useState(false);
  const [searchVersion, setSearchVersion] = useState(0);
  const [logoutLoading, setLogoutLoading] = useState(false);
  const [socialData, setSocialData] = useState({
    received: [],
    sent: [],
    neighbors: [],
    blocks: [],
  });
  const [loginForm, setLoginForm] = useState({
    mail: "",
    password: "",
  });
  const [signUpForm, setSignUpForm] = useState({
    name: "",
    surname: "",
    username: "",
    mail: "",
    password: "",
    neighborhoodId: "1",
    birthDate: "",
    bio: "",
  });
  const [profileForm, setProfileForm] = useState({
    name: "",
    surname: "",
    username: "",
    neighborhoodId: "1",
    birthDate: "",
    bio: "",
  });

  const peopleIndex = useMemo(() => {
    const entries = [...demoPeople, ...searchResults];
    if (user && !entries.some((person) => person.id === user.id)) {
      entries.push(user);
    }
    return entries.reduce((map, person) => {
      map.set(person.id, person);
      return map;
    }, new Map());
  }, [searchResults, user]);

  const neighborhoodName = useMemo(() => {
    return (id) => neighborhoods.find((item) => item.id === id)?.name ?? `Neighborhood ${id}`;
  }, []);

  useEffect(() => {
    if (!session?.token) {
      setLoading(false);
      return;
    }

    loadDashboard(session.token);
  }, [session?.token]);

  useEffect(() => {
    if (!user) {
      return;
    }

    setProfileForm({
      name: user.name,
      surname: user.surname,
      username: user.username,
      neighborhoodId: String(user.neighborhoodId),
      birthDate: user.birthDate.slice(0, 10),
      bio: user.bio,
    });
  }, [user]);

  useEffect(() => {
    const query = search.trim();
    if (!session?.token || !query) {
      setSearchResults([]);
      setSearchLoading(false);
      return undefined;
    }

    const controller = new AbortController();
    setSearchResults([]);
    setSearchLoading(true);
    const timeout = window.setTimeout(async () => {
      try {
        const results = await api.searchUsers(session.token, query, controller.signal);
        setSearchResults(results);
      } catch (error) {
        if (error.name !== "AbortError") {
          setSearchResults([]);
          setStatus({ type: "error", message: error.message });
        }
      } finally {
        if (!controller.signal.aborted) {
          setSearchLoading(false);
        }
      }
    }, 300);

    return () => {
      window.clearTimeout(timeout);
      controller.abort();
    };
  }, [search, searchVersion, session?.token]);

  async function loadDashboard(token) {
    setLoading(true);
    try {
      const [currentUser, received, sent, neighbors, blocks] = await Promise.all([
        api.getCurrentUser(token),
        api.getReceivedRequests(token),
        api.getSentRequests(token),
        api.getNeighbors(token),
        api.getBlocks(token),
      ]);

      setUser(currentUser);
      setSocialData({ received, sent, neighbors, blocks });
      setStatus({ type: "idle", message: "" });
    } catch (error) {
      setStatus({ type: "error", message: error.message });
      clearSession();
    } finally {
      setLoading(false);
    }
  }

  function clearSession() {
    setSession(null);
    writeSession(null);
    setUser(null);
    setSocialData({ received: [], sent: [], neighbors: [], blocks: [] });
    setSearch("");
    setSearchResults([]);
  }

  async function handleLogOut() {
    setLogoutLoading(true);
    setStatus({ type: "idle", message: "" });
    try {
      await api.logOut(session.token);
      clearSession();
      setStatus({ type: "success", message: "You have been logged out." });
    } catch (error) {
      if (error.status === 401) {
        clearSession();
        setStatus({ type: "success", message: "Your session ended. You have been logged out." });
      } else {
        setStatus({ type: "error", message: error.message });
      }
    } finally {
      setLogoutLoading(false);
    }
  }

  async function handleLogIn(event) {
    event.preventDefault();
    setLoading(true);
    setStatus({ type: "idle", message: "" });

    try {
      const response = await api.logIn(loginForm);
      const nextSession = {
        token: response.token,
      };
      setSession(nextSession);
      writeSession(nextSession);
    } catch (error) {
      setStatus({ type: "error", message: error.message });
      setLoading(false);
    }
  }

  async function handleSignUp(event) {
    event.preventDefault();
    setLoading(true);
    setStatus({ type: "idle", message: "" });

    try {
      await api.signUp({
        ...signUpForm,
        neighborhoodId: Number(signUpForm.neighborhoodId),
        birthDate: toApiDate(signUpForm.birthDate),
      });
      setStatus({
        type: "success",
        message: "Account created. You can log in now.",
      });
      setAuthMode("login");
      setLoginForm({ mail: signUpForm.mail, password: "" });
    } catch (error) {
      setStatus({ type: "error", message: error.message });
    } finally {
      setLoading(false);
    }
  }

  async function handleProfileSave(event) {
    event.preventDefault();
    setLoading(true);
    setStatus({ type: "idle", message: "" });

    try {
      const updated = await api.updateProfile(session.token, {
        ...profileForm,
        neighborhoodId: Number(profileForm.neighborhoodId),
        birthDate: toApiDate(profileForm.birthDate),
      });
      setUser(updated);
      setStatus({ type: "success", message: "Profile updated." });
      await loadDashboard(session.token);
    } catch (error) {
      setStatus({ type: "error", message: error.message });
      setLoading(false);
    }
  }

  async function runSocialAction(action) {
    setLoading(true);
    setStatus({ type: "idle", message: "" });

    try {
      await action();
      await loadDashboard(session.token);
      setSearchVersion((version) => version + 1);
    } catch (error) {
      setStatus({ type: "error", message: error.message });
      setLoading(false);
    }
  }

  const blockedIds = new Set(socialData.blocks.map((item) => item.blockedId));
  const neighborIds = new Set(
    socialData.neighbors.map((item) => (item.userId === user?.id ? item.neighborId : item.userId)),
  );
  const pendingSentIds = new Set(
    socialData.sent.filter((item) => item.status === "PENDING").map((item) => item.receiverId),
  );
  const pendingReceivedBySender = new Map(
    socialData.received
      .filter((item) => item.status === "PENDING")
      .map((item) => [item.senderId, item]),
  );

  if (!session?.token) {
    return (
      <div className="auth-shell">
        <div className="auth-card hero-card">
          <div className="brand-pill">Rione</div>
          <h1>A tiny social network for people living nearby.</h1>
          <p>
            This demo uses the real backend for sign up, authentication, profile updates, neighbor
            requests, blocks, and relationship management.
          </p>
        </div>

        <div className="auth-card">
          <div className="tab-row">
            <button
              className={authMode === "login" ? "tab active" : "tab"}
              onClick={() => setAuthMode("login")}
              type="button"
            >
              Log in
            </button>
            <button
              className={authMode === "signup" ? "tab active" : "tab"}
              onClick={() => setAuthMode("signup")}
              type="button"
            >
              Create account
            </button>
          </div>

          {status.message ? (
            <div className={status.type === "error" ? "notice error" : "notice success"}>
              {status.message}
            </div>
          ) : null}

          {authMode === "login" ? (
            <form className="stack" onSubmit={handleLogIn}>
              <label>
                Email
                <input
                  required
                  type="email"
                  value={loginForm.mail}
                  onChange={(event) => setLoginForm({ ...loginForm, mail: event.target.value })}
                />
              </label>
              <label>
                Password
                <input
                  required
                  type="password"
                  value={loginForm.password}
                  onChange={(event) =>
                    setLoginForm({ ...loginForm, password: event.target.value })
                  }
                />
              </label>
              <button className="primary-button" disabled={loading} type="submit">
                {loading ? "Loading..." : "Enter Rione"}
              </button>
            </form>
          ) : (
            <form className="stack" onSubmit={handleSignUp}>
              <div className="two-col">
                <label>
                  Name
                  <input
                    required
                    value={signUpForm.name}
                    onChange={(event) => setSignUpForm({ ...signUpForm, name: event.target.value })}
                  />
                </label>
                <label>
                  Surname
                  <input
                    required
                    value={signUpForm.surname}
                    onChange={(event) =>
                      setSignUpForm({ ...signUpForm, surname: event.target.value })
                    }
                  />
                </label>
              </div>
              <label>
                Username
                <input
                  minLength="3"
                  required
                  value={signUpForm.username}
                  onChange={(event) =>
                    setSignUpForm({ ...signUpForm, username: event.target.value })
                  }
                />
              </label>
              <label>
                Email
                <input
                  required
                  type="email"
                  value={signUpForm.mail}
                  onChange={(event) => setSignUpForm({ ...signUpForm, mail: event.target.value })}
                />
              </label>
              <label>
                Password
                <input
                  required
                  type="password"
                  value={signUpForm.password}
                  onChange={(event) =>
                    setSignUpForm({ ...signUpForm, password: event.target.value })
                  }
                />
              </label>
              <div className="two-col">
                <label>
                  Neighborhood
                  <select
                    value={signUpForm.neighborhoodId}
                    onChange={(event) =>
                      setSignUpForm({ ...signUpForm, neighborhoodId: event.target.value })
                    }
                  >
                    {neighborhoods.map((item) => (
                      <option key={item.id} value={item.id}>
                        {item.name}, {item.city}
                      </option>
                    ))}
                  </select>
                </label>
                <label>
                  Birth date
                  <input
                    required
                    type="date"
                    value={signUpForm.birthDate}
                    onChange={(event) =>
                      setSignUpForm({ ...signUpForm, birthDate: event.target.value })
                    }
                  />
                </label>
              </div>
              <label>
                Bio
                <textarea
                  minLength="20"
                  required
                  rows="4"
                  value={signUpForm.bio}
                  onChange={(event) => setSignUpForm({ ...signUpForm, bio: event.target.value })}
                />
              </label>
              <button className="primary-button" disabled={loading} type="submit">
                {loading ? "Creating..." : "Create account"}
              </button>
            </form>
          )}
        </div>
      </div>
    );
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div>
          <div className="brand-pill">Rione</div>
          <h2 className="sidebar-title">A simple neighborhood network.</h2>
          <p className="sidebar-copy">
            Meet people nearby, manage requests, and keep your profile in shape.
          </p>
        </div>

        <div className="sidebar-section">
          <span className="sidebar-label">Signed in as</span>
          <strong>{user?.name} {user?.surname}</strong>
          <span>@{user?.username}</span>
          <span>{neighborhoodName(user?.neighborhoodId)}</span>
        </div>

        <div className="sidebar-section">
          <span className="sidebar-label">Now available</span>
          <div className="mini-stat">
            <strong>{socialData.neighbors.length}</strong>
            <span>Neighbors</span>
          </div>
          <div className="mini-stat">
            <strong>{socialData.received.filter((item) => item.status === "PENDING").length}</strong>
            <span>Requests to review</span>
          </div>
          <div className="mini-stat">
            <strong>{socialData.blocks.length}</strong>
            <span>Blocked people</span>
          </div>
        </div>

        <button className="ghost-button" disabled={logoutLoading} onClick={handleLogOut} type="button">
          {logoutLoading ? "Logging out..." : "Log out"}
        </button>
      </aside>

      <main className="content">
        <section className="panel panel-hero">
          <div>
            <span className="section-kicker">Local network</span>
            <h1>Find people in your area and keep your circles tidy.</h1>
            <p>
              Requests, neighbors, and blocks are live against the existing backend. Posts and
              notifications are reserved here for the next iteration.
            </p>
          </div>
          <div className="hero-badge">
            <span>Area</span>
            <strong>{neighborhoodName(user?.neighborhoodId)}</strong>
          </div>
        </section>

        {status.message ? (
          <div className={status.type === "error" ? "notice error" : "notice success"}>
            {status.message}
          </div>
        ) : null}

        <section className="panel">
          <div className="section-header">
            <div>
              <span className="section-kicker">Profile</span>
              <h3>Your public card</h3>
            </div>
            <span className="section-meta">{user?.mail}</span>
          </div>

          <form className="stack" onSubmit={handleProfileSave}>
            <div className="two-col">
              <label>
                Name
                <input
                  required
                  value={profileForm.name}
                  onChange={(event) => setProfileForm({ ...profileForm, name: event.target.value })}
                />
              </label>
              <label>
                Surname
                <input
                  required
                  value={profileForm.surname}
                  onChange={(event) =>
                    setProfileForm({ ...profileForm, surname: event.target.value })
                  }
                />
              </label>
            </div>
            <div className="two-col">
              <label>
                Username
                <input
                  required
                  value={profileForm.username}
                  onChange={(event) =>
                    setProfileForm({ ...profileForm, username: event.target.value })
                  }
                />
              </label>
              <label>
                Birth date
                <input
                  required
                  type="date"
                  value={profileForm.birthDate}
                  onChange={(event) =>
                    setProfileForm({ ...profileForm, birthDate: event.target.value })
                  }
                />
              </label>
            </div>
            <div className="two-col">
              <label>
                Neighborhood
                <select
                  value={profileForm.neighborhoodId}
                  onChange={(event) =>
                    setProfileForm({ ...profileForm, neighborhoodId: event.target.value })
                  }
                >
                  {neighborhoods.map((item) => (
                    <option key={item.id} value={item.id}>
                      {item.name}, {item.city}
                    </option>
                  ))}
                </select>
              </label>
              <div className="profile-summary">
                <span className="sidebar-label">Member since</span>
                <strong>{formatDate(user?.birthDate)}</strong>
              </div>
            </div>
            <label>
              Bio
              <textarea
                minLength="20"
                required
                rows="4"
                value={profileForm.bio}
                onChange={(event) => setProfileForm({ ...profileForm, bio: event.target.value })}
              />
            </label>
            <button className="primary-button" disabled={loading} type="submit">
              {loading ? "Saving..." : "Save profile"}
            </button>
          </form>
        </section>

        <section className="panel">
          <div className="section-header">
            <div>
              <span className="section-kicker">People</span>
              <h3>Discover locals</h3>
            </div>
            <input
              className="search-input"
              aria-label="Search users"
              placeholder="Search by name, surname, or username..."
              value={search}
              onChange={(event) => setSearch(event.target.value)}
            />
          </div>

          {!search.trim() ? (
            <p className="empty-state">Enter a name, surname, or username to find people nearby.</p>
          ) : searchLoading ? (
            <p className="empty-state">Searching...</p>
          ) : searchResults.length === 0 ? (
            <p className="empty-state">No matching users found in your neighborhood.</p>
          ) : (
            <div className="card-grid">
              {searchResults.map((person) => {
                const pendingRequest = pendingReceivedBySender.get(person.id);
                const isBlocked = blockedIds.has(person.id);
                const isNeighbor = neighborIds.has(person.id);
                const isPending = pendingSentIds.has(person.id);

                return (
                  <article className="person-card" key={person.id}>
                    <div className="person-header">
                      <div>
                        <h4>
                          {person.name} {person.surname}
                        </h4>
                        <span>@{person.username}</span>
                      </div>
                      <span className="badge">Nearby</span>
                    </div>
                    <div className="person-footer">
                      <span>ID {person.id}</span>
                    </div>
                    <div className="action-row">
                      {pendingRequest ? (
                      <>
                        <button
                          className="primary-button"
                          disabled={loading}
                          onClick={() =>
                            runSocialAction(() =>
                              api.acceptNeighborRequest(session.token, pendingRequest.id),
                            )
                          }
                          type="button"
                        >
                          Accept
                        </button>
                        <button
                          className="ghost-button"
                          disabled={loading}
                          onClick={() =>
                            runSocialAction(() =>
                              api.rejectNeighborRequest(session.token, pendingRequest.id),
                            )
                          }
                          type="button"
                        >
                          Reject
                        </button>
                      </>
                    ) : isBlocked ? (
                      <button
                        className="primary-button"
                        disabled={loading}
                        onClick={() => runSocialAction(() => api.unblockUser(session.token, person.id))}
                        type="button"
                      >
                        Unblock
                      </button>
                    ) : isNeighbor ? (
                      <>
                        <button
                          className="primary-button"
                          disabled={loading}
                          onClick={() =>
                            runSocialAction(() => api.removeNeighbor(session.token, person.id))
                          }
                          type="button"
                        >
                          Remove neighbor
                        </button>
                        <button
                          className="ghost-button"
                          disabled={loading}
                          onClick={() => runSocialAction(() => api.blockUser(session.token, person.id))}
                          type="button"
                        >
                          Block
                        </button>
                      </>
                    ) : isPending ? (
                      <>
                        <button className="primary-button" disabled type="button">
                          Pending
                        </button>
                        <button
                          className="ghost-button"
                          disabled={loading}
                          onClick={() => runSocialAction(() => api.blockUser(session.token, person.id))}
                          type="button"
                        >
                          Block
                        </button>
                      </>
                    ) : (
                      <>
                        <button
                          className="primary-button"
                          disabled={loading}
                          onClick={() =>
                            runSocialAction(() => api.sendNeighborRequest(session.token, person.id))
                          }
                          type="button"
                        >
                          Connect
                        </button>
                        <button
                          className="ghost-button"
                          disabled={loading}
                          onClick={() => runSocialAction(() => api.blockUser(session.token, person.id))}
                          type="button"
                        >
                          Block
                        </button>
                      </>
                      )}
                    </div>
                  </article>
                );
              })}
            </div>
          )}
        </section>

        <section className="split-row">
          <section className="panel">
            <div className="section-header">
              <div>
                <span className="section-kicker">Requests</span>
                <h3>Inbox</h3>
              </div>
            </div>
            <ul className="item-list">
              {socialData.received.length === 0 ? (
                <li className="empty-state">No incoming requests right now.</li>
              ) : (
                socialData.received.map((request) => (
                  <li key={request.id}>
                    <strong>
                      {peopleIndex.get(request.senderId)?.name ?? "User"}{" "}
                      {peopleIndex.get(request.senderId)?.surname ?? request.senderId}
                    </strong>
                    <span>{request.status}</span>
                    <small>{formatDate(request.date)}</small>
                  </li>
                ))
              )}
            </ul>
          </section>

          <section className="panel">
            <div className="section-header">
              <div>
                <span className="section-kicker">Requests</span>
                <h3>Sent</h3>
              </div>
            </div>
            <ul className="item-list">
              {socialData.sent.length === 0 ? (
                <li className="empty-state">No outgoing requests yet.</li>
              ) : (
                socialData.sent.map((request) => (
                  <li key={request.id}>
                    <strong>
                      {peopleIndex.get(request.receiverId)?.name ?? "User"}{" "}
                      {peopleIndex.get(request.receiverId)?.surname ?? request.receiverId}
                    </strong>
                    <span>{request.status}</span>
                    <small>{formatDate(request.date)}</small>
                  </li>
                ))
              )}
            </ul>
          </section>
        </section>

        <section className="split-row">
          <section className="panel">
            <div className="section-header">
              <div>
                <span className="section-kicker">Network</span>
                <h3>Neighbors</h3>
              </div>
            </div>
            <ul className="item-list">
              {socialData.neighbors.length === 0 ? (
                <li className="empty-state">No neighbors connected yet.</li>
              ) : (
                socialData.neighbors.map((neighbor) => {
                  const counterpartId = neighbor.userId === user?.id ? neighbor.neighborId : neighbor.userId;
                  const counterpart = peopleIndex.get(counterpartId);
                  return (
                    <li key={neighbor.id}>
                      <strong>
                        {counterpart?.name ?? "User"} {counterpart?.surname ?? counterpartId}
                      </strong>
                      <span>{counterpart?.username ? `@${counterpart.username}` : `ID ${counterpartId}`}</span>
                      <small>{formatDate(neighbor.date)}</small>
                    </li>
                  );
                })
              )}
            </ul>
          </section>

          <section className="panel">
            <div className="section-header">
              <div>
                <span className="section-kicker">Safety</span>
                <h3>Blocked</h3>
              </div>
            </div>
            <ul className="item-list">
              {socialData.blocks.length === 0 ? (
                <li className="empty-state">No blocked users.</li>
              ) : (
                socialData.blocks.map((block) => {
                  const blockedPerson = peopleIndex.get(block.blockedId);
                  return (
                    <li key={block.id}>
                      <strong>
                        {blockedPerson?.name ?? "User"} {blockedPerson?.surname ?? block.blockedId}
                      </strong>
                      <span>{blockedPerson?.username ? `@${blockedPerson.username}` : `ID ${block.blockedId}`}</span>
                    </li>
                  );
                })
              )}
            </ul>
          </section>
        </section>
      </main>

      <aside className="right-rail">
        <section className="panel placeholder-panel">
          <div className="section-header">
            <div>
              <span className="section-kicker">Soon</span>
              <h3>Posts</h3>
            </div>
          </div>
          <p>
            A lightweight local feed will live here: shared updates, short notes, and neighborhood
            highlights.
          </p>
        </section>

        <section className="panel placeholder-panel">
          <div className="section-header">
            <div>
              <span className="section-kicker">Soon</span>
              <h3>Notifications</h3>
            </div>
          </div>
          <p>
            Request activity, post alerts, and local reminders will appear in this column once the
            notification flow is implemented.
          </p>
        </section>
      </aside>
    </div>
  );
}

export default App;
