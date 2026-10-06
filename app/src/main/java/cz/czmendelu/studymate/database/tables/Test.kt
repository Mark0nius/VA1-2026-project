package cz.czmendelu.studymate.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * test.
 *
 * nastavení testu, otázky jsou připojené přes tabulku TestQuestion.
 */
@Entity(tableName = "tests")
data class Test(
    val subjectId: Long,
    val name: String = "",
    val testType: String = "FLASHCARDS",
    val questionCount: Int = 0,
    val isTimed: Boolean = false,
    val timeLimitSeconds: Int? = null,

    @PrimaryKey(autoGenerate = true)
    val id: Long? = null
)
