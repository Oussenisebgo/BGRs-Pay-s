package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.BgrDatabase
import com.example.data.local.entity.MerchantEntity
import com.example.data.local.entity.TokenEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.TransactionType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: BgrDatabase
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, BgrDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testAppNameResource() {
        val appName = context.getString(R.string.app_name)
        assertEquals("BGR Pay", appName)
    }

    @Test
    fun testTokenDaoOperations() = runBlocking {
        val token = TokenEntity(
            symbol = "BGR",
            name = "BGR Pay Native",
            balance = 1000.0,
            priceUsd = 0.485,
            change24h = 10.0,
            network = "BGR Chain"
        )
        database.tokenDao().insertTokens(listOf(token))

        val retrieved = database.tokenDao().getToken("BGR")
        assertNotNull(retrieved)
        assertEquals(1000.0, retrieved?.balance ?: 0.0, 0.001)

        // Update balance
        database.tokenDao().updateBalance("BGR", 1500.0)
        val updated = database.tokenDao().getToken("BGR")
        assertEquals(1500.0, updated?.balance ?: 0.0, 0.001)
    }

    @Test
    fun testMerchantVerification() = runBlocking {
        val merchant = MerchantEntity(
            id = "MCH-TEST",
            name = "Test Café Web3",
            category = "FOOD_DRINK",
            cashbackRate = 5.0,
            walletAddress = "0x123...456",
            isVerified = false,
            totalVolumeUsd = 500.0,
            city = "Paris"
        )
        database.merchantDao().insertMerchants(listOf(merchant))

        val initial = database.merchantDao().getMerchantById("MCH-TEST")
        assertEquals(false, initial?.isVerified)

        database.merchantDao().updateVerificationStatus("MCH-TEST", true)
        val updated = database.merchantDao().getMerchantById("MCH-TEST")
        assertEquals(true, updated?.isVerified)
    }

    @Test
    fun testTransactionInsertion() = runBlocking {
        val tx = TransactionEntity(
            type = TransactionType.PAYMENT_MERCHANT.name,
            title = "Achat Boutique",
            amount = 50.0,
            symbol = "USDT",
            fiatAmount = 50.0,
            counterparty = "0xMerchantAddress",
            cashbackBgr = 5.15,
            status = "CONFIRMED",
            category = "SHOPPING",
            txHash = "0xabcdef123456"
        )
        val id = database.transactionDao().insertTransaction(tx)
        assertTrue(id > 0)
    }
}
