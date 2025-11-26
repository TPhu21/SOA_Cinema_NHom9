// services/MovieService/server.js
require('dotenv').config();
const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const { sequelize } = require('./models'); // 👈 ĐÃ MỞ LẠI

const PORT = process.env.PORT || 4002;
const app = express();

app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// (Chúng ta sẽ thêm Routes vào đây sau)
 const movieRoutes = require('./routes/movies');
 app.use('/', movieRoutes); 

// ⭐️ ĐÃ MỞ LẠI PHẦN NÀY ⭐️
sequelize.sync({ alter: true })
  .then(() => {
    console.log('✅ (MovieService) Database synced (với cinemax_movies)');
    app.listen(PORT, () => {
      console.log(`🚀 MovieService (CON) đang chạy tại: http://localhost:${PORT}`);
    });
  })
  .catch(err => {
    console.error('❌ Lỗi DB (MovieService):', err);
  });