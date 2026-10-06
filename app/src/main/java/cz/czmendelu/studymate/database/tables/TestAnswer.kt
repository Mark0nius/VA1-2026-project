package cz.czmendelu.studymate.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Uložená odpověď na jednu otázku
 */
@Entity(tableName = "test_answers")
data class TestAnswer(
    val resultId: Long,
    val questionId: Long,
    val userAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean,

    @PrimaryKey(autoGenerate = true)
    val id: Long? = null
)
