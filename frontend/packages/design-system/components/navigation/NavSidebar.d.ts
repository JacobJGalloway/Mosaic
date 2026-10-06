/**
 * @startingPoint section="Navigation" subtitle="Left app nav rail" viewport="220x320"
 */
export interface NavItem { key: string; label: string; href?: string; }
export interface NavSidebarProps {
  items: NavItem[];
  active: string;
  productName?: string;
}
