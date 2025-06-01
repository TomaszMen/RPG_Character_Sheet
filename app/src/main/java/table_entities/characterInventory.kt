package table_entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "character_inventory",
    foreignKeys = [
        ForeignKey(
            entity = Character::class,
            parentColumns = ["characterId"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["characterId", "itemId"], unique = false) // Non-unique as characters can have multiple of same item
    ]
)
data class CharacterInventory(
    @PrimaryKey(autoGenerate = true)
    val inventoryId: Int = 0,
    val characterId: Int,
    val itemId: Int,
    val quantity: Int = 1,
    val equipped: Boolean = false
)