import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.dietlens.R
import com.example.dietlens.feature.home.MainViewModel
import com.example.dietlens.feature.home.ProductCategoryEnum
import com.example.dietlens.feature.scanner.data.UiProduct
import com.example.dietlens.feature.scanner.data.toUiProduct
import com.example.dietlens.theme.Buttons
import com.example.dietlens.theme.Card
import com.example.dietlens.theme.Chip
import com.example.dietlens.theme.DarkText
import com.example.dietlens.theme.OnPrimary
import com.example.dietlens.theme.Selected

@Composable
fun ProductsRoute(
    viewModel: MainViewModel = hiltViewModel(),
    onProductClick: (String) -> Unit,
) {
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val allCategories = viewModel.allCategories

    val errorMessage by viewModel.errorMessage.collectAsState()

    ProductsScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(OnPrimary),
        products = products.map { it.toUiProduct() },
        isLoading = isLoading,
        errorMessage = errorMessage,
        onRetry = viewModel::loadNextPage,
        allCategories = allCategories,
        selectedCategory = selectedCategory,
        onCategorySelected = viewModel::loadCategory,
        onLoadMore = viewModel::loadNextPage,
        onProductClick = onProductClick
    )
}

@Composable
fun ProductsScreen(
    modifier: Modifier = Modifier,
    products: List<UiProduct>,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,

    allCategories: List<ProductCategoryEnum>,
    selectedCategory: ProductCategoryEnum,
    onCategorySelected: (ProductCategoryEnum) -> Unit,
    onLoadMore: () -> Unit,
    onProductClick: (String) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        CategorySelectorRow(
            categories = allCategories,
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected
        )

        if (isLoading && products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null && products.isEmpty()) {
            FullScreenError(
                message = errorMessage,
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            ProductGrid(
                products = products,
                isLoadingMore = isLoading,
                errorMessage = errorMessage,
                onLoadMore = onLoadMore,
                onRetry = onRetry,
                onProductClick = onProductClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectorRow(
    categories: List<ProductCategoryEnum>,
    selectedCategory: ProductCategoryEnum,
    onCategorySelected: (ProductCategoryEnum) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = (category == selectedCategory),
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = stringResource(id = category.titleRes)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(0.5.dp, Selected.copy(alpha = 0.5f)),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    labelColor = Selected,
                    selectedContainerColor = Selected,
                    selectedLabelColor = Color.White,
                    disabledContainerColor = Color.White,
                    disabledLabelColor = Selected.copy(alpha = 0.5f),
                )
            )
        }
    }
}


@Composable
fun ProductGrid(
    products: List<UiProduct>,
    isLoadingMore: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onProductClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(products) { index, product ->
            if (index >= products.size - 4 && !isLoadingMore && errorMessage == null) {
                onLoadMore()
            }
            ProductCard(product = product) {
                onProductClick(product.barcode)
            }
        }

        if (isLoadingMore) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (errorMessage != null) {
            item(span = { GridItemSpan(2) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Button(onClick = onRetry) {
                        Text(stringResource(R.string.try_again))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(product: UiProduct, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(product.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
            Column(
                modifier = Modifier
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    minLines = 2,
                    maxLines = 2,
                    color = DarkText,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.brand,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun FullScreenError(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) {
                Text(stringResource(R.string.try_again))
            }
        }
    }
}