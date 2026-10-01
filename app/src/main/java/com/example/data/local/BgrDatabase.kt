package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.MerchantDao
import com.example.data.local.dao.MiningDao
import com.example.data.local.dao.TokenDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.dao.UserWalletDao
import com.example.data.local.entity.MerchantEntity
import com.example.data.local.entity.MiningEntity
import com.example.data.local.entity.TokenEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserWalletEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TokenEntity::class,
        TransactionEntity::class,
        MerchantEntity::class,
        MiningEntity::class,
        UserWalletEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BgrDatabase : RoomDatabase() {
    abstract fun tokenDao(): TokenDao
    abstract fun transactionDao(): TransactionDao
    abstract fun merchantDao(): MerchantDao
    abstract fun miningDao(): MiningDao
    abstract fun userWalletDao(): UserWalletDao

    companion object {
        @Volatile
        private var INSTANCE: BgrDatabase? = null

        fun getInstance(context: Context): BgrDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BgrDatabase::class.java,
                    "bgr_pay.db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedInitialData(database)
                    }
                }
            }
        }

        suspend fun seedInitialData(database: BgrDatabase) {
            // Seed Tokens
            val initialTokens = listOf(
                TokenEntity(
                    symbol = "BGR",
                    name = "BGR Pay Native",
                    balance = 5250.00,
                    priceUsd = 0.485,
                    change24h = 12.4,
                    network = "BGR Chain"
                ),
                TokenEntity(
                    symbol = "USDT",
                    name = "Tether USD",
                    balance = 1420.00,
                    priceUsd = 1.00,
                    change24h = 0.05,
                    network = "Ethereum"
                ),
                TokenEntity(
                    symbol = "ETH",
                    name = "Ethereum",
                    balance = 1.35,
                    priceUsd = 3480.00,
                    change24h = 3.8,
                    network = "Ethereum"
                ),
                TokenEntity(
                    symbol = "BTC",
                    name = "Bitcoin",
                    balance = 0.075,
                    priceUsd = 68900.00,
                    change24h = -1.2,
                    network = "Bitcoin"
                ),
                TokenEntity(
                    symbol = "SOL",
                    name = "Solana",
                    balance = 14.50,
                    priceUsd = 152.40,
                    change24h = 6.2,
                    network = "Solana"
                )
            )
            database.tokenDao().insertTokens(initialTokens)

            // Seed Merchants
            val initialMerchants = listOf(
                MerchantEntity(
                    id = "MCH-01",
                    name = "CyberCafé Web3 Paris",
                    category = "FOOD_DRINK",
                    cashbackRate = 5.0,
                    walletAddress = "0x89A3B5c4...E2F1",
                    isVerified = true,
                    totalVolumeUsd = 48500.00,
                    city = "Paris 11e"
                ),
                MerchantEntity(
                    id = "MCH-02",
                    name = "Ledger Hardware Boutique",
                    category = "TECH",
                    cashbackRate = 7.5,
                    walletAddress = "0x12F8A43b...C901",
                    isVerified = true,
                    totalVolumeUsd = 182400.00,
                    city = "Paris 2e"
                ),
                MerchantEntity(
                    id = "MCH-03",
                    name = "CryptoBurger & Tacos",
                    category = "FOOD_DRINK",
                    cashbackRate = 4.0,
                    walletAddress = "0x77Cd42B1...AA98",
                    isVerified = true,
                    totalVolumeUsd = 21400.00,
                    city = "Lyon"
                ),
                MerchantEntity(
                    id = "MCH-04",
                    name = "Sneakers Drop Web3",
                    category = "SHOPPING",
                    cashbackRate = 6.0,
                    walletAddress = "0x98BE4192...45D0",
                    isVerified = true,
                    totalVolumeUsd = 69200.00,
                    city = "Marseille"
                ),
                MerchantEntity(
                    id = "MCH-05",
                    name = "DeFi Cloud Infrastructure",
                    category = "SERVICES",
                    cashbackRate = 3.0,
                    walletAddress = "0x44E27189...B3C2",
                    isVerified = false,
                    totalVolumeUsd = 8900.00,
                    city = "En ligne"
                )
            )
            database.merchantDao().insertMerchants(initialMerchants)

            // Seed Transactions
            val now = System.currentTimeMillis()
            val initialTransactions = listOf(
                TransactionEntity(
                    type = "PAYMENT_MERCHANT",
                    title = "CyberCafé Web3 Paris",
                    amount = 32.50,
                    symbol = "USDT",
                    fiatAmount = 32.50,
                    counterparty = "0x89A3B5c4...E2F1",
                    cashbackBgr = 3.35, // 5% in BGR at $0.485 = ~3.35 BGR
                    timestamp = now - (1000 * 60 * 45), // 45 mins ago
                    status = "CONFIRMED",
                    category = "FOOD_DRINK",
                    txHash = "0x8a91c3f7b24e65d098e72c8172901fab249e0123"
                ),
                TransactionEntity(
                    type = "SWAP",
                    title = "Swap USDT vers BGR",
                    amount = 150.00,
                    symbol = "USDT",
                    fiatAmount = 150.00,
                    counterparty = "Uniswap V3 BGR Pool",
                    cashbackBgr = 0.0,
                    timestamp = now - (1000 * 60 * 60 * 3), // 3 hours ago
                    status = "CONFIRMED",
                    category = "CRYPTO",
                    txHash = "0x3e18f2d90a78c5b1049281aef1049581938501da"
                ),
                TransactionEntity(
                    type = "PAYMENT_MERCHANT",
                    title = "Ledger Hardware Boutique",
                    amount = 179.00,
                    symbol = "USDT",
                    fiatAmount = 179.00,
                    counterparty = "0x12F8A43b...C901",
                    cashbackBgr = 27.68, // 7.5% cashback in BGR
                    timestamp = now - (1000 * 60 * 60 * 26), // yesterday
                    status = "CONFIRMED",
                    category = "TECH",
                    txHash = "0x44c9b201f98e721a9807530281baf89201934892"
                ),
                TransactionEntity(
                    type = "MINING_REWARD",
                    title = "Récompense Tap-to-Mine BGR",
                    amount = 25.50,
                    symbol = "BGR",
                    fiatAmount = 12.36,
                    counterparty = "BGR Mining Contract",
                    cashbackBgr = 0.0,
                    timestamp = now - (1000 * 60 * 60 * 48), // 2 days ago
                    status = "CONFIRMED",
                    category = "MINING",
                    txHash = "0x981273abcf809182379109283019384728190384"
                ),
                TransactionEntity(
                    type = "RECEIVE",
                    title = "Reçu de Vitalik.eth",
                    amount = 0.50,
                    symbol = "ETH",
                    fiatAmount = 1740.00,
                    counterparty = "0xd8dA6BF26964aF9D7eEd9e03E53415D37aA96045",
                    cashbackBgr = 0.0,
                    timestamp = now - (1000 * 60 * 60 * 96), // 4 days ago
                    status = "CONFIRMED",
                    category = "CRYPTO",
                    txHash = "0x78901234abcd5678ef0123456789abcdef012345"
                )
            )
            database.transactionDao().insertTransactions(initialTransactions)

            // Seed Mining State
            database.miningDao().insertMiningState(
                MiningEntity(
                    id = 1,
                    currentEnergy = 920,
                    maxEnergy = 1000,
                    energyPerTap = 2,
                    bgrPerTap = 0.25,
                    multiplier = 1.0,
                    minerLevel = 2,
                    totalMinedBgr = 184.50,
                    unclaimedBgr = 14.75,
                    lastUpdateTimestamp = now
                )
            )

            // Seed User Wallet & Default Admin Credential Info
            database.userWalletDao().insertUserWallet(
                UserWalletEntity(
                    id = 1,
                    walletAddress = "0x7F2e89d1C34B8c42aA4E819b5832a8A8D59E7C01",
                    seedPhrase = "alpha shield orbit velvet cyber quantum pulse matrix beacon vault galaxy neon",
                    currentRole = "USER",
                    securityPin = "1234",
                    isBiometricActive = true,
                    isWalletCreated = true,
                    preferredCurrency = "USD"
                )
            )
        }
    }
}
