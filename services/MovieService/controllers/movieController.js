// services/MovieService/controllers/movieController.js
const { Movie, Showtime } = require('../models'); // Lấy model từ index.js
const { Op } = require('sequelize');

// API 1: Lấy TẤT CẢ phim
exports.getAllMovies = async (req, res) => {
    try {
        const movies = await Movie.findAll({
            // (Bạn có thể thêm 'include' Suất chiếu nếu muốn)
        });
        res.status(200).json(movies);
    } catch (error) {
        console.error("Lỗi getAllMovies:", error);
        res.status(500).json({ message: "Lỗi server (4002)", error: error.message });
    }
};

// API 2: Lấy Suất chiếu (Showtime) theo ID của Phim
exports.getShowtimesByMovie = async (req, res) => {
    try {
        const movieId = req.params.movieId;
        
        const showtimes = await Showtime.findAll({
            where: { 
                movieId: movieId,
                // (Bạn có thể thêm điều kiện lọc, ví dụ: startTime > new Date())
            }
        });
        
        res.status(200).json(showtimes);
    } catch (error) {
        console.error("Lỗi getShowtimesByMovie:", error);
        res.status(500).json({ message: "Lỗi server (4002)", error: error.message });
    }
};
exports.getScheduleByDate = async (req, res) => {
    try {
        const dateStr = req.query.date; // Ví dụ: "2025-11-18"
        
        if (!dateStr) {
            return res.status(400).json({ message: "Thiếu ngày (date)" });
        }

        // Tạo khoảng thời gian: Từ 00:00:00 đến 23:59:59 của ngày đó
        const startOfDay = new Date(dateStr);
        startOfDay.setHours(0, 0, 0, 0);
        
        const endOfDay = new Date(dateStr);
        endOfDay.setHours(23, 59, 59, 999);

        // Tìm suất chiếu trong khoảng đó, KÈM THEO thông tin Phim
        const showtimes = await Showtime.findAll({
            where: {
                startTime: {
                    [Op.between]: [startOfDay, endOfDay]
                }
            },
            include: [{
                model: Movie,
                as: 'movie' // Phải khớp với alias trong models/Showtime.js
            }],
            order: [['startTime', 'ASC']]
        });

        res.json(showtimes);

    } catch (error) {
        console.error("Lỗi getScheduleByDate:", error);
        res.status(500).json({ message: "Lỗi server" });
    }
};