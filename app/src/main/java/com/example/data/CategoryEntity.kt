package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a hierarchical category/folder.
 * Allows recursive parent-child folder structures (e.g., Parent -> Subcategory -> Deep Subfolder).
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val parentId: Long? = null, // null means top-level root category
    val name: String,
    val description: String = "",
    val iconName: String = "folder",
    val colorHex: Long = 0xFF0D9488,
    val orderIndex: Int = 0,
    val createdDate: Long = System.currentTimeMillis()
)

/**
 * UI representation of a category node with computed path and child counts.
 */
data class CategoryNode(
    val category: CategoryEntity,
    val level: Int = 0,
    val children: List<CategoryNode> = emptyList(),
    val directDocCount: Int = 0,
    val totalDocCount: Int = 0,
    val path: String = ""
)
