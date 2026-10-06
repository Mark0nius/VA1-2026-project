package cz.czmendelu.studymate.database.repository

import cz.czmendelu.studymate.database.tables.TestAnswer
import cz.czmendelu.studymate.database.tables.TestResult
import kotlinx.coroutines.flow.Flow

interface ITestResultRepository {

    fun getResultsBySubject(subjectId: Long): Flow<List<TestResult>>

    fun getResultsByTest(testId: Long): Flow<List<TestResult>>

    suspend fun getTestResultById(resultId: Long): TestResult?

    suspend fun insertTestResult(testResult: TestResult): Long

    suspend fun insertTestAnswers(testAnswers: List<TestAnswer>)

    suspend fun getAnswersByResult(resultId: Long): List<TestAnswer>

    suspend fun deleteResultsBySubject(subjectId: Long)
}