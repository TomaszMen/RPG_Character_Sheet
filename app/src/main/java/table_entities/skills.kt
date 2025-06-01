package table_entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skills")
data class Skill(
    @PrimaryKey(autoGenerate = true)
    val skillId: Int = 0,
    val skillName: String,
    val abilityScore: AbilityScoreType
) {
    enum class AbilityScoreType {
        STR, DEX, CON, INT, WIS, CHA
    }
}