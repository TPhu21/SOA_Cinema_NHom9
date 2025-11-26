// services/PaymentService/providers/momo.js
const axios = require('axios'); 
const { createHmacSha256 } = require('../utils/signature'); // 👈 ĐÃ SỬA LỖI

// Cấu hình lấy từ .env
const config = {
    partnerCode: process.env.MOMO_PARTNER_CODE,
    accessKey: process.env.MOMO_ACCESS_KEY,
    secretKey: process.env.MOMO_SECRET_KEY,
    endpoint: process.env.MOMO_ENDPOINT || "https://test-payment.momo.vn/v2/gateway/api/create",
    ipnUrl: process.env.MOMO_IPN_URL,
    redirectUrl: process.env.MOMO_REDIRECT_URL
};

exports.createPaymentRequest = async ({ orderId, amount, orderInfo }) => {
    // (orderId này đã được thêm timestamp từ service, ví dụ: "45_176...")
    const requestId = orderId; 
    const requestType = "payWithMethod";
    const extraData = "";

    // Tạo chuỗi Raw Signature
    const rawSignature = `accessKey=${config.accessKey}&amount=${amount}&extraData=${extraData}&ipnUrl=${config.ipnUrl}&orderId=${orderId}&orderInfo=${orderInfo}&partnerCode=${config.partnerCode}&redirectUrl=${config.redirectUrl}&requestId=${requestId}&requestType=${requestType}`;
    
    // Mã hóa chữ ký
    const signature = createHmacSha256(rawSignature, config.secretKey);

    // Body gửi đi
    const requestBody = {
        partnerCode: config.partnerCode,
        requestId: requestId,
        amount: amount,
        orderId: orderId,
        orderInfo: orderInfo,
        redirectUrl: config.redirectUrl,
        ipnUrl: config.ipnUrl,
        lang: 'vi',
        requestType: requestType,
        extraData: extraData,
        signature: signature
    };

    // Gửi sang Momo
    const response = await axios.post(config.endpoint, requestBody);
    return response.data; 
};

// Hàm kiểm tra chữ ký khi Momo gọi ngược lại
exports.verifySignature = (data) => {
    const { amount, extraData, message, orderId, orderInfo, orderType, partnerCode, payType, requestId, responseTime, resultCode, transId } = data;
    const rawSignature = `accessKey=${config.accessKey}&amount=${amount}&extraData=${extraData}&message=${message}&orderId=${orderId}&orderInfo=${orderInfo}&orderType=${orderType}&partnerCode=${partnerCode}&payType=${payType}&requestId=${requestId}&responseTime=${responseTime}&resultCode=${resultCode}&transId=${transId}`;
    
    const mySignature = createHmacSha256(rawSignature, config.secretKey);
    return mySignature === data.signature;
};