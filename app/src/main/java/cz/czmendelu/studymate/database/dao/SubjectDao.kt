package cz.czmendelu.studymate.database.dao

import androidx.room.*
import cz.czmendelu.studymate.database.tables.Subject
import kotlinx.coroutines.flow.Flow


@Dao
interface SubjectDao {

    @Query("SELECT * FROM subjects ORDER BY createdAt DESC")
    fun getAllSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: Long): Subject?

    @Insert
    suspend fun insertSubject(subject: Subject): Long

    @Update
    suspend fun updateSubject(subject: Subject)

    @Delete
    suspend fun deleteSubject(subject: Subject)
}
