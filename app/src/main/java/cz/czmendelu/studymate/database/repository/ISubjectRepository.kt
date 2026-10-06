package cz.czmendelu.studymate.database.repository

import cz.czmendelu.studymate.database.tables.Subject
import kotlinx.coroutines.flow.Flow

interface ISubjectRepository {
    fun getAllSubjects(): Flow<List<Subject>>
    suspend fun getSubjectById(id: Long): Subject?
    suspend fun insertSubject(subject: Subject): Long
    suspend fun updateSubject(subject: Subject)
    suspend fun deleteSubject(subject: Subject)
}