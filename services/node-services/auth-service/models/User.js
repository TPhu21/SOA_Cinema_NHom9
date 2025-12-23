module.exports = (sequelize, DataTypes) => {
  const User = sequelize.define('User', {
    username: {
      type: DataTypes.STRING(50),
      allowNull: false,
    },
    email: {
      type: DataTypes.STRING(100),
      allowNull: false,
      unique: true
    },
    password: {
      type: DataTypes.STRING(255),
      allowNull: true
    },
    phone: {
      type: DataTypes.STRING(15),
      allowNull: true
    },
    googleId: {
      type: DataTypes.STRING,
      allowNull: true,
      unique: true
    },
    facebookId: {
      type: DataTypes.STRING,
      allowNull: true,
      unique: true
    },
    // ============================================================
    // ROLE SYSTEM
    // ============================================================
    // CUSTOMER: Khách hàng - Đặt vé, xem phim, thanh toán
    // STAFF: Nhân viên - Quản lý suất chiếu, kiểm vé, hỗ trợ khách hàng
    // ADMIN: Quản trị viên - Toàn quyền quản lý hệ thống
    // ============================================================
    role: {
      type: DataTypes.ENUM('CUSTOMER', 'STAFF', 'ADMIN'),
      allowNull: false,
      defaultValue: 'CUSTOMER'
    },
  }, {
    tableName: 'users',
    timestamps: true 
  });
  
  
  return User;
};