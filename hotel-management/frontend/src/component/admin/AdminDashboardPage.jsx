import React, { useState, useEffect } from 'react';
import ApiService from '../../service/ApiService';

const AdminDashboardPage = () => {
    const [dashboardData, setDashboardData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchDashboardData = async () => {
        try {
            setLoading(true);
            setError(null);
            
            // Check authentication - let backend handle role verification
            if (!ApiService.isAuthenticated()) {
                setError('Please log in to access the dashboard');
                setLoading(false);
                return;
            }
            
            const response = await ApiService.getDashboardStatistics();
            if (response.statusCode === 200) {
                setDashboardData(response.dashboard);
            } else {
                setError(response.message || 'Failed to load dashboard data');
            }
        } catch (err) {
            console.error('Dashboard error:', err);
            let errorMessage = 'Error loading dashboard data';
            
            // Handle different error types
            if (err.message && err.message.includes('No authentication token')) {
                errorMessage = 'Please log in to access the dashboard';
            } else if (err.response?.status === 401) {
                errorMessage = 'Your session has expired. Please log in again.';
            } else if (err.response?.status === 403) {
                const currentRole = localStorage.getItem('role');
                errorMessage = `Access denied. Admin privileges required. Current role: ${currentRole || 'Not logged in'}. 
                
Please try:
1. Log out completely
2. Log back in with admin@gmail.com / admin@123
3. Make sure the backend server is running`;
            } else if (err.response?.status === 500) {
                errorMessage = 'Server error. Please try again later.';
            } else if (err.response?.data?.message) {
                errorMessage = err.response.data.message;
            } else if (err.message) {
                errorMessage = err.message;
            }
            
            setError(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchDashboardData();
    }, []);

    if (loading) {
        return <div className="dashboard-loading">Loading dashboard...</div>;
    }

    const testAuthentication = async () => {
        try {
            const response = await fetch('http://localhost:5050/admin/dashboard/test-auth', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`,
                    'Content-Type': 'application/json'
                }
            });
            const data = await response.json();
            alert(`Test Auth Result:\nStatus: ${data.statusCode}\nMessage: ${data.message}`);
        } catch (err) {
            alert(`Test Auth Error: ${err.message}`);
        }
    };

    if (error) {
        return (
            <div className="admin-dashboard">
                <div className="dashboard-error" style={{
                    padding: '40px',
                    textAlign: 'center',
                    backgroundColor: '#fff',
                    border: '1px solid #ddd',
                    borderRadius: '8px',
                    maxWidth: '600px',
                    margin: '50px auto'
                }}>
                    <h2 style={{ color: '#dc3545', marginBottom: '20px' }}>Error Loading Dashboard</h2>
                    <p style={{ color: '#666', marginBottom: '20px', whiteSpace: 'pre-line' }}>{error}</p>
                    <div style={{ marginBottom: '15px' }}>
                        <button 
                            onClick={testAuthentication}
                            style={{
                                padding: '10px 20px',
                                backgroundColor: '#6B9BD1',
                                color: 'white',
                                border: 'none',
                                borderRadius: '5px',
                                cursor: 'pointer',
                                fontSize: '14px',
                                fontWeight: 'bold',
                                marginRight: '10px'
                            }}
                        >
                            Test Authentication
                        </button>
                        <button 
                            onClick={fetchDashboardData}
                            style={{
                                padding: '10px 20px',
                                backgroundColor: '#007F86',
                                color: 'white',
                                border: 'none',
                                borderRadius: '5px',
                                cursor: 'pointer',
                                fontSize: '14px',
                                fontWeight: 'bold',
                                marginRight: '10px'
                            }}
                        >
                            Retry
                        </button>
                        {error.includes('Authentication') && (
                            <button 
                                onClick={() => {
                                    ApiService.logout();
                                    window.location.href = '/login';
                                }}
                                style={{
                                    padding: '10px 20px',
                                    backgroundColor: '#6B9BD1',
                                    color: 'white',
                                    border: 'none',
                                    borderRadius: '5px',
                                    cursor: 'pointer',
                                    fontSize: '14px',
                                    fontWeight: 'bold'
                                }}
                            >
                                Go to Login
                            </button>
                        )}
                    </div>
                    <div style={{ marginTop: '20px', padding: '15px', backgroundColor: '#f9f9f9', borderRadius: '5px', fontSize: '12px', textAlign: 'left' }}>
                        <strong>Debug Info:</strong><br/>
                        Token: {localStorage.getItem('token') ? 'Present' : 'Missing'}<br/>
                        Role: {localStorage.getItem('role') || 'Not set'}<br/>
                        <strong style={{ color: '#dc3545' }}>⚠️ Make sure the backend server is restarted with the latest code!</strong>
                    </div>
                </div>
            </div>
        );
    }

    if (!dashboardData) {
        return <div className="dashboard-error">No data available</div>;
    }

    return (
        <div className="admin-dashboard">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                <h1 className="dashboard-title" style={{ margin: 0 }}>Admin Dashboard</h1>
                <button 
                    onClick={fetchDashboardData}
                    disabled={loading}
                    style={{
                        padding: '10px 20px',
                        backgroundColor: '#007F86',
                        color: 'white',
                        border: 'none',
                        borderRadius: '5px',
                        cursor: loading ? 'not-allowed' : 'pointer',
                        fontSize: '14px',
                        fontWeight: 'bold'
                    }}
                >
                    {loading ? 'Refreshing...' : 'Refresh Data'}
                </button>
            </div>
            
            {/* Quick Stats Summary */}
            <div style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
                gap: '15px',
                marginBottom: '30px',
                padding: '20px',
                backgroundColor: '#f9f9f9',
                borderRadius: '8px'
            }}>
                <div style={{ textAlign: 'center' }}>
                    <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#007F86' }}>
                        {dashboardData.totalUsers || 0}
                    </div>
                    <div style={{ fontSize: '14px', color: '#666' }}>Total Users</div>
                </div>
                <div style={{ textAlign: 'center' }}>
                    <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#007F86' }}>
                        {dashboardData.totalBookings || 0}
                    </div>
                    <div style={{ fontSize: '14px', color: '#666' }}>Total Bookings</div>
                </div>
                <div style={{ textAlign: 'center' }}>
                    <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#007F86' }}>
                        {dashboardData.totalRooms || 0}
                    </div>
                    <div style={{ fontSize: '14px', color: '#666' }}>Total Rooms</div>
                </div>
                <div style={{ textAlign: 'center' }}>
                    <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#6B9BD1' }}>
                        ${dashboardData.totalRevenue?.toFixed(2) || '0.00'}
                    </div>
                    <div style={{ fontSize: '14px', color: '#666' }}>Total Revenue</div>
                </div>
            </div>

            {/* Statistics Cards */}
            <div className="dashboard-stats">
                {/* User Statistics */}
                <div className="stat-card">
                    <h3>Users</h3>
                    <div className="stat-value">{dashboardData.totalUsers || 0}</div>
                    <div className="stat-details">
                        <span>Admins: {dashboardData.totalAdmins || 0}</span>
                        <span>Regular Users: {dashboardData.totalRegularUsers || 0}</span>
                    </div>
                </div>

                {/* Booking Statistics */}
                <div className="stat-card">
                    <h3>Bookings</h3>
                    <div className="stat-value">{dashboardData.totalBookings || 0}</div>
                    <div className="stat-details">
                        <span>Confirmed: {dashboardData.confirmedBookings || 0}</span>
                        <span>Cancelled: {dashboardData.cancelledBookings || 0}</span>
                    </div>
                </div>

                {/* Room Statistics */}
                <div className="stat-card">
                    <h3>Rooms</h3>
                    <div className="stat-value">{dashboardData.totalRooms || 0}</div>
                    <div className="stat-details">
                        <span>Available: {dashboardData.availableRooms || 0}</span>
                        <span>Booked: {dashboardData.bookedRooms || 0}</span>
                    </div>
                </div>

                {/* Payment Statistics */}
                <div className="stat-card">
                    <h3>Payments</h3>
                    <div className="stat-value">${dashboardData.totalRevenue?.toFixed(2) || '0.00'}</div>
                    <div className="stat-details">
                        <span>Success: ${dashboardData.successfulPayments?.toFixed(2) || '0.00'}</span>
                        <span>Failed: ${dashboardData.failedPayments?.toFixed(2) || '0.00'}</span>
                    </div>
                    <div className="stat-details">
                        <span>Total Payments: {dashboardData.totalPayments || 0}</span>
                        <span>Success: {dashboardData.successfulPaymentCount || 0}</span>
                        <span>Failed: {dashboardData.failedPaymentCount || 0}</span>
                    </div>
                </div>
                
                {/* Additional Statistics Card */}
                <div className="stat-card">
                    <h3>Occupancy Rate</h3>
                    <div className="stat-value">
                        {dashboardData.totalRooms > 0 
                            ? ((dashboardData.bookedRooms / dashboardData.totalRooms) * 100).toFixed(1) + '%'
                            : '0%'}
                    </div>
                    <div className="stat-details">
                        <span>Booked: {dashboardData.bookedRooms || 0}</span>
                        <span>Total: {dashboardData.totalRooms || 0}</span>
                    </div>
                </div>
            </div>

            {/* Detailed Lists */}
            <div className="dashboard-sections">
                {/* All Users Section */}
                <div className="dashboard-section">
                    <h2>All Registered Users ({dashboardData.allUsers?.length || 0})</h2>
                    <div className="table-container">
                        <table className="dashboard-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Phone</th>
                                    <th>Role</th>
                                </tr>
                            </thead>
                            <tbody>
                                {dashboardData.allUsers && dashboardData.allUsers.length > 0 ? (
                                    dashboardData.allUsers.map((user) => (
                                        <tr key={user.id}>
                                            <td>{user.id}</td>
                                            <td>{user.name}</td>
                                            <td>{user.email}</td>
                                            <td>{user.phoneNumber}</td>
                                            <td>
                                                <span className={`role-badge ${user.role === 'ADMIN' ? 'admin' : 'user'}`}>
                                                    {user.role}
                                                </span>
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan="5" style={{ textAlign: 'center' }}>No users found</td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>

                {/* All Bookings Section */}
                <div className="dashboard-section">
                    <h2>All Bookings ({dashboardData.allBookings?.length || 0})</h2>
                    <div className="table-container">
                        <table className="dashboard-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Confirmation Code</th>
                                    <th>User</th>
                                    <th>Room Type</th>
                                    <th>Check-in</th>
                                    <th>Check-out</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                {dashboardData.allBookings && dashboardData.allBookings.length > 0 ? (
                                    dashboardData.allBookings.map((booking) => (
                                        <tr key={booking.id}>
                                            <td>{booking.id}</td>
                                            <td>{booking.bookingConfirmationCode}</td>
                                            <td>{booking.user?.name || 'N/A'}</td>
                                            <td>{booking.room?.roomType || 'N/A'}</td>
                                            <td>{booking.checkInDate}</td>
                                            <td>{booking.checkOutDate}</td>
                                            <td>
                                                <span className={`status-badge ${booking.status === 'CONFIRMED' ? 'confirmed' : 'cancelled'}`}>
                                                    {booking.status || 'CONFIRMED'}
                                                </span>
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan="7" style={{ textAlign: 'center' }}>No bookings found</td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>

                {/* Payment Summary Section */}
                <div className="dashboard-section">
                    <h2>Payment Summary ({dashboardData.allPayments?.length || 0})</h2>
                    <div className="table-container">
                        <table className="dashboard-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Booking ID</th>
                                    <th>Amount</th>
                                    <th>Status</th>
                                    <th>Payment Date</th>
                                </tr>
                            </thead>
                            <tbody>
                                {dashboardData.allPayments && dashboardData.allPayments.length > 0 ? (
                                    dashboardData.allPayments.map((payment) => (
                                        <tr key={payment.id}>
                                            <td>{payment.id}</td>
                                            <td>{payment.bookingId}</td>
                                            <td>${payment.amount}</td>
                                            <td>
                                                <span className={`payment-status ${payment.status === 'SUCCESS' ? 'success' : 'failed'}`}>
                                                    {payment.status}
                                                </span>
                                            </td>
                                            <td>{payment.paymentDate ? new Date(payment.paymentDate).toLocaleString() : 'N/A'}</td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan="5" style={{ textAlign: 'center' }}>No payments found</td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>

                {/* All Rooms Section */}
                <div className="dashboard-section">
                    <h2>All Rooms ({dashboardData.allRooms?.length || 0})</h2>
                    <div className="table-container">
                        <table className="dashboard-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Room Type</th>
                                    <th>Price/Night</th>
                                    <th>Description</th>
                                    <th>Image</th>
                                </tr>
                            </thead>
                            <tbody>
                                {dashboardData.allRooms && dashboardData.allRooms.length > 0 ? (
                                    dashboardData.allRooms.map((room) => (
                                        <tr key={room.id}>
                                            <td>{room.id}</td>
                                            <td>{room.roomType}</td>
                                            <td>${room.roomPrice}</td>
                                            <td style={{ maxWidth: '300px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                                                {room.roomDescription || 'N/A'}
                                            </td>
                                            <td>
                                                {room.roomPhotoUrl ? (
                                                    <img 
                                                        src={room.roomPhotoUrl} 
                                                        alt={room.roomType}
                                                        style={{ width: '80px', height: '60px', objectFit: 'cover', borderRadius: '4px' }}
                                                        onError={(e) => {
                                                            e.target.onerror = null;
                                                            e.target.src = 'https://via.placeholder.com/80x60?text=No+Image';
                                                        }}
                                                    />
                                                ) : (
                                                    <span style={{ color: '#999' }}>No Image</span>
                                                )}
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan="5" style={{ textAlign: 'center' }}>No rooms found</td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AdminDashboardPage;

