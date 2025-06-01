package table_entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subraces",
    foreignKeys = [
        ForeignKey(
            entity = Race::class,
            parentColumns = ["raceId"],
            childColumns = ["raceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["raceId", "subraceName"], unique = true)
    ]
)
data class Subrace(
    @PrimaryKey(autoGenerate = true)
    val subraceId: Int = 0,
    val raceId: Int,
    val subraceName: String,
    val description: String? = null
)