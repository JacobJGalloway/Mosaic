Immediate-effect toggle (e.g. "autopay enabled") — unlike Checkbox, implies the change applies instantly, not on form submit.

```jsx
<Switch label="Autopay enabled" checked={autopay} onChange={e => setAutopay(e.target.checked)} />
```
