// Server/services/providers/index.js
const momoProvider = require('./momo');
const vnpayProvider = require('./vnpay');
const vietqrProvider = require('./vietqr');

module.exports = (providerName) => {
    switch (providerName) {
        case 'momo':
            return momoProvider;
        
        case 'vnpay':
            return vnpayProvider;
        case 'chuyenkhoan':
            return vietqrProvider;
            case 'cash': 
            return {}; // Trả về đối tượng rỗng cho thanh toán tiền mặt
        default:
            throw new Error(`Cổng thanh toán '${providerName}' không được hỗ trợ.`);
    }
};