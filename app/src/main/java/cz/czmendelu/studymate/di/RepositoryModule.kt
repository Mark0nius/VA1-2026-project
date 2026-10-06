package cz.czmendelu.studymate.di

import cz.czmendelu.studymate.database.dao.QuestionDao
import cz.czmendelu.studymate.database.dao.SubjectDao
import cz.czmendelu.studymate.database.dao.TestDao
import cz.czmendelu.studymate.database.dao.TestResultDao

import cz.czmendelu.studymate.database.repository.IQuestionRepository
import cz.czmendelu.studymate.database.repository.ISubjectRepository
import cz.czmendelu.studymate.database.repository.ITestRepository
import cz.czmendelu.studymate.database.repository.ITestResultRepository

import cz.czmendelu.studymate.database.repository.QuestionRepositoryImpl
import cz.czmendelu.studymate.database.repository.SubjectRepositoryImpl
import cz.czmendelu.studymate.database.repository.TestRepositoryImpl
import cz.czmendelu.studymate.database.repository.TestResultRepositoryImpl

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt modul, který propojuje repository rozhraní s jejich implementacemi.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideSubjectRepository(
        dao: SubjectDao
    ): ISubjectRepository {
        return SubjectRepositoryImpl(dao)
    }

    @Provides
    fun provideQuestionRepository(
        dao: QuestionDao
    ): IQuestionRepository {

        return QuestionRepositoryImpl(dao)
    }

    @Provides
    fun provideTestRepository(
        dao: TestDao
    ): ITestRepository {

        return TestRepositoryImpl(dao)
    }

    @Provides
    fun provideTestResultRepository(
        dao: TestResultDao
    ): ITestResultRepository {

        return TestResultRepositoryImpl(dao)
    }
}
