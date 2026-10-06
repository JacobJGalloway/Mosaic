/**
 * @startingPoint section="Forms" subtitle="Toggle switch" viewport="200x40"
 */
export interface SwitchProps {
  label: string;
  checked?: boolean;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
}
