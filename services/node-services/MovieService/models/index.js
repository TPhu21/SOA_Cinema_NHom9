// services/MovieService/models/index.js
'use strict';

const fs = require('fs');
const path = require('path');
const Sequelize = require('sequelize');
const process = require('process');
const basename = path.basename(__filename);

const db = {};

// ⭐️ SỬA LỖI Ở ĐÂY: Đọc thẳng từ .env (đã được nạp bởi server.js)
const config = {
  username: process.env.DB_USERNAME || 'root',
  password: process.env.DB_PASSWORD || null,
  database: process.env.DB_DATABASE || 'cinemax_movies', // 👈 Đọc đúng DB
  host: process.env.DB_HOST || '127.0.0.1',
  dialect: 'mysql',
  logging: false // Tắt log SQL cho gọn
};

let sequelize;
sequelize = new Sequelize(config.database, config.username, config.password, config);

// ĐỌC TẤT CẢ CÁC FILE MODEL (Movie.js, Showtime.js)
fs
  .readdirSync(__dirname)
  .filter(file => {
    return (
      file.indexOf('.') !== 0 &&
      file !== basename &&
      file.slice(-3) === '.js'
    );
  })
  .forEach(file => {
    const model = require(path.join(__dirname, file))(sequelize, Sequelize.DataTypes);
    db[model.name] = model;
  });

// KẾT NỐI (Associate)
Object.keys(db).forEach(modelName => {
  if (db[modelName].associate) {
    db[modelName].associate(db);
  }
});

db.sequelize = sequelize;
db.Sequelize = Sequelize;

module.exports = db;