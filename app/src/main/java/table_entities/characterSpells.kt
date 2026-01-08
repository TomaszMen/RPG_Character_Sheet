package table_entities

import androidx.room.ColumnInfo
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
    ]
)
data class CharacterSpell(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "characterSpellId")
    val characterSpellId: Int = 0,

    @ColumnInfo(name = "characterId")
    val characterId: Int,

    @ColumnInfo(name = "spellId")
    val spellId: Int,

    @ColumnInfo(name = "prepared")
    val prepared: Int = 0
)