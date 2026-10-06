package cz.czmendelu.studymate.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cz.czmendelu.studymate.database.tables.TestAnswer
import cz.czmendelu.studymate.database.tables.TestResult
import kotlinx.coroutines.flow.Flow

@Dao
interface TestResultDao {

    @Query("SELECT * FROM test_results WHERE subjectId = :subjectId ORDER BY createdAt DESC")
    fun getResultsBySubject(subjectId: Long): Flow<List<TestResult>>

    @Query("SELECT * FROM test_results WHERE testId = :testId ORDER BY createdAt DESC")
    fun getResultsByTest(testId: Long): Flow<List<TestResult>>

    @Query("SELECT * FROM test_results WHERE id = :resultId")
    suspend fun getTestResultById(resultId: Long): TestResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestResult(testResult: TestResult): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestAnswers(testAnswers: List<TestAnswer>)

    @Query("SELECT * FROM test_answers WHERE resultId = :resultId")
    suspend fun getAnswersByResult(resultId: Long): List<TestAnswer>

    @Query("DELETE FROM test_results WHERE subjectId = :subjectId")
    suspend fun deleteResultsBySubject(subjectId: Long)
}
