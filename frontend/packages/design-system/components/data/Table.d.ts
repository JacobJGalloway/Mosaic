/**
 * @startingPoint section="Data" subtitle="Bordered data grid (claims, policies)" viewport="520x200"
 */
export interface TableColumn { key: string; label: string; }
export interface TableProps {
  columns: TableColumn[];
  rows: Record<string, React.ReactNode>[];
  caption?: string;
}
