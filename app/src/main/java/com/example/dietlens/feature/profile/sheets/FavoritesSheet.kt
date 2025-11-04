package com.example.dietlens.feature.profile.sheets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import com.example.dietlens.feature.profile.FavoriteProduct
import com.example.dietlens.theme.Buttons
import com.example.dietlens.theme.OnPrimary

@Composable
fun FavoritesSheet(
    items: List<FavoriteProduct>,
    updatingIds: Set<String>,
    onToggle: (productId: String, name: String, imageUrl: String?) -> Unit,
    onOpenDetails: (productId: String) -> Unit,
    onClose: () -> Unit
) {
    Column(Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)
        .background(OnPrimary)) {
        Text(stringResource(R.string.favourite_products_sheet), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))

        if (items.isEmpty()) {
            Text(stringResource(R.string.empty_products_sheet))
            Spacer(Modifier.height(8.dp))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(items, key = { it.productId }) { item ->
                    FavoriteRow(
                        item = item,
                        loading = updatingIds.contains(item.productId),
                        onToggle = { onToggle(item.productId, item.name, item.imageUrl) },
                        onRowClick = { onOpenDetails(item.productId) }
                    )
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }

        Spacer(Modifier.height(12.dp))
        OutlinedButton (onClick = onClose, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.close_history), color = Buttons)
        }
    }
}

@Composable
private fun FavoriteRow(
    item: FavoriteProduct,
    loading: Boolean,
    onToggle: () -> Unit,
    onRowClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !loading) { onRowClick() },
        colors = CardDefaults.elevatedCardColors(containerColor = androidx.compose.ui.graphics.Color.White)
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
                Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.FavoriteBorder, contentDescription = null)
                }
            }

            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Buttons)
                if (!item.brand.isNullOrBlank()) {
                    Text(item.brand!!, style = MaterialTheme.typography.bodySmall, color = Buttons)
                }
            }

            IconButton(onClick = onToggle, enabled = !loading) {
                val tint = if (item.liked) Buttons else MaterialTheme.colorScheme.onSurfaceVariant
                Icon(
                    imageVector = if (item.liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = tint
                )
            }
        }
    }
}
