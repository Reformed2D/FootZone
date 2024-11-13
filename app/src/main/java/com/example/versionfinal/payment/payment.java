package com.example.versionfinal.payment;

public class payment {
    private long id;
    private String email;
    private double amount;
    private String paymentMethod;
    private String cardNumber;
    private long timestamp;

    public payment(long id, String email, double amount, String paymentMethod, String cardNumber, long timestamp) {
        this.id = id;
        this.email = email;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.cardNumber = cardNumber;
        this.timestamp = timestamp;
    }

    // Getters
    public long getId() { return id; }
    public String getEmail() { return email; }
    public double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getCardNumber() { return cardNumber; }
    public long getTimestamp() { return timestamp; }
}