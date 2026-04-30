import { motion, useReducedMotion } from "framer-motion";
import { Search, Mic, Sparkles, ArrowRight, Star } from "lucide-react";
import { useNavigate, Link } from "react-router-dom";
import { useState } from "react";
import burger from "@/assets/burger.png";
import pizza from "@/assets/pizza.png";
import sushi from "@/assets/sushi.png";
import drink from "@/assets/drink.png";

const offers = [
  "🔥 50% off your first order — code WELCOME50",
  "🚀 Free delivery on orders over $20",
  "🎁 Earn 2x loyalty points all weekend",
  "⏱️ Order in under 30 min — guaranteed",
];

export default function Hero() {
  const reduce = useReducedMotion();
  const navigate = useNavigate();
  const [q, setQ] = useState("");
  const submit = (e: React.FormEvent) => { e.preventDefault(); if (q.trim()) navigate(`/search?q=${encodeURIComponent(q.trim())}`); };
  return (
    <section className="relative pt-32 pb-20 md:pt-40 md:pb-32 overflow-hidden">
      {/* Glow background */}
      <div className="absolute inset-0 bg-gradient-radial pointer-events-none" />
      <div className="absolute -top-40 -left-40 w-[36rem] h-[36rem] bg-primary/30 blur-[120px] rounded-full animate-blob" />
      <div className="absolute -bottom-40 -right-40 w-[36rem] h-[36rem] bg-secondary/30 blur-[120px] rounded-full animate-blob" />

      {/* Floating dots */}
      {!reduce &&
        Array.from({ length: 14 }).map((_, i) => (
          <motion.span
            key={i}
            className="absolute rounded-full bg-gradient-brand opacity-40"
            style={{
              width: `${4 + (i % 4) * 3}px`,
              height: `${4 + (i % 4) * 3}px`,
              top: `${(i * 53) % 100}%`,
              left: `${(i * 37) % 100}%`,
            }}
            animate={{ y: [0, -30, 0], opacity: [0.3, 0.8, 0.3] }}
            transition={{ duration: 6 + (i % 5), repeat: Infinity, delay: i * 0.3 }}
          />
        ))}

      <div className="container relative grid lg:grid-cols-[1.05fr_1fr] gap-12 items-center">
        {/* Left */}
        <div className="relative z-10">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6 }}
            className="inline-flex items-center gap-2 glass rounded-full px-4 py-1.5 text-xs font-medium"
          >
            <Sparkles className="h-3.5 w-3.5 text-primary" />
            <span>AI-powered cravings, delivered hot</span>
          </motion.div>

          <motion.h1
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.7, delay: 0.05 }}
            className="mt-6 font-display font-extrabold text-5xl md:text-6xl lg:text-7xl leading-[1.05] tracking-tight"
          >
            Crave it.{" "}
            <span className="text-gradient-brand">Tap it.</span>
            <br />
            Devour it.
          </motion.h1>

          <motion.p
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.7, delay: 0.15 }}
            className="mt-6 text-lg text-muted-foreground max-w-xl"
          >
            Discover 12,000+ restaurants near you. Hot food, ice-cold drinks, and lightning fast delivery — every single time.
          </motion.p>

          {/* Search */}
          <motion.form
            onSubmit={submit}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.7, delay: 0.25 }}
            className="mt-8 glass-strong rounded-2xl p-2 flex items-center gap-2 ring-glow border border-border/60 transition-all"
          >
            <div className="pl-3 text-muted-foreground">
              <Search className="h-5 w-5" />
            </div>
            <input
              value={q}
              onChange={(e) => setQ(e.target.value)}
              placeholder="Search restaurants, cuisines or dishes…"
              className="flex-1 bg-transparent outline-none text-base placeholder:text-muted-foreground py-3"
            />
            <button type="button" className="grid place-items-center h-10 w-10 rounded-xl glass hover:scale-105 transition" aria-label="Voice">
              <Mic className="h-4 w-4" />
            </button>
            <button type="submit" className="h-11 px-5 rounded-xl bg-gradient-brand text-primary-foreground font-semibold shadow-glow-pink hover:scale-[1.02] active:scale-[0.98] transition flex items-center gap-2">
              Find food
              <ArrowRight className="h-4 w-4" />
            </button>
          </motion.form>

          {/* CTAs */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.7, delay: 0.35 }}
            className="mt-6 flex flex-wrap items-center gap-4"
          >
            <Link to="/delivery" className="h-12 px-6 rounded-xl bg-foreground text-background font-semibold hover:scale-[1.02] transition inline-flex items-center">
              Order now
            </Link>
            <Link to="/restaurants" className="h-12 px-6 rounded-xl glass font-semibold hover:scale-[1.02] transition inline-flex items-center">
              Explore restaurants
            </Link>
          </motion.div>

          {/* Stats */}
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.7, delay: 0.5 }}
            className="mt-10 grid grid-cols-3 max-w-md gap-4"
          >
            {[
              { k: "4.9", l: "App rating", icon: <Star className="h-3.5 w-3.5 fill-secondary text-secondary" /> },
              { k: "12K+", l: "Restaurants" },
              { k: "22m", l: "Avg. delivery" },
            ].map((s) => (
              <div key={s.l} className="glass rounded-xl p-3">
                <div className="flex items-center gap-1 font-display font-bold text-2xl">
                  {s.k} {s.icon}
                </div>
                <div className="text-xs text-muted-foreground">{s.l}</div>
              </div>
            ))}
          </motion.div>
        </div>

        {/* Right — floating food */}
        <div className="relative h-[480px] md:h-[560px] lg:h-[620px]">
          {/* Center plate */}
          <motion.div
            initial={{ scale: 0.8, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            transition={{ duration: 0.8, delay: 0.2 }}
            className="absolute inset-0 grid place-items-center"
          >
            <div className="relative h-[360px] w-[360px] md:h-[440px] md:w-[440px]">
              <div className="absolute inset-0 rounded-full bg-gradient-brand opacity-30 blur-3xl animate-glow-pulse" />
              <div className="absolute inset-8 rounded-full bg-gradient-brand-soft border border-border/40 backdrop-blur-xl" />
              <div className="absolute inset-0 rounded-full border border-dashed border-primary/30 animate-spin-slow" />
              <img
                src={burger}
                alt="Hero burger"
                width={440}
                height={440}
                className="absolute inset-0 m-auto w-[78%] h-auto drop-shadow-[0_30px_50px_hsl(var(--primary)/0.45)] animate-float"
              />
            </div>
          </motion.div>

          {/* Pizza floating */}
          <motion.img
            src={pizza}
            alt=""
            width={220}
            height={220}
            className="absolute top-2 -left-4 w-32 md:w-44 drop-shadow-[0_20px_40px_hsl(var(--secondary)/0.5)] animate-float-slow"
            initial={{ x: -50, opacity: 0 }}
            animate={{ x: 0, opacity: 1 }}
            transition={{ delay: 0.4, duration: 0.8 }}
          />
          <motion.img
            src={sushi}
            alt=""
            width={220}
            height={220}
            className="absolute bottom-12 -right-4 w-36 md:w-48 drop-shadow-[0_20px_40px_hsl(var(--primary)/0.45)] animate-float"
            initial={{ x: 50, opacity: 0 }}
            animate={{ x: 0, opacity: 1 }}
            transition={{ delay: 0.5, duration: 0.8 }}
          />
          <motion.img
            src={drink}
            alt=""
            width={180}
            height={180}
            className="absolute bottom-0 left-4 w-24 md:w-32 drop-shadow-[0_20px_40px_hsl(var(--primary)/0.45)] animate-float-slow"
            initial={{ y: 50, opacity: 0 }}
            animate={{ y: 0, opacity: 1 }}
            transition={{ delay: 0.6, duration: 0.8 }}
          />

          {/* Floating chip - delivery */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.8 }}
            className="absolute top-6 right-2 glass-strong rounded-2xl px-4 py-3 shadow-soft"
          >
            <div className="text-[10px] uppercase tracking-wider text-muted-foreground">Arriving in</div>
            <div className="font-display font-bold text-xl">18 min</div>
          </motion.div>

          {/* Floating chip - rating */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.9 }}
            className="absolute bottom-6 right-10 glass-strong rounded-2xl px-4 py-3 shadow-soft flex items-center gap-2"
          >
            <Star className="h-4 w-4 fill-secondary text-secondary" />
            <div>
              <div className="font-display font-bold text-sm leading-none">4.9</div>
              <div className="text-[10px] text-muted-foreground">2.4k reviews</div>
            </div>
          </motion.div>
        </div>
      </div>

      {/* Promo marquee */}
      <div className="relative mt-16 overflow-hidden border-y border-border/50 bg-gradient-brand-soft py-4">
        <div className="flex w-max animate-marquee gap-12 whitespace-nowrap text-sm font-medium">
          {[...offers, ...offers, ...offers].map((o, i) => (
            <span key={i} className="flex items-center gap-3">
              <span className="h-1.5 w-1.5 rounded-full bg-gradient-brand" />
              {o}
            </span>
          ))}
        </div>
      </div>
    </section>
  );
}
