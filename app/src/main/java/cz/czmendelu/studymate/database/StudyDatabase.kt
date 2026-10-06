package cz.czmendelu.studymate.database

import androidx.room.Database
import androidx.room.RoomDatabase
import cz.czmendelu.studymate.database.dao.QuestionDao
import cz.czmendelu.studymate.database.dao.SubjectDao
import cz.czmendelu.studymate.database.dao.TestDao
import cz.czmendelu.studymate.database.dao.TestResultDao
import cz.czmendelu.studymate.database.tables.Question
import cz.czmendelu.studymate.database.tables.Subject
import cz.czmendelu.studymate.database.tables.Test
import cz.czmendelu.studymate.database.tables.TestAnswer
import cz.czmendelu.studymate.database.tables.TestQuestion
import cz.czmendelu.studymate.database.tables.TestResult

/**
 * databáze.
 */
@Database(
    entities = [
        Subject::class,
        Question::class,
        Test::class,
        TestQuestion::class,
        TestResult::class,
        TestAnswer::class
    ],
    version = 2,
    exportSchema = false
)
abstract class StudyDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao

    abstract fun questionDao(): QuestionDao

    abstract fun testDao(): TestDao

    abstract fun testResultDao(): TestResultDao
}
