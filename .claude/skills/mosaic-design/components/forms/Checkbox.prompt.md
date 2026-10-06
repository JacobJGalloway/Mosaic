Single boolean toggle with inline label (consent, "has both policies").

```jsx
<Checkbox label="Customer also holds a home policy" checked={hasHome} onChange={e => setHasHome(e.target.checked)} />
```
