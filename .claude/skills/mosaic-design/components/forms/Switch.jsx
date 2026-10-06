import React, { useId } from "react";

export function Switch({ label, checked, onChange }) {
  const id = useId();
  return (
    <label htmlFor={id} style={{ display: "flex", alignItems: "center", gap: "10px", fontFamily: "var(--font-sans)", fontSize: "14px", color: "var(--label-default)", cursor: "pointer" }}>
      <span
        role="switch"
        aria-checked={checked}
        style={{
          width: "36px", height: "20px", borderRadius: "var(--radius-pill)",
          background: checked ? "var(--accent-fill)" : "var(--border-strong)",
          position: "relative", transition: "background-color 0.18s", flexShrink: 0,
        }}
      >
        <span style={{
          position: "absolute", top: "2px", left: checked ? "18px" : "2px",
          width: "16px", height: "16px", borderRadius: "50%", background: "var(--neutral-0)",
          transition: "left 0.18s", boxShadow: "var(--shadow-sm)",
        }} />
      </span>
      <input id={id} type="checkbox" checked={checked} onChange={onChange} style={{ position: "absolute", opacity: 0, width: 0, height: 0 }} />
      {label}
    </label>
  );
}
