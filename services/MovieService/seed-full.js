// services/MovieService/seed-full.js
const axios = require('axios');
const { sequelize, Movie, Showtime } = require('./models');

const OMDB_KEY = 'fa85c569'; // Key của bạn
const MOVIE_TITLES = [
    "The Avengers",
    "Iron Man",
    "Thor",
    "Captain America",
    "Black Panther",
    "Spider-Man: No Way Home",
    "Doctor Strange",
    "Guardians of the Galaxy",
    "Avatar",
    "Titanic"
];

const seed = async () => {
    console.log("🔄 Đang xóa dữ liệu cũ và nhập phim mới từ OMDb...");
    
    try {
        await sequelize.sync({ force: true }); // Xóa sạch DB cũ

        for (const title of MOVIE_TITLES) {
            // 1. Gọi OMDb lấy thông tin
            const url = `http://www.omdbapi.com/?t=${encodeURIComponent(title)}&apikey=${OMDB_KEY}`;
            const res = await axios.get(url);
            const data = res.data;

            if (data.Response === "True") {
                // 2. Lưu vào DB của mình
                const duration = parseInt(data.Runtime) || 120; // Lấy số phút
                
                const movie = await Movie.create({
                    title: data.Title,
                    description: data.Plot,
                    posterUrl: data.Poster !== 'N/A' ? data.Poster : 'https://via.placeholder.com/300',
                    duration: duration
                });

                console.log(`✅ Đã nhập: ${movie.title}`);

                // 3. Tạo 3 suất chiếu cho phim này (Hôm nay và Ngày mai)
                await Showtime.create({
                    startTime: new Date(new Date().setHours(9, 0, 0, 0)), // 9h sáng nay
                    roomId: "Rạp 1", price: 75000, movieId: movie.id
                });
                await Showtime.create({
                    startTime: new Date(new Date().setHours(19, 30, 0, 0)), // 7h30 tối nay
                    roomId: "Rạp 2", price: 95000, movieId: movie.id
                });
                 await Showtime.create({
                    startTime: new Date(new Date().setDate(new Date().getDate() + 1)), // Mai
                    roomId: "Rạp 3", price: 85000, movieId: movie.id
                });
            } else {
                console.log(`⚠️ Không tìm thấy phim: ${title}`);
            }
        }

        console.log("🎉 HOÀN TẤT! Database đã đầy ắp phim.");

    } catch (error) {
        console.error("Lỗi:", error);
    } finally {
        await sequelize.close();
    }
};

seed();