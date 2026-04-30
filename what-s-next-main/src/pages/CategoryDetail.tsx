import { useParams, Link } from "react-router-dom";
import PageShell from "@/components/foodly/layout/PageShell";
import RestaurantCard from "@/components/foodly/RestaurantCard";
import { restaurants, restaurantsByCategory, categories, categorySlug } from "@/data/menu";

export default function CategoryDetail() {
  const { slug = "" } = useParams();
  const cat = categories.find((c) => categorySlug(c.name) === slug);
  const ids = restaurantsByCategory[slug] ?? [];
  const list = restaurants.filter((r) => ids.includes(r.id));

  return (
    <PageShell title={`${cat?.name ?? "Category"} · foodly`}>
      <section className="container py-12">
        <Link to="/categories" className="text-sm text-muted-foreground hover:text-foreground">← All categories</Link>
        <h1 className="mt-3 font-display font-bold text-4xl md:text-5xl">
          {cat?.emoji} {cat?.name ?? "Category"}
        </h1>
        <p className="text-muted-foreground mt-2">{list.length} restaurant{list.length === 1 ? "" : "s"} found</p>
        {list.length > 0 ? (
          <div className="mt-10 grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {list.map((r, i) => <RestaurantCard key={r.id} r={r} i={i} />)}
          </div>
        ) : (
          <p className="mt-10 text-muted-foreground">No restaurants in this category yet.</p>
        )}
      </section>
    </PageShell>
  );
}