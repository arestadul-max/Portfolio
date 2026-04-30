import { motion } from "framer-motion";
import { Star, Clock } from "lucide-react";
import { Link } from "react-router-dom";

export type RestaurantLite = {
  id: string; name: string; cuisine: string; cover: string;
  rating: number; eta: string; fee: string; tag?: string;
};

export default function RestaurantCard({ r, i = 0 }: { r: RestaurantLite; i?: number }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 30 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, margin: "-50px" }}
      transition={{ duration: 0.5, delay: i * 0.06 }}
    >
      <Link to={`/restaurant/${r.id}`} className="group relative block rounded-3xl overflow-hidden glass border border-border/50 hover-lift">
        <div className="relative aspect-[4/3] overflow-hidden">
          <img src={r.cover} alt={r.name} loading="lazy" className="h-full w-full object-cover transition-transform duration-700 group-hover:scale-110" />
          <div className="absolute inset-0 bg-gradient-to-t from-background via-background/30 to-transparent" />
          {r.tag && <span className="absolute top-3 left-3 px-2.5 py-1 rounded-full bg-gradient-brand text-primary-foreground text-[11px] font-bold shadow-glow-pink">{r.tag}</span>}
          <div className="absolute bottom-3 left-3 right-3 flex items-center justify-between text-xs">
            <span className="glass-strong rounded-full px-2.5 py-1 flex items-center gap-1 font-semibold"><Star className="h-3 w-3 fill-secondary text-secondary" />{r.rating}</span>
            <span className="glass-strong rounded-full px-2.5 py-1 flex items-center gap-1 font-medium"><Clock className="h-3 w-3" />{r.eta}</span>
          </div>
        </div>
        <div className="p-5">
          <h3 className="font-display font-bold text-lg leading-tight">{r.name}</h3>
          <p className="text-xs text-muted-foreground mt-1">{r.cuisine}</p>
          <div className="mt-3 flex items-center justify-between">
            <span className="text-xs font-medium text-primary">{r.fee}</span>
            <span className="inline-flex items-center gap-1 text-[11px] text-muted-foreground"><span className="h-1.5 w-1.5 rounded-full bg-emerald-500 animate-pulse" />Open now</span>
          </div>
        </div>
      </Link>
    </motion.div>
  );
}