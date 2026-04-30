import { useSearchParams } from "react-router-dom";
import PageShell from "@/components/foodly/layout/PageShell";
import RestaurantCard from "@/components/foodly/RestaurantCard";
import DishCard from "@/components/foodly/DishCard";
import { restaurants, allDishes } from "@/data/menu";
import { Search as SearchIcon } from "lucide-react";
import { useState, useEffect } from "react";

export default function SearchPage() {
  const [params, setParams] = useSearchParams();
  const q = params.get("q") ?? "";
  const [val, setVal] = useState(q);
  useEffect(() => setVal(q), [q]);

  const term = q.trim().toLowerCase();
  const matchedRestaurants = term ? restaurants.filter((r) => r.name.toLowerCase().includes(term) || r.cuisine.toLowerCase().includes(term)) : [];
  const matchedDishes = term ? allDishes.filter((d) => d.name.toLowerCase().includes(term) || d.restaurant.toLowerCase().includes(term)) : [];

  return (
    <PageShell title={q ? `Search: ${q} · foodly` : "Search · foodly"}>
      <section className="container py-12">
        <h1 className="font-display font-bold text-4xl">Search</h1>
        <form onSubmit={(e) => { e.preventDefault(); setParams({ q: val }); }} className="mt-6 glass-strong rounded-2xl p-2 flex items-center gap-2 ring-glow border border-border/60">
          <SearchIcon className="h-5 w-5 ml-3 text-muted-foreground" />
          <input value={val} onChange={(e) => setVal(e.target.value)} placeholder="Search restaurants, dishes…" className="flex-1 bg-transparent outline-none py-3 text-base" autoFocus />
          <button className="h-11 px-5 rounded-xl bg-gradient-brand text-primary-foreground font-semibold shadow-glow-pink">Search</button>
        </form>

        {term && (
          <>
            <h2 className="mt-12 font-display font-bold text-2xl">Restaurants ({matchedRestaurants.length})</h2>
            {matchedRestaurants.length > 0 ? (
              <div className="mt-6 grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
                {matchedRestaurants.map((r, i) => <RestaurantCard key={r.id} r={r} i={i} />)}
              </div>
            ) : <p className="text-muted-foreground mt-3">No restaurants match "{q}"</p>}

            <h2 className="mt-12 font-display font-bold text-2xl">Dishes ({matchedDishes.length})</h2>
            {matchedDishes.length > 0 ? (
              <div className="mt-6 grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
                {matchedDishes.map((d, i) => <DishCard key={d.id} dish={d} i={i} />)}
              </div>
            ) : <p className="text-muted-foreground mt-3">No dishes match "{q}"</p>}
          </>
        )}
      </section>
    </PageShell>
  );
}