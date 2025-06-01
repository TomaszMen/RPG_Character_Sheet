package table_entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "class_spells",
    foreignKeys = [
        ForeignKey(
            entity = Spell::class,
            parentColumns = ["spellId"],
            childColumns = ["spellId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CharacterClass::class,
            parentColumns = ["classId"],
            childColumns = ["classId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["spellId", "classId"], unique = true)
    ]
)
data class ClassSpell(
    @PrimaryKey(autoGenerate = true)
    val classSpellId: Int = 0,
    val spellId: Int,
    val classId: Int
)