package com.springotel.model;

public record Payment(
        String transactionId,
        Double value,
        String paymentType) { }
