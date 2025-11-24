package table_entities

import androidx.room.ColumnInfo
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
    ]
)
data class CharacterLanguage(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "characterLanguageId")
    val characterLanguageId: Int = 0,

    @ColumnInfo(name = "characterId")
    val characterId: Int,

    @ColumnInfo(name = "languageId")
    val languageId: Int
)