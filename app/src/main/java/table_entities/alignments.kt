package table_entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alignments")
data class Alignment(
    @PrimaryKey(autoGenerate = true)
    val alignmentId: Int = 0,
    val alignmentName: String,
    val description: String? = null
)