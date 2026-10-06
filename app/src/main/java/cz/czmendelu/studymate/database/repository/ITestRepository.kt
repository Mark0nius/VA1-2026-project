package cz.czmendelu.studymate.database.repository

import cz.czmendelu.studymate.database.tables.Test
import cz.czmendelu.studymate.database.tables.TestQuestion
import kotlinx.coroutines.flow.Flow

interface ITestRepository {
    fun getTestsBySubject(subjectId: Long): Flow<List<Test>>
    suspend fun getTestById(id: Long): Test?
    suspend fun insertTest(test: Test): Long
    suspend fun updateTest(test: Test)
    suspend fun deleteTest(test: Test)

    suspend fun insertTestQuestions(testQuestions: List<TestQuestion>)
    suspend fun getTestQuestions(testId: Long): List<TestQuestion>
    suspend fun deleteTestQuestions(testId: Long)
}