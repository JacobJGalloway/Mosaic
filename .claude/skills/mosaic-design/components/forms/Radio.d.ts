/**
 * @startingPoint section="Forms" subtitle="Grouped radio choice" viewport="320x60"
 */
export interface RadioOption { value: string; label: string; }
export interface RadioProps {
  name: string;
  label?: string;
  options: RadioOption[];
  value?: string;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
}
