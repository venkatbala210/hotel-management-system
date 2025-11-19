import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import ApiService from '../../service/ApiService'; // Assuming your service is in a file called ApiService.js
import DatePicker from 'react-datepicker';
import PaymentModal from '../common/PaymentModal';
// import 'react-datepicker/dist/react-datepicker.css';

const RoomDetailsPage = () => {
  const navigate = useNavigate(); // Access the navigate function to navigate
  const { roomId } = useParams(); // Get room ID from URL parameters
  const [roomDetails, setRoomDetails] = useState(null);
  const [isLoading, setIsLoading] = useState(true); // Track loading state
  const [error, setError] = useState(null); // Track any errors
  const [checkInDate, setCheckInDate] = useState(null); // State variable for check-in date
  const [checkOutDate, setCheckOutDate] = useState(null); // State variable for check-out date
  const [numAdults, setNumAdults] = useState(1); // State variable for number of adults
  const [numChildren, setNumChildren] = useState(0); // State variable for number of children
  const [totalPrice, setTotalPrice] = useState(0); // State variable for total booking price
  const [totalGuests, setTotalGuests] = useState(1); // State variable for total number of guests
  const [showDatePicker, setShowDatePicker] = useState(false); // State variable to control date picker visibility
  const [userId, setUserId] = useState(''); // Set user id
  const [showMessage, setShowMessage] = useState(false); // State variable to control message visibility
  const [confirmationCode, setConfirmationCode] = useState(''); // State variable for booking confirmation code
  const [errorMessage, setErrorMessage] = useState(''); // State variable for error message
  const [currentBookingId, setCurrentBookingId] = useState(null); // State to store booking ID for payment
  const [showPaymentModal, setShowPaymentModal] = useState(false); // State to show payment modal
  const navigationTimeoutRef = useRef(null); // Ref to store timeout ID for cleanup

  // Cleanup timeout on component unmount
  useEffect(() => {
    return () => {
      if (navigationTimeoutRef.current) {
        clearTimeout(navigationTimeoutRef.current);
      }
    };
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setIsLoading(true); // Set loading state to true
        // Fetch room details (public endpoint, no auth needed)
        const response = await ApiService.getRoomById(roomId);
        setRoomDetails(response.room);
        
        // Fetch user profile only if authenticated (for booking)
        // This will be called when user clicks "Book Now" if not already fetched
        if (ApiService.isAuthenticated()) {
          try {
            const userProfile = await ApiService.getUserProfile();
            if (userProfile && userProfile.user && userProfile.user.id) {
              setUserId(userProfile.user.id);
            }
          } catch (profileError) {
            // User profile fetch failed - will fetch when booking
            console.warn('Could not fetch user profile:', profileError.message);
          }
        }
      } catch (error) {
        setError(error.response?.data?.message || error.message);
      } finally {
        setIsLoading(false); // Set loading state to false after fetching or error
      }
    };
    fetchData();
  }, [roomId]); // Re-run effect when roomId changes


  const handleConfirmBooking = async () => {
    // Check if check-in and check-out dates are selected
    if (!checkInDate || !checkOutDate) {
      setErrorMessage('Please select check-in and check-out dates.');
      setTimeout(() => setErrorMessage(''), 5000); // Clear error message after 5 seconds
      return;
    }

    // Check if number of adults and children are valid
    if (isNaN(numAdults) || numAdults < 1 || isNaN(numChildren) || numChildren < 0) {
      setErrorMessage('Please enter valid numbers for adults and children.');
      setTimeout(() => setErrorMessage(''), 5000); // Clear error message after 5 seconds
      return;
    }

    // Calculate total number of days
    const oneDay = 24 * 60 * 60 * 1000; // hours * minutes * seconds * milliseconds
    const startDate = new Date(checkInDate);
    const endDate = new Date(checkOutDate);
    const totalDays = Math.round(Math.abs((endDate - startDate) / oneDay)) + 1;

    // Calculate total number of guests
    const totalGuests = numAdults + numChildren;

    // Calculate total price
    const roomPricePerNight = roomDetails.roomPrice;
    const totalPrice = roomPricePerNight * totalDays;

    setTotalPrice(totalPrice);
    setTotalGuests(totalGuests);
  };

  const acceptBooking = async () => {
    try {
      // Ensure user is authenticated
      if (!ApiService.isAuthenticated()) {
        setErrorMessage('Please log in to make a booking.');
        setTimeout(() => {
          navigate('/login', { state: { from: { pathname: `/room-details-book/${roomId}` } } });
        }, 2000);
        return;
      }

      // Fetch userId if not already set
      if (!userId) {
        try {
          const userProfile = await ApiService.getUserProfile();
          if (userProfile && userProfile.user && userProfile.user.id) {
            setUserId(userProfile.user.id);
          } else {
            setErrorMessage('Could not retrieve user information. Please try logging in again.');
            setTimeout(() => {
              navigate('/login', { state: { from: { pathname: `/room-details-book/${roomId}` } } });
            }, 2000);
            return;
          }
        } catch (profileError) {
          setErrorMessage('Please log in to make a booking.');
          setTimeout(() => {
            navigate('/login', { state: { from: { pathname: `/room-details-book/${roomId}` } } });
          }, 2000);
          return;
        }
      }

      // Ensure checkInDate and checkOutDate are Date objects
      const startDate = new Date(checkInDate);
      const endDate = new Date(checkOutDate);

      // Log the original dates for debugging
      console.log("Original Check-in Date:", startDate);
      console.log("Original Check-out Date:", endDate);

      // Convert dates to YYYY-MM-DD format, adjusting for time zone differences
      const formattedCheckInDate = new Date(startDate.getTime() - (startDate.getTimezoneOffset() * 60000)).toISOString().split('T')[0];
      const formattedCheckOutDate = new Date(endDate.getTime() - (endDate.getTimezoneOffset() * 60000)).toISOString().split('T')[0];


      // Log the original dates for debugging
      console.log("Formated Check-in Date:", formattedCheckInDate);
      console.log("Formated Check-out Date:", formattedCheckOutDate);

      // Create booking object
      const booking = {
        checkInDate: formattedCheckInDate,
        checkOutDate: formattedCheckOutDate,
        numOfAdults: numAdults,
        numOfChildren: numChildren
      };
      console.log(booking)
      console.log(checkOutDate)

      // Make booking
      const response = await ApiService.bookRoom(roomId, userId, booking);
      if (response.statusCode === 200) {
        const bookingId = response.booking?.id;
        const newConfirmationCode = response.bookingConfirmationCode;
        setConfirmationCode(newConfirmationCode);
        setShowMessage(true);
        setShowDatePicker(false);
        setCurrentBookingId(bookingId);

        if (bookingId && totalPrice > 0) {
          setShowPaymentModal(true);
        } else {
          setTotalPrice(0);
          if (navigationTimeoutRef.current) {
            clearTimeout(navigationTimeoutRef.current);
          }
          navigationTimeoutRef.current = setTimeout(() => {
            setShowMessage(false);
            navigate('/rooms');
          }, 10000);
        }
      }
    } catch (error) {
      setErrorMessage(error.response?.data?.message || error.message);
      setTimeout(() => setErrorMessage(''), 5000); // Clear error message after 5 seconds
    }
  };

  // Handle successful payment
  const handlePaymentSuccess = () => {
    setShowPaymentModal(false);
    setTotalPrice(0);
    if (navigationTimeoutRef.current) {
      clearTimeout(navigationTimeoutRef.current);
    }
    navigationTimeoutRef.current = setTimeout(() => {
      setShowMessage(false);
      navigate('/rooms');
    }, 10000);
  };

  // Handle payment failure
  const handlePaymentFailure = (paymentMessage) => {
    setErrorMessage(paymentMessage || 'Payment processing failed. Please try again.');
    setTimeout(() => setErrorMessage(''), 5000);
  };

  if (isLoading) {
    return <p className='room-detail-loading'>Loading room details...</p>;
  }

  if (error) {
    return <p className='room-detail-loading'>{error}</p>;
  }

  if (!roomDetails) {
    return <p className='room-detail-loading'>Room not found.</p>;
  }

  const { roomType, roomPrice, roomPhotoUrl, roomDescription, bookings } = roomDetails;
  const description = roomDescription || '';

  return (
    <div className="room-details-booking">
      {showMessage && (
        <p className="booking-success-message">
          Booking successful! Confirmation code: {confirmationCode}. An SMS and email of your booking details have been sent to you.
        </p>
      )}
      {errorMessage && (
        <p className="error-message">
          {errorMessage}
        </p>
      )}
      <h2>Room Details</h2>
      <br />
      <img 
        src={roomPhotoUrl || 'https://via.placeholder.com/800x600?text=Room+Image'} 
        alt={roomType} 
        className="room-details-image"
        onError={(e) => {
          e.target.onerror = null;
          e.target.src = 'https://via.placeholder.com/800x600?text=Room+Image';
        }}
      />
      <div className="room-details-info">
        <h3>{roomType}</h3>
        <p>Price: ${roomPrice} / night</p>
        <p>{description}</p>
      </div>
      {bookings && bookings.length > 0 && (
        <div>
          <h3>Existing Booking Details</h3>
          <ul className="booking-list">
            {bookings.map((booking, index) => (
              <li key={booking.id} className="booking-item">
                <span className="booking-number">Booking {index + 1} </span>
                <span className="booking-text">Check-in: {booking.checkInDate} </span>
                <span className="booking-text">Out: {booking.checkOutDate}</span>
              </li>
            ))}
          </ul>
        </div>
      )}
      <div className="booking-info">
        <button className="book-now-button" onClick={() => {
          // Check authentication before showing date picker
          if (!ApiService.isAuthenticated()) {
            navigate('/login', { state: { from: { pathname: `/room-details-book/${roomId}` } } });
          } else {
            setShowDatePicker(true);
          }
        }}>Book Now</button>
        <button className="go-back-button" onClick={() => {
          setShowDatePicker(false);
          navigate('/rooms');
        }}>Go Back</button>
        {showDatePicker && !showMessage && (
          <div className="date-picker-container">
            <DatePicker
              className="detail-search-field"
              selected={checkInDate}
              onChange={(date) => setCheckInDate(date)}
              selectsStart
              startDate={checkInDate}
              endDate={checkOutDate}
              placeholderText="Check-in Date"
              dateFormat="dd/MM/yyyy"
              // dateFormat="yyyy-MM-dd"
            />
            <DatePicker
              className="detail-search-field"
              selected={checkOutDate}
              onChange={(date) => setCheckOutDate(date)}
              selectsEnd
              startDate={checkInDate}
              endDate={checkOutDate}
              minDate={checkInDate}
              placeholderText="Check-out Date"
              // dateFormat="yyyy-MM-dd"
              dateFormat="dd/MM/yyyy"
            />

            <div className='guest-container'>
              <div className="guest-div">
                <label>Adults:</label>
                <input
                  type="number"
                  min="1"
                  value={numAdults}
                  onChange={(e) => setNumAdults(parseInt(e.target.value))}
                />
              </div>
              <div className="guest-div">
                <label>Children:</label>
                <input
                  type="number"
                  min="0"
                  value={numChildren}
                  onChange={(e) => setNumChildren(parseInt(e.target.value))}
                />
              </div>
              <button className="confirm-booking" onClick={handleConfirmBooking}>Confirm Booking</button>
            </div>
          </div>
        )}
        {totalPrice > 0 && !showMessage && (
          <div className="total-price">
            <p>Total Price: ${totalPrice}</p>
            <p>Total Guests: {totalGuests}</p>
            <button onClick={acceptBooking} className="accept-booking">
              Accept & Pay
            </button>
          </div>
        )}
      </div>
      <PaymentModal
        isOpen={showPaymentModal}
        onClose={() => setShowPaymentModal(false)}
        amount={totalPrice}
        bookingId={currentBookingId}
        confirmationCode={confirmationCode}
        roomType={roomDetails?.roomType}
        onPaymentSuccess={handlePaymentSuccess}
        onPaymentFailure={handlePaymentFailure}
      />
    </div>
  );
};

export default RoomDetailsPage;
