// services/MovieService/models/Movie.js
module.exports = (sequelize, DataTypes) => {
  const Movie = sequelize.define('Movie', {
    title: {
      type: DataTypes.STRING,
      allowNull: false
    },
    description: {
      type: DataTypes.TEXT
    },
    posterUrl: {
      type: DataTypes.STRING
    },
    duration: {
      type: DataTypes.INTEGER // Số phút
    }
    // ... (Thêm các trường khác của phim)
  });

  Movie.associate = (models) => {
    // Một Phim (Movie) có nhiều Suất chiếu (Showtime)
    Movie.hasMany(models.Showtime, { foreignKey: 'movieId', as: 'showtimes' });
  };

  return Movie;
};