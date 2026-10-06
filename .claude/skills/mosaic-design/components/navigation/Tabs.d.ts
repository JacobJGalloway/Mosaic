/**
 * @startingPoint section="Navigation" subtitle="Underline tab row" viewport="400x50"
 */
export interface TabItem { key: string; label: string; }
export interface TabsProps {
  tabs: TabItem[];
  active: string;
  onChange?: (key: string) => void;
}
