import { useState } from "react";
import {
  Home, User, Search, Users, Bell, Shield, LogOut,
  ThumbsUp, Heart, AlertTriangle, MessageCircle,
  Calendar, HelpCircle, Plus, Check, X,
  MapPin, Send, UserMinus, Eye, EyeOff, ChevronDown,
} from "lucide-react";

/* ── Types ─────────────────────────────────────────────────────────────────── */

type View = "feed" | "profile" | "search" | "neighbors" | "blocked" | "notifications";
type PostType = "warning" | "help" | "event" | "discussion";
type Rxn = "like" | "heart" | "alert";

interface Person {
  id: string; name: string; username: string; avatar: string;
  neighborhood: string; city: string; bio: string;
  joinedAt: string; neighborCount: number; postCount: number;
}

interface Post {
  id: string; author: Person; type: PostType; content: string;
  image?: string; isPublic: boolean; createdAt: string;
  reactions: { like: number; heart: number; alert: number };
  userReaction: Rxn | null;
  comments: { id: string; author: Person; content: string; createdAt: string }[];
}

interface NRequest { id: string; from: Person; to: Person; sentAt: string; }

interface Notif {
  id: string;
  type: "reaction" | "comment" | "neighbor_request" | "neighbor_accepted";
  actor: Person; excerpt?: string; read: boolean; at: string;
}

/* ── Mock People ────────────────────────────────────────────────────────────── */

const ME: Person = {
  id: "me", name: "Marco Bianchi", username: "marco.bianchi",
  avatar: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=80&h=80&fit=crop&auto=format",
  neighborhood: "Trastevere", city: "Rome",
  bio: "Lifelong Trastevere resident. Love good coffee, local history, and knowing my neighbors. Father of two, occasional cyclist.",
  joinedAt: "January 2025", neighborCount: 34, postCount: 12,
};

const u1: Person = {
  id: "u1", name: "Sofia Esposito", username: "sofia.esposito",
  avatar: "https://images.unsplash.com/photo-1580489944761-15a19d654956?w=80&h=80&fit=crop&auto=format",
  neighborhood: "Trastevere", city: "Rome",
  bio: "Elementary school teacher. Passionate about community gardens and street art.",
  joinedAt: "March 2025", neighborCount: 28, postCount: 9,
};

const u2: Person = {
  id: "u2", name: "Luca Ferretti", username: "luca.ferretti",
  avatar: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=80&h=80&fit=crop&auto=format",
  neighborhood: "Trastevere", city: "Rome",
  bio: "Plumber and part-time cook. Always happy to help with home repairs.",
  joinedAt: "February 2025", neighborCount: 41, postCount: 7,
};

const u3: Person = {
  id: "u3", name: "Giulia Romano", username: "giulia.romano",
  avatar: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=80&h=80&fit=crop&auto=format",
  neighborhood: "Trastevere", city: "Rome",
  bio: "Architect and urban planning enthusiast. Working on local heritage preservation.",
  joinedAt: "April 2025", neighborCount: 19, postCount: 15,
};

const u4: Person = {
  id: "u4", name: "Antonio Mele", username: "antonio.mele",
  avatar: "https://images.unsplash.com/photo-1590086782792-42dd2350140d?w=80&h=80&fit=crop&auto=format",
  neighborhood: "Trastevere", city: "Rome",
  bio: "Retired postal worker. Daily walks along the Tiber, volunteer at the local library.",
  joinedAt: "January 2025", neighborCount: 56, postCount: 22,
};

const u5: Person = {
  id: "u5", name: "Elena Conti", username: "elena.conti",
  avatar: "https://images.unsplash.com/photo-1506863530036-1efeddceb993?w=80&h=80&fit=crop&auto=format",
  neighborhood: "Trastevere", city: "Rome",
  bio: "Freelance journalist covering local news. Dog owner, coffee addict.",
  joinedAt: "May 2025", neighborCount: 23, postCount: 18,
};

const u6: Person = {
  id: "u6", name: "Roberto Neri", username: "roberto.neri",
  avatar: "https://images.unsplash.com/photo-1587397845856-e6cf49176c70?w=80&h=80&fit=crop&auto=format",
  neighborhood: "Trastevere", city: "Rome",
  bio: "Restaurant owner on Via della Lungaretta. Hosting monthly community dinners.",
  joinedAt: "June 2025", neighborCount: 72, postCount: 31,
};

const u7: Person = {
  id: "u7", name: "Carla Vitale", username: "carla.vitale",
  avatar: "https://images.unsplash.com/photo-1528892952291-009c663ce843?w=80&h=80&fit=crop&auto=format",
  neighborhood: "Trastevere", city: "Rome",
  bio: "Yoga instructor and urban beekeeper.",
  joinedAt: "June 2025", neighborCount: 8, postCount: 3,
};

const u8: Person = {
  id: "u8", name: "Davide Marchetti", username: "davide.marchetti",
  avatar: "https://images.unsplash.com/photo-1528892952291-009c663ce843?w=80&h=80&fit=crop&auto=format",
  neighborhood: "Trastevere", city: "Rome",
  bio: "Architect and part-time musician.",
  joinedAt: "May 2025", neighborCount: 14, postCount: 6,
};

/* ── Mock Data ──────────────────────────────────────────────────────────────── */

const INITIAL_POSTS: Post[] = [
  {
    id: "p1", author: u1, type: "event",
    content: "This Saturday at 10am we're organizing a neighborhood cleanup along the Tiber riverbank! Bring gloves and good spirits — kids are welcome too, there will be snacks. Meeting point is at Piazza Trilussa. Let's make Trastevere shine together!",
    image: "https://images.unsplash.com/photo-1556231673-79c47d27cde0?w=600&h=320&fit=crop&auto=format",
    isPublic: true, createdAt: "2h ago",
    reactions: { like: 24, heart: 8, alert: 0 }, userReaction: null,
    comments: [
      { id: "c1", author: u2, content: "I'll be there with my family! Great initiative Sofia.", createdAt: "1h ago" },
      { id: "c2", author: u4, content: "Already marked in my calendar. See everyone Saturday!", createdAt: "45m ago" },
    ],
  },
  {
    id: "p2", author: u2, type: "warning",
    content: "Heads up: broken water main on Via Garibaldi near Vicolo del Cedro. Water is pooling on the sidewalk and it's getting slippery. I've already called the municipality — repairs start tomorrow morning. Avoid the area or walk carefully until then.",
    isPublic: true, createdAt: "5h ago",
    reactions: { like: 6, heart: 2, alert: 19 }, userReaction: "alert",
    comments: [
      { id: "c3", author: u5, content: "Thank you for reporting this! I almost slipped there this morning.", createdAt: "4h ago" },
    ],
  },
  {
    id: "p3", author: u3, type: "discussion",
    content: "Has anyone noticed the new construction near Piazza di Santa Maria? I'm trying to find the environmental impact assessment. The vibrations from machinery are already affecting older buildings nearby. Would love to organize a residents' meeting with the municipal planning office — who's in?",
    isPublic: true, createdAt: "Yesterday",
    reactions: { like: 15, heart: 3, alert: 7 }, userReaction: null,
    comments: [
      { id: "c4", author: u6, content: "I was at the planning office last week — they have the docs. I can share them.", createdAt: "22h ago" },
      { id: "c5", author: u4, content: "Count me in for a meeting. This is exactly why we need Rione.", createdAt: "20h ago" },
      { id: "c6", author: u1, content: "I'll ask at school too — many parents are worried about this.", createdAt: "18h ago" },
    ],
  },
  {
    id: "p4", author: u6, type: "help",
    content: "Does anyone have a ladder to lend for the weekend? Need to hang decorations for our restaurant's anniversary on Friday night. Will return it Monday in perfect condition. Happy to trade with a dinner reservation!",
    isPublic: false, createdAt: "Yesterday",
    reactions: { like: 11, heart: 4, alert: 0 }, userReaction: "like",
    comments: [
      { id: "c7", author: u2, content: "I have a 4-meter one. DM me!", createdAt: "23h ago" },
    ],
  },
  {
    id: "p5", author: u4, type: "discussion",
    content: "The neighborhood beautification vote results are in: flower boxes on the main thoroughfare and a mosaic mural near the playground. The municipality approved €2,000. Looking for volunteers with artistic skills or anyone who knows local tile artisans. What an exciting project for all of us!",
    isPublic: true, createdAt: "2 days ago",
    reactions: { like: 38, heart: 22, alert: 0 }, userReaction: null,
    comments: [],
  },
  {
    id: "p6", author: u5, type: "warning",
    content: "Lost cat — orange tabby named Gatto, about 4 years old, red collar with a small bell. Last seen near Via della Scala on Tuesday evening. He's friendly but shy. Please contact me if you spot him. We miss him terribly.",
    image: "https://images.unsplash.com/photo-1677126369481-81b4ddcf0e72?w=600&h=320&fit=crop&auto=format",
    isPublic: true, createdAt: "3 days ago",
    reactions: { like: 12, heart: 31, alert: 2 }, userReaction: null,
    comments: [
      { id: "c8", author: u3, content: "Sharing this immediately. Hope Gatto finds his way home!", createdAt: "3 days ago" },
    ],
  },
];

interface SearchPerson extends Person { mutualNeighbors: number; }

const SEARCH_POOL: SearchPerson[] = [
  { ...u6, mutualNeighbors: 12 },
  { ...u7, mutualNeighbors: 3 },
  { ...u8, mutualNeighbors: 7 },
];

const INITIAL_NEIGHBORS: Person[] = [u1, u2, u3, u4, u5];

const INITIAL_RECEIVED: NRequest[] = [
  { id: "rr1", from: u6, to: ME, sentAt: "2 hours ago" },
  { id: "rr2", from: u7, to: ME, sentAt: "1 day ago" },
];

const INITIAL_SENT: NRequest[] = [
  { id: "sr1", from: ME, to: u8, sentAt: "3 days ago" },
];

const INITIAL_BLOCKED: Person[] = [
  {
    id: "ub1", name: "Sergio Palumbo", username: "sergio.palumbo",
    avatar: "https://images.unsplash.com/photo-1587397845856-e6cf49176c70?w=80&h=80&fit=crop&auto=format",
    neighborhood: "Trastevere", city: "Rome", bio: "",
    joinedAt: "April 2025", neighborCount: 0, postCount: 0,
  },
];

const INITIAL_NOTIFS: Notif[] = [
  { id: "n1", type: "neighbor_request", actor: u6, read: false, at: "2h ago" },
  { id: "n2", type: "reaction", actor: u1, excerpt: "This Saturday at 10am we're organizing...", read: false, at: "3h ago" },
  { id: "n3", type: "comment", actor: u2, excerpt: "Heads up: broken water main on Via Garibaldi...", read: false, at: "5h ago" },
  { id: "n4", type: "neighbor_accepted", actor: u3, read: true, at: "Yesterday" },
  { id: "n5", type: "reaction", actor: u4, excerpt: "The neighborhood beautification vote results...", read: true, at: "3 days ago" },
];

/* ── Post type config ───────────────────────────────────────────────────────── */

const PT: Record<PostType, { label: string; textCls: string; pillCls: string; Icon: React.ElementType }> = {
  warning:    { label: "Warning",    textCls: "text-amber-700",   pillCls: "bg-amber-50 text-amber-700 border border-amber-200",   Icon: AlertTriangle },
  help:       { label: "Help",       textCls: "text-blue-700",    pillCls: "bg-blue-50 text-blue-700 border border-blue-200",     Icon: HelpCircle },
  event:      { label: "Event",      textCls: "text-purple-700",  pillCls: "bg-purple-50 text-purple-700 border border-purple-200", Icon: Calendar },
  discussion: { label: "Discussion", textCls: "text-emerald-700", pillCls: "bg-emerald-50 text-emerald-700 border border-emerald-200", Icon: MessageCircle },
};

/* ── Small Components ───────────────────────────────────────────────────────── */

function Av({ p, size = 40 }: { p: Person; size?: number }) {
  return (
    <img
      src={p.avatar}
      alt={p.name}
      className="rounded-full object-cover bg-secondary shrink-0"
      style={{ width: size, height: size }}
    />
  );
}

function TypePill({ type }: { type: PostType }) {
  const { label, pillCls, Icon } = PT[type];
  return (
    <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium ${pillCls}`}>
      <Icon size={11} />
      {label}
    </span>
  );
}

function SectionTitle({ children }: { children: React.ReactNode }) {
  return <h2 className="text-xl font-semibold text-foreground mb-5">{children}</h2>;
}

/* ── Sidebar ────────────────────────────────────────────────────────────────── */

interface SidebarProps {
  view: View;
  setView: (v: View) => void;
  unreadCount: number;
  receivedCount: number;
}

function Sidebar({ view, setView, unreadCount, receivedCount }: SidebarProps) {
  const navItems: { id: View; label: string; Icon: React.ElementType; badge?: number }[] = [
    { id: "feed",          label: "Home",          Icon: Home },
    { id: "profile",       label: "Profile",       Icon: User },
    { id: "search",        label: "Search Neighbors", Icon: Search },
    { id: "neighbors",     label: "Neighbours",    Icon: Users, badge: receivedCount > 0 ? receivedCount : undefined },
    { id: "blocked",       label: "Blocked Users", Icon: Shield },
    { id: "notifications", label: "Notifications", Icon: Bell, badge: unreadCount > 0 ? unreadCount : undefined },
  ];

  return (
    <aside className="fixed left-0 top-0 h-full w-64 bg-sidebar flex flex-col z-20 select-none">
      {/* Brand */}
      <div className="px-6 pt-7 pb-6">
        <div
          className="text-2xl font-bold tracking-tight text-sidebar-primary"
          style={{ fontFamily: "'Playfair Display', Georgia, serif" }}
        >
          Rione
        </div>
        <div className="flex items-center gap-1 mt-1 text-sidebar-foreground/50 text-xs">
          <MapPin size={11} />
          <span>Trastevere · Rome</span>
        </div>
      </div>

      {/* Current user mini card */}
      <div className="mx-4 mb-5 px-3 py-3 rounded-lg bg-sidebar-accent flex items-center gap-3">
        <Av p={ME} size={36} />
        <div className="min-w-0">
          <div className="text-sidebar-foreground text-sm font-medium truncate">{ME.name}</div>
          <div className="text-sidebar-foreground/50 text-xs truncate">@{ME.username}</div>
        </div>
      </div>

      {/* Nav */}
      <nav className="flex-1 px-3 flex flex-col gap-0.5 overflow-y-auto">
        {navItems.map(({ id, label, Icon, badge }) => {
          const active = view === id;
          return (
            <button
              key={id}
              onClick={() => setView(id)}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors text-left ${
                active
                  ? "bg-sidebar-primary/20 text-sidebar-primary"
                  : "text-sidebar-foreground/70 hover:bg-sidebar-accent hover:text-sidebar-foreground"
              }`}
            >
              <Icon size={17} className="shrink-0" />
              <span className="flex-1">{label}</span>
              {badge != null && (
                <span className="ml-auto bg-primary text-primary-foreground text-xs font-semibold rounded-full px-1.5 py-0.5 min-w-[20px] text-center leading-none">
                  {badge}
                </span>
              )}
            </button>
          );
        })}
      </nav>

      {/* Logout */}
      <div className="px-3 py-4 border-t border-sidebar-border">
        <button className="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-sidebar-foreground/50 hover:text-sidebar-foreground hover:bg-sidebar-accent transition-colors">
          <LogOut size={17} />
          Log out
        </button>
      </div>
    </aside>
  );
}

/* ── CreatePost ─────────────────────────────────────────────────────────────── */

interface CreatePostProps {
  onSubmit: (content: string, type: PostType, isPublic: boolean) => void;
}

function CreatePost({ onSubmit }: CreatePostProps) {
  const [open, setOpen] = useState(false);
  const [content, setContent] = useState("");
  const [type, setType] = useState<PostType>("discussion");
  const [isPublic, setIsPublic] = useState(true);
  const [showTypeMenu, setShowTypeMenu] = useState(false);

  function handleSubmit() {
    if (content.trim().length < 20) return;
    onSubmit(content.trim(), type, isPublic);
    setContent("");
    setType("discussion");
    setIsPublic(true);
    setOpen(false);
  }

  return (
    <div className="bg-card rounded-xl border border-border shadow-sm overflow-hidden mb-5">
      {!open ? (
        <button
          onClick={() => setOpen(true)}
          className="w-full flex items-center gap-3 px-5 py-4 text-left hover:bg-secondary/50 transition-colors"
        >
          <Av p={ME} size={38} />
          <span className="text-muted-foreground text-sm">What's happening in Trastevere?</span>
          <Plus size={18} className="ml-auto text-primary shrink-0" />
        </button>
      ) : (
        <div className="p-4">
          <div className="flex items-start gap-3 mb-3">
            <Av p={ME} size={38} />
            <textarea
              autoFocus
              value={content}
              onChange={(e) => setContent(e.target.value)}
              placeholder="Share a warning, event, help request, or start a discussion..."
              rows={3}
              className="flex-1 resize-none bg-input-background rounded-lg px-3 py-2.5 text-sm text-foreground placeholder:text-muted-foreground outline-none focus:ring-2 focus:ring-ring/30 transition-shadow"
            />
          </div>

          <div className="flex items-center gap-2 flex-wrap">
            {/* Type selector */}
            <div className="relative">
              <button
                onClick={() => setShowTypeMenu(!showTypeMenu)}
                className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full text-xs font-medium border transition-colors ${PT[type].pillCls}`}
              >
                {(() => { const { Icon } = PT[type]; return <Icon size={12} />; })()}
                {PT[type].label}
                <ChevronDown size={11} />
              </button>
              {showTypeMenu && (
                <div className="absolute top-full left-0 mt-1 bg-card border border-border rounded-lg shadow-lg py-1 z-10 min-w-[140px]">
                  {(["discussion", "event", "warning", "help"] as PostType[]).map((t) => (
                    <button
                      key={t}
                      onClick={() => { setType(t); setShowTypeMenu(false); }}
                      className={`w-full flex items-center gap-2 px-3 py-2 text-xs font-medium hover:bg-secondary transition-colors ${PT[t].textCls}`}
                    >
                      {(() => { const { Icon } = PT[t]; return <Icon size={12} />; })()}
                      {PT[t].label}
                    </button>
                  ))}
                </div>
              )}
            </div>

            {/* Public / private toggle */}
            <button
              onClick={() => setIsPublic(!isPublic)}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full text-xs font-medium border transition-colors ${
                isPublic
                  ? "bg-secondary border-border text-muted-foreground"
                  : "bg-muted border-border text-muted-foreground"
              }`}
            >
              {isPublic ? <Eye size={12} /> : <EyeOff size={12} />}
              {isPublic ? "Public" : "Private"}
            </button>

            <div className="ml-auto flex items-center gap-2">
              <button
                onClick={() => { setOpen(false); setContent(""); }}
                className="px-3 py-1.5 rounded-lg text-xs font-medium text-muted-foreground hover:text-foreground transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleSubmit}
                disabled={content.trim().length < 20}
                className="px-4 py-1.5 rounded-lg text-xs font-semibold bg-primary text-primary-foreground hover:opacity-90 disabled:opacity-40 transition-opacity"
              >
                Post
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

/* ── PostCard ───────────────────────────────────────────────────────────────── */

interface PostCardProps {
  post: Post;
  commentsOpen: boolean;
  commentInput: string;
  onToggleComments: () => void;
  onCommentChange: (v: string) => void;
  onCommentSubmit: () => void;
  onReact: (rxn: Rxn) => void;
}

function PostCard({ post, commentsOpen, commentInput, onToggleComments, onCommentChange, onCommentSubmit, onReact }: PostCardProps) {
  const totalReactions = post.reactions.like + post.reactions.heart + post.reactions.alert;

  return (
    <article className="bg-card rounded-xl border border-border shadow-sm overflow-hidden mb-4">
      <div className="p-5">
        {/* Author row */}
        <div className="flex items-start justify-between mb-3">
          <div className="flex items-center gap-3">
            <Av p={post.author} size={42} />
            <div>
              <div className="font-semibold text-sm text-foreground">{post.author.name}</div>
              <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                <span>@{post.author.username}</span>
                <span>·</span>
                <span>{post.createdAt}</span>
                {!post.isPublic && (
                  <>
                    <span>·</span>
                    <span className="flex items-center gap-0.5"><EyeOff size={10} /> private</span>
                  </>
                )}
              </div>
            </div>
          </div>
          <TypePill type={post.type} />
        </div>

        {/* Content */}
        <p className="text-sm text-foreground leading-relaxed mb-3">{post.content}</p>
      </div>

      {/* Image */}
      {post.image && (
        <div className="bg-secondary">
          <img
            src={post.image}
            alt="Post attachment"
            className="w-full object-cover"
            style={{ maxHeight: 320 }}
          />
        </div>
      )}

      {/* Reactions + Comments bar */}
      <div className="px-5 py-3 flex items-center gap-1 border-t border-border">
        {([["like", ThumbsUp, "Like"], ["heart", Heart, "Like"], ["alert", AlertTriangle, "Alert"]] as [Rxn, React.ElementType, string][]).map(([rxn, Icon, label]) => (
          <button
            key={rxn}
            onClick={() => onReact(rxn)}
            className={`flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-xs font-medium transition-colors ${
              post.userReaction === rxn
                ? rxn === "like"
                  ? "bg-primary/10 text-primary"
                  : rxn === "heart"
                  ? "bg-rose-50 text-rose-600"
                  : "bg-amber-50 text-amber-600"
                : "text-muted-foreground hover:bg-secondary hover:text-foreground"
            }`}
          >
            <Icon size={14} />
            {post.reactions[rxn] > 0 && <span>{post.reactions[rxn]}</span>}
          </button>
        ))}

        <div className="ml-auto">
          <button
            onClick={onToggleComments}
            className={`flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-xs font-medium transition-colors ${
              commentsOpen ? "bg-secondary text-foreground" : "text-muted-foreground hover:bg-secondary hover:text-foreground"
            }`}
          >
            <MessageCircle size={14} />
            {post.comments.length > 0 && <span>{post.comments.length}</span>}
            <span>{post.comments.length === 0 ? "Comment" : "Comments"}</span>
          </button>
        </div>
      </div>

      {/* Comments section */}
      {commentsOpen && (
        <div className="border-t border-border bg-secondary/30">
          {post.comments.length > 0 && (
            <div className="px-5 pt-4 pb-2 flex flex-col gap-3">
              {post.comments.map((c) => (
                <div key={c.id} className="flex gap-2.5">
                  <Av p={c.author} size={28} />
                  <div className="flex-1">
                    <div className="bg-card rounded-xl px-3 py-2 inline-block max-w-full">
                      <span className="text-xs font-semibold text-foreground">{c.author.name} </span>
                      <span className="text-xs text-foreground">{c.content}</span>
                    </div>
                    <div className="text-[11px] text-muted-foreground mt-0.5 ml-3">{c.createdAt}</div>
                  </div>
                </div>
              ))}
            </div>
          )}
          <div className="px-5 py-3 flex gap-2.5 items-center">
            <Av p={ME} size={28} />
            <div className="flex-1 flex items-center gap-2 bg-card rounded-full border border-border px-3 py-1.5">
              <input
                value={commentInput}
                onChange={(e) => onCommentChange(e.target.value)}
                onKeyDown={(e) => { if (e.key === "Enter" && !e.shiftKey) { e.preventDefault(); onCommentSubmit(); } }}
                placeholder="Write a comment..."
                className="flex-1 text-xs bg-transparent outline-none text-foreground placeholder:text-muted-foreground"
              />
              <button
                onClick={onCommentSubmit}
                disabled={commentInput.trim().length < 10}
                className="text-primary disabled:text-muted-foreground transition-colors"
              >
                <Send size={13} />
              </button>
            </div>
          </div>
        </div>
      )}
    </article>
  );
}

/* ── Profile View ───────────────────────────────────────────────────────────── */

function ProfileView({ posts }: { posts: Post[] }) {
  const myPosts = posts.filter((p) => p.author.id === "me");

  return (
    <div>
      {/* Cover + avatar */}
      <div className="relative mb-16 rounded-xl overflow-hidden">
        <div
          className="h-40 bg-cover bg-center"
          style={{ backgroundImage: "url('https://images.unsplash.com/photo-1555636222-cae831e670b3?w=900&h=240&fit=crop&auto=format')" }}
        />
        <div className="absolute left-6 -bottom-12">
          <img
            src={ME.avatar}
            alt={ME.name}
            className="w-24 h-24 rounded-full object-cover border-4 border-card bg-secondary"
          />
        </div>
      </div>

      {/* Info */}
      <div className="bg-card rounded-xl border border-border p-5 mb-5">
        <div className="flex items-start justify-between mb-3">
          <div>
            <h1 className="text-xl font-semibold text-foreground">{ME.name}</h1>
            <div className="text-sm text-muted-foreground">@{ME.username}</div>
            <div className="flex items-center gap-1 text-xs text-muted-foreground mt-1">
              <MapPin size={12} />
              <span>{ME.neighborhood}, {ME.city}</span>
              <span>·</span>
              <span>Joined {ME.joinedAt}</span>
            </div>
          </div>
          <button className="px-4 py-1.5 rounded-lg text-sm font-medium border border-border text-foreground hover:bg-secondary transition-colors">
            Edit Profile
          </button>
        </div>
        <p className="text-sm text-foreground/80 leading-relaxed mb-4">{ME.bio}</p>
        <div className="flex gap-6">
          {[["Neighbours", ME.neighborCount], ["Posts", ME.postCount]].map(([label, count]) => (
            <div key={label as string}>
              <div className="text-lg font-semibold text-foreground">{count}</div>
              <div className="text-xs text-muted-foreground">{label}</div>
            </div>
          ))}
        </div>
      </div>

      {/* Own posts */}
      <h3 className="text-base font-semibold text-foreground mb-3">My Posts</h3>
      {myPosts.length === 0 ? (
        <div className="bg-card rounded-xl border border-border p-8 text-center text-muted-foreground text-sm">
          You haven't posted anything yet.
        </div>
      ) : (
        myPosts.map((p) => (
          <div key={p.id} className="bg-card rounded-xl border border-border p-4 mb-3">
            <div className="flex items-center justify-between mb-2">
              <TypePill type={p.type} />
              <span className="text-xs text-muted-foreground">{p.createdAt}</span>
            </div>
            <p className="text-sm text-foreground leading-relaxed">{p.content}</p>
          </div>
        ))
      )}
    </div>
  );
}

/* ── Search View ────────────────────────────────────────────────────────────── */

interface SearchViewProps {
  sentIds: Set<string>;
  onSendRequest: (userId: string) => void;
}

function SearchView({ sentIds, onSendRequest }: SearchViewProps) {
  const [query, setQuery] = useState("");

  const results: SearchPerson[] = query.trim().length > 0
    ? SEARCH_POOL.filter((u) =>
        u.name.toLowerCase().includes(query.toLowerCase()) ||
        u.username.toLowerCase().includes(query.toLowerCase())
      )
    : SEARCH_POOL;

  return (
    <div>
      <SectionTitle>Search Neighbors</SectionTitle>
      <div className="relative mb-5">
        <Search size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-muted-foreground" />
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search by name or username..."
          className="w-full pl-10 pr-4 py-2.5 bg-card border border-border rounded-xl text-sm text-foreground placeholder:text-muted-foreground outline-none focus:ring-2 focus:ring-ring/30 transition-shadow"
        />
      </div>

      {results.length === 0 ? (
        <div className="bg-card rounded-xl border border-border p-8 text-center text-muted-foreground text-sm">
          No neighbors found matching &ldquo;{query}&rdquo;
        </div>
      ) : (
        <div className="flex flex-col gap-3">
          {results.map((u) => {
            const alreadySent = sentIds.has(u.id);
            return (
              <div key={u.id} className="bg-card rounded-xl border border-border p-4 flex items-center gap-3">
                <Av p={u} size={48} />
                <div className="flex-1 min-w-0">
                  <div className="font-semibold text-sm text-foreground">{u.name}</div>
                  <div className="text-xs text-muted-foreground">@{u.username}</div>
                  <div className="flex items-center gap-2 mt-1">
                    <div className="flex items-center gap-1 text-xs text-muted-foreground">
                      <MapPin size={11} />
                      <span>{u.neighborhood}</span>
                    </div>
                    {u.mutualNeighbors > 0 && (
                      <span className="text-xs text-muted-foreground">· {u.mutualNeighbors} mutual neighbors</span>
                    )}
                  </div>
                </div>
                <button
                  onClick={() => !alreadySent && onSendRequest(u.id)}
                  disabled={alreadySent}
                  className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors shrink-0 ${
                    alreadySent
                      ? "bg-secondary text-muted-foreground cursor-default"
                      : "bg-primary text-primary-foreground hover:opacity-90"
                  }`}
                >
                  {alreadySent ? <Check size={13} /> : <Plus size={13} />}
                  {alreadySent ? "Request sent" : "Add neighbor"}
                </button>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}

/* ── Neighbors View ─────────────────────────────────────────────────────────── */

interface NeighborsViewProps {
  neighbors: Person[];
  received: NRequest[];
  sent: NRequest[];
  onRemove: (id: string) => void;
  onAccept: (id: string) => void;
  onDecline: (id: string) => void;
  onCancelSent: (id: string) => void;
}

function NeighborsView({ neighbors, received, sent, onRemove, onAccept, onDecline, onCancelSent }: NeighborsViewProps) {
  const [tab, setTab] = useState<"list" | "requests">("list");
  const [reqTab, setReqTab] = useState<"received" | "sent">("received");

  return (
    <div>
      <SectionTitle>Neighbours</SectionTitle>

      {/* Main tabs */}
      <div className="flex gap-1 mb-5 bg-secondary rounded-xl p-1">
        {(["list", "requests"] as const).map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={`flex-1 py-2 rounded-lg text-sm font-medium transition-colors capitalize relative ${
              tab === t ? "bg-card text-foreground shadow-sm" : "text-muted-foreground hover:text-foreground"
            }`}
          >
            {t === "list" ? "My Neighbours" : "Requests"}
            {t === "requests" && received.length > 0 && (
              <span className="ml-1.5 bg-primary text-primary-foreground text-xs rounded-full px-1.5 py-0.5">
                {received.length}
              </span>
            )}
          </button>
        ))}
      </div>

      {tab === "list" && (
        <div>
          {neighbors.length === 0 ? (
            <div className="bg-card rounded-xl border border-border p-8 text-center text-muted-foreground text-sm">
              You have no neighbours yet. Search for people in your area!
            </div>
          ) : (
            <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
              {neighbors.map((n) => (
                <div key={n.id} className="bg-card rounded-xl border border-border p-4 flex items-center gap-3">
                  <Av p={n} size={46} />
                  <div className="flex-1 min-w-0">
                    <div className="font-semibold text-sm text-foreground truncate">{n.name}</div>
                    <div className="text-xs text-muted-foreground truncate">@{n.username}</div>
                    <div className="text-xs text-muted-foreground mt-0.5">{n.neighborCount} neighbours</div>
                  </div>
                  <button
                    onClick={() => onRemove(n.id)}
                    className="p-2 rounded-lg text-muted-foreground hover:text-destructive hover:bg-destructive/10 transition-colors"
                    title="Remove neighbor"
                  >
                    <UserMinus size={15} />
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {tab === "requests" && (
        <div>
          {/* Request sub-tabs */}
          <div className="flex gap-2 mb-4">
            {(["received", "sent"] as const).map((t) => (
              <button
                key={t}
                onClick={() => setReqTab(t)}
                className={`px-4 py-1.5 rounded-full text-xs font-medium border transition-colors capitalize ${
                  reqTab === t
                    ? "bg-primary text-primary-foreground border-primary"
                    : "bg-card text-muted-foreground border-border hover:border-foreground/30"
                }`}
              >
                {t}
                {t === "received" && received.length > 0 && ` (${received.length})`}
                {t === "sent" && sent.length > 0 && ` (${sent.length})`}
              </button>
            ))}
          </div>

          {reqTab === "received" && (
            <div className="flex flex-col gap-3">
              {received.length === 0 ? (
                <div className="bg-card rounded-xl border border-border p-8 text-center text-muted-foreground text-sm">
                  No pending requests received.
                </div>
              ) : received.map((req) => (
                <div key={req.id} className="bg-card rounded-xl border border-border p-4 flex items-center gap-3">
                  <Av p={req.from} size={46} />
                  <div className="flex-1 min-w-0">
                    <div className="font-semibold text-sm text-foreground">{req.from.name}</div>
                    <div className="text-xs text-muted-foreground">@{req.from.username} · {req.sentAt}</div>
                    {req.from.bio && (
                      <div className="text-xs text-muted-foreground mt-1 line-clamp-1">{req.from.bio}</div>
                    )}
                  </div>
                  <div className="flex gap-2 shrink-0">
                    <button
                      onClick={() => onDecline(req.id)}
                      className="p-2 rounded-lg border border-border text-muted-foreground hover:bg-secondary transition-colors"
                    >
                      <X size={15} />
                    </button>
                    <button
                      onClick={() => onAccept(req.id)}
                      className="p-2 rounded-lg bg-primary text-primary-foreground hover:opacity-90 transition-opacity"
                    >
                      <Check size={15} />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}

          {reqTab === "sent" && (
            <div className="flex flex-col gap-3">
              {sent.length === 0 ? (
                <div className="bg-card rounded-xl border border-border p-8 text-center text-muted-foreground text-sm">
                  No pending sent requests.
                </div>
              ) : sent.map((req) => (
                <div key={req.id} className="bg-card rounded-xl border border-border p-4 flex items-center gap-3">
                  <Av p={req.to} size={46} />
                  <div className="flex-1 min-w-0">
                    <div className="font-semibold text-sm text-foreground">{req.to.name}</div>
                    <div className="text-xs text-muted-foreground">@{req.to.username} · Sent {req.sentAt}</div>
                  </div>
                  <button
                    onClick={() => onCancelSent(req.id)}
                    className="px-3 py-1.5 rounded-lg text-xs font-medium border border-border text-muted-foreground hover:bg-secondary transition-colors shrink-0"
                  >
                    Cancel
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

/* ── Blocked View ───────────────────────────────────────────────────────────── */

function BlockedView({ blocked, onUnblock }: { blocked: Person[]; onUnblock: (id: string) => void }) {
  return (
    <div>
      <SectionTitle>Blocked Users</SectionTitle>
      {blocked.length === 0 ? (
        <div className="bg-card rounded-xl border border-border p-8 text-center text-muted-foreground text-sm">
          You have no blocked users.
        </div>
      ) : (
        <div className="flex flex-col gap-3">
          {blocked.map((u) => (
            <div key={u.id} className="bg-card rounded-xl border border-border p-4 flex items-center gap-3">
              <div className="w-11 h-11 rounded-full bg-muted flex items-center justify-center shrink-0">
                <Shield size={18} className="text-muted-foreground" />
              </div>
              <div className="flex-1 min-w-0">
                <div className="font-semibold text-sm text-foreground">{u.name}</div>
                <div className="text-xs text-muted-foreground">@{u.username}</div>
              </div>
              <button
                onClick={() => onUnblock(u.id)}
                className="px-3 py-1.5 rounded-lg text-xs font-medium border border-border text-muted-foreground hover:bg-secondary transition-colors shrink-0"
              >
                Unblock
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

/* ── Notifications View ─────────────────────────────────────────────────────── */

function NotificationsView({ notifs, onMarkAllRead }: { notifs: Notif[]; onMarkAllRead: () => void }) {
  const unread = notifs.filter((n) => !n.read).length;

  function notifText(n: Notif): string {
    switch (n.type) {
      case "reaction": return `reacted to your post`;
      case "comment": return `commented on your post`;
      case "neighbor_request": return `sent you a neighbour request`;
      case "neighbor_accepted": return `accepted your neighbour request`;
    }
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-5">
        <h2 className="text-xl font-semibold text-foreground">Notifications</h2>
        {unread > 0 && (
          <button
            onClick={onMarkAllRead}
            className="text-xs font-medium text-primary hover:opacity-70 transition-opacity"
          >
            Mark all as read
          </button>
        )}
      </div>

      {notifs.length === 0 ? (
        <div className="bg-card rounded-xl border border-border p-8 text-center text-muted-foreground text-sm">
          No notifications yet.
        </div>
      ) : (
        <div className="flex flex-col gap-2">
          {notifs.map((n) => (
            <div
              key={n.id}
              className={`bg-card rounded-xl border p-4 flex items-start gap-3 transition-colors ${
                !n.read ? "border-primary/30 bg-primary/[0.03]" : "border-border"
              }`}
            >
              <div className="relative shrink-0">
                <Av p={n.actor} size={40} />
                {!n.read && (
                  <span className="absolute -top-0.5 -right-0.5 w-2.5 h-2.5 bg-primary rounded-full border-2 border-card" />
                )}
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm text-foreground">
                  <span className="font-semibold">{n.actor.name}</span>{" "}
                  <span className="text-muted-foreground">{notifText(n)}</span>
                </p>
                {n.excerpt && (
                  <p className="text-xs text-muted-foreground mt-0.5 line-clamp-1 italic">"{n.excerpt}"</p>
                )}
                <div className="text-xs text-muted-foreground mt-1">{n.at}</div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

/* ── App ────────────────────────────────────────────────────────────────────── */

export default function App() {
  const [view, setView] = useState<View>("feed");
  const [posts, setPosts] = useState<Post[]>(INITIAL_POSTS);
  const [openComments, setOpenComments] = useState<Set<string>>(new Set());
  const [commentInputs, setCommentInputs] = useState<Record<string, string>>({});
  const [neighbors, setNeighbors] = useState<Person[]>(INITIAL_NEIGHBORS);
  const [received, setReceived] = useState<NRequest[]>(INITIAL_RECEIVED);
  const [sent, setSent] = useState<NRequest[]>(INITIAL_SENT);
  const [blocked, setBlocked] = useState<Person[]>(INITIAL_BLOCKED);
  const [notifs, setNotifs] = useState<Notif[]>(INITIAL_NOTIFS);
  const [sentRequestIds, setSentRequestIds] = useState<Set<string>>(new Set(["u8"]));

  const unreadCount = notifs.filter((n) => !n.read).length;
  const receivedCount = received.length;

  function toggleReaction(postId: string, rxn: Rxn) {
    setPosts((prev) =>
      prev.map((p) => {
        if (p.id !== postId) return p;
        const prev_rxn = p.userReaction;
        const reactions = { ...p.reactions };
        if (prev_rxn) reactions[prev_rxn] = Math.max(0, reactions[prev_rxn] - 1);
        if (prev_rxn !== rxn) reactions[rxn] = reactions[rxn] + 1;
        return { ...p, reactions, userReaction: prev_rxn === rxn ? null : rxn };
      })
    );
  }

  function toggleComments(postId: string) {
    setOpenComments((prev) => {
      const next = new Set(prev);
      next.has(postId) ? next.delete(postId) : next.add(postId);
      return next;
    });
  }

  function submitComment(postId: string) {
    const text = (commentInputs[postId] || "").trim();
    if (text.length < 10) return;
    setPosts((prev) =>
      prev.map((p) => {
        if (p.id !== postId) return p;
        return {
          ...p,
          comments: [...p.comments, { id: `new-${Date.now()}`, author: ME, content: text, createdAt: "Just now" }],
        };
      })
    );
    setCommentInputs((prev) => ({ ...prev, [postId]: "" }));
  }

  function createPost(content: string, type: PostType, isPublic: boolean) {
    const newPost: Post = {
      id: `p-${Date.now()}`,
      author: ME,
      type,
      content,
      isPublic,
      createdAt: "Just now",
      reactions: { like: 0, heart: 0, alert: 0 },
      userReaction: null,
      comments: [],
    };
    setPosts((prev) => [newPost, ...prev]);
  }

  function acceptRequest(reqId: string) {
    const req = received.find((r) => r.id === reqId);
    if (req) {
      setNeighbors((prev) => [...prev, req.from]);
      setNotifs((prev) => [
        { id: `n-${Date.now()}`, type: "neighbor_accepted", actor: req.from, read: false, at: "Just now" },
        ...prev,
      ]);
    }
    setReceived((prev) => prev.filter((r) => r.id !== reqId));
  }

  function declineRequest(reqId: string) {
    setReceived((prev) => prev.filter((r) => r.id !== reqId));
  }

  function cancelSent(reqId: string) {
    const req = sent.find((r) => r.id === reqId);
    if (req) setSentRequestIds((prev) => { const s = new Set(prev); s.delete(req.to.id); return s; });
    setSent((prev) => prev.filter((r) => r.id !== reqId));
  }

  function removeNeighbor(userId: string) {
    setNeighbors((prev) => prev.filter((n) => n.id !== userId));
  }

  function unblock(userId: string) {
    setBlocked((prev) => prev.filter((u) => u.id !== userId));
  }

  function markAllRead() {
    setNotifs((prev) => prev.map((n) => ({ ...n, read: true })));
  }

  function sendRequest(userId: string) {
    const person = SEARCH_POOL.find((u) => u.id === userId);
    if (!person) return;
    setSentRequestIds((prev) => new Set([...prev, userId]));
    setSent((prev) => [...prev, { id: `sr-${Date.now()}`, from: ME, to: person, sentAt: "Just now" }]);
  }

  const VIEW_TITLES: Record<View, string> = {
    feed: "Home",
    profile: "My Profile",
    search: "Search Neighbors",
    neighbors: "Neighbours",
    blocked: "Blocked Users",
    notifications: "Notifications",
  };

  return (
    <div className="min-h-screen bg-background flex">
      <Sidebar
        view={view}
        setView={setView}
        unreadCount={unreadCount}
        receivedCount={receivedCount}
      />

      {/* Main */}
      <div className="flex-1 ml-64 min-h-screen flex flex-col">
        {/* Top bar */}
        <header className="sticky top-0 z-10 bg-background/90 backdrop-blur border-b border-border px-8 py-4">
          <h1 className="text-base font-semibold text-foreground">{VIEW_TITLES[view]}</h1>
        </header>

        {/* Content */}
        <main className="flex-1 px-8 py-6 max-w-2xl w-full mx-auto">
          {view === "feed" && (
            <div>
              <CreatePost onSubmit={createPost} />
              {posts.map((post) => (
                <PostCard
                  key={post.id}
                  post={post}
                  commentsOpen={openComments.has(post.id)}
                  commentInput={commentInputs[post.id] || ""}
                  onToggleComments={() => toggleComments(post.id)}
                  onCommentChange={(v) => setCommentInputs((prev) => ({ ...prev, [post.id]: v }))}
                  onCommentSubmit={() => submitComment(post.id)}
                  onReact={(rxn) => toggleReaction(post.id, rxn)}
                />
              ))}
            </div>
          )}

          {view === "profile" && <ProfileView posts={posts} />}

          {view === "search" && (
            <SearchView sentIds={sentRequestIds} onSendRequest={sendRequest} />
          )}

          {view === "neighbors" && (
            <NeighborsView
              neighbors={neighbors}
              received={received}
              sent={sent}
              onRemove={removeNeighbor}
              onAccept={acceptRequest}
              onDecline={declineRequest}
              onCancelSent={cancelSent}
            />
          )}

          {view === "blocked" && (
            <BlockedView blocked={blocked} onUnblock={unblock} />
          )}

          {view === "notifications" && (
            <NotificationsView notifs={notifs} onMarkAllRead={markAllRead} />
          )}
        </main>
      </div>
    </div>
  );
}
