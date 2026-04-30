import { motion } from "framer-motion";
import { Star, Clock, Heart, Plus } from "lucide-react";
import { useState } from "react";
import { Link } from "react-router-dom";
import { restaurants, featured } from "@/data/menu";
import { useCart } from "@/context/CartContext";

export default function Restaurants() {
  const { add } = useCart();
  const [favs, setFavs] = useState<Record<string, boolean>>({});
  const toggleFav = (id: string) => setFavs((f) => ({ ...f, [id]: !f[id] }));

  return (
    <section className="container py-20 md:py-28">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true, margin: "-100px" }}
        transition={{ duration: 0.6 }}
        className="mb-10"
      >
        <div className="text-sm font-semibold text-secondary uppercase tracking-wider">Popular near you</div>
        <h2 className="mt-2 font-display font-bold text-4xl md:text-5xl tracking-tight">
          Restaurants <span className="text-gradient-brand">trending</span> tonight
        </h2>
      </motion.div>

      <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {restaurants.map((r, i) => (
          <motion.div
            key={r.id}
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-50px" }}
            transition={{ duration: 0.5, delay: i * 0.08 }}
          >
            <Link to={`/restaurant/${r.id}`} className="group relative block rounded-3xl overflow-hidden glass border border-border/50 hover-lift">
            <div className="relative aspect-[4/3] overflow-hidden">
              <img
                src={r.cover}
                alt={r.name}
                loading="lazy"
                width={1024}
                height={768}
                className="h-full w-full object-cover transition-transform duration-700 group-hover:scale-110"
              />
              <div className="absolute inset-0 bg-gradient-to-t from-background via-background/30 to-transparent" />
              <span className="absolute top-3 left-3 px-2.5 py-1 rounded-full bg-gradient-brand text-primary-foreground text-[11px] font-bold shadow-glow-pink">
                {r.tag}
              </span>
              <button
                onClick={(e) => { e.preventDefault(); toggleFav(r.id); }}
                aria-label="Favorite"
                className="absolute top-3 right-3 grid place-items-center h-9 w-9 rounded-full glass-strong hover:scale-110 transition"
              >
                <Heart className={`h-4 w-4 transition-all ${favs[r.id] ? "fill-primary text-primary scale-110" : "text-foreground"}`} />
              </button>
              <div className="absolute bottom-3 left-3 right-3 flex items-center justify-between text-xs text-foreground">
                <span className="glass-strong rounded-full px-2.5 py-1 flex items-center gap-1 font-semibold">
                  <Star className="h-3 w-3 fill-secondary text-secondary" /> {r.rating}
                </span>
                <span className="glass-strong rounded-full px-2.5 py-1 flex items-center gap-1 font-medium">
                  <Clock className="h-3 w-3" /> {r.eta}
                </span>
              </div>
            </div>
            <div className="p-5">
              <h3 className="font-display font-bold text-lg leading-tight">{r.name}</h3>
              <p className="text-xs text-muted-foreground mt-1">{r.cuisine}</p>
              <div className="mt-3 flex items-center justify-between">
                <span className="text-xs font-medium text-primary">{r.fee}</span>
                <span className="inline-flex items-center gap-1 text-[11px] text-muted-foreground">
                  <span className="h-1.5 w-1.5 rounded-full bg-emerald-500 animate-pulse" /> Open now
                </span>
              </div>
            </div>
            </Link>
          </motion.div>
        ))}
      </div>

      {/* Featured dishes */}
      <div className="mt-24">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          className="flex items-end justify-between mb-10"
        >
          <div>
            <div className="text-sm font-semibold text-primary uppercase tracking-wider">Tap to add</div>
            <h2 className="mt-2 font-display font-bold text-3xl md:text-4xl tracking-tight">
              Tonight's <span className="text-gradient-brand">chef picks</span>
            </h2>
          </div>
        </motion.div>

        <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {featured.map((d, i) => (
            <motion.div
              key={d.id}
              initial={{ opacity: 0, y: 30 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, margin: "-50px" }}
              transition={{ duration: 0.5, delay: i * 0.07 }}
              className="group relative rounded-3xl glass border border-border/50 p-6 pt-24 overflow-hidden hover-lift"
            >
              <div className="absolute -top-10 right-4 h-44 w-44">
                <div className="absolute inset-0 rounded-full bg-gradient-brand opacity-30 blur-2xl group-hover:opacity-60 transition" />
                <img
                  src={d.img}
                  alt={d.name}
                  loading="lazy"
                  width={300}
                  height={300}
                  className="relative h-44 w-44 object-contain drop-shadow-[0_20px_40px_hsl(var(--primary)/0.45)] transition-transform duration-500 group-hover:-rotate-12 group-hover:scale-110"
                />
              </div>

              {d.tag && (
                <span className="absolute top-4 left-4 px-2.5 py-1 rounded-full bg-foreground text-background text-[11px] font-bold">
                  {d.tag}
                </span>
              )}

              <div className="relative z-10 max-w-[55%]">
                <div className="text-xs text-muted-foreground">{d.restaurant}</div>
                <h3 className="mt-1 font-display font-bold text-xl leading-tight">{d.name}</h3>
                <div className="mt-3 flex items-center gap-3 text-xs text-muted-foreground">
                  <span className="inline-flex items-center gap-1">
                    <Star className="h-3 w-3 fill-secondary text-secondary" /> {d.rating}
                  </span>
                  <span className="inline-flex items-center gap-1">
                    <Clock className="h-3 w-3" /> {d.eta}
                  </span>
                </div>
              </div>

              <div className="relative z-10 mt-6 flex items-end justify-between">
                <div>
                  <div className="text-[11px] text-muted-foreground uppercase tracking-wider">From</div>
                  <div className="font-display font-bold text-2xl">${d.price.toFixed(2)}</div>
                </div>
                <button
                  onClick={() => add(d)}
                  className="group/btn relative h-12 px-5 rounded-2xl bg-gradient-brand text-primary-foreground font-semibold shadow-glow-pink overflow-hidden hover:scale-105 active:scale-95 transition flex items-center gap-2"
                >
                  <span className="absolute inset-0 bg-white/20 translate-y-full group-hover/btn:translate-y-0 transition-transform duration-500" />
                  <Plus className="h-4 w-4 relative" />
                  <span className="relative">Add</span>
                </button>
              </div>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
}
