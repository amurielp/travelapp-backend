package com.travelapp.payment.domain;

public enum PaymentMethodType {
    CARD,       // tarjeta de crédito/débito
    TRANSFER,   // transferencia bancaria
    CASH,       // efectivo
    CRYPTO,     // criptomonedas
    OTHER       // PayPal, Bizum, millas, etc.
}
