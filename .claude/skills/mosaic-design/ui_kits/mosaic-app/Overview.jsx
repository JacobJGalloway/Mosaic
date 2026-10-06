const { Card } = window.MosaicKit;

const WEEKS = ["7/13", "7/20", "7/27", "8/3", "8/10", "8/17", "8/24", "8/31", "9/7", "9/14", "9/21", "9/28", "10/5"];
const INGESTION_RATE = [83, 85, 82, 88, 90, 87, 91, 93, 90, 94, 96, 93, 97];
const CLAIMS_RESOLVED = [22, 28, 19, 31, 26, 35, 24, 30, 38, 33, 29, 41, 36];
const FAILURE_REASONS = [
  { label: "Illegible scan", pct: 34, color: "var(--emerald-700)" },
  { label: "Missing required page", pct: 26, color: "var(--emerald-500)" },
  { label: "Unsupported file format", pct: 18, color: "var(--emerald-300)" },
  { label: "OCR field mismatch", pct: 14, color: "var(--neutral-400)" },
  { label: "Duplicate submission", pct: 8, color: "var(--neutral-600)" },
];

function BarChart({ values, labels, suffix, color, ariaLabel }) {
  const max = Math.max(...values);
  return (
    <div role="img" aria-label={ariaLabel} style={{ display: "flex", alignItems: "flex-end", gap: 4, height: 160, paddingBottom: 26 }}>
      {values.map((v, i) => (
        <div key={i} style={{ flex: 1, minWidth: 0, display: "flex", flexDirection: "column", alignItems: "center", gap: 6, height: "100%", justifyContent: "flex-end", position: "relative" }}>
          <span style={{ fontSize: 9, fontFamily: "var(--font-mono)", color: "var(--label-muted)", whiteSpace: "nowrap", overflow: "hidden" }}>{v}{suffix}</span>
          <div style={{ width: "100%", maxWidth: 22, borderRadius: "3px 3px 0 0", background: color, height: `${(v / max) * 110}px` }} />
          <span style={{ position: "absolute", top: "100%", left: "50%", fontSize: 9, color: "var(--label-muted)", whiteSpace: "nowrap", transform: "translateX(-50%) rotate(-45deg)", transformOrigin: "top center" }}>{labels[i]}</span>
        </div>
      ))}
    </div>
  );
}

function FailureBreakdown() {
  return (
    <div>
      <div role="img" aria-label="Ingestion failure reasons, past 4 weeks: illegible scan 34%, missing required page 26%, unsupported file format 18%, OCR field mismatch 14%, duplicate submission 8%."
        style={{ display: "flex", width: "100%", height: 14, borderRadius: "var(--radius-sm)", overflow: "hidden", marginBottom: 16 }}>
        {FAILURE_REASONS.map((r) => <div key={r.label} style={{ width: `${r.pct}%`, background: r.color }} />)}
      </div>
      <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
        {FAILURE_REASONS.map((r) => (
          <div key={r.label} style={{ display: "flex", alignItems: "center", gap: 8, fontSize: 13, color: "var(--label-default)" }}>
            <span style={{ width: 10, height: 10, borderRadius: 2, background: r.color, flexShrink: 0 }} />
            <span style={{ flex: 1 }}>{r.label}</span>
            <span style={{ fontFamily: "var(--font-mono)", color: "var(--label-muted)" }}>{r.pct}%</span>
          </div>
        ))}
      </div>
    </div>
  );
}

function Overview({ onNavigate }) {
  const { NavSidebar } = window.MosaicKit;
  return (
    <div style={{ display: "flex", minHeight: "100%" }}>
      <NavSidebar active="overview" onNavigate={onNavigate} />
      <main style={{ flex: 1, padding: 32, background: "var(--bg)" }}>
        <h1 style={{ margin: 0, fontSize: 25, fontWeight: 600, color: "var(--label-default)" }}>Dashboard</h1>
        <p style={{ margin: "4px 0 24px", fontSize: 14, color: "var(--label-muted)" }}>Document ingestion and claims activity, rolling 3 months</p>

        <div style={{ display: "grid", gridTemplateColumns: "minmax(0, 1fr) minmax(0, 1fr)", gap: 20, marginBottom: 20 }}>
          <Card title="Ingestion Success Rate" subtitle="By week, past 13 weeks">
            <BarChart values={INGESTION_RATE} labels={WEEKS} suffix="%" color="var(--accent-graphic)"

              ariaLabel={`Ingestion success rate by week, past 13 weeks, ranging from ${Math.min(...INGESTION_RATE)}% to ${Math.max(...INGESTION_RATE)}%, trending upward.`} />
          </Card>
          <Card title="Claims Resolved" subtitle="By week, past 13 weeks">
            <BarChart values={CLAIMS_RESOLVED} labels={WEEKS} suffix="" color="var(--accent-graphic)"
              ariaLabel={`Claims resolved by week, past 13 weeks, ranging from ${Math.min(...CLAIMS_RESOLVED)} to ${Math.max(...CLAIMS_RESOLVED)} claims.`} />
          </Card>
        </div>

        <Card title="Ingestion Failure Reasons" subtitle="Past 4 weeks" style={{ maxWidth: 560 }}>
          <FailureBreakdown />
        </Card>
      </main>
    </div>
  );
}

window.MosaicKit.Overview = Overview;
