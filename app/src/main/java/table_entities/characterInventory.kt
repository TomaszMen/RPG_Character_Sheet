package table_entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
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
    ]
)
data class CharacterInventory(
    @PrimaryKey(autoGenerate = true) val inventoryId: Int = 0,
    @ColumnInfo(name = "characterId") val characterId: Int,
    @ColumnInfo(name = "itemId") val itemId: Int,
    @ColumnInfo(name = "quantity") val quantity: Int = 1,
    @ColumnInfo(name = "equipped") val equipped: Boolean = false
)