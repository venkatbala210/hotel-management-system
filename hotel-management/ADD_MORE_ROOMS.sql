-- SQL script to add more rooms to the hotel_booking database
-- Organized by categories (Pages 1-4) with diverse images
-- Run this script in MySQL to add rooms directly

USE hotel_booking;

-- PAGE 1: Standard & Deluxe Rooms (6 rooms)
INSERT INTO rooms (room_type, room_price, room_photo_url, room_description, price, room_number, capacity, is_available, status) VALUES
('Classic Single', 129.00, 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800&q=80', 'Cozy single room perfect for solo travelers with modern amenities and city views.', 129.00, 101, 1, 1, 'AVAILABLE'),
('Standard Double', 149.00, 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&q=80', 'Comfortable double room with queen bed, work desk, and complimentary Wi-Fi.', 149.00, 102, 2, 1, 'AVAILABLE'),
('Deluxe King', 199.00, 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=800&q=80', 'Spacious room with a king bed, rainfall shower, and city skyline views.', 199.00, 103, 2, 1, 'AVAILABLE'),
('Premium Twin', 159.00, 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&q=80', 'Two plush twin beds, ergonomic workspace, perfect for friends or colleagues.', 159.00, 104, 2, 1, 'AVAILABLE'),
('Garden Terrace', 189.00, 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=800&q=80', 'Ground-floor room with private terrace opening onto tranquil gardens.', 189.00, 105, 2, 1, 'AVAILABLE'),
('City View Deluxe', 209.00, 'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800&q=80', 'Elegant room with panoramic city views, king bed, and marble bathroom.', 209.00, 106, 2, 1, 'AVAILABLE'),

-- PAGE 2: Suites & Premium Rooms (6 rooms)
('Executive Suite', 289.00, 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800&q=80', 'Separate living area, dining nook, and complimentary executive lounge access.', 289.00, 201, 4, 1, 'AVAILABLE'),
('Family Deluxe', 219.00, 'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800&q=80', 'Ideal for families with two queen beds, sofa sleeper, and kid-friendly amenities.', 219.00, 202, 4, 1, 'AVAILABLE'),
('Ocean View King', 249.00, 'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800&q=80', 'Panoramic ocean vistas, private balcony, and in-room espresso station.', 249.00, 203, 2, 1, 'AVAILABLE'),
('Wellness Retreat', 269.00, 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&q=80', 'In-room yoga gear, air purification, and complimentary spa hydrotherapy pass.', 269.00, 204, 2, 1, 'AVAILABLE'),
('Business Studio', 179.00, 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&q=80', 'Open-concept layout with sit-stand desk, fast Wi-Fi, and smart TV conferencing.', 179.00, 205, 2, 1, 'AVAILABLE'),
('Junior Suite', 229.00, 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=800&q=80', 'Spacious suite with separate sitting area, mini-bar, and premium bedding.', 229.00, 206, 3, 1, 'AVAILABLE'),

-- PAGE 3: Luxury & Special Rooms (6 rooms)
('Presidential Suite', 499.00, 'https://images.unsplash.com/photo-1595576508892-0b3a3e7a8b0e?w=800&q=80', 'Two-bedroom suite with grand living room, kitchenette, and butler service.', 499.00, 301, 6, 1, 'AVAILABLE'),
('Loft Penthouse', 379.00, 'https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?w=800&q=80', 'Bi-level loft with floor-to-ceiling windows, soaking tub, and private rooftop deck.', 379.00, 302, 4, 1, 'AVAILABLE'),
('Romantic Suite', 329.00, 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&q=80', 'Intimate suite with king bed, jacuzzi, champagne service, and sunset views.', 329.00, 303, 2, 1, 'AVAILABLE'),
('Spa Suite', 349.00, 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&q=80', 'Luxury suite with private sauna, steam room, and in-room massage area.', 349.00, 304, 2, 1, 'AVAILABLE'),
('Skyline Penthouse', 429.00, 'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800&q=80', 'Top-floor penthouse with 360-degree views, private elevator, and rooftop access.', 429.00, 305, 4, 1, 'AVAILABLE'),
('Art Deco Suite', 299.00, 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800&q=80', 'Elegantly designed suite with vintage furnishings and curated art collection.', 299.00, 306, 3, 1, 'AVAILABLE'),

-- PAGE 4: Themed & Unique Rooms (8 rooms)
('Tropical Paradise', 239.00, 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=800&q=80', 'Caribbean-inspired room with bamboo accents, tropical plants, and ocean breeze.', 239.00, 401, 2, 1, 'AVAILABLE'),
('Modern Minimalist', 199.00, 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&q=80', 'Sleek contemporary design with smart home controls and minimalist aesthetics.', 199.00, 402, 2, 1, 'AVAILABLE'),
('Vintage Classic', 219.00, 'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800&q=80', 'Timeless elegance with antique furniture, four-poster bed, and period details.', 219.00, 403, 2, 1, 'AVAILABLE'),
('Zen Garden Room', 249.00, 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&q=80', 'Peaceful retreat with Japanese design, meditation space, and garden views.', 249.00, 404, 2, 1, 'AVAILABLE'),
('Tech Hub Suite', 279.00, 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=800&q=80', 'Fully equipped tech suite with multiple monitors, gaming setup, and high-speed internet.', 279.00, 405, 2, 1, 'AVAILABLE'),
('Cozy Cabin', 189.00, 'https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?w=800&q=80', 'Rustic charm with wood paneling, fireplace, and mountain-inspired decor.', 189.00, 406, 2, 1, 'AVAILABLE'),
('Beachfront Bungalow', 269.00, 'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800&q=80', 'Direct beach access with private deck, outdoor shower, and hammock.', 269.00, 407, 2, 1, 'AVAILABLE'),
('Urban Loft', 259.00, 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&q=80', 'Industrial-chic loft with exposed brick, high ceilings, and city skyline views.', 259.00, 408, 3, 1, 'AVAILABLE');

-- Verify rooms were added
SELECT COUNT(*) as total_rooms FROM rooms;
SELECT room_type, room_price FROM rooms ORDER BY room_price DESC;
