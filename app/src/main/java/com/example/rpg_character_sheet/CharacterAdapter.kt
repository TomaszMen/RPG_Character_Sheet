package com.example.rpg_character_sheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class CharacterAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<Character, CharacterAdapter.CharacterViewHolder>(CharacterDiffCallback()) {

    inner class CharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val characterName: TextView = itemView.findViewById(R.id.character_name)
        private val characterClass: TextView = itemView.findViewById(R.id.character_class)
        private val healthPoints: TextView = itemView.findViewById(R.id.health_points)
        private val armorClass: TextView = itemView.findViewById(R.id.a_c)
        private val walkingSpeed: TextView = itemView.findViewById(R.id.walking_speed)
        private val str: TextView = itemView.findViewById(R.id.character_strength)
        private val dex: TextView = itemView.findViewById(R.id.character_dexterity)
        private val con: TextView = itemView.findViewById(R.id.character_constitution)
        private val intl: TextView = itemView.findViewById(R.id.character_intelligence)
        private val wis: TextView = itemView.findViewById(R.id.character_wisdom)
        private val cha: TextView = itemView.findViewById(R.id.character_charisma)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position))
                }
            }
        }

        fun bind(character: Character) {
            characterName.text = character.getDisplayName()
            characterClass.text = character.getCharacterClassDisplay()
            healthPoints.text = "Maximum Health Points: ${character.getHP()}"
            armorClass.text = "Armor Class: ${character.getAC()}"
            walkingSpeed.text = "Walking Speed: ${character.getWS()}ft"
            str.text = character.getStr().toString()
            dex.text = character.getDex().toString()
            con.text = character.getCon().toString()
            intl.text = character.getInt().toString()
            wis.text = character.getWis().toString()
            cha.text = character.getCha().toString()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.character_item, parent, false)
        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface OnItemClickListener {
        fun onItemClick(character: Character)
    }

    private class CharacterDiffCallback : DiffUtil.ItemCallback<Character>() {
        override fun areItemsTheSame(oldItem: Character, newItem: Character) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Character, newItem: Character) =
            oldItem == newItem
    }
}