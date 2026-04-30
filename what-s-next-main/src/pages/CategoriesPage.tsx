import PageShell from "@/components/foodly/layout/PageShell";
import { motion } from "framer-motion";
import { Link } from "react-router-dom";
import { categories, categorySlug } from "@/data/menu";

export default function CategoriesPage() {
  return (
    <PageShell title="Browse categories · foodly" description="Pizza, burgers, sushi, healthy bowls and more.">
      <section className="container py-12">
        <div className="text-sm font-semibold text-primary uppercase tracking-wider">Browse</div>
        <h1 className="mt-2 font-display font-bold text-4xl md:text-5xl tracking-tight">All <span className="text-gradient-brand">categories</span></h1>
        <div className="mt-10 grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
          {categories.map((c, i) => (
            <motion.div key={c.name} initial={{ opacity: 0, y: 20 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true }} transition={{ delay: i * 0.05 }}>
              <Link to={`/category/${categorySlug(c.name)}`} className="group relative block aspect-[4/5] rounded-3xl overflow-hidden glass border border-border/50 p-4 hover-lift">
                <div className={`absolute inset-0 bg-gradient-to-br ${c.tint} opacity-60 group-hover:opacity-100 transition`} />
                <img src={c.img} alt={c.name} className="relative z-10 w-3/4 mx-auto mt-2 group-hover:scale-110 transition-transform duration-500" />
                <div className="absolute bottom-4 left-4 right-4 z-10">
                  <div className="text-xs text-muted-foreground">Discover</div>
                  <div className="font-display font-bold text-lg">{c.name} {c.emoji}</div>
                </div>
              </Link>
            </motion.div>
          ))}
        </div>
      </section>
    </PageShell>
  );
}