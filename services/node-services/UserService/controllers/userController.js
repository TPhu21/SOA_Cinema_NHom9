// services/UserService/controllers/userController.js
const { User } = require('../models'); // Lấy model từ index.js
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

// 1. HÀM ĐĂNG KÝ
exports.register = async (req, res) => {
    try {
        const { username, email, password } = req.body;

        // Kiểm tra xem email đã tồn tại chưa
        const alreadyExists = await User.findOne({ where: { email } });
        if (alreadyExists) {
            return res.status(400).json({ message: "Email đã tồn tại." });
        }

        // Mã hóa mật khẩu
        const hashedPassword = await bcrypt.hash(password, 10);

        // Tạo user mới
        const newUser = await User.create({
            username,
            email,
            password: hashedPassword
        });

        res.status(201).json({ message: "Đăng ký thành công!", userId: newUser.id });

    } catch (error) {
        res.status(500).json({ message: "Lỗi server", error: error.message });
    }
};

// 2. HÀM ĐĂNG NHẬP
exports.login = async (req, res) => {
    try {
        const { email, password } = req.body;

        // Tìm user
        const user = await User.findOne({ where: { email } });
        if (!user) {
            return res.status(404).json({ message: "Không tìm thấy email." });
        }

        // So sánh mật khẩu
        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) {
            return res.status(400).json({ message: "Sai mật khẩu." });
        }

        // Tạo Token (Phiếu bé ngoan)
        const token = jwt.sign(
            { id: user.id, email: user.email }, // Thông tin muốn lưu vào token
            process.env.JWT_SECRET,             // Lấy "mật mã" từ file .env
            { expiresIn: '1h' }                 // Hết hạn sau 1 giờ
        );

        res.status(200).json({
            message: "Đăng nhập thành công!",
            token: token,
            user: {
                id: user.id,
                username: user.username,
                email: user.email
            }
        });

    } catch (error) {
        res.status(500).json({ message: "Lỗi server", error: error.message });
    }
};