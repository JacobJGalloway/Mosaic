/**
 * @startingPoint section="Feedback" subtitle="Modal confirmation" viewport="420x240"
 */
export interface DialogProps {
  open: boolean;
  title: string;
  children: React.ReactNode;
  onClose?: () => void;
  actions?: React.ReactNode;
}
