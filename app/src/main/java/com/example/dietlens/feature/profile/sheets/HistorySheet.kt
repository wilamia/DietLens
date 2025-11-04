package com.example.dietlens.feature.profile.sheets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.dietlens.R
import com.example.dietlens.feature.profile.ScanHistoryEntry
import com.example.dietlens.theme.Buttons
import com.example.dietlens.theme.OnPrimary

@Composable
fun HistorySheet(
    items: List<ScanHistoryEntry>,
    deletingIds: Set<String>,
    onOpenDetails: (barcode: String) -> Unit,
    onDelete: (docId: String) -> Unit,
    onClose: () -> Unit
) {
    val total = items.size
    val dangerous = items.count { it.detectedAllergens.isNotEmpty() }

    Column(
        Modifier
            .fillMaxWidth()
            .background(OnPrimary)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            stringResource(R.string.history_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = Buttons
        )
        if (total > 0) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(
                    R.string.history_stats,
                    total,
                    dangerous
                ),
            )
        }

        Spacer(Modifier.height(8.dp))

        if (items.isEmpty()) {
            Text(stringResource(R.string.empty_history_product))
            Spacer(Modifier.height(8.dp))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(items, key = { it.docId }) { item ->
                    HistoryRow(
                        item = item,
                        loading = deletingIds.contains(item.docId),
                        onOpen = { onOpenDetails(item.barcode) },
                        onDelete = { onDelete(item.docId) }
                    )
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }

        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.close_history), color = Buttons)
        }
    }
}

@Composable
private fun HistoryRow(
    item: ScanHistoryEntry,
    loading: Boolean,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !loading, onClick = onOpen),
        colors = CardDefaults.elevatedCardColors(containerColor = OnPrimary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!item.imageUrl.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(item.imageUrl),
                    contentDescription = item.name,
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                    Text("🧾")
                }
            }

            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (item.barcode.isNotBlank()) {
                    Text(
                        stringResource(R.string.barcode_history, item.barcode),
                        style = MaterialTheme.typography.bodySmall,
                        color = Buttons
                    )
                }
                if (item.detectedAllergens.isNotEmpty()) {
                    Text(
                        stringResource(
                            R.string.allergies_history,
                            item.detectedAllergens.joinToString()
                        ), style = MaterialTheme.typography.bodySmall, color = Buttons
                    )
                }
            }

            IconButton(onClick = onDelete, enabled = !loading) {
                if (loading) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                } else {
                    Icon(Icons.Filled.Close, contentDescription = "Удалить")
                }
            }
        }
    }
}