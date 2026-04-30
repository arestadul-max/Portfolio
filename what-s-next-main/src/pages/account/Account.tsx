import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import PageShell from "@/components/foodly/layout/PageShell";
import { useAuth } from "@/context/AuthContext";
import { supabase } from "@/integrations/supabase/client";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { toast } from "sonner";
import { LogOut, User as UserIcon, Heart, ClipboardList } from "lucide-react";

export default function Account() {
  const { user, loading, signOut } = useAuth();
  const navigate = useNavigate();
  const [profile, setProfile] = useState<{ full_name: string; phone: string }>({ full_name: "", phone: "" });
  const [favCount, setFavCount] = useState(0);
  const [orderCount, setOrderCount] = useState(0);

  useEffect(() => {
    if (!loading && !user) navigate("/auth");
  }, [user, loading, navigate]);

  useEffect(() => {
    if (!user) return;
    supabase.from("profiles").select("full_name, phone").eq("id", user.id).maybeSingle()
      .then(({ data }) => data && setProfile({ full_name: data.full_name ?? "", phone: data.phone ?? "" }));
    supabase.from("favorites").select("id", { count: "exact", head: true }).eq("user_id", user.id)
      .then(({ count }) => setFavCount(count ?? 0));
    supabase.from("orders").select("id", { count: "exact", head: true }).eq("user_id", user.id)
      .then(({ count }) => setOrderCount(count ?? 0));
  }, [user]);

  const save = async () => {
    if (!user) return;
    const { error } = await supabase.from("profiles").update({
      full_name: profile.full_name.trim().slice(0, 80),
      phone: profile.phone.trim().slice(0, 30),
    }).eq("id", user.id);
    if (error) toast.error(error.message);
    else toast.success("Profile updated");
  };

  const handleSignOut = async () => {
    await signOut();
    toast.success("Signed out");
    navigate("/");
  };

  if (loading || !user) return <PageShell title="Account · foodly"><div className="container py-20 text-center text-muted-foreground">Loading…</div></PageShell>;

  return (
    <PageShell title="Account · foodly">
      <section className="container py-12 max-w-3xl">
        <div className="flex items-center gap-4">
          <div className="h-16 w-16 rounded-2xl bg-gradient-brand grid place-items-center text-primary-foreground text-2xl font-bold shadow-glow-pink">
            {(profile.full_name || user.email || "?").slice(0, 1).toUpperCase()}
          </div>
          <div>
            <h1 className="font-display font-bold text-3xl">{profile.full_name || "Welcome"}</h1>
            <p className="text-muted-foreground text-sm">{user.email}</p>
          </div>
        </div>

        <div className="mt-8 grid sm:grid-cols-3 gap-3">
          <Link to="/account/orders" className="glass rounded-2xl p-5 hover-lift"><ClipboardList className="h-5 w-5 text-primary mb-2" /><div className="font-display font-bold text-2xl">{orderCount}</div><div className="text-xs text-muted-foreground">Orders</div></Link>
          <Link to="/account/favorites" className="glass rounded-2xl p-5 hover-lift"><Heart className="h-5 w-5 text-primary mb-2" /><div className="font-display font-bold text-2xl">{favCount}</div><div className="text-xs text-muted-foreground">Favorites</div></Link>
          <button onClick={handleSignOut} className="glass rounded-2xl p-5 hover-lift text-left"><LogOut className="h-5 w-5 text-primary mb-2" /><div className="font-display font-bold text-base">Sign out</div><div className="text-xs text-muted-foreground">End session</div></button>
        </div>

        <div className="mt-8 glass rounded-2xl p-6">
          <h2 className="font-display font-bold text-xl flex items-center gap-2"><UserIcon className="h-5 w-5" />Profile</h2>
          <div className="mt-5 space-y-4">
            <div><Label>Full name</Label><Input value={profile.full_name} onChange={(e) => setProfile({ ...profile, full_name: e.target.value })} maxLength={80} className="mt-1.5 h-11 rounded-xl" /></div>
            <div><Label>Phone</Label><Input value={profile.phone} onChange={(e) => setProfile({ ...profile, phone: e.target.value })} maxLength={30} className="mt-1.5 h-11 rounded-xl" /></div>
            <Button onClick={save} className="h-11 rounded-xl bg-gradient-brand text-primary-foreground font-semibold shadow-glow-pink">Save changes</Button>
          </div>
        </div>
      </section>
    </PageShell>
  );
}