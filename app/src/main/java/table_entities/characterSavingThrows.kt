package table_entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "character_saving_throws",
    foreignKeys = [
        ForeignKey(
            entity = Character::class,
            parentColumns = ["characterId"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AbilityScore::class,
            parentColumns = ["abilityScoreId"],
            childColumns = ["abilityScoreId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CharacterSavingThrow(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "characterSavingThrowId")
    val characterSavingThrowId: Int = 0,

    @ColumnInfo(name = "characterId")
    val characterId: Int,

    @ColumnInfo(name = "abilityScoreId")
    val abilityScoreId: Int,

    @ColumnInfo(name = "proficiency")
    val proficiency: Int = 0
)