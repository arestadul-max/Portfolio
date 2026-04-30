import { motion } from "framer-motion";
import { Search, ShoppingBag, Heart, MapPin, Sun, Moon, User as UserIcon } from "lucide-react";
import { useEffect, useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useCart } from "@/context/CartContext";
import { useAuth } from "@/context/AuthContext";
import { Button } from "@/components/ui/button";

export default function Navbar() {
  const { count, setOpen } = useCart();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [dark, setDark] = useState(true);
  const [searchOpen, setSearchOpen] = useState(false);
  const [q, setQ] = useState("");

  useEffect(() => {
    document.documentElement.classList.toggle("dark", dark);
  }, [dark]);

  const submitSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (q.trim()) { navigate(`/search?q=${encodeURIComponent(q.trim())}`); setSearchOpen(false); }
  };

  const links = [
    { to: "/restaurants", label: "Restaurants" },
    { to: "/categories", label: "Categories" },
    { to: "/offers", label: "Offers" },
    { to: "/track", label: "Track Order" },
  ];

  return (
    <motion.header
      initial={{ y: -40, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ duration: 0.6, ease: "easeOut" }}
      className="fixed top-4 inset-x-4 z-50 mx-auto max-w-7xl"
    >
      <div className="glass-strong rounded-2xl px-5 py-3 flex items-center gap-4 shadow-soft">
        <Link to="/" className="flex items-center gap-2 group">
          <div className="relative h-9 w-9 rounded-xl bg-gradient-brand grid place-items-center shadow-glow-pink">
            <span className="text-lg">🍱</span>
            <div className="absolute inset-0 rounded-xl bg-gradient-brand opacity-50 blur-md group-hover:opacity-80 transition" />
          </div>
          <span className="font-display font-bold text-xl tracking-tight">
            food<span className="text-gradient-brand">ly</span>
          </span>
        </Link>

        <div className="hidden md:flex items-center gap-1.5 ml-4 text-sm text-muted-foreground">
          <MapPin className="h-4 w-4 text-primary" />
          <span>Deliver to <span className="text-foreground font-medium">Downtown</span></span>
        </div>

        <nav className="hidden lg:flex items-center gap-6 mx-auto text-sm font-medium">
          {links.map((n) => (
            <NavLink key={n.to} to={n.to} className={({ isActive }) => `relative transition group ${isActive ? "text-foreground" : "text-muted-foreground hover:text-foreground"}`}>
              {n.label}
              <span className="absolute -bottom-1 left-0 h-0.5 w-0 bg-gradient-brand group-hover:w-full transition-all duration-300" />
            </NavLink>
          ))}
        </nav>

        <div className="flex items-center gap-2 ml-auto">
          <button
            aria-label="Search"
            onClick={() => setSearchOpen((v) => !v)}
            className="hidden sm:grid place-items-center h-10 w-10 rounded-xl glass hover:scale-105 transition"
          >
            <Search className="h-4 w-4" />
          </button>
          <Link
            to={user ? "/account/favorites" : "/auth"}
            aria-label="Wishlist"
            className="hidden sm:grid place-items-center h-10 w-10 rounded-xl glass hover:scale-105 transition"
          >
            <Heart className="h-4 w-4" />
          </Link>
          <button
            aria-label="Theme"
            onClick={() => setDark((v) => !v)}
            className="grid place-items-center h-10 w-10 rounded-xl glass hover:scale-105 transition"
          >
            {dark ? <Sun className="h-4 w-4" /> : <Moon className="h-4 w-4" />}
          </button>
          <button
            onClick={() => setOpen(true)}
            className="relative grid place-items-center h-10 w-10 rounded-xl bg-gradient-brand shadow-glow-pink hover:scale-105 transition"
            aria-label="Cart"
          >
            <ShoppingBag className="h-4 w-4 text-primary-foreground" />
            {count > 0 && (
              <motion.span
                key={count}
                initial={{ scale: 0 }}
                animate={{ scale: 1 }}
                className="absolute -top-1 -right-1 min-w-5 h-5 px-1 rounded-full bg-foreground text-background text-[11px] font-bold grid place-items-center"
              >
                {count}
              </motion.span>
            )}
          </button>
          {user ? (
            <Link to="/account" className="hidden md:inline-flex items-center gap-2 h-10 px-3 rounded-xl glass border border-border/60 font-semibold text-sm hover:scale-[1.02] transition">
              <UserIcon className="h-4 w-4" /> Account
            </Link>
          ) : (
            <Link to="/auth" className="hidden md:inline-flex items-center h-10 px-4 rounded-xl bg-foreground text-background font-semibold text-sm hover:bg-foreground/90">
              Sign in
            </Link>
          )}
        </div>
      </div>

      {searchOpen && (
        <motion.form
          initial={{ opacity: 0, y: -8 }}
          animate={{ opacity: 1, y: 0 }}
          onSubmit={submitSearch}
          className="mt-2 glass-strong rounded-2xl p-2 flex items-center gap-2 ring-glow border border-border/60"
        >
          <Search className="h-4 w-4 ml-3 text-muted-foreground" />
          <input autoFocus value={q} onChange={(e) => setQ(e.target.value)} placeholder="Search restaurants or dishes…" className="flex-1 bg-transparent outline-none py-2.5 text-sm" />
          <button className="h-9 px-4 rounded-xl bg-gradient-brand text-primary-foreground font-semibold text-sm shadow-glow-pink">Go</button>
        </motion.form>
      )}
    </motion.header>
  );
}
