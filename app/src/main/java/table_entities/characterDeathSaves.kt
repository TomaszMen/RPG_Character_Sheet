package table_entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "character_death_saves",
    foreignKeys = [
        ForeignKey(
            entity = Character::class,
            parentColumns = ["characterId"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["characterId"], unique = true)
    ]
)
data class CharacterDeathSaves(
    @PrimaryKey(autoGenerate = true)
    val deathSaveId: Int = 0,
    val characterId: Int,
    val successes: Int = 0,
    val failures: Int = 0
)