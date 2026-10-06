/**
 * @startingPoint section="Forms" subtitle="Primary action button, 4 variants" viewport="320x80"
 */
export interface ButtonProps {
  variant?: "primary" | "secondary" | "ghost" | "danger";
  size?: "sm" | "md" | "lg";
  disabled?: boolean;
  type?: "button" | "submit";
  ariaLabel?: string;
  onClick?: () => void;
  children: React.ReactNode;
}
