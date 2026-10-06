package cz.czmendelu.studymate.localization

enum class AppLanguage {
    ENGLISH,
    CZECH
}

fun String.toAppLanguage(): AppLanguage {
    return when (this) {
        "Čeština" -> AppLanguage.CZECH
        "Czech" -> AppLanguage.CZECH
        "cs" -> AppLanguage.CZECH
        else -> AppLanguage.ENGLISH
    }
}