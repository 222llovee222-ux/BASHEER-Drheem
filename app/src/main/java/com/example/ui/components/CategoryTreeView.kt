package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CategoryEntity
import com.example.data.CategoryNode

/**
 * Interactive Breadcrumb Navigation Bar allowing immediate jump to any parent level in tree.
 */
@Composable
fun CategoryBreadcrumbsBar(
    currentCategory: CategoryEntity?,
    allCategories: List<CategoryEntity>,
    onSelectCategory: (CategoryEntity?) -> Unit,
    modifier: Modifier = Modifier
) {
    // Build breadcrumb chain from root down to current
    val breadcrumbList = remember(currentCategory, allCategories) {
        val list = mutableListOf<CategoryEntity>()
        var curr = currentCategory
        while (curr != null) {
            list.add(0, curr)
            curr = if (curr.parentId != null) allCategories.find { it.id == curr!!.parentId } else null
        }
        list
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Root "All Categories" chip
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (currentCategory == null) MaterialTheme.colorScheme.primary else Color.Transparent,
                modifier = Modifier.clickable { onSelectCategory(null) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "الرئيسية",
                        tint = if (currentCategory == null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "كافة التصنيفات",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (currentCategory == null) FontWeight.Bold else FontWeight.Medium,
                        color = if (currentCategory == null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            breadcrumbList.forEachIndexed { index, cat ->
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(16.dp)
                )

                val isLast = index == breadcrumbList.lastIndex
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isLast) Color(cat.colorHex).copy(alpha = 0.15f) else Color.Transparent,
                    modifier = Modifier.clickable { onSelectCategory(cat) }
                ) {
                    Text(
                        text = cat.name,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isLast) FontWeight.Bold else FontWeight.Normal,
                        color = if (isLast) Color(cat.colorHex) else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

/**
 * Recursive Expandable Tree View for Hierarchical Categories.
 */
@Composable
fun HierarchicalCategoryTree(
    categoryNodes: List<CategoryNode>,
    selectedCategoryId: Long?,
    onCategoryClick: (CategoryEntity) -> Unit,
    onAddSubCategory: (parent: CategoryEntity) -> Unit,
    onEditCategory: (CategoryEntity) -> Unit,
    onDeleteCategory: (CategoryEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    // Track expanded nodes
    val expandedMap = remember { mutableStateMapOf<Long, Boolean>() }

    Column(modifier = modifier.fillMaxWidth()) {
        categoryNodes.forEach { node ->
            CategoryTreeNodeItem(
                node = node,
                selectedCategoryId = selectedCategoryId,
                expandedMap = expandedMap,
                onCategoryClick = onCategoryClick,
                onAddSubCategory = onAddSubCategory,
                onEditCategory = onEditCategory,
                onDeleteCategory = onDeleteCategory
            )
        }
    }
}

@Composable
fun CategoryTreeNodeItem(
    node: CategoryNode,
    selectedCategoryId: Long?,
    expandedMap: MutableMap<Long, Boolean>,
    onCategoryClick: (CategoryEntity) -> Unit,
    onAddSubCategory: (parent: CategoryEntity) -> Unit,
    onEditCategory: (CategoryEntity) -> Unit,
    onDeleteCategory: (CategoryEntity) -> Unit
) {
    val isExpanded = expandedMap[node.category.id] ?: (node.level == 0) // Expand top level by default
    val isSelected = selectedCategoryId == node.category.id
    var showMenu by remember { mutableStateOf(false) }

    val hasChildren = node.children.isNotEmpty()
    val categoryColor = Color(node.category.colorHex)

    Column(modifier = Modifier.fillMaxWidth()) {
        // Node Row
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable { onCategoryClick(node.category) },
            shape = RoundedCornerShape(10.dp),
            color = when {
                isSelected -> categoryColor.copy(alpha = 0.15f)
                else -> Color.Transparent
            },
            border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(categoryColor)) else null
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = (12 + (node.level * 20)).dp,
                        end = 8.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Tree branch indent marker for subcategories
                    if (node.level > 0) {
                        Box(
                            modifier = Modifier
                                .size(width = 8.dp, height = 2.dp)
                                .background(categoryColor.copy(alpha = 0.4f))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    // Expand / Collapse Chevron
                    if (hasChildren) {
                        IconButton(
                            onClick = { expandedMap[node.category.id] = !isExpanded },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isExpanded) "طي" else "توسيع",
                                tint = categoryColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(24.dp))
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Folder Icon with Color
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = categoryColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                CategoryIconHelper.getIcon(node.category.iconName),
                                contentDescription = null,
                                tint = categoryColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Category Name & Counts
                    Column {
                        Text(
                            text = node.category.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isSelected) categoryColor else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (node.category.description.isNotBlank() && node.level == 0) {
                            Text(
                                text = node.category.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Right side: Document count pill & action menu
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Document count badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (node.totalDocCount > 0) categoryColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "${node.totalDocCount}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (node.totalDocCount > 0) categoryColor else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    // More Menu
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "خيارات التصنيف",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("إضافة تصنيف فرعي داخل هذا المجلد") },
                                onClick = {
                                    showMenu = false
                                    onAddSubCategory(node.category)
                                },
                                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) }
                            )

                            DropdownMenuItem(
                                text = { Text("تعديل اسم وبيانات التصنيف") },
                                onClick = {
                                    showMenu = false
                                    onEditCategory(node.category)
                                },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                            )

                            DropdownMenuItem(
                                text = { Text("حذف هذا التصنيف", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showMenu = false
                                    onDeleteCategory(node.category)
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        // Recursive Children View with Animation
        AnimatedVisibility(
            visible = hasChildren && isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                node.children.forEach { childNode ->
                    CategoryTreeNodeItem(
                        node = childNode,
                        selectedCategoryId = selectedCategoryId,
                        expandedMap = expandedMap,
                        onCategoryClick = onCategoryClick,
                        onAddSubCategory = onAddSubCategory,
                        onEditCategory = onEditCategory,
                        onDeleteCategory = onDeleteCategory
                    )
                }
            }
        }
    }
}
