import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import PageShell from "@/components/foodly/layout/PageShell";
import RestaurantCard from "@/components/foodly/RestaurantCard";
import { useAuth } from "@/context/AuthContext";
import { supabase } from "@/integrations/supabase/client";
import { restaurants } from "@/data/menu";

export default function Favorites() {
  const { user, loading } = useAuth();
  const [ids, setIds] = useState<string[]>([]);
  const navigate = useNavigate();

  useEffect(() => { if (!loading && !user) navigate("/auth"); }, [user, loading, navigate]);

  useEffect(() => {
    if (!user) return;
    supabase.from("favorites").select("restaurant_id").eq("user_id", user.id)
      .then(({ data }) => setIds((data ?? []).map((d: any) => d.restaurant_id)));
  }, [user]);

  const list = restaurants.filter((r) => ids.includes(r.id));

  return (
    <PageShell title="Favorites · foodly">
      <section className="container py-12">
        <Link to="/account" className="text-sm text-muted-foreground hover:text-foreground">← Account</Link>
        <h1 className="mt-3 font-display font-bold text-4xl">Your favorites</h1>
        {list.length === 0 ? (
          <p className="text-muted-foreground mt-6">No favorites yet. <Link to="/restaurants" className="text-primary">Find one to love →</Link></p>
        ) : (
          <div className="mt-10 grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {list.map((r, i) => <RestaurantCard key={r.id} r={r} i={i} />)}
          </div>
        )}
      </section>
    </PageShell>
  );
}