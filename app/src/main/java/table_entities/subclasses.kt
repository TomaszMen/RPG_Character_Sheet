package table_entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subclasses",
    foreignKeys = [
        ForeignKey(
            entity = CharacterClass::class,
            parentColumns = ["classId"],
            childColumns = ["classId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["classId", "subclassName"], unique = true)
    ]
)
data class Subclass(
    @PrimaryKey(autoGenerate = true)
    val subclassId: Int = 0,
    val classId: Int? = null,
    val subclassName: String,
    val description: String? = null
)