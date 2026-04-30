import { motion } from "framer-motion";
import { Link } from "react-router-dom";
import { categories, categorySlug } from "@/data/menu";

export default function Categories() {
  return (
    <section className="container py-20 md:py-28">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true, margin: "-100px" }}
        transition={{ duration: 0.6 }}
        className="flex items-end justify-between gap-6 mb-10"
      >
        <div>
          <div className="text-sm font-semibold text-primary uppercase tracking-wider">Browse by craving</div>
          <h2 className="mt-2 font-display font-bold text-4xl md:text-5xl tracking-tight">
            What are you in the <span className="text-gradient-brand">mood</span> for?
          </h2>
        </div>
        <Link to="/categories" className="hidden md:inline text-sm font-medium text-muted-foreground hover:text-foreground">
          See all categories →
        </Link>
      </motion.div>

      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-7 gap-4">
        {categories.map((c, i) => (
          <motion.div
            key={c.name}
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-50px" }}
            transition={{ duration: 0.5, delay: i * 0.06, ease: "easeOut" }}
            whileHover={{ y: -8 }}
          >
            <Link to={`/category/${categorySlug(c.name)}`} className="group relative block aspect-[4/5] rounded-3xl overflow-hidden glass border border-border/50 text-left p-4 hover-lift">
            <div className={`absolute inset-0 bg-gradient-to-br ${c.tint} opacity-60 group-hover:opacity-100 transition-opacity`} />
            <div className="absolute -bottom-6 -right-6 h-32 w-32 rounded-full bg-gradient-brand opacity-20 blur-2xl group-hover:opacity-50 transition" />
            <img
              src={c.img}
              alt={c.name}
              loading="lazy"
              width={160}
              height={160}
              className="relative z-10 w-3/4 mx-auto mt-2 drop-shadow-[0_18px_30px_hsl(var(--primary)/0.35)] transition-transform duration-500 group-hover:scale-110 group-hover:-rotate-6"
            />
            <div className="absolute bottom-4 left-4 right-4 z-10">
              <div className="text-xs text-muted-foreground">Discover</div>
              <div className="font-display font-bold text-lg flex items-center gap-1.5">
                {c.name} <span className="text-base">{c.emoji}</span>
              </div>
            </div>
            </Link>
          </motion.div>
        ))}
      </div>
    </section>
  );
}
