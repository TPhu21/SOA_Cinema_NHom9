// services/BookingService/routes/bookings.js
const express = require('express');
const router = express.Router();
const bookingController = require('../controllers/bookingController');

// --- API CHO CLIENT (5173) GỌI ---

// 1. Tạo đơn hàng mới (Đặt vé)
// Gọi bởi: DatVe.html (hàm completeBooking)
router.post('/create', bookingController.createBooking);

// 2. Lấy danh sách ghế đã đặt (để tô màu đỏ)
// Gọi bởi: DatVe.html (hàm generateSeats)
router.get('/occupied-seats/:showtimeId', bookingController.getOccupiedSeats);


// --- API NỘI BỘ (CHO CÁC SERVICE KHÁC GỌI) ---

// 3. Cập nhật trạng thái đơn hàng (PENDING -> CONFIRMED)
// Gọi bởi: PaymentService (4004) khi nhận được IPN từ Momo/VNPay
router.post('/update-status', bookingController.updateStatus);

// 4. Kiểm tra trạng thái đơn hàng
// Gọi bởi: PaymentService (4004) khi Popup VietQR đang "hóng" (Polling)
router.get('/status/:bookingId', bookingController.getStatus);


// --- API QUẢN TRỊ (ADMIN HOẶC CRON JOB) ---

// 5. Dọn dẹp các đơn hàng PENDING bị kẹt quá hạn
// Gọi bởi: Cron job nội bộ hoặc Admin chạy tay
router.delete('/cleanup', bookingController.cleanupStuckBookings);

// 6. Hủy đơn hàng ngay lập tức (Khi khách bấm "Hủy bỏ" trên popup)
// Gọi bởi: DatVe.html (hàm closeVietQRModal)
router.post('/cancel', bookingController.cancelBooking);

module.exports = router;