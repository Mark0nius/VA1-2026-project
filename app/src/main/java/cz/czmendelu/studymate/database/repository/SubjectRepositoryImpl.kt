package cz.czmendelu.studymate.database.repository

import cz.czmendelu.studymate.database.dao.SubjectDao
import cz.czmendelu.studymate.database.tables.Subject
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao
) : ISubjectRepository {

    override fun getAllSubjects(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects()
    }

    override suspend fun getSubjectById(id: Long): Subject? {
        return subjectDao.getSubjectById(id)
    }

    override suspend fun insertSubject(subject: Subject): Long {
        return subjectDao.insertSubject(subject)
    }

    override suspend fun updateSubject(subject: Subject) {
        subjectDao.updateSubject(subject)
    }

    override suspend fun deleteSubject(subject: Subject) {
        subjectDao.deleteSubject(subject)
    }
}