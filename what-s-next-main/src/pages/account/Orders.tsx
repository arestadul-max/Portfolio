import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import PageShell from "@/components/foodly/layout/PageShell";
import { useAuth } from "@/context/AuthContext";
import { supabase } from "@/integrations/supabase/client";

type Order = { id: string; status: string; fulfillment: string; total: number; created_at: string; items: any };

export default function Orders() {
  const { user, loading } = useAuth();
  const [orders, setOrders] = useState<Order[]>([]);
  const navigate = useNavigate();

  useEffect(() => { if (!loading && !user) navigate("/auth"); }, [user, loading, navigate]);

  useEffect(() => {
    if (!user) return;
    supabase.from("orders").select("*").eq("user_id", user.id).order("created_at", { ascending: false })
      .then(({ data }) => setOrders((data as Order[]) ?? []));
  }, [user]);

  return (
    <PageShell title="Your orders · foodly">
      <section className="container py-12 max-w-3xl">
        <Link to="/account" className="text-sm text-muted-foreground hover:text-foreground">← Account</Link>
        <h1 className="mt-3 font-display font-bold text-4xl">Your orders</h1>
        {orders.length === 0 ? (
          <p className="text-muted-foreground mt-6">No orders yet. <Link to="/restaurants" className="text-primary">Order something delicious →</Link></p>
        ) : (
          <ul className="mt-8 space-y-3">
            {orders.map((o) => (
              <li key={o.id} className="glass rounded-2xl p-5 flex items-center justify-between">
                <div>
                  <div className="font-display font-bold">${Number(o.total).toFixed(2)} · <span className="capitalize text-muted-foreground font-normal text-sm">{o.fulfillment}</span></div>
                  <div className="text-xs text-muted-foreground mt-0.5">{Array.isArray(o.items) ? o.items.length : 0} items · {new Date(o.created_at).toLocaleString()}</div>
                </div>
                <span className="px-3 py-1 rounded-full bg-primary/15 text-primary text-xs font-semibold capitalize">{o.status}</span>
              </li>
            ))}
          </ul>
        )}
      </section>
    </PageShell>
  );
}