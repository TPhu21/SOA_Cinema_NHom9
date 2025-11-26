// services/BookingService/services/bookingService.js
const { Booking } = require('../models');
const { Op } = require('sequelize');
const axios = require('axios');

const PAYMENT_SERVICE_URL = 'http://localhost:4004'; // 👈 CHỈ GIỮ LẠI CỔNG

exports.createBookingAndGetLink = async (userId, bookingData, ipAddr) => {
    
    console.log("[4003] 💡 BƯỚC 1: Đã nhận lệnh. Bắt đầu check ghế...");
    const { showtimeId, seats, amount, providerName } = bookingData;

    // 1. Check trùng ghế
    const existingBookings = await Booking.findAll({
        where: { showtimeId: showtimeId, status: { [Op.in]: ['PENDING', 'CONFIRMED'] } }
    });
    let isTaken = false;
    for (const booking of existingBookings) {
        const takenSeats = booking.seats;
        const hasOverlap = seats.some(seat => takenSeats.includes(seat));
        if (hasOverlap) { isTaken = true; break; }
    }
    if (isTaken) throw new Error('Ghế đã được chọn, vui lòng chọn ghế khác!');
    console.log("[4003] 💡 BƯỚC 2: Check ghế OK.");

    // 2. Tạo đơn hàng
    console.log("[4003] 💡 BƯỚC 3: Chuẩn bị tạo đơn PENDING...");
    const newBooking = await Booking.create({
        userId: userId || null, showtimeId, seats,
        totalPrice: amount, status: 'PENDING'
    });
    console.log(`[4003] 💡 BƯỚC 4: Tạo đơn PENDING (ID: ${newBooking.id}) THÀNH CÔNG.`);

    // 3. GỌI ĐIỆN SANG PAYMENT_SERVICE (4004)
    console.log(`[4003] 💡 BƯỚC 5: Đang gọi hàng xóm PaymentService (4004)...`);
    
    try {
        const paymentPayload = {
            orderId: newBooking.id, amount: newBooking.totalPrice,
            orderInfo: `Thanh toan ve phim ID ${newBooking.id}`,
            providerName: providerName, ipAddr: ipAddr
        };
        
        const response = await axios.post(`${PAYMENT_SERVICE_URL}/create-link`, paymentPayload);
        
        console.log("[4003] 💡 BƯỚC 6: Hàng xóm (4004) đã trả lời.");
        return response.data; 

    } catch (error) {
        console.log("[4003] ☠️ BƯỚC X: Hàng xóm (4004) bị sập hoặc không trả lời!");
        await newBooking.destroy(); 
        const errorMessage = error.response ? error.response.data.message : error.message;
        throw new Error(`Lỗi từ Payment Service: ${errorMessage}`);
    }
};