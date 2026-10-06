import React from "react";

const sizes = {
  sm: { padding: "6px 12px", fontSize: "13px" },
  md: { padding: "9px 16px", fontSize: "15px" },
  lg: { padding: "12px 20px", fontSize: "17px" },
};

export function Button({
  variant = "primary",
  size = "md",
  disabled = false,
  children,
  onClick,
  type = "button",
  ariaLabel,
}) {
  const base = {
    fontFamily: "var(--font-sans)",
    fontWeight: 600,
    borderRadius: "var(--radius-sm)",
    border: "1px solid transparent",
    cursor: disabled ? "not-allowed" : "pointer",
    opacity: disabled ? 0.5 : 1,
    transition: "background-color 0.18s, border-color 0.18s, color 0.18s",
    ...sizes[size],
  };

  const variants = {
    primary: {
      background: "var(--accent-fill)",
      color: "var(--label-on-accent-fill)",
      borderColor: "var(--accent-fill)",
    },
    secondary: {
      background: "var(--surface)",
      color: "var(--label-default)",
      borderColor: "var(--border-input)",
    },
    ghost: {
      background: "transparent",
      color: "var(--accent-text)",
      borderColor: "transparent",
    },
    danger: {
      background: "var(--danger-text)",
      color: "var(--label-on-accent-fill)",
      borderColor: "var(--danger-text)",
    },
  };

  return (
    <button
      type={type}
      disabled={disabled}
      onClick={onClick}
      aria-label={ariaLabel}
      style={{ ...base, ...variants[variant] }}
    >
      {children}
    </button>
  );
}
