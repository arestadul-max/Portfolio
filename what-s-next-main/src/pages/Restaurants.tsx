import PageShell from "@/components/foodly/layout/PageShell";
import RestaurantCard from "@/components/foodly/RestaurantCard";
import { restaurants } from "@/data/menu";

export default function RestaurantsPage() {
  return (
    <PageShell title="Restaurants near you · foodly" description="Browse 12,000+ restaurants delivering to your door.">
      <section className="container py-12">
        <div className="text-sm font-semibold text-secondary uppercase tracking-wider">All restaurants</div>
        <h1 className="mt-2 font-display font-bold text-4xl md:text-5xl tracking-tight">Find your <span className="text-gradient-brand">next favorite</span></h1>
        <div className="mt-10 grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {restaurants.map((r, i) => <RestaurantCard key={r.id} r={r} i={i} />)}
        </div>
      </section>
    </PageShell>
  );
}