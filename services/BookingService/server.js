// services/BookingService/server.js
require('dotenv').config();
const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const { sequelize } = require('./models');

const PORT = process.env.PORT || 4003;
const app = express();

app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// --- 🔦 ĐÈN PIN SỐ 3 (Gắn ở Cửa Hàng Booking) 🔦 ---
app.use((req, res, next) => {
    console.log(`[BOOKING SVC 4003] 💡: Nhận ${req.method} tới ${req.originalUrl}`);
    next(); 
});
// ---------------------------------------------

// ⭐️ ĐÃ SỬA: Lắng nghe ở gốc '/'
const bookingRoutes = require('./routes/bookings');
app.use('/', bookingRoutes);          

sequelize.sync({ alter: true })
  .then(() => {
    console.log('✅ (BookingService) Database synced');
    app.listen(PORT, () => {
      console.log(`🚀 BookingService (CON) đang chạy tại: http://localhost:${PORT}`);
    });
  })
  .catch(err => {
    console.error('❌ Lỗi DB (BookingService):', err);
  });