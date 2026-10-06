Primary action trigger for forms, toolbars, and record actions — use when the user commits to a single action.

```jsx
<Button variant="primary" onClick={startOnboarding}>Start Onboarding</Button>
<Button variant="secondary" size="sm">Cancel</Button>
<Button variant="danger" ariaLabel="Deny claim">Deny Claim</Button>
```

Variants: `primary` (solid emerald fill, the one CTA per view), `secondary` (outlined, default action density), `ghost` (text-only, lowest emphasis, use inside table rows/toolbars), `danger` (destructive — deny/deactivate). Sizes: `sm`/`md`/`lg`. Always pass `ariaLabel` when children is icon-only.
