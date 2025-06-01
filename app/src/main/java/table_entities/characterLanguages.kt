package table_entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "character_languages",
    foreignKeys = [
        ForeignKey(
            entity = Character::class,
            parentColumns = ["characterId"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Language::class,
            parentColumns = ["languageId"],
            childColumns = ["languageId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["characterId", "languageId"], unique = true)
    ]
)
data class CharacterLanguage(
    @PrimaryKey(autoGenerate = true)
    val characterLanguageId: Int = 0,
    val characterId: Int,
    val languageId: Int
)