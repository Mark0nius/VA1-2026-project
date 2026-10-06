package cz.czmendelu.studymate.di

import android.content.Context
import androidx.room.Room
import cz.czmendelu.studymate.database.StudyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

/**
 * Hilt modul, který vytváří jednu sdílenou instanci Room databáze.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): StudyDatabase {

        return Room.databaseBuilder(
            context,
            StudyDatabase::class.java,
            // Název databázového souboru uloženého v interním úložišti aplikace.
            "training_database"
        ).build()
    }
}
