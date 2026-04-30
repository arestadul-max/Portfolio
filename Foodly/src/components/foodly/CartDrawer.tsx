import { AnimatePresence, motion } from "framer-motion";
import { X, Plus, Minus, Trash2, Tag, Sparkles } from "lucide-react";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useCart } from "@/context/CartContext";
import { toast } from "@/hooks/use-toast";

export default function CartDrawer() {
  const { items, open, setOpen, inc, dec, remove, total } = useCart();
  const navigate = useNavigate();
  const [coupon, setCoupon] = useState("");
  const [discount, setDiscount] = useState(0);

  const fee = items.length ? 1.99 : 0;
  const grand = Math.max(0, total + fee - discount);

  const apply = () => {
    if (coupon.toUpperCase() === "WELCOME50") {
      setDiscount(total * 0.5);
      toast({ title: "🎉 50% off applied!", description: "Welcome to foodly." });
    } else {
      toast({ title: "Invalid coupon", description: "Try WELCOME50." });
    }
  };

  const checkout = () => {
    setOpen(false);
    navigate("/checkout");
  };

  return (
    <AnimatePresence>
      {open && (
        <>
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={() => setOpen(false)}
            className="fixed inset-0 bg-foreground/40 backdrop-blur-sm z-50"
          />
          <motion.aside
            initial={{ x: "100%" }}
            animate={{ x: 0 }}
            exit={{ x: "100%" }}
            transition={{ type: "spring", damping: 28, stiffness: 240 }}
            className="fixed right-0 top-0 bottom-0 w-full sm:w-[440px] z-50 glass-strong border-l border-border/60 flex flex-col"
          >
            <div className="flex items-center justify-between p-5 border-b border-border/60">
              <div>
                <div className="text-xs text-muted-foreground uppercase tracking-wider">Your basket</div>
                <div className="font-display font-bold text-xl">
                  {items.length} item{items.length !== 1 ? "s" : ""}
                </div>
              </div>
              <button onClick={() => setOpen(false)} className="grid place-items-center h-10 w-10 rounded-xl glass hover:scale-105 transition" aria-label="Close">
                <X className="h-4 w-4" />
              </button>
            </div>

            <div className="flex-1 overflow-y-auto p-5 space-y-3">
              {items.length === 0 && (
                <div className="text-center py-20">
                  <div className="text-6xl mb-3">🍽️</div>
                  <div className="font-display font-bold text-lg">Your basket is empty</div>
                  <p className="text-sm text-muted-foreground mt-1">Add a dish to get started.</p>
                </div>
              )}
              <AnimatePresence>
                {items.map((i) => (
                  <motion.div
                    key={i.id}
                    layout
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0, x: 80 }}
                    className="flex items-center gap-3 glass rounded-2xl p-3"
                  >
                    <div className="relative h-16 w-16 rounded-xl bg-gradient-brand-soft grid place-items-center overflow-hidden">
                      <img src={i.img} alt={i.name} className="h-14 w-14 object-contain" />
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="font-semibold text-sm truncate">{i.name}</div>
                      <div className="text-xs text-muted-foreground">{i.restaurant}</div>
                      <div className="font-display font-bold text-sm mt-1">${(i.price * i.qty).toFixed(2)}</div>
                    </div>
                    <div className="flex items-center gap-1.5 glass rounded-full px-1 py-1">
                      <button onClick={() => dec(i.id)} className="h-7 w-7 rounded-full grid place-items-center hover:bg-primary/15 transition" aria-label="Decrease">
                        <Minus className="h-3 w-3" />
                      </button>
                      <span className="w-5 text-center text-sm font-bold">{i.qty}</span>
                      <button onClick={() => inc(i.id)} className="h-7 w-7 rounded-full grid place-items-center bg-gradient-brand text-primary-foreground" aria-label="Increase">
                        <Plus className="h-3 w-3" />
                      </button>
                    </div>
                    <button onClick={() => remove(i.id)} className="text-muted-foreground hover:text-destructive transition" aria-label="Remove">
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </motion.div>
                ))}
              </AnimatePresence>
            </div>

            {items.length > 0 && (
              <div className="border-t border-border/60 p-5 space-y-4">
                <div className="flex items-center gap-2">
                  <div className="flex-1 flex items-center gap-2 glass rounded-xl px-3 py-2.5">
                    <Tag className="h-4 w-4 text-primary" />
                    <input
                      value={coupon}
                      onChange={(e) => setCoupon(e.target.value)}
                      placeholder="Coupon code"
                      className="flex-1 bg-transparent outline-none text-sm"
                    />
                  </div>
                  <button onClick={apply} className="h-11 px-4 rounded-xl bg-foreground text-background text-sm font-semibold">
                    Apply
                  </button>
                </div>

                <div className="space-y-1.5 text-sm">
                  <div className="flex justify-between text-muted-foreground">
                    <span>Subtotal</span><span>${total.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between text-muted-foreground">
                    <span>Delivery</span><span>${fee.toFixed(2)}</span>
                  </div>
                  {discount > 0 && (
                    <div className="flex justify-between text-primary font-medium">
                      <span>Discount</span><span>-${discount.toFixed(2)}</span>
                    </div>
                  )}
                  <div className="flex justify-between font-display font-bold text-lg pt-2 border-t border-border/60">
                    <span>Total</span>
                    <motion.span key={grand} initial={{ scale: 1.15 }} animate={{ scale: 1 }} className="text-gradient-brand">
                      ${grand.toFixed(2)}
                    </motion.span>
                  </div>
                </div>

                <button
                  onClick={checkout}
                  className="relative w-full h-14 rounded-2xl bg-gradient-brand text-primary-foreground font-display font-bold shadow-glow-pink overflow-hidden group"
                >
                  <span className="absolute inset-0 bg-white/25 -translate-x-full group-hover:translate-x-0 transition-transform duration-700" />
                  <span className="relative flex items-center justify-center gap-2">
                    <Sparkles className="h-4 w-4" /> Checkout · ${grand.toFixed(2)}
                  </span>
                </button>
              </div>
            )}
          </motion.aside>
        </>
      )}
    </AnimatePresence>
  );
}
