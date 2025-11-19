import React, { useState } from 'react';
import ApiService from '../../service/ApiService'; // Assuming your service is in a file called ApiService.js
import PaymentModal from '../common/PaymentModal';

const FindBookingPage = () => {
    const [confirmationCode, setConfirmationCode] = useState(''); // State variable for confirmation code
    const [bookingDetails, setBookingDetails] = useState(null); // State variable for booking details
    const [error, setError] = useState(null); // Track any errors
    const [showPaymentModal, setShowPaymentModal] = useState(false); // State to show payment modal
    const [successMessage, setSuccessMessage] = useState(''); // Success message after payment

    const handleSearch = async () => {
        if (!confirmationCode.trim()) {
            setError("Please Enter a booking confirmation code");
            setTimeout(() => setError(''), 5000);
            return;
        }
        try {
            // Call API to get booking details
            const response = await ApiService.getBookingByConfirmationCode(confirmationCode);
            setBookingDetails(response.booking);
            setError(null); // Clear error if successful
            setSuccessMessage(''); // Clear success message
        } catch (error) {
            setError(error.response?.data?.message || error.message);
            setTimeout(() => setError(''), 5000);
        }
    };

    // Calculate total price for booking
    const calculateTotalPrice = () => {
        if (!bookingDetails || !bookingDetails.room || !bookingDetails.room.roomPrice) return 0;
        
        try {
            const checkIn = new Date(bookingDetails.checkInDate);
            const checkOut = new Date(bookingDetails.checkOutDate);
            const oneDay = 24 * 60 * 60 * 1000;
            const totalDays = Math.round(Math.abs((checkOut - checkIn) / oneDay)) + 1;
            
            const price = bookingDetails.room.roomPrice * totalDays;
            return isNaN(price) ? 0 : price;
        } catch (error) {
            console.error('Error calculating total price:', error);
            return 0;
        }
    };

    // Handle successful payment
    const handlePaymentSuccess = (paymentResponse) => {
        setShowPaymentModal(false);
        setSuccessMessage('Payment processed successfully!');
        // Refresh booking details to show updated payment info
        if (confirmationCode) {
            handleSearch();
        }
        setTimeout(() => setSuccessMessage(''), 5000);
    };

    // Handle payment failure
    const handlePaymentFailure = (errorMessage) => {
        setError(errorMessage || 'Payment processing failed. Please try again.');
        setTimeout(() => setError(''), 5000);
    };

    return (
        <div className="find-booking-page">
            <h2>Find Booking</h2>
            <div className="search-container">
                <input
                    required
                    type="text"
                    placeholder="Enter your booking confirmation code"
                    value={confirmationCode}
                    onChange={(e) => setConfirmationCode(e.target.value)}
                />
                <button onClick={handleSearch}>Find</button>
            </div>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {successMessage && <p style={{ color: '#6B9BD1', fontWeight: 'bold' }}>{successMessage}</p>}
            {bookingDetails && (
                <div className="booking-details">
                    <h3>Booking Details</h3>
                    <p>Confirmation Code: {bookingDetails.bookingConfirmationCode}</p>
                    <p><strong>Status:</strong> 
                        <span className={bookingDetails.status === 'CONFIRMED' ? 'booking-confirmed' : 'booking-cancelled'}>
                            {bookingDetails.status || 'CONFIRMED'}
                        </span>
                    </p>
                    <p>Check-in Date: {bookingDetails.checkInDate}</p>
                    <p>Check-out Date: {bookingDetails.checkOutDate}</p>
                    <p>Num Of Adults: {bookingDetails.numOfAdults}</p>
                    <p>Num Of Children: {bookingDetails.numOfChildren}</p>

                    <br />
                    <hr />
                    <br />
                    <h3>Booker Detials</h3>
                    <div>
                        <p> Name: {bookingDetails.user.name}</p>
                        <p> Email: {bookingDetails.user.email}</p>
                        <p> Phone Number: {bookingDetails.user.phoneNumber}</p>
                    </div>

                    <br />
                    <hr />
                    <br />
                    <h3>Room Details</h3>
                    <div>
                        <p> Room Type: {bookingDetails.room.roomType}</p>
                        <img 
                          src={bookingDetails.room.roomPhotoUrl || 'https://via.placeholder.com/400x300?text=Room+Image'} 
                          alt={bookingDetails.room.roomType} 
                          onError={(e) => {
                            e.target.onerror = null;
                            e.target.src = 'https://via.placeholder.com/400x300?text=Room+Image';
                          }}
                        />
                    </div>

                    <br />
                    <hr />
                    <br />
                    <div className="payment-details-container" style={{
                        margin: '20px auto',
                        maxWidth: '600px',
                        padding: '20px',
                        backgroundColor: '#f9f9f9',
                        borderRadius: '8px',
                        border: '1px solid #ddd',
                        textAlign: 'center'
                    }}>
                        <h3 style={{ marginBottom: '15px', color: '#333' }}>Payment Details</h3>
                        {bookingDetails.payment ? (
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                                <p style={{ margin: '5px 0' }}>
                                    <strong>Booking ID:</strong> {bookingDetails.id}
                                </p>
                                <p style={{ margin: '5px 0' }}>
                                    <strong>Payment Amount:</strong> ${bookingDetails.payment.amount}
                                </p>
                                <p style={{ margin: '5px 0' }}>
                                    <strong>Payment Status:</strong> 
                                    <span className={bookingDetails.payment.status === 'SUCCESS' ? 'payment-success' : 'payment-failed'} style={{
                                        marginLeft: '10px',
                                        padding: '4px 12px',
                                        borderRadius: '4px',
                                        fontWeight: 'bold'
                                    }}>
                                        {bookingDetails.payment.status}
                                    </span>
                                </p>
                                <p style={{ margin: '5px 0' }}>
                                    <strong>Payment Date:</strong> {new Date(bookingDetails.payment.paymentDate).toLocaleString()}
                                </p>
                            </div>
                        ) : (
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                                <p style={{ margin: '5px 0', color: '#666' }}>
                                    <strong>Booking ID:</strong> {bookingDetails.id}
                                </p>
                                <p style={{ margin: '5px 0', color: '#999', fontStyle: 'italic' }}>
                                    Payment not yet processed
                                </p>
                                {bookingDetails.status !== 'CANCELLED' && (
                                    <button
                                        onClick={() => {
                                            const totalPrice = calculateTotalPrice();
                                            if (totalPrice > 0) {
                                                setShowPaymentModal(true);
                                            } else {
                                                setError('Unable to calculate payment amount. Please contact support.');
                                                setTimeout(() => setError(''), 5000);
                                            }
                                        }}
                                        style={{
                                            marginTop: '15px',
                                            padding: '12px 30px',
                                            fontSize: '16px',
                                            backgroundColor: '#007F86',
                                            color: 'white',
                                            border: 'none',
                                            borderRadius: '5px',
                                            cursor: 'pointer',
                                            fontWeight: 'bold',
                                            alignSelf: 'center'
                                        }}
                                    >
                                        Pay Now (${calculateTotalPrice().toFixed(2)})
                                    </button>
                                )}
                            </div>
                        )}
                    </div>
                </div>
            )}
            
            {/* Payment Modal */}
            {bookingDetails && !bookingDetails.payment && (
                <PaymentModal
                    isOpen={showPaymentModal}
                    onClose={() => setShowPaymentModal(false)}
                    amount={calculateTotalPrice()}
                    bookingId={bookingDetails.id}
                    confirmationCode={bookingDetails.bookingConfirmationCode}
                    roomType={bookingDetails.room?.roomType}
                    onPaymentSuccess={handlePaymentSuccess}
                    onPaymentFailure={handlePaymentFailure}
                />
            )}
        </div>
    );
};

export default FindBookingPage;
