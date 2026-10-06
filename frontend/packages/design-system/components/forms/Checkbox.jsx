import React, { useId } from "react";

export function Checkbox({ label, checked, onChange }) {
  const id = useId();
  return (
    <label htmlFor={id} style={{ display: "flex", alignItems: "center", gap: "8px", fontFamily: "var(--font-sans)", fontSize: "14px", color: "var(--label-default)", cursor: "pointer" }}>
      <input
        id={id}
        type="checkbox"
        checked={checked}
        onChange={onChange}
        style={{ width: "18px", height: "18px", accentColor: "var(--accent-fill)" }}
      />
      {label}
    </label>
  );
}
