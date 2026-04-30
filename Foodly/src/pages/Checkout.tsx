import PageShell from "@/components/foodly/layout/PageShell";
import { useCart } from "@/context/CartContext";
import { useAuth } from "@/context/AuthContext";
import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { supabase } from "@/integrations/supabase/client";
import { toast } from "sonner";
import { z } from "zod";

const schema = z.object({
  address: z.string().trim().min(5, "Address required").max(200),
  fulfillment: z.enum(["delivery", "pickup"]),
});

export default function Checkout() {
  const { items, total, clear } = useCart();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [address, setAddress] = useState("");
  const [fulfillment, setFulfillment] = useState<"delivery" | "pickup">("delivery");
  const [paymentMethod, setPaymentMethod] = useState<"bkash" | "nagad" | "rocket" | "cash">("bkash");
  const [walletNumber, setWalletNumber] = useState("");
  const [transactionId, setTransactionId] = useState("");
  const [loading, setLoading] = useState(false);

  const fee = fulfillment === "delivery" ? (total >= 20 ? 0 : 2.99) : 0;
  const grand = total + fee;

  const placeOrder = async () => {
    if (!user) { navigate("/auth"); return; }
    if (items.length === 0) return toast.error("Your cart is empty");
    try {
      const v = schema.parse({ address, fulfillment });
      if (paymentMethod !== "cash" && (!walletNumber || !transactionId)) {
        return toast.error("Enter wallet number and transaction ID to proceed");
      }

      setLoading(true);
      if (paymentMethod !== "cash") {
        await new Promise((resolve) => setTimeout(resolve, 1200));
        toast.success(`${paymentMethod.toUpperCase()} payment confirmed`);
      }

      const { data, error } = await supabase.from("orders").insert({
        user_id: user.id,
        status: paymentMethod === "cash" ? "placed" : "paid",
        fulfillment: v.fulfillment,
        items: items.map((i) => ({ id: i.id, name: i.name, qty: i.qty, price: i.price })),
        subtotal: total,
        total: grand,
        address: v.address,
        payment_method: paymentMethod,
        payment_reference: paymentMethod === "cash" ? null : transactionId,
      }).select().single();
      if (error) throw error;
      clear();
      toast.success("Order placed! 🎉");
      navigate(`/track?id=${data.id}`);
    } catch (e: any) {
      toast.error(e?.errors?.[0]?.message || e?.message || "Failed to place order");
    } finally {
      setLoading(false);
    }
  };

  return (
    <PageShell title="Checkout · foodly">
      <section className="container py-12 grid lg:grid-cols-[1fr_400px] gap-8">
        <div>
          <h1 className="font-display font-bold text-4xl">Checkout</h1>

          <div className="mt-8 glass rounded-2xl p-6">
            <h2 className="font-display font-bold text-xl mb-4">Fulfillment</h2>
            <div className="grid grid-cols-2 gap-3">
              {(["delivery", "pickup"] as const).map((f) => (
                <button key={f} onClick={() => setFulfillment(f)} className={`h-14 rounded-xl border font-semibold capitalize transition ${fulfillment === f ? "bg-gradient-brand text-primary-foreground border-transparent shadow-glow-pink" : "border-border hover:bg-muted"}`}>{f}</button>
              ))}
            </div>
          </div>

          <div className="mt-6 glass rounded-2xl p-6">
            <h2 className="font-display font-bold text-xl mb-4">{fulfillment === "delivery" ? "Delivery address" : "Pickup location"}</h2>
            <Label>{fulfillment === "delivery" ? "Where should we deliver?" : "Which store?"}</Label>
            <Input value={address} onChange={(e) => setAddress(e.target.value)} maxLength={200} placeholder={fulfillment === "delivery" ? "123 Main St, Apt 4B" : "Downtown branch"} className="mt-2 h-12 rounded-xl" />
          </div>

          <div className="mt-6 glass rounded-2xl p-6">
            <h2 className="font-display font-bold text-xl mb-4">Payment gateway</h2>
            <p className="text-sm text-muted-foreground mb-4">Choose a Bangladesh mobile wallet or cash on delivery.</p>
            <div className="grid gap-3">
              {(["bkash", "nagad", "rocket", "cash"] as const).map((method) => (
                <button
                  key={method}
                  type="button"
                  onClick={() => setPaymentMethod(method)}
                  className={`h-14 rounded-xl border text-left px-4 font-semibold capitalize transition ${paymentMethod === method ? "bg-gradient-brand text-primary-foreground border-transparent shadow-glow-pink" : "border-border hover:bg-muted"}`}
                >
                  {method === "cash" ? "Cash on delivery" : method.toUpperCase()}
                </button>
              ))}
            </div>
            {paymentMethod !== "cash" && (
              <div className="mt-4 space-y-3">
                <Label>Wallet number</Label>
                <Input value={walletNumber} onChange={(e) => setWalletNumber(e.target.value)} placeholder="01XXXXXXXXX" maxLength={14} className="h-12 rounded-xl" />
                <Label>Transaction ID</Label>
                <Input value={transactionId} onChange={(e) => setTransactionId(e.target.value)} placeholder="TXN123456789" maxLength={50} className="h-12 rounded-xl" />
                <p className="text-xs text-muted-foreground">Enter the transaction ID from your selected wallet app to confirm the payment.</p>
              </div>
            )}
          </div>
        </div>

        <aside className="glass-strong rounded-2xl p-6 h-fit sticky top-28">
          <h2 className="font-display font-bold text-xl">Order summary</h2>
          {items.length === 0 ? (
            <p className="text-muted-foreground mt-4">Your cart is empty. <Link to="/restaurants" className="text-primary">Browse restaurants</Link></p>
          ) : (
            <>
              <ul className="mt-4 divide-y divide-border/50">
                {items.map((i) => (
                  <li key={i.id} className="py-3 flex justify-between text-sm">
                    <span>{i.qty}× {i.name}</span>
                    <span className="font-semibold">${(i.price * i.qty).toFixed(2)}</span>
                  </li>
                ))}
              </ul>
              <div className="mt-4 space-y-1.5 text-sm">
                <div className="flex justify-between text-muted-foreground"><span>Subtotal</span><span>${total.toFixed(2)}</span></div>
                <div className="flex justify-between text-muted-foreground"><span>{fulfillment === "delivery" ? "Delivery fee" : "Pickup"}</span><span>{fee === 0 ? "Free" : `$${fee.toFixed(2)}`}</span></div>
                <div className="flex justify-between font-bold text-lg pt-2 border-t border-border/50"><span>Total</span><span>${grand.toFixed(2)}</span></div>
              </div>
              <Button onClick={placeOrder} disabled={loading} className="w-full mt-5 h-12 rounded-xl bg-gradient-brand text-primary-foreground font-semibold shadow-glow-pink">
                {loading ? "Placing…" : user ? "Place order" : "Sign in to order"}
              </Button>
            </>
          )}
        </aside>
      </section>
    </PageShell>
  );
}