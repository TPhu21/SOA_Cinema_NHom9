// services/PaymentService/server.js
require('dotenv').config();
const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const { sequelize } = require('./models');

const PORT = process.env.PORT || 4004;
const app = express();

app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// --- 🔦 ĐÈN PIN SỐ 4 (Gắn ở Cửa Hàng Payment) 🔦 ---
app.use((req, res, next) => {
    console.log(`[PAYMENT SVC 4004] 💡: Nhận ${req.method} tới ${req.originalUrl}`);
    next(); 
});
// ---------------------------------------------

// ⭐️ ĐÃ SỬA: Lắng nghe ở gốc '/'
const paymentRoutes = require('./routes/payments');
app.use('/', paymentRoutes); 

sequelize.sync({ alter: true })
  .then(() => {
    console.log('✅ (PaymentService) Database synced');
    app.listen(PORT, () => {
      console.log(`🚀 PaymentService (CON) đang chạy tại: http://localhost:${PORT}`);
    });
  })
  .catch(err => {
    console.error('❌ Lỗi DB (PaymentService):', err);
  });