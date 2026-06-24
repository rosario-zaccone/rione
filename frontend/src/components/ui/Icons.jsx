export function Icon({ name }) {
  const paths = {
    home: <path d="M4 11.5 12 5l8 6.5V20h-5v-5H9v5H4Z" />,
    search: (
      <>
        <circle cx="10.5" cy="10.5" r="4.5" />
        <path d="m14 14 4 4" />
      </>
    ),
    requests: (
      <>
        <path d="M8 9a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z" />
        <path d="M3 19c.5-3.1 2.2-5 5-5 1.2 0 2.2.3 3 .9" />
        <path d="M15 12h5" />
        <path d="M17.5 9.5V15" />
      </>
    ),
    neighbours: (
      <>
        <path d="M8 10a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z" />
        <path d="M16 10a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z" />
        <path d="M3 20c.5-3.1 2.2-5 5-5s4.5 1.9 5 5" />
        <path d="M12 16.5c.9-1 2.2-1.5 4-1.5 2.8 0 4.5 1.9 5 5" />
      </>
    ),
    bell: (
      <>
        <path d="M7 10a5 5 0 0 1 10 0c0 4 1.5 5 2.2 6H4.8C5.5 15 7 14 7 10Z" />
        <path d="M10 19a2 2 0 0 0 4 0" />
      </>
    ),
    profile: (
      <>
        <circle cx="12" cy="8" r="3.25" />
        <path d="M5.5 19c.8-3.3 3-5 6.5-5s5.7 1.7 6.5 5" />
      </>
    ),
    blocked: (
      <>
        <circle cx="12" cy="12" r="7" />
        <path d="m7.5 16.5 9-9" />
      </>
    ),
    settings: (
      <>
        <circle cx="12" cy="12" r="3" />
        <path d="M19 12a7 7 0 0 0-.1-1l2-1.6-2-3.4-2.5 1a7.6 7.6 0 0 0-1.8-1L14 3h-4l-.4 3a7.6 7.6 0 0 0-1.8 1l-2.5-1-2 3.4 2 1.6a7 7 0 0 0 0 2l-2 1.6 2 3.4 2.5-1a7.6 7.6 0 0 0 1.8 1l.4 3h4l.4-3a7.6 7.6 0 0 0 1.8-1l2.5 1 2-3.4-2-1.6c.1-.3.1-.7.1-1Z" />
      </>
    ),
    admin: (
      <>
        <path d="M5 20V8l7-4 7 4v12" />
        <path d="M9 20v-7h6v7" />
        <path d="M9 9h.01M12 9h.01M15 9h.01" />
      </>
    ),
  };

  return (
    <svg aria-hidden="true" className="icon" viewBox="0 0 24 24">
      {paths[name]}
    </svg>
  );
}
