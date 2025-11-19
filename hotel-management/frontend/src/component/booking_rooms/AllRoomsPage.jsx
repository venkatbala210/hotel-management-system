import React, { useState, useEffect } from 'react';
import ApiService from '../../service/ApiService';
import Pagination from '../common/Pagination';
import RoomResult from '../common/RoomResult';
import RoomSearch from '../common/RoomSearch';

const FALLBACK_ROOMS = [
  {
    id: null,
    roomType: 'Deluxe King',
    roomPrice: 199,
    roomDescription: 'Spacious room with a king bed, rainfall shower, and city skyline views.',
    roomPhotoUrl: '/assets/images/rooms/download.jpg'
  },
  {
    id: null,
    roomType: 'Premium Twin',
    roomPrice: 159,
    roomDescription: 'Two plush twin beds, ergonomic workspace, perfect for friends or colleagues.',
    roomPhotoUrl: '/assets/images/rooms/download (1).jpg'
  },
  {
    id: null,
    roomType: 'Executive Suite',
    roomPrice: 289,
    roomDescription: 'Separate living area, dining nook, and complimentary executive lounge access.',
    roomPhotoUrl: '/assets/images/rooms/download (2).jpg'
  },
  {
    id: null,
    roomType: 'Family Deluxe',
    roomPrice: 219,
    roomDescription: 'Ideal for families with two queen beds, sofa sleeper, and kid-friendly amenities.',
    roomPhotoUrl: '/assets/images/rooms/download (3).jpg'
  },
  {
    id: null,
    roomType: 'Ocean View King',
    roomPrice: 249,
    roomDescription: 'Panoramic ocean vistas, private balcony, and in-room espresso station.',
    roomPhotoUrl: '/assets/images/rooms/download (4).jpg'
  },
  {
    id: null,
    roomType: 'Garden Terrace',
    roomPrice: 189,
    roomDescription: 'Ground-floor room with private terrace opening onto tranquil gardens.',
    roomPhotoUrl: '/assets/images/rooms/download (5).jpg'
  },
  {
    id: null,
    roomType: 'Presidential Suite',
    roomPrice: 499,
    roomDescription: 'Two-bedroom suite with grand living room, kitchenette, and butler service.',
    roomPhotoUrl: '/assets/images/rooms/download (6).jpg'
  },
  {
    id: null,
    roomType: 'Wellness Retreat',
    roomPrice: 269,
    roomDescription: 'In-room yoga gear, air purification, and complimentary spa hydrotherapy pass.',
    roomPhotoUrl: '/assets/images/rooms/images.jpg'
  },
  {
    id: null,
    roomType: 'Business Studio',
    roomPrice: 179,
    roomDescription: 'Open-concept layout with sit-stand desk, fast Wi-Fi, and smart TV conferencing.',
    roomPhotoUrl: '/assets/images/rooms/images (1).jpg'
  },
  {
    id: null,
    roomType: 'Loft Penthouse',
    roomPrice: 379,
    roomDescription: 'Bi-level loft with floor-to-ceiling windows, soaking tub, and private rooftop deck.',
    roomPhotoUrl: '/assets/images/rooms/images (2).jpg'
  },
  {
    id: null,
    roomType: 'Skyline Premier',
    roomPrice: 269,
    roomDescription: 'Upper-floor haven boasting wraparound windows and curated mini bar.',
    roomPhotoUrl: '/assets/images/rooms/images (3).jpg'
  },
  {
    id: null,
    roomType: 'Courtyard Queen',
    roomPrice: 169,
    roomDescription: 'Cozy retreat with a plush queen bed and soothing courtyard views.',
    roomPhotoUrl: '/assets/images/rooms/images (4).jpg'
  },
  {
    id: null,
    roomType: 'Artist Loft',
    roomPrice: 239,
    roomDescription: 'Industrial-chic loft with curated artwork and open-plan living space.',
    roomPhotoUrl: '/assets/images/rooms/images (5).jpg'
  },
  {
    id: null,
    roomType: 'Honeymoon Hideaway',
    roomPrice: 299,
    roomDescription: 'Romantic suite featuring a four-poster bed, whirlpool tub, and candlelight lighting package.',
    roomPhotoUrl: '/assets/images/rooms/images (6).jpg'
  }
];

const FALLBACK_ROOM_TYPES = [...new Set(FALLBACK_ROOMS.map((room) => room.roomType))];

const AllRoomsPage = () => {
  const [rooms, setRooms] = useState([]);
  const [filteredRooms, setFilteredRooms] = useState([]);
  const [roomTypes, setRoomTypes] = useState([]);
  const [selectedRoomType, setSelectedRoomType] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [roomsPerPage] = useState(5);

  // Function to handle search results
  const handleSearchResult = (results) => {
    setRooms(results);
    setFilteredRooms(results);
  };


  useEffect(() => {
    const fetchRooms = async () => {
      try {
        // Try to get all available rooms first (no authentication needed)
        const response = await ApiService.getAllAvailableRooms();
        const allRooms = response?.roomList;
        if (Array.isArray(allRooms) && allRooms.length > 0) {
          setRooms(allRooms);
          setFilteredRooms(allRooms);
        } else {
          // If no available rooms, try to get all rooms (might require auth)
          try {
            const allRoomsResponse = await ApiService.getAllRooms();
            const allRoomsList = allRoomsResponse?.roomList;
            if (Array.isArray(allRoomsList) && allRoomsList.length > 0) {
              setRooms(allRoomsList);
              setFilteredRooms(allRoomsList);
            } else {
              // Only use fallback if both API calls fail
              console.warn('No rooms found in database, using fallback rooms');
              setRooms(FALLBACK_ROOMS);
              setFilteredRooms(FALLBACK_ROOMS);
            }
          } catch (allRoomsError) {
            console.error('Error fetching all rooms:', allRoomsError.message);
            // Only use fallback if both API calls fail
            setRooms(FALLBACK_ROOMS);
            setFilteredRooms(FALLBACK_ROOMS);
          }
        }
      } catch (error) {
        console.error('Error fetching available rooms:', error.message);
        // Try fallback to getAllRooms
        try {
          const allRoomsResponse = await ApiService.getAllRooms();
          const allRoomsList = allRoomsResponse?.roomList;
          if (Array.isArray(allRoomsList) && allRoomsList.length > 0) {
            setRooms(allRoomsList);
            setFilteredRooms(allRoomsList);
          } else {
            setRooms(FALLBACK_ROOMS);
            setFilteredRooms(FALLBACK_ROOMS);
          }
        } catch (allRoomsError) {
          console.error('Error fetching all rooms:', allRoomsError.message);
          setRooms(FALLBACK_ROOMS);
          setFilteredRooms(FALLBACK_ROOMS);
        }
      }
    };

    const fetchRoomTypes = async () => {
      try {
        const types = await ApiService.getRoomTypes();
        if (Array.isArray(types) && types.length > 0) {
          setRoomTypes(types);
        } else {
          setRoomTypes(FALLBACK_ROOM_TYPES);
        }
      } catch (error) {
        console.error('Error fetching room types:', error.message);
        setRoomTypes(FALLBACK_ROOM_TYPES);
      }
    };

    fetchRooms();
    fetchRoomTypes();
  }, []);

  const handleRoomTypeChange = (e) => {
    setSelectedRoomType(e.target.value);
    filterRooms(e.target.value);
  };

  const filterRooms = (type) => {
    if (type === '') {
      setFilteredRooms(rooms);
    } else {
      const filtered = rooms.filter((room) => room.roomType === type);
      setFilteredRooms(filtered);
    }
    setCurrentPage(1); // Reset to first page after filtering
  };

  // Pagination
  const indexOfLastRoom = currentPage * roomsPerPage;
  const indexOfFirstRoom = indexOfLastRoom - roomsPerPage;
  const currentRooms = filteredRooms.slice(indexOfFirstRoom, indexOfLastRoom);

  // Change page
  const paginate = (pageNumber) => setCurrentPage(pageNumber);

  return (
    <div className='all-rooms'>
      <h2>All Rooms</h2>
      <div className='all-room-filter-div'>
        <label>Filter by Room Type:</label>
        <select value={selectedRoomType} onChange={handleRoomTypeChange}>
          <option value="">All</option>
          {roomTypes.map((type) => (
            <option key={type} value={type}>
              {type}
            </option>
          ))}
        </select>
      </div>
      
      <RoomSearch handleSearchResult={handleSearchResult} />
      <RoomResult roomSearchResults={currentRooms} />

      <Pagination
        roomsPerPage={roomsPerPage}
        totalRooms={filteredRooms.length}
        currentPage={currentPage}
        paginate={paginate}
      />
    </div>
  );
};

export default AllRoomsPage;
