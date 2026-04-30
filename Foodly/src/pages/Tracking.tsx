import PageShell from "@/components/foodly/layout/PageShell";
import OrderTracking from "@/components/foodly/OrderTracking";

export default function Tracking() {
  return (
    <PageShell title="Track your order · foodly" description="Live tracking of your foodly order.">
      <OrderTracking />
    </PageShell>
  );
}