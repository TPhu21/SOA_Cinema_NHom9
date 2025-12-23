(function () {
    const API_BASE_URL = "http://localhost:8080/api";
    const TOKEN_KEYS = ["cinemax_token", "token", "adminToken"];

    function getToken() {
        for (const k of TOKEN_KEYS) {
            const v = localStorage.getItem(k);
            if (v) return v;
        }
        return null;
    }

    function setToken(token) {
        localStorage.setItem("cinemax_token", token);
        localStorage.setItem("token", token); // giữ tương thích code cũ
    }

    function setUser(user) {
        localStorage.setItem("cinemax_user", JSON.stringify(user));
        localStorage.setItem("user", JSON.stringify(user));
    }

    async function apiFetch(path, options = {}) {
        if (!path.startsWith("/")) path = "/" + path;

        const headers = { ...(options.headers || {}) };

        if (options.body && !headers["Content-Type"]) {
            headers["Content-Type"] = "application/json";
        }

        const token = getToken();
        if (token && !headers["Authorization"]) {
            headers["Authorization"] = "Bearer " + token;
        }

        const res = await fetch(API_BASE_URL + path, { ...options, headers });

        if (!res.ok) {
            let msg = `HTTP ${res.status}`;
            try {
                const data = await res.json();
                msg = data.message || data.error || msg;
            } catch (_) {}
            throw new Error(msg);
        }

        if (res.status === 204) return null;
        return res.json();
    }

    window.API_BASE_URL = API_BASE_URL;
    window.apiFetch = apiFetch;
    window.getToken = getToken;
    window.setToken = setToken;
    window.setUser = setUser;
})();
