package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BgrDatabase
import com.example.data.local.entity.MerchantEntity
import com.example.data.local.entity.MiningEntity
import com.example.data.local.entity.TokenEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserWalletEntity
import com.example.data.model.UserRole
import com.example.data.repository.BgrRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BgrUiState(
    val tokens: List<TokenEntity> = emptyList(),
    val transactions: List<TransactionEntity> = emptyList(),
    val filteredTransactions: List<TransactionEntity> = emptyList(),
    val merchants: List<MerchantEntity> = emptyList(),
    val miningState: MiningEntity? = null,
    val userWallet: UserWalletEntity? = null,
    val totalCashbackBgr: Double = 0.0,
    val totalPortfolioUsd: Double = 0.0,
    val selectedFilter: String = "ALL",
    val searchQuery: String = "",
    val activeRole: UserRole = UserRole.USER,
    val isWalletSetupComplete: Boolean = true,
    val feedbackMessage: String? = null,
    val isSuccess: Boolean = true,
    val isLoading: Boolean = false
)

class BgrViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BgrRepository

    init {
        val database = BgrDatabase.getInstance(application)
        repository = BgrRepository(database)
        viewModelScope.launch {
            repository.checkAndSeed()
        }
    }

    private val _selectedFilter = MutableStateFlow("ALL")
    private val _searchQuery = MutableStateFlow("")
    private val _feedbackMessage = MutableStateFlow<String?>(null)
    private val _isSuccess = MutableStateFlow(true)
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<BgrUiState> = combine(
        repository.allTokens,
        repository.allTransactions,
        repository.allMerchants,
        repository.miningState,
        repository.userWallet,
        repository.totalCashbackBgr,
        _selectedFilter,
        _searchQuery,
        _feedbackMessage,
        _isSuccess,
        _isLoading
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val tokens = args[0] as? List<TokenEntity> ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        val transactions = args[1] as? List<TransactionEntity> ?: emptyList()
        @Suppress("UNCHECKED_CAST")
        val merchants = args[2] as? List<MerchantEntity> ?: emptyList()
        val mining = args[3] as? MiningEntity
        val wallet = args[4] as? UserWalletEntity
        val cashbackBgr = args[5] as? Double ?: 0.0
        val filter = args[6] as? String ?: "ALL"
        val query = args[7] as? String ?: ""
        val feedback = args[8] as? String
        val success = args[9] as? Boolean ?: true
        val loading = args[10] as? Boolean ?: false

        val totalUsd = tokens.sumOf { it.balance * it.priceUsd }

        val filtered = transactions.filter { tx ->
            val matchesFilter = when (filter) {
                "ALL" -> true
                else -> tx.type == filter
            }
            val matchesQuery = query.isBlank() ||
                    tx.title.contains(query, ignoreCase = true) ||
                    tx.counterparty.contains(query, ignoreCase = true) ||
                    tx.symbol.contains(query, ignoreCase = true)
            matchesFilter && matchesQuery
        }

        val role = when (wallet?.currentRole) {
            UserRole.MERCHANT.name -> UserRole.MERCHANT
            UserRole.ADMIN.name -> UserRole.ADMIN
            else -> UserRole.USER
        }

        BgrUiState(
            tokens = tokens,
            transactions = transactions,
            filteredTransactions = filtered,
            merchants = merchants,
            miningState = mining,
            userWallet = wallet,
            totalCashbackBgr = cashbackBgr,
            totalPortfolioUsd = totalUsd,
            selectedFilter = filter,
            searchQuery = query,
            activeRole = role,
            isWalletSetupComplete = wallet?.isWalletCreated ?: true,
            feedbackMessage = feedback,
            isSuccess = success,
            isLoading = loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BgrUiState()
    )

    fun onFilterTransactions(filter: String) {
        _selectedFilter.value = filter
    }

    fun onSearchTransactions(query: String) {
        _searchQuery.value = query
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    fun tapToMine() {
        viewModelScope.launch {
            val result = repository.performTap()
            result.onFailure {
                _feedbackMessage.value = it.message ?: "Énergie insuffisante"
                _isSuccess.value = false
            }
        }
    }

    fun claimMiningRewards() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.claimMiningRewards()
            _isLoading.value = false
            result.onSuccess { claimed ->
                _feedbackMessage.value = "+${"%.2f".format(claimed)} BGR réclamés et ajoutés au portefeuille !"
                _isSuccess.value = true
            }.onFailure {
                _feedbackMessage.value = it.message ?: "Erreur de réclamation"
                _isSuccess.value = false
            }
        }
    }

    fun upgradeMiner() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.upgradeMinerLevel()
            _isLoading.value = false
            result.onSuccess { newLvl ->
                _feedbackMessage.value = "Niveau Mineur $newLvl débloqué ! Multiplicateur augmenté."
                _isSuccess.value = true
            }.onFailure {
                _feedbackMessage.value = it.message ?: "Échec de l'amélioration"
                _isSuccess.value = false
            }
        }
    }

    fun executeSwap(
        fromSymbol: String,
        toSymbol: String,
        amountIn: Double,
        slippage: Double = 0.5
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.executeSwap(fromSymbol, toSymbol, amountIn, slippage)
            _isLoading.value = false
            result.onSuccess { received ->
                _feedbackMessage.value = "Swap réussi : +${"%.4f".format(received)} $toSymbol reçus !"
                _isSuccess.value = true
            }.onFailure {
                _feedbackMessage.value = it.message ?: "Échec du swap"
                _isSuccess.value = false
            }
        }
    }

    fun payMerchant(
        merchantId: String,
        tokenSymbol: String,
        amount: Double,
        pin: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.executeMerchantPayment(merchantId, tokenSymbol, amount, pin)
            _isLoading.value = false
            result.onSuccess { (fiat, cashbackBgr) ->
                _feedbackMessage.value = "Paiement de $${"%.2f".format(fiat)} validé ! Cashback instantané : +${"%.2f".format(cashbackBgr)} BGR !"
                _isSuccess.value = true
            }.onFailure {
                _feedbackMessage.value = it.message ?: "Paiement échoué"
                _isSuccess.value = false
            }
        }
    }

    fun sendCrypto(
        address: String,
        symbol: String,
        amount: Double
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.sendToken(address, symbol, amount)
            _isLoading.value = false
            result.onSuccess {
                _feedbackMessage.value = "Transfert de $amount $symbol envoyé avec succès sur le réseau !"
                _isSuccess.value = true
            }.onFailure {
                _feedbackMessage.value = it.message ?: "Transfert échoué"
                _isSuccess.value = false
            }
        }
    }

    fun switchRole(role: UserRole) {
        viewModelScope.launch {
            repository.switchRole(role)
            _feedbackMessage.value = "Mode basculé vers : ${role.labelFr}"
            _isSuccess.value = true
        }
    }

    fun toggleMerchantVerification(id: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleMerchantVerification(id, currentStatus)
            _feedbackMessage.value = "Statut marchand mis à jour"
            _isSuccess.value = true
        }
    }

    fun updateMerchantCashbackRate(id: String, rate: Double) {
        viewModelScope.launch {
            repository.updateMerchantCashbackRate(id, rate)
            _feedbackMessage.value = "Taux de cashback marchand modifié à $rate%"
            _isSuccess.value = true
        }
    }

    fun createWallet(pin: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createNewWallet(pin)
            _isLoading.value = false
            _feedbackMessage.value = "Nouveau portefeuille Web3 généré avec succès !"
            _isSuccess.value = true
        }
    }

    fun importWallet(seed: String, pin: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.importWallet(seed, pin)
            _isLoading.value = false
            result.onSuccess {
                _feedbackMessage.value = "Portefeuille importé avec succès !"
                _isSuccess.value = true
            }.onFailure {
                _feedbackMessage.value = it.message ?: "Échec d'importation de la seed"
                _isSuccess.value = false
            }
        }
    }
}
