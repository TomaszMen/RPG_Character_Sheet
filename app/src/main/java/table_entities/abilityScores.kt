package table_entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ability_scores")
data class AbilityScore(
    @PrimaryKey(autoGenerate = true)
    val abilityScoreId: Int = 0,
    val abilityName: String,
    val abbreviation: AbilityAbbreviation
) {
    enum class AbilityAbbreviation {
        STR, DEX, CON, INT, WIS, CHA
    }
}
