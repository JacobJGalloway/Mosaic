import React from "react";

export function Radio({ name, options = [], value, onChange, label }) {
  return (
    <fieldset style={{ border: "none", padding: 0, margin: 0, fontFamily: "var(--font-sans)" }}>
      {label && <legend style={{ fontSize: "13px", fontWeight: 600, color: "var(--label-default)", marginBottom: "6px", padding: 0 }}>{label}</legend>}
      <div style={{ display: "flex", gap: "16px" }}>
        {options.map((opt) => (
          <label key={opt.value} style={{ display: "flex", alignItems: "center", gap: "6px", fontSize: "14px", color: "var(--label-default)", cursor: "pointer" }}>
            <input type="radio" name={name} value={opt.value} checked={value === opt.value} onChange={onChange} style={{ accentColor: "var(--accent-fill)" }} />
            {opt.label}
          </label>
        ))}
      </div>
    </fieldset>
  );
}
