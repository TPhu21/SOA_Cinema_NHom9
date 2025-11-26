// services/BookingService/controllers/bookingController.js
const bookingService = require('../services/bookingService');
const { Booking } = require('../models');
const Sequelize = require('sequelize'); // 👈 BẠN ĐANG THIẾU DÒNG NÀY
const Op = Sequelize.Op; 

// 1. API cho Client (5173) gọi (để tạo đơn)
exports.createBooking = async (req, res) => {
    console.log("[CONTROLLER 4003] 💡: Đã vào hàm createBooking.");
    try {
        const userId = null; 
        const ipAddr = req.headers['x-forwarded-for'] || req.connection.remoteAddress;
        
        console.log("[CONTROLLER 4003] 💡: Chuẩn bị gọi service (bookingService.js)...");
        const result = await bookingService.createBookingAndGetLink(userId, req.body, ipAddr);
        
        console.log("[CONTROLLER 4003] 💡: Service đã trả về, gửi JSON.");
        res.json(result); 
    } catch (error) {
        console.error("==== ☠️ LỖI TẠI BookingController (4003) ☠️ ====");
        console.error(error); 
        res.status(400).json({ message: error.message });
    }
};

// 2. API cho PaymentService (4004) gọi (để cập nhật)
exports.updateStatus = async (req, res) => {
    try {
        const { bookingId, status } = req.body;
        const booking = await Booking.findByPk(bookingId); 
        if (booking) {
            booking.status = status;
            await booking.save();
            res.status(200).json({ message: "Cập nhật thành công" });
        } else {
            res.status(404).json({ message: "Không tìm thấy booking" });
        }
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

// 3. API cho PaymentService (4004) gọi (để "hóng")
exports.getStatus = async (req, res) => {
    try {
        const booking = await Booking.findByPk(req.params.bookingId);
        if (booking) {
            res.status(200).json({ status: booking.status });
        } else {
            res.status(404).json({ message: "Không tìm thấy booking" });
        }
    } catch (error) {
        console.error("☠️ LỖI SẬP SERVER 4003 TẠI HÀM getStatus:", error.message);
        res.status(500).json({ message: error.message });
    }
};

// 4. API Lấy Ghế Đã Đặt (Cho Client render)
exports.getOccupiedSeats = async (req, res) => {
    try {
        const showtimeId = req.params.showtimeId;
        
        const confirmedBookings = await Booking.findAll({
            where: {
                showtimeId: showtimeId,
                status: { [Op.in]: ['CONFIRMED', 'PENDING'] } // Dùng Op ở đây
            },
            attributes: ['seats']
        });

        let occupiedSeats = [];
        confirmedBookings.forEach(booking => {
            // Parse JSON string: "['A1', 'A2']" -> ['A1', 'A2']
            let seatsArray = [];
            try {
                // Kiểm tra nếu dữ liệu đã là array hoặc string
                seatsArray = typeof booking.seats === 'string' ? JSON.parse(booking.seats) : booking.seats;
            } catch (e) {
                seatsArray = booking.seats;
            }
            occupiedSeats = occupiedSeats.concat(seatsArray);
        });

        console.log(`[CONTROLLER 4003] 💡: Đã trả về ${occupiedSeats.length} ghế đã đặt cho ID ${showtimeId}.`);
        res.json({ occupiedSeats: occupiedSeats }); 

    } catch (error) {
        console.error("☠️ LỖI TẠI BookingController (4003) khi lấy ghế đã đặt:", error);
        res.status(500).json({ message: "Lỗi server khi lấy ghế đã đặt" });
    }
};

// 5. API Dọn dẹp các đơn PENDING bị kẹt
exports.cleanupStuckBookings = async (req, res) => {
    try {
        const fifteenMinutesAgo = new Date(Date.now() - 15 * 60 * 1000); 

        const result = await Booking.destroy({
            where: {
                status: 'PENDING',
                createdAt: {
                    [Op.lt]: fifteenMinutesAgo // Dùng Op ở đây
                }
            }
        });

        res.status(200).json({ 
            message: `Đã xóa thành công ${result} đơn hàng PENDING quá hạn.`,
            deletedCount: result
        });
    } catch (error) {
        console.error("☠️ LỖI TẠI BookingController (4003) khi dọn dẹp:", error);
        res.status(500).json({ message: "Lỗi server khi dọn dẹp" });
    }
};

// 6. API Hủy đơn hàng (Khi khách bấm Hủy trên popup)
exports.cancelBooking = async (req, res) => {
    try {
        const { orderId } = req.body;
        const result = await Booking.destroy({
            where: {
                id: orderId,
                status: 'PENDING'
            }
        });

        if (result > 0) {
            console.log(`[4003] 🗑️ Đã hủy đơn hàng ID: ${orderId}`);
            res.status(200).json({ message: "Đã hủy đơn hàng thành công." });
        } else {
            res.status(400).json({ message: "Không tìm thấy đơn hàng hoặc đơn đã thanh toán." });
        }
    } catch (error) {
        console.error("Lỗi hủy đơn:", error);
        res.status(500).json({ message: "Lỗi server" });
    }
};
