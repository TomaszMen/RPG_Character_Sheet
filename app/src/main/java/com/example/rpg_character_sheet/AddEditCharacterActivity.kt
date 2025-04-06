package com.example.rpg_character_sheet


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class AddEditCharacterActivity : AppCompatActivity() {

    private lateinit var characterViewModel: CharacterViewModel
    private lateinit var nameEdit: EditText
    private lateinit var descriptionEdit: EditText
    private lateinit var levelEdit: EditText
    private lateinit var strengthEdit: EditText
    private lateinit var dexterityEdit: EditText
    private lateinit var constitutionEdit: EditText
    private lateinit var intelligenceEdit: EditText
    private lateinit var wisdomEdit: EditText
    private lateinit var charismaEdit: EditText
    private lateinit var characterClass: Spinner
    private lateinit var raceSpinner: Spinner
    private lateinit var armorSpinner: Spinner

    private var characterId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_edit_character)

        nameEdit = findViewById(R.id.character_name_edit)
        descriptionEdit = findViewById(R.id.character_description_edit)
        levelEdit = findViewById(R.id.character_level_edit)
        strengthEdit = findViewById(R.id.character_strength_edit)
        dexterityEdit = findViewById(R.id.character_dexterity_edit)
        constitutionEdit = findViewById(R.id.character_constitution_edit)
        intelligenceEdit = findViewById(R.id.character_intelligence_edit)
        wisdomEdit = findViewById(R.id.character_wisdom_edit)
        charismaEdit = findViewById(R.id.character_charisma_edit)
        characterClass = findViewById(R.id.character_class)
        raceSpinner = findViewById(R.id.race_spinner)
        armorSpinner = findViewById(R.id.armor_spinner)
        val saveButton: Button = findViewById(R.id.save_button)
        val deleteButton: Button = findViewById(R.id.delete_button)

        characterViewModel = ViewModelProvider(this).get(CharacterViewModel::class.java)

        setupSpinners()

        val intent = intent
        if (intent.hasExtra("character_id")) {
            characterId = intent.getIntExtra("character_id", 0)
            if (characterId != 0) {
                loadCharacterData(characterId)
                deleteButton.visibility = View.VISIBLE
            }
        }

        saveButton.setOnClickListener { saveCharacter() }
        deleteButton.setOnClickListener { deleteCharacter() }
    }

    private fun deleteCharacter() {
        try {
            characterViewModel.getCharacterById(characterId).observe(this) { character ->
                if (character != null) {
                    Thread {
                        characterViewModel.delete(character)
                        runOnUiThread {
                            Toast.makeText(this, "Character deleted successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }.start()
                } else {
                    Toast.makeText(this, "Character not found", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("DeleteError", "Error deleting character: ${e.message}")
            Toast.makeText(this, "Error deleting character", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSpinners() {
        val classAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.character_classes,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        characterClass.adapter = classAdapter

        val raceAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.character_races,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        raceSpinner.adapter = raceAdapter

        val armorAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.armor_types,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        armorSpinner.adapter = armorAdapter
    }

    private fun loadCharacterData(characterId: Int) {
        characterViewModel.getCharacterById(characterId).observe(this) { character ->
            if (character != null) {
                nameEdit.setText(character.name)
                descriptionEdit.setText(character.description)
                levelEdit.setText(character.level.toString())
                strengthEdit.setText(character.stats[globalStrId].toString())
                dexterityEdit.setText(character.stats[globalDexId].toString())
                constitutionEdit.setText(character.stats[globalConId].toString())
                intelligenceEdit.setText(character.stats[globalIntId].toString())
                wisdomEdit.setText(character.stats[globalWisId].toString())
                charismaEdit.setText(character.stats[globalChaId].toString())

                characterClass.setSelection(getIndex(characterClass, character.characterClass))
                raceSpinner.setSelection(getIndex(raceSpinner, character.race))
                armorSpinner.setSelection(getIndex(armorSpinner, character.armorType))
            }
        }
    }

    private fun getIndex(spinner: Spinner, value: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(value, ignoreCase = true)) {
                return i
            }
        }
        return 0
    }

    private fun saveCharacter() {
        val name = nameEdit.text.toString().trim()
        val description = descriptionEdit.text.toString().trim()
        val selectedCharacterClass = characterClass.selectedItem.toString()
        val race = raceSpinner.selectedItem.toString()
        val armorType = armorSpinner.selectedItem.toString()
        val lvl = levelEdit.text.toString()
        val str = strengthEdit.text.toString()
        val dex = dexterityEdit.text.toString()
        val con = constitutionEdit.text.toString()
        val intl = intelligenceEdit.text.toString()
        val wis = wisdomEdit.text.toString()
        val cha = charismaEdit.text.toString()

        if (name.isEmpty() || lvl.isEmpty() || str.isEmpty() || dex.isEmpty() ||
            con.isEmpty() || intl.isEmpty() || wis.isEmpty() || cha.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val level = lvl.toInt()
        val strength = str.toInt()
        val dexterity = dex.toInt()
        val constitution = con.toInt()
        val intelligence = intl.toInt()
        val wisdom = wis.toInt()
        val charisma = cha.toInt()

        if (level < 1 || level > 12) {
            Toast.makeText(this, "Invalid data received: Level must be from 1 to 12", Toast.LENGTH_SHORT).show()
            return
        }

        if (strength < 8 || strength > 20 || dexterity < 8 || dexterity > 20 ||
            constitution < 8 || constitution > 20 || intelligence < 8 || intelligence > 20 ||
            wisdom < 8 || wisdom > 20 || charisma < 8 || charisma > 20) {
            Toast.makeText(this, "Invalid data received: Stats must be from 8 to 20", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            Thread {
                val character = Character().apply {
                    id = characterId
                    this.name = name
                    this.description = description
                    this.characterClass = selectedCharacterClass
                    this.race = race
                    this.armorType = armorType
                    this.level = level
                    stats[globalStrId] = strength
                    stats[globalDexId] = dexterity
                    stats[globalConId] = constitution
                    stats[globalIntId] = intelligence
                    stats[globalWisId] = wisdom
                    stats[globalChaId] = charisma

                    updateArmorStats()
                    updateMaxHp()
                }

                if (characterId == 0) {
                    characterViewModel.insertCharacter(character)
                } else {
                    characterViewModel.updateCharacter(character)
                }

                runOnUiThread {
                    Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }.start()
        } catch (e: Exception) {
            Log.e("SaveError", "Error saving data: ${e.message}")
            Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show()
        }
    }
}