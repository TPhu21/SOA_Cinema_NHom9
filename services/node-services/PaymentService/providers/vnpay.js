// Server/services/providers/vnpay.js
const crypto = require('crypto');
const moment = require('moment');

const config = {
    vnp_TmnCode: process.env.VNPAY_TMN_CODE,
    vnp_HashSecret: process.env.VNPAY_HASH_SECRET,
    vnp_Url: process.env.VNPAY_URL,
    // ⭐️ SỬA 1: Dùng Link Mồi (Siêu An Toàn) ⭐️
    vnp_ReturnUrl: process.env.CLIENT_REDIRECT_URL,
    vnp_IpnUrl: process.env.VNPAY_IPN_URL     
};

/**
 * HÀM TẠO LINK (BẢN SIÊU AN TOÀN + CHỮ KÝ HOÀN HẢO)
 */
exports.createPaymentRequest = async ({ orderId, amount, orderInfo, ipAddr }) => {
    
    const tmnCode = config.vnp_TmnCode;
    const secretKey = config.vnp_HashSecret;
    let vnpUrl = config.vnp_Url;
    
    const createDate = moment().format('YYYYMMDDHHmmss');
    const vnp_TxnRef = `${orderId}_${createDate}`; 
    
    let vnp_Params = {};
    vnp_Params['vnp_Version'] = '2.1.0';
    vnp_Params['vnp_Command'] = 'pay';
    vnp_Params['vnp_TmnCode'] = tmnCode;
    vnp_Params['vnp_Locale'] = 'vn';
    vnp_Params['vnp_CurrCode'] = 'VND';
    vnp_Params['vnp_TxnRef'] = vnp_TxnRef;
    
    // ⭐️ SỬA 2: Dùng OrderInfo an toàn nhất
    vnp_Params['vnp_OrderInfo'] = `TEST`; 
    
    vnp_Params['vnp_OrderType'] = 'other';
    vnp_Params['vnp_Amount'] = amount * 100; 
    
    // ⭐️ SỬA 3: Dùng ReturnUrl mồi (google)
    vnp_Params['vnp_ReturnUrl'] = config.vnp_ReturnUrl; 
    
    // ⭐️ SỬA 4: TẠM THỜI "NÉ" IPN ĐỂ TEST
    // vnp_Params['vnp_IpnUrl'] = config.vnp_IpnUrl; 

    vnp_Params['vnp_IpAddr'] = ipAddr || '127.0.0.1';

    vnp_Params['vnp_CreateDate'] = createDate;

    // Sắp xếp A-Z (Code này đã chuẩn)
    let sortedKeys = Object.keys(vnp_Params).sort();
    
    // ⭐️ CHUẨN MÃ HÓA (Không .replace) ⭐️
    let signData = "";
    for (let key of sortedKeys) {
        signData += `${encodeURIComponent(key)}=${encodeURIComponent(vnp_Params[key])}&`;
    }
    signData = signData.slice(0, -1); 

    const hmac = crypto.createHmac("sha512", secretKey);
    const signed = hmac.update(Buffer.from(signData, 'utf-8')).digest("hex");

    vnpUrl += `?${signData}&vnp_SecureHash=${signed}`;

    return { payUrl: vnpUrl };
};

/**
 * HÀM XÁC THỰC CALLBACK (Dùng code chuẩn)
 */
exports.verifyCallback = (vnp_Params) => {
    
    const secureHash = vnp_Params['vnp_SecureHash'];
    delete vnp_Params['vnp_SecureHash'];
    delete vnp_Params['vnp_SecureHashType']; 

    let sortedKeys = Object.keys(vnp_Params).sort();
    
    let signData = "";
    for (let key of sortedKeys) {
        signData += `${encodeURIComponent(key)}=${encodeURIComponent(vnp_Params[key])}&`;
    }
    signData = signData.slice(0, -1); 

    const secretKey = config.vnp_HashSecret;
    const hmac = crypto.createHmac("sha512", secretKey);
    const signed = hmac.update(Buffer.from(signData, 'utf-8')).digest("hex");

    return secureHash === signed;
};