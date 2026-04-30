import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { Toaster } from "@/components/ui/toaster";
import { TooltipProvider } from "@/components/ui/tooltip";
import Index from "./pages/Index.tsx";
import NotFound from "./pages/NotFound.tsx";
import { CartProvider } from "@/context/CartContext";
import { AuthProvider } from "@/context/AuthContext";
import Auth from "./pages/auth/Auth";
import RestaurantsPage from "./pages/Restaurants";
import RestaurantDetail from "./pages/RestaurantDetail";
import CategoriesPage from "./pages/CategoriesPage";
import CategoryDetail from "./pages/CategoryDetail";
import SearchPage from "./pages/Search";
import Delivery from "./pages/Delivery";
import Pickup from "./pages/Pickup";
import DineIn from "./pages/DineIn";
import Offers from "./pages/Offers";
import Tracking from "./pages/Tracking";
import Checkout from "./pages/Checkout";
import Account from "./pages/account/Account";
import Orders from "./pages/account/Orders";
import Favorites from "./pages/account/Favorites";

const queryClient = new QueryClient();

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <BrowserRouter>
        <AuthProvider>
          <CartProvider>
            <Toaster />
            <Sonner />
            <Routes>
              <Route path="/" element={<Index />} />
              <Route path="/auth" element={<Auth />} />
              <Route path="/restaurants" element={<RestaurantsPage />} />
              <Route path="/restaurant/:id" element={<RestaurantDetail />} />
              <Route path="/categories" element={<CategoriesPage />} />
              <Route path="/category/:slug" element={<CategoryDetail />} />
              <Route path="/search" element={<SearchPage />} />
              <Route path="/delivery" element={<Delivery />} />
              <Route path="/pickup" element={<Pickup />} />
              <Route path="/dine-in" element={<DineIn />} />
              <Route path="/offers" element={<Offers />} />
              <Route path="/track" element={<Tracking />} />
              <Route path="/checkout" element={<Checkout />} />
              <Route path="/account" element={<Account />} />
              <Route path="/account/orders" element={<Orders />} />
              <Route path="/account/favorites" element={<Favorites />} />
              {/* ADD ALL CUSTOM ROUTES ABOVE THE CATCH-ALL "*" ROUTE */}
              <Route path="*" element={<NotFound />} />
            </Routes>
          </CartProvider>
        </AuthProvider>
      </BrowserRouter>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
