import React from "react";

export function Card({ title, subtitle, actions, children }) {
  return (
    <div style={{
      background: "var(--surface)", border: "1px solid var(--border)", borderRadius: "var(--radius-md)",
      padding: "20px", boxShadow: "var(--shadow-sm)", fontFamily: "var(--font-sans)",
    }}>
      {(title || actions) && (
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: "14px" }}>
          <div>
            {title && <h3 style={{ margin: 0, fontSize: "17px", fontWeight: 600, color: "var(--label-default)" }}>{title}</h3>}
            {subtitle && <p style={{ margin: "2px 0 0", fontSize: "13px", color: "var(--label-muted)" }}>{subtitle}</p>}
          </div>
          {actions}
        </div>
      )}
      {children}
    </div>
  );
}
