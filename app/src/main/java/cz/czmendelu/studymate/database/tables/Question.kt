package cz.czmendelu.studymate.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * otázky
 */
@Entity(tableName = "questions")
data class Question(
    val subjectId: Long,
    val questionText: String,
    val answerText: String,
    val questionType: String,
    // Cesta k obrázku je vyplněná jen u obrázkových otázek.
    val imagePath: String? = null,
    // Boolean odpověď se používá jen pro otázky typu YES_NO.
    val correctAnswer: Boolean? = null,
    val createdAt: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null
)
