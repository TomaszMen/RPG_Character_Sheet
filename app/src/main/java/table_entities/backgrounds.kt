package table_entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backgrounds")
data class Background(
    @PrimaryKey(autoGenerate = true)
    val backgroundId: Int = 0,
    val backgroundName: String,
    val description: String? = null
)