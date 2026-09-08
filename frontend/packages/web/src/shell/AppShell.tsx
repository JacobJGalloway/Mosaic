import { useState } from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuthStore } from '@mosaic/shared';
import './AppShell.css';

interface NavItem {
  label: string;
  icon: string;
  to: string;
}

// Only Dashboard exists this sprint — Clients/Policies views are Sprint 3
// scope (ARCHITECTURE.md). Extend this list as real routes land rather
// than linking to pages that don't exist yet.
const NAV_ITEMS: NavItem[] = [{ label: 'Dashboard', icon: '▦', to: '/dashboard' }];

export function AppShell() {
  const [mobileNavOpen, setMobileNavOpen] = useState(false);
  const logout = useAuthStore((s) => s.logout);
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/login');
  }

  return (
    <div className="shell-container">
      {mobileNavOpen && (
        <div className="shell-backdrop" onClick={() => setMobileNavOpen(false)} />
      )}

      <nav className={`shell-sidenav${mobileNavOpen ? ' open' : ''}`} aria-label="Main navigation">
        <div className="sidenav-header">
          <span className="sidenav-header-label">Mosaic</span>
        </div>
        <ul className="nav-list">
          {NAV_ITEMS.map((item) => (
            <li key={item.to}>
              <NavLink
                to={item.to}
                className={({ isActive }) => `nav-item${isActive ? ' active' : ''}`}
                onClick={() => setMobileNavOpen(false)}
              >
                <span className="nav-item-icon" aria-hidden="true">{item.icon}</span>
                <span className="nav-item-label">{item.label}</span>
              </NavLink>
            </li>
          ))}
        </ul>
      </nav>

      <div className="shell-content">
        <header className="shell-toolbar">
          <button
            className="hamburger-toggle"
            aria-label="Toggle navigation"
            onClick={() => setMobileNavOpen((open) => !open)}
          >
            &#9776;
          </button>
          <span />
          <button className="logout-button" onClick={handleLogout}>
            Log out
          </button>
        </header>
        <main className="shell-main">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
