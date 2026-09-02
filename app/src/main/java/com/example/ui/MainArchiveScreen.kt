package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CategoryEntity
import com.example.data.CategoryNode
import com.example.data.DocumentEntity
import com.example.data.DocumentImportance
import com.example.ui.category.AddEditCategoryDialog
import com.example.ui.category.MoveCategoryDialog
import com.example.ui.components.CategoryBreadcrumbsBar
import com.example.ui.components.CategoryIconHelper
import com.example.ui.components.HierarchicalCategoryTree
import com.example.ui.document.AddEditDocumentDialog
import com.example.ui.document.DocumentCard
import com.example.ui.viewer.DocumentViewerDialog
import com.example.util.FileStorageHelper

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MainArchiveScreen(
    viewModel: MainArchiveViewModel,
    modifier: Modifier = Modifier
) {
    val allCategories by viewModel.allCategories.collectAsStateWithLifecycle()
    val categoryTreeNodes by viewModel.categoryTree.collectAsStateWithLifecycle()
    val filteredDocs by viewModel.filteredDocuments.collectAsStateWithLifecycle()
    val expiringDocs by viewModel.expiringDocuments.collectAsStateWithLifecycle()
    val archivedDocs by viewModel.archivedDocuments.collectAsStateWithLifecycle()
    val totalStorage by viewModel.totalStorageUsed.collectAsStateWithLifecycle()

    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val importanceFilter by viewModel.importanceFilter.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()

    val viewingDoc by viewModel.viewingDocument.collectAsStateWithLifecycle()
    val editingDoc by viewModel.editingDocument.collectAsStateWithLifecycle()
    val isAddingDoc by viewModel.isAddingDocument.collectAsStateWithLifecycle()
    val editingCat by viewModel.editingCategory.collectAsStateWithLifecycle()
    val isAddingCat by viewModel.isAddingCategory.collectAsStateWithLifecycle()
    val parentForNewCat by viewModel.parentForNewCategory.collectAsStateWithLifecycle()
    val movingDoc by viewModel.movingDocument.collectAsStateWithLifecycle()

    var showSearchBar by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "أرشيف المستندات والوثائق",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "تصنيفات هرمية • معاينة وتكبير • استيراد الملفات",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showSearchBar = !showSearchBar }) {
                        Icon(
                            if (showSearchBar) Icons.Default.Clear else Icons.Default.Search,
                            contentDescription = "بحث"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Secondary FAB: Create Category
                FloatingActionButton(
                    onClick = { viewModel.openAddCategory(selectedCategory) },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.CreateNewFolder,
                        contentDescription = "إنشاء مجلد",
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Primary Extended FAB: Add Document from Storage
                ExtendedFloatingActionButton(
                    onClick = { viewModel.openAddDocument() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = { Icon(Icons.Default.CloudUpload, contentDescription = null) },
                    text = { Text("إضافة مستند من التخزين", fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // --- Search Bar (Animated Expand) ---
            AnimatedVisibility(visible = showSearchBar) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("بحث بالعنوان، رقم المعاملة، الموقع، الوسوم...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "مسح")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            // --- Stats Summary Bar ---
            ArchiveStatsBanner(
                totalDocs = filteredDocs.size + archivedDocs.size,
                totalCategories = allCategories.size,
                expiringCount = expiringDocs.size,
                totalStorageBytes = totalStorage ?: 0L,
                onExpiringClick = { viewModel.setActiveTab(ArchiveTab.EXPIRING_ALERTS) }
            )

            // --- Navigation Tabs ---
            ScrollableTabRow(
                selectedTabIndex = activeTab.ordinal,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                ArchiveTab.entries.forEach { tab ->
                    val isSelected = activeTab == tab
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.setActiveTab(tab) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                when (tab) {
                                    ArchiveTab.BROWSER -> Icon(Icons.Default.GridView, contentDescription = null, modifier = Modifier.size(16.dp))
                                    ArchiveTab.TREE_EXPLORER -> Icon(Icons.Default.AccountTree, contentDescription = null, modifier = Modifier.size(16.dp))
                                    ArchiveTab.EXPIRING_ALERTS -> {
                                        if (expiringDocs.isNotEmpty()) {
                                            BadgedBox(
                                                badge = {
                                                    Badge(containerColor = Color(0xFFDC2626)) {
                                                        Text("${expiringDocs.size}", color = Color.White)
                                                    }
                                                }
                                            ) {
                                                Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                                            }
                                        } else {
                                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    ArchiveTab.ARCHIVED -> Icon(Icons.Default.Archive, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = tab.titleAr,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // --- Main Content Switcher based on Tab ---
            when (activeTab) {
                ArchiveTab.BROWSER -> {
                    BrowserTabContent(
                        selectedCategory = selectedCategory,
                        allCategories = allCategories,
                        categoryTreeNodes = categoryTreeNodes,
                        documents = filteredDocs,
                        importanceFilter = importanceFilter,
                        onSelectCategory = { viewModel.setSelectedCategory(it) },
                        onImportanceFilterChange = { viewModel.setImportanceFilter(it) },
                        onOpenDocument = { viewModel.openDocumentViewer(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onToggleArchive = { viewModel.toggleArchive(it) },
                        onEditDocument = { viewModel.openEditDocument(it) },
                        onDeleteDocument = { viewModel.deleteDocument(it) },
                        onMoveDocument = { viewModel.openMoveDocumentDialog(it) },
                        onAddDocument = { viewModel.openAddDocument() },
                        onAddCategory = { viewModel.openAddCategory(selectedCategory) }
                    )
                }

                ArchiveTab.TREE_EXPLORER -> {
                    TreeExplorerTabContent(
                        categoryTreeNodes = categoryTreeNodes,
                        selectedCategoryId = selectedCategory?.id,
                        onCategoryClick = { cat ->
                            viewModel.setSelectedCategory(cat)
                            viewModel.setActiveTab(ArchiveTab.BROWSER)
                        },
                        onAddSubCategory = { parent -> viewModel.openAddCategory(parent) },
                        onEditCategory = { cat -> viewModel.openEditCategory(cat) },
                        onDeleteCategory = { cat -> viewModel.deleteCategory(cat) },
                        onAddNewRootCategory = { viewModel.openAddCategory(null) }
                    )
                }

                ArchiveTab.EXPIRING_ALERTS -> {
                    ExpiringAlertsTabContent(
                        documents = expiringDocs,
                        onOpenDocument = { viewModel.openDocumentViewer(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onToggleArchive = { viewModel.toggleArchive(it) },
                        onEditDocument = { viewModel.openEditDocument(it) },
                        onDeleteDocument = { viewModel.deleteDocument(it) },
                        onMoveDocument = { viewModel.openMoveDocumentDialog(it) }
                    )
                }

                ArchiveTab.ARCHIVED -> {
                    ArchivedTabContent(
                        documents = archivedDocs,
                        onOpenDocument = { viewModel.openDocumentViewer(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onToggleArchive = { viewModel.toggleArchive(it) },
                        onEditDocument = { viewModel.openEditDocument(it) },
                        onDeleteDocument = { viewModel.deleteDocument(it) },
                        onMoveDocument = { viewModel.openMoveDocumentDialog(it) }
                    )
                }
            }
        }
    }

    // --- Dialogs & Sheets ---

    // 1. Zoomable & Pannable Document Viewer Dialog
    viewingDoc?.let { doc ->
        DocumentViewerDialog(
            document = doc,
            onDismiss = { viewModel.closeDocumentViewer() },
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onToggleArchive = { viewModel.toggleArchive(it) },
            onDelete = { viewModel.deleteDocument(it) },
            onMoveCategory = { viewModel.openMoveDocumentDialog(it) }
        )
    }

    // 2. Add / Edit Document Dialog (with storage file picker)
    if (isAddingDoc) {
        AddEditDocumentDialog(
            documentToEdit = editingDoc,
            initialCategory = selectedCategory,
            allCategories = allCategories,
            categoryTreeNodes = categoryTreeNodes,
            onDismiss = { viewModel.closeAddEditDocument() },
            onSaveDocument = { viewModel.saveDocument(it) }
        )
    }

    // 3. Add / Edit Category Dialog
    if (isAddingCat) {
        AddEditCategoryDialog(
            categoryToEdit = editingCat,
            initialParentCategory = parentForNewCat,
            allCategories = allCategories,
            onDismiss = { viewModel.closeAddEditCategory() },
            onSaveCategory = { viewModel.saveCategory(it) }
        )
    }

    // 4. Move Document to Category Dialog
    movingDoc?.let { doc ->
        MoveCategoryDialog(
            document = doc,
            allCategories = allCategories,
            onDismiss = { viewModel.closeMoveDocumentDialog() },
            onMove = { docId, newCatId, newCatName ->
                viewModel.moveDocumentToCategory(docId, newCatId, newCatName)
            }
        )
    }
}

/**
 * Top Stats Overview Banner.
 */
@Composable
fun ArchiveStatsBanner(
    totalDocs: Int,
    totalCategories: Int,
    expiringCount: Int,
    totalStorageBytes: Long,
    onExpiringClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Total Docs Stat
            StatPill(
                icon = Icons.Default.Description,
                value = "$totalDocs",
                label = "مستند",
                color = MaterialTheme.colorScheme.primary
            )

            // Total Categories
            StatPill(
                icon = Icons.Default.Folder,
                value = "$totalCategories",
                label = "تصنيف هرمي",
                color = MaterialTheme.colorScheme.secondary
            )

            // Storage Size
            StatPill(
                icon = Icons.Default.Storage,
                value = FileStorageHelper.formatFileSize(totalStorageBytes),
                label = "حجم الأرشيف",
                color = MaterialTheme.colorScheme.tertiary
            )

            // Expiring Alert
            if (expiringCount > 0) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFEE2E2),
                    modifier = Modifier.clickable { onExpiringClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$expiringCount ينتهي قريباً",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = color.copy(alpha = 0.15f),
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = MaterialTheme.colorScheme.outline)
        }
    }
}

/**
 * 1. Browser Tab Content: Breadcrumbs, Subcategory pills, Importance filter chips, Document Cards.
 */
@Composable
fun BrowserTabContent(
    selectedCategory: CategoryEntity?,
    allCategories: List<CategoryEntity>,
    categoryTreeNodes: List<CategoryNode>,
    documents: List<DocumentEntity>,
    importanceFilter: String?,
    onSelectCategory: (CategoryEntity?) -> Unit,
    onImportanceFilterChange: (String?) -> Unit,
    onOpenDocument: (DocumentEntity) -> Unit,
    onToggleFavorite: (DocumentEntity) -> Unit,
    onToggleArchive: (DocumentEntity) -> Unit,
    onEditDocument: (DocumentEntity) -> Unit,
    onDeleteDocument: (DocumentEntity) -> Unit,
    onMoveDocument: (DocumentEntity) -> Unit,
    onAddDocument: () -> Unit,
    onAddCategory: () -> Unit
) {
    // Child categories of the currently selected category
    val directChildren = remember(selectedCategory, allCategories) {
        if (selectedCategory == null) {
            allCategories.filter { it.parentId == null }
        } else {
            allCategories.filter { it.parentId == selectedCategory.id }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- Breadcrumb Navigation Bar ---
        item {
            CategoryBreadcrumbsBar(
                currentCategory = selectedCategory,
                allCategories = allCategories,
                onSelectCategory = onSelectCategory
            )
        }

        // --- Sub-folder Horizontal Chips / Grid ---
        if (directChildren.isNotEmpty()) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedCategory == null) "المجلدات والتصنيفات الرئيسية" else "التصنيفات الفرعية",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "+ مجلد جديد",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onAddCategory() }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(directChildren) { childCat ->
                            val childColor = Color(childCat.colorHex)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = childColor.copy(alpha = 0.12f),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(childColor.copy(alpha = 0.4f))),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSelectCategory(childCat) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        CategoryIconHelper.getIcon(childCat.iconName),
                                        contentDescription = null,
                                        tint = childColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = childCat.name,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = childColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- Importance Filters Row ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الأهمية:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (importanceFilter == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { onImportanceFilterChange(null) }
                ) {
                    Text(
                        text = "الكل",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (importanceFilter == null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (importanceFilter == null) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                listOf("عادي", "هام", "سري وعاجل").forEach { imp ->
                    val isSelected = importanceFilter == imp
                    val impColor = Color(DocumentImportance.fromString(imp).colorHex)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) impColor else impColor.copy(alpha = 0.15f),
                        modifier = Modifier.clickable { onImportanceFilterChange(imp) }
                    ) {
                        Text(
                            text = imp,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.White else impColor,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // --- Document Cards Section ---
        if (documents.isEmpty()) {
            item {
                EmptyArchiveState(
                    title = "لا توجد مستندات في هذا التصنيف",
                    subtitle = "قم بإضافة مستند جديد من وحدة التخزين أو تحديد تصنيف آخر",
                    onAction = onAddDocument
                )
            }
        } else {
            items(documents, key = { it.id }) { doc ->
                DocumentCard(
                    document = doc,
                    onClick = { onOpenDocument(doc) },
                    onToggleFavorite = { onToggleFavorite(doc) },
                    onToggleArchive = { onToggleArchive(doc) },
                    onEdit = { onEditDocument(doc) },
                    onDelete = { onDeleteDocument(doc) },
                    onMoveCategory = { onMoveDocument(doc) }
                )
            }
        }

        // Bottom Spacer for FAB
        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}

/**
 * 2. Tree Explorer Tab Content: Full hierarchical tree view with branch expansion and actions.
 */
@Composable
fun TreeExplorerTabContent(
    categoryTreeNodes: List<CategoryNode>,
    selectedCategoryId: Long?,
    onCategoryClick: (CategoryEntity) -> Unit,
    onAddSubCategory: (parent: CategoryEntity) -> Unit,
    onEditCategory: (CategoryEntity) -> Unit,
    onDeleteCategory: (CategoryEntity) -> Unit,
    onAddNewRootCategory: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AccountTree,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "الهيكل الهرمي لشجرة الأرشفة",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "تصفح المجلدات المتفرعة، التوسيع والطي، وإنشاء الفروع الفرعية",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onAddNewRootCategory() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("جذر جديد", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            HierarchicalCategoryTree(
                categoryNodes = categoryTreeNodes,
                selectedCategoryId = selectedCategoryId,
                onCategoryClick = onCategoryClick,
                onAddSubCategory = onAddSubCategory,
                onEditCategory = onEditCategory,
                onDeleteCategory = onDeleteCategory
            )
        }

        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}

/**
 * 3. Expiring Alerts Tab Content: Documents expiring soon or expired.
 */
@Composable
fun ExpiringAlertsTabContent(
    documents: List<DocumentEntity>,
    onOpenDocument: (DocumentEntity) -> Unit,
    onToggleFavorite: (DocumentEntity) -> Unit,
    onToggleArchive: (DocumentEntity) -> Unit,
    onEditDocument: (DocumentEntity) -> Unit,
    onDeleteDocument: (DocumentEntity) -> Unit,
    onMoveDocument: (DocumentEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "تنبيهات انتهاء الصلاحية والمواعيد",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                        Text(
                            text = "الوثائق، العقود، الهويات والضمانات التي تنتهي قريباً وتتطلب تجديداً",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFB45309)
                        )
                    }
                }
            }
        }

        if (documents.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لا توجد مستندات تقترب من الانتهاء حالياً",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "جميع الوثائق والعقود سارية المفعول.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        } else {
            items(documents, key = { it.id }) { doc ->
                DocumentCard(
                    document = doc,
                    onClick = { onOpenDocument(doc) },
                    onToggleFavorite = { onToggleFavorite(doc) },
                    onToggleArchive = { onToggleArchive(doc) },
                    onEdit = { onEditDocument(doc) },
                    onDelete = { onDeleteDocument(doc) },
                    onMoveCategory = { onMoveDocument(doc) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}

/**
 * 4. Archived Tab Content.
 */
@Composable
fun ArchivedTabContent(
    documents: List<DocumentEntity>,
    onOpenDocument: (DocumentEntity) -> Unit,
    onToggleFavorite: (DocumentEntity) -> Unit,
    onToggleArchive: (DocumentEntity) -> Unit,
    onEditDocument: (DocumentEntity) -> Unit,
    onDeleteDocument: (DocumentEntity) -> Unit,
    onMoveDocument: (DocumentEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Archive,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "سجل الأرشيف القديم والمستندات المؤرشفة",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "الوثائق والعقود المنتهية التي تمت أرشفتها للحفظ التاريخي",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }

        if (documents.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Archive,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "سجل الأرشيف فارغ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "يمكنك أرشفة أي مستند لإبعاده عن القائمة النشطة والاحتفاظ به هنا.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        } else {
            items(documents, key = { it.id }) { doc ->
                DocumentCard(
                    document = doc,
                    onClick = { onOpenDocument(doc) },
                    onToggleFavorite = { onToggleFavorite(doc) },
                    onToggleArchive = { onToggleArchive(doc) },
                    onEdit = { onEditDocument(doc) },
                    onDelete = { onDeleteDocument(doc) },
                    onMoveCategory = { onMoveDocument(doc) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}

@Composable
fun EmptyArchiveState(
    title: String,
    subtitle: String,
    onAction: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onAction() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إضافة مستند الآن", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
