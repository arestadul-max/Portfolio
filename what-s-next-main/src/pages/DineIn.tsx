import PageShell from "@/components/foodly/layout/PageShell";
import RestaurantCard from "@/components/foodly/RestaurantCard";
import { restaurants } from "@/data/menu";
import { Utensils } from "lucide-react";

export default function DineIn() {
  return (
    <PageShell title="Dine in · foodly" description="Reserve a table and enjoy a chef-prepared meal.">
      <section className="container py-12">
        <div className="inline-flex items-center gap-2 glass rounded-full px-4 py-1.5 text-xs font-semibold text-primary"><Utensils className="h-3.5 w-3.5" /> Book a table</div>
        <h1 className="mt-3 font-display font-bold text-4xl md:text-5xl">Dine <span className="text-gradient-brand">in style</span></h1>
        <p className="text-muted-foreground mt-2 max-w-xl">Reserve a table at top spots near you.</p>
        <div className="mt-10 grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {restaurants.map((r, i) => <RestaurantCard key={r.id} r={r} i={i} />)}
        </div>
      </section>
    </PageShell>
  );
}