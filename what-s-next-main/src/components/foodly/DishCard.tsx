import { motion } from "framer-motion";
import { Star, Clock, Plus } from "lucide-react";
import type { Dish } from "@/data/menu";
import { useCart } from "@/context/CartContext";

export default function DishCard({ dish, i = 0 }: { dish: Dish; i?: number }) {
  const { add } = useCart();
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, margin: "-50px" }}
      transition={{ duration: 0.4, delay: i * 0.05 }}
      className="group relative rounded-3xl glass border border-border/50 p-5 pt-20 overflow-hidden hover-lift"
    >
      <div className="absolute -top-6 right-2 h-32 w-32">
        <div className="absolute inset-0 rounded-full bg-gradient-brand opacity-25 blur-2xl group-hover:opacity-50 transition" />
        <img src={dish.img} alt={dish.name} loading="lazy" className="relative h-32 w-32 object-contain drop-shadow-[0_15px_30px_hsl(var(--primary)/0.4)] transition-transform duration-500 group-hover:-rotate-12 group-hover:scale-110" />
      </div>
      {dish.tag && <span className="absolute top-3 left-3 px-2 py-1 rounded-full bg-foreground text-background text-[10px] font-bold">{dish.tag}</span>}
      <div className="relative max-w-[60%]">
        <div className="text-[11px] text-muted-foreground">{dish.restaurant}</div>
        <h3 className="mt-1 font-display font-bold text-base leading-tight">{dish.name}</h3>
        <div className="mt-2 flex items-center gap-2 text-[11px] text-muted-foreground">
          <span className="inline-flex items-center gap-0.5"><Star className="h-3 w-3 fill-secondary text-secondary" />{dish.rating}</span>
          <span className="inline-flex items-center gap-0.5"><Clock className="h-3 w-3" />{dish.eta}</span>
        </div>
      </div>
      <div className="relative mt-5 flex items-end justify-between">
        <div className="font-display font-bold text-xl">${dish.price.toFixed(2)}</div>
        <button onClick={() => add(dish)} className="h-10 px-4 rounded-xl bg-gradient-brand text-primary-foreground font-semibold shadow-glow-pink hover:scale-105 transition flex items-center gap-1.5">
          <Plus className="h-4 w-4" />Add
        </button>
      </div>
    </motion.div>
  );
}