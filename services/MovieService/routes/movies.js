// services/MovieService/routes/movies.js
const express = require('express');
const router = express.Router();
const movieController = require('../controllers/movieController');

// "Menu" của chúng ta:

// 1. Lấy tất cả phim
// (Sẽ được gọi bằng: GET /)
router.get('/', movieController.getAllMovies);

// 2. Lấy suất chiếu của 1 phim
// (Sẽ được gọi bằng: GET /showtimes/1)
router.get('/showtimes/:movieId', movieController.getShowtimesByMovie);
router.get('/schedule', movieController.getScheduleByDate);
module.exports = router;