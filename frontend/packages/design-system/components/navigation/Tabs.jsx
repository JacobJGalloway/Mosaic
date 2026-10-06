import React from "react";

export function Tabs({ tabs = [], active, onChange }) {
  return (
    <div role="tablist" aria-label="Section tabs" style={{ display: "flex", gap: "4px", borderBottom: "1px solid var(--border)", fontFamily: "var(--font-sans)" }}>
      {tabs.map((tab) => {
        const isActive = tab.key === active;
        return (
          <button
            key={tab.key}
            role="tab"
            aria-selected={isActive}
            onClick={() => onChange?.(tab.key)}
            style={{
              padding: "10px 16px", fontSize: "14px", fontWeight: 500, background: "none", border: "none",
              borderBottom: isActive ? "2px solid var(--accent-graphic-strong)" : "2px solid transparent",
              color: isActive ? "var(--accent-text)" : "var(--label-muted)", cursor: "pointer", marginBottom: "-1px",
            }}
          >
            {tab.label}
          </button>
        );
      })}
    </div>
  );
}
