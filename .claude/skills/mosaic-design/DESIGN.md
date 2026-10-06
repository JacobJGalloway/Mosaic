# Mosaic — DESIGN.md (handoff draft)

Design tokens and conventions for the Mosaic internal platform (insurance customer onboarding + policy/claims records). Source of truth: `tokens/colors.css`, `tokens/typography.css`, `tokens/spacing.css` in the design system project — this doc restates them for the SCSS setup.

## Scope
**Desktop only.** No breakpoints defined. A future reduced screen subset (notifications/escalations, read-focused) may get tablet/mobile treatment later, but won't be a fully functional parallel UI — don't build responsive infrastructure speculatively.

## Naming convention
SCSS variables keep the **same names as the CSS custom properties**, e.g. `$emerald-600`, `$accent-fill`, `$label-default`. Preserves searchability between design docs and code; traded off against onboarding speed for a cold engineer (flag this as a known tradeoff, not a gap to silently fix).

## Icons
**Font Awesome**, default/solid or regular style (not picked yet — pick one and apply consistently; don't mix weights). No custom icon work for this pass.

## Color
Single hue (158°, emerald) across brand ramp + emerald-tinted neutrals. Light/Dark shift lightness only, hue is constant. Status colors (amber/red/blue) are deliberately off-hue from brand so "success" ≠ "brand".

Brand ramp (OKLCH `L C 158`, hex is sRGB approximation):
```
emerald-50   oklch(0.97  0.012 158)  #e9f3ee
emerald-100  oklch(0.94  0.022 158)  #d6ecdf
emerald-200  oklch(0.86  0.045 158)  #b9dac6
emerald-300  oklch(0.76  0.075 158)  #88c09f
emerald-400  oklch(0.665 0.105 158)  #56a77b
emerald-500  oklch(0.595 0.135 158)  #12965f
emerald-600  oklch(0.515 0.145 158)  #007f45
emerald-700  oklch(0.435 0.135 158)  #006632
emerald-750  oklch(0.405 0.125 158)  (accent-text, light mode)
emerald-800  oklch(0.355 0.11  158)
emerald-900  oklch(0.27  0.08  158)
emerald-950  oklch(0.195 0.055 158)
```
Full neutral ramp, status hues (amber/red/blue), and semantic Light/Dark tokens: `tokens/colors.css` (copy verbatim into `_tokens.scss` as `$`-prefixed vars — don't re-derive).

**Accessibility:** every text/background and icon/background pair in `tokens/colors.css` was computed and documented in `guidelines/colors-contrast-table.html` — AA minimum, most body/status text at AAA (7:1+). Preserve these pairs; don't substitute a token for "a similar green" without re-checking contrast.

## Typography
- UI font: **Public Sans** (400/500/600/700) — all headings, labels, body.
- Data font: **Roboto Mono** (400/500) — IDs, policy numbers, dollar amounts, dates.
- Scale: `--text-xs` 12px → `--text-3xl` 39px, 1.25 ratio. Line-heights: tight 1.2 (headings), normal 1.5 (body), relaxed 1.65 (long-form).

## Spacing / radii
4px base grid, 0–96px (`tokens/spacing.css`). Radii: 6px inputs/buttons, 8px cards, 12px modals, pill badges. Shadows: translucent black only (no color tint), sm/md/lg weights.

## Components built so far
Forms: Button (4 variants), Input, Select, Checkbox, Radio, Switch — all in `components/forms/`.
Feedback: Badge (5 tones), Dialog — `components/feedback/`.
Data: Card, Table — `components/data/`.
Navigation: Tabs, NavSidebar — `components/navigation/`.
Each has a `.jsx` reference impl, `.d.ts` prop shape, and `.prompt.md` usage note — pull prop shapes from there rather than re-guessing them.

## Reference screens
`ui_kits/mosaic-app/index.html` — click-through: Dashboard (ingestion/claims charts), Customers (searchable list — name, policy number, claim ID/description), Customer Detail (Auto/Home tabs, claims history), Onboarding Wizard (document-aware, multi-step, "From Document" vs "Missing — required" badges). Light/Dark toggle included.

## Brand assets
`assets/` — logo mark (dark-mode PNG done; light-mode mark still has a live-text "M" layer pending path conversion — treat as known cleanup, not a bug, until replaced), wordmark SVGs for light/dark (`mosaic-text-light.svg`, `mosaic-text-dark.svg`, outlined paths, portable). No name/company text baked permanently next to the mark — wordmark is its own asset, placed per-context (nav needs icon-only at small scale).

## Open items (tracked, not blocking)
- Font Awesome icon weight (solid vs regular) — pick one, apply consistently.
- Logo light-mode "M" layer — convert text to path to match the wordmark treatment.
- Possible future "Claim Alerts" column on Customers table — deferred, no spec yet.
