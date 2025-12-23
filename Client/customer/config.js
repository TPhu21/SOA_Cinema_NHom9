// const config = {
//   API_URL: process.env.API_URL || 'http://localhost:4000/api'
// };

// export default config;


const API_BASE_URL = 'http://localhost:8080/api';

// (optional) Hàm tiện gọi API nếu bạn muốn dùng:
async function fetchJson(path, options = {}) {
    // path có thể là "cinemas" hoặc "/cinemas"
    if (!path.startsWith('/'))
        path = '/' + path;

    //Thiết lập Header Mặc định (Content-Type)
    const defaultHeaders = {};
    if (options.body && !options.headers?.['Content-Type']) {
        defaultHeaders['Content-Type'] = 'application/json';
    }
    //Gửi Yêu cầu và Xử lý Lỗi
    const response = await fetch(API_BASE_URL + path, {
        ...options,
        headers: {
            ...defaultHeaders,
            ...(options.headers || {})
        }
    });

    if (!response.ok) {
        let msg = 'Lỗi gọi API';
        try {
            const data = await response.json();
            if (data.message) msg = data.message;
        } catch (e) {}
        throw new Error(msg);
    }

    if (response.status === 204) return null;
    return response.json();

}

// Đảm bảo có thể dùng từ mọi script
window.API_BASE_URL = API_BASE_URL;
window.fetchJson = fetchJson;