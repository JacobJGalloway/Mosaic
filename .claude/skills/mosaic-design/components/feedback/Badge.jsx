import React from "react";

const tones = {
  success: { bg: "var(--success-bg)", text: "var(--success-text)", border: "var(--success-border)" },
  warning: { bg: "var(--warning-bg)", text: "var(--warning-text)", border: "var(--warning-border)" },
  danger: { bg: "var(--danger-bg)", text: "var(--danger-text)", border: "var(--danger-border)" },
  info: { bg: "var(--info-bg)", text: "var(--info-text)", border: "var(--info-border)" },
  neutral: { bg: "var(--surface-sunken)", text: "var(--label-muted)", border: "var(--border)" },
};

export function Badge({ tone = "neutral", children }) {
  const t = tones[tone];
  return (
    <span style={{
      display: "inline-flex", alignItems: "center", gap: "5px",
      padding: "3px 10px", borderRadius: "var(--radius-pill)",
      fontFamily: "var(--font-sans)", fontSize: "12px", fontWeight: 600,
      background: t.bg, color: t.text, border: `1px solid ${t.border}`,
    }}>
      {children}
    </span>
  );
}
