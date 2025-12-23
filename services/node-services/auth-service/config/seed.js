// Seed users: admin, staff
const bcrypt = require('bcrypt');

module.exports = async (db) => {
  try {
    const User = db.User;

    // Kiểm tra đã có dữ liệu chưa
    const count = await User.count();
    if (count > 0) {
      console.log('✓ Database đã có users, bỏ qua seed.');
      return;
    }

    console.log('🌱 Bắt đầu seed users...');

    // Hash passwords
    const adminPassword = await bcrypt.hash('admin', 10);
    const staffPassword = await bcrypt.hash('staff', 10);

    // Tạo users
    const users = await User.bulkCreate([
      {
        username: 'admin',
        email: 'admin@cinemax.vn',
        password: adminPassword,
        fullName: 'System Admin',
        phone: '0901234567',
        role: 'ADMIN',
        status: 'ACTIVE'
      },
      {
        username: 'staff',
        email: 'staff@cinemax.vn',
        password: staffPassword,
        fullName: 'Staff Member',
        phone: '0987654321',
        role: 'STAFF',
        status: 'ACTIVE'
      }
    
    ]);

    console.log('✓ Đã tạo 3 users:');
    console.log('  - Admin: admin / admin');
    console.log('  - Staff: staff / staff');

  } catch (error) {
    console.error('❌ Lỗi khi seed users:', error.message);
    throw error;
  }
};
