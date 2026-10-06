import React, { useId } from "react";

export function Input({
  label,
  placeholder,
  value,
  onChange,
  type = "text",
  error,
  hint,
  required = false,
}) {
  const id = useId();
  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "6px", fontFamily: "var(--font-sans)" }}>
      <label htmlFor={id} style={{ fontSize: "13px", fontWeight: 600, color: "var(--label-default)" }}>
        {label}
        {required && <span style={{ color: "var(--danger-text)" }}> *</span>}
      </label>
      <input
        id={id}
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        aria-invalid={!!error}
        aria-describedby={error || hint ? `${id}-desc` : undefined}
        style={{
          font: "15px var(--font-sans)",
          padding: "9px 12px",
          borderRadius: "var(--radius-sm)",
          border: `1px solid ${error ? "var(--danger-text)" : "var(--border-input)"}`,
          background: "var(--surface)",
          color: "var(--label-default)",
          outline: "none",
        }}
        onFocus={(e) => (e.target.style.boxShadow = "0 0 0 2px var(--focus-ring)")}
        onBlur={(e) => (e.target.style.boxShadow = "none")}
      />
      {(error || hint) && (
        <span id={`${id}-desc`} style={{ fontSize: "12px", color: error ? "var(--danger-text)" : "var(--label-muted)" }}>
          {error || hint}
        </span>
      )}
    </div>
  );
}
