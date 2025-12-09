
// // services/PaymentService/services/paymentService.js
const { Payment } = require('../models');
const getProvider = require('../providers');
const axios = require('axios');
const GATEWAY_URL = process.env.GATEWAY_URL;
// ⭐️ SỬA LỖI ĐƯỜNG DẪN GỌI 4003 (Bỏ /api/bookings)
const BOOKING_SERVICE_URL = `${GATEWAY_URL}/bookings`;

exports.createPaymentLink = async (paymentData) => {

    console.log("[SERVICE 8004] 💡: Đã vào hàm createPaymentLink."); // 👈 ĐÈN PIN MỚI
    const { orderId, amount, orderInfo, providerName, ipAddr } = paymentData;

    // 1. Gọi "Nhà máy"
    console.log("[SERVICE 8004] 💡: Đang gọi 'Nhà máy' (providers/index)..."); // 👈 ĐÈN PIN MỚI
    const provider = getProvider(providerName);

    // 2. Chuẩn bị dữ liệu
    let result = {};
    let providerPayload = {
        orderId: orderId, amount: amount,
        orderInfo: orderInfo, ipAddr: ipAddr
    };

    if (providerName === 'momo') {
        providerPayload.orderId = `${orderId}_${new Date().getTime()}`;
        console.log("[SERVICE 8004] 💡: Đang gọi Momo.createPaymentRequest..."); // 👈 ĐÈN PIN MỚI
        result = await provider.createPaymentRequest(providerPayload);

    } else if (providerName === 'zalopay') {
        // --- LOGIC MỚI CHO ZALOPAY ---
        console.log("[SERVICE 8004] 💡: Gọi ZaloPay...");
        // ZaloPay cần xử lý kết quả trả về hơi khác một chút
        const zalopayResponse = await provider.createPaymentRequest(providerPayload);

        // Map lại dữ liệu để Controller trả về thống nhất cho Frontend
        result = {
            payUrl: zalopayResponse.payUrl,
            orderId: orderId,
            message: "Tạo link ZaloPay thành công"
        };

    } else if (providerName === 'vnpay') {
        console.log("[SERVICE 8004] 💡: Đang gọi VNPay.createPaymentRequest..."); // 👈 ĐÈN PIN MỚI
        result = await provider.createPaymentRequest(providerPayload);

    } else if (providerName === 'chuyenkhoan') {
        console.log("[SERVICE 8004] 💡: Đang gọi VietQR.createPaymentRequest..."); // 👈 ĐÈN PIN MỚI
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

    console.log("[SERVICE 8004] 💡: Đã tạo link xong, trả về Controller."); // 👈 ĐÈN PIN MỚI
    return result;
};

// ... (Các hàm callback và getStatus khác giữ nguyên) ...
exports.processMomoCallback = async (data) => {

    try {
        const provider = getProvider('momo');

        // 1. Check chữ ký (Dùng hàm verifySignature bạn vừa gửi)
        if (!provider.verifySignature(data)) {
            console.error("❌ [PaymentService] Chữ ký MoMo không hợp lệ!");
            throw new Error('Invalid Signature');
        }

        // 2. Tách lấy ID gốc (Vì orderId gửi đi có thể là "123456_17300...")
        // Nếu bạn gửi đi là "123456" thì nó lấy "123456". Nếu gửi "123456_timestamp" nó lấy "123456"
        const realBookingId = data.orderId.toString().split('_')[0];

        console.log("✅ [PaymentService] Chữ ký chuẩn. Đang lưu BookingID:", realBookingId);

        // 3. Lưu vào Database (Đã sửa tên cột cho khớp với Model Payment.js)
        await Payment.create({
            bookingId: parseInt(realBookingId), // Ép kiểu số nguyên
            amount: data.amount,                // Số tiền
            provider: 'momo',                   // Tên cột là 'provider' (Code cũ sai là paymentMethod)
            transId: data.transId,              // Mã giao dịch MoMo
            resultCode: parseInt(data.resultCode) // Mã kết quả (0 là thành công)
        });

        console.log("✅ [PaymentService] Đã lưu vào DB thành công!");

        // 4. Gọi sang BookingService để cập nhật trạng thái vé (chỉ khi thành công)
        if (data.resultCode == 0) {
            console.log(`📡 [PaymentService] Đang gọi BookingService update booking ${realBookingId}...`);
            await axios.post(`${BOOKING_SERVICE_URL}/update-status`, {
                bookingId: parseInt(realBookingId),
                status: 'CONFIRMED'
            });
            console.log("✅ [PaymentService] Update BookingService thành công!");
        }

    } catch (error) {
        console.error("🔥 Lỗi xử lý MoMo Callback:", error.message);
        // Log chi tiết lỗi nếu do Sequelize từ chối lưu
        if (error.name === 'SequelizeValidationError') {
            console.error("Chi tiết lỗi DB:", error.errors.map(e => e.message));
        }
        throw error;
    }
};
exports.processVietQRCallback = async (webhookData, headers) => {
    const provider = getProvider('vietqr');

    // Kiểm tra bảo mật (Optional)
    if (!provider.verifyCallback(headers)) {
        console.error("❌ [VietQR] Token bảo mật không khớp!");
        throw new Error("Invalid Secure Token");
    }

    // Webhook của Casso trả về mảng các giao dịch (có thể có nhiều giao dịch cùng lúc)
    const transactions = webhookData.data;

    if (!transactions || transactions.length === 0) {
        console.log("⚠️ [VietQR] Webhook không có giao dịch nào.");
        return;
    }

    for (const trans of transactions) {
        const description = trans.description; // Nội dung chuyển khoản, VD: "PAY 105"
        const amount = trans.amount;

        console.log(`📡 [VietQR] Nhận biến động: +${amount} | ND: ${description}`);


        const match = description.match(/PAY\s*(\d+)/i);

        if (match) {
            const realBookingId = match[1]; // Lấy được số 105
            console.log(`✅ [VietQR] Tìm thấy BookingID: ${realBookingId}`);

            // Lưu vào DB
            await Payment.create({
                bookingId: parseInt(realBookingId),
                amount: amount,
                provider: 'vietqr',
                transId: trans.tid, // Mã giao dịch ngân hàng
                resultCode: 0
            });

            // Gọi Booking Service update trạng thái
            console.log(`📡 [PaymentService] Gọi BookingService update đơn ${realBookingId}...`);
            await axios.post(`${BOOKING_SERVICE_URL}/update-status`, {
                bookingId: parseInt(realBookingId),
                status: 'CONFIRMED'
            });
        } else {
            console.log("⚠️ [VietQR] Không tìm thấy mã đơn trong nội dung chuyển khoản.");
        }
    }
};
exports.processZalopayCallback = async (cbData) => {
    const provider = getProvider('zalopay');
    const { data, mac } = cbData;

    // 1. Kiểm tra chữ ký (MAC) xem có đúng là ZaloPay gửi không
    const isValid = provider.verifyCallback(cbData);
    if (!isValid) {
        throw new Error("Invalid ZaloPay Signature");
    }
    const dataJson = JSON.parse(data);

    console.log(`✅ [PaymentService] ZaloPay Callback thành công cho giao dịch: ${dataJson.app_trans_id}`);

    // TODO: Lưu vào DB Payment và gọi BookingService update status (Tương tự MoMo)

    return { return_code: 1, return_message: "success" };
};
exports.processVnpayCallback = async (vnp_Params) => {
    const provider = getProvider('vnpay');
    if (!provider.verifyCallback(vnp_Params)) throw new Error('Invalid VNPay Signature');
    const realBookingId = vnp_Params['vnp_TxnRef'].split('_')[0];
    await Payment.create({ /* ... */ });
    if (vnp_Params['vnp_ResponseCode'] === '00') {
        await axios.post(`${BOOKING_SERVICE_URL}/update-status`, {
            bookingId: realBookingId, status: 'CONFIRMED'
        });
    }
};

exports.getBookingStatus = async (bookingId) => {
    try {
        const response = await axios.get(`${BOOKING_SERVICE_URL}/status/${bookingId}`);
        return response.data.status; // Trả về status ('PENDING'/'CONFIRMED')
    } catch (error) {
        // Nếu 4003 sập hoặc 404, nó sẽ báo lỗi ở đây
        console.error("Lỗi khi 8004 gọi 8003 để check status:", error.message);
        return 'NOT_FOUND';
    }
};
