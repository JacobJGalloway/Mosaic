Modal for confirmations that stop the flow (finalize customer, deny claim).

```jsx
<Dialog open={confirming} title="Finalize Customer Record?" onClose={() => setConfirming(false)}
  actions={<><Button variant="secondary" onClick={() => setConfirming(false)}>Cancel</Button><Button onClick={finalize}>Finalize</Button></>}>
  This locks the auto and home policy data entered in this session.
</Dialog>
```
