import React from "react";

export function Table({ columns = [], rows = [], caption }) {
  return (
    <table style={{ width: "100%", borderCollapse: "collapse", fontFamily: "var(--font-sans)", fontSize: "14px" }}>
      {caption && <caption style={{ textAlign: "left", fontSize: "12px", color: "var(--label-muted)", marginBottom: "8px" }}>{caption}</caption>}
      <thead>
        <tr>
          {columns.map((col) => (
            <th key={col.key} scope="col" style={{
              textAlign: "left", padding: "10px 12px", fontSize: "12px", textTransform: "uppercase",
              letterSpacing: "0.05em", color: "var(--label-muted)", borderBottom: "1px solid var(--border-strong)",
            }}>{col.label}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {rows.map((row, i) => (
          <tr key={i} style={{ borderBottom: "1px solid var(--border)" }}>
            {columns.map((col) => (
              <td key={col.key} style={{ padding: "12px", color: "var(--label-default)" }}>{row[col.key]}</td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
}
