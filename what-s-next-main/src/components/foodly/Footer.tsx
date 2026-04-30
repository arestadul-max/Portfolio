export default function Footer() {
  return (
    <footer className="border-t border-border/60 mt-10">
      <div className="container py-14 grid md:grid-cols-4 gap-10">
        <div>
          <div className="flex items-center gap-2">
            <div className="h-9 w-9 rounded-xl bg-gradient-brand grid place-items-center shadow-glow-pink">
              <span className="text-lg">🍱</span>
            </div>
            <span className="font-display font-bold text-xl">food<span className="text-gradient-brand">ly</span></span>
          </div>
          <p className="text-sm text-muted-foreground mt-4 max-w-xs">
            Premium food delivery, reimagined. Hot food, cold drinks, lightning fast.
          </p>
        </div>
        {[
          { t: "Company", l: ["About", "Careers", "Blog", "Press"] },
          { t: "For partners", l: ["Become a partner", "Restaurant sign up", "Rider portal", "Brand kit"] },
          { t: "Support", l: ["Help center", "Track order", "Privacy", "Terms"] },
        ].map((c) => (
          <div key={c.t}>
            <div className="font-display font-semibold mb-3">{c.t}</div>
            <ul className="space-y-2 text-sm text-muted-foreground">
              {c.l.map((i) => (
                <li key={i}>
                  <a href="#" className="hover:text-foreground transition">{i}</a>
                </li>
              ))}
            </ul>
          </div>
        ))}
      </div>
      <div className="border-t border-border/60 py-5 text-center text-xs text-muted-foreground">
        © {new Date().getFullYear()} foodly · Crafted with 🔥 for hungry humans.
      </div>
    </footer>
  );
}
