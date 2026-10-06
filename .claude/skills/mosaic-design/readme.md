# Mosaic — Design System

An insurance-company internal platform: onboarding new customers and maintaining their current record — policy data plus claims history — for customers who hold an **auto policy, a home policy, or both**.

No codebase, Figma, or brand assets were provided. Everything here is built from the brief: an emerald-green palette, Light + Dark modes, and a customer-onboarding/record workflow. **No logo was supplied** — the wordmark renders as plain type ("Mosaic" in Public Sans 600) until real assets arrive.

---

## Content Fundamentals

**Tone.** Direct, functional. No exclamation marks, no sparkle — written by someone who processes claims, not someone selling the product.

**Casing.** Title Case for page titles and primary actions (`Start Onboarding`, `Finalize Customer`, `File Claim`). Sentence case for secondary copy, table cells, and hints.

**Voice.** Names things rather than addressing the user ("Customers", "Onboarding", "Claims History"). Imperative for instructions ("Confirm if different from mailing address").

**Emoji.** Not used.

**Placeholders.** Em dash `—` for empty table cells / no data ("No claims filed").

**Document-extraction language.** Two states recur throughout onboarding: **"From Document"** (an info-toned badge marking a field auto-filled from an uploaded document) and **"Missing — required"** (a warning-toned badge marking a field the document set didn't cover). This distinction is the core of the onboarding screen — the agent's attention goes to the second group only.

---

## Visual Foundations

**Color.** One hue (158°, emerald) runs through the entire system — brand ramp, neutrals, icons, borders, fills. Light and Dark modes shift lightness only; the hue never moves, so emerald reads as the same color in both modes. Status colors (success / warning / danger / info) use **separate hues** (amber, red, blue) from the brand green, so "success" and "brand" stay distinguishable signals even though both happen to be green-adjacent in casual description.

**Accessibility.** Every foreground/background pairing in the system was computed (OKLCH → sRGB → WCAG relative luminance) and tuned to clear 4.5:1 (AA text), with most body/label and status pairs pushed to 7:1+ (AAA). Structural borders (input outlines) clear 3:1 per non-text contrast. Full ratio table: `guidelines/colors-contrast-table.html`.

**Type.** Public Sans for all UI text (headings, body, labels) — a neutral grotesque that holds up at small sizes in dense tables. Roboto Mono for IDs, policy numbers, dollar amounts, and dates — anything meant to be scanned or compared.

**Spacing.** 4px base grid, 0–96.

**Radii.** 6px inputs/buttons, 8px cards, 12px modals, pill badges.

**Shadows.** Translucent black only, no color tint — sm/md/lg weights, deeper opacity in Dark mode.

**Backgrounds.** Flat. No gradients, no textures, no imagery.

**Cards.** Surface + hairline border + optional shadow on hover. No colored left-border accents.

**Borders.** Hairline default (`--border`); a stronger token (`--border-input`) on form fields and anything structurally load-bearing, tuned to meet 3:1 against its background in both modes.

**Hover / focus / press.** Hover deepens border/background subtly. Focus is a 2px emerald ring with offset. Press darkens fill — no scale/shrink.

**Animation.** Minimal — 0.18s ease transitions on color/border/background only. No slides, no bounces.

---

## Iconography

No icon set was specified in the brief and no codebase was available to source one from. The UI kit avoids icons entirely for this pass — status is communicated through the Badge component's tone + label instead of a glyph. If icons are wanted later, Lucide (outline, stroke-width 2) is a safe default that will sit cleanly on this type system — flag before adopting it as a substitution.

---

## Index

- `styles.css` — root import list (link this one file)
- `tokens/colors.css` — emerald ramp, emerald-tinted neutrals, status hues, Light/Dark semantic tokens
- `tokens/typography.css` — Public Sans + Roboto Mono, type scale (Google Fonts import)
- `tokens/spacing.css` — spacing scale, radii
- `guidelines/` — 9 foundation specimen cards (Colors ×4 incl. contrast table, Type ×2, Spacing ×2, Brand ×1)
- `components/forms/` — Button, Input, Select, Checkbox, Radio, Switch
- `components/feedback/` — Badge, Dialog
- `components/data/` — Card, Table
- `components/navigation/` — Tabs, NavSidebar
- `ui_kits/mosaic-app/` — Dashboard (customer list), Onboarding Wizard (document-aware, multi-step), Customer Detail (Auto/Home tabs + claims history) — `index.html` is click-through with a Light/Dark toggle

### Intentional additions
No source defined a component inventory, so the standard B2B set was built sized to this workflow: Button, Input, Select, Checkbox, Radio, Switch, Badge, Dialog, Card, Table, Tabs, NavSidebar. Tooltip, Toast, and IconButton were left out — nothing in the three example screens needed them yet.
