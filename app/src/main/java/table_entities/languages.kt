package table_entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "languages")
data class Language(
    @PrimaryKey(autoGenerate = true)
    val languageId: Int = 0,
    val languageName: String,
    val script: String? = null,
    val description: String? = null
)