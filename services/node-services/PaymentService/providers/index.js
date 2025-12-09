// Server/services/providers/index.js
const momoProvider = require('./momo');
const vnpayProvider = require('./vnpay');
const vietqrProvider = require('./vietqr');
const zalopayProvider = require('./zalopay');

module.exports = (providerName) => {
    // 1. Chuyển về chữ hoa để so sánh cho chuẩn (tránh lỗi MoMo vs MOMO)
    const normalizedName = providerName ? providerName.toUpperCase() : '';

    switch (normalizedName) {
        case 'MOMO':
            return momoProvider;

        case 'VNPAY':
            return vnpayProvider;

        case 'ZALOPAY':
            return zalopayProvider; // 2. Trả về provider

        case 'TRANSFER':   // Tên chuẩn từ Java gửi sang
        case 'BANKING':    // Tên dự phòng
        case 'CHUYENKHOAN':
            return vietqrProvider;

        case 'CASH':
            return {}; // Trả về đối tượng rỗng cho tiền mặt

        default:
            throw new Error(`Cổng thanh toán '${providerName}' không được hỗ trợ.`);
    }
};