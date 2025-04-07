package com.example.rpg_character_sheet

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast

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
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_add_edit_character)

        initializeViews()
        setupSpinners()
        characterViewModel = ViewModelProvider(this).get(CharacterViewModel::class.java)

        intent?.extras?.getInt("character_id")?.let { id ->
            if (id != 0) {
                characterId = id
                loadCharacterData(id)
                findViewById<Button>(R.id.delete_button).visibility = View.VISIBLE
            }
        }

        findViewById<Button>(R.id.save_button).setOnClickListener { saveCharacter() }
        findViewById<Button>(R.id.delete_button).setOnClickListener { deleteCharacter() }
    }

    private fun initializeViews() {
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
    }

    private fun setupSpinners() {
        characterClass.adapter = createSpinnerAdapter(R.array.character_classes)
        raceSpinner.adapter = createSpinnerAdapter(R.array.character_races)
        armorSpinner.adapter = createSpinnerAdapter(R.array.armor_types)
    }

    private fun createSpinnerAdapter(arrayResId: Int): ArrayAdapter<CharSequence> {
        return ArrayAdapter.createFromResource(
            this,
            arrayResId,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun loadCharacterData(id: Int) {
        characterViewModel.getCharacterById(id).observe(this) { character ->
            character?.let {
                nameEdit.setText(it.name)
                descriptionEdit.setText(it.description)
                levelEdit.setText(it.level.toString())
                strengthEdit.setText(it.stats[Character.globalStrId].toString())
                dexterityEdit.setText(it.stats[Character.globalDexId].toString())
                constitutionEdit.setText(it.stats[Character.globalConId].toString())
                intelligenceEdit.setText(it.stats[Character.globalIntId].toString())
                wisdomEdit.setText(it.stats[Character.globalWisId].toString())
                charismaEdit.setText(it.stats[Character.globalChaId].toString())

                characterClass.setSelection(getSpinnerIndex(characterClass, it.characterClass))
                raceSpinner.setSelection(getSpinnerIndex(raceSpinner, it.race))
                armorSpinner.setSelection(getSpinnerIndex(armorSpinner, it.armorType))
            }
        }
    }

    private fun getSpinnerIndex(spinner: Spinner, value: String?): Int {
        return (0 until spinner.count).firstOrNull { i ->
            spinner.getItemAtPosition(i).toString().equals(value, ignoreCase = true)
        } ?: 0
    }

    private fun deleteCharacter() {
        characterViewModel.getCharacterById(characterId).observe(this) { character ->
            character?.let {
                GlobalScope.launch(Dispatchers.IO) {
                    characterViewModel.delete(it)
                    runOnUiThread {
                        Toast.makeText(this@AddEditCharacterActivity,
                            "Character deleted successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } ?: run {
                Toast.makeText(this, "Character not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveCharacter() {
        val name = nameEdit.text.toString().trim()
        val description = descriptionEdit.text.toString().trim()
        val selectedClass = characterClass.selectedItem.toString()
        val race = raceSpinner.selectedItem.toString()
        val armorType = armorSpinner.selectedItem.toString()
        val levelStr = levelEdit.text.toString()
        val strStr = strengthEdit.text.toString()
        val dexStr = dexterityEdit.text.toString()
        val conStr = constitutionEdit.text.toString()
        val intStr = intelligenceEdit.text.toString()
        val wisStr = wisdomEdit.text.toString()
        val chaStr = charismaEdit.text.toString()

        if (name.isEmpty() || levelStr.isEmpty() || strStr.isEmpty() || dexStr.isEmpty() ||
            conStr.isEmpty() || intStr.isEmpty() || wisStr.isEmpty() || chaStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val level = levelStr.toInt()
        val strength = strStr.toInt()
        val dexterity = dexStr.toInt()
        val constitution = conStr.toInt()
        val intelligence = intStr.toInt()
        val wisdom = wisStr.toInt()
        val charisma = chaStr.toInt()

        if (level !in 1..12) {
            Toast.makeText(this,
                "Invalid data received: Level must be from 1 to 12", Toast.LENGTH_SHORT).show()
            return
        }

        if (listOf(strength, dexterity, constitution, intelligence, wisdom, charisma).any { it !in 8..20 }) {
            Toast.makeText(this,
                "Invalid data received: Stats must be from 8 to 20", Toast.LENGTH_SHORT).show()
            return
        }

        GlobalScope.launch(Dispatchers.IO) {
            val character = Character().apply {
                id = characterId
                this.name = name
                this.description = description
                characterClass = selectedClass
                this.race = race
                this.armorType = armorType
                this.level = level
                stats[Character.globalStrId] = strength
                stats[Character.globalDexId] = dexterity
                stats[Character.globalConId] = constitution
                stats[Character.globalIntId] = intelligence
                stats[Character.globalWisId] = wisdom
                stats[Character.globalChaId] = charisma

                updateArmorStats()
                updateMaxHp()
            }

            if (characterId == 0) {
                characterViewModel.insertCharacter(character)
            } else {
                characterViewModel.updateCharacter(character)
            }

            runOnUiThread {
                Toast.makeText(this@AddEditCharacterActivity,
                    "Saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}