package table_entities

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
    ],
    indices = [
        Index(value = ["characterId", "abilityScoreId"], unique = true)
    ]
)
data class CharacterSavingThrow(
    @PrimaryKey(autoGenerate = true)
    val characterSavingThrowId: Int = 0,
    val characterId: Int,
    val abilityScoreId: Int,
    val proficiency: Boolean = false
)