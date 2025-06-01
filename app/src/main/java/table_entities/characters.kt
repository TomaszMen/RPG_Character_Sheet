package table_entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "characters",
    indices = [
        Index(value = ["characterName"], name = "idx_character_name"),
        Index(value = ["playerName"], name = "idx_character_player")
    ],
    foreignKeys = [
        ForeignKey(
            entity = Race::class,
            parentColumns = ["raceId"],
            childColumns = ["raceId"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = Subrace::class,
            parentColumns = ["subraceId"],
            childColumns = ["subraceId"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = CharacterClass::class,
            parentColumns = ["classId"],
            childColumns = ["classId"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = Subclass::class,
            parentColumns = ["subclassId"],
            childColumns = ["subclassId"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = Background::class,
            parentColumns = ["backgroundId"],
            childColumns = ["backgroundId"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = Alignment::class,
            parentColumns = ["alignmentId"],
            childColumns = ["alignmentId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class Character(
    @PrimaryKey(autoGenerate = true)
    val characterId: Int = 0,
    val characterName: String,
    val raceId: Int,
    val subraceId: Int? = null,
    val classId: Int,
    val subclassId: Int? = null,
    val backgroundId: Int,
    val alignmentId: Int,
    val experiencePoints: Int = 0,
    val level: Int = 1,
    val inspiration: Boolean = false,
    val strength: Int = 10,
    val dexterity: Int = 10,
    val constitution: Int = 10,
    val intelligence: Int = 10,
    val wisdom: Int = 10,
    val charisma: Int = 10,
    val hitPointMax: Int = 1,
    val currentHitPoints: Int = 1,
    val temporaryHitPoints: Int = 0,
    val armorClass: Int = 10,
    val initiative: Int = 0,
    val speed: Int = 30,
    val personalityTraits: String? = null,
    val ideals: String? = null,
    val bonds: String? = null,
    val flaws: String? = null,
    val appearance: String? = null,
    val backstory: String? = null

)