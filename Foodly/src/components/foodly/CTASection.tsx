import { motion } from "framer-motion";
import { Apple, Smartphone, Gift } from "lucide-react";

export default function CTASection() {
  return (
    <section className="container py-20 md:py-28">
      <motion.div
        initial={{ opacity: 0, y: 30 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true }}
        className="relative overflow-hidden rounded-[2.5rem] p-10 md:p-16 bg-gradient-brand"
      >
        <div className="absolute -top-20 -right-20 w-96 h-96 bg-white/15 blur-3xl rounded-full animate-blob" />
        <div className="absolute -bottom-20 -left-20 w-96 h-96 bg-foreground/20 blur-3xl rounded-full animate-blob" />

        <div className="relative grid lg:grid-cols-[1.3fr_1fr] gap-10 items-center text-primary-foreground">
          <div>
            <div className="inline-flex items-center gap-2 bg-white/15 backdrop-blur rounded-full px-3 py-1 text-xs font-semibold">
              <Gift className="h-3.5 w-3.5" /> Limited time · $10 off your first order
            </div>
            <h2 className="mt-5 font-display font-extrabold text-4xl md:text-6xl tracking-tight leading-[1.05]">
              Get foodly in your pocket.
            </h2>
            <p className="mt-4 text-base md:text-lg text-primary-foreground/85 max-w-md">
              Faster checkout, exclusive in-app offers, and real-time delivery tracking. Available on iOS and Android.
            </p>
            <div className="mt-7 flex flex-wrap gap-3">
              <button className="h-12 px-5 rounded-xl bg-foreground text-background font-semibold flex items-center gap-2 hover:scale-[1.03] transition">
                <Apple className="h-5 w-5" /> App Store
              </button>
              <button className="h-12 px-5 rounded-xl bg-background text-foreground font-semibold flex items-center gap-2 hover:scale-[1.03] transition">
                <Smartphone className="h-5 w-5" /> Google Play
              </button>
            </div>
          </div>

          {/* Phone mock */}
          <div className="relative mx-auto">
            <motion.div
              animate={{ y: [0, -10, 0] }}
              transition={{ duration: 5, repeat: Infinity }}
              className="relative w-64 md:w-72 aspect-[9/19] rounded-[3rem] bg-foreground border-[10px] border-foreground shadow-2xl overflow-hidden"
            >
              <div className="absolute top-2 left-1/2 -translate-x-1/2 h-5 w-24 rounded-full bg-foreground z-10" />
              <div className="h-full w-full bg-background p-4 flex flex-col gap-3">
                <div className="text-xs text-muted-foreground mt-6">Good evening</div>
                <div className="font-display font-bold text-lg">What's for dinner?</div>
                <div className="glass rounded-xl p-3 text-xs">🔍 Search restaurants…</div>
                <div className="grid grid-cols-3 gap-2 mt-1">
                  {["🍕", "🍔", "🍣"].map((e) => (
                    <div key={e} className="aspect-square rounded-xl bg-gradient-brand-soft grid place-items-center text-2xl">
                      {e}
                    </div>
                  ))}
                </div>
                <div className="rounded-xl bg-gradient-brand p-3 text-primary-foreground mt-1">
                  <div className="text-[10px] uppercase tracking-wider opacity-80">On the way</div>
                  <div className="font-bold text-sm">Truffle Burger · 9 min</div>
                </div>
                <div className="rounded-xl glass p-3 mt-auto text-xs">
                  <div className="text-muted-foreground text-[10px] uppercase">Loyalty</div>
                  <div className="font-bold">2,450 pts · Gold member 🏆</div>
                </div>
              </div>
            </motion.div>
          </div>
        </div>
      </motion.div>
    </section>
  );
}
