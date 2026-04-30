import PageShell from "@/components/foodly/layout/PageShell";
import RestaurantCard from "@/components/foodly/RestaurantCard";
import { restaurants } from "@/data/menu";
import { ShoppingBag } from "lucide-react";

export default function Pickup() {
  return (
    <PageShell title="Pickup · foodly" description="Skip the wait — order ahead and pick up at the counter.">
      <section className="container py-12">
        <div className="inline-flex items-center gap-2 glass rounded-full px-4 py-1.5 text-xs font-semibold text-secondary"><ShoppingBag className="h-3.5 w-3.5" /> Skip the line</div>
        <h1 className="mt-3 font-display font-bold text-4xl md:text-5xl">Order ahead. <span className="text-gradient-brand">Pick up.</span></h1>
        <p className="text-muted-foreground mt-2 max-w-xl">Save on delivery and grab your food on the go.</p>
        <div className="mt-10 grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {restaurants.map((r, i) => <RestaurantCard key={r.id} r={r} i={i} />)}
        </div>
      </section>
    </PageShell>
  );
}