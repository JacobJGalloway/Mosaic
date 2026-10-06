/**
 * @startingPoint section="Data" subtitle="Surface panel with title + actions" viewport="360x180"
 */
export interface CardProps {
  title?: string;
  subtitle?: string;
  actions?: React.ReactNode;
  children: React.ReactNode;
}
