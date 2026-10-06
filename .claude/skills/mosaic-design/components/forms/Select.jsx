import React, { useId } from "react";

export function Select({ label, options = [], value, onChange, required = false }) {
  const id = useId();
  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "6px", fontFamily: "var(--font-sans)" }}>
      <label htmlFor={id} style={{ fontSize: "13px", fontWeight: 600, color: "var(--label-default)" }}>
        {label}
        {required && <span style={{ color: "var(--danger-text)" }}> *</span>}
      </label>
      <select
        id={id}
        value={value}
        onChange={onChange}
        style={{
          font: "15px var(--font-sans)",
          padding: "9px 12px",
          borderRadius: "var(--radius-sm)",
          border: "1px solid var(--border-input)",
          background: "var(--surface)",
          color: "var(--label-default)",
        }}
      >
        {options.map((opt) => (
          <option key={opt.value} value={opt.value}>{opt.label}</option>
        ))}
      </select>
    </div>
  );
}
