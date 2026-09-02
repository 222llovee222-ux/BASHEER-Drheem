package com.example.ui.category

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.CategoryEntity
import com.example.ui.components.CategoryIconHelper

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditCategoryDialog(
    categoryToEdit: CategoryEntity? = null,
    initialParentCategory: CategoryEntity? = null,
    allCategories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onSaveCategory: (CategoryEntity) -> Unit
) {
    var name by remember { mutableStateOf(categoryToEdit?.name ?: "") }
    var description by remember { mutableStateOf(categoryToEdit?.description ?: "") }
    var parentId by remember { mutableStateOf(categoryToEdit?.parentId ?: initialParentCategory?.id) }
    var selectedIconName by remember { mutableStateOf(categoryToEdit?.iconName ?: "folder") }
    var selectedColorHex by remember { mutableLongStateOf(categoryToEdit?.colorHex ?: 0xFF0D9488) }

    var parentDropdownExpanded by remember { mutableStateOf(false) }

    // Possible parent categories (cannot be self or children to avoid cyclical loops)
    val availableParents = remember(categoryToEdit, allCategories) {
        if (categoryToEdit == null) allCategories
        else allCategories.filter { it.id != categoryToEdit.id && it.parentId != categoryToEdit.id }
    }

    val selectedParentName = remember(parentId, availableParents) {
        if (parentId == null) "تصنيف رئيسي (مستوى أول - جذر)"
        else availableParents.find { it.id == parentId }?.name ?: "تصنيف رئيسي"
    }

    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (categoryToEdit == null) "إنشاء تصنيف / مجلد هرمي" else "تعديل التصنيف",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إلغاء")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Parent Category Selection (Hierarchy)
                Text(
                    text = "الموقع في الهيكل التنظيمي (الأب)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                ExposedDropdownMenuBox(
                    expanded = parentDropdownExpanded,
                    onExpandedChange = { parentDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedParentName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = parentDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = parentDropdownExpanded,
                        onDismissRequest = { parentDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("📁 تصنيف رئيسي (مستوى أول - جذر)", fontWeight = FontWeight.Bold) },
                            onClick = {
                                parentId = null
                                parentDropdownExpanded = false
                            }
                        )

                        availableParents.forEach { parent ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(if (parent.parentId != null) "  ↳ " else "")
                                        Icon(
                                            CategoryIconHelper.getIcon(parent.iconName),
                                            contentDescription = null,
                                            tint = Color(parent.colorHex),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(parent.name)
                                    }
                                },
                                onClick = {
                                    parentId = parent.id
                                    parentDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم التصنيف / المجلد *") },
                    placeholder = { Text("مثال: عقود الصيانة، فواتير المشتريات...") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("وصف التصنيف (اختياري)") },
                    placeholder = { Text("وصف لنوع الوثائق المخزنة هنا...") },
                    maxLines = 2,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Icon Picker
                Text(
                    text = "الأيقونة المعبرة",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryIconHelper.availableIcons.forEach { (iconKey, label) ->
                        val isSelected = selectedIconName == iconKey
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Color(selectedColorHex).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(selectedColorHex))) else null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedIconName = iconKey }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    CategoryIconHelper.getIcon(iconKey),
                                    contentDescription = label,
                                    tint = if (isSelected) Color(selectedColorHex) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color(selectedColorHex) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Color Swatches
                Text(
                    text = "اللون المميز للمجلد",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CategoryIconHelper.availableColors.forEach { (colorHex, _) ->
                        val isSelected = selectedColorHex == colorHex
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(colorHex))
                                .clickable { selectedColorHex = colorHex }
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("إلغاء")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                val newCategory = (categoryToEdit ?: CategoryEntity(name = name)).copy(
                                    name = name.trim(),
                                    description = description.trim(),
                                    parentId = parentId,
                                    iconName = selectedIconName,
                                    colorHex = selectedColorHex
                                )
                                onSaveCategory(newCategory)
                                onDismiss()
                            }
                        },
                        enabled = name.isNotBlank(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (categoryToEdit == null) "إنشاء المجلد" else "حفظ التعديلات")
                    }
                }
            }
        }
    }
}
