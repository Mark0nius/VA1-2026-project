package cz.czmendelu.studymate.database.repository

import cz.czmendelu.studymate.database.dao.TestDao
import cz.czmendelu.studymate.database.tables.Test
import cz.czmendelu.studymate.database.tables.TestQuestion
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TestRepositoryImpl @Inject constructor(
    private val testDao: TestDao
) : ITestRepository {

    override fun getTestsBySubject(subjectId: Long): Flow<List<Test>> {
        return testDao.getTestsBySubject(subjectId)
    }

    override suspend fun getTestById(id: Long): Test? {
        return testDao.getTestById(id)
    }

    override suspend fun insertTest(test: Test): Long {
        return testDao.insertTest(test)
    }

    override suspend fun updateTest(test: Test) {
        testDao.updateTest(test)
    }

    override suspend fun deleteTest(test: Test) {
        testDao.deleteTest(test)
    }

    override suspend fun insertTestQuestions(testQuestions: List<TestQuestion>) {
        testDao.insertTestQuestions(testQuestions)
    }

    override suspend fun getTestQuestions(testId: Long): List<TestQuestion> {
        return testDao.getTestQuestions(testId)
    }

    override suspend fun deleteTestQuestions(testId: Long) {
        testDao.deleteTestQuestions(testId)
    }
}