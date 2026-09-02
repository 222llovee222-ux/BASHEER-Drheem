package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CategoryEntity
import com.example.data.CategoryNode
import com.example.data.DocumentEntity
import com.example.data.DocumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

enum class ArchiveTab(val titleAr: String) {
    BROWSER("المستندات"),
    TREE_EXPLORER("شجرة التصنيفات"),
    EXPIRING_ALERTS("تنبيهات الصلاحية"),
    ARCHIVED("الأرشيف المؤرشف")
}

class MainArchiveViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DocumentRepository

    val allCategories: StateFlow<List<CategoryEntity>>
    val categoryTree: StateFlow<List<CategoryNode>>
    val totalStorageUsed: StateFlow<Long?>

    private val _selectedCategory = MutableStateFlow<CategoryEntity?>(null)
    val selectedCategory: StateFlow<CategoryEntity?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _importanceFilter = MutableStateFlow<String?>(null)
    val importanceFilter: StateFlow<String?> = _importanceFilter.asStateFlow()

    private val _activeTab = MutableStateFlow(ArchiveTab.BROWSER)
    val activeTab: StateFlow<ArchiveTab> = _activeTab.asStateFlow()

    // Dialog state holders
    private val _viewingDocument = MutableStateFlow<DocumentEntity?>(null)
    val viewingDocument: StateFlow<DocumentEntity?> = _viewingDocument.asStateFlow()

    private val _editingDocument = MutableStateFlow<DocumentEntity?>(null)
    val editingDocument: StateFlow<DocumentEntity?> = _editingDocument.asStateFlow()

    private val _isAddingDocument = MutableStateFlow(false)
    val isAddingDocument: StateFlow<Boolean> = _isAddingDocument.asStateFlow()

    private val _editingCategory = MutableStateFlow<CategoryEntity?>(null)
    val editingCategory: StateFlow<CategoryEntity?> = _editingCategory.asStateFlow()

    private val _isAddingCategory = MutableStateFlow(false)
    val isAddingCategory: StateFlow<Boolean> = _isAddingCategory.asStateFlow()

    private val _parentForNewCategory = MutableStateFlow<CategoryEntity?>(null)
    val parentForNewCategory: StateFlow<CategoryEntity?> = _parentForNewCategory.asStateFlow()

    private val _movingDocument = MutableStateFlow<DocumentEntity?>(null)
    val movingDocument: StateFlow<DocumentEntity?> = _movingDocument.asStateFlow()

    val filteredDocuments: StateFlow<List<DocumentEntity>>
    val expiringDocuments: StateFlow<List<DocumentEntity>>
    val archivedDocuments: StateFlow<List<DocumentEntity>>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = DocumentRepository(db.categoryDao(), db.documentDao())

        // Seed sample data if empty
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }

        allCategories = repository.allCategories.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        categoryTree = repository.categoryTree.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        totalStorageUsed = repository.totalStorageUsed.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0L
        )

        // 30 days threshold for expiring alerts
        val thirtyDaysFromNow = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30)
        expiringDocuments = repository.getExpiringDocuments(thirtyDaysFromNow).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        archivedDocuments = repository.archivedDocuments.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Combine filter flows to produce dynamic filtered list
        filteredDocuments = combine(
            repository.activeDocuments,
            _selectedCategory,
            _searchQuery,
            _importanceFilter
        ) { docs, selectedCat, query, importance ->
            var result = docs

            // Filter by category (and all its sub-branch descendants if selected)
            if (selectedCat != null) {
                val allowedCatIds = repository.getCategoryAndDescendantIds(selectedCat.id)
                result = result.filter { it.categoryId in allowedCatIds }
            }

            // Filter by search query
            if (query.isNotBlank()) {
                val q = query.trim().lowercase()
                result = result.filter { doc ->
                    doc.title.lowercase().contains(q) ||
                    doc.docNumber.lowercase().contains(q) ||
                    doc.tags.lowercase().contains(q) ||
                    doc.notes.lowercase().contains(q) ||
                    doc.archiveLocation.lowercase().contains(q) ||
                    doc.categoryName.lowercase().contains(q) ||
                    doc.categoryPath.lowercase().contains(q) ||
                    (doc.fileName?.lowercase()?.contains(q) == true)
                }
            }

            // Filter by importance
            if (importance != null) {
                result = result.filter { it.importance == importance }
            }

            result
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun setSelectedCategory(category: CategoryEntity?) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setImportanceFilter(importance: String?) {
        _importanceFilter.value = if (_importanceFilter.value == importance) null else importance
    }

    fun setActiveTab(tab: ArchiveTab) {
        _activeTab.value = tab
    }

    fun openDocumentViewer(document: DocumentEntity) {
        _viewingDocument.value = document
    }

    fun closeDocumentViewer() {
        _viewingDocument.value = null
    }

    fun openAddDocument() {
        _editingDocument.value = null
        _isAddingDocument.value = true
    }

    fun openEditDocument(document: DocumentEntity) {
        _editingDocument.value = document
        _isAddingDocument.value = true
    }

    fun closeAddEditDocument() {
        _editingDocument.value = null
        _isAddingDocument.value = false
    }

    fun openAddCategory(parent: CategoryEntity? = null) {
        _editingCategory.value = null
        _parentForNewCategory.value = parent
        _isAddingCategory.value = true
    }

    fun openEditCategory(category: CategoryEntity) {
        _editingCategory.value = category
        _parentForNewCategory.value = null
        _isAddingCategory.value = true
    }

    fun closeAddEditCategory() {
        _editingCategory.value = null
        _parentForNewCategory.value = null
        _isAddingCategory.value = false
    }

    fun openMoveDocumentDialog(document: DocumentEntity) {
        _movingDocument.value = document
    }

    fun closeMoveDocumentDialog() {
        _movingDocument.value = null
    }

    fun saveDocument(document: DocumentEntity) {
        viewModelScope.launch {
            if (document.id == 0L) {
                repository.insertDocument(document)
            } else {
                repository.updateDocument(document)
            }
        }
    }

    fun deleteDocument(document: DocumentEntity) {
        viewModelScope.launch {
            repository.deleteDocument(document)
            if (_viewingDocument.value?.id == document.id) {
                _viewingDocument.value = null
            }
        }
    }

    fun toggleFavorite(document: DocumentEntity) {
        viewModelScope.launch {
            val newStatus = !document.isFavorite
            repository.setFavoriteStatus(document.id, newStatus)
            if (_viewingDocument.value?.id == document.id) {
                _viewingDocument.value = _viewingDocument.value?.copy(isFavorite = newStatus)
            }
        }
    }

    fun toggleArchive(document: DocumentEntity) {
        viewModelScope.launch {
            val newStatus = !document.isArchived
            repository.setArchivedStatus(document.id, newStatus)
            if (_viewingDocument.value?.id == document.id) {
                _viewingDocument.value = _viewingDocument.value?.copy(isArchived = newStatus)
            }
        }
    }

    fun moveDocumentToCategory(docId: Long, newCategoryId: Long, newCategoryName: String) {
        viewModelScope.launch {
            repository.moveDocumentToCategory(docId, newCategoryId, newCategoryName)
            if (_viewingDocument.value?.id == docId) {
                val newPath = repository.getCategoryPath(newCategoryId)
                _viewingDocument.value = _viewingDocument.value?.copy(
                    categoryId = newCategoryId,
                    categoryName = newCategoryName,
                    categoryPath = newPath
                )
            }
        }
    }

    fun saveCategory(category: CategoryEntity) {
        viewModelScope.launch {
            if (category.id == 0L) {
                repository.insertCategory(category)
            } else {
                repository.updateCategory(category)
            }
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            if (_selectedCategory.value?.id == category.id) {
                _selectedCategory.value = null
            }
        }
    }
}
