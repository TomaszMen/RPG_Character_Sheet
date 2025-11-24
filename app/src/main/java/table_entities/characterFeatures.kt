package table_entities

import androidx.room.ColumnInfo
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
    ]
)
data class CharacterFeature(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "characterFeatureId")
    val characterFeatureId: Int = 0,

    @ColumnInfo(name = "characterId")
    val characterId: Int,

    @ColumnInfo(name = "featureId")
    val featureId: Int
)