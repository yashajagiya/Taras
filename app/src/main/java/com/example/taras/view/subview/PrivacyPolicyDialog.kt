package com.example.taras.view.subview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit,
    onOpenOnlinePolicy: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LegalAndAboutDialog(
        initialTab = LegalTab.PRIVACY,
        onDismiss = onDismiss,
        modifier = modifier
    )
}
