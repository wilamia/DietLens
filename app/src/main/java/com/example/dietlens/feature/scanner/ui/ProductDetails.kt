package com.example.dietlens.feature.scanner.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.dietlens.R
import com.example.dietlens.feature.scanner.data.AllergenKey
import com.example.dietlens.feature.scanner.data.Nutriments
import com.example.dietlens.feature.scanner.data.NutriscoreYearData
import com.example.dietlens.feature.scanner.data.Product
import com.example.dietlens.theme.Buttons
import com.example.dietlens.theme.OnPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProductDetails(
    modifier: Modifier = Modifier,
    product: Product,
    isLiked: Boolean,
    detectedAllergens: List<AllergenKey>,
    scannedAt: Date? = null,
    onLikeClick: () -> Unit
) {

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(OnPrimary),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            SafetyStatusCard(
                detectedAllergens = detectedAllergens,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            ProductInfoCard(
                imageUrl = product.image_front_url,
                productName = product.product_name ?: stringResource(R.string.name_not_found),
                brand = product.brands ?: stringResource(R.string.empty_brand),
                nutriscore = product.getLatestNutriscore(),
                scannedAt = scannedAt,
                isLiked = isLiked,
                onLikeClick = onLikeClick
            )
        }

        item {
            fun splitAndClean(text: String?): List<String> {
                if (text.isNullOrBlank()) return emptyList()
                return text.replace(Regex("en:|-|ru:"), " ")
                    .trim('[', ']')
                    .split(",")
                    .map { it.trim().replaceFirstChar { c -> c.titlecase() } }
                    .filter { it.isNotBlank() }
            }

            val allergenList = splitAndClean(product.allergens)
            val tracesList = splitAndClean(product.traces)

            val combinedList = (allergenList + tracesList)
                .toSet()
                .toList()

            if (combinedList.isNotEmpty()) {
                TracesSection(allergens = combinedList)
            }
        }


        item {
            val ingredients = product.translations?.ingredients
                ?: product.ingredients_text

            if (!ingredients.isNullOrBlank()) {
                InfoBlockCard(
                    title = stringResource(R.string.ingredients),
                    content = ingredients,
                    icon = null
                )
            }
        }

        item {
            val nutriments = product.nutriments
            if (nutriments != null) {
                NutritionInfoCard(nutriscore = null, nutriments = nutriments)
            }
        }
        item {
            Spacer(Modifier.height(90.dp))
        }
    }
}

@Composable
fun ProductInfoCard(
    imageUrl: String?,
    productName: String,
    brand: String,
    scannedAt: Date? = null,
    nutriscore: NutriscoreYearData?,
    isLiked: Boolean,
    onLikeClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            imageUrl?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = productName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )

                    ThumbButton(
                        icon = Icons.Filled.ThumbUp,
                        onClick = onLikeClick,
                        isLiked = isLiked,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = productName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                nutriscore?.grade?.let { grade ->
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(getNutriScoreColor(grade)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = grade.uppercase(),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = brand,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            scannedAt?.let { ts ->
                Text(
                    text = stringResource(
                        R.string.scanned, SimpleDateFormat(
                            "dd-MM-yyyy HH:mm",
                            Locale.getDefault()
                        ).format(ts)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TracesSection(allergens: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.traces),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allergens.forEach { allergen ->
                    ProductTag(text = allergen, color = Color(0xFFE0E0E0))
                }
            }
        }
    }
}

@Composable
fun InfoBlockCard(
    title: String,
    content: String,
    icon: ImageVector?,
    isWarning: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon?.let {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isWarning) MaterialTheme.colorScheme.error else Color.Unspecified
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun NutritionInfoCard(
    nutriscore: NutriscoreYearData?,
    nutriments: Nutriments?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.nutrients),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )

            nutriscore?.grade?.let { grade ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(R.string.nutri_score),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = grade.uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = getNutriScoreColor(grade)
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            nutriments?.let {
                NutrientRow(
                    stringResource(R.string.energy), it.energyKcal?.toString(),
                    stringResource(R.string.kkal)
                )
                NutrientRow(
                    stringResource(R.string.fats), it.fat?.toString(),
                    stringResource(R.string.grams)
                )
                NutrientRow(
                    stringResource(R.string.fats_more),
                    it.saturatedFat?.toString(),
                    stringResource(R.string.grams)
                )
                NutrientRow(
                    stringResource(R.string.carbs),
                    it.carbohydrates?.toString(),
                    stringResource(R.string.grams)
                )
                NutrientRow(
                    stringResource(R.string.sugar),
                    it.sugars?.toString(),
                    stringResource(R.string.grams)
                )
                NutrientRow(
                    stringResource(R.string.protein),
                    it.proteins?.toString(),
                    stringResource(R.string.grams)
                )
                NutrientRow(
                    stringResource(R.string.fiber),
                    it.fiber?.toString(),
                    stringResource(R.string.grams)
                )
                NutrientRow(
                    stringResource(R.string.salt),
                    it.salt?.toString(),
                    stringResource(R.string.grams)
                )
            }
        }
    }
}

@Composable
fun ProductTag(text: String, color: Color, shadow: Boolean = false) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color,
        modifier = Modifier.padding(4.dp),
        shadowElevation = if (shadow) 2.dp else 0.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

@Composable
fun ThumbButton(
    icon: ImageVector,
    onClick: () -> Unit,
    isLiked: Boolean,
    modifier: Modifier = Modifier
) {
    val iconTint = if (isLiked) {
        Buttons
    } else {
        Color.Gray
    }

    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.White)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Like",
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun NutrientRow(label: String, value: String?, unit: String = "") {
    if (value.isNullOrBlank()) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = stringResource(R.string.nutrient_unit, value, unit).trim(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

fun getNutriScoreColor(grade: String?): Color {
    return when (grade?.lowercase()) {
        "a" -> Color(0xFF00882B)
        "b" -> Color(0xFF83C41A)
        "c" -> Color(0xFFFFCC00)
        "d" -> Color(0xFFF67B00)
        "e" -> Color(0xFFE52400)
        else -> Color.Gray
    }
}

@Composable
fun SafetyStatusCard(detectedAllergens: List<AllergenKey>, modifier: Modifier = Modifier) {
    val isSafe = detectedAllergens.isEmpty()

    val backgroundColor = Color(0xFFFFEBEE)
    val contentColor = Color(0xFFC62828)
    val icon = Icons.Default.Close

    val text =
        stringResource(R.string.warning, detectedAllergens.joinToString(", "))

    if (!isSafe) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = contentColor,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = text,
                    color = contentColor,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}