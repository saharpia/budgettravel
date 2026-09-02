package com.tripbudget.app.ui.quickadd

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tripbudget.app.ui.appViewModelFactory
import com.tripbudget.app.ui.theme.*
import com.tripbudget.app.util.formatMoney

/**
 * The quick-entry sheet — the whole app's reason for being. Typing parses
 * live into a preview so the user confirms by glance, not by filling a form.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddSheet(onDismiss: () -> Unit, onSaved: () -> Unit) {
    val viewModel: QuickAddViewModel = viewModel(factory = appViewModelFactory())
    val state by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(state.saved) {
        if (state.saved) onSaved()
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = Cream) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp).fillMaxWidth()) {
            OutlinedTextField(
                value = state.inputText,
                onValueChange = viewModel::onTextChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("coffee — 5 euro") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
            )

            Spacer(Modifier.height(14.dp))

            val parsed = state.parsed
            if (parsed != null) {
                Surface(color = MintLight, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = TealDark)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(parsed.description, style = MaterialTheme.typography.titleMedium, color = MintText)
                            Text(parsed.category.displayName, style = MaterialTheme.typography.bodyMedium, color = MintMuted)
                        }
                        Text(
                            formatMoney(parsed.amountMinorUnits, parsed.currencyCode, showDecimals = true),
                            style = MaterialTheme.typography.titleLarge,
                            color = MintText,
                        )
                    }
                }
                if (!parsed.confident) {
                    Text(
                        "Guessed the currency — check it before saving.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftMuted,
                        modifier = Modifier.padding(top = 6.dp, start = 4.dp),
                    )
                }
            } else if (state.inputText.isNotBlank()) {
                Text(
                    "Add an amount, e.g. \"$5\" or \"5 euro\".",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftMuted,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(50)) {
                    Text("Cancel")
                }
                Button(
                    onClick = viewModel::save,
                    enabled = parsed != null,
                    modifier = Modifier.weight(2f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal, contentColor = Cream),
                    shape = RoundedCornerShape(50),
                ) {
                    Text("Save expense", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(14.dp))
            Text(
                "Saved offline — will sync when you're back online",
                style = MaterialTheme.typography.bodyMedium,
                color = SoftMuted,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}
