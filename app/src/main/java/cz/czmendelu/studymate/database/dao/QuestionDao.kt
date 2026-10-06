package cz.czmendelu.studymate.database.dao

import androidx.room.*
import cz.czmendelu.studymate.database.tables.Question
import kotlinx.coroutines.flow.Flow


@Dao
interface QuestionDao {

    @Query("SELECT * FROM questions WHERE subjectId = :subjectId ORDER BY createdAt DESC")
    fun getQuestionsBySubject(subjectId: Long): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getQuestionById(id: Long): Question?

    @Insert
    suspend fun insertQuestion(question: Question): Long

    @Update
    suspend fun updateQuestion(question: Question)

    @Delete
    suspend fun deleteQuestion(question: Question)

    @Query("DELETE FROM questions WHERE subjectId = :subjectId")
    suspend fun deleteQuestionsBySubject(subjectId: Long)
}
