package com.example.ui.document

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CategoryEntity
import com.example.data.CategoryNode
import com.example.data.DocumentEntity
import com.example.data.DocumentImportance
import com.example.ui.components.CategoryIconHelper
import com.example.util.FileStorageHelper
import com.example.util.ImportedFileInfo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditDocumentDialog(
    documentToEdit: DocumentEntity? = null,
    initialCategory: CategoryEntity? = null,
    allCategories: List<CategoryEntity>,
    categoryTreeNodes: List<CategoryNode>,
    onDismiss: () -> Unit,
    onSaveDocument: (DocumentEntity) -> Unit
) {
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd", Locale("ar")) }

    // Form fields
    var title by remember { mutableStateOf(documentToEdit?.title ?: "") }
    var docNumber by remember { mutableStateOf(documentToEdit?.docNumber ?: "") }
    var selectedCategoryId by remember {
        mutableLongStateOf(
            documentToEdit?.categoryId ?: initialCategory?.id ?: (allCategories.firstOrNull()?.id ?: 1L)
        )
    }
    var archiveLocation by remember { mutableStateOf(documentToEdit?.archiveLocation ?: "") }
    var issueDate by remember { mutableLongStateOf(documentToEdit?.issueDate ?: System.currentTimeMillis()) }
    var expiryDate by remember { mutableStateOf<Long?>(documentToEdit?.expiryDate) }
    var importance by remember { mutableStateOf(documentToEdit?.importance ?: "عادي") }
    var tags by remember { mutableStateOf(documentToEdit?.tags ?: "") }
    var notes by remember { mutableStateOf(documentToEdit?.notes ?: "") }

    // File attachments from device storage
    var localFilePath by remember { mutableStateOf(documentToEdit?.filePath) }
    var originalUri by remember { mutableStateOf(documentToEdit?.fileUri) }
    var fileName by remember { mutableStateOf(documentToEdit?.fileName) }
    var fileType by remember { mutableStateOf(documentToEdit?.fileType ?: "مستند") }
    var mimeType by remember { mutableStateOf(documentToEdit?.mimeType) }
    var fileSize by remember { mutableLongStateOf(documentToEdit?.fileSize ?: 0L) }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var importanceDropdownExpanded by remember { mutableStateOf(false) }

    // SAF File Pickers
    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileInfo = FileStorageHelper.copyUriToInternalStorage(context, uri)
            if (fileInfo != null) {
                localFilePath = fileInfo.localFilePath
                originalUri = fileInfo.originalUri
                fileName = fileInfo.fileName
                fileType = fileInfo.fileTypeDisplay
                mimeType = fileInfo.mimeType
                fileSize = fileInfo.fileSize

                if (title.isBlank()) {
                    title = fileInfo.fileName.substringBeforeLast(".")
                }
                Toast.makeText(context, "تم استيراد الملف بنجاح: ${fileInfo.fileName}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileInfo = FileStorageHelper.copyUriToInternalStorage(context, uri)
            if (fileInfo != null) {
                localFilePath = fileInfo.localFilePath
                originalUri = fileInfo.originalUri
                fileName = fileInfo.fileName
                fileType = "صورة / مسح ضوئي"
                mimeType = fileInfo.mimeType
                fileSize = fileInfo.fileSize

                if (title.isBlank()) {
                    title = "مستند مصور ${dateFormat.format(Date())}"
                }
                Toast.makeText(context, "تم إرفاق الصورة بنجاح", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Date picker dialog helpers
    fun showDatePicker(initialDate: Long, onDateSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance().apply { timeInMillis = initialDate }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                onDateSelected(selectedCal.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    val selectedCategory = remember(selectedCategoryId, allCategories) {
        allCategories.find { it.id == selectedCategoryId }
    }

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
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
                        text = if (documentToEdit == null) "إضافة وأرشفة مستند جديد" else "تعديل بيانات المستند",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إلغاء")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- 1. Storage Import & Attachment Section ---
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (fileName != null) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (fileName != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (fileName != null) {
                            // Attached File Info Card
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            if (FileStorageHelper.isImageFile(mimeType, localFilePath, fileName)) Icons.Default.Image else Icons.Default.Description,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = fileName ?: "مستند مرفق",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${fileType} • ${FileStorageHelper.formatFileSize(fileSize)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        fileName = null
                                        localFilePath = null
                                        originalUri = null
                                        fileSize = 0L
                                        mimeType = null
                                    }
                                ) {
                                    Icon(Icons.Default.Clear, contentDescription = "إزالة المرفق", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        } else {
                            // Empty Attachment Prompt with Storage picker buttons
                            Icon(
                                Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "إرفاق ملف المستند من وحدة التخزين",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "يدعم استيراد الصور، الماسح الضوئي، ملفات PDF والمستندات",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        openDocumentLauncher.launch(
                                            arrayOf(
                                                "application/pdf",
                                                "image/*",
                                                "application/msword",
                                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                                "*/*"
                                            )
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تصفح وحدة التخزين", style = MaterialTheme.typography.labelMedium)
                                }

                                OutlinedButton(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    modifier = Modifier.weight(0.9f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("معرض الصور", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- 2. Title & Document Number ---
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان المستند *") },
                    placeholder = { Text("مثال: عقد إيجار الشقة، شهادة التخرج...") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = docNumber,
                    onValueChange = { docNumber = it },
                    label = { Text("رقم المستند / المعاملة (اختياري)") },
                    placeholder = { Text("مثال: CNT-2024-8841") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- 3. Hierarchical Category Selector ---
                Text(
                    text = "التصنيف الهرمي للأرشفة *",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory?.let { cat ->
                            val parent = allCategories.find { it.id == cat.parentId }
                            if (parent != null) "${parent.name} > ${cat.name}" else cat.name
                        } ?: "اختر تصنيفاً",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        leadingIcon = {
                            selectedCategory?.let {
                                Icon(
                                    CategoryIconHelper.getIcon(it.iconName),
                                    contentDescription = null,
                                    tint = Color(it.colorHex),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        allCategories.forEach { cat ->
                            val parent = allCategories.find { it.id == cat.parentId }
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (parent != null) {
                                            Text("  ↳ ", color = MaterialTheme.colorScheme.outline)
                                        }
                                        Icon(
                                            CategoryIconHelper.getIcon(cat.iconName),
                                            contentDescription = null,
                                            tint = Color(cat.colorHex),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (parent != null) "${parent.name} > ${cat.name}" else cat.name,
                                            fontWeight = if (parent == null) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                },
                                onClick = {
                                    selectedCategoryId = cat.id
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- 4. Physical Archive Location ---
                OutlinedTextField(
                    value = archiveLocation,
                    onValueChange = { archiveLocation = it },
                    label = { Text("موقع الحفظ الفيزيائي (الخزنة / الرف / الدرج)") },
                    placeholder = { Text("مثال: خزنة أ - الرف 2 - ملف العقود") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- 5. Dates: Issue Date & Expiry Date ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Issue Date
                    OutlinedCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showDatePicker(issueDate) { selected -> issueDate = selected }
                            },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تاريخ الإصدار", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                dateFormat.format(Date(issueDate)),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Expiry Date
                    OutlinedCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showDatePicker(expiryDate ?: (System.currentTimeMillis() + 31536000000L)) { selected ->
                                    expiryDate = selected
                                }
                            },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تاريخ الانتهاء", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                }
                                if (expiryDate != null) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "إلغاء",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clickable { expiryDate = null }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = expiryDate?.let { dateFormat.format(Date(it)) } ?: "غير محدد / دائم",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (expiryDate != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- 6. Importance Level ---
                Text(
                    text = "درجة الأهمية",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("عادي", "هام", "سري وعاجل").forEach { level ->
                        val isSelected = importance == level
                        val levelColor = Color(DocumentImportance.fromString(level).colorHex)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) levelColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(levelColor)) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { importance = level }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = level,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) levelColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- 7. Tags & Notes ---
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("الوسوم (مفصولة بفاصلة)") },
                    placeholder = { Text("مثال: عقد, إيجار, رسمي, 2024") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات وتفاصيل إضافية") },
                    placeholder = { Text("أي شروط أو تفاصيل هامة تخص المستند...") },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

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
                            if (title.isNotBlank()) {
                                val category = selectedCategory
                                val catName = category?.name ?: "عام"
                                val parent = allCategories.find { it.id == category?.parentId }
                                val path = if (parent != null) "${parent.name} > ${catName}" else catName

                                val doc = (documentToEdit ?: DocumentEntity(
                                    title = title,
                                    categoryId = selectedCategoryId
                                )).copy(
                                    title = title.trim(),
                                    docNumber = docNumber.trim(),
                                    categoryId = selectedCategoryId,
                                    categoryName = catName,
                                    categoryPath = path,
                                    archiveLocation = archiveLocation.trim(),
                                    issueDate = issueDate,
                                    expiryDate = expiryDate,
                                    importance = importance,
                                    tags = tags.trim(),
                                    notes = notes.trim(),
                                    filePath = localFilePath,
                                    fileUri = originalUri,
                                    fileName = fileName,
                                    fileType = fileType,
                                    mimeType = mimeType,
                                    fileSize = fileSize
                                )

                                onSaveDocument(doc)
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (documentToEdit == null) "حفظ وأرشفة المستند" else "حفظ التعديلات")
                    }
                }
            }
        }
    }
}
