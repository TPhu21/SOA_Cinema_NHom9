module.exports = (sequelize, DataTypes) => {
  const User = sequelize.define('User', {
    username: {
      type: DataTypes.STRING(50),
      allowNull: false,
      unique: true
    },
    email: {
      type: DataTypes.STRING(100),
      allowNull: false,
      unique: true
    },
    password: {
      type: DataTypes.STRING(255),
      allowNull: false
    }
  }, {
    tableName: 'users',
    timestamps: true 
  });

  User.associate = (models) => {
    // Nếu bạn muốn liên kết User với các đơn đặt vé (Booking)
    // User.hasMany(models.Booking, { foreignKey: 'userId', as: 'bookings' });
  };

  return User;
};