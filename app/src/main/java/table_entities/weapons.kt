package table_entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weapons",
    indices = [
        Index(value = ["weaponType"], name = "idx_weapon_type")
    ],
    foreignKeys = [
        ForeignKey(
            entity = Item::class,
            parentColumns = ["itemId"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Weapon(
    @PrimaryKey(autoGenerate = true)
    val weaponId: Int = 0,
    val itemId: Int,
    val weaponType: WeaponType,
    val weaponCategory: WeaponCategory,
    val damageDice: String,
    val damageType: DamageType,
    val properties: String? = null,
    val rangeNormal: Int? = null,
    val rangeLong: Int? = null
) {
    enum class WeaponType {
        Simple, Martial;

        companion object {
            fun fromString(value: String): WeaponType = valueOf(value)
        }
    }

    enum class WeaponCategory {
        Melee, Ranged;

        companion object {
            fun fromString(value: String): WeaponCategory = valueOf(value)
        }
    }

    enum class DamageType {
        Acid, Bludgeoning, Cold, Fire, Force, Lightning,
        Necrotic, Piercing, Poison, Psychic, Radiant, Slashing, Thunder;

        companion object {
            fun fromString(value: String): DamageType = valueOf(value)
        }
    }
}