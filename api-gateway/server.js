// api-gateway/server.js
const express = require('express');
const cors = require('cors');
const proxy = require('express-http-proxy');

const app = express();
app.use(cors());
app.use(express.json());

// --- 🔦 ĐÈN PIN SỐ 1 (Gắn ở Lễ Tân) 🔦 ---
app.use((req, res, next) => {
    console.log(`[LỄ TÂN 4000] 💡: Nhận ${req.method} tới ${req.originalUrl}`);
    next(); 
});
// ---------------------------------------------

// --- DANH SÁCH "CỬA HÀNG" ---
const userServiceHost = 'http://localhost:4001';
const movieServiceHost = 'http://localhost:4002';
const bookingServiceHost = 'http://localhost:4003';
const paymentServiceHost = 'http://localhost:4004';

// --- ĐIỀU PHỐI (Bản "Cắt Xén" - KHÔNG DÙNG proxyReqPathResolver) ---
app.use('/api/users', proxy(userServiceHost)); 
app.use('/api/bookings', proxy(bookingServiceHost)); 
app.use('/api/payments', proxy(paymentServiceHost)); 
app.use('/api/movies', proxy(movieServiceHost));

app.listen(4000, () => {
    console.log("=========================================================");
    console.log("🚀 API Gateway (LỄ TÂN) [Bản Cắt Xén] đang chạy tại: http://localhost:4000");
    console.log(`   - /api/users     -> ${userServiceHost}`);
    console.log(`   - /api/bookings  -> ${bookingServiceHost}`);
    console.log(`   - /api/payments  -> ${paymentServiceHost}`);
    console.log(`   - /api/movies    -> ${movieServiceHost}`);
    console.log("=========================================================");
});