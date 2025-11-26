const API_BASE = "http://localhost:3000/api";
const loginSection = document.getElementById("login-section");
const dashboardSection = document.getElementById("dashboard-section");
const loginBtn = document.getElementById("login-btn");
const logoutBtn = document.getElementById("logout-btn");
const loginError = document.getElementById("login-error");

// 🔹 Kiểm tra token có sẵn
document.addEventListener("DOMContentLoaded", () => {
  const token = localStorage.getItem("token");
  if (token) {
    showDashboard();
    loadUsers();
  }
});

// 🔹 Xử lý đăng nhập
loginBtn.addEventListener("click", async () => {
  const username = document.getElementById("username").value.trim();
  const password = document.getElementById("password").value.trim();

  try {
    const res = await fetch(`${API_BASE}/users/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password }),
    });
    const data = await res.json();

    if (data.token) {
      localStorage.setItem("token", data.token);
      showDashboard();
      loadUsers();
    } else {
      loginError.textContent = "Sai tên đăng nhập hoặc mật khẩu!";
    }
  } catch (err) {
    loginError.textContent = "Lỗi kết nối server!";
  }
});

// 🔹 Đăng xuất
logoutBtn.addEventListener("click", () => {
  localStorage.removeItem("token");
  loginSection.classList.remove("hidden");
  dashboardSection.classList.add("hidden");
});

// 🔹 Hiển thị Dashboard
function showDashboard() {
  loginSection.classList.add("hidden");
  dashboardSection.classList.remove("hidden");
}

// 🔹 Load danh sách người dùng
async function loadUsers() {
  try {
    const token = localStorage.getItem("token");
    const res = await fetch(`${API_BASE}/users`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    const users = await res.json();
    const tbody = document.querySelector("#user-table tbody");
    tbody.innerHTML = "";
    users.forEach(u => {
      const tr = document.createElement("tr");
      tr.innerHTML = `<td>${u.id}</td><td>${u.username}</td><td>${u.email}</td>`;
      tbody.appendChild(tr);
    });
  } catch (err) {
    console.error("Lỗi tải user:", err);
  }
}

// 🔹 Tìm kiếm phim qua backend proxy (hoặc trực tiếp OMDb)
document.getElementById("search-btn").addEventListener("click", async () => {
  const query = document.getElementById("movie-search").value.trim();
  if (!query) return;
  try {
    const res = await fetch(`${API_BASE}/movies/search?query=${encodeURIComponent(query)}`);
    const data = await res.json();
    displayMovies(data.Search || []);
  } catch (err) {
    console.error(err);
  }
});

function displayMovies(movies) {
  const container = document.getElementById("movies");
  container.innerHTML = "";
  if (!movies.length) {
    container.innerHTML = "<p>Không tìm thấy phim nào.</p>";
    return;
  }
  movies.forEach(m => {
    const card = document.createElement("div");
    card.className = "movie-card";
    card.innerHTML = `
      <img src="${m.Poster}" alt="${m.Title}">
      <h4>${m.Title}</h4>
      <p>${m.Year}</p>
    `;
    container.appendChild(card);
  });
}
