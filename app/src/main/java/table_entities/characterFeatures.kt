package table_entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "character_features",
    foreignKeys = [
        ForeignKey(
            entity = Character::class,
            parentColumns = ["characterId"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Feature::class,
            parentColumns = ["featureId"],
            childColumns = ["featureId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["characterId", "featureId"], unique = true)
    ]
)
data class CharacterFeature(
    @PrimaryKey(autoGenerate = true)
    val characterFeatureId: Int = 0,
    val characterId: Int,
    val featureId: Int
)