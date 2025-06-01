package table_entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "classes")
data class CharacterClass(
    @PrimaryKey(autoGenerate = true)
    val classId: Int = 0,
    val className: String,
    val hitDie: HitDie,
    val description: String? = null
) {
    enum class HitDie(val value: Int) {
        D6(6),
        D8(8),
        D10(10),
        D12(12);

        companion object {
            fun fromValue(value: Int): HitDie {
                return values().first { it.value == value }
            }
        }
    }
}