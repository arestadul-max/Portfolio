import Navbar from "@/components/foodly/Navbar";
import Hero from "@/components/foodly/Hero";
import Categories from "@/components/foodly/Categories";
import Restaurants from "@/components/foodly/Restaurants";
import HowItWorks from "@/components/foodly/HowItWorks";
import OrderTracking from "@/components/foodly/OrderTracking";
import CTASection from "@/components/foodly/CTASection";
import Footer from "@/components/foodly/Footer";
import CartDrawer from "@/components/foodly/CartDrawer";

const Index = () => {
  return (
    <main className="min-h-screen bg-background text-foreground overflow-x-hidden">
      <Navbar />
      <Hero />
      <Categories />
      <Restaurants />
      <HowItWorks />
      <OrderTracking />
      <CTASection />
      <Footer />
      <CartDrawer />
    </main>
  );
};

export default Index;
