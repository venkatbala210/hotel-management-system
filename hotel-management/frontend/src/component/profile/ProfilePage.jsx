import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import ApiService from '../../service/ApiService';

const ProfilePage = () => {
    const [user, setUser] = useState(null);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchUserProfile = async () => {
            try {
                const response = await ApiService.getUserProfile();
                // Fetch user bookings using the fetched user ID
                const userPlusBookings = await ApiService.getUserBookings(response.user.id);
                setUser(userPlusBookings.user)

            } catch (error) {
                setError(error.response?.data?.message || error.message);
            }
        };

        fetchUserProfile();
    }, []);

    const handleLogout = () => {
        ApiService.logout();
        navigate('/home');
    };

    const handleEditProfile = () => {
        navigate('/edit-profile');
    };

    const handleCancelBooking = async (bookingId) => {
        if (window.confirm('Are you sure you want to cancel this booking?')) {
            try {
                const response = await ApiService.cancelBooking(bookingId);
                if (response.statusCode === 200) {
                    // Refresh the user profile to get updated bookings
                    const userProfile = await ApiService.getUserProfile();
                    const userPlusBookings = await ApiService.getUserBookings(userProfile.user.id);
                    setUser(userPlusBookings.user);
                    setError(null);
                } else {
                    setError(response.message || 'Failed to cancel booking');
                }
            } catch (error) {
                setError(error.response?.data?.message || 'Failed to cancel booking');
            }
        }
    };

    return (
        <div className="profile-page">
            {user && <h2>Welcome, {user.name}</h2>}
            <div className="profile-actions">
                <button className="edit-profile-button" onClick={handleEditProfile}>Edit Profile</button>
                <button className="logout-button" onClick={handleLogout}>Logout</button>
            </div>
            {error && <p className="error-message">{error}</p>}
            {user && (
                <div className="profile-details">
                    <h3>My Profile Details</h3>
                    <p><strong>Email:</strong> {user.email}</p>
                    <p><strong>Phone Number:</strong> {user.phoneNumber}</p>
                </div>
            )}
            <div className="bookings-section">
                <h3>My Booking History</h3>
                <div className="booking-list">
                    {user && user.bookings.length > 0 ? (
                        user.bookings.map((booking) => (
                            <div key={booking.id} className="booking-item">
                                <p><strong>Booking Code:</strong> {booking.bookingConfirmationCode}</p>
                                <p><strong>Status:</strong> 
                                    <span className={booking.status === 'CONFIRMED' ? 'booking-confirmed' : 'booking-cancelled'}>
                                        {booking.status || 'CONFIRMED'}
                                    </span>
                                </p>
                                <p><strong>Check-in Date:</strong> {booking.checkInDate}</p>
                                <p><strong>Check-out Date:</strong> {booking.checkOutDate}</p>
                                <p><strong>Total Guests:</strong> {booking.totalNumOfGuest}</p>
                                <p><strong>Room Type:</strong> {booking.room.roomType}</p>
                                {booking.payment && (
                                    <div className="payment-details">
                                        <h4>Payment Information</h4>
                                        <p><strong>Amount:</strong> ${booking.payment.amount}</p>
                                        <p><strong>Status:</strong> 
                                            <span className={booking.payment.status === 'SUCCESS' ? 'payment-success' : 'payment-failed'}>
                                                {booking.payment.status}
                                            </span>
                                        </p>
                                        <p><strong>Payment Date:</strong> {new Date(booking.payment.paymentDate).toLocaleString()}</p>
                                    </div>
                                )}
                                {booking.status === 'CONFIRMED' && (
                                    <button 
                                        className="cancel-booking-button" 
                                        onClick={() => handleCancelBooking(booking.id)}
                                    >
                                        Cancel Booking
                                    </button>
                                )}
                                <img 
                                  src={booking.room.roomPhotoUrl || 'https://via.placeholder.com/400x300?text=Room+Image'} 
                                  alt="Room" 
                                  className="room-photo"
                                  onError={(e) => {
                                    e.target.onerror = null;
                                    e.target.src = 'https://via.placeholder.com/400x300?text=Room+Image';
                                  }}
                                />
                            </div>
                        ))
                    ) : (
                        <p>No bookings found.</p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ProfilePage;
