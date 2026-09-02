package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryIconHelper {

    fun getIcon(iconName: String): ImageVector {
        return when (iconName.lowercase()) {
            "gavel" -> Icons.Default.Gavel
            "home" -> Icons.Default.Home
            "work" -> Icons.Default.Work
            "verified_user" -> Icons.Default.VerifiedUser
            "account_balance" -> Icons.Default.AccountBalance
            "receipt_long" -> Icons.Default.ReceiptLong
            "receipt" -> Icons.Default.Receipt
            "payments" -> Icons.Default.Payments
            "request_quote" -> Icons.Default.RequestQuote
            "badge" -> Icons.Default.Badge
            "perm_identity" -> Icons.Default.PermIdentity
            "directions_car" -> Icons.Default.DirectionsCar
            "local_hospital" -> Icons.Default.LocalHospital
            "science" -> Icons.Default.Science
            "health_and_safety" -> Icons.Default.HealthAndSafety
            "school" -> Icons.Default.School
            "history_edu" -> Icons.Default.HistoryEdu
            "military_tech" -> Icons.Default.MilitaryTech
            "verified" -> Icons.Default.Verified
            "devices" -> Icons.Default.Devices
            "description" -> Icons.Default.Description
            else -> Icons.Default.Folder
        }
    }

    val availableIcons = listOf(
        "folder" to "مجلد عام",
        "gavel" to "قانون وقضاء",
        "home" to "عقارات وسكن",
        "work" to "عمل ووظائف",
        "account_balance" to "بنوك ومالية",
        "receipt_long" to "فواتير ومبيعات",
        "payments" to "مدفوعات وسندات",
        "badge" to "بطاقات وهويات",
        "directions_car" to "مركبات ورخص",
        "local_hospital" to "طب وصحة",
        "science" to "تحاليل ومختبر",
        "school" to "تعليم وجامعات",
        "verified" to "ضمانات وكفالات",
        "devices" to "أجهزة وإلكترونيات",
        "description" to "وثائق رسمية"
    )

    val availableColors = listOf(
        0xFF0D9488 to "فيروزي داكن",
        0xFF0284C7 to "أزرق سماوي",
        0xFF7C3AED to "بنفسجي ملكي",
        0xFFE11D48 to "ياقوتي / وردي",
        0xFF059669 to "أخضر زمردي",
        0xFFD97706 to "كهرماني / ذهبي",
        0xFF475569 to "رمادي حجري",
        0xFFDC2626 to "أحمر قرمزي"
    )
}
