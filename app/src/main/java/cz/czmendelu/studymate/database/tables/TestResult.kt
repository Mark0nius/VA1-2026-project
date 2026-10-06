package cz.czmendelu.studymate.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * výsledek testu.
 *
 * Detailní odpovědi uložené v tabulce test_answers.
 */
@Entity(tableName = "test_results")
data class TestResult(
    val testId: Long,
    val subjectId: Long,
    val scorePercent: Double,
    val correctCount: Int,
    val wrongCount: Int,
    val totalQuestions: Int,
    val createdAt: Long = System.currentTimeMillis(),

    @PrimaryKey(autoGenerate = true)
    val id: Long? = null
)
