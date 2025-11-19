-- SQL script to add sample rooms to the hotel_booking database
-- Run this script in MySQL to add rooms directly

USE hotel_booking;

-- Delete existing rooms (optional - comment out if you want to keep existing rooms)
-- DELETE FROM rooms;

-- Insert sample rooms (with all required fields)
INSERT INTO rooms (room_type, room_price, room_photo_url, room_description, price, room_number, capacity, is_available, status) VALUES
('Deluxe King', 199.00, 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=800', 'Spacious room with a king bed, rainfall shower, and city skyline views.', 199.00, 101, 2, 1, 'AVAILABLE'),
('Premium Twin', 159.00, 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800', 'Two plush twin beds, ergonomic workspace, perfect for friends or colleagues.', 159.00, 102, 2, 1, 'AVAILABLE'),
('Executive Suite', 289.00, 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800', 'Separate living area, dining nook, and complimentary executive lounge access.', 289.00, 201, 4, 1, 'AVAILABLE'),
('Family Deluxe', 219.00, 'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800', 'Ideal for families with two queen beds, sofa sleeper, and kid-friendly amenities.', 219.00, 103, 4, 1, 'AVAILABLE'),
('Ocean View King', 249.00, 'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800', 'Panoramic ocean vistas, private balcony, and in-room espresso station.', 249.00, 301, 2, 1, 'AVAILABLE'),
('Garden Terrace', 189.00, 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=800', 'Ground-floor room with private terrace opening onto tranquil gardens.', 189.00, 104, 2, 1, 'AVAILABLE'),
('Presidential Suite', 499.00, 'https://images.unsplash.com/photo-1595576508892-0b3a3e7a8b0e?w=800', 'Two-bedroom suite with grand living room, kitchenette, and butler service.', 499.00, 401, 6, 1, 'AVAILABLE'),
('Wellness Retreat', 269.00, 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800', 'In-room yoga gear, air purification, and complimentary spa hydrotherapy pass.', 269.00, 202, 2, 1, 'AVAILABLE'),
('Business Studio', 179.00, 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800', 'Open-concept layout with sit-stand desk, fast Wi-Fi, and smart TV conferencing.', 179.00, 105, 2, 1, 'AVAILABLE'),
('Loft Penthouse', 379.00, 'https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?w=800', 'Bi-level loft with floor-to-ceiling windows, soaking tub, and private rooftop deck.', 379.00, 501, 4, 1, 'AVAILABLE');

-- Verify rooms were added
SELECT * FROM rooms;

