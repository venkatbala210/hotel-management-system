import React, { useState } from 'react';
import ApiService from '../../service/ApiService';
import './PaymentModal.css';

const PaymentModal = ({ isOpen, onClose, amount, bookingId, confirmationCode, roomType, onPaymentSuccess, onPaymentFailure }) => {
    const [formData, setFormData] = useState({
        email: '',
        cardNumber: '',
        expiryDate: '',
        cvc: '',
        rememberMe: false
    });
    const [errors, setErrors] = useState({});
    const [processing, setProcessing] = useState(false);

    if (!isOpen) return null;

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
        // Clear error when user starts typing
        if (errors[name]) {
            setErrors(prev => ({
                ...prev,
                [name]: ''
            }));
        }
    };

    const formatCardNumber = (value) => {
        const v = value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
        const matches = v.match(/\d{4,16}/g);
        const match = matches && matches[0] || '';
        const parts = [];
        for (let i = 0, len = match.length; i < len; i += 4) {
            parts.push(match.substring(i, i + 4));
        }
        if (parts.length) {
            return parts.join(' ');
        } else {
            return v;
        }
    };

    const formatExpiryDate = (value) => {
        const v = value.replace(/\D/g, '');
        if (v.length >= 2) {
            return v.substring(0, 2) + '/' + v.substring(2, 4);
        }
        return v;
    };

    const handleCardNumberChange = (e) => {
        const formatted = formatCardNumber(e.target.value);
        setFormData(prev => ({ ...prev, cardNumber: formatted }));
        if (errors.cardNumber) {
            setErrors(prev => ({ ...prev, cardNumber: '' }));
        }
    };

    const handleExpiryChange = (e) => {
        const formatted = formatExpiryDate(e.target.value);
        setFormData(prev => ({ ...prev, expiryDate: formatted }));
        if (errors.expiryDate) {
            setErrors(prev => ({ ...prev, expiryDate: '' }));
        }
    };

    const handleCvcChange = (e) => {
        const value = e.target.value.replace(/\D/g, '').substring(0, 3);
        setFormData(prev => ({ ...prev, cvc: value }));
        if (errors.cvc) {
            setErrors(prev => ({ ...prev, cvc: '' }));
        }
    };

    const validateForm = () => {
        const newErrors = {};
        
        if (!formData.email || !/\S+@\S+\.\S+/.test(formData.email)) {
            newErrors.email = 'Valid email is required';
        }
        
        const cardNumber = formData.cardNumber.replace(/\s/g, '');
        if (!cardNumber || cardNumber.length < 13 || cardNumber.length > 19) {
            newErrors.cardNumber = 'Valid card number is required';
        }
        
        if (!formData.expiryDate || formData.expiryDate.length !== 5) {
            newErrors.expiryDate = 'Valid expiry date is required';
        }
        
        if (!formData.cvc || formData.cvc.length !== 3) {
            newErrors.cvc = 'Valid CVC is required';
        }
        
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!validateForm()) {
            return;
        }

        setProcessing(true);
        
        try {
            const paymentResponse = await ApiService.processPayment(bookingId, amount);
            
            if (paymentResponse.statusCode === 200) {
                onPaymentSuccess(paymentResponse);
            } else {
                onPaymentFailure(paymentResponse.message || 'Payment processing failed. Please try again.');
            }
        } catch (paymentError) {
            onPaymentFailure(paymentError.response?.data?.message || 'Payment processing failed. Please contact support.');
        } finally {
            setProcessing(false);
        }
    };

    return (
        <div className="payment-modal-overlay" onClick={onClose}>
            <div className="payment-modal" onClick={(e) => e.stopPropagation()}>
                <button className="payment-modal-close" onClick={onClose}>×</button>
                
                <div className="payment-modal-header">
                    <h2 className="payment-modal-title">Payment for {roomType || 'Booking'}</h2>
                    <p className="payment-modal-amount">{amount.toFixed(2)} USD</p>
                </div>

                <form className="payment-modal-form" onSubmit={handleSubmit}>
                    <div className="payment-form-group">
                        <label className="payment-form-label">Email</label>
                        <div className="payment-input-wrapper">
                            <span className="payment-input-icon">✉</span>
                            <input
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                className={`payment-form-input ${errors.email ? 'error' : ''}`}
                                placeholder="your.email@example.com"
                            />
                        </div>
                        {errors.email && <span className="payment-error-message">{errors.email}</span>}
                    </div>

                    <div className="payment-form-group">
                        <label className="payment-form-label">Card number</label>
                        <div className="payment-input-wrapper">
                            <span className="payment-input-icon">💳</span>
                            <input
                                type="text"
                                name="cardNumber"
                                value={formData.cardNumber}
                                onChange={handleCardNumberChange}
                                className={`payment-form-input ${errors.cardNumber ? 'error' : ''}`}
                                placeholder="1234 5678 9012 3456"
                                maxLength="19"
                            />
                        </div>
                        {errors.cardNumber && <span className="payment-error-message">{errors.cardNumber}</span>}
                    </div>

                    <div className="payment-form-row">
                        <div className="payment-form-group payment-form-group-half">
                            <label className="payment-form-label">MM/YY</label>
                            <div className="payment-input-wrapper">
                                <span className="payment-input-icon">📅</span>
                                <input
                                    type="text"
                                    name="expiryDate"
                                    value={formData.expiryDate}
                                    onChange={handleExpiryChange}
                                    className={`payment-form-input ${errors.expiryDate ? 'error' : ''}`}
                                    placeholder="MM/YY"
                                    maxLength="5"
                                />
                            </div>
                            {errors.expiryDate && <span className="payment-error-message">{errors.expiryDate}</span>}
                        </div>

                        <div className="payment-form-group payment-form-group-half">
                            <label className="payment-form-label">CVC</label>
                            <div className="payment-input-wrapper">
                                <span className="payment-input-icon">🔒</span>
                                <input
                                    type="text"
                                    name="cvc"
                                    value={formData.cvc}
                                    onChange={handleCvcChange}
                                    className={`payment-form-input ${errors.cvc ? 'error' : ''}`}
                                    placeholder="123"
                                    maxLength="3"
                                />
                            </div>
                            {errors.cvc && <span className="payment-error-message">{errors.cvc}</span>}
                        </div>
                    </div>

                    <div className="payment-form-group payment-form-checkbox">
                        <label>
                            <input
                                type="checkbox"
                                name="rememberMe"
                                checked={formData.rememberMe}
                                onChange={handleChange}
                            />
                            <span>Remember me</span>
                        </label>
                    </div>

                    <button
                        type="submit"
                        className="payment-submit-button"
                        disabled={processing}
                    >
                        {processing ? 'Processing...' : `Pay $${amount.toFixed(2)}`}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default PaymentModal;

