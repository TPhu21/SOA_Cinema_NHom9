// services/PaymentService/models/Payment.js
module.exports = (sequelize, DataTypes) => {
  const Payment = sequelize.define('Payment', {
    // ⭐️ QUAN TRỌNG: Chỉ lưu ID, không liên kết (belongsTo)
    bookingId: { 
      type: DataTypes.INTEGER, 
      allowNull: false
    },
    amount: {
      type: DataTypes.DECIMAL(10, 2),
      allowNull: false
    },
    provider: {
      type: DataTypes.STRING,
      defaultValue: 'momo'
    },
    transId: {
      type: DataTypes.STRING, 
      allowNull: true
    },
    resultCode: {
      type: DataTypes.INTEGER,
      allowNull: true
    }
    // ... (các trường khác nếu bạn muốn)
  });

  // ⭐️ XÓA HÀM ASSOCIATE CŨ ĐI ⭐️
  Payment.associate = (models) => {
      // (Để trống)
  };

  return Payment;
};