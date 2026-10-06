Full-width bordered grid — the system deliberately shows a visible grid rather than a borderless list, for claims history and policy lists.

```jsx
<Table columns={[{key:"id",label:"Claim"},{key:"status",label:"Status"}]} rows={[{id:"CLM-20931", status:<Badge tone="warning">Pending</Badge>}]} />
```
