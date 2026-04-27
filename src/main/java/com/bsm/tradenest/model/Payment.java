package com.bsm.tradenest.model;

public class Payment {
    private String id;
    private String bookingId;
    private String orderId; // simulated order id
    private String paymentId; // simulated payment id
    private double amount; // total paid in INR
    private double workerAmount; // 98% goes to worker
    private double adminAmount; // 2% commission
    private String status; // PENDING / SUCCESS
    private String createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getWorkerAmount() {
        return workerAmount;
    }

    public void setWorkerAmount(double workerAmount) {
        this.workerAmount = workerAmount;
    }

    public double getAdminAmount() {
        return adminAmount;
    }

    public void setAdminAmount(double adminAmount) {
        this.adminAmount = adminAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
