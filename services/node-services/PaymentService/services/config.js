require('dotenv').config();
const config = {
    momo: {
        partnerCode: process.env.MOMO_PARTNER_CODE,
        accessKey: process.env.MOMO_ACCESS_KEY,
        secretKey: process.env.MOMO_SECRET_KEY,
        endpoint: process.env.MOMO_ENDPOINT || 'https://test-payment.momo.vn/v2/gateway/api/create',
        ipnUrl: process.env.MOMO_IPN_URL,
        redirectUrl: process.env.MOMO_REDIRECT_URL
    },
    vnpay: {
        tmnCode: process.env.VNPAY_TMN_CODE,
        hashSecret: process.env.VNPAY_HASH_SECRET,
        url: process.env.VNPAY_URL,
        ipnUrl: process.env.VNPAY_IPN_URL
    },
    vietqr: {
        bankId: process.env.VIETQR_BANK_ID,
        accountNo: process.env.VIETQR_ACCOUNT_NO,
        accountName: process.env.VIETQR_ACCOUNT_NAME
    }
};

module.exports = config;