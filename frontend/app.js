/* ===================================================================
   app.js - Shared utilities for the Skill Exchange Platform
   =================================================================== */

const API_BASE = 'https://skillexchange-1-6m3j.onrender.com';

// ── Token Management ──

function getToken() {
    return localStorage.getItem('jwt_token');
}

function setToken(token) {
    localStorage.setItem('jwt_token', token);
}

function removeToken() {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_email');
}

function logout() {
    removeToken();
    window.location.href = 'index.html';
}

/**
 * Check if token is expired by decoding the JWT payload.
 * JWT payload contains an "exp" field (seconds since epoch).
 */
function isTokenExpired() {
    const token = getToken();
    if (!token) return true;

    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const expiry = payload.exp * 1000; // convert to ms
        return Date.now() >= expiry;
    } catch (e) {
        return true; // malformed token
    }
}

/**
 * Protect page — redirect to login if no token or token expired.
 * Shows a toast if session expired (vs never logged in).
 */
function requireAuth() {
    const token = getToken();

    if (!token) {
        window.location.href = 'index.html';
        return;
    }

    if (isTokenExpired()) {
        removeToken();
        sessionStorage.setItem('session_expired', 'true');
        window.location.href = 'index.html';
        return;
    }

    // Check expiry every 30 seconds while on the page
    setInterval(() => {
        if (isTokenExpired()) {
            removeToken();
            sessionStorage.setItem('session_expired', 'true');
            window.location.href = 'index.html';
        }
    }, 30000);
}

/**
 * Get current user info from JWT token.
 * Returns { email, name, exp }
 */
function getCurrentUser() {
    const token = getToken();
    if (!token) return null;

    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const email = payload.sub || '';
        const name = email.split('@')[0];
        const displayName = name.charAt(0).toUpperCase() + name.slice(1);

        return {
            email: email,
            name: displayName,
            initial: name.charAt(0).toUpperCase(),
            exp: new Date(payload.exp * 1000)
        };
    } catch (e) {
        return null;
    }
}

// ── API Helper ──

async function apiCall(endpoint, method = 'GET', body = null) {
    const headers = { 'Content-Type': 'application/json' };
    const token = getToken();

    if (token) {
        headers['Authorization'] = 'Bearer ' + token;
    }

    const options = { method, headers };

    if (body) {
        options.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(API_BASE + endpoint, options);

        // Only treat 401/403 as session problems if a token already exists
        if ((response.status === 401 || response.status === 403) && token) {
            if (isTokenExpired()) {
                removeToken();
                sessionStorage.setItem('session_expired', 'true');
                window.location.href = 'index.html';
                throw new Error('Session expired. Please login again.');
            }
        }

        let data = {};
        const contentType = response.headers.get('content-type') || '';

        if (contentType.includes('application/json')) {
            data = await response.json();
        } else {
            const text = await response.text();
            data = { message: text };
        }

        if (!response.ok) {
            throw new Error(data.error || data.message || 'Something went wrong');
        }

        return data;
    } catch (err) {
        if (err.message === 'Failed to fetch') {
            throw new Error('Cannot connect to server. Is the backend running?');
        }
        throw err;
    }
}

// ── Toast Notifications ──

function showToast(message, type = 'success') {
    document.querySelectorAll('.toast').forEach(t => t.remove());

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => {
        if (toast.parentNode) toast.remove();
    }, 3000);
}

// ── Skeleton Loading ──

/**
 * Generate skeleton loading placeholder HTML.
 * @param {number} count - number of skeleton cards
 * @param {string} type - 'card' | 'chat' | 'list'
 */
function showSkeleton(containerId, count = 3, type = 'card') {
    const container = document.getElementById(containerId);
    if (!container) return;

    let html = '';

    if (type === 'card') {
        html = '<div class="card-grid">';
        for (let i = 0; i < count; i++) {
            html += `
                <div class="skeleton-card">
                    <div class="skeleton skeleton-line" style="width: 60%; height: 20px; margin-bottom: 12px;"></div>
                    <div class="skeleton skeleton-line" style="width: 100%; height: 14px; margin-bottom: 8px;"></div>
                    <div class="skeleton skeleton-line" style="width: 80%; height: 14px; margin-bottom: 16px;"></div>
                    <div class="skeleton skeleton-line" style="width: 40%; height: 12px;"></div>
                </div>
            `;
        }
        html += '</div>';
    } else if (type === 'chat') {
        for (let i = 0; i < count; i++) {
            const isLeft = i % 2 === 0;
            html += `
                <div style="display:flex; ${isLeft ? '' : 'justify-content:flex-end;'} margin-bottom: 12px;">
                    <div class="skeleton" style="width: ${40 + Math.random() * 30}%; height: 48px; border-radius: 18px;"></div>
                </div>
            `;
        }
    } else if (type === 'list') {
        for (let i = 0; i < count; i++) {
            html += `
                <div class="skeleton-card" style="margin-bottom: 10px;">
                    <div class="skeleton skeleton-line" style="width: 50%; height: 18px; margin-bottom: 10px;"></div>
                    <div class="skeleton skeleton-line" style="width: 90%; height: 13px; margin-bottom: 6px;"></div>
                    <div class="skeleton skeleton-line" style="width: 30%; height: 13px;"></div>
                </div>
            `;
        }
    }

    container.innerHTML = html;
}

/**
 * Show an empty state with icon and message.
 */
function showEmptyState(containerId, icon, title, subtitle = '') {
    const container = document.getElementById(containerId);
    if (!container) return;

    container.innerHTML = `
        <div class="empty-state">
            <div class="empty-icon">${icon}</div>
            <h3 class="empty-title">${title}</h3>
            ${subtitle ? `<p class="empty-subtitle">${subtitle}</p>` : ''}
        </div>
    `;
}

// ── HTML Escape ──
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// ── Navbar Builder ──

function buildNavbar(activePage) {
    const nav = document.getElementById('navbar');
    if (!nav) return;

    const user = getCurrentUser();
    const initial = user ? user.initial : '?';

    nav.innerHTML = `
        <a href="dashboard.html" class="navbar-brand">
            ⚡ <span>SkillExchange</span>
        </a>
        <ul class="nav-links">
            <li><a href="dashboard.html" class="${activePage === 'dashboard' ? 'active' : ''}">🏠 <span>Home</span></a></li>
            <li><a href="add-skill.html" class="${activePage === 'add-skill' ? 'active' : ''}">➕ <span>Add Skill</span></a></li>
            <li><a href="search.html" class="${activePage === 'search' ? 'active' : ''}">🔍 <span>Search</span></a></li>
            <li><a href="requests.html" class="${activePage === 'requests' ? 'active' : ''}">📋 <span>Requests</span></a></li>
            <li><a href="chat.html" class="${activePage === 'chat' ? 'active' : ''}">💬 <span>Chat</span></a></li>
            <li><a href="feedback.html" class="${activePage === 'feedback' ? 'active' : ''}">⭐ <span>Feedback</span></a></li>
            <li>
                <a href="profile.html" class="nav-profile ${activePage === 'profile' ? 'active' : ''}">
                    <span class="nav-avatar">${initial}</span>
                </a>
            </li>
            <li><a href="#" class="btn-logout" onclick="logout(); return false;">↪ Logout</a></li>
        </ul>
    `;
}