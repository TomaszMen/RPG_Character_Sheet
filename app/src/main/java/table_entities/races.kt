package table_entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "races")
data class Race(
    @PrimaryKey(autoGenerate = true)
    val raceId: Int = 0,
    val raceName: String,
    val speed: Int,
    val size: RaceSize,
    val description: String? = null
) {
    enum class RaceSize {
        Tiny,
        Small,
        Medium,
        Large,
        Huge,
        Gargantuan
    }
}