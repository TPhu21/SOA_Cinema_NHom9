// services/MovieService/models/Showtime.js
module.exports = (sequelize, DataTypes) => {
  const Showtime = sequelize.define('Showtime', {
    startTime: {
      type: DataTypes.DATE,
      allowNull: false
    },
    roomId: {
      type: DataTypes.STRING, 
      allowNull: false
    },
    price: {
      type: DataTypes.INTEGER,
      defaultValue: 50000
    }
  });

  Showtime.associate = (models) => {
    // Một Suất chiếu (Showtime) thuộc về một Phim (Movie)
    Showtime.belongsTo(models.Movie, { foreignKey: 'movieId', as: 'movie' });
  };

  return Showtime;
};