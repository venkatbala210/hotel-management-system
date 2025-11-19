-- Sample rooms data for hotel_booking database
-- This file will be executed when the application starts (if spring.sql.init.mode is set to always)

INSERT INTO rooms (room_type, room_price, room_photo_url, room_description) VALUES
('Deluxe King', 199.00, 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=800', 'Spacious room with a king bed, rainfall shower, and city skyline views.'),
('Premium Twin', 159.00, 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800', 'Two plush twin beds, ergonomic workspace, perfect for friends or colleagues.'),
('Executive Suite', 289.00, 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800', 'Separate living area, dining nook, and complimentary executive lounge access.'),
('Family Deluxe', 219.00, 'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800', 'Ideal for families with two queen beds, sofa sleeper, and kid-friendly amenities.'),
('Ocean View King', 249.00, 'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800', 'Panoramic ocean vistas, private balcony, and in-room espresso station.'),
('Garden Terrace', 189.00, 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=800', 'Ground-floor room with private terrace opening onto tranquil gardens.'),
('Presidential Suite', 499.00, 'https://images.unsplash.com/photo-1595576508892-0b3a3e7a8b0e?w=800', 'Two-bedroom suite with grand living room, kitchenette, and butler service.'),
('Wellness Retreat', 269.00, 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800', 'In-room yoga gear, air purification, and complimentary spa hydrotherapy pass.'),
('Business Studio', 179.00, 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800', 'Open-concept layout with sit-stand desk, fast Wi-Fi, and smart TV conferencing.'),
('Loft Penthouse', 379.00, 'https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?w=800', 'Bi-level loft with floor-to-ceiling windows, soaking tub, and private rooftop deck.')
ON DUPLICATE KEY UPDATE room_type=room_type;
