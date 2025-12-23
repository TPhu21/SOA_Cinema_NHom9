package com.example.bookingservice.enums;

/**
 * Trạng thái vé đặt
 * 
 * Luồng thông thường:
 * PENDING → CONFIRMED → USED
 *         ↘ CANCELLED
 *         → EXPIRED (tự động nếu quá giờ)
 */
public enum BookingStatus {
    /** Đang chờ thanh toán (khách chọn ghế xong, chưa pay) */
    PENDING,
    
    /** Đã thanh toán thành công, chưa sử dụng (chưa vào rạp) */
    CONFIRMED,
    
    /** Đã kiểm vé, khách đã vào rạp xem phim */
    USED,
    
    /** Quá giờ chiếu mà không check-in (hệ thống tự đánh dấu) */
    EXPIRED,
    
    /** Đã hủy vé / hoàn tiền */
    CANCELLED,
}
