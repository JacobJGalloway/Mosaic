const { useState } = React;
const { Badge, Button, NavSidebar } = window.MosaicKit;

const CUSTOMERS = [
  { name: "Jane Alvarez", status: "success", statusLabel: "Active", updated: "Oct 2, 2026",
    auto: { policyNumber: "POL-AUTO-4471829", vehicle: "2021 Honda Accord", premium: "$1,240.00 / yr", renewal: "Jan 14, 2027",
      claims: [
        { id: "CLM-20931", date: "Aug 14, 2026", desc: "Rear-end collision", status: "warning", label: "Pending Review" },
        { id: "CLM-19844", date: "Mar 2, 2025", desc: "Windshield replacement", status: "success", label: "Closed" },
      ] },
    home: { policyNumber: "POL-HOME-2290104", property: "118 Birchwood Ln", premium: "$980.00 / yr", renewal: "Jun 2, 2027", claims: [] } },
  { name: "Marcus Webb", status: "success", statusLabel: "Active", updated: "Sep 29, 2026",
    auto: { policyNumber: "POL-AUTO-5519203", vehicle: "2019 Ford F-150", premium: "$1,510.00 / yr", renewal: "Nov 3, 2026",
      claims: [{ id: "CLM-20655", date: "Jul 2, 2026", desc: "Hail damage", status: "success", label: "Closed" }] },
    home: null },
  { name: "Priya Nair", status: "warning", statusLabel: "Pending Review", updated: "Sep 27, 2026",
    auto: null,
    home: { policyNumber: "POL-HOME-3387711", property: "42 Sycamore Ct", premium: "$1,120.00 / yr", renewal: "Feb 18, 2027",
      claims: [{ id: "CLM-21102", date: "Sep 20, 2026", desc: "Burst pipe — water damage", status: "warning", label: "Pending Review" }] } },
  { name: "Derek Osei", status: "success", statusLabel: "Active", updated: "Sep 20, 2026",
    auto: { policyNumber: "POL-AUTO-6603348", vehicle: "2023 Subaru Outback", premium: "$1,080.00 / yr", renewal: "Apr 9, 2027", claims: [] },
    home: { policyNumber: "POL-HOME-4471256", property: "900 Ridgeline Dr", premium: "$1,340.00 / yr", renewal: "Dec 1, 2026",
      claims: [{ id: "CLM-18820", date: "Jun 11, 2025", desc: "Storm damage — roof", status: "success", label: "Closed" }] } },
  { name: "Lin Chen", status: "danger", statusLabel: "Lapsed", updated: "Sep 11, 2026",
    auto: { policyNumber: "POL-AUTO-7715560", vehicle: "2018 Toyota Camry", premium: "$990.00 / yr", renewal: "Lapsed Sep 1, 2026",
      claims: [{ id: "CLM-17790", date: "Apr 30, 2025", desc: "Theft", status: "danger", label: "Denied" }] },
    home: null },
];

function policyList(c) {
  const list = [];
  if (c.auto) list.push({ key: "Auto", number: c.auto.policyNumber });
  if (c.home) list.push({ key: "Home", number: c.home.policyNumber });
  return list;
}
function allClaims(c) {
  return [...(c.auto?.claims || []), ...(c.home?.claims || [])];
}

function matches(c, q) {
  if (!q) return { hit: true };
  const query = q.toLowerCase();
  if (c.name.toLowerCase().includes(query)) return { hit: true };
  const policyHit = policyList(c).find((p) => p.number.toLowerCase().includes(query) || p.key.toLowerCase().includes(query));
  if (policyHit) return { hit: true };
  const claimHit = allClaims(c).find((cl) => cl.id.toLowerCase().includes(query) || cl.desc.toLowerCase().includes(query));
  if (claimHit) return { hit: true, claim: claimHit };
  return { hit: false };
}

function Dashboard({ onNavigate, onOpenCustomer }) {
  const [query, setQuery] = useState("");
  const results = CUSTOMERS.map((c) => ({ c, m: matches(c, query) })).filter((r) => r.m.hit);

  return (
    <div style={{ display: "flex", minHeight: "100%" }}>
      <NavSidebar active="customers" onNavigate={onNavigate} />
      <main style={{ flex: 1, padding: 32, background: "var(--bg)" }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 24 }}>
          <div>
            <h1 style={{ margin: 0, fontSize: 25, fontWeight: 600, color: "var(--label-default)" }}>Customers</h1>
            <p style={{ margin: "4px 0 0", fontSize: 14, color: "var(--label-muted)" }}>Auto and home policyholders</p>
          </div>
          <Button onClick={() => onNavigate("onboarding")}>New Customer</Button>
        </div>

        <div style={{ marginBottom: 16 }}>
          <label htmlFor="customer-search" style={{ display: "block", fontSize: 13, fontWeight: 600, color: "var(--label-default)", marginBottom: 6 }}>
            Search customers, policies, or claims
          </label>
          <input
            id="customer-search"
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Name, policy number, or claim ID / description"
            style={{ font: "15px var(--font-sans)", padding: "9px 12px", borderRadius: "var(--radius-sm)", border: "1px solid var(--border-input)", background: "var(--surface)", color: "var(--label-default)", width: "100%", maxWidth: 420, boxSizing: "border-box" }}
          />
        </div>

        <div style={{ background: "var(--surface)", border: "1px solid var(--border)", borderRadius: "var(--radius-md)", boxShadow: "var(--shadow-sm)", overflow: "hidden" }}>
          <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14 }}>
            <thead>
              <tr>
                {["Name", "Policies", "Status", "Last Activity", ""].map((h) => (
                  <th key={h} scope="col" style={{ textAlign: "left", padding: "10px 16px", fontSize: 12, textTransform: "uppercase", letterSpacing: "0.05em", color: "var(--label-muted)", borderBottom: "1px solid var(--border-strong)" }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {results.map(({ c, m }) => (
                <tr key={c.name} style={{ borderBottom: "1px solid var(--border)" }}>
                  <td style={{ padding: "12px 16px", color: "var(--label-default)", fontWeight: 500, verticalAlign: "top" }}>
                    {c.name}
                    {m.claim && (
                      <div style={{ marginTop: 4, fontSize: 12, color: "var(--label-muted)" }}>
                        Matched claim <span style={{ fontFamily: "var(--font-mono)" }}>{m.claim.id}</span> — {m.claim.desc}
                      </div>
                    )}
                  </td>
                  <td style={{ padding: "12px 16px", verticalAlign: "top" }}>
                    <div style={{ display: "flex", gap: 6 }}>
                      {policyList(c).map((p) => <Badge key={p.key} tone="neutral">{p.key}</Badge>)}
                    </div>
                  </td>
                  <td style={{ padding: "12px 16px", verticalAlign: "top" }}><Badge tone={c.status}>{c.statusLabel}</Badge></td>
                  <td style={{ padding: "12px 16px", color: "var(--label-muted)", fontFamily: "var(--font-mono)", fontSize: 13, verticalAlign: "top" }}>{c.updated}</td>
                  <td style={{ padding: "12px 16px", verticalAlign: "top" }}><Button variant="ghost" size="sm" onClick={() => onOpenCustomer(c)}>View</Button></td>
                </tr>
              ))}
              {results.length === 0 && (
                <tr><td colSpan={5} style={{ padding: "20px 16px", color: "var(--label-muted)", textAlign: "center" }}>No customers found.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </main>
    </div>
  );
}

window.MosaicKit.Dashboard = Dashboard;
