package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserRole
import com.example.ui.components.BgrBadge
import com.example.ui.theme.BgrBorder
import com.example.ui.theme.BgrCyanElectric
import com.example.ui.theme.BgrDarkBackground
import com.example.ui.theme.BgrDarkCard
import com.example.ui.theme.BgrEmerald
import com.example.ui.theme.BgrGold
import com.example.ui.theme.BgrTextMuted
import com.example.ui.theme.BgrTextPrimary
import com.example.ui.theme.BgrTextSecondary
import com.example.ui.viewmodel.BgrUiState
import com.example.ui.viewmodel.BgrViewModel

enum class BgrNavDestination(val route: String, val title: String, val icon: ImageVector) {
    WALLET("wallet", "Portefeuille", Icons.Default.AccountBalanceWallet),
    SWAP("swap", "Swaps", Icons.Default.CurrencyExchange),
    PAY("pay", "Payer & CB", Icons.Default.Paid),
    MINING("mining", "Tap-to-Mine", Icons.Default.Bolt),
    HISTORY("history", "Historique", Icons.Default.ReceiptLong),
    ADMIN_PRO("admin_pro", "Espace Pro", Icons.Default.Storefront)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    state: BgrUiState,
    viewModel: BgrViewModel
) {
    var currentScreen by remember { mutableStateOf(BgrNavDestination.WALLET) }
    var isOnboardingDone by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Display feedback snackbars
    LaunchedEffect(state.feedbackMessage) {
        state.feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    // Hardware/gesture back press handling
    BackHandler(enabled = currentScreen != BgrNavDestination.WALLET) {
        currentScreen = BgrNavDestination.WALLET
    }

    if (!isOnboardingDone && !state.isWalletSetupComplete) {
        OnboardingScreen(
            onCreateWallet = { pin -> viewModel.createWallet(pin) },
            onImportWallet = { seed, pin -> viewModel.importWallet(seed, pin) },
            onComplete = { isOnboardingDone = true }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_bgr_app_logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "BGR PAY",
                            color = BgrGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        )
                    }
                },
                actions = {
                    // Role badge indicator (clickable)
                    val roleLabel = when (state.activeRole) {
                        UserRole.ADMIN -> "Admin"
                        UserRole.MERCHANT -> "Marchand"
                        UserRole.USER -> "Client"
                    }
                    val roleColor = when (state.activeRole) {
                        UserRole.ADMIN -> BgrGold
                        UserRole.MERCHANT -> BgrCyanElectric
                        UserRole.USER -> BgrEmerald
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(roleColor.copy(alpha = 0.15f))
                            .border(1.dp, roleColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { currentScreen = BgrNavDestination.ADMIN_PRO }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("top_role_badge"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Mode $roleLabel",
                            color = roleColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BgrDarkBackground,
                    titleContentColor = BgrTextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = BgrDarkCard,
                contentColor = BgrTextSecondary,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .border(0.5.dp, BgrBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                BgrNavDestination.values().forEach { destination ->
                    val isSelected = currentScreen == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = destination },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = destination.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BgrDarkBackground,
                            selectedTextColor = BgrGold,
                            indicatorColor = BgrGold,
                            unselectedIconColor = BgrTextMuted,
                            unselectedTextColor = BgrTextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_${destination.route}")
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = BgrDarkCard,
                    contentColor = BgrTextPrimary,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        containerColor = BgrDarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                BgrNavDestination.WALLET -> {
                    WalletScreen(
                        state = state,
                        onNavigateToSwap = { currentScreen = BgrNavDestination.SWAP },
                        onNavigateToPay = { currentScreen = BgrNavDestination.PAY },
                        onNavigateToMining = { currentScreen = BgrNavDestination.MINING },
                        onSendCrypto = { address, symbol, amount ->
                            viewModel.sendCrypto(address, symbol, amount)
                        }
                    )
                }

                BgrNavDestination.SWAP -> {
                    SwapScreen(
                        tokens = state.tokens,
                        onExecuteSwap = { from, to, amount, slippage ->
                            viewModel.executeSwap(from, to, amount, slippage)
                        },
                        isLoading = state.isLoading
                    )
                }

                BgrNavDestination.PAY -> {
                    PayScreen(
                        merchants = state.merchants,
                        tokens = state.tokens,
                        totalCashbackBgr = state.totalCashbackBgr,
                        onPayMerchant = { mchId, token, amount, pin ->
                            viewModel.payMerchant(mchId, token, amount, pin)
                        },
                        isLoading = state.isLoading
                    )
                }

                BgrNavDestination.MINING -> {
                    MiningScreen(
                        miningState = state.miningState,
                        onTap = { viewModel.tapToMine() },
                        onClaimRewards = { viewModel.claimMiningRewards() },
                        onUpgradeMiner = { viewModel.upgradeMiner() },
                        isLoading = state.isLoading
                    )
                }

                BgrNavDestination.HISTORY -> {
                    TransactionsScreen(
                        transactions = state.filteredTransactions,
                        selectedFilter = state.selectedFilter,
                        searchQuery = state.searchQuery,
                        onFilterChange = { viewModel.onFilterTransactions(it) },
                        onSearchChange = { viewModel.onSearchTransactions(it) }
                    )
                }

                BgrNavDestination.ADMIN_PRO -> {
                    AdminMerchantScreen(
                        currentRole = state.activeRole,
                        merchants = state.merchants,
                        onSwitchRole = { viewModel.switchRole(it) },
                        onToggleVerification = { id, currentStatus ->
                            viewModel.toggleMerchantVerification(id, currentStatus)
                        },
                        onUpdateCashbackRate = { id, rate ->
                            viewModel.updateMerchantCashbackRate(id, rate)
                        }
                    )
                }
            }
        }
    }
}
