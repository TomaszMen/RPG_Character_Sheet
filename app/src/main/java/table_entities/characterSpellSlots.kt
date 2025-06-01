package table_entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.annotation.IntRange

@Entity(
    tableName = "character_spell_slots",
    foreignKeys = [
        ForeignKey(
            entity = Character::class,
            parentColumns = ["characterId"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["characterId", "spellLevel"], unique = true)
    ]
)
data class CharacterSpellSlot(
    @PrimaryKey(autoGenerate = true)
    val spellSlotId: Int = 0,
    val characterId: Int,
    @IntRange(from = 1, to = 9)
    val spellLevel: Int,
    val totalSlots: Int = 0,
    val usedSlots: Int = 0
)