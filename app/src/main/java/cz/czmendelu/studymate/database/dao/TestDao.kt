package cz.czmendelu.studymate.database.dao

import androidx.room.*
import cz.czmendelu.studymate.database.tables.Test
import cz.czmendelu.studymate.database.tables.TestQuestion
import kotlinx.coroutines.flow.Flow


@Dao
interface TestDao {

    @Query("SELECT * FROM tests WHERE subjectId = :subjectId ORDER BY id DESC")
    fun getTestsBySubject(subjectId: Long): Flow<List<Test>>

    @Query("SELECT * FROM tests WHERE id = :id")
    suspend fun getTestById(id: Long): Test?

    @Insert
    suspend fun insertTest(test: Test): Long

    @Update
    suspend fun updateTest(test: Test)

    @Delete
    suspend fun deleteTest(test: Test)

    @Insert
    suspend fun insertTestQuestions(testQuestions: List<TestQuestion>)

    @Query("SELECT * FROM test_questions WHERE testId = :testId ORDER BY position ASC")
    suspend fun getTestQuestions(testId: Long): List<TestQuestion>

    @Query("DELETE FROM test_questions WHERE testId = :testId")
    suspend fun deleteTestQuestions(testId: Long)
}
