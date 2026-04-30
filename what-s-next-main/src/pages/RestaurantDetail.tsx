import { useParams, Link } from "react-router-dom";
import { motion } from "framer-motion";
import { Star, Clock, Heart, ArrowLeft } from "lucide-react";
import { useEffect, useState } from "react";
import PageShell from "@/components/foodly/layout/PageShell";
import DishCard from "@/components/foodly/DishCard";
import { restaurants, restaurantMenus } from "@/data/menu";
import { useAuth } from "@/context/AuthContext";
import { supabase } from "@/integrations/supabase/client";
import { toast } from "sonner";

export default function RestaurantDetail() {
  const { id } = useParams();
  const r = restaurants.find((x) => x.id === id);
  const menu = id ? restaurantMenus[id] : undefined;
  const { user } = useAuth();
  const [fav, setFav] = useState(false);
  const [activeCat, setActiveCat] = useState<string | null>(menu?.[0]?.category ?? null);

  useEffect(() => {
    if (!user || !id) return;
    supabase.from("favorites").select("id").eq("user_id", user.id).eq("restaurant_id", id).maybeSingle()
      .then(({ data }) => setFav(!!data));
  }, [user, id]);

  const toggleFav = async () => {
    if (!user) return toast.error("Sign in to save favorites");
    if (!id) return;
    if (fav) {
      await supabase.from("favorites").delete().eq("user_id", user.id).eq("restaurant_id", id);
      setFav(false); toast.success("Removed from favorites");
    } else {
      await supabase.from("favorites").insert({ user_id: user.id, restaurant_id: id });
      setFav(true); toast.success("Saved to favorites");
    }
  };

  if (!r) {
    return (
      <PageShell title="Restaurant not found · foodly">
        <div className="container py-20 text-center">
          <h1 className="font-display font-bold text-3xl">Restaurant not found</h1>
          <Link to="/restaurants" className="text-primary mt-4 inline-block">← Back to restaurants</Link>
        </div>
      </PageShell>
    );
  }

  return (
    <PageShell title={`${r.name} · foodly`} description={`Order from ${r.name} — ${r.cuisine}`}>
      <section className="relative">
        <div className="container">
          <Link to="/restaurants" className="inline-flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground mb-4">
            <ArrowLeft className="h-4 w-4" /> All restaurants
          </Link>
        </div>
        <div className="relative h-72 md:h-96 overflow-hidden">
          <img src={r.cover} alt={r.name} className="absolute inset-0 w-full h-full object-cover" />
          <div className="absolute inset-0 bg-gradient-to-t from-background via-background/40 to-transparent" />
        </div>
        <div className="container relative -mt-24 z-10">
          <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="glass-strong rounded-3xl p-6 md:p-8 shadow-soft">
            <div className="flex items-start justify-between gap-4 flex-wrap">
              <div>
                <h1 className="font-display font-bold text-3xl md:text-4xl">{r.name}</h1>
                <p className="text-muted-foreground mt-1">{r.cuisine}</p>
                <div className="mt-3 flex items-center gap-4 text-sm">
                  <span className="inline-flex items-center gap-1"><Star className="h-4 w-4 fill-secondary text-secondary" />{r.rating}</span>
                  <span className="inline-flex items-center gap-1"><Clock className="h-4 w-4" />{r.eta}</span>
                  <span className="text-primary font-semibold">{r.fee}</span>
                </div>
              </div>
              <button onClick={toggleFav} className="h-12 px-4 rounded-xl glass border border-border/60 flex items-center gap-2 hover:scale-105 transition">
                <Heart className={`h-4 w-4 ${fav ? "fill-primary text-primary" : ""}`} />
                {fav ? "Saved" : "Save"}
              </button>
            </div>
          </motion.div>
        </div>
      </section>

      <section className="container py-12">
        {menu && menu.length > 0 ? (
          <>
            <div className="flex flex-wrap gap-2 mb-8 sticky top-24 z-20 glass-strong rounded-2xl p-2">
              {menu.map((c) => (
                <button key={c.category} onClick={() => setActiveCat(c.category)} className={`px-4 py-2 rounded-xl text-sm font-semibold transition ${activeCat === c.category ? "bg-gradient-brand text-primary-foreground shadow-glow-pink" : "hover:bg-muted"}`}>
                  {c.category}
                </button>
              ))}
            </div>
            {menu.map((c) => (
              <div key={c.category} id={c.category} className="mb-12">
                <h2 className="font-display font-bold text-2xl mb-6">{c.category}</h2>
                <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
                  {c.items.map((d, i) => <DishCard key={d.id} dish={d} i={i} />)}
                </div>
              </div>
            ))}
          </>
        ) : (
          <p className="text-center text-muted-foreground py-12">Menu coming soon.</p>
        )}
      </section>
    </PageShell>
  );
}