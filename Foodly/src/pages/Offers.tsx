import PageShell from "@/components/foodly/layout/PageShell";
import { motion } from "framer-motion";
import { Tag, Copy } from "lucide-react";
import { toast } from "sonner";

const offers = [
  { code: "WELCOME50", title: "50% off your first order", desc: "Up to $15 off — new customers only.", color: "from-primary/30 to-secondary/20" },
  { code: "FREESHIP", title: "Free delivery this weekend", desc: "On orders over $20. No code needed.", color: "from-secondary/30 to-primary/20" },
  { code: "PIZZA20", title: "20% off all pizza", desc: "Wood-fired Margherita, anyone?", color: "from-primary/25 to-primary-glow/20" },
  { code: "SUSHI15", title: "15% off sushi night", desc: "Every Thursday. Auto-applied.", color: "from-secondary/25 to-secondary-glow/20" },
];

export default function Offers() {
  return (
    <PageShell title="Offers & promos · foodly" description="Hot deals and promo codes for hungry humans.">
      <section className="container py-12">
        <div className="inline-flex items-center gap-2 glass rounded-full px-4 py-1.5 text-xs font-semibold text-primary"><Tag className="h-3.5 w-3.5" /> Limited time</div>
        <h1 className="mt-3 font-display font-bold text-4xl md:text-5xl">Today's <span className="text-gradient-brand">hot offers</span></h1>
        <div className="mt-10 grid sm:grid-cols-2 lg:grid-cols-2 gap-6">
          {offers.map((o, i) => (
            <motion.div key={o.code} initial={{ opacity: 0, y: 20 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }} transition={{ delay: i * 0.08 }}
              className="relative rounded-3xl glass border border-border/50 p-6 overflow-hidden hover-lift">
              <div className={`absolute inset-0 bg-gradient-to-br ${o.color} opacity-50`} />
              <div className="relative">
                <h3 className="font-display font-bold text-2xl">{o.title}</h3>
                <p className="text-muted-foreground mt-2">{o.desc}</p>
                <div className="mt-5 flex items-center gap-3">
                  <code className="px-4 py-2 rounded-xl bg-foreground text-background font-mono font-bold">{o.code}</code>
                  <button onClick={() => { navigator.clipboard.writeText(o.code); toast.success("Code copied!"); }} className="h-10 px-4 rounded-xl glass border border-border/60 inline-flex items-center gap-1.5 text-sm font-semibold hover:scale-105 transition">
                    <Copy className="h-4 w-4" />Copy
                  </button>
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      </section>
    </PageShell>
  );
}