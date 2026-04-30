import Navbar from "@/components/foodly/Navbar";
import Footer from "@/components/foodly/Footer";
import CartDrawer from "@/components/foodly/CartDrawer";
import { ReactNode, useEffect } from "react";

export default function PageShell({ children, title, description }: { children: ReactNode; title?: string; description?: string }) {
  useEffect(() => {
    if (title) document.title = title;
    if (description) {
      let m = document.querySelector('meta[name="description"]');
      if (!m) {
        m = document.createElement("meta");
        m.setAttribute("name", "description");
        document.head.appendChild(m);
      }
      m.setAttribute("content", description);
    }
  }, [title, description]);
  return (
    <main className="min-h-screen bg-background text-foreground overflow-x-hidden">
      <Navbar />
      <div className="pt-28">{children}</div>
      <Footer />
      <CartDrawer />
    </main>
  );
}