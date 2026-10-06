package cz.czmendelu.studymate.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *  entita mezi testem otázkou.
 *
 * position = pořadí náhodně vybraných otázek.
 */
@Entity(tableName = "test_questions")
data class TestQuestion(
    val testId: Long,
    val questionId: Long,
    val position: Int = 0,

    @PrimaryKey(autoGenerate = true)
    val id: Long? = null
)
