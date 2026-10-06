package cz.czmendelu.studymate.di

import cz.czmendelu.studymate.database.StudyDatabase
import cz.czmendelu.studymate.database.dao.QuestionDao
import cz.czmendelu.studymate.database.dao.SubjectDao
import cz.czmendelu.studymate.database.dao.TestDao
import cz.czmendelu.studymate.database.dao.TestResultDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt modul pro poskytování DAO objektů z hlavní databáze.
 */
@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    fun provideSubjectDao(database: StudyDatabase
    ): SubjectDao {
        return database.subjectDao()
    }

    @Provides
    fun provideQuestionDao(database: StudyDatabase
    ): QuestionDao {
        return database.questionDao()
    }

    @Provides
    fun provideTestDao(database: StudyDatabase
    ): TestDao {
        return database.testDao()
    }

    @Provides
    fun provideTestResultDao(database: StudyDatabase
    ): TestResultDao {
        return database.testResultDao()
    }
}
