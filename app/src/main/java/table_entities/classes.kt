package table_entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "classes")
data class CharacterClass(
    @PrimaryKey(autoGenerate = true)
    val classId: Int = 0,
    val className: String,
    val hitDie: String,
    val description: String? = null
)