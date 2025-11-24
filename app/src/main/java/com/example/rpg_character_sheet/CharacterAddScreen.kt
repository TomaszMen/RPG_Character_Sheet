// ZASTĄP cały plik CharacterAddScreen.kt tym:

package com.example.rpg_character_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import table_entities.Character

@Composable
fun CharacterAddScreen(
    viewModel: CharacterViewModel,
    navController: NavHostController
) {
    val races by viewModel.getAllRacesAsPairs().collectAsState(initial = emptyList())
    val classes by viewModel.getAllClassesAsPair().collectAsState(initial = emptyList())
    val backgrounds by viewModel.getAllBackgroundsAsPairs().collectAsState(initial = emptyList())
    val alignments by viewModel.getAllAlignmentsAsPairs().collectAsState(initial = emptyList())
    val skills by viewModel.getAllSkills().collectAsState(initial = emptyList())

    var name by remember { mutableStateOf("") }
    var selectedRace by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var selectedSubrace by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var selectedClass by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var selectedBackground by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var selectedAlignment by remember { mutableStateOf<Pair<Int, String>?>(null) }

    // Stats
    var strength by remember { mutableStateOf("10") }
    var dexterity by remember { mutableStateOf("10") }
    var constitution by remember { mutableStateOf("10") }
    var intelligence by remember { mutableStateOf("10") }
    var wisdom by remember { mutableStateOf("10") }
    var charisma by remember { mutableStateOf("10") }

    // Skills selection
    var selectedSkills by remember { mutableStateOf<Set<Int>>(emptySet()) }

    // Equipment selection
    var selectedEquipmentOption by remember { mutableStateOf(0) }

    // Race info
    val raceInfo by viewModel.getRaceById(selectedRace?.first ?: 0).collectAsState(null)
    val raceFeatures by viewModel.getRaceFeatures(selectedRace?.first ?: 0).collectAsState(emptyList())
    val availableSubraces by viewModel.getSubracesForRace(selectedRace?.first ?: 0).collectAsState(emptyList())
    val subraceFeatures by viewModel.getSubraceFeatures(selectedSubrace?.first ?: 0).collectAsState(emptyList())

    // Class info
    val classInfo by viewModel.getClassById(selectedClass?.first ?: 0).collectAsState(null)
    val classFeatures by viewModel.getClassFeatures(selectedClass?.first ?: 0).collectAsState(emptyList())

    // Background info
    val backgroundInfo by viewModel.getBackgroundById(selectedBackground?.first ?: 0).collectAsState(null)

    // Calculate race bonuses
    val raceBonuses = remember(selectedRace?.first, selectedSubrace?.first) {
        viewModel.calculateRaceBonuses(
            selectedRace?.first ?: 0,
            selectedSubrace?.first ?: 0
        )
    }

    // Calculate stats with bonuses
    val baseStrength = strength.toIntOrNull() ?: 10
    val baseDexterity = dexterity.toIntOrNull() ?: 10
    val baseConstitution = constitution.toIntOrNull() ?: 10
    val baseIntelligence = intelligence.toIntOrNull() ?: 10
    val baseWisdom = wisdom.toIntOrNull() ?: 10
    val baseCharisma = charisma.toIntOrNull() ?: 10

    val totalStrength = baseStrength + raceBonuses.strength
    val totalDexterity = baseDexterity + raceBonuses.dexterity
    val totalConstitution = baseConstitution + raceBonuses.constitution
    val totalIntelligence = baseIntelligence + raceBonuses.intelligence
    val totalWisdom = baseWisdom + raceBonuses.wisdom
    val totalCharisma = baseCharisma + raceBonuses.charisma

    // Calculate HP
    val calculatedHP = classInfo?.let {
        calculateStartingHP(it.hitDie.toInt(), totalConstitution) + raceBonuses.extraHP
    } ?: 0

    // Limit skill selections
    val maxSkills = viewModel.getMaxSkillProficiencies(selectedClass?.first ?: 0)
    val skillSelectionWarning = if (selectedSkills.size > maxSkills) {
        "Too many skills selected! Maximum is $maxSkills"
    } else {
        "Select up to $maxSkills skills"
    }

    // Equipment options
    val equipmentOptions = viewModel.getStartingEquipmentOptions(selectedClass?.first ?: 0)

    LaunchedEffect(selectedRace) {
        selectedRace?.let {
            selectedSubrace = null
        }
    }

    Scaffold(
        floatingActionButton = {
            val isEnabled = name.isNotBlank() && selectedRace != null && selectedClass != null &&
                    selectedBackground != null && selectedAlignment != null &&
                    selectedSkills.size <= maxSkills

            FloatingActionButton(
                onClick = {
                    if (isEnabled) {
                        val character = Character(
                            characterName = name,
                            raceId = selectedRace!!.first,
                            subraceId = selectedSubrace?.first ?: 0,
                            classId = selectedClass!!.first,
                            subclassId = 0,
                            backgroundId = selectedBackground!!.first,
                            alignmentId = selectedAlignment!!.first,
                            level = 1,
                            strength = totalStrength,
                            dexterity = totalDexterity,
                            constitution = totalConstitution,
                            intelligence = totalIntelligence,
                            wisdom = totalWisdom,
                            charisma = totalCharisma,
                            hitPointMax = calculatedHP,
                            currentHitPoints = calculatedHP,
                            armorClass = 10 + ((totalDexterity - 10) / 2),
                            speed = raceInfo?.speed ?: 30
                        )

                        viewModel.insertCharacter(character)
                        navController.navigate(Screens.CharactersScreen.route)
                    }
                },
                containerColor = if (isEnabled) MaterialTheme.colorScheme.primaryContainer else Color.Gray
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Create New Character", style = MaterialTheme.typography.headlineMedium)

            // Basic Information Section
            BasicInfoSection(
                name = name,
                onNameChange = { name = it },
                races = races,
                selectedRace = selectedRace,
                onRaceSelected = { selectedRace = it },
                availableSubraces = availableSubraces,
                selectedSubrace = selectedSubrace,
                onSubraceSelected = { selectedSubrace = it },
                classes = classes,
                selectedClass = selectedClass,
                onClassSelected = { selectedClass = it },
                backgrounds = backgrounds,
                selectedBackground = selectedBackground,
                onBackgroundSelected = { selectedBackground = it },
                alignments = alignments,
                selectedAlignment = selectedAlignment,
                onAlignmentSelected = { selectedAlignment = it },
                raceInfo = raceInfo,
                raceFeatures = raceFeatures,
                subraceFeatures = subraceFeatures,
                classInfo = classInfo,
                classFeatures = classFeatures,
                backgroundInfo = backgroundInfo,
                raceBonuses = raceBonuses
            )

            // Ability Scores Section
            AbilityScoresSection(
                strength = strength,
                onStrengthChange = { strength = it },
                dexterity = dexterity,
                onDexterityChange = { dexterity = it },
                constitution = constitution,
                onConstitutionChange = { constitution = it },
                intelligence = intelligence,
                onIntelligenceChange = { intelligence = it },
                wisdom = wisdom,
                onWisdomChange = { wisdom = it },
                charisma = charisma,
                onCharismaChange = { charisma = it },
                raceBonuses = raceBonuses,
                classInfo = classInfo,
                calculatedHP = calculatedHP
            )

            // Skills Section
            SkillsSection(
                skills = skills,
                selectedSkills = selectedSkills,
                onSkillsChange = { selectedSkills = it },
                maxSkills = maxSkills,
                skillSelectionWarning = skillSelectionWarning
            )

            // Equipment Section
            EquipmentSection(
                equipmentOptions = equipmentOptions,
                selectedEquipmentOption = selectedEquipmentOption,
                onEquipmentSelected = { selectedEquipmentOption = it }
            )
        }
    }
}

// Helper function for HP calculation
private fun calculateStartingHP(hitDie: Int, constitution: Int): Int {
    val conModifier = (constitution - 10) / 2
    return hitDie + conModifier
}

// Basic Information Section
@Composable
fun BasicInfoSection(
    name: String,
    onNameChange: (String) -> Unit,
    races: List<Pair<Int, String>>,
    selectedRace: Pair<Int, String>?,
    onRaceSelected: (Pair<Int, String>) -> Unit,
    availableSubraces: List<Pair<Int, String>>,
    selectedSubrace: Pair<Int, String>?,
    onSubraceSelected: (Pair<Int, String>) -> Unit,
    classes: List<Pair<Int, String>>,
    selectedClass: Pair<Int, String>?,
    onClassSelected: (Pair<Int, String>) -> Unit,
    backgrounds: List<Pair<Int, String>>,
    selectedBackground: Pair<Int, String>?,
    onBackgroundSelected: (Pair<Int, String>) -> Unit,
    alignments: List<Pair<Int, String>>,
    selectedAlignment: Pair<Int, String>?,
    onAlignmentSelected: (Pair<Int, String>) -> Unit,
    raceInfo: table_entities.Race?,
    raceFeatures: List<table_entities.Feature>,
    subraceFeatures: List<table_entities.Feature>,
    classInfo: table_entities.CharacterClass?,
    classFeatures: List<table_entities.Feature>,
    backgroundInfo: table_entities.Background?,
    raceBonuses: CharacterViewModel.RaceBonus
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0x22000000))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Basic Information", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Character Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Race Selection
            SimpleDropdown(
                label = "Race",
                options = races,
                selected = selectedRace,
                onSelected = onRaceSelected
            )

            // Race Info
            selectedRace?.let {
                raceInfo?.let { race ->
                    Column {
                        Text("Race: ${race.raceName}", fontWeight = FontWeight.Bold)
                        Text("Speed: ${race.speed} ft")
                        Text("Size: ${race.size}")
                        // Show race bonuses
                        if (raceBonuses.strength != 0) Text("STR +${raceBonuses.strength}")
                        if (raceBonuses.dexterity != 0) Text("DEX +${raceBonuses.dexterity}")
                        if (raceBonuses.constitution != 0) Text("CON +${raceBonuses.constitution}")
                        if (raceBonuses.intelligence != 0) Text("INT +${raceBonuses.intelligence}")
                        if (raceBonuses.wisdom != 0) Text("WIS +${raceBonuses.wisdom}")
                        if (raceBonuses.charisma != 0) Text("CHA +${raceBonuses.charisma}")
                        if (raceBonuses.extraHP != 0) Text("Extra HP: +${raceBonuses.extraHP}")
                        race.description?.let { desc ->
                            Text(desc)
                        }
                    }
                }

                // Race Features
                if (raceFeatures.isNotEmpty()) {
                    Column {
                        Text("Race Features:", fontWeight = FontWeight.Bold)
                        raceFeatures.forEach { feature ->
                            Text("• ${feature.featureName}: ${feature.description}")
                        }
                    }
                }

                // Subrace Selection
                if (availableSubraces.isNotEmpty()) {
                    SimpleDropdown(
                        label = "Subrace (Optional)",
                        options = availableSubraces,
                        selected = selectedSubrace,
                        onSelected = onSubraceSelected
                    )

                    // Subrace Features
                    selectedSubrace?.let {
                        if (subraceFeatures.isNotEmpty()) {
                            Column {
                                Text("Subrace Features:", fontWeight = FontWeight.Bold)
                                subraceFeatures.forEach { feature ->
                                    Text("• ${feature.featureName}: ${feature.description}")
                                }
                            }
                        }
                    }
                }
            }

            // Class Selection
            SimpleDropdown(
                label = "Class",
                options = classes,
                selected = selectedClass,
                onSelected = onClassSelected
            )

            // Class Info
            selectedClass?.let {
                classInfo?.let { cls ->
                    Column {
                        Text("Class: ${cls.className}", fontWeight = FontWeight.Bold)
                        Text("Hit Die: d${cls.hitDie}")
                        Text("Starting HP: ${calculateStartingHP(cls.hitDie.toInt(), 10)}")
                        cls.description?.let { desc ->
                            Text(desc)
                        }
                    }
                }

                // Class Features
                if (classFeatures.isNotEmpty()) {
                    Column {
                        Text("Class Features:", fontWeight = FontWeight.Bold)
                        classFeatures.forEach { feature ->
                            Text("• ${feature.featureName}: ${feature.description}")
                        }
                    }
                }
            }

            // Background Selection
            SimpleDropdown(
                label = "Background",
                options = backgrounds,
                selected = selectedBackground,
                onSelected = onBackgroundSelected
            )

            // Background Info
            selectedBackground?.let {
                backgroundInfo?.let { bg ->
                    Column {
                        Text("Background: ${bg.backgroundName}", fontWeight = FontWeight.Bold)
                        bg.description?.let { desc ->
                            Text(desc)
                        }
                    }
                }
            }

            // Alignment Selection
            SimpleDropdown(
                label = "Alignment",
                options = alignments,
                selected = selectedAlignment,
                onSelected = onAlignmentSelected
            )
        }
    }
}

// Ability Scores Section
@Composable
fun AbilityScoresSection(
    strength: String,
    onStrengthChange: (String) -> Unit,
    dexterity: String,
    onDexterityChange: (String) -> Unit,
    constitution: String,
    onConstitutionChange: (String) -> Unit,
    intelligence: String,
    onIntelligenceChange: (String) -> Unit,
    wisdom: String,
    onWisdomChange: (String) -> Unit,
    charisma: String,
    onCharismaChange: (String) -> Unit,
    raceBonuses: CharacterViewModel.RaceBonus,
    classInfo: table_entities.CharacterClass?,
    calculatedHP: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0x22000000))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Ability Scores", style = MaterialTheme.typography.headlineSmall)

            // Current Stats Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0x220000FF))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text("Current Stats (with race bonuses):", fontWeight = FontWeight.Bold)
                    AbilityScoreDisplayWithBonus("STR", strength.toIntOrNull() ?: 10, raceBonuses.strength)
                    AbilityScoreDisplayWithBonus("DEX", dexterity.toIntOrNull() ?: 10, raceBonuses.dexterity)
                    AbilityScoreDisplayWithBonus("CON", constitution.toIntOrNull() ?: 10, raceBonuses.constitution)
                    AbilityScoreDisplayWithBonus("INT", intelligence.toIntOrNull() ?: 10, raceBonuses.intelligence)
                    AbilityScoreDisplayWithBonus("WIS", wisdom.toIntOrNull() ?: 10, raceBonuses.wisdom)
                    AbilityScoreDisplayWithBonus("CHA", charisma.toIntOrNull() ?: 10, raceBonuses.charisma)
                }
            }

            // HP Calculation
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0x2200FF00))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text("HP Calculation:", fontWeight = FontWeight.Bold)
                    Text("Class Hit Die: d${classInfo?.hitDie ?: "?"}")
                    Text("CON Modifier: ${if (((constitution.toIntOrNull() ?: 10) + raceBonuses.constitution - 10) / 2 >= 0) "+${((constitution.toIntOrNull() ?: 10) + raceBonuses.constitution - 10) / 2}" else ((constitution.toIntOrNull() ?: 10) + raceBonuses.constitution - 10) / 2}")
                    Text("Starting HP: $calculatedHP", fontWeight = FontWeight.Bold)
                }
            }

            // Stats Input
            Text("Enter Ability Scores:", style = MaterialTheme.typography.titleSmall)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatFieldWithModifier("STR", strength, onStrengthChange, Modifier.weight(1f))
                StatFieldWithModifier("DEX", dexterity, onDexterityChange, Modifier.weight(1f))
                StatFieldWithModifier("CON", constitution, onConstitutionChange, Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatFieldWithModifier("INT", intelligence, onIntelligenceChange, Modifier.weight(1f))
                StatFieldWithModifier("WIS", wisdom, onWisdomChange, Modifier.weight(1f))
                StatFieldWithModifier("CHA", charisma, onCharismaChange, Modifier.weight(1f))
            }
        }
    }
}

// Skills Section
@Composable
fun SkillsSection(
    skills: List<table_entities.Skill>,
    selectedSkills: Set<Int>,
    onSkillsChange: (Set<Int>) -> Unit,
    maxSkills: Int,
    skillSelectionWarning: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor =
            if (selectedSkills.size > maxSkills) Color(0x44FF0000) else Color(0x22000000)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Skill Proficiencies", style = MaterialTheme.typography.headlineSmall)
            Text(skillSelectionWarning,
                color = if (selectedSkills.size > maxSkills) Color.Red else Color.Unspecified)

            val skillsByAbility = skills.groupBy { it.abilityScore }

            skillsByAbility.forEach { (ability, abilitySkills) ->
                Text("$ability Skills:", fontWeight = FontWeight.Bold)
                abilitySkills.forEach { skill ->
                    SkillSelectionItem(
                        skill = skill,
                        selected = selectedSkills.contains(skill.skillId),
                        enabled = selectedSkills.size < maxSkills || selectedSkills.contains(skill.skillId),
                        onSelectionChange = { selected ->
                            val newSkills = if (selected) {
                                if (selectedSkills.size < maxSkills) {
                                    selectedSkills + skill.skillId
                                } else {
                                    selectedSkills
                                }
                            } else {
                                selectedSkills - skill.skillId
                            }
                            onSkillsChange(newSkills)
                        }
                    )
                }
            }
        }
    }
}

// Equipment Section
@Composable
fun EquipmentSection(
    equipmentOptions: List<CharacterViewModel.StartingEquipmentOption>,
    selectedEquipmentOption: Int,
    onEquipmentSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0x22000000))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Starting Equipment", style = MaterialTheme.typography.headlineSmall)

            equipmentOptions.forEachIndexed { index, option ->
                EquipmentOptionItem(
                    option = option,
                    selected = selectedEquipmentOption == index,
                    onSelected = { onEquipmentSelected(index) }
                )
            }
        }
    }
}

// Helper Composable Functions
@Composable
fun SimpleDropdown(
    label: String,
    options: List<Pair<Int, String>>,
    selected: Pair<Int, String>?,
    onSelected: (Pair<Int, String>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
    ) {
        OutlinedTextField(
            value = selected?.second ?: "",
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            enabled = false,
            readOnly = true,
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.second) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AbilityScoreDisplayWithBonus(label: String, base: Int, bonus: Int) {
    val total = base + bonus
    val modifier = (total - 10) / 2
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("$label:")
        if (bonus != 0) {
            Text("$base + $bonus = $total (${if (modifier >= 0) "+$modifier" else modifier})")
        } else {
            Text("$total (${if (modifier >= 0) "+$modifier" else modifier})")
        }
    }
}

@Composable
fun StatFieldWithModifier(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || (newValue.toIntOrNull() != null && newValue.toInt() in 1..20)) {
                    onChange(newValue)
                }
            },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        val currentValue = value.toIntOrNull() ?: 10
        Text(
            text = "Mod: ${(currentValue - 10) / 2}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun SkillSelectionItem(
    skill: table_entities.Skill,
    selected: Boolean,
    enabled: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onSelectionChange(!selected) },
        colors = CardDefaults.cardColors(
            containerColor = when {
                selected && enabled -> MaterialTheme.colorScheme.primaryContainer
                !enabled -> Color.LightGray
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(skill.skillName, style = MaterialTheme.typography.bodyLarge)
                Text("Based on: ${skill.abilityScore}", style = MaterialTheme.typography.bodyMedium)
            }
            Checkbox(
                checked = selected,
                onCheckedChange = if (enabled) onSelectionChange else null,
                enabled = enabled
            )
        }
    }
}

@Composable
fun EquipmentOptionItem(
    option: CharacterViewModel.StartingEquipmentOption,
    selected: Boolean,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(option.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            if (option.items.isNotEmpty()) {
                Text("Includes standard equipment")
            } else {
                Text("100 gold pieces to buy equipment")
            }
        }
    }
}