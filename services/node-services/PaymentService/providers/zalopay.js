const axios = require('axios').default;
const CryptoJS = require('crypto-js');
const moment = require('moment');
require('dotenv').config();

const config = {
    app_id: process.env.ZALO_APP_ID,
    key1: process.env.ZALO_KEY1,
    key2: process.env.ZALO_KEY2,
    endpoint: process.env.ZALO_API_URL
};

exports.createPaymentRequest = async (payload) => {
    const { orderId, amount, orderInfo, ipAddr } = payload;

    const embed_data = {
        // 1. Redirect về Frontend sau khi thanh toán xong
        redirecturl: process.env.CLIENT_REDIRECT_URL
    };

    const items = [{}];

    // Random transID để tránh trùng lặp khi test
    const transID = Math.floor(Math.random() * 1000000);

    const order = {
        app_id: config.app_id,
        app_user: "MovieUser",
        app_time: Date.now(), // miliseconds
        amount: amount,
        app_trans_id: `${moment().format('YYMMDD')}_${transID}`, // Format: YYMMDD_xxxx
        embed_data: JSON.stringify(embed_data),
        item: JSON.stringify(items),
        description: orderInfo || `Thanh toan don hang #${orderId}`,
        bank_code: "",

        // 2. Callback server-to-server (Dùng link Ngrok từ .env)
        // Gateway (8080) sẽ nhận request này và chuyển vào PaymentService (8004)
        callback_url: `${process.env.PUBLIC_WEBHOOK_URL}/api/payment/zalopay-callback`
    };

    // Tạo chữ ký (MAC) dùng Key1
    const data = config.app_id + "|" + order.app_trans_id + "|" + order.app_user + "|" + order.amount + "|" + order.app_time + "|" + order.embed_data + "|" + order.item;
    order.mac = CryptoJS.HmacSHA256(data, config.key1).toString();

    try {
        console.log(`[ZALOPAY] Đang tạo đơn hàng: ${order.app_trans_id}`);

        const result = await axios.post(config.endpoint, null, { params: order });

        console.log("[ZALOPAY] Kết quả trả về:", result.data);

        if (result.data.return_code === 1) {
            return {
                payUrl: result.data.order_url,        // Link thanh toán
                zpTransToken: result.data.zp_trans_token,
                orderId: orderId,                     // ID gốc của hệ thống mình
                providerOrder: order.app_trans_id,    // ID của ZaloPay
                isSuccess: true
            };
        } else {
            throw new Error(`ZaloPay Error: ${result.data.return_message}`);
        }

    } catch (err) {
        console.error("[ZALOPAY] Lỗi gọi API:", err.message);
        throw err;
    }
};

// 3. Hàm xác thực chữ ký khi ZaloPay gọi Callback
exports.verifyCallback = (cbData) => {
    try {
        const { data, mac } = cbData;

        // Công thức: HmacSHA256(data, key2)
        const calculatedMac = CryptoJS.HmacSHA256(data, config.key2).toString();

        // So sánh mac nhận được và mac tính toán
        return mac === calculatedMac;
    } catch (e) {
        console.error("Lỗi verifyCallback ZaloPay:", e.message);
        return false;
    }
};