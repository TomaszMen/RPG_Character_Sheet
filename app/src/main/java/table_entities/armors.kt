package table_entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "armor",
    indices = [
        Index(value = ["armorType"], name = "idx_armor_type")
    ],
    foreignKeys = [
        ForeignKey(
            entity = Item::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Armor(
    @PrimaryKey(autoGenerate = true)
    val armorId: Int = 0,
    val itemId: Int,
    val armorType: ArmorType,
    val armorClass: Int,
    val strengthRequirement: Int = 0,
    val stealthDisadvantage: Boolean = false
) {
    enum class ArmorType {
        Light, Medium, Heavy, Shield;

        companion object {
            fun fromString(value: String): ArmorType = valueOf(value)
        }
    }
}