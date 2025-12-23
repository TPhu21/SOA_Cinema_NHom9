require('dotenv').config();
const jwt = require('jsonwebtoken');

/**
 * Middleware xác thực JWT token
 */
const auth = (req, res, next) => {
    try {
        const authHeader = req.header('Authorization');
        if (!authHeader) return res.status(401).json({ error: 'Không có token xác thực.' });

        const token = authHeader.startsWith('Bearer ')
            ? authHeader.slice(7)
            : authHeader;

        if (!token) return res.status(401).json({ error: 'Token không hợp lệ.' });

        const decoded = jwt.verify(token, process.env.JWT_SECRET);

        // ✅ Chuẩn hoá userId: ưu tiên sub, fallback id
        const userId = decoded.sub || decoded.id;
        if (!userId) return res.status(401).json({ error: 'Token thiếu user id.' });

        // đảm bảo các handler phía sau dùng req.user.id vẫn chạy
        decoded.id = decoded.id || userId;

        req.user = decoded;
        req.userId = String(userId);

        next();
    } catch (error) {
        return res.status(401).json({ error: 'Token hết hạn hoặc không hợp lệ.' });
    }
};

/**
 * Middleware phân quyền theo role
 *
 * ROLE HIERARCHY:
 * - ADMIN: Toàn quyền (quản lý users, movies, cinemas, showtimes, bookings, reports)
 * - STAFF: Quản lý suất chiếu, kiểm vé, xem báo cáo hạn chế
 * - CUSTOMER: Đặt vé, xem phim, thanh toán
 *
 * @param  {...string} allowedRoles - Các role được phép truy cập
 *
 * Ví dụ sử dụng:
 * router.get('/admin-only', auth, requireRole('ADMIN'), handler);
 * router.get('/staff-or-admin', auth, requireRole('STAFF', 'ADMIN'), handler);
 */
const requireRole = (...allowedRoles) => {
    return async (req, res, next) => {
        try {
            // Lấy user từ database để có role mới nhất
            const { User } = require('../models');
            const user = await User.findByPk(req.userId || req.user.id || req.user.sub);
            if (!user) {
                return res.status(404).json({ error: 'Không tìm thấy người dùng.' });
            }

            if (!allowedRoles.includes(user.role)) {
                return res.status(403).json({
                    error: 'Bạn không có quyền truy cập chức năng này.',
                    required: allowedRoles,
                    your_role: user.role
                });
            }

            // Gắn thêm role vào request để các handler sau có thể dùng
            req.userRole = user.role;
            next();
        } catch (error) {
            console.error('Role check error:', error);
            res.status(500).json({ error: 'Lỗi kiểm tra quyền truy cập.' });
        }
    };
};

/**
 * Middleware cho Admin only
 */
const adminOnly = requireRole('ADMIN');

/**
 * Middleware cho Staff hoặc Admin
 */
const staffOrAdmin = requireRole('STAFF', 'ADMIN');

module.exports = auth;
module.exports.requireRole = requireRole;
module.exports.adminOnly = adminOnly;
module.exports.staffOrAdmin = staffOrAdmin;