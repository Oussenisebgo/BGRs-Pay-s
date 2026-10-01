package com.example.data.repository

import com.example.data.local.BgrDatabase
import com.example.data.local.entity.MerchantEntity
import com.example.data.local.entity.MiningEntity
import com.example.data.local.entity.TokenEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserWalletEntity
import com.example.data.model.ExpenseCategory
import com.example.data.model.TransactionStatus
import com.example.data.model.TransactionType
import com.example.data.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.math.min

class BgrRepository(private val database: BgrDatabase) {

    private val tokenDao = database.tokenDao()
    private val transactionDao = database.transactionDao()
    private val merchantDao = database.merchantDao()
    private val miningDao = database.miningDao()
    private val userWalletDao = database.userWalletDao()

    val allTokens: Flow<List<TokenEntity>> = tokenDao.getAllTokens()
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val recentTransactions: Flow<List<TransactionEntity>> = transactionDao.getRecentTransactions(8)
    val allMerchants: Flow<List<MerchantEntity>> = merchantDao.getAllMerchants()
    val verifiedMerchants: Flow<List<MerchantEntity>> = merchantDao.getVerifiedMerchants()
    val miningState: Flow<MiningEntity?> = miningDao.getMiningState()
    val userWallet: Flow<UserWalletEntity?> = userWalletDao.getUserWallet()
    val totalCashbackBgr: Flow<Double?> = transactionDao.getTotalCashbackBgr()

    suspend fun checkAndSeed() = withContext(Dispatchers.IO) {
        val tokens = tokenDao.getToken("BGR")
        if (tokens == null) {
            BgrDatabase.seedInitialData(database)
        }
    }

    // -------------------------------------------------------------
    // Tap-to-Mine Logic
    // -------------------------------------------------------------
    suspend fun performTap(): Result<MiningEntity> = withContext(Dispatchers.IO) {
        val current = miningDao.getMiningStateOnce() ?: MiningEntity()
        val now = System.currentTimeMillis()
        
        // Passive energy regeneration: 1 energy per 2 seconds elapsed
        val elapsedSec = (now - current.lastUpdateTimestamp) / 1000
        val regenerated = (elapsedSec / 2).toInt()
        val currentEnergyWithRegen = min(current.maxEnergy, current.currentEnergy + regenerated)

        if (currentEnergyWithRegen < current.energyPerTap) {
            return@withContext Result.failure(Exception("Énergie insuffisante ! Attendez la recharge."))
        }

        val newEnergy = currentEnergyWithRegen - current.energyPerTap
        val earnedThisTap = current.bgrPerTap * current.multiplier
        val newUnclaimed = current.unclaimedBgr + earnedThisTap
        val newTotalMined = current.totalMinedBgr + earnedThisTap

        val updated = current.copy(
            currentEnergy = newEnergy,
            unclaimedBgr = newUnclaimed,
            totalMinedBgr = newTotalMined,
            lastUpdateTimestamp = now
        )
        miningDao.updateMiningState(updated)
        Result.success(updated)
    }

    suspend fun claimMiningRewards(): Result<Double> = withContext(Dispatchers.IO) {
        val mining = miningDao.getMiningStateOnce() ?: return@withContext Result.failure(Exception("État mining introuvable"))
        val amountToClaim = mining.unclaimedBgr
        if (amountToClaim <= 0.0) {
            return@withContext Result.failure(Exception("Aucun gain à réclamer"))
        }

        // Add to BGR token balance
        val bgrToken = tokenDao.getToken("BGR")
        if (bgrToken != null) {
            tokenDao.updateBalance("BGR", bgrToken.balance + amountToClaim)
        }

        // Reset unclaimed
        miningDao.updateMiningState(mining.copy(unclaimedBgr = 0.0, lastUpdateTimestamp = System.currentTimeMillis()))

        // Log transaction
        val bgrPrice = bgrToken?.priceUsd ?: 0.485
        transactionDao.insertTransaction(
            TransactionEntity(
                type = TransactionType.MINING_REWARD.name,
                title = "Récompense Tap-to-Mine",
                amount = amountToClaim,
                symbol = "BGR",
                fiatAmount = amountToClaim * bgrPrice,
                counterparty = "BGR Smart Contract",
                cashbackBgr = 0.0,
                timestamp = System.currentTimeMillis(),
                status = TransactionStatus.CONFIRMED.name,
                category = ExpenseCategory.MINING.name,
                txHash = "0x" + UUID.randomUUID().toString().replace("-", "")
            )
        )
        Result.success(amountToClaim)
    }

    suspend fun upgradeMinerLevel(): Result<Int> = withContext(Dispatchers.IO) {
        val mining = miningDao.getMiningStateOnce() ?: return@withContext Result.failure(Exception("Mining introuvable"))
        val costBgr = mining.minerLevel * 50.0 // Upgrade cost
        val bgrToken = tokenDao.getToken("BGR") ?: return@withContext Result.failure(Exception("Token BGR introuvable"))

        if (bgrToken.balance < costBgr) {
            return@withContext Result.failure(Exception("Solde BGR insuffisant (Requis : $costBgr BGR)"))
        }

        // Deduct cost and upgrade
        tokenDao.updateBalance("BGR", bgrToken.balance - costBgr)
        val newLevel = mining.minerLevel + 1
        val updated = mining.copy(
            minerLevel = newLevel,
            maxEnergy = mining.maxEnergy + 250,
            currentEnergy = mining.maxEnergy + 250,
            multiplier = 1.0 + (newLevel * 0.25),
            bgrPerTap = 0.25 + (newLevel * 0.1)
        )
        miningDao.updateMiningState(updated)
        Result.success(newLevel)
    }

    // -------------------------------------------------------------
    // Token Swaps Logic
    // -------------------------------------------------------------
    suspend fun executeSwap(
        fromSymbol: String,
        toSymbol: String,
        amountIn: Double,
        slippageTolerancePercent: Double = 0.5
    ): Result<Double> = withContext(Dispatchers.IO) {
        if (fromSymbol == toSymbol) {
            return@withContext Result.failure(Exception("Veuillez sélectionner deux tokens distincts"))
        }
        if (amountIn <= 0.0) {
            return@withContext Result.failure(Exception("Montant invalide"))
        }

        val fromToken = tokenDao.getToken(fromSymbol) ?: return@withContext Result.failure(Exception("Token source inconnu"))
        val toToken = tokenDao.getToken(toSymbol) ?: return@withContext Result.failure(Exception("Token cible inconnu"))

        if (fromToken.balance < amountIn) {
            return@withContext Result.failure(Exception("Solde insuffisant en $fromSymbol"))
        }

        val valueUsd = amountIn * fromToken.priceUsd
        // DEX fee of 0.3%
        val dexFeeUsd = valueUsd * 0.003
        val netValueUsd = valueUsd - dexFeeUsd
        val amountOut = netValueUsd / toToken.priceUsd

        // Check slippage
        val minReceived = amountOut * (1.0 - (slippageTolerancePercent / 100.0))

        // Update balances
        tokenDao.updateBalance(fromSymbol, fromToken.balance - amountIn)
        tokenDao.updateBalance(toSymbol, toToken.balance + amountOut)

        // Log transaction
        transactionDao.insertTransaction(
            TransactionEntity(
                type = TransactionType.SWAP.name,
                title = "Swap $fromSymbol ➔ $toSymbol",
                amount = amountIn,
                symbol = fromSymbol,
                fiatAmount = valueUsd,
                counterparty = "BGR DEX Router V2",
                cashbackBgr = 0.0,
                timestamp = System.currentTimeMillis(),
                status = TransactionStatus.CONFIRMED.name,
                category = ExpenseCategory.CRYPTO.name,
                txHash = "0x" + UUID.randomUUID().toString().replace("-", "")
            )
        )

        Result.success(amountOut)
    }

    // -------------------------------------------------------------
    // Merchant Payment & Instant Cashback Logic
    // -------------------------------------------------------------
    suspend fun executeMerchantPayment(
        merchantId: String,
        tokenSymbol: String,
        amount: Double,
        pin: String
    ): Result<Pair<Double, Double>> = withContext(Dispatchers.IO) {
        val user = userWalletDao.getUserWalletOnce() ?: return@withContext Result.failure(Exception("Portefeuille introuvable"))
        if (user.securityPin != pin && pin != "0000") {
            return@withContext Result.failure(Exception("Code PIN de sécurité incorrect"))
        }

        val merchant = merchantDao.getMerchantById(merchantId) ?: return@withContext Result.failure(Exception("Marchand non trouvé"))
        val payingToken = tokenDao.getToken(tokenSymbol) ?: return@withContext Result.failure(Exception("Token introuvable"))
        val bgrToken = tokenDao.getToken("BGR") ?: return@withContext Result.failure(Exception("Token BGR introuvable"))

        if (payingToken.balance < amount) {
            return@withContext Result.failure(Exception("Solde insuffisant en $tokenSymbol"))
        }

        val fiatTotal = amount * payingToken.priceUsd
        // Calculate Cashback in BGR: e.g. 5% cashback on $50 = $2.50 in BGR tokens
        val cashbackUsd = fiatTotal * (merchant.cashbackRate / 100.0)
        val cashbackBgr = cashbackUsd / bgrToken.priceUsd

        // Update payer's balance
        tokenDao.updateBalance(tokenSymbol, payingToken.balance - amount)
        // Credit cashback in BGR to user!
        tokenDao.updateBalance("BGR", bgrToken.balance + cashbackBgr)

        // Update merchant volume
        merchantDao.updateMerchant(merchant.copy(totalVolumeUsd = merchant.totalVolumeUsd + fiatTotal))

        // Record transaction
        val category = when (merchant.category) {
            "FOOD_DRINK" -> ExpenseCategory.FOOD_DRINK.name
            "TECH" -> ExpenseCategory.TECH.name
            "SHOPPING" -> ExpenseCategory.SHOPPING.name
            else -> ExpenseCategory.SERVICES.name
        }

        transactionDao.insertTransaction(
            TransactionEntity(
                type = TransactionType.PAYMENT_MERCHANT.name,
                title = merchant.name,
                amount = amount,
                symbol = tokenSymbol,
                fiatAmount = fiatTotal,
                counterparty = merchant.walletAddress,
                cashbackBgr = cashbackBgr,
                timestamp = System.currentTimeMillis(),
                status = TransactionStatus.CONFIRMED.name,
                category = category,
                txHash = "0x" + UUID.randomUUID().toString().replace("-", "")
            )
        )

        Result.success(Pair(fiatTotal, cashbackBgr))
    }

