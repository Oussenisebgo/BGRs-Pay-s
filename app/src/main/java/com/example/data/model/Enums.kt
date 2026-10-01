package com.example.data.model

enum class TransactionType(val labelFr: String) {
    SEND("Envoi"),
    RECEIVE("Réception"),
    SWAP("Échange (Swap)"),
    PAYMENT_MERCHANT("Paiement Marchand"),
    MINING_REWARD("Minage Tap-to-Mine")
}

enum class TransactionStatus(val labelFr: String) {
    CONFIRMED("Confirmé"),
    PENDING("En attente"),
    FAILED("Échoué")
}

enum class ExpenseCategory(val labelFr: String, val iconName: String) {
    FOOD_DRINK("Alimentation & Café", "restaurant"),
    SHOPPING("Shopping & Mode", "shopping_bag"),
    TECH("High-Tech & Matériel", "devices"),
    CRYPTO("Swaps & DeFi", "currency_exchange"),
    MINING("Gains Tap-to-Mine", "bolt"),
    SERVICES("Services & Abonnements", "receipt")
}

enum class UserRole(val labelFr: String) {
    USER("Client Particulier"),
    MERCHANT("Commerçant Partenaire"),
    ADMIN("Administrateur BGR")
}
