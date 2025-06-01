package table_entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "character_spells",
    foreignKeys = [
        ForeignKey(
            entity = Character::class,
            parentColumns = ["characterId"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Spell::class,
            parentColumns = ["spellId"],
            childColumns = ["spellId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["characterId", "spellId"], unique = true)
    ]
)
data class CharacterSpell(
    @PrimaryKey(autoGenerate = true)
    val characterSpellId: Int = 0,
    val characterId: Int,
    val spellId: Int,
    val prepared: Boolean = false
)