import { motion } from "framer-motion";
import { CheckCircle2, ChefHat, Bike, MapPin, Phone, MessageSquare } from "lucide-react";

const stages = [
  { icon: CheckCircle2, label: "Confirmed", time: "8:42 PM", done: true },
  { icon: ChefHat, label: "Preparing", time: "8:48 PM", done: true },
  { icon: Bike, label: "On the way", time: "9:02 PM", active: true },
  { icon: MapPin, label: "Delivered", time: "ETA 9:18 PM", done: false },
];

export default function OrderTracking() {
  return (
    <section className="container py-20 md:py-28">
      <div className="grid lg:grid-cols-[1fr_1.1fr] gap-10 items-center">
        <motion.div
          initial={{ opacity: 0, x: -30 }}
          whileInView={{ opacity: 1, x: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.7 }}
        >
          <div className="text-sm font-semibold text-primary uppercase tracking-wider">Live order tracking</div>
          <h2 className="mt-2 font-display font-bold text-4xl md:text-5xl tracking-tight">
            See your food <span className="text-gradient-brand">move</span> in real time
          </h2>
          <p className="mt-4 text-muted-foreground max-w-md">
            Watch your rider zip across the city, get push notifications at every milestone, and tip with one tap when your meal arrives.
          </p>

          <ol className="mt-10 space-y-5">
            {stages.map((s, i) => {
              const Icon = s.icon;
              return (
                <motion.li
                  key={s.label}
                  initial={{ opacity: 0, y: 10 }}
                  whileInView={{ opacity: 1, y: 0 }}
                  viewport={{ once: true }}
                  transition={{ delay: i * 0.1 }}
                  className="flex items-center gap-4"
                >
                  <div className={`relative grid place-items-center h-12 w-12 rounded-2xl ${s.done || s.active ? "bg-gradient-brand text-primary-foreground shadow-glow-pink" : "glass text-muted-foreground"}`}>
                    {s.active && <span className="absolute inset-0 rounded-2xl bg-gradient-brand animate-ping opacity-40" />}
                    <Icon className="h-5 w-5 relative" />
                  </div>
                  <div className="flex-1">
                    <div className="font-display font-semibold">{s.label}</div>
                    <div className="text-xs text-muted-foreground">{s.time}</div>
                  </div>
                  {s.active && (
                    <span className="text-xs font-bold text-primary uppercase tracking-wider">Now</span>
                  )}
                </motion.li>
              );
            })}
          </ol>

          <div className="mt-8 flex items-center gap-3">
            <button className="h-11 px-4 rounded-xl glass font-semibold flex items-center gap-2 hover:scale-[1.02] transition">
              <Phone className="h-4 w-4" /> Call rider
            </button>
            <button className="h-11 px-4 rounded-xl glass font-semibold flex items-center gap-2 hover:scale-[1.02] transition">
              <MessageSquare className="h-4 w-4" /> Message
            </button>
          </div>
        </motion.div>

        {/* Map mock */}
        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          whileInView={{ opacity: 1, scale: 1 }}
          viewport={{ once: true }}
          transition={{ duration: 0.7 }}
          className="relative aspect-[4/3] rounded-3xl glass-strong border border-border/50 overflow-hidden shadow-soft"
        >
          {/* Map grid */}
          <div className="absolute inset-0 bg-gradient-radial opacity-50" />
          <svg className="absolute inset-0 w-full h-full opacity-20" preserveAspectRatio="none" viewBox="0 0 400 300">
            {Array.from({ length: 20 }).map((_, i) => (
              <line key={`v${i}`} x1={i * 20} y1="0" x2={i * 20} y2="300" stroke="currentColor" strokeWidth="0.5" />
            ))}
            {Array.from({ length: 15 }).map((_, i) => (
              <line key={`h${i}`} x1="0" y1={i * 20} x2="400" y2={i * 20} stroke="currentColor" strokeWidth="0.5" />
            ))}
          </svg>

          {/* Route */}
          <svg className="absolute inset-0 w-full h-full" viewBox="0 0 400 300" preserveAspectRatio="none">
            <defs>
              <linearGradient id="route" x1="0" x2="1">
                <stop offset="0%" stopColor="hsl(var(--primary))" />
                <stop offset="100%" stopColor="hsl(var(--secondary))" />
              </linearGradient>
            </defs>
            <path
              id="rider-path"
              d="M40,240 C120,240 140,160 200,160 S320,80 360,60"
              fill="none"
              stroke="url(#route)"
              strokeWidth="4"
              strokeLinecap="round"
              strokeDasharray="500"
              strokeDashoffset="0"
            />
          </svg>

          {/* Restaurant marker */}
          <div className="absolute bottom-[20%] left-[10%]">
            <div className="relative grid place-items-center h-10 w-10 rounded-full bg-foreground text-background shadow-soft">
              <ChefHat className="h-4 w-4" />
              <span className="absolute -top-1 -right-1 h-3 w-3 rounded-full bg-secondary border-2 border-background" />
            </div>
          </div>

          {/* Home marker */}
          <div className="absolute top-[20%] right-[10%]">
            <div className="grid place-items-center h-10 w-10 rounded-full bg-gradient-brand text-primary-foreground shadow-glow-pink">
              <MapPin className="h-4 w-4" />
            </div>
          </div>

          {/* Animated rider */}
          <motion.div
            className="absolute h-12 w-12 grid place-items-center"
            initial={false}
            animate={{
              left: ["10%", "30%", "50%", "70%", "88%"],
              top: ["80%", "78%", "55%", "30%", "20%"],
            }}
            transition={{ duration: 8, repeat: Infinity, ease: "easeInOut" }}
          >
            <div className="absolute inset-0 rounded-full bg-primary/40 blur-xl animate-glow-pulse" />
            <div className="relative grid place-items-center h-11 w-11 rounded-full bg-background border-2 border-primary shadow-glow-pink">
              <Bike className="h-5 w-5 text-primary" />
            </div>
          </motion.div>

          {/* Floating ETA card */}
          <div className="absolute top-4 left-4 glass-strong rounded-2xl px-4 py-3 shadow-soft">
            <div className="text-[10px] uppercase tracking-wider text-muted-foreground">Rider</div>
            <div className="font-display font-bold text-sm">Marcus · 1.2 km away</div>
          </div>
          <div className="absolute bottom-4 right-4 glass-strong rounded-2xl px-4 py-3 shadow-soft">
            <div className="text-[10px] uppercase tracking-wider text-muted-foreground">Arriving in</div>
            <div className="font-display font-bold text-2xl text-gradient-brand">9 min</div>
          </div>
        </motion.div>
      </div>
    </section>
  );
}
