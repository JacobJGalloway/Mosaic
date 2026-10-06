import React from "react";

export function NavSidebar({ items = [], active, productName = "Mosaic" }) {
  return (
    <nav aria-label="Primary" style={{
      width: "220px", height: "100%", background: "var(--surface)", borderRight: "1px solid var(--border)",
      display: "flex", flexDirection: "column", fontFamily: "var(--font-sans)", flexShrink: 0,
    }}>
      <div style={{ padding: "20px 20px 16px", fontSize: "18px", fontWeight: 700, color: "var(--label-default)" }}>{productName}</div>
      <div style={{ display: "flex", flexDirection: "column", gap: "2px", padding: "0 10px" }}>
        {items.map((item) => {
          const isActive = item.key === active;
          return (
            <a
              key={item.key}
              href={item.href || "#"}
              aria-current={isActive ? "page" : undefined}
              style={{
                display: "flex", alignItems: "center", gap: "10px", padding: "9px 12px",
                borderRadius: "var(--radius-sm)", fontSize: "14px", fontWeight: 500, textDecoration: "none",
                background: isActive ? "var(--accent-tint)" : "transparent",
                color: isActive ? "var(--accent-text)" : "var(--label-default)",
              }}
            >
              {item.label}
            </a>
          );
        })}
      </div>
    </nav>
  );
}
