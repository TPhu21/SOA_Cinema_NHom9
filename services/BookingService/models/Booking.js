// services/BookingService/models/Booking.js
module.exports = (sequelize, DataTypes) => {
  const Booking = sequelize.define('Booking', {
    showtimeId: {
      type: DataTypes.INTEGER, 
      allowNull: false
    },
    seats: {
      type: DataTypes.JSON, 
      allowNull: false
    },
    totalPrice: {
      type: DataTypes.DECIMAL(10, 2), 
      allowNull: false
    },
    status: {
      type: DataTypes.ENUM('PENDING', 'CONFIRMED', 'FAILED'),
      defaultValue: 'PENDING'
    },
    expireAt: {
      type: DataTypes.DATE,
      defaultValue: DataTypes.NOW
    },

    // ⭐️ QUAN TRỌNG: Chúng ta không 'liên kết' (belongsTo)
    // Chúng ta chỉ lưu ID của User (dạng số)
    userId: {
        type: DataTypes.INTEGER,
        allowNull: true // Cho phép khách vãng lai
    }
  });

  // Chúng ta xóa hết hàm associate cũ đi vì service này không biết User là ai
  Booking.associate = (models) => {
      // (Để trống)
  };

  return Booking;
};