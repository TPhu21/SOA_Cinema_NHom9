// Server/services/providers/vietqr.js
const axios = require('axios'); // 👈 Chỉ cần import axios

const config = {
    api_endpoint: "https://api.vietqr.io/v2/generate",
    bankId: process.env.VIETQR_BANK_ID,
    accountNo: process.env.VIETQR_ACCOUNT_NO,
    accountName: process.env.VIETQR_ACCOUNT_NAME,
};

exports.createPaymentRequest = async ({ orderId, amount, orderInfo }) => {
    try {
        const addInfoContent = `PAY${orderId}`; // Nội dung CK (ví dụ: PAY21)

        const payload = {
            accountNo: config.accountNo,
            accountName: config.accountName,
            acqId: config.bankId,
            amount: amount,
            addInfo: addInfoContent, 
            format: "text",
            template: "compact"
        };
        
        console.log("👉 Đang gửi payload này đến VietQR:", payload);
        const response = await axios.post(config.api_endpoint, payload);
        console.log("👉 VietQR đã trả về:", response.data);

        if (response.data.code === '00') { 
            // THÀNH CÔNG
            return { 
                isVietQR: true, 
                qrDataURL: response.data.data.qrDataURL,
                orderId: orderId,
                amount: amount,
                orderInfo: addInfoContent
            };
        } else {
            // THẤT BẠI (Do VietQR từ chối)
            throw new Error(response.data.desc || 'Lỗi không xác định từ VietQR');
        }

    } catch (error) {
        // THẤT BẠI (Do mạng hoặc code sập)
        console.error("Lỗi TẬN CÙNG trong vietqr.js:", error.response ? error.response.data : error.message);
        throw error; // Ném lỗi về cho controller
    }
};