// services/PaymentService/routes/payments.js
const express = require('express');
const router = express.Router();
// ⭐️ SỬA LỖI: Gọi Controller (Bộ não)
const paymentController = require('../controllers/paymentController'); 

// API nội bộ (BookingService 4003 sẽ gọi)
router.post('/create-link', paymentController.createPaymentLink); 

// API công khai (Gateway sẽ chuyển từ Ngrok)
router.post('/momo-callback', paymentController.momoCallback);
router.get('/vnpay-callback', paymentController.vnpayCallback);

// API công khai (Gateway sẽ chuyển từ Client 5173)
router.get('/status/:orderId', paymentController.getPaymentStatus);

module.exports = router;