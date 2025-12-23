/**
 * Authentication Module - Shared giữa Customer và Backoffice
 */

const AUTH_CONFIG = {
    API_BASE: 'http://localhost:8080',
    TOKEN_KEY: 'cinemax_token',
    USER_KEY: 'cinemax_user',
    ROLE_KEY: 'cinemax_role'
};

// Roles
const ROLES = {
    ADMIN: 'ADMIN',
    STAFF: 'STAFF',
    CUSTOMER: 'CUSTOMER'
};

/**
 * Lưu thông tin đăng nhập
 */
function saveAuth(token, user) {
    localStorage.setItem(AUTH_CONFIG.TOKEN_KEY, token);
    localStorage.setItem(AUTH_CONFIG.USER_KEY, JSON.stringify(user));
    localStorage.setItem(AUTH_CONFIG.ROLE_KEY, user.role || ROLES.CUSTOMER);
}

/**
 * Lấy token
 */
function getToken() {
    return localStorage.getItem(AUTH_CONFIG.TOKEN_KEY);
}

/**
 * Lấy thông tin user
 */
function getUser() {
    const userStr = localStorage.getItem(AUTH_CONFIG.USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
}

/**
 * Lấy role
 */
function getRole() {
    return localStorage.getItem(AUTH_CONFIG.ROLE_KEY) || null;
}

/**
 * Kiểm tra đã đăng nhập chưa
 */
function isAuthenticated() {
    return !!getToken();
}

/**
 * Kiểm tra có quyền truy cập không
 */
function hasRole(...allowedRoles) {
    const role = getRole();
    return allowedRoles.includes(role);
}

/**
 * Đăng xuất
 */
function logout() {
    localStorage.removeItem(AUTH_CONFIG.TOKEN_KEY);
    localStorage.removeItem(AUTH_CONFIG.USER_KEY);
    localStorage.removeItem(AUTH_CONFIG.ROLE_KEY);
}

/**
 * Redirect theo role sau khi đăng nhập backoffice
 */
function redirectByRole() {
    const role = getRole();
    
    switch (role) {
        case ROLES.ADMIN:
            window.location.href = 'dashboard.html';
            break;
        case ROLES.STAFF:
            window.location.href = 'staff/checkin.html';
            break;
        default:
            // Không có quyền vào backoffice
            logout();
            window.location.href = 'login.html?error=unauthorized';
    }
}

/**
 * Kiểm tra quyền truy cập trang backoffice
 * Gọi ở đầu mỗi trang cần bảo vệ
 */
function requireAuth(...allowedRoles) {
    if (!isAuthenticated()) {
        window.location.href = '../login.html?redirect=' + encodeURIComponent(window.location.href);
        return false;
    }
    
    if (allowedRoles.length > 0 && !hasRole(...allowedRoles)) {
        window.location.href = '../login.html?error=forbidden';
        return false;
    }
    
    return true;
}

/**
 * API call với auth header
 */
async function authFetch(url, options = {}) {
    const token = getToken();
    
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };
    
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    
    const response = await fetch(AUTH_CONFIG.API_BASE + url, {
        ...options,
        headers
    });
    
    // Token hết hạn
    if (response.status === 401) {
        logout();
        window.location.href = '/backoffice/login.html?error=session_expired';
        return null;
    }
    
    return response;
}

// Export for module usage
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        AUTH_CONFIG, ROLES,
        saveAuth, getToken, getUser, getRole,
        isAuthenticated, hasRole, logout,
        redirectByRole, requireAuth, authFetch
    };
}
