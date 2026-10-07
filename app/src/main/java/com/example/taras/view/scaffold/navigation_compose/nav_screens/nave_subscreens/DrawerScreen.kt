package com.example.taras.view.scaffold.navigation_compose.nav_screens.nave_subscreens

import android.Manifest
import android.content.Context
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
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.SportsMotorsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Style
import com.example.taras.R
import com.example.taras.network_calls.ApiConstants
import com.example.taras.view.subview.PrivacyPolicyDialog
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
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
    onOpenOnboarding: () -> Unit = {},
    onNavigateToTcgBinder: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val openDialog = remember { mutableStateOf(false) }
    val openPrivacyDialog = remember { mutableStateOf(false) }

    val gitLink = "https://github.com/yashajagiya"
    val uriHandler = LocalUriHandler.current
    val appearance by appearanceViewModel.appearanceData.collectAsStateWithLifecycle()
    val appearanceOptions = listOf("Light", "Dark", "System Default")

    val userName by userViewModel.userName.collectAsStateWithLifecycle()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            modifier = modifier,
            drawerState = drawerState,
            gesturesEnabled = drawerState.isOpen,
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
                                text = stringResource(R.string.drawer_title),
                                maxLines = 1,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic,
                                letterSpacing = 1.sp,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            // F1 GridTCG 2026 Navigation Item
                            NavigationDrawerItem(
                                label = {
                                    Text(
                                        text = "F1 GridTCG Binder",
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                selected = false,
                                icon = {
                                    Icon(
                                        imageVector = Icons.Filled.Style,
                                        contentDescription = "F1 GridTCG",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    onNavigateToTcgBinder()
                                }
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                            Box {
                                NavigationDrawerItem(
                                    label = { Text(stringResource(R.string.drawer_hello_user, userName)) },
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
                                label = { Text(stringResource(R.string.drawer_github)) },
                                selected = false,
                                icon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_github),
                                        contentDescription = "GitHub"
                                    )
                                },
                                onClick = { uriHandler.openUri(gitLink) }
                            )

                            val localizedAppearance = when (appearance) {
                                "Light" -> stringResource(R.string.theme_light)
                                "Dark" -> stringResource(R.string.theme_dark)
                                else -> stringResource(R.string.theme_system_default)
                            }
                            Box {
                                NavigationDrawerItem(
                                    label = { Text(stringResource(R.string.drawer_theme, localizedAppearance)) },
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
                                        val itemLabel = when (it) {
                                            "Light" -> stringResource(R.string.theme_light)
                                            "Dark" -> stringResource(R.string.theme_dark)
                                            else -> stringResource(R.string.theme_system_default)
                                        }
                                        DropdownMenuItem(
                                            text = { Text(text = itemLabel) },
                                            onClick = {
                                                appearanceViewModel.updateAppearance(it)
                                                onAppearanceExpandChange(false)
                                            }
                                        )
                                    }
                                }
                            }

                            var widgetThemeExpanded by remember { mutableStateOf(false) }
                            val widgetTheme by appearanceViewModel.widgetThemeData.collectAsStateWithLifecycle()
                            val widgetThemeOptions = listOf("Dark", "Light", "System Default")
                            val localizedWidgetTheme = when (widgetTheme) {
                                "Light" -> stringResource(R.string.theme_light)
                                "Dark" -> stringResource(R.string.theme_dark)
                                else -> stringResource(R.string.theme_system_default)
                            }

                            Box {
                                NavigationDrawerItem(
                                    label = { Text(stringResource(R.string.drawer_widget_theme, localizedWidgetTheme)) },
                                    selected = false,
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.ColorLens,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = { widgetThemeExpanded = true }
                                )
                                DropdownMenu(
                                    modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                                    expanded = widgetThemeExpanded,
                                    onDismissRequest = { widgetThemeExpanded = false },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    widgetThemeOptions.forEach {
                                        val itemLabel = when (it) {
                                            "Light" -> stringResource(R.string.theme_light)
                                            "Dark" -> stringResource(R.string.theme_dark)
                                            else -> stringResource(R.string.theme_system_default)
                                        }
                                        DropdownMenuItem(
                                            text = { Text(text = itemLabel) },
                                            onClick = {
                                                appearanceViewModel.updateWidgetTheme(it)
                                                widgetThemeExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Language Selector
                            var languageExpanded by remember { mutableStateOf(false) }
                            val drawerContext = LocalContext.current
                            val currentLocaleTag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val localeManager = drawerContext.getSystemService(Context.LOCALE_SERVICE) as? android.app.LocaleManager
                                localeManager?.applicationLocales?.toLanguageTags()?.split(",")?.firstOrNull() ?: ""
                            } else {
                                java.util.Locale.getDefault().language
                            }

                            val languageOptions = listOf(
                                "" to stringResource(R.string.lang_system_default),
                                "en" to stringResource(R.string.lang_en),
                                "hi" to stringResource(R.string.lang_hi),
                                "es" to stringResource(R.string.lang_es),
                                "de" to stringResource(R.string.lang_de),
                                "fr" to stringResource(R.string.lang_fr),
                                "ja" to stringResource(R.string.lang_ja),
                                "pt" to stringResource(R.string.lang_pt)
                            )

                            val currentLangDisplay = languageOptions.firstOrNull { it.first == currentLocaleTag }?.second
                                ?: languageOptions.firstOrNull { currentLocaleTag.startsWith(it.first) && it.first.isNotEmpty() }?.second
                                ?: stringResource(R.string.lang_system_default)

                            Box {
                                NavigationDrawerItem(
                                    label = { Text(stringResource(R.string.drawer_language, currentLangDisplay)) },
                                    selected = false,
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Language,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = { languageExpanded = true }
                                )
                                DropdownMenu(
                                    modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                                    expanded = languageExpanded,
                                    onDismissRequest = { languageExpanded = false },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    languageOptions.forEach { (tag, name) ->
                                        DropdownMenuItem(
                                            text = { Text(text = name) },
                                            onClick = {
                                                languageExpanded = false
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                    val localeManager = drawerContext.getSystemService(Context.LOCALE_SERVICE) as? android.app.LocaleManager
                                                    if (tag.isEmpty()) {
                                                        localeManager?.applicationLocales = android.os.LocaleList.getEmptyLocaleList()
                                                    } else {
                                                        localeManager?.applicationLocales = android.os.LocaleList.forLanguageTags(tag)
                                                    }
                                                } else {
                                                    val locale = if (tag.isEmpty()) java.util.Locale.getDefault() else java.util.Locale.forLanguageTag(tag)
                                                    java.util.Locale.setDefault(locale)
                                                    val config = drawerContext.resources.configuration
                                                    config.setLocale(locale)
                                                    drawerContext.resources.updateConfiguration(config, drawerContext.resources.displayMetrics)
                                                    (drawerContext as? android.app.Activity)?.recreate()
                                                }
                                            }
                                        )
                                    }
                                }
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                NotificationPermissionItem()
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            NavigationDrawerItem(
                                label = { Text(stringResource(R.string.drawer_welcome_guide)) },
                                selected = false,
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.SportsMotorsports,
                                        contentDescription = "Setup"
                                    )
                                },
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    onOpenOnboarding()
                                }
                            )

                            NavigationDrawerItem(
                                label = { Text(stringResource(R.string.drawer_about_privacy)) },
                                selected = false,
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.PrivacyTip,
                                        contentDescription = "Privacy Policy"
                                    )
                                },
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    openPrivacyDialog.value = true
                                }
                            )

                            if (openPrivacyDialog.value) {
                                PrivacyPolicyDialog(
                                    onDismiss = { openPrivacyDialog.value = false },
                                    onOpenOnlinePolicy = {
                                        try {
                                            uriHandler.openUri(ApiConstants.PRIVACY_POLICY_URL)
                                        } catch (e: Exception) {
                                            Toast.makeText(drawerContext, "Could not open browser", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
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
    var hasRequestedOnce by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }

    if (!notificationPermissionState.status.isGranted) {
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.drawer_notification_permission)) },
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
                    Toast.makeText(context, context.getString(R.string.drawer_notifications_already_granted), Toast.LENGTH_SHORT).show()
                } else if (notificationPermissionState.status.shouldShowRationale) {
                    showRationaleDialog = true
                } else if (hasRequestedOnce) {
                    showSettingsDialog = true
                } else {
                    hasRequestedOnce = true
                    notificationPermissionState.launchPermissionRequest()
                }
            }
        )
    }

    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            title = { Text(stringResource(R.string.dialog_enable_notifications_title)) },
            text = { Text(stringResource(R.string.dialog_enable_notifications_desc)) },
            confirmButton = {
                TextButton(onClick = {
                    showRationaleDialog = false
                    notificationPermissionState.launchPermissionRequest()
                }) {
                    Text(stringResource(R.string.continue_text))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRationaleDialog = false }) {
                    Text(stringResource(R.string.dismiss))
                }
            }
        )
    }

    if (showSettingsDialog && !notificationPermissionState.status.shouldShowRationale) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text(stringResource(R.string.dialog_permissions_required_title)) },
            text = { Text(stringResource(R.string.dialog_permissions_required_desc)) },
            confirmButton = {
                TextButton(onClick = {
                    showSettingsDialog = false
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, context.getString(R.string.drawer_could_not_open_settings), Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text(stringResource(R.string.dialog_open_settings))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text(stringResource(R.string.cancel))
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
                    text = stringResource(R.string.drawer_user_name_dialog_title),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = tempName.value,
                    onValueChange = { tempName.value = it },
                    label = { Text(stringResource(R.string.drawer_name_label)) },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(
                        onClick = {
                            onNameChange(tempName.value)
                            onDismiss()
                        }
                    ) {
                        Text(stringResource(R.string.done))
                    }
                }
            }
        }
    }
}
