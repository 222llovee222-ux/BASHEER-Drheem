package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    @Query("SELECT * FROM documents ORDER BY createdDate DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE isArchived = 0 ORDER BY createdDate DESC")
    fun getActiveDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE isArchived = 1 ORDER BY createdDate DESC")
    fun getArchivedDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE categoryId = :categoryId AND isArchived = 0 ORDER BY createdDate DESC")
    fun getDocumentsByCategoryId(categoryId: Long): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE categoryId IN (:categoryIds) AND isArchived = 0 ORDER BY createdDate DESC")
    fun getDocumentsByCategoryIds(categoryIds: List<Long>): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE isFavorite = 1 AND isArchived = 0 ORDER BY createdDate DESC")
    fun getFavoriteDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :id")
    fun getDocumentById(id: Long): Flow<DocumentEntity?>

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getDocumentByIdSync(id: Long): DocumentEntity?

    @Query("""
        SELECT * FROM documents 
        WHERE (title LIKE '%' || :query || '%' 
           OR docNumber LIKE '%' || :query || '%' 
           OR tags LIKE '%' || :query || '%' 
           OR notes LIKE '%' || :query || '%' 
           OR archiveLocation LIKE '%' || :query || '%'
           OR categoryName LIKE '%' || :query || '%'
           OR categoryPath LIKE '%' || :query || '%'
           OR fileName LIKE '%' || :query || '%')
           AND (:categoryId = 0 OR categoryId = :categoryId)
           AND (:isArchived = -1 OR isArchived = :isArchived)
        ORDER BY createdDate DESC
    """)
    fun searchDocuments(
        query: String,
        categoryId: Long = 0,
        isArchived: Int = 0 // 0=active, 1=archived, -1=all
    ): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE expiryDate IS NOT NULL AND expiryDate <= :thresholdDate AND isArchived = 0 ORDER BY expiryDate ASC")
    fun getExpiringDocuments(thresholdDate: Long): Flow<List<DocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(documents: List<DocumentEntity>)

    @Update
    suspend fun updateDocument(document: DocumentEntity)

    @Delete
    suspend fun deleteDocument(document: DocumentEntity)

    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteDocumentById(id: Long)

    @Query("UPDATE documents SET isArchived = :isArchived WHERE id = :id")
    suspend fun setArchivedStatus(id: Long, isArchived: Boolean)

    @Query("UPDATE documents SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavoriteStatus(id: Long, isFavorite: Boolean)

    @Query("UPDATE documents SET categoryId = :newCategoryId, categoryName = :newCategoryName, categoryPath = :newCategoryPath WHERE id = :id")
    suspend fun moveDocumentToCategory(id: Long, newCategoryId: Long, newCategoryName: String, newCategoryPath: String)

    @Query("SELECT COUNT(*) FROM documents")
    suspend fun getDocumentCount(): Int

    @Query("SELECT COUNT(*) FROM documents WHERE categoryId = :categoryId")
    suspend fun getDocumentCountForCategory(categoryId: Long): Int

    @Query("SELECT SUM(fileSize) FROM documents")
    fun getTotalStorageUsed(): Flow<Long?>
}
