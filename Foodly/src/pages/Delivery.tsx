import PageShell from "@/components/foodly/layout/PageShell";
import RestaurantCard from "@/components/foodly/RestaurantCard";
import { restaurants } from "@/data/menu";
import { Bike } from "lucide-react";

export default function Delivery() {
  return (
    <PageShell title="Delivery · foodly" description="Hot food delivered to your door in under 30 minutes.">
      <section className="container py-12">
        <div className="inline-flex items-center gap-2 glass rounded-full px-4 py-1.5 text-xs font-semibold text-primary"><Bike className="h-3.5 w-3.5" /> Delivery in 22 min avg</div>
        <h1 className="mt-3 font-display font-bold text-4xl md:text-5xl">Delivered <span className="text-gradient-brand">hot & fast</span></h1>
        <p className="text-muted-foreground mt-2 max-w-xl">Track your rider in real-time and get free delivery on orders over $20.</p>
        <div className="mt-10 grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {restaurants.map((r, i) => <RestaurantCard key={r.id} r={r} i={i} />)}
        </div>
      </section>
    </PageShell>
  );
}