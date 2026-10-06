/**
 * @startingPoint section="Forms" subtitle="Labeled text field with error/hint state" viewport="320x90"
 */
export interface InputProps {
  label: string;
  placeholder?: string;
  value?: string;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
  type?: "text" | "email" | "tel" | "date" | "number";
  error?: string;
  hint?: string;
  required?: boolean;
}
