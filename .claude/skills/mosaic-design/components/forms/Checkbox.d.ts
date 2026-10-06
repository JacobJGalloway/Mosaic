/**
 * @startingPoint section="Forms" subtitle="Labeled checkbox" viewport="260x40"
 */
export interface CheckboxProps {
  label: string;
  checked?: boolean;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
}
