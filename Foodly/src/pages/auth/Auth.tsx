import { useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { motion } from "framer-motion";
import { z } from "zod";
import { toast } from "sonner";
import { supabase } from "@/integrations/supabase/client";
import { lovable } from "@/integrations/lovable";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ArrowLeft, Mail, Lock, User as UserIcon } from "lucide-react";

const signInSchema = z.object({
  email: z.string().trim().email("Invalid email").max(255),
  password: z.string().min(6, "Min 6 characters").max(72),
});
const signUpSchema = signInSchema.extend({
  full_name: z.string().trim().min(2, "Name required").max(80),
});

export default function Auth() {
  const [params] = useSearchParams();
  const initial = params.get("mode") === "signup" ? "signup" : "signin";
  const [mode, setMode] = useState<"signin" | "signup">(initial);
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({ email: "", password: "", full_name: "" });
  const navigate = useNavigate();

  const handleEmail = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      if (mode === "signup") {
        const v = signUpSchema.parse(form);
        const { data, error } = await supabase.auth.signUp({
          email: v.email,
          password: v.password,
          options: {
            emailRedirectTo: window.location.origin,
            data: { full_name: v.full_name },
          },
        });
        if (error) throw error;
        if (data.session) {
          toast.success("Welcome to foodly! 🎉");
          navigate("/");
        } else {
          toast.success("Check your inbox to confirm your account.");
          navigate("/auth");
        }
      } else {
        const v = signInSchema.parse(form);
        const { data, error } = await supabase.auth.signInWithPassword({ email: v.email, password: v.password });
        if (error) throw error;
        if (data.session) {
          toast.success("Welcome back!");
          navigate("/");
        } else {
          toast.success("Signed in successfully.");
          navigate("/");
        }
      }
    } catch (err: any) {
      toast.error(err?.errors?.[0]?.message || err?.message || "Something went wrong");
    } finally {
      setLoading(false);
    }
  };

  const handleGoogle = async () => {
    setLoading(true);
    try {
      const result = await lovable.auth.signInWithOAuth("google", { redirect_uri: window.location.origin });
      if (result.error) throw result.error;
    } catch (err: any) {
      toast.error(err?.message || "Google sign-in failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="min-h-screen grid place-items-center bg-background relative overflow-hidden p-4">
      <div className="absolute inset-0 bg-gradient-radial pointer-events-none" />
      <div className="absolute -top-40 -left-40 w-[36rem] h-[36rem] bg-primary/30 blur-[120px] rounded-full" />
      <div className="absolute -bottom-40 -right-40 w-[36rem] h-[36rem] bg-secondary/30 blur-[120px] rounded-full" />

      <Link to="/" className="absolute top-6 left-6 inline-flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground">
        <ArrowLeft className="h-4 w-4" /> Back
      </Link>

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="relative w-full max-w-md glass-strong rounded-3xl p-8 shadow-soft"
      >
        <div className="flex items-center gap-2 mb-6">
          <div className="h-10 w-10 rounded-xl bg-gradient-brand grid place-items-center shadow-glow-pink">
            <span className="text-lg">🍱</span>
          </div>
          <span className="font-display font-bold text-2xl">food<span className="text-gradient-brand">ly</span></span>
        </div>

        <h1 className="font-display font-bold text-3xl">
          {mode === "signin" ? "Welcome back" : "Create account"}
        </h1>
        <p className="text-muted-foreground text-sm mt-1">
          {mode === "signin" ? "Sign in to keep ordering your favorites." : "Join foodly and unlock 50% off your first order."}
        </p>

        <Button
          type="button"
          onClick={handleGoogle}
          variant="outline"
          className="w-full mt-6 h-12 rounded-xl glass border-border/60 font-semibold"
        >
          <svg className="h-5 w-5 mr-2" viewBox="0 0 24 24"><path fill="#EA4335" d="M12 5.04c1.62 0 3.07.56 4.21 1.65l3.15-3.15C17.45 1.7 14.97.7 12 .7 7.39.7 3.4 3.34 1.46 7.18l3.67 2.85C6.05 7.07 8.79 5.04 12 5.04z"/><path fill="#4285F4" d="M23.3 12.27c0-.83-.07-1.62-.21-2.39H12v4.51h6.34c-.27 1.45-1.1 2.68-2.34 3.5l3.6 2.79c2.1-1.94 3.7-4.81 3.7-8.41z"/><path fill="#FBBC05" d="M5.13 14.34a7.04 7.04 0 0 1-.37-2.34c0-.81.14-1.6.37-2.34L1.46 6.81A11.94 11.94 0 0 0 .2 12c0 1.93.46 3.76 1.26 5.39l3.67-3.05z"/><path fill="#34A853" d="M12 23.3c3.24 0 5.96-1.07 7.95-2.91l-3.6-2.79c-1 .67-2.27 1.07-4.35 1.07-3.21 0-5.95-2.03-6.87-4.99l-3.67 3.05C3.4 20.66 7.39 23.3 12 23.3z"/></svg>
          Continue with Google
        </Button>

        <div className="my-5 flex items-center gap-3 text-xs text-muted-foreground">
          <div className="h-px flex-1 bg-border" />or<div className="h-px flex-1 bg-border" />
        </div>

        <form onSubmit={handleEmail} className="space-y-4">
          {mode === "signup" && (
            <div className="space-y-1.5">
              <Label>Full name</Label>
              <div className="relative">
                <UserIcon className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input value={form.full_name} onChange={(e) => setForm({ ...form, full_name: e.target.value })} className="pl-10 h-12 rounded-xl" placeholder="Jane Doe" maxLength={80} />
              </div>
            </div>
          )}
          <div className="space-y-1.5">
            <Label>Email</Label>
            <div className="relative">
              <Mail className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <Input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} className="pl-10 h-12 rounded-xl" placeholder="you@email.com" maxLength={255} />
            </div>
          </div>
          <div className="space-y-1.5">
            <Label>Password</Label>
            <div className="relative">
              <Lock className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <Input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} className="pl-10 h-12 rounded-xl" placeholder="••••••••" maxLength={72} />
            </div>
          </div>

          <Button type="submit" disabled={loading} className="w-full h-12 rounded-xl bg-gradient-brand text-primary-foreground font-semibold shadow-glow-pink hover:scale-[1.01]">
            {loading ? "Please wait…" : mode === "signin" ? "Sign in" : "Create account"}
          </Button>
        </form>

        <p className="text-center text-sm text-muted-foreground mt-6">
          {mode === "signin" ? "New to foodly? " : "Already have an account? "}
          <button onClick={() => setMode(mode === "signin" ? "signup" : "signin")} className="text-primary font-semibold hover:underline">
            {mode === "signin" ? "Create account" : "Sign in"}
          </button>
        </p>
      </motion.div>
    </main>
  );
}