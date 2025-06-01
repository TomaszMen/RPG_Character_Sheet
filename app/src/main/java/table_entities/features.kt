package table_entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "features")
data class Feature(
    @PrimaryKey(autoGenerate = true)
    val featureId: Int = 0,
    val featureName: String,
    val description: String,
    val sourceType: FeatureSourceType,
    val sourceId: Int,
    val levelRequirement: Int = 1
) {
    enum class FeatureSourceType {
        Race,
        Subrace,
        Class,
        Subclass,
        Feat,
        Other;

        companion object {
            fun fromString(value: String): FeatureSourceType {
                return valueOf(value)
            }
        }
    }
}