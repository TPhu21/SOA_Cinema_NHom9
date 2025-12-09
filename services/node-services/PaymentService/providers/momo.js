const axios = require('axios');
const { createHmacSha256 } = require('../utils/signature'); // ✅ Giữ nguyên theo yêu cầu của bạn
require('dotenv').config();

// Cấu hình lấy từ .env
const config = {
    partnerCode: process.env.MOMO_PARTNER_CODE,
    accessKey: process.env.MOMO_ACCESS_KEY,
    secretKey: process.env.MOMO_SECRET_KEY,
    endpoint: process.env.MOMO_ENDPOINT || "https://test-payment.momo.vn/v2/gateway/api/create",
    ipnUrl: `${process.env.PUBLIC_WEBHOOK_URL}/api/payment/momo-callback`,
    redirectUrl: process.env.CLIENT_REDIRECT_URL
};

exports.createPaymentRequest = async ({ orderId, amount, orderInfo }) => {
    // MoMo yêu cầu requestId và orderId phải unique
    const requestId = orderId;

    // ⚠️ QUAN TRỌNG: Dùng 'captureWallet' thay vì 'payWithMethod' (chuẩn mới hỗ trợ QR tốt hơn)
    const requestType = "captureWallet";
    const extraData = "";

    // 1. Tạo chuỗi Raw Signature (BẮT BUỘC sắp xếp Alpha bét)
    // accessKey -> amount -> extraData -> ipnUrl -> orderId -> orderInfo -> partnerCode -> redirectUrl -> requestId -> requestType
    const rawSignature = `accessKey=${config.accessKey}&amount=${amount}&extraData=${extraData}&ipnUrl=${config.ipnUrl}&orderId=${orderId}&orderInfo=${orderInfo}&partnerCode=${config.partnerCode}&redirectUrl=${config.redirectUrl}&requestId=${requestId}&requestType=${requestType}`;

    // 2. Mã hóa chữ ký (Dùng util của bạn)
    const signature = createHmacSha256(rawSignature, config.secretKey);

    // 3. Body gửi đi
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

    console.log(`[MoMo] Đang tạo giao dịch: ${orderId}`);

    try {
        // 4. Gửi sang Momo
        const response = await axios.post(config.endpoint, requestBody);
        console.log("[MoMo] Kết quả:", response.data);
        return response.data;
    } catch (error) {
        console.error("[MoMo] Lỗi tạo đơn:", error.message);
        if(error.response) console.error("Chi tiết:", error.response.data);
        throw error;
    }
};

exports.verifySignature = (data) => {
    const { amount, extraData, message, orderId, orderInfo, orderType, partnerCode, payType, requestId, responseTime, resultCode, transId } = data;

    // 1. Log dữ liệu MoMo gửi sang để kiểm tra
    console.log("🔵 [DEBUG] Data from MoMo:", JSON.stringify(data, null, 2));

    // 2. Tạo chuỗi Raw để kiểm tra (Sắp xếp theo tài liệu MoMo cho luồng IPN)
    // accessKey -> amount -> extraData -> message -> orderId -> orderInfo -> orderType -> partnerCode -> payType -> requestId -> responseTime -> resultCode -> transId
    const rawSignature = `accessKey=${config.accessKey}&amount=${amount}&extraData=${extraData}&message=${message}&orderId=${orderId}&orderInfo=${orderInfo}&orderType=${orderType}&partnerCode=${partnerCode}&payType=${payType}&requestId=${requestId}&responseTime=${responseTime}&resultCode=${resultCode}&transId=${transId}`;

    console.log("🟡 [DEBUG] My Raw String:", rawSignature);

    // 3. Hash lại bằng util của bạn
    const mySignature = createHmacSha256(rawSignature, config.secretKey);

    // 4. So sánh
    console.log("🔴 [DEBUG] MoMo Signature:", data.signature);
    console.log("🟢 [DEBUG] My Signature:  ", mySignature);

    return mySignature === data.signature;
};