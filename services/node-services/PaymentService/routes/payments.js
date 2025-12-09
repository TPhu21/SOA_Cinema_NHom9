// services/PaymentService/routes/payments.js
const express = require('express');
const router = express.Router();
// ⭐️ SỬA LỖI: Gọi Controller (Bộ não)
const paymentController = require('../controllers/paymentController'); 

router.post('/create-payment-link', paymentController.createPaymentLinkApi);

// API công khai (Gateway sẽ chuyển từ Ngrok)
router.post('/momo-callback', paymentController.momoCallback);
router.get('/vnpay-callback', paymentController.vnpayCallback);
router.post('/zalopay-callback', paymentController.zalopayCallback);
router.post('/vietqr-callback', paymentController.vietqrCallback);

// API công khai (Gateway sẽ chuyển từ Client 5173)
router.get('/status/:orderId', paymentController.getPaymentStatus);

module.exports = router;