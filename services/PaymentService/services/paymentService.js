// services/PaymentService/services/paymentService.js
const { Payment } = require('../models');
const getProvider = require('../providers/index'); 
const axios = require('axios');

// ⭐️ SỬA LỖI ĐƯỜNG DẪN GỌI 4003 (Bỏ /api/bookings)
const BOOKING_SERVICE_URL = 'http://localhost:4003'; 

exports.createPaymentLink = async (paymentData) => {
    
    console.log("[SERVICE 4004] 💡: Đã vào hàm createPaymentLink."); // 👈 ĐÈN PIN MỚI
    const { orderId, amount, orderInfo, providerName, ipAddr } = paymentData;

    // 1. Gọi "Nhà máy"
    console.log("[SERVICE 4004] 💡: Đang gọi 'Nhà máy' (providers/index)..."); // 👈 ĐÈN PIN MỚI
    const provider = getProvider(providerName);
    
    // 2. Chuẩn bị dữ liệu
    let result = {};
    let providerPayload = {
        orderId: orderId, amount: amount,
        orderInfo: orderInfo, ipAddr: ipAddr
    };

    if (providerName === 'momo') {
        providerPayload.orderId = `${orderId}_${new Date().getTime()}`; 
        console.log("[SERVICE 4004] 💡: Đang gọi Momo.createPaymentRequest..."); // 👈 ĐÈN PIN MỚI
        result = await provider.createPaymentRequest(providerPayload);

    } else if (providerName === 'vnpay') {
        console.log("[SERVICE 4004] 💡: Đang gọi VNPay.createPaymentRequest..."); // 👈 ĐÈN PIN MỚI
        result = await provider.createPaymentRequest(providerPayload);
    
    } else if (providerName === 'chuyenkhoan') {
        console.log("[SERVICE 4004] 💡: Đang gọi VietQR.createPaymentRequest..."); // 👈 ĐÈN PIN MỚI
        result = await provider.createPaymentRequest(providerPayload);
    
    } else if (providerName === 'cash') {
        result = { 
            isCash: true, 
            orderId: orderId, 
            amount: amount,
            orderInfo: `Dat ve ${orderId} thanh toan tai quay`
        };
    } else {
        throw new Error('Provider không hợp lệ');
    }
    
    console.log("[SERVICE 4004] 💡: Đã tạo link xong, trả về Controller."); // 👈 ĐÈN PIN MỚI
    return result; 
};

// ... (Các hàm callback và getStatus khác giữ nguyên) ...
exports.processMomoCallback = async (data) => {
    const provider = getProvider('momo');
    if (!provider.verifySignature(data)) throw new Error('Invalid Signature');
    const realBookingId = data.orderId.split('_')[0];
    await Payment.create({ /* ... */ });
    // ⭐️ SỬA LỖI ĐƯỜNG DẪN GỌI 4003
    if (data.resultCode == 0) {
        await axios.post(`${BOOKING_SERVICE_URL}/update-status`, {
            bookingId: realBookingId, status: 'CONFIRMED'
        });
    }
};
exports.processVnpayCallback = async (vnp_Params) => {
    const provider = getProvider('vnpay');
    if (!provider.verifyCallback(vnp_Params)) throw new Error('Invalid VNPay Signature');
    const realBookingId = vnp_Params['vnp_TxnRef'].split('_')[0];
    await Payment.create({ /* ... */ });
    // ⭐️ SỬA LỖI ĐƯỜNG DẪN GỌI 4003
    if (vnp_Params['vnp_ResponseCode'] === '00') {
        await axios.post(`${BOOKING_SERVICE_URL}/update-status`, {
            bookingId: realBookingId, status: 'CONFIRMED'
        });
    }
};
exports.getBookingStatus = async (bookingId) => {
    try {
        // ⭐️ SỬA LẠI ĐƯỜNG DẪN GỌI (Bỏ /api/bookings) ⭐️
        const response = await axios.get(`${BOOKING_SERVICE_URL}/status/${bookingId}`);
        return response.data.status; // Trả về status ('PENDING'/'CONFIRMED')
    
    } catch (error) {
        // Nếu 4003 sập hoặc 404, nó sẽ báo lỗi ở đây
        console.error("Lỗi khi 4004 gọi 4003 để check status:", error.message);
        return 'NOT_FOUND';
    }
};