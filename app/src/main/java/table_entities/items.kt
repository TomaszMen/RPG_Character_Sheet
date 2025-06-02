package table_entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "items",
    indices = [
        Index(value = ["itemName"], name = "idx_item_name")
    ])
data class Item(
    @PrimaryKey(autoGenerate = true)
    val itemId: Int = 0,
    val itemName: String,
    val itemType: ItemType,
    val weight: Int = 0,
    val cost: Int = 0,
    val description: String? = null
) {
    enum class ItemType {
        Weapon,
        Armor,
        Gear,
        Tool,
        Consumable,
        Other;

        companion object {
            fun fromString(value: String): ItemType {
                return valueOf(value)
            }
        }
    }
}