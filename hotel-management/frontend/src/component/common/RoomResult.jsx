import React from 'react';
import { useNavigate } from 'react-router-dom'; // Import useNavigate
import ApiService from '../../service/ApiService';

const RoomResult = ({ roomSearchResults }) => {
  const navigate = useNavigate(); // Initialize useNavigate hook
  const isAdmin = ApiService.isAdmin();
  return (
    <section className="room-results">
      {roomSearchResults && roomSearchResults.length > 0 && (
        <div className="room-list">
          {roomSearchResults.map((room, index) => {
            const key = room.id ?? `fallback-room-${index}`;
            // Check if room has a valid ID (not null, undefined, or 0)
            const hasValidId = room.id != null && room.id !== undefined && room.id !== 0;
            const handleAdminClick = () => {
              if (!hasValidId) return;
              navigate(`/admin/edit-room/${room.id}`);
            };
            const handleBookClick = () => {
              if (!hasValidId) {
                console.warn('Cannot book room: missing room ID', room);
                alert('This room is not available for booking. Please try another room.');
                return;
              }
              // Check if user is authenticated, if not redirect to login
              if (!ApiService.isAuthenticated()) {
                navigate('/login', { state: { from: { pathname: `/room-details-book/${room.id}` } } });
              } else {
                navigate(`/room-details-book/${room.id}`);
              }
            };
            const handleImageError = (e) => {
              // Fallback to a placeholder image if the image fails to load
              console.error('Image failed to load:', room.roomPhotoUrl);
              e.target.onerror = null; // Prevent infinite loop
              e.target.src = 'https://via.placeholder.com/400x300?text=Room+Image';
            };

            const handleImageLoad = (e) => {
              // Image loaded successfully
              e.target.style.display = 'block';
              e.target.style.opacity = '1';
            };

            // Debug: Log room data
            if (index === 0) {
              console.log('Room data:', { id: room.id, roomType: room.roomType, roomPhotoUrl: room.roomPhotoUrl, hasValidId });
            }

            return (
              <div key={key} className="room-list-item">
                {room.roomPhotoUrl ? (
                  <img
                    className="room-list-item-image"
                    src={room.roomPhotoUrl.trim()}
                    alt={room.roomType || 'Room image'}
                    onError={handleImageError}
                    onLoad={handleImageLoad}
                    loading="lazy"
                  />
                ) : (
                  <div className="room-list-item-image" style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    backgroundColor: '#e0e0e0',
                    color: '#999',
                    fontSize: '14px'
                  }}>
                    No Image
                  </div>
                )}
                <div className="room-details">
                  <h3>{room.roomType}</h3>
                  <p>Price: ${room.roomPrice} / night</p>
                  <p>Description: {room.roomDescription}</p>
                </div>

                <div className="book-now-div">
                  {isAdmin ? (
                    <button
                      className="edit-room-button"
                      onClick={handleAdminClick}
                      disabled={!hasValidId}
                    >
                      {hasValidId ? 'Edit Room' : 'Edit Unavailable'}
                    </button>
                  ) : (
                    <button
                      className="book-now-button"
                      onClick={handleBookClick}
                    >
                      View/Book Room
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </section>
  );
};

export default RoomResult;
