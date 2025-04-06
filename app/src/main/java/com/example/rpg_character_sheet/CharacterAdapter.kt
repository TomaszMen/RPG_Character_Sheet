package com.example.rpg_character_sheet

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class CharacterAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Character, CharacterAdapter.CharacterViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Character>() {
            override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean =
                oldItem.id == newItem.id

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean =
                oldItem == newItem
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
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(bindingAdapterPosition))
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(character: Character) {
            characterName.text = character.name ?: "N/A"
            characterClass.text = character.characterClass ?: "N/A"
            healthPoints.text = "Maximum Health Points: ${character.hp}"
            armorClass.text = "Armor Class: ${character.ac}"
            walkingSpeed.text = "Walking Speed: ${character.ws}ft"
            str.text = character.str.toString()
            dex.text = character.dex.toString()
            con.text = character.con.toString()
            intl.text = character.int.toString()
            wis.text = character.wis.toString()
            cha.text = character.cha.toString()
        }
    }

    interface OnItemClickListener {
        fun onItemClick(character: Character)
    }
}