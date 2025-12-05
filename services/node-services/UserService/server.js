// services/UserService/server.js
require('dotenv').config();
const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const { sequelize } = require('./models');

const PORT = process.env.PORT || 4001; 
const app = express();

app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// --- 🔦 ĐÈN PIN SỐ 2 (Gắn ở Cửa Hàng User) 🔦 ---
app.use((req, res, next) => {
    console.log(`[USER SVC 4001] 💡: Nhận ${req.method} tới ${req.originalUrl}`);
    next(); 
});
// ---------------------------------------------

// ⭐️ ĐÃ SỬA: Lắng nghe ở gốc '/'
const userRoutes = require('./routes/users');
app.use('/', userRoutes); 

sequelize.sync({ alter: true })
  .then(() => {
    console.log('✅ (UserService) Database synced');
    app.listen(PORT, () => {
      console.log(`🚀 UserService (CON) đang chạy tại: http://localhost:${PORT}`);
    });
  })
  .catch(err => {
    console.error('❌ Lỗi DB (UserService):', err);
  });