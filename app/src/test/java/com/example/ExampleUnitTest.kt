package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testCashbackCalculation() {
        val fiatPurchase = 100.0 // $100 purchase
        val merchantCashbackRate = 5.0 // 5%
        val bgrPriceUsd = 0.50 // $0.50 per BGR

        val expectedCashbackUsd = fiatPurchase * (merchantCashbackRate / 100.0) // $5.00
        val expectedCashbackBgr = expectedCashbackUsd / bgrPriceUsd // 10.0 BGR

        assertEquals(5.0, expectedCashbackUsd, 0.001)
        assertEquals(10.0, expectedCashbackBgr, 0.001)
    }

    @Test
    fun testSwapFeeAndOutput() {
        val amountInUsdt = 150.0
        val usdtPrice = 1.0
        val bgrPrice = 0.50
        val dexFeeRate = 0.003 // 0.3%

        val grossValueUsd = amountInUsdt * usdtPrice
        val feeUsd = grossValueUsd * dexFeeRate
        val netValueUsd = grossValueUsd - feeUsd
        val expectedBgrOut = netValueUsd / bgrPrice

        assertEquals(150.0, grossValueUsd, 0.001)
        assertEquals(0.45, feeUsd, 0.001)
        assertEquals(149.55, netValueUsd, 0.001)
        assertEquals(299.10, expectedBgrOut, 0.001)
    }

    @Test
    fun testMiningMultiplierLevel() {
        val baseBgrPerTap = 0.25
        val minerLevel = 3
        val multiplier = 1.0 + (minerLevel * 0.25) // 1.75x
        val earnedPerTap = baseBgrPerTap * multiplier

        assertEquals(1.75, multiplier, 0.001)
        assertEquals(0.4375, earnedPerTap, 0.0001)
    }
}
