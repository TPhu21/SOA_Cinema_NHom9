
// services/PaymentService/controllers/paymentController.js
const paymentService = require('../services/paymentService');


exports.createPaymentLinkApi = async (req, res) => {
    
    console.log("[CONTROLLER 8004] 💡: Đã vào hàm createPaymentLink.");

    try {
        console.log("[CONTROLLER 8004] 💡: Chuẩn bị gọi service (paymentService.js)...");
        
        // Dòng này gọi service "chủ xị" (4004)
        const result = await paymentService.createPaymentLink(req.body);
        
        console.log("[CONTROLLER 8004] 💡: Service đã trả về, gửi JSON.");
        res.json(result); // Trả link về cho BookingService (4003)

    } catch (error) {
        console.error("==== ☠️ LỖI TẠI PaymentController (4004) ☠️ ====");
        console.error(error); 
        res.status(400).json({ 
            message: "Lỗi Server (8004): " + (error.message || 'Lỗi không xác định')
        });
    }
};

// API công khai (Gateway sẽ chuyển từ Ngrok)
exports.momoCallback = async (req, res) => {
    console.log("--- Nhận Callback từ Momo ---");
    console.log("Request Body:", req.body); // Log toàn bộ dữ liệu nhận được

    try {
        await paymentService.processMomoCallback(req.body);
        res.status(204).send(); // Trả về status 204 nếu thành công
    } catch (error) {
        console.error("Lỗi callback Momo:", error);
        console.error(error.stack);  // In ra stack trace để debug chi tiết
        res.status(500).send("Lỗi server khi xử lý callback Momo");
    }
};

exports.zalopayCallback = async (req, res) => {
    console.log("--- [WEBHOOK] Nhận Callback từ ZaloPay ---");
    try {
        // ZaloPay bắn dữ liệu qua Body (POST) dạng: { data: "...", mac: "..." }
        const result = await paymentService.processZalopayCallback(req.body);

        // ZaloPay yêu cầu trả về JSON đúng định dạng này:
        res.json(result);
    } catch (error) {
        console.error("Lỗi callback ZaloPay:", error.message);
        // Trả về lỗi để ZaloPay biết (return_code = 0)
        res.json({ return_code: 0, return_message: error.message });
    }
};
// API công khai (Gateway sẽ chuyển từ Ngrok)
exports.vnpayCallback = async (req, res) => {
    try {
        console.log("--- Nhận Callback từ VNPay ---");
        const vnp_Params = req.query; 
        await paymentService.processVnpayCallback(vnp_Params);
        res.json({ RspCode: '00', Message: 'success' });
    } catch (error) {
        console.error("Lỗi callback VNPay:", error);
        res.json({ RspCode: '97', Message: 'error' });
    }
};
exports.vietqrCallback = async (req, res) => {
    console.log("--- [WEBHOOK] Nhận biến động số dư từ Casso/VietQR ---");
    try {
        // Casso gửi data trong body, và token trong headers
        await paymentService.processVietQRCallback(req.body, req.headers);

        // Luôn trả về success để bên Casso biết mình đã nhận tin
        res.json({ error: 0, message: 'Success' });
    } catch (error) {
        console.error("Lỗi xử lý VietQR:", error.message);
        res.json({ error: 1, message: 'Error' });
    }
};
// API công khai (Gateway sẽ chuyển từ Client 5173)
exports.getPaymentStatus = async (req, res) => {
     try {
        const orderIdWithTimestamp = req.params.orderId;
        
        let realBookingId;
        if (orderIdWithTimestamp.includes('_')) { 
             realBookingId = orderIdWithTimestamp.split('_')[0];
        } else {
             realBookingId = orderIdWithTimestamp; 
        }

        const status = await paymentService.getBookingStatus(realBookingId);
        res.status(200).json({ status: status });
    } catch (error) {
        console.error("Lỗi getPaymentStatus:", error);
        res.status(500).json({ message: "Lỗi server", error: error.message });
    }
};