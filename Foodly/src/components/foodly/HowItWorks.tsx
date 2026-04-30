import { motion } from "framer-motion";
import { Search, UtensilsCrossed, Bike, PartyPopper } from "lucide-react";

const steps = [
  { icon: Search, title: "Discover", desc: "Browse 12K+ restaurants and AI-curated dishes near you." },
  { icon: UtensilsCrossed, title: "Order", desc: "Customize your meal, apply coupons, and check out in seconds." },
  { icon: Bike, title: "Track", desc: "Watch your rider in real-time on the live delivery map." },
  { icon: PartyPopper, title: "Enjoy", desc: "Hot food at your door — earn loyalty points with every order." },
];

export default function HowItWorks() {
  return (
    <section className="container py-20 md:py-28">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true }}
        className="text-center max-w-2xl mx-auto"
      >
        <div className="text-sm font-semibold text-secondary uppercase tracking-wider">How it works</div>
        <h2 className="mt-2 font-display font-bold text-4xl md:text-5xl tracking-tight">
          From craving to <span className="text-gradient-brand">doorstep</span> in 4 taps
        </h2>
      </motion.div>

      <div className="mt-14 grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {steps.map((s, i) => {
          const Icon = s.icon;
          return (
            <motion.div
              key={s.title}
              initial={{ opacity: 0, y: 30 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, margin: "-50px" }}
              transition={{ duration: 0.5, delay: i * 0.08 }}
              className="relative rounded-3xl glass border border-border/50 p-6 hover-lift"
            >
              <div className="absolute -top-3 -left-3 grid place-items-center h-10 w-10 rounded-2xl bg-foreground text-background font-display font-bold text-sm shadow-soft">
                0{i + 1}
              </div>
              <div className="grid place-items-center h-14 w-14 rounded-2xl bg-gradient-brand-soft text-primary mb-4">
                <Icon className="h-6 w-6" />
              </div>
              <h3 className="font-display font-bold text-lg">{s.title}</h3>
              <p className="text-sm text-muted-foreground mt-1">{s.desc}</p>
            </motion.div>
          );
        })}
      </div>
    </section>
  );
}
