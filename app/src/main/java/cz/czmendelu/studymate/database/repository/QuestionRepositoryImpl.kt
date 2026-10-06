package cz.czmendelu.studymate.database.repository

import cz.czmendelu.studymate.database.dao.QuestionDao
import cz.czmendelu.studymate.database.tables.Question
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val questionDao: QuestionDao
) : IQuestionRepository {

    override fun getQuestionsBySubject(subjectId: Long): Flow<List<Question>> {
        return questionDao.getQuestionsBySubject(subjectId)
    }

    override suspend fun getQuestionById(id: Long): Question? {
        return questionDao.getQuestionById(id)
    }

    override suspend fun insertQuestion(question: Question): Long {
        return questionDao.insertQuestion(question)
    }

    override suspend fun updateQuestion(question: Question) {
        questionDao.updateQuestion(question)
    }

    override suspend fun deleteQuestion(question: Question) {
        questionDao.deleteQuestion(question)
    }

    override suspend fun deleteQuestionsBySubject(subjectId: Long) {
        questionDao.deleteQuestionsBySubject(subjectId)
    }
}