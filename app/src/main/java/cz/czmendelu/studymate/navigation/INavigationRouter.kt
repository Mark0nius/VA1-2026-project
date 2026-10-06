package cz.czmendelu.studymate.navigation

interface INavigationRouter {

    fun navigateToSubjects()

    fun navigateToSettings()

    fun navigateToSubjectDetail(subjectId: Long)

    fun navigateToSubjectDetailFromResult(subjectId: Long)

    fun navigateToAddQuestion(subjectId: Long)

    fun navigateToEditQuestion(subjectId: Long, questionId: Long)

    fun navigateToCreateTest(subjectId: Long)

    fun navigateToTest(testId: Long)

    fun navigateToTestResult(resultId: Long)

    fun returnBack()
}
