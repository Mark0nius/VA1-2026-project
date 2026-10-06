package cz.czmendelu.studymate.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Databázová entita jednoho studijního předmětu.
 */
@Entity(tableName = "subjects")
data class Subject(
    val name: String = "",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),

    @PrimaryKey(autoGenerate = true)
    val id: Long? = null
)
