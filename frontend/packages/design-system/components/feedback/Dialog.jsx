import React from "react";

export function Dialog({ open, title, children, onClose, actions }) {
  if (!open) return null;
  return (
    <div
      role="presentation"
      onClick={onClose}
      style={{ position: "fixed", inset: 0, background: "rgba(0,0,0,0.5)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 100 }}
    >
      <div
        role="dialog"
        aria-modal="true"
        aria-labelledby="dialog-title"
        onClick={(e) => e.stopPropagation()}
        style={{
          background: "var(--surface)", borderRadius: "var(--radius-lg)", boxShadow: "var(--shadow-lg)",
          padding: "24px", width: "min(420px, 90vw)", fontFamily: "var(--font-sans)", color: "var(--label-default)",
        }}
      >
        <h2 id="dialog-title" style={{ fontSize: "20px", fontWeight: 600, margin: "0 0 12px" }}>{title}</h2>
        <div style={{ fontSize: "14px", lineHeight: 1.5, color: "var(--label-muted)", marginBottom: "20px" }}>{children}</div>
        <div style={{ display: "flex", justifyContent: "flex-end", gap: "10px" }}>{actions}</div>
      </div>
    </div>
  );
}
