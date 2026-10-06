/**
 * @startingPoint section="Feedback" subtitle="Status pill — 5 tones" viewport="200x40"
 */
export interface BadgeProps {
  tone?: "success" | "warning" | "danger" | "info" | "neutral";
  children: React.ReactNode;
}
