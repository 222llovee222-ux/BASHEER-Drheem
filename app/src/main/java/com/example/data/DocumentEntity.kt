package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Document entity representing an archived document with metadata, physical location,
 * hierarchical category reference, tags, and attached file from storage.
 */
@Entity(
    tableName = "documents",
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["createdDate"]),
        Index(value = ["expiryDate"]),
        Index(value = ["isArchived"])
    ]
)
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val docNumber: String = "",
    val categoryId: Long = 1, // References CategoryEntity.id
    val categoryName: String = "عام", // Cached name for quick lookup
    val categoryPath: String = "عام", // Full hierarchical path e.g. "العقود / السكنية / 2024"
    val archiveLocation: String = "", // Physical archive: خزنة أ، درج 2، الرف الثالث
    val issueDate: Long = System.currentTimeMillis(),
    val expiryDate: Long? = null, // Optional expiry date
    val createdDate: Long = System.currentTimeMillis(),
    val filePath: String? = null, // Path to file stored in app internal storage
    val fileUri: String? = null, // Original Content URI if imported from storage
    val fileName: String? = null, // Original file name e.g. "contract_scan.pdf"
    val fileType: String = "مستند", // صورة, PDF, مستند, بطاقة, عقد, فاتورة
    val mimeType: String? = null, // e.g. "image/jpeg", "application/pdf"
    val fileSize: Long = 0L, // File size in bytes
    val importance: String = "عادي", // عادي, هام, سري وعاجل
    val tags: String = "", // Comma-separated tags
    val notes: String = "",
    val isArchived: Boolean = false,
    val isFavorite: Boolean = false
)

enum class DocumentImportance(val titleAr: String, val colorHex: Long) {
    NORMAL("عادي", 0xFF64748B),
    IMPORTANT("هام", 0xFFD97706),
    CRITICAL("سري وعاجل", 0xFFDC2626);

    companion object {
        fun fromString(value: String): DocumentImportance {
            return entries.firstOrNull { it.titleAr == value || it.name.equals(value, ignoreCase = true) } ?: NORMAL
        }
    }
}
