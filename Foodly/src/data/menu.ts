import burger from "@/assets/burger.png";
import pizza from "@/assets/pizza.png";
import sushi from "@/assets/sushi.png";
import drink from "@/assets/drink.png";
import dessert from "@/assets/dessert.png";
import healthy from "@/assets/healthy.png";
import fries from "@/assets/fries.png";
import r1 from "@/assets/restaurant1.jpg";
import r2 from "@/assets/restaurant2.jpg";
import r3 from "@/assets/restaurant3.jpg";
import r4 from "@/assets/restaurant4.jpg";

export const categories = [
  { name: "Pizza", emoji: "🍕", img: pizza, tint: "from-primary/30 to-secondary/20" },
  { name: "Burgers", emoji: "🍔", img: burger, tint: "from-secondary/30 to-primary/20" },
  { name: "Sushi", emoji: "🍣", img: sushi, tint: "from-primary/25 to-secondary/25" },
  { name: "Drinks", emoji: "🥤", img: drink, tint: "from-primary/30 to-primary-glow/20" },
  { name: "Desserts", emoji: "🍰", img: dessert, tint: "from-secondary/30 to-primary/20" },
  { name: "Healthy", emoji: "🥗", img: healthy, tint: "from-secondary/25 to-secondary-glow/20" },
  { name: "Fast Food", emoji: "🍟", img: fries, tint: "from-secondary/35 to-primary/20" },
] as const;

export type Dish = {
  id: string;
  name: string;
  restaurant: string;
  price: number;
  img: string;
  rating: number;
  eta: string;
  tag?: string;
};

export const restaurants = [
  {
    id: "r1",
    name: "Neon Kitchen",
    cuisine: "Asian Fusion · Bowls",
    cover: r1,
    rating: 4.9,
    eta: "20–30 min",
    fee: "Free delivery",
    tag: "Trending",
  },
  {
    id: "r2",
    name: "Sakura Sushi Bar",
    cuisine: "Japanese · Sushi · Ramen",
    cover: r2,
    rating: 4.8,
    eta: "25–35 min",
    fee: "$1.99 delivery",
    tag: "Top rated",
  },
  {
    id: "r3",
    name: "Patty & Co.",
    cuisine: "American · Burgers",
    cover: r3,
    rating: 4.7,
    eta: "15–25 min",
    fee: "Free delivery",
    tag: "Fast",
  },
  {
    id: "r4",
    name: "Forno Rosso",
    cuisine: "Italian · Wood-fired Pizza",
    cover: r4,
    rating: 4.9,
    eta: "30–40 min",
    fee: "$0.99 delivery",
    tag: "New",
  },
];

export const featured: Dish[] = [
  { id: "d1", name: "Truffle Smash Burger", restaurant: "Patty & Co.", price: 14.5, img: burger, rating: 4.9, eta: "20 min", tag: "Bestseller" },
  { id: "d2", name: "Napoli Pepperoni", restaurant: "Forno Rosso", price: 18.0, img: pizza, rating: 4.8, eta: "30 min", tag: "Wood-fired" },
  { id: "d3", name: "Salmon Dragon Roll", restaurant: "Sakura Sushi", price: 22.0, img: sushi, rating: 4.9, eta: "25 min", tag: "Chef's pick" },
  { id: "d4", name: "Strawberry Cloud Shake", restaurant: "Neon Kitchen", price: 7.5, img: drink, rating: 4.7, eta: "15 min" },
  { id: "d5", name: "Molten Chocolate Lava", restaurant: "Forno Rosso", price: 9.0, img: dessert, rating: 4.9, eta: "20 min", tag: "Sweet" },
  { id: "d6", name: "Avocado Power Bowl", restaurant: "Neon Kitchen", price: 12.5, img: healthy, rating: 4.8, eta: "20 min", tag: "Healthy" },
];

// Per-restaurant menus + categories
export const restaurantMenus: Record<string, { category: string; items: Dish[] }[]> = {
  r1: [
    { category: "Bowls", items: [
      { id: "r1-1", name: "Avocado Power Bowl", restaurant: "Neon Kitchen", price: 12.5, img: healthy, rating: 4.8, eta: "20 min", tag: "Healthy" },
      { id: "r1-2", name: "Spicy Tuna Bowl", restaurant: "Neon Kitchen", price: 14.0, img: sushi, rating: 4.7, eta: "20 min" },
    ]},
    { category: "Drinks", items: [
      { id: "r1-3", name: "Strawberry Cloud Shake", restaurant: "Neon Kitchen", price: 7.5, img: drink, rating: 4.7, eta: "15 min" },
    ]},
  ],
  r2: [
    { category: "Sushi", items: [
      { id: "r2-1", name: "Salmon Dragon Roll", restaurant: "Sakura Sushi Bar", price: 22.0, img: sushi, rating: 4.9, eta: "25 min", tag: "Chef's pick" },
      { id: "r2-2", name: "Rainbow Roll", restaurant: "Sakura Sushi Bar", price: 19.0, img: sushi, rating: 4.8, eta: "25 min" },
    ]},
    { category: "Desserts", items: [
      { id: "r2-3", name: "Mochi Trio", restaurant: "Sakura Sushi Bar", price: 8.0, img: dessert, rating: 4.7, eta: "10 min" },
    ]},
  ],
  r3: [
    { category: "Burgers", items: [
      { id: "r3-1", name: "Truffle Smash Burger", restaurant: "Patty & Co.", price: 14.5, img: burger, rating: 4.9, eta: "20 min", tag: "Bestseller" },
      { id: "r3-2", name: "Classic Cheeseburger", restaurant: "Patty & Co.", price: 11.0, img: burger, rating: 4.7, eta: "15 min" },
    ]},
    { category: "Sides", items: [
      { id: "r3-3", name: "Loaded Fries", restaurant: "Patty & Co.", price: 6.5, img: fries, rating: 4.8, eta: "10 min" },
    ]},
  ],
  r4: [
    { category: "Pizza", items: [
      { id: "r4-1", name: "Napoli Pepperoni", restaurant: "Forno Rosso", price: 18.0, img: pizza, rating: 4.8, eta: "30 min", tag: "Wood-fired" },
      { id: "r4-2", name: "Margherita DOC", restaurant: "Forno Rosso", price: 16.0, img: pizza, rating: 4.9, eta: "30 min" },
    ]},
    { category: "Desserts", items: [
      { id: "r4-3", name: "Molten Chocolate Lava", restaurant: "Forno Rosso", price: 9.0, img: dessert, rating: 4.9, eta: "20 min", tag: "Sweet" },
    ]},
  ],
};

// All dishes flat (for search)
export const allDishes: Dish[] = Object.values(restaurantMenus).flatMap((cats) =>
  cats.flatMap((c) => c.items)
);

// Category mapping (which restaurants/dishes belong)
export const categorySlug = (name: string) => name.toLowerCase().replace(/\s+/g, "-");

export const restaurantsByCategory: Record<string, string[]> = {
  pizza: ["r4"],
  burgers: ["r3"],
  sushi: ["r2"],
  drinks: ["r1"],
  desserts: ["r2", "r4"],
  healthy: ["r1"],
  "fast-food": ["r3"],
};
