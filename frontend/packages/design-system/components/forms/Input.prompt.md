Labeled text field for onboarding forms and record edits — always carries a visible `<label>`.

```jsx
<Input label="Policyholder Name" required value={name} onChange={e => setName(e.target.value)} />
<Input label="VIN" hint="17 characters" />
<Input label="Email" error="Enter a valid email address" />
```

`error` replaces `hint` and switches the border to `--danger-text`. Never render without `label` — unlabeled fields fail accessibility review.
