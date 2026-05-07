package com.alkapa.chiflido.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alkapa.chiflido.R
import com.alkapa.chiflido.ui.battery.BatteryTab
import com.alkapa.chiflido.ui.battery.BatteryViewModel
import com.alkapa.chiflido.ui.messaging.MessagingTab
import com.alkapa.chiflido.ui.messaging.MessagingViewModel

private enum class HomeTab { Messaging, Battery, Payments }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    messagingViewModel: MessagingViewModel,
    batteryViewModel: BatteryViewModel,
    serviceEnabled: Boolean,
    onServiceEnabledChange: (Boolean) -> Unit,
    onAddContact: () -> Unit,
    notifListenerGranted: Boolean,
    onRequestNotifListener: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(HomeTab.Messaging) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    Switch(
                        checked = serviceEnabled,
                        onCheckedChange = onServiceEnabledChange,
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        floatingActionButton = {
            if (selectedTab == HomeTab.Messaging) {
                FloatingActionButton(onClick = onAddContact) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_contact))
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            if (!notifListenerGranted) {
                com.alkapa.chiflido.ui.components.PermissionBanner(
                    title = stringResource(R.string.permission_notif_listener_title),
                    body = stringResource(R.string.permission_notif_listener_body),
                    actionLabel = stringResource(R.string.permission_grant),
                    onAction = onRequestNotifListener,
                )
            }
            HomeTabs(
                selected = selectedTab,
                onSelected = { selectedTab = it },
            )
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    HomeTab.Messaging -> MessagingTab(
                        viewModel = messagingViewModel,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 8.dp),
                    )
                    HomeTab.Battery -> BatteryTab(
                        viewModel = batteryViewModel,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 8.dp),
                    )
                    HomeTab.Payments -> PaymentsPlaceholder()
                }
            }
        }
    }
}

@Composable
private fun HomeTabs(
    selected: HomeTab,
    onSelected: (HomeTab) -> Unit,
) {
    TabRow(selectedTabIndex = selected.ordinal) {
        Tab(
            selected = selected == HomeTab.Messaging,
            onClick = { onSelected(HomeTab.Messaging) },
            text = { Text(stringResource(R.string.tab_messaging)) },
        )
        Tab(
            selected = selected == HomeTab.Battery,
            onClick = { onSelected(HomeTab.Battery) },
            text = { Text(stringResource(R.string.tab_battery)) },
        )
        Tab(
            selected = selected == HomeTab.Payments,
            onClick = { onSelected(HomeTab.Payments) },
            enabled = false,                  // habilitar cuando se implemente
            text = { Text(stringResource(R.string.tab_payments)) },
        )
    }
}

@Composable
private fun PaymentsPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.payments_coming_soon),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
