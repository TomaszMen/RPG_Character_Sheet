package table_entities

import androidx.room.ColumnInfo
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
    ]
)
data class CharacterSkill(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "characterSkillId")
    val characterSkillId: Int = 0,

    @ColumnInfo(name = "characterId")
    val characterId: Int,

    @ColumnInfo(name = "skillId")
    val skillId: Int,

    @ColumnInfo(name = "proficiency")
    val proficiency: Int = 0,

    @ColumnInfo(name = "expertise")
    val expertise: Int = 0
)