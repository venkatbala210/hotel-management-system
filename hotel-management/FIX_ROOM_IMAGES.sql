-- SQL script to fix all room images to show actual room photos, not people
-- All images are from Unsplash and show hotel rooms/interiors

USE hotel_booking;

-- Update all rooms with proper room images
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&q=80' WHERE room_type = 'Classic Single';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&q=80' WHERE room_type = 'Standard Double';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=800&q=80' WHERE room_type = 'Deluxe King';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&q=80' WHERE room_type = 'Premium Twin';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=800&q=80' WHERE room_type = 'Garden Terrace';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800&q=80' WHERE room_type = 'City View Deluxe';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800&q=80' WHERE room_type = 'Executive Suite';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800&q=80' WHERE room_type = 'Family Deluxe';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800&q=80' WHERE room_type = 'Ocean View King';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&q=80' WHERE room_type = 'Wellness Retreat';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&q=80' WHERE room_type = 'Business Studio';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=800&q=80' WHERE room_type = 'Junior Suite';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1595576508892-0b3a3e7a8b0e?w=800&q=80' WHERE room_type = 'Presidential Suite';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?w=800&q=80' WHERE room_type = 'Loft Penthouse';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&q=80' WHERE room_type = 'Romantic Suite';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&q=80' WHERE room_type = 'Spa Suite';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800&q=80' WHERE room_type = 'Skyline Penthouse';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800&q=80' WHERE room_type = 'Art Deco Suite';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=800&q=80' WHERE room_type = 'Tropical Paradise';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&q=80' WHERE room_type = 'Modern Minimalist';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800&q=80' WHERE room_type = 'Vintage Classic';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&q=80' WHERE room_type = 'Zen Garden Room';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=800&q=80' WHERE room_type = 'Tech Hub Suite';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?w=800&q=80' WHERE room_type = 'Cozy Cabin';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800&q=80' WHERE room_type = 'Beachfront Bungalow';
UPDATE rooms SET room_photo_url = 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&q=80' WHERE room_type = 'Urban Loft';

-- Verify all rooms have proper room images
SELECT room_type, room_photo_url FROM rooms ORDER BY room_type;

