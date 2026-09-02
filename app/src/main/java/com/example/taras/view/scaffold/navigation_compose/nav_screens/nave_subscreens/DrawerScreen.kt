package com.example.taras.view.scaffold.navigation_compose.nav_screens.nave_subscreens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Notifications
import com.example.taras.R
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.DrawerState
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taras.viewmodel.AppearanceViewModel
import com.example.taras.viewmodel.UserViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingDrawer(
    appearanceViewModel: AppearanceViewModel,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    appearanceExpanded: Boolean = false,
    onAppearanceExpandChange: (Boolean) -> Unit = {},
    content: @Composable () -> Unit
) {
    val openDialog = remember { mutableStateOf(false) }

    val gitLink = "https://github.com/yashajagiya"
    val uriHandler = LocalUriHandler.current
    val appearance by appearanceViewModel.appearanceData.collectAsStateWithLifecycle()
    val appearanceOptions = listOf("Light", "Dark", "System Default")

    val userName by userViewModel.userName.collectAsStateWithLifecycle()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            modifier = modifier,
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    ModalDrawerSheet {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Spacer(Modifier.height(12.dp))
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "TARAS",
                                maxLines = 1,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic,
                                letterSpacing = 1.sp,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Box {
                                NavigationDrawerItem(
                                    label = { Text("Hello $userName") },
                                    selected = false,
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.PermIdentity,
                                            contentDescription = "PermIdentity"
                                        )
                                    },
                                    onClick = { openDialog.value = true }
                                )
                                if (openDialog.value) {
                                    UserNameAlertDialog(
                                        currentName = userName,
                                        onNameChange = { userViewModel.updateName(it) },
                                        onDismiss = { openDialog.value = false }
                                    )
                                }
                            }
                            NavigationDrawerItem(
                                label = { Text("GitHub") },
                                selected = false,
                                icon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_github),
                                        contentDescription = "GitHub"
                                    )
                                },
                                onClick = { uriHandler.openUri(gitLink) }
                            )

                            Box {
                                NavigationDrawerItem(
                                    label = { Text("Theme: $appearance") },
                                    selected = false,
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.ColorLens,
                                            contentDescription = "Appearance"
                                        )
                                    },
                                    onClick = { onAppearanceExpandChange(true) }
                                )
                                DropdownMenu(
                                    modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                                    expanded = appearanceExpanded,
                                    onDismissRequest = { onAppearanceExpandChange(false) },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    appearanceOptions.forEach {
                                        DropdownMenuItem(
                                            text = { Text(text = it) },
                                            onClick = {
                                                appearanceViewModel.updateAppearance(it)
                                                onAppearanceExpandChange(false)
                                            }
                                        )
                                    }
                                }
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                NotificationPermissionItem()
                            }
                        }
                    }
                }
            },
            content = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    content()
                }
            }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun NotificationPermissionItem() {
    val context = LocalContext.current
    val notificationPermissionState =
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

    var showRationaleDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    if (!notificationPermissionState.status.isGranted) {
        NavigationDrawerItem(
            label = { Text("Notification Permission ") },
            selected = false,
            icon = {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notification"
                )
            },
            onClick = {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    Toast.makeText(context, "Permission already granted", Toast.LENGTH_SHORT).show()
                } else if (notificationPermissionState.status.shouldShowRationale) {
                    showRationaleDialog = true
                } else {
                    notificationPermissionState.launchPermissionRequest()
                    showSettingsDialog = true
                }
            }
        )
    }

    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            title = { Text("Enable Notifications") },
            text = { Text("Stay updated with race schedules, results, and the latest F1 news. Please grant permission in the next screen.") },
            confirmButton = {
                TextButton(onClick = {
                    showRationaleDialog = false
                    notificationPermissionState.launchPermissionRequest()
                }) {
                    Text("Continue")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRationaleDialog = false }) {
                    Text("Dismiss")
                }
            }
        )
    }

    if (showSettingsDialog && !notificationPermissionState.status.shouldShowRationale) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Permissions Required") },
            text = { Text("It seems notification permissions are disabled or permanently denied. Please enable them in the App Settings to receive updates.") },
            confirmButton = {
                TextButton(onClick = {
                    showSettingsDialog = false
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Could not open settings", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserNameAlertDialog(
    currentName: String,
    onNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tempName = remember { mutableStateOf(currentName) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            modifier = modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Enter your name",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = tempName.value,
                    onValueChange = { tempName.value = it },
                    label = { Text("Name") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = {
                            onNameChange(tempName.value)
                            onDismiss()
                        }
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}
