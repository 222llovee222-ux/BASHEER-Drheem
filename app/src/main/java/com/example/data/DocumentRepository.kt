package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class DocumentRepository(
    private val categoryDao: CategoryDao,
    private val documentDao: DocumentDao
) {

    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories().flowOn(Dispatchers.IO)
    val rootCategories: Flow<List<CategoryEntity>> = categoryDao.getRootCategories().flowOn(Dispatchers.IO)

    val allDocuments: Flow<List<DocumentEntity>> = documentDao.getAllDocuments().flowOn(Dispatchers.IO)
    val activeDocuments: Flow<List<DocumentEntity>> = documentDao.getActiveDocuments().flowOn(Dispatchers.IO)
    val archivedDocuments: Flow<List<DocumentEntity>> = documentDao.getArchivedDocuments().flowOn(Dispatchers.IO)
    val favoriteDocuments: Flow<List<DocumentEntity>> = documentDao.getFavoriteDocuments().flowOn(Dispatchers.IO)
    val totalStorageUsed: Flow<Long?> = documentDao.getTotalStorageUsed().flowOn(Dispatchers.IO)

    /**
     * Builds a full hierarchical tree of categories combined with document counts for fast reactive UI.
     */
    val categoryTree: Flow<List<CategoryNode>> = combine(
        allCategories,
        activeDocuments
    ) { categories, docs ->
        buildTree(categories, docs, null, 0, "")
    }.flowOn(Dispatchers.Default)

    private fun buildTree(
        allCategories: List<CategoryEntity>,
        allDocs: List<DocumentEntity>,
        parentId: Long?,
        level: Int,
        parentPath: String
    ): List<CategoryNode> {
        val currentLevelCategories = allCategories.filter { it.parentId == parentId }
            .sortedBy { it.orderIndex }

        return currentLevelCategories.map { category ->
            val currentPath = if (parentPath.isEmpty()) category.name else "$parentPath > ${category.name}"
            val directDocs = allDocs.count { it.categoryId == category.id }
            val children = buildTree(allCategories, allDocs, category.id, level + 1, currentPath)
            val totalDocs = directDocs + children.sumOf { it.totalDocCount }

            CategoryNode(
                category = category,
                level = level,
                children = children,
                directDocCount = directDocs,
                totalDocCount = totalDocs,
                path = currentPath
            )
        }
    }

    /**
     * Returns list of category IDs including the given category and all its recursive descendants.
     */
    suspend fun getCategoryAndDescendantIds(categoryId: Long): List<Long> = withContext(Dispatchers.IO) {
        val allCats = categoryDao.getAllCategoriesSync()
        val result = mutableListOf<Long>()

        fun collect(currentId: Long) {
            result.add(currentId)
            val children = allCats.filter { it.parentId == currentId }
            children.forEach { collect(it.id) }
        }

        collect(categoryId)
        result
    }

    /**
     * Computes the hierarchical path string (e.g. "الشؤون القانونية > عقود الإيجار") for a given category.
     */
    suspend fun getCategoryPath(categoryId: Long): String = withContext(Dispatchers.IO) {
        val allCats = categoryDao.getAllCategoriesSync()
        val pathSegments = mutableListOf<String>()
        var current: CategoryEntity? = allCats.find { it.id == categoryId }

        while (current != null) {
            pathSegments.add(0, current.name)
            current = if (current.parentId != null) allCats.find { it.id == current!!.parentId } else null
        }

        if (pathSegments.isEmpty()) "عام" else pathSegments.joinToString(" > ")
    }

    fun getDocumentsByCategoryId(categoryId: Long): Flow<List<DocumentEntity>> {
        return documentDao.getDocumentsByCategoryId(categoryId).flowOn(Dispatchers.IO)
    }

    fun getDocumentsByCategoryIds(categoryIds: List<Long>): Flow<List<DocumentEntity>> {
        return documentDao.getDocumentsByCategoryIds(categoryIds).flowOn(Dispatchers.IO)
    }

    fun getDocumentById(id: Long): Flow<DocumentEntity?> {
        return documentDao.getDocumentById(id).flowOn(Dispatchers.IO)
    }

    fun searchDocuments(query: String, categoryId: Long = 0, isArchived: Int = 0): Flow<List<DocumentEntity>> {
        return documentDao.searchDocuments(query, categoryId, isArchived).flowOn(Dispatchers.IO)
    }

    fun getExpiringDocuments(thresholdTimestamp: Long): Flow<List<DocumentEntity>> {
        return documentDao.getExpiringDocuments(thresholdTimestamp).flowOn(Dispatchers.IO)
    }

    suspend fun insertCategory(category: CategoryEntity): Long = withContext(Dispatchers.IO) {
        categoryDao.insertCategory(category)
    }

    suspend fun updateCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        // Also remove subcategories
        categoryDao.deleteSubcategories(category.id)
        categoryDao.deleteCategory(category)
    }

    suspend fun insertDocument(document: DocumentEntity): Long = withContext(Dispatchers.IO) {
        // Ensure path is populated
        val fullPath = if (document.categoryPath.isBlank() || document.categoryPath == "عام") {
            getCategoryPath(document.categoryId)
        } else {
            document.categoryPath
        }
        documentDao.insertDocument(document.copy(categoryPath = fullPath))
    }

    suspend fun updateDocument(document: DocumentEntity) = withContext(Dispatchers.IO) {
        val fullPath = getCategoryPath(document.categoryId)
        documentDao.updateDocument(document.copy(categoryPath = fullPath))
    }

    suspend fun deleteDocument(document: DocumentEntity) = withContext(Dispatchers.IO) {
        documentDao.deleteDocument(document)
    }

    suspend fun deleteDocumentById(id: Long) = withContext(Dispatchers.IO) {
        documentDao.deleteDocumentById(id)
    }

    suspend fun setArchivedStatus(id: Long, isArchived: Boolean) = withContext(Dispatchers.IO) {
        documentDao.setArchivedStatus(id, isArchived)
    }

    suspend fun setFavoriteStatus(id: Long, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        documentDao.setFavoriteStatus(id, isFavorite)
    }

    suspend fun moveDocumentToCategory(docId: Long, newCategoryId: Long, newCategoryName: String) = withContext(Dispatchers.IO) {
        val newPath = getCategoryPath(newCategoryId)
        documentDao.moveDocumentToCategory(docId, newCategoryId, newCategoryName, newPath)
    }

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        if (categoryDao.getCategoryCount() == 0) {
            categoryDao.insertAll(SampleData.getInitialCategories())
        }
        if (documentDao.getDocumentCount() == 0) {
            documentDao.insertAll(SampleData.getInitialDocuments())
        }
    }
}
