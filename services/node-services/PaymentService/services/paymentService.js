const axios = require('axios');
const { Payment } = require('../models');
const BOOKING_SERVICE_URL = 'http://localhost:8080/api/bookings';
const getProvider = require('../providers');  // Đảm bảo rằng đường dẫn đúng
exports.createPaymentLink = async (paymentData) => {
    const { orderId, amount, orderInfo, providerName, ipAddr } = paymentData;
    let result = {};
    const providerPayload = { orderId, amount, orderInfo, ipAddr };

    // Tạo payment link từ nhà cung cấp thanh toán (Momo, VNPay, etc.)
    if (providerName === 'momo') {
        result = await createMomoPaymentLink(providerPayload);
    } else if (providerName === 'vnpay') {
        result = await createVnPayPaymentLink(providerPayload);
    }

    // Sau khi tạo payment link, gọi API BookingService để tạo booking
    await finalizeBooking(paymentData, result.paymentLink);

    return result;
};

// Sau khi thanh toán thành công, gọi API của BookingService để xác nhận booking
async function finalizeBooking(paymentData, paymentLink) {
    const bookingPayload = {
        userId: paymentData.userId,
        showTimeId: paymentData.showTimeId,
        seats: paymentData.seats,
        totalPrice: paymentData.totalPrice,
        paymentLink: paymentLink
    };

    try {
        const bookingRes = await axios.post(`${BOOKING_SERVICE_URL}/pending`, bookingPayload);
        console.log("Booking đã được hoàn tất:", bookingRes.data);
    } catch (error) {
        console.error("Lỗi khi gọi API BookingService:", error);
    }
}

// Momo callback xử lý trạng thái thanh toán
const crypto = require('crypto');
const { momo } = require('./config'); // Lấy thông tin từ config

// Hàm tính toán và xác minh chữ ký
exports.verifySignature = (data) => {
    const { amount, extraData, message, orderId, orderInfo, orderType, partnerCode, payType, requestId, responseTime, resultCode, transId } = data;

    const rawSignature = `accessKey=${momo.accessKey}&amount=${amount}&extraData=${extraData}&message=${message}&orderId=${orderId}&orderInfo=${orderInfo}&orderType=${orderType}&partnerCode=${partnerCode}&payType=${payType}&requestId=${requestId}&responseTime=${responseTime}&resultCode=${resultCode}&transId=${transId}`;

    console.log("Raw Signature: ", rawSignature);
    console.log("Signature từ Momo: ", data.signature);

    const mySignature = createHmacSha256(rawSignature, momo.secretKey);

    console.log("My Signature: ", mySignature);

    return mySignature === data.signature; // So sánh chữ ký tính toán với chữ ký của Momo
};

// Hàm tạo HMAC SHA256 để mã hóa chữ ký
function createHmacSha256(data, secretKey) {
    return crypto.createHmac('sha256', secretKey).update(data).digest('hex');
}

exports.processMomoCallback = async (data) => {
    try {
        const provider = getProvider('momo');
        if (!provider.verifySignature(data)) {
            console.error("Chữ ký không hợp lệ.");
            throw new Error('Invalid Signature');
        }

        const realBookingId = data.orderId.split('_')[0];

        // Lưu thông tin thanh toán vào database
        await Payment.create({
            bookingId: realBookingId,
            amount: data.amount,
            transId: data.transId,
            status: 'CONFIRMED'
        });

        // Cập nhật trạng thái booking nếu thanh toán thành công
        if (data.resultCode === '0') {
            await axios.post(`${BOOKING_SERVICE_URL}/update-status`, {
                bookingId: realBookingId,
                status: 'CONFIRMED'
            });
        }
    } catch (error) {
        console.error("Lỗi callback Momo:", error);
        // Log lỗi chi tiết vào console
        console.error(error.stack);
        throw error;  // Ném lỗi lên trên để hệ thống nhận biết
    }
};

// VNPay callback xử lý trạng thái thanh toán
exports.processVnpayCallback = async (vnp_Params) => {
    const provider = getProvider('vnpay');
    if (!provider.verifyCallback(vnp_Params)) throw new Error('Invalid VNPay Signature');
    const realBookingId = vnp_Params['vnp_TxnRef'].split('_')[0];

    await Payment.create({ /* lưu thông tin thanh toán */ });
    if (vnp_Params['vnp_ResponseCode'] === '00') {
        await axios.post(`${BOOKING_SERVICE_URL}/update-status`, {
            bookingId: realBookingId, status: 'CONFIRMED'
        });
    }
};

// Lấy trạng thái booking
exports.getBookingStatus = async (bookingId) => {
    try {
        const response = await axios.get(`${BOOKING_SERVICE_URL}/status/${bookingId}`);
        return response.data.status; // Trả về status ('PENDING'/'CONFIRMED')
    } catch (error) {
        console.error("Lỗi khi 8004 gọi 8003 để check status:", error.message);
        return 'NOT_FOUND';
    }
};

// // services/PaymentService/services/paymentService.js
// const { Payment } = require('../models');
// const getProvider = require('../providers');
// const axios = require('axios');
//
// // ⭐️ SỬA LỖI ĐƯỜNG DẪN GỌI 4003 (Bỏ /api/bookings)
// const BOOKING_SERVICE_URL = 'http://localhost:8080/api/bookings';
//
// exports.createPaymentLink = async (paymentData) => {
//
//     console.log("[SERVICE 8004] 💡: Đã vào hàm createPaymentLink."); // 👈 ĐÈN PIN MỚI
//     const { orderId, amount, orderInfo, providerName, ipAddr } = paymentData;
//
//     // 1. Gọi "Nhà máy"
//     console.log("[SERVICE 8004] 💡: Đang gọi 'Nhà máy' (providers/index)..."); // 👈 ĐÈN PIN MỚI
//     const provider = getProvider(providerName);
//
//     // 2. Chuẩn bị dữ liệu
//     let result = {};
//     let providerPayload = {
//         orderId: orderId, amount: amount,
//         orderInfo: orderInfo, ipAddr: ipAddr
//     };
//
//     if (providerName === 'momo') {
//         providerPayload.orderId = `${orderId}_${new Date().getTime()}`;
//         console.log("[SERVICE 8004] 💡: Đang gọi Momo.createPaymentRequest..."); // 👈 ĐÈN PIN MỚI
//         result = await provider.createPaymentRequest(providerPayload);
//
//     } else if (providerName === 'vnpay') {
//         console.log("[SERVICE 8004] 💡: Đang gọi VNPay.createPaymentRequest..."); // 👈 ĐÈN PIN MỚI
//         result = await provider.createPaymentRequest(providerPayload);
//
//     } else if (providerName === 'chuyenkhoan') {
//         console.log("[SERVICE 8004] 💡: Đang gọi VietQR.createPaymentRequest..."); // 👈 ĐÈN PIN MỚI
//         result = await provider.createPaymentRequest(providerPayload);
//
//     } else if (providerName === 'cash') {
//         result = {
//             isCash: true,
//             orderId: orderId,
//             amount: amount,
//             orderInfo: `Dat ve ${orderId} thanh toan tai quay`
//         };
//     } else {
//         throw new Error('Provider không hợp lệ');
//     }
//
//     console.log("[SERVICE 8004] 💡: Đã tạo link xong, trả về Controller."); // 👈 ĐÈN PIN MỚI
//     return result;
// };
//
// // ... (Các hàm callback và getStatus khác giữ nguyên) ...
// exports.processMomoCallback = async (data) => {
//     const provider = getProvider('momo');
//     if (!provider.verifySignature(data)) throw new Error('Invalid Signature');
//     const realBookingId = data.orderId.split('_')[0];
//     await Payment.create({ /* ... */ });
//     if (data.resultCode == 0) {
//         await axios.post(`${BOOKING_SERVICE_URL}/update-status`, {
//             bookingId: realBookingId, status: 'CONFIRMED'
//         });
//     }
// };
//
// exports.processVnpayCallback = async (vnp_Params) => {
//     const provider = getProvider('vnpay');
//     if (!provider.verifyCallback(vnp_Params)) throw new Error('Invalid VNPay Signature');
//     const realBookingId = vnp_Params['vnp_TxnRef'].split('_')[0];
//     await Payment.create({ /* ... */ });
//     if (vnp_Params['vnp_ResponseCode'] === '00') {
//         await axios.post(`${BOOKING_SERVICE_URL}/update-status`, {
//             bookingId: realBookingId, status: 'CONFIRMED'
//         });
//     }
// };
//
// exports.getBookingStatus = async (bookingId) => {
//     try {
//         const response = await axios.get(`${BOOKING_SERVICE_URL}/status/${bookingId}`);
//         return response.data.status; // Trả về status ('PENDING'/'CONFIRMED')
//     } catch (error) {
//         // Nếu 4003 sập hoặc 404, nó sẽ báo lỗi ở đây
//         console.error("Lỗi khi 8004 gọi 8003 để check status:", error.message);
//         return 'NOT_FOUND';
//     }
// };