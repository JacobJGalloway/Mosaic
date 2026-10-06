const { useState } = React;
const { Badge, Button, Field, TextInput, Card, NavSidebar } = window.MosaicKit;

const STEPS = ["Documents", "Policyholder", "Auto Policy", "Home Policy", "Review"];

function DocBadge() { return <Badge tone="info">From Document</Badge>; }
function MissingBadge() { return <Badge tone="warning">Missing — required</Badge>; }

function OnboardingWizard({ onNavigate, onFinalize }) {
  const [step, setStep] = useState(0);
  const [confirming, setConfirming] = useState(false);

  return (
    <div style={{ display: "flex", minHeight: "100%" }}>
      <NavSidebar active="onboarding" onNavigate={onNavigate} />
      <main style={{ flex: 1, padding: 32, background: "var(--bg)", display: "flex", gap: 32 }}>
        <aside style={{ width: 200, flexShrink: 0 }}>
          <h2 style={{ fontSize: 13, textTransform: "uppercase", letterSpacing: "0.06em", color: "var(--label-muted)", margin: "0 0 12px" }}>New Customer</h2>
          <ol style={{ listStyle: "none", margin: 0, padding: 0, display: "flex", flexDirection: "column", gap: 2 }}>
            {STEPS.map((s, i) => (
              <li key={s} style={{ display: "flex", alignItems: "center", gap: 10, padding: "8px 10px", borderRadius: "var(--radius-sm)", background: i === step ? "var(--accent-tint)" : "transparent" }}>
                <span style={{ width: 22, height: 22, borderRadius: "50%", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 12, fontWeight: 600, flexShrink: 0,
                  background: i < step ? "var(--accent-fill)" : i === step ? "var(--surface)" : "var(--surface-sunken)",
                  color: i < step ? "var(--label-on-accent-fill)" : i === step ? "var(--accent-text)" : "var(--label-muted)",
                  border: i === step ? "1px solid var(--accent-graphic-strong)" : "none" }}>{i < step ? "✓" : i + 1}</span>
                <span style={{ fontSize: 14, fontWeight: i === step ? 600 : 400, color: i === step ? "var(--label-default)" : "var(--label-muted)" }}>{s}</span>
              </li>
            ))}
          </ol>
        </aside>

        <div style={{ flex: 1, maxWidth: 560 }}>
          {step === 0 && (
            <Card title="Documents Received" subtitle="Uploaded before this session">
              <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
                {[["Auto_Application.pdf", "Parsed"], ["Home_Inspection_Report.pdf", "Parsed"], ["ID_Verification.jpg", "Parsed"]].map(([f, s]) => (
                  <div key={f} style={{ display: "flex", justifyContent: "space-between", alignItems: "center", padding: "10px 12px", background: "var(--surface-sunken)", borderRadius: "var(--radius-sm)" }}>
                    <span style={{ fontSize: 14, color: "var(--label-default)", fontFamily: "var(--font-mono)" }}>{f}</span>
                    <Badge tone="success">{s}</Badge>
                  </div>
                ))}
              </div>
              <p style={{ fontSize: 13, color: "var(--label-muted)", marginTop: 14 }}>Fields extracted from these documents are pre-filled below. Only missing fields need manual entry.</p>
            </Card>
          )}

          {step === 1 && (
            <Card title="Policyholder Information">
              <div style={{ display: "flex", flexDirection: "column", gap: 16 }}>
                <Field label="Full Name" tag={<DocBadge />}><TextInput value="Jane Alvarez" readOnly /></Field>
                <Field label="Date of Birth" tag={<DocBadge />}><TextInput value="04/12/1988" readOnly /></Field>
                <Field label="Mailing Address" tag={<DocBadge />}><TextInput value="118 Birchwood Ln, Austin, TX" readOnly /></Field>
                <Field label="Email Address" tag={<MissingBadge />}><TextInput placeholder="jane@example.com" missing /></Field>
                <Field label="Preferred Contact Method" tag={<MissingBadge />}><TextInput placeholder="Phone, email, or mail" missing /></Field>
              </div>
            </Card>
          )}

          {step === 2 && (
            <Card title="Auto Policy">
              <div style={{ display: "flex", flexDirection: "column", gap: 16 }}>
                <Field label="VIN" tag={<DocBadge />}><TextInput value="1HGCM82633A004352" readOnly /></Field>
                <Field label="Vehicle" tag={<DocBadge />}><TextInput value="2021 Honda Accord" readOnly /></Field>
                <Field label="Garaging Address" tag={<MissingBadge />}><TextInput placeholder="Confirm if different from mailing address" missing /></Field>
                <Field label="Lienholder" tag={<MissingBadge />}><TextInput placeholder="Bank / finance company, if financed" missing /></Field>
              </div>
            </Card>
          )}

          {step === 3 && (
            <Card title="Home Policy">
              <div style={{ display: "flex", flexDirection: "column", gap: 16 }}>
                <Field label="Property Address" tag={<DocBadge />}><TextInput value="118 Birchwood Ln, Austin, TX" readOnly /></Field>
                <Field label="Year Built" tag={<DocBadge />}><TextInput value="1994" readOnly /></Field>
                <Field label="Roof Condition" tag={<DocBadge />}><TextInput value="Good — replaced 2019" readOnly /></Field>
                <Field label="Mortgage Company" tag={<MissingBadge />}><TextInput placeholder="Required for escrow billing" missing /></Field>
              </div>
            </Card>
          )}

          {step === 4 && (
            <Card title="Review & Finalize" subtitle="Confirm before the record is created">
              <div style={{ display: "flex", flexDirection: "column", gap: 10, fontSize: 14 }}>
                <div style={{ display: "flex", justifyContent: "space-between" }}><span style={{ color: "var(--label-muted)" }}>Policyholder</span><span style={{ color: "var(--label-default)", fontWeight: 500 }}>Jane Alvarez</span></div>
                <div style={{ display: "flex", justifyContent: "space-between" }}><span style={{ color: "var(--label-muted)" }}>Lines of business</span><span><Badge tone="neutral">Auto</Badge> <Badge tone="neutral">Home</Badge></span></div>
                <div style={{ display: "flex", justifyContent: "space-between" }}><span style={{ color: "var(--label-muted)" }}>Fields completed</span><span style={{ color: "var(--success-text)", fontWeight: 600 }}>12 / 12</span></div>
              </div>
            </Card>
          )}

          <div style={{ display: "flex", justifyContent: "space-between", marginTop: 20 }}>
            <Button variant="secondary" disabled={step === 0} onClick={() => setStep((s) => Math.max(0, s - 1))}>Back</Button>
            {step < STEPS.length - 1
              ? <Button onClick={() => setStep((s) => Math.min(STEPS.length - 1, s + 1))}>Continue</Button>
              : <Button onClick={() => setConfirming(true)}>Finalize Customer</Button>}
          </div>

          {confirming && (
            <div onClick={() => setConfirming(false)} style={{ position: "fixed", inset: 0, background: "rgba(0,0,0,0.5)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 100 }}>
              <div onClick={(e) => e.stopPropagation()} style={{ background: "var(--surface)", borderRadius: "var(--radius-lg)", boxShadow: "var(--shadow-lg)", padding: 24, width: 360 }}>
                <h3 style={{ margin: "0 0 10px", fontSize: 18, color: "var(--label-default)" }}>Finalize Customer Record?</h3>
                <p style={{ fontSize: 13, color: "var(--label-muted)", margin: "0 0 18px" }}>This creates the customer and both policies. You can still edit the record afterward.</p>
                <div style={{ display: "flex", justifyContent: "flex-end", gap: 10 }}>
                  <Button variant="secondary" onClick={() => setConfirming(false)}>Cancel</Button>
                  <Button onClick={() => { setConfirming(false); onFinalize(); }}>Finalize</Button>
                </div>
              </div>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}

window.MosaicKit.OnboardingWizard = OnboardingWizard;
