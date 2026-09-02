package com.example.ui.viewer

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.DocumentEntity
import com.example.data.DocumentImportance
import com.example.util.FileStorageHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentViewerDialog(
    document: DocumentEntity,
    onDismiss: () -> Unit,
    onToggleFavorite: (DocumentEntity) -> Unit,
    onToggleArchive: (DocumentEntity) -> Unit,
    onDelete: (DocumentEntity) -> Unit,
    onMoveCategory: ((DocumentEntity) -> Unit)? = null
) {
    val context = LocalContext.current

    // Zoom and Pan states
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var rotationAngle by remember { mutableIntStateOf(0) }
    var isFullscreen by remember { mutableStateOf(false) }
    var showDetailsSheet by remember { mutableStateOf(false) }

    // Transformation gesture handler
    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(0.5f, 8.0f)
        scale = newScale
        offset += offsetChange
    }

    // Helper functions for zoom controls
    val zoomIn = {
        scale = (scale * 1.35f).coerceAtMost(8.0f)
    }
    val zoomOut = {
        scale = (scale / 1.35f).coerceAtLeast(0.5f)
    }
    val resetZoom = {
        scale = 1f
        offset = Offset.Zero
        rotationAngle = 0
    }
    val rotateClockwise = {
        rotationAngle = (rotationAngle + 90) % 360
    }

    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd", Locale("ar")) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF090D16) // Deep obsidian darkroom viewer
        ) {
            Box(modifier = Modifier.fillMaxSize()) {

                // --- 1. Interactive Zoomable & Pannable Document Canvas ---
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = { tapOffset ->
                                    if (scale > 1.2f) {
                                        scale = 1f
                                        offset = Offset.Zero
                                    } else {
                                        scale = 2.5f
                                        offset = Offset.Zero
                                    }
                                }
                            )
                        }
                        .transformable(state = transformState),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y,
                                rotationZ = rotationAngle.toFloat()
                            )
                            .padding(24.dp)
                    ) {
                        DocumentRenderContent(document = document)
                    }
                }

                // --- 2. Top App Bar (Header & Actions) ---
                AnimatedVisibility(
                    visible = !isFullscreen,
                    enter = fadeIn() + slideInVertically { -it },
                    exit = fadeOut() + slideOutVertically { -it },
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xCC0F172A),
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                IconButton(
                                    onClick = onDismiss,
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = Color(0x33FFFFFF),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "إغلاق")
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = document.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = document.categoryPath.ifBlank { document.categoryName },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF94A3B8),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onToggleFavorite(document) },
                                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                                ) {
                                    Icon(
                                        if (document.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "المفضلة",
                                        tint = if (document.isFavorite) Color(0xFFF43F5E) else Color.White
                                    )
                                }

                                IconButton(
                                    onClick = { showDetailsSheet = true },
                                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = "تفاصيل المستند")
                                }
                            }
                        }
                    }
                }

                // --- 3. Bottom Interactive Floating Zoom Controls Bar ---
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 28.dp, start = 16.dp, end = 16.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = Color(0xDD1E293B),
                    shadowElevation = 12.dp,
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0x3394A3B8)))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Zoom Out
                        IconButton(
                            onClick = zoomOut,
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.ZoomOut, contentDescription = "تصغير", modifier = Modifier.size(20.dp))
                        }

                        // Zoom Percentage Pill (Tap to reset 100%)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x3338BDF8),
                            modifier = Modifier.clickable { resetZoom() }
                        ) {
                            Text(
                                text = "${(scale * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        // Zoom In
                        IconButton(
                            onClick = zoomIn,
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.ZoomIn, contentDescription = "تكبير", modifier = Modifier.size(20.dp))
                        }

                        Box(
                            modifier = Modifier
                                .height(20.dp)
                                .width(1.dp)
                                .background(Color(0x4494A3B8))
                        )

                        // Rotate Clockwise
                        IconButton(
                            onClick = rotateClockwise,
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.RotateRight, contentDescription = "تدوير 90°", modifier = Modifier.size(20.dp))
                        }

                        // Reset to default
                        IconButton(
                            onClick = resetZoom,
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.FitScreen, contentDescription = "إعادة ضبط", modifier = Modifier.size(20.dp))
                        }

                        // Fullscreen toggle
                        IconButton(
                            onClick = { isFullscreen = !isFullscreen },
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                        ) {
                            Icon(
                                if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = "ملء الشاشة",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // --- 4. Details Bottom Sheet ---
                if (showDetailsSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showDetailsSheet = false },
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                        containerColor = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ) {
                        DocumentDetailsContent(
                            document = document,
                            dateFormat = dateFormat,
                            onClose = { showDetailsSheet = false },
                            onToggleFavorite = { onToggleFavorite(document) },
                            onToggleArchive = { onToggleArchive(document) },
                            onDelete = {
                                showDetailsSheet = false
                                onDismiss()
                                onDelete(document)
                            },
                            onMoveCategory = {
                                showDetailsSheet = false
                                onMoveCategory?.invoke(document)
                            },
                            onShare = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, document.title)
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "مستند مؤرشف: ${document.title}\n" +
                                                "رقم المستند: ${document.docNumber}\n" +
                                                "التصنيف: ${document.categoryPath}\n" +
                                                "موقع الحفظ: ${document.archiveLocation}\n" +
                                                "تاريخ الإصدار: ${dateFormat.format(Date(document.issueDate))}\n" +
                                                (document.expiryDate?.let { "تاريخ الانتهاء: ${dateFormat.format(Date(it))}\n" } ?: "") +
                                                "الملاحظات: ${document.notes}"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "مشاركة بيانات المستند"))
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Renders high-fidelity document sheet content (real image if available, or styled legal paper certificate/contract).
 */
@Composable
fun DocumentRenderContent(document: DocumentEntity) {
    val context = LocalContext.current
    val hasLocalImage = document.filePath != null && File(document.filePath).exists() &&
            FileStorageHelper.isImageFile(document.mimeType, document.filePath, document.fileName)

    if (hasLocalImage) {
        // High-res Image Scan from Device Storage
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            modifier = Modifier.shadow(24.dp, RoundedCornerShape(12.dp))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(File(document.filePath!!))
                    .crossfade(true)
                    .build(),
                contentDescription = document.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.82f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black)
            )
        }
    } else {
        // High-Fidelity Official Parchment Digital Document Render
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF9)), // Warm ivory archival paper
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
            modifier = Modifier
                .width(360.dp)
                .shadow(24.dp, RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFE2D9C8), RoundedCornerShape(8.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Archival Stamp & Border
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "منظومة الأرشيف الرقمي المعتمد",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF78716C),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ARCHIVE DOCUMENT RECORD",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 8.sp,
                            color = Color(0xFFA8A29E),
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF0F766E).copy(alpha = 0.12f),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF0F766E)))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = Color(0xFF0F766E),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = document.fileType,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF0F766E),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = Color(0xFFE7E0D3), thickness = 1.dp)
                Spacer(modifier = Modifier.height(14.dp))

                // Title & Doc Number
                Text(
                    text = document.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF1C1917),
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 28.sp
                )

                if (document.docNumber.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF5F0E6)
                    ) {
                        Text(
                            text = "رقم المستند: ${document.docNumber}",
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF57534E),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hierarchical Path Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFEFF6FF),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBFDBFE)))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "التصنيف الهرمي للأرشفة:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF1D4ED8),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = document.categoryPath.ifBlank { document.categoryName },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1E40AF),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Physical Archive Locker Location
                if (document.archiveLocation.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFEF3C7),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFDE68A)))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "موقع الحفظ الفيزيائي / الورقي:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF92400E),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = document.archiveLocation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF78350F)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Notes & Content Summary
                if (document.notes.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF5F5F4),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "موجز وتفاصيل المحتوى:",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF57534E),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = document.notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF292524),
                                lineHeight = 20.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Simulated Security Seal & Barcode
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Canvas(modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)) {
                        val barWidth = 3.dp.toPx()
                        val spacing = 5.dp.toPx()
                        var currentX = 0f
                        while (currentX < size.width) {
                            val isThick = ((currentX / spacing).toInt() % 3) == 0
                            drawLine(
                                color = Color(0xFF44403C),
                                start = Offset(currentX, 0f),
                                end = Offset(currentX, size.height),
                                strokeWidth = if (isThick) barWidth * 1.6f else barWidth
                            )
                            currentX += spacing
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "* تم توثيق وفهرسة هذا المستند وفق معايير الأرشفة الإلكترونية الرقمية.",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = Color(0xFFA8A29E),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Bottom Sheet details content with full metadata and action buttons.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DocumentDetailsContent(
    document: DocumentEntity,
    dateFormat: SimpleDateFormat,
    onClose: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleArchive: () -> Unit,
    onDelete: () -> Unit,
    onMoveCategory: () -> Unit,
    onShare: () -> Unit
) {
    val scrollState = rememberScrollState()
    val now = System.currentTimeMillis()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "تفاصيل وبيانات المستند",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "إغلاق")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title and Category
        Text(
            text = document.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Breadcrumb Path Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.DriveFileMove,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "المسار في شجرة التصنيفات:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = document.categoryPath.ifBlank { document.categoryName },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Metadata Grid (Dates, Importance, Storage Location)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Issue Date
            OutlinedCard(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("تاريخ الإصدار", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        dateFormat.format(Date(document.issueDate)),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Expiry Date (with status badge)
            OutlinedCard(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("تاريخ الانتهاء", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(4.dp))
                    if (document.expiryDate != null) {
                        val daysLeft = TimeUnit.MILLISECONDS.toDays(document.expiryDate - now)
                        Text(
                            dateFormat.format(Date(document.expiryDate)),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (daysLeft < 0) Color(0xFFDC2626) else if (daysLeft <= 30) Color(0xFFD97706) else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (daysLeft < 0) "منتهي الصلاحية" else "متبقي $daysLeft يوم",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = if (daysLeft < 0) Color(0xFFDC2626) else if (daysLeft <= 30) Color(0xFFD97706) else Color(0xFF059669)
                        )
                    } else {
                        Text("غير محدد / دائم", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Physical Location & Importance
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedCard(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("درجة الأهمية", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        document.importance,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(DocumentImportance.fromString(document.importance).colorHex)
                    )
                }
            }

            OutlinedCard(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("حجم ومصدر الملف", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        if (document.fileSize > 0) FileStorageHelper.formatFileSize(document.fileSize) else "مستند مدخل",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (document.archiveLocation.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("موقع الحفظ الفيزيائي (الخزنة / الرف / الدرج)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text(document.archiveLocation, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Tags
        if (document.tags.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("الوسوم والكلمات الدلالية", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                document.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "#$tag",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Notes
        if (document.notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("الملاحظات", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = document.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalIconButton(
                onClick = onMoveCategory,
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DriveFileMove, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("نقل المجلد", style = MaterialTheme.typography.labelMedium)
                }
            }

            FilledTonalIconButton(
                onClick = onShare,
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("مشاركة", style = MaterialTheme.typography.labelMedium)
                }
            }

            FilledTonalIconButton(
                onClick = onToggleArchive,
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (document.isArchived) Icons.Default.Unarchive else Icons.Default.Archive,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (document.isArchived) "استعادة" else "أرشفة", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Delete Button
        FilledTonalIconButton(
            onClick = onDelete,
            modifier = Modifier.fillMaxWidth(),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("حذف المستند نهائياً", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
