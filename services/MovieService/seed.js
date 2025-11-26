// services/MovieService/seed.js
const { sequelize, Movie, Showtime } = require('./models');

const seedDatabase = async () => {
    console.log("Bắt đầu seeding (bỏ dữ liệu mồi) vào [cinemax_movies]...");
    
    try {
        // Xóa sạch và tạo lại bảng
        await sequelize.sync({ force: true });

        // 1. Tạo Phim 
        // (Chúng ta "ép" ID=1 để nó khớp với demoMovies[0] trong DatVe.html)
        const movie1 = await Movie.create({
            id: 1, // 👈 Ép ID
            title: "AVENGERS: ENDGAME",
            description: "Phần cuối của loạt phim Avengers.",
            posterUrl: "https://example.com/poster.jpg", // (Bạn có thể đổi link ảnh)
            duration: 181
        });

        // 2. Tạo 2 Suất Chiếu cho phim đó (ID 1)
        await Showtime.create({
            startTime: new Date('2025-11-18T19:00:00'), // Giả sử 7h tối nay
            roomId: "PHONG_01",
            price: 85000,
            movieId: movie1.id // 👈 Liên kết với phim ID 1
        });
        
        await Showtime.create({
            startTime: new Date('2025-11-18T21:00:00'), // Giả sử 9h tối nay
            roomId: "PHONG_02",
            price: 95000,
            movieId: movie1.id // 👈 Liên kết với phim ID 1
        });
        
        console.log("✅ Seed database (cinemax_movies) thành công!");

    } catch (error) {
        console.error("❌ Lỗi seeding database:", error);
    } finally {
        await sequelize.close(); // Đóng kết nối
    }
};

seedDatabase();