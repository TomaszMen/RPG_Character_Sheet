package com.example.rpg_character_sheet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class PlayCharacterActivity : AppCompatActivity() {
    private lateinit var characterViewModel: CharacterViewModel
    private lateinit var currentHpEdit: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_character)

        currentHpEdit = findViewById(R.id.current_hp_value)
        findViewById<Button>(R.id.edit_button).setOnClickListener { openEditMode() }
        findViewById<Button>(R.id.save_hp_button).setOnClickListener { saveHpChanges() }

        characterViewModel = ViewModelProvider(this).get(CharacterViewModel::class.java)
        loadCharacterData()
    }

    private fun loadCharacterData() {
        intent.getIntExtra("character_id", 0).let { id ->
            characterViewModel.getCharacterById(id).observe(this) { character ->
                character?.let {
                    findViewById<TextView>(R.id.ac_value).text = "AC: ${it.armorClass}"
                    findViewById<TextView>(R.id.speed_value).text = "Speed: ${it.walkingSpeed}ft"
                    findViewById<TextView>(R.id.initiative_value).text = "Initiative: +${it.initiative}"
                    currentHpEdit.setText(it.currentHp.toString())
                    findViewById<TextView>(R.id.max_hp_value).text = "/ ${it.maxHp}"
                }
            }
        }
    }

    private fun openEditMode() {
        intent.getIntExtra("character_id", 0).let { id ->
            startActivity(Intent(this, AddEditCharacterActivity::class.java).apply {
                putExtra("character_id", id)
            })
        }
    }

    private fun saveHpChanges() {
        val newHp = currentHpEdit.text.toString().toIntOrNull() ?: return
        intent.getIntExtra("character_id", 0).let { id ->
            characterViewModel.getCharacterById(id).observe(this) { character ->
                character?.let {
                    it.currentHp = newHp.coerceIn(0, it.maxHp)
                    characterViewModel.updateCharacter(it)
                    Toast.makeText(this, "HP updated", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}