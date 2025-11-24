package table_entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "character_skills",
    foreignKeys = [
        ForeignKey(
            entity = Character::class,
            parentColumns = ["characterId"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Skill::class,
            parentColumns = ["skillId"],
            childColumns = ["skillId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["characterId", "skillId"], unique = true)
    ]
)
data class CharacterSkill(
    @PrimaryKey(autoGenerate = true)
    val characterSkillId: Int = 0,
    val characterId: Long,
    val skillId: Int,
    val proficiency: Int = false,
    val expertise: Boolean = false
)