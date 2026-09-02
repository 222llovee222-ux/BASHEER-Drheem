package com.example.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.DecimalFormat

data class ImportedFileInfo(
    val fileName: String,
    val mimeType: String,
    val fileSize: Long,
    val localFilePath: String,
    val originalUri: String,
    val fileTypeDisplay: String
)

object FileStorageHelper {

    /**
     * Copies a file from content URI to app internal storage directory for safe and permanent access.
     */
    fun copyUriToInternalStorage(context: Context, uri: Uri): ImportedFileInfo? {
        return try {
            val contentResolver = context.contentResolver
            var fileName = "document_${System.currentTimeMillis()}"
            var fileSize: Long = 0

            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex) ?: fileName
                    }
                    if (sizeIndex != -1) {
                        fileSize = cursor.getLong(sizeIndex)
                    }
                }
            }

            var mimeType = contentResolver.getType(uri)
            if (mimeType.isNullOrBlank()) {
                val extension = MimeTypeMap.getFileExtensionFromUrl(fileName)
                if (!extension.isNullOrBlank()) {
                    mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
                }
            }
            if (mimeType.isNullOrBlank()) {
                mimeType = "application/octet-stream"
            }

            val docDir = File(context.filesDir, "archived_documents")
            if (!docDir.exists()) {
                docDir.mkdirs()
            }

            // Create unique destination file
            val sanitizedName = fileName.replace("[^a-zA-Z0-9._-]".toRegex(), "_")
            val destFile = File(docDir, "${System.currentTimeMillis()}_$sanitizedName")

            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(destFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            if (fileSize <= 0) {
                fileSize = destFile.length()
            }

            val fileTypeDisplay = determineFileTypeDisplay(fileName, mimeType)

            ImportedFileInfo(
                fileName = fileName,
                mimeType = mimeType,
                fileSize = fileSize,
                localFilePath = destFile.absolutePath,
                originalUri = uri.toString(),
                fileTypeDisplay = fileTypeDisplay
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 بايت"
        val units = arrayOf("بايت", "ك.ب", "م.ب", "ج.ب")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
        val index = digitGroups.coerceIn(0, units.size - 1)
        val value = bytes / Math.pow(1024.0, index.toDouble())
        return "${DecimalFormat("#,##0.#").format(value)} ${units[index]}"
    }

    fun determineFileTypeDisplay(fileName: String, mimeType: String): String {
        val lowerMime = mimeType.lowercase()
        val lowerName = fileName.lowercase()

        return when {
            lowerMime.startsWith("image/") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || lowerName.endsWith(".png") || lowerName.endsWith(".webp") -> "صورة رقمية"
            lowerMime == "application/pdf" || lowerName.endsWith(".pdf") -> "مستند PDF"
            lowerName.endsWith(".doc") || lowerName.endsWith(".docx") -> "مستند Word"
            lowerName.endsWith(".xls") || lowerName.endsWith(".xlsx") -> "جدول بيانات"
            lowerName.contains("عقد") || lowerName.contains("contract") -> "عقد رسمي"
            lowerName.contains("فاتورة") || lowerName.contains("invoice") -> "فاتورة ضريبية"
            lowerName.contains("شهادة") || lowerName.contains("cert") -> "شهادة معتمدة"
            lowerName.contains("هوية") || lowerName.contains("id") || lowerName.contains("pass") -> "بطاقة / هوية"
            else -> "مستند مؤرشف"
        }
    }

    fun isImageFile(mimeType: String?, filePath: String?, fileName: String?): Boolean {
        val mime = mimeType?.lowercase() ?: ""
        val path = filePath?.lowercase() ?: ""
        val name = fileName?.lowercase() ?: ""
        return mime.startsWith("image/") ||
                path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png") || path.endsWith(".webp") ||
                name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".webp")
    }
}
