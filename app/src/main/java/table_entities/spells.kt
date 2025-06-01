package table_entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.IntRange
import androidx.room.Index

@Entity(
    tableName = "spells",
    indices = [
        Index(value = ["spellName"], name = "idx_spell_name"),
        Index(value = ["spellLevel"], name = "idx_spell_level"),
        Index(value = ["school"], name = "idx_spell_school")
    ])
data class Spell(
    @PrimaryKey(autoGenerate = true)
    val spellId: Int = 0,
    val spellName: String,
    @field:IntRange(from = 0, to = 9) //level 0 spells are cantrips which do not need spellslots
    val spellLevel: Int,
    val school: MagicSchool,
    val castingTime: String,
    val spellRange: String,
    val components: String,
    val duration: String,
    val description: String,
    val higherLevels: String? = null,
    val ritual: Boolean = false,
    val concentration: Boolean = false
) {
    enum class MagicSchool {
        Abjuration,
        Conjuration,
        Divination,
        Enchantment,
        Evocation,
        Illusion,
        Necromancy,
        Transmutation;

        companion object {
            fun fromString(value: String): MagicSchool = valueOf(value)
        }
    }
}