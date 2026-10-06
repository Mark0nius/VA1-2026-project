package cz.czmendelu.studymate.database.repository

import cz.czmendelu.studymate.database.dao.TestResultDao
import cz.czmendelu.studymate.database.tables.TestAnswer
import cz.czmendelu.studymate.database.tables.TestResult
import kotlinx.coroutines.flow.Flow

class TestResultRepositoryImpl(
    private val dao: TestResultDao
) : ITestResultRepository {

    override fun getResultsBySubject(subjectId: Long): Flow<List<TestResult>> {
        return dao.getResultsBySubject(subjectId)
    }

    override fun getResultsByTest(testId: Long): Flow<List<TestResult>> {
        return dao.getResultsByTest(testId)
    }

    override suspend fun getTestResultById(resultId: Long): TestResult? {
        return dao.getTestResultById(resultId)
    }

    override suspend fun insertTestResult(testResult: TestResult): Long {
        return dao.insertTestResult(testResult)
    }

    override suspend fun insertTestAnswers(testAnswers: List<TestAnswer>) {
        dao.insertTestAnswers(testAnswers)
    }

    override suspend fun getAnswersByResult(resultId: Long): List<TestAnswer> {
        return dao.getAnswersByResult(resultId)
    }

    override suspend fun deleteResultsBySubject(subjectId: Long) {
        dao.deleteResultsBySubject(subjectId)
    }
}