
// services/PaymentService/controllers/paymentController.js
const paymentService = require('../services/paymentService'); // 👈 SỬA LẠI: Gọi "Nghiệp vụ" CỦA CHÍNH NÓ

// API nội bộ (BookingService 4003 sẽ gọi)
exports.createPaymentLink = async (req, res) => {
    
    console.log("[CONTROLLER 4004] 💡: Đã vào hàm createPaymentLink.");

    try {
        console.log("[CONTROLLER 4004] 💡: Chuẩn bị gọi service (paymentService.js)...");
        
        // Dòng này gọi service "chủ xị" (4004)
        const result = await paymentService.createPaymentLink(req.body);
        
        console.log("[CONTROLLER 4004] 💡: Service đã trả về, gửi JSON.");
        res.json(result); // Trả link về cho BookingService (4003)

    } catch (error) {
        console.error("==== ☠️ LỖI TẠI PaymentController (4004) ☠️ ====");
        console.error(error); 
        res.status(400).json({ 
            message: "Lỗi Server (4004): " + (error.message || 'Lỗi không xác định') 
        });
    }
};

// API công khai (Gateway sẽ chuyển từ Ngrok)
exports.momoCallback = async (req, res) => {
    try {
        console.log("--- Nhận Callback từ Momo ---");
        await paymentService.processMomoCallback(req.body); 
        res.status(204).send(); 
    } catch (error) {
        console.error("Lỗi callback Momo:", error);
        res.status(500).send();
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