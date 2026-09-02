package com.example.data

import java.util.concurrent.TimeUnit

object SampleData {

    fun getInitialCategories(): List<CategoryEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            // --- Root 1: الشؤون القانونية والعقود ---
            CategoryEntity(
                id = 1,
                parentId = null,
                name = "الشؤون القانونية والعقود",
                description = "العقود المبرمة والاتفاقيات والتوكيلات القانونية",
                iconName = "gavel",
                colorHex = 0xFF0D9488,
                orderIndex = 1,
                createdDate = now
            ),
            CategoryEntity(
                id = 2,
                parentId = 1,
                name = "عقود الإيجار والعقارات",
                description = "عقود الإيجار السكني والتجاري والملكية العقارية",
                iconName = "home",
                colorHex = 0xFF0F766E,
                orderIndex = 1,
                createdDate = now
            ),
            CategoryEntity(
                id = 3,
                parentId = 1,
                name = "عقود العمل والخدمات",
                description = "عقود التوظيف والخدمات الاستشارية والتوريد",
                iconName = "work",
                colorHex = 0xFF14B8A6,
                orderIndex = 2,
                createdDate = now
            ),
            CategoryEntity(
                id = 4,
                parentId = 1,
                name = "الوكالات والاتفاقيات الرسمية",
                description = "التوكيلات الشرعية والمصادقات العدلية",
                iconName = "verified_user",
                colorHex = 0xFF047857,
                orderIndex = 3,
                createdDate = now
            ),

            // --- Root 2: المعاملات المالية والمحاسبية ---
            CategoryEntity(
                id = 5,
                parentId = null,
                name = "المعاملات المالية والمحاسبية",
                description = "الفواتير، الإيصالات، السندات البنكية والإقرارات الضريبية",
                iconName = "account_balance",
                colorHex = 0xFF0284C7,
                orderIndex = 2,
                createdDate = now
            ),
            CategoryEntity(
                id = 6,
                parentId = 5,
                name = "فواتير الشراء والتوريد",
                description = "فواتير الأجهزة، الأثاث والمشتريات الرسمية",
                iconName = "receipt_long",
                colorHex = 0xFF0369A1,
                orderIndex = 1,
                createdDate = now
            ),
            CategoryEntity(
                id = 7,
                parentId = 5,
                name = "السندات والتحويلات البنكية",
                description = "سندات القبض والصرف وكشوف الحسابات",
                iconName = "payments",
                colorHex = 0xFF38BDF8,
                orderIndex = 2,
                createdDate = now
            ),
            CategoryEntity(
                id = 8,
                parentId = 5,
                name = "الإقرارات والوثائق الضريبية",
                description = "إقرارات ضريبة القيمة المضافة والزكاة",
                iconName = "request_quote",
                colorHex = 0xFF075985,
                orderIndex = 3,
                createdDate = now
            ),

            // --- Root 3: الوثائق والهويات الشخصية ---
            CategoryEntity(
                id = 9,
                parentId = null,
                name = "الوثائق والهويات الشخصية",
                description = "الهويات الوطنية، جوازات السفر، والرخص الرسمية",
                iconName = "badge",
                colorHex = 0xFF7C3AED,
                orderIndex = 3,
                createdDate = now
            ),
            CategoryEntity(
                id = 10,
                parentId = 9,
                name = "بطاقات الهوية وجوازات السفر",
                description = "الهوية الوطنية، الإقامة، وجواز السفر الدولي",
                iconName = "perm_identity",
                colorHex = 0xFF6D28D9,
                orderIndex = 1,
                createdDate = now
            ),
            CategoryEntity(
                id = 11,
                parentId = 9,
                name = "رخص القيادة وسير المركبات",
                description = "رخص السير، ملكيات المركبات والفحص الدوري",
                iconName = "directions_car",
                colorHex = 0xFF8B5CF6,
                orderIndex = 2,
                createdDate = now
            ),

            // --- Root 4: السجلات والتقارير الطبية ---
            CategoryEntity(
                id = 13,
                parentId = null,
                name = "السجلات والتقارير الطبية",
                description = "الفحوصات، الوصفات، وبوالص التأمين الصحي",
                iconName = "local_hospital",
                colorHex = 0xFFE11D48,
                orderIndex = 4,
                createdDate = now
            ),
            CategoryEntity(
                id = 14,
                parentId = 13,
                name = "التحاليل والفحوصات المخبرية",
                description = "نتائج المختبر والأشعة الدورية",
                iconName = "science",
                colorHex = 0xFFBE123C,
                orderIndex = 1,
                createdDate = now
            ),
            CategoryEntity(
                id = 15,
                parentId = 13,
                name = "بوالص التأمين الصحي",
                description = "كروت التأمين، والموافقات الطبية",
                iconName = "health_and_safety",
                colorHex = 0xFFF43F5E,
                orderIndex = 2,
                createdDate = now
            ),

            // --- Root 5: الشهادات والمؤهلات الأكاديمية ---
            CategoryEntity(
                id = 17,
                parentId = null,
                name = "الشهادات والمؤهلات الأكاديمية",
                description = "الشهادات الجامعية، السجلات الأكاديمية والدورات",
                iconName = "school",
                colorHex = 0xFF059669,
                orderIndex = 5,
                createdDate = now
            ),
            CategoryEntity(
                id = 18,
                parentId = 17,
                name = "الشهادات الجامعية والدراسات العليا",
                description = "وثائق التخرج الرسمية والسجلات الأكاديمية",
                iconName = "history_edu",
                colorHex = 0xFF047857,
                orderIndex = 1,
                createdDate = now
            ),
            CategoryEntity(
                id = 19,
                parentId = 17,
                name = "الدورات التدريبية والمهنية",
                description = "شهادات الحضور، الدبلومات والدورات التخصصية",
                iconName = "military_tech",
                colorHex = 0xFF10B981,
                orderIndex = 2,
                createdDate = now
            ),

            // --- Root 6: الضمانات والكفالات الفنية ---
            CategoryEntity(
                id = 20,
                parentId = null,
                name = "الضمانات والكفالات الفنية",
                description = "شهادات ضمان الأجهزة والسيارات والإلكترونيات",
                iconName = "verified",
                colorHex = 0xFFD97706,
                orderIndex = 6,
                createdDate = now
            ),
            CategoryEntity(
                id = 21,
                parentId = 20,
                name = "ضمانات الأجهزة والمعدات",
                description = "الأجهزة المنزلية، الإلكترونيات والهواتف",
                iconName = "devices",
                colorHex = 0xFFB45309,
                orderIndex = 1,
                createdDate = now
            )
        )
    }

    fun getInitialDocuments(): List<DocumentEntity> {
        val now = System.currentTimeMillis()
        val day = TimeUnit.DAYS.toMillis(1)

        return listOf(
            DocumentEntity(
                id = 1,
                title = "عقد إيجار الشقة السكنية 2024 - 2025",
                docNumber = "CNT-EJR-88419",
                categoryId = 2,
                categoryName = "عقود الإيجار والعقارات",
                categoryPath = "الشؤون القانونية والعقود > عقود الإيجار والعقارات",
                archiveLocation = "خزنة أ - مصنف العقود السكنية - الحافظة الزرقاء",
                issueDate = now - (90 * day),
                expiryDate = now + (275 * day),
                createdDate = now - (90 * day),
                fileName = "residential_lease_2024.pdf",
                fileType = "عقد موثق",
                mimeType = "application/pdf",
                fileSize = 2450000L, // ~2.45 MB
                importance = DocumentImportance.CRITICAL.titleAr,
                tags = "عقد, إيجار, سكن, عقارات, منصة إيجار, موثق",
                notes = "عقد إيجار سكني موثق مع المالك متضمناً جدول السداد الربع سنوي وتفاصيل الحساب البنكي المعتمد.",
                isArchived = false,
                isFavorite = true
            ),
            DocumentEntity(
                id = 2,
                title = "جواز السفر الدولي والهوية الوطنية",
                docNumber = "PASS-902481023",
                categoryId = 10,
                categoryName = "بطاقات الهوية وجوازات السفر",
                categoryPath = "الوثائق والهويات الشخصية > بطاقات الهوية وجوازات السفر",
                archiveLocation = "درج المكتب العلوي (مغلق) - حافظة الوثائق الجلدية",
                issueDate = now - (400 * day),
                expiryDate = now + (22 * day), // Expiring soon alert!
                createdDate = now - (400 * day),
                fileName = "passport_scan_hd.jpg",
                fileType = "جواز سفر / بطاقة",
                mimeType = "image/jpeg",
                fileSize = 1850000L,
                importance = DocumentImportance.CRITICAL.titleAr,
                tags = "جواز سفر, هوية, رسمي, تجديد, سفر دولي",
                notes = "تنبيه: الجواز يقترب من موعد الانتهاء (أقل من شهر). يلزم حجز موعد لتجديده قبل السفر القادم.",
                isArchived = false,
                isFavorite = true
            ),
            DocumentEntity(
                id = 3,
                title = "شهادة البكالوريوس في هندسة البرمجيات",
                docNumber = "UNIV-2021-CERT-091",
                categoryId = 18,
                categoryName = "الشهادات الجامعية والدراسات العليا",
                categoryPath = "الشهادات والمؤهلات الأكاديمية > الشهادات الجامعية والدراسات العليا",
                archiveLocation = "الرف الخشبي 2 - إطار الشهادات المعتمدة",
                issueDate = now - (1200 * day),
                expiryDate = null,
                createdDate = now - (1200 * day),
                fileName = "degree_certificate_honor.pdf",
                fileType = "شهادة جامعية",
                mimeType = "application/pdf",
                fileSize = 3120000L,
                importance = DocumentImportance.IMPORTANT.titleAr,
                tags = "شهادة, جامعة, بكالوريوس, هندسة, مرتبة الشرف, معتمد",
                notes = "النسخة الأصلية المصدقة من وزارة التعليم مع السجل الأكاديمي الشامل للمقررات والمعدل التراكمي.",
                isArchived = false,
                isFavorite = true
            ),
            DocumentEntity(
                id = 4,
                title = "تقرير الفحص الطبي السنوي والتحاليل الشاملة",
                docNumber = "MED-LAB-2024-512",
                categoryId = 14,
                categoryName = "التحاليل والفحوصات المخبرية",
                categoryPath = "السجلات والتقارير الطبية > التحاليل والفحوصات المخبرية",
                archiveLocation = "مجلد السجلات الصحية - الخزانة 1",
                issueDate = now - (35 * day),
                expiryDate = now + (330 * day),
                createdDate = now - (35 * day),
                fileName = "comprehensive_health_report.pdf",
                fileType = "تقرير طبي",
                mimeType = "application/pdf",
                fileSize = 1420000L,
                importance = DocumentImportance.IMPORTANT.titleAr,
                tags = "طبي, فحص سنوي, تحاليل, مختبر, وظائف كبد, سكر",
                notes = "نتائج فحص الدم الشامل، نسبة السكر التراكمي ووظائف الكبد والكلى. التوصية: متابعة فيتامين د.",
                isArchived = false,
                isFavorite = false
            ),
            DocumentEntity(
                id = 5,
                title = "فاتورة شراء الأجهزة الذكية وشاشات العرض",
                docNumber = "INV-ELEC-8812",
                categoryId = 6,
                categoryName = "فواتير الشراء والتوريد",
                categoryPath = "المعاملات المالية والمحاسبية > فواتير الشراء والتوريد",
                archiveLocation = "ملف فواتير الأجهزة - صندوق الإيصالات 2",
                issueDate = now - (150 * day),
                expiryDate = now + (580 * day),
                createdDate = now - (150 * day),
                fileName = "electronics_invoice_stamped.pdf",
                fileType = "فاتورة ضريبية",
                mimeType = "application/pdf",
                fileSize = 980000L,
                importance = DocumentImportance.NORMAL.titleAr,
                tags = "فاتورة, أجهزة, شاشة, إلكترونيات, ضمان",
                notes = "فاتورة ضريبية رسمية تشمل الرقم الضريبي للشركة وتفاصيل الصيانة وضمان المورد لمدة سنتين.",
                isArchived = false,
                isFavorite = false
            ),
            DocumentEntity(
                id = 6,
                title = "رخصة سير المركبة وبوليصة التأمين الشامل",
                docNumber = "VEH-INS-44219",
                categoryId = 11,
                categoryName = "رخص القيادة وسير المركبات",
                categoryPath = "الوثائق والهويات الشخصية > رخص القيادة وسير المركبات",
                archiveLocation = "درج السيارة الأمامي / الخزنة ب - الرف 1",
                issueDate = now - (280 * day),
                expiryDate = now + (85 * day),
                createdDate = now - (280 * day),
                fileName = "car_insurance_registration.pdf",
                fileType = "رخصة وتأمين",
                mimeType = "application/pdf",
                fileSize = 1650000L,
                importance = DocumentImportance.IMPORTANT.titleAr,
                tags = "سيارة, رخصة سير, استمارة, تأمين شامل, مرور",
                notes = "وثيقة التأمين الشامل متضمنة تغطية الحوادث، الزجاج الأمامي، والمساعدة على الطريق 24/7.",
                isArchived = false,
                isFavorite = false
            ),
            DocumentEntity(
                id = 7,
                title = "شهادة ضمان مكيفات الهواء المركزية (5 سنوات)",
                docNumber = "WAR-AC-5YR-991",
                categoryId = 21,
                categoryName = "ضمانات الأجهزة والمعدات",
                categoryPath = "الضمانات والكفالات الفنية > ضمانات الأجهزة والمعدات",
                archiveLocation = "خزنة أ - درج الضمانات والصيانة",
                issueDate = now - (500 * day),
                expiryDate = now + (1325 * day),
                createdDate = now - (500 * day),
                fileName = "ac_warranty_card_5years.jpg",
                fileType = "شهادة ضمان",
                mimeType = "image/jpeg",
                fileSize = 2100000L,
                importance = DocumentImportance.NORMAL.titleAr,
                tags = "ضمان, تكييف, صيانة دورية, كفالة 5 سنوات, كمبروسر",
                notes = "تشمل شهادة الضمان الصيانة الدورية المعتمدة مرتين كل عام مع قطع الغيار الأصلية مجاناً.",
                isArchived = false,
                isFavorite = false
            ),
            DocumentEntity(
                id = 8,
                title = "عقد تقديم خدمات الاستشارات التقنية والتطوير",
                docNumber = "CNT-TECH-2024-03",
                categoryId = 3,
                categoryName = "عقود العمل والخدمات",
                categoryPath = "الشؤون القانونية والعقود > عقود العمل والخدمات",
                archiveLocation = "خزنة ب - ملف عقود الأعمال والشراكات",
                issueDate = now - (60 * day),
                expiryDate = now + (305 * day),
                createdDate = now - (60 * day),
                fileName = "consultancy_agreement_signed.pdf",
                fileType = "عقد خدمات",
                mimeType = "application/pdf",
                fileSize = 4200000L,
                importance = DocumentImportance.CRITICAL.titleAr,
                tags = "عقد, استشارات, تقنية, برمجة, NDA, اتفاقية سرية",
                notes = "عقد تقديم خدمات استشارية متضمن بند الحفاظ على سرية المعلومات وشروط الدفعات المرحلية.",
                isArchived = false,
                isFavorite = true
            ),
            DocumentEntity(
                id = 9,
                title = "عقد صيانة المصعد الكهربائي لعام 2023 (منتهي)",
                docNumber = "CNT-2023-ELEV-OLD",
                categoryId = 2,
                categoryName = "عقود الإيجار والعقارات",
                categoryPath = "الشؤون القانونية والعقود > عقود الإيجار والعقارات",
                archiveLocation = "الأرشيف التاريخي - الصندوق الكرتوني رقم 4",
                issueDate = now - (750 * day),
                expiryDate = now - (385 * day),
                createdDate = now - (750 * day),
                fileName = "old_elevator_contract_2023.pdf",
                fileType = "عقد مؤرشف",
                mimeType = "application/pdf",
                fileSize = 1100000L,
                importance = DocumentImportance.NORMAL.titleAr,
                tags = "أرشيف, عقد قديم, صيانة 2023, منتهي",
                notes = "عقد منتهي تمت أرشفته بعد استبداله بالعقد المحدث مع الشركة الجديدة.",
                isArchived = true,
                isFavorite = false
            )
        )
    }
}
