package cz.czmendelu.studymate.database.repository

import cz.czmendelu.studymate.database.tables.Question
import kotlinx.coroutines.flow.Flow

interface IQuestionRepository {
    fun getQuestionsBySubject(subjectId: Long): Flow<List<Question>>
    suspend fun getQuestionById(id: Long): Question?
    suspend fun insertQuestion(question: Question): Long
    suspend fun updateQuestion(question: Question)
    suspend fun deleteQuestion(question: Question)
    suspend fun deleteQuestionsBySubject(subjectId: Long)
}