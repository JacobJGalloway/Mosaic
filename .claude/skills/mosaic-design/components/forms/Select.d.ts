/**
 * @startingPoint section="Forms" subtitle="Labeled dropdown" viewport="320x90"
 */
export interface SelectOption { value: string; label: string; }
export interface SelectProps {
  label: string;
  options: SelectOption[];
  value?: string;
  onChange?: (e: React.ChangeEvent<HTMLSelectElement>) => void;
  required?: boolean;
}
