// Mosaic UI Kit — shared visual primitives (re-implemented inline against the token system;
// mirrors components/ for the kit's zero-build preview).
const { useState } = React;

function Badge({ tone = "neutral", children }) {
  const tones = {
    success: ["var(--success-bg)", "var(--success-text)", "var(--success-border)"],
    warning: ["var(--warning-bg)", "var(--warning-text)", "var(--warning-border)"],
    danger: ["var(--danger-bg)", "var(--danger-text)", "var(--danger-border)"],
    info: ["var(--info-bg)", "var(--info-text)", "var(--info-border)"],
    neutral: ["var(--surface-sunken)", "var(--label-muted)", "var(--border)"],
  };
  const [bg, text, border] = tones[tone];
  return (
    <span style={{ display: "inline-flex", alignItems: "center", gap: 5, padding: "3px 10px", borderRadius: "var(--radius-pill)", fontSize: 12, fontWeight: 600, background: bg, color: text, border: `1px solid ${border}` }}>
      {children}
    </span>
  );
}

function Button({ variant = "primary", size = "md", children, onClick, ariaLabel, disabled }) {
  const sizes = { sm: "6px 12px", md: "9px 16px", lg: "12px 20px" };
  const fsize = { sm: 13, md: 15, lg: 17 }[size];
  const variants = {
    primary: { background: "var(--accent-fill)", color: "var(--label-on-accent-fill)", border: "1px solid var(--accent-fill)" },
    secondary: { background: "var(--surface)", color: "var(--label-default)", border: "1px solid var(--border-input)" },
    ghost: { background: "transparent", color: "var(--accent-text)", border: "1px solid transparent" },
    danger: { background: "var(--danger-text)", color: "var(--label-on-accent-fill)", border: "1px solid var(--danger-text)" },
  };
  return (
    <button onClick={onClick} disabled={disabled} aria-label={ariaLabel} style={{ fontFamily: "var(--font-sans)", fontWeight: 600, fontSize: fsize, padding: sizes[size], borderRadius: "var(--radius-sm)", cursor: disabled ? "not-allowed" : "pointer", opacity: disabled ? 0.5 : 1, ...variants[variant] }}>
      {children}
    </button>
  );
}

function Field({ label, children, tag }) {
  return (
    <div style={{ display: "flex", flexDirection: "column", gap: 6 }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline" }}>
        <label style={{ fontSize: 13, fontWeight: 600, color: "var(--label-default)" }}>{label}</label>
        {tag}
      </div>
      {children}
    </div>
  );
}

function TextInput(props) {
  return (
    <input {...props} style={{ font: "15px var(--font-sans)", padding: "9px 12px", borderRadius: "var(--radius-sm)", border: `1px solid ${props.missing ? "var(--warning-border)" : "var(--border-input)"}`, background: props.readOnly ? "var(--surface-sunken)" : "var(--surface)", color: "var(--label-default)", width: "100%", boxSizing: "border-box" }} />
  );
}

function Card({ title, subtitle, actions, children, style }) {
  return (
    <div style={{ background: "var(--surface)", border: "1px solid var(--border)", borderRadius: "var(--radius-md)", padding: 20, boxShadow: "var(--shadow-sm)", ...style }}>
      {(title || actions) && (
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: 14 }}>
          <div>
            {title && <h3 style={{ margin: 0, fontSize: 17, fontWeight: 600, color: "var(--label-default)" }}>{title}</h3>}
            {subtitle && <p style={{ margin: "2px 0 0", fontSize: 13, color: "var(--label-muted)", fontFamily: "var(--font-mono)" }}>{subtitle}</p>}
          </div>
          {actions}
        </div>
      )}
      {children}
    </div>
  );
}

function NavSidebar({ active, onNavigate }) {
  const items = [
    { key: "overview", label: "Dashboard" },
    { key: "customers", label: "Customers" },
  ];
  return (
    <nav aria-label="Primary" style={{ width: 220, background: "var(--surface)", borderRight: "1px solid var(--border)", display: "flex", flexDirection: "column", flexShrink: 0 }}>
      <div style={{ padding: "20px 20px 16px", fontSize: 18, fontWeight: 700, color: "var(--label-default)" }}>Mosaic</div>
      <div style={{ display: "flex", flexDirection: "column", gap: 2, padding: "0 10px" }}>
        {items.map((item) => (
          <a key={item.key} href="#" onClick={(e) => { e.preventDefault(); onNavigate(item.key); }} aria-current={item.key === active ? "page" : undefined}
            style={{ display: "block", padding: "9px 12px", borderRadius: "var(--radius-sm)", fontSize: 14, fontWeight: 500, textDecoration: "none",
              background: item.key === active ? "var(--accent-tint)" : "transparent", color: item.key === active ? "var(--accent-text)" : "var(--label-default)" }}>
            {item.label}
          </a>
        ))}
      </div>
    </nav>
  );
}

function ThemeToggle({ dark, onChange }) {
  return (
    <label style={{ display: "flex", alignItems: "center", gap: 8, fontSize: 13, color: "var(--label-muted)", cursor: "pointer" }}>
      Dark mode
      <span role="switch" aria-checked={dark} onClick={() => onChange(!dark)} style={{ width: 36, height: 20, borderRadius: "var(--radius-pill)", background: dark ? "var(--accent-fill)" : "var(--border-strong)", position: "relative" }}>
        <span style={{ position: "absolute", top: 2, left: dark ? 18 : 2, width: 16, height: 16, borderRadius: "50%", background: "var(--neutral-0)", transition: "left .18s" }} />
      </span>
    </label>
  );
}

window.MosaicKit = { Badge, Button, Field, TextInput, Card, NavSidebar, ThemeToggle };