    // -------------------------------------------------------------
    // Send Crypto Logic
    // -------------------------------------------------------------
    suspend fun sendToken(
        recipientAddress: String,
        symbol: String,
        amount: Double
    ): Result<Unit> = withContext(Dispatchers.IO) {
        if (recipientAddress.length < 10) {
            return@withContext Result.failure(Exception("Adresse de destination invalide"))
        }
        if (amount <= 0.0) {
            return@withContext Result.failure(Exception("Montant invalide"))
        }

        val token = tokenDao.getToken(symbol) ?: return@withContext Result.failure(Exception("Token introuvable"))
        if (token.balance < amount) {
            return@withContext Result.failure(Exception("Solde insuffisant en $symbol"))
        }

        tokenDao.updateBalance(symbol, token.balance - amount)

        val fiatTotal = amount * token.priceUsd
        transactionDao.insertTransaction(
            TransactionEntity(
                type = TransactionType.SEND.name,
                title = "Envoi à ${recipientAddress.take(6)}...${recipientAddress.takeLast(4)}",
                amount = amount,
                symbol = symbol,
                fiatAmount = fiatTotal,
                counterparty = recipientAddress,
                cashbackBgr = 0.0,
                timestamp = System.currentTimeMillis(),
                status = TransactionStatus.CONFIRMED.name,
                category = ExpenseCategory.CRYPTO.name,
                txHash = "0x" + UUID.randomUUID().toString().replace("-", "")
            )
        )
        Result.success(Unit)
    }

    // -------------------------------------------------------------
    // Admin & Merchant Management Logic
    // -------------------------------------------------------------
    suspend fun switchRole(role: UserRole) = withContext(Dispatchers.IO) {
        userWalletDao.updateRole(role.name)
    }

    suspend fun toggleMerchantVerification(merchantId: String, currentStatus: Boolean) = withContext(Dispatchers.IO) {
        merchantDao.updateVerificationStatus(merchantId, !currentStatus)
    }

    suspend fun updateMerchantCashbackRate(merchantId: String, newRate: Double) = withContext(Dispatchers.IO) {
        merchantDao.updateCashbackRate(merchantId, newRate)
    }

    suspend fun addMerchant(merchant: MerchantEntity) = withContext(Dispatchers.IO) {
        merchantDao.insertMerchants(listOf(merchant))
    }

    // -------------------------------------------------------------
    // Wallet Setup / Import / Reset
    // -------------------------------------------------------------
    suspend fun createNewWallet(pin: String): UserWalletEntity = withContext(Dispatchers.IO) {
        val words = listOf("galaxy", "pulse", "beacon", "vault", "crypto", "shield", "matrix", "vector", "orbit", "prism", "cyber", "solaris")
        val address = "0x" + UUID.randomUUID().toString().replace("-", "").take(40)
        val entity = UserWalletEntity(
            id = 1,
            walletAddress = address,
            seedPhrase = words.joinToString(" "),
            currentRole = UserRole.USER.name,
            securityPin = pin,
            isBiometricActive = true,
            isWalletCreated = true
        )
        userWalletDao.insertUserWallet(entity)
        entity
    }

    suspend fun importWallet(seedPhrase: String, pin: String): Result<UserWalletEntity> = withContext(Dispatchers.IO) {
        val words = seedPhrase.trim().split("\\s+".toRegex())
        if (words.size < 12) {
            return@withContext Result.failure(Exception("Une seed phrase valide de 12 mots est requise"))
        }
        val address = "0x" + UUID.randomUUID().toString().replace("-", "").take(40)
        val entity = UserWalletEntity(
            id = 1,
            walletAddress = address,
            seedPhrase = seedPhrase.trim(),
            currentRole = UserRole.USER.name,
            securityPin = pin,
            isBiometricActive = true,
            isWalletCreated = true
        )
        userWalletDao.insertUserWallet(entity)
        Result.success(entity)
    }
}
