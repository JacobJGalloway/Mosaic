const { useState } = React;
const { Badge, Button, Card, NavSidebar } = window.MosaicKit;

function Tabs({ available, tab, setTab }) {
  const labels = { auto: "Auto Policy", home: "Home Policy", documents: "Documents" };
  const keys = [...available, "documents"];
  return (
    <div role="tablist" aria-label="Policy sections" style={{ display: "flex", gap: 4, borderBottom: "1px solid var(--border)", marginBottom: 20 }}>
      {keys.map((key) => (
        <button key={key} role="tab" aria-selected={tab === key} onClick={() => setTab(key)}
          style={{ padding: "10px 16px", fontSize: 14, fontWeight: 500, background: "none", border: "none", cursor: "pointer", marginBottom: -1,
            borderBottom: tab === key ? "2px solid var(--accent-graphic-strong)" : "2px solid transparent",
            color: tab === key ? "var(--accent-text)" : "var(--label-muted)" }}>
          {labels[key]}
        </button>
      ))}
    </div>
  );
}

function ClaimsTable({ claims }) {
  if (!claims.length) return <p style={{ fontSize: 14, color: "var(--label-muted)" }}>No claims filed.</p>;
  return (
    <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14 }}>
      <thead><tr>{["Claim", "Date", "Description", "Status"].map((h) => (
        <th key={h} style={{ textAlign: "left", padding: "9px 10px", fontSize: 12, textTransform: "uppercase", color: "var(--label-muted)", borderBottom: "1px solid var(--border-strong)" }}>{h}</th>
      ))}</tr></thead>
      <tbody>{claims.map((c) => (
        <tr key={c.id} style={{ borderBottom: "1px solid var(--border)" }}>
          <td style={{ padding: "10px", fontFamily: "var(--font-mono)", color: "var(--label-default)" }}>{c.id}</td>
          <td style={{ padding: "10px", color: "var(--label-muted)", fontFamily: "var(--font-mono)", fontSize: 13 }}>{c.date}</td>
          <td style={{ padding: "10px", color: "var(--label-default)" }}>{c.desc}</td>
          <td style={{ padding: "10px" }}><Badge tone={c.status}>{c.label}</Badge></td>
        </tr>
      ))}</tbody>
    </table>
  );
}

function CustomerDetail({ customer, onNavigate }) {
  const available = [customer.auto && "auto", customer.home && "home"].filter(Boolean);
  const [tab, setTab] = useState(available[0] || "documents");
  const policies = [customer.auto && "Auto", customer.home && "Home"].filter(Boolean);

  return (
    <div style={{ display: "flex", minHeight: "100%" }}>
      <NavSidebar active="customers" onNavigate={onNavigate} />
      <main style={{ flex: 1, padding: 32, background: "var(--bg)" }}>
        <button onClick={() => onNavigate("customers")} style={{ background: "none", border: "none", color: "var(--accent-text)", fontSize: 13, fontWeight: 600, cursor: "pointer", padding: 0, marginBottom: 16 }}>← Customers</button>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 24 }}>
          <div>
            <h1 style={{ margin: 0, fontSize: 25, fontWeight: 600, color: "var(--label-default)" }}>{customer.name}</h1>
            <div style={{ display: "flex", gap: 8, marginTop: 8 }}>
              <Badge tone={customer.status}>{customer.statusLabel}</Badge>
              {policies.map((p) => <Badge key={p} tone="neutral">{p}</Badge>)}
            </div>
          </div>
          <Button variant="secondary">Edit Record</Button>
        </div>

        <Tabs available={available} tab={tab} setTab={setTab} />

        {tab === "auto" && customer.auto && (
          <Card title="Auto Policy" subtitle={customer.auto.policyNumber}>
            <div style={{ display: "flex", gap: 32, marginBottom: 18, fontSize: 14 }}>
              <div><div style={{ color: "var(--label-muted)", fontSize: 12 }}>Vehicle</div><div style={{ color: "var(--label-default)" }}>{customer.auto.vehicle}</div></div>
              <div><div style={{ color: "var(--label-muted)", fontSize: 12 }}>Premium</div><div style={{ color: "var(--label-default)", fontFamily: "var(--font-mono)" }}>{customer.auto.premium}</div></div>
              <div><div style={{ color: "var(--label-muted)", fontSize: 12 }}>Renewal</div><div style={{ color: "var(--label-default)" }}>{customer.auto.renewal}</div></div>
            </div>
            <h4 style={{ fontSize: 13, color: "var(--label-muted)", margin: "0 0 10px" }}>Claims History</h4>
            <ClaimsTable claims={customer.auto.claims} />
          </Card>
        )}
        {tab === "home" && customer.home && (
          <Card title="Home Policy" subtitle={customer.home.policyNumber}>
            <div style={{ display: "flex", gap: 32, marginBottom: 18, fontSize: 14 }}>
              <div><div style={{ color: "var(--label-muted)", fontSize: 12 }}>Property</div><div style={{ color: "var(--label-default)" }}>{customer.home.property}</div></div>
              <div><div style={{ color: "var(--label-muted)", fontSize: 12 }}>Premium</div><div style={{ color: "var(--label-default)", fontFamily: "var(--font-mono)" }}>{customer.home.premium}</div></div>
              <div><div style={{ color: "var(--label-muted)", fontSize: 12 }}>Renewal</div><div style={{ color: "var(--label-default)" }}>{customer.home.renewal}</div></div>
            </div>
            <h4 style={{ fontSize: 13, color: "var(--label-muted)", margin: "0 0 10px" }}>Claims History</h4>
            <ClaimsTable claims={customer.home.claims} />
          </Card>
        )}
        {tab === "documents" && (
          <Card title="Documents">
            <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
              {["Auto_Application.pdf", "Home_Inspection_Report.pdf", "ID_Verification.jpg"].map((f) => (
                <div key={f} style={{ display: "flex", justifyContent: "space-between", padding: "10px 12px", background: "var(--surface-sunken)", borderRadius: "var(--radius-sm)" }}>
                  <span style={{ fontFamily: "var(--font-mono)", fontSize: 14, color: "var(--label-default)" }}>{f}</span>
                  <Badge tone="success">Parsed</Badge>
                </div>
              ))}
            </div>
          </Card>
        )}
      </main>
    </div>
  );
}

window.MosaicKit.CustomerDetail = CustomerDetail;
