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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.first
import table_entities.Character
import kotlinx.coroutines.launch

@Composable
fun CharacterDetailsScreen(
    characterId: Int,
    viewModel: CharacterViewModel,
    navController: NavHostController
) {
    // Load character data
    viewModel.selectCharacter(characterId)
    val character by viewModel.getSelectedCharacter().collectAsState(initial = null)

    // Current editable stats
    var strength by remember { mutableStateOf(character?.strength?.toString() ?: "10") }
    var dexterity by remember { mutableStateOf(character?.dexterity?.toString() ?: "10") }
    var constitution by remember { mutableStateOf(character?.constitution?.toString() ?: "10") }
    var intelligence by remember { mutableStateOf(character?.intelligence?.toString() ?: "10") }
    var wisdom by remember { mutableStateOf(character?.wisdom?.toString() ?: "10") }
    var charisma by remember { mutableStateOf(character?.charisma?.toString() ?: "10") }

    // Level up state
    var showLevelUpDialog by remember { mutableStateOf(false) }
    var levelsToAdd by remember { mutableStateOf(1) }
    var showLevelUpWizard by remember { mutableStateOf(false) }
    var levelUpSteps by remember { mutableStateOf<List<LevelUpStep>>(emptyList()) }
    var currentStepIndex by remember { mutableStateOf(0) }

    // Load data
    val raceInfo by viewModel.getRaceById(character?.raceId ?: 0).collectAsState(null)
    val classInfo by viewModel.getClassById(character?.classId ?: 0).collectAsState(null)
    val backgroundInfo by viewModel.getBackgroundById(character?.backgroundId ?: 0).collectAsState(null)
    val alignmentInfo by viewModel.getAlignmentById(character?.alignmentId ?: 0).collectAsState(null)

    // Load current subclass
    val currentSubclass by viewModel.getSubclassById(character?.subclassId ?: 0).collectAsState(null)

    // State for wizard choices
    var selectedSubclass by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var featChoices by remember { mutableStateOf<Map<Int, Int?>>(emptyMap()) } // level -> featId or null for ASI
    var asiChoices by remember { mutableStateOf<Map<Int, Map<String, Int>>>(emptyMap()) } // level -> ability increases

    // Calculate HP for level up preview
    val levelUpPreview = remember(character, classInfo, levelsToAdd) {
        calculateLevelUpPreview(character, classInfo, levelsToAdd)
    }

    // Coroutine scope for suspend functions
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Level Up Button
                ExtendedFloatingActionButton(
                    onClick = { showLevelUpDialog = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Level Up") },
                    text = { Text("Level Up") },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )

                // Save Button
                FloatingActionButton(
                    onClick = {
                        character?.let { char ->
                            // Update stats
                            viewModel.updateCharacterStats(
                                char.characterId,
                                strength.toIntOrNull() ?: 10,
                                dexterity.toIntOrNull() ?: 10,
                                constitution.toIntOrNull() ?: 10,
                                intelligence.toIntOrNull() ?: 10,
                                wisdom.toIntOrNull() ?: 10,
                                charisma.toIntOrNull() ?: 10
                            )
                            navController.popBackStack()
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Save")
                }
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
            Text("Character Details", style = MaterialTheme.typography.headlineMedium)

            if (character != null) {
                // Basic Information Section (Read-only)
                BasicInfoReadOnlySection(
                    character = character!!,
                    raceInfo = raceInfo,
                    classInfo = classInfo,
                    backgroundInfo = backgroundInfo,
                    alignmentInfo = alignmentInfo,
                    currentSubclass = currentSubclass
                )

                // Current Level and Stats
                CurrentLevelAndStatsSection(
                    character = character!!,
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
                    onCharismaChange = { charisma = it }
                )

                // Level Up Preview (if dialog is shown)
                if (showLevelUpDialog) {
                    LevelUpPreviewSection(
                        character = character!!,
                        classInfo = classInfo,
                        levelsToAdd = levelsToAdd,
                        onLevelsToAddChange = { levelsToAdd = it },
                        levelUpPreview = levelUpPreview,
                        onStartLevelUp = {
                            // Launch coroutine to call suspend function
                            coroutineScope.launch {
                                prepareLevelUpWizard(
                                    character = character!!,
                                    classInfo = classInfo,
                                    levelsToAdd = levelsToAdd,
                                    viewModel = viewModel,
                                    onReady = { steps ->
                                        levelUpSteps = steps
                                        showLevelUpWizard = true
                                        currentStepIndex = 0
                                    }
                                )
                            }
                        },
                        onCancel = { showLevelUpDialog = false }
                    )
                }
            } else {
                Text("Loading character...")
            }
        }
    }

    // Level Up Wizard
    if (showLevelUpWizard && currentStepIndex < levelUpSteps.size) {
        val currentStep = levelUpSteps[currentStepIndex]

        AlertDialog(
            onDismissRequest = { showLevelUpWizard = false },
            title = { Text("Level ${currentStep.level}: ${currentStep.title}") },
            text = {
                LevelUpStepContent(
                    step = currentStep,
                    character = character!!,
                    classInfo = classInfo,
                    viewModel = viewModel,
                    selectedSubclass = selectedSubclass,
                    onSubclassSelected = { selectedSubclass = it },
                    featChoices = featChoices,
                    onFeatSelected = { level, featId ->
                        featChoices = featChoices + (level to featId)
                        asiChoices = asiChoices - level // Clear ASI if feat selected
                    },
                    asiChoices = asiChoices,
                    onAsiSelected = { level, ability, value ->
                        val current = asiChoices[level] ?: emptyMap()
                        asiChoices = asiChoices + (level to (current + (ability to value)))
                        featChoices = featChoices - level // Clear feat if ASI selected
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Validate step
                        if (validateStep(
                                currentStep,
                                selectedSubclass,
                                featChoices,
                                asiChoices
                            )) {
                            if (currentStepIndex >= levelUpSteps.size - 1) {
                                // Process all level ups
                                processLevelUps(
                                    character!!,
                                    viewModel,
                                    levelUpSteps,
                                    selectedSubclass,
                                    featChoices,
                                    asiChoices,
                                    levelUpPreview
                                )
                                showLevelUpWizard = false
                                // Refresh character data
                                viewModel.selectCharacter(characterId)
                            } else {
                                currentStepIndex++
                            }
                        }
                    }
                ) {
                    Text(if (currentStepIndex >= levelUpSteps.size - 1) "Finish" else "Next")
                }
            },
            dismissButton = {
                Button(onClick = { showLevelUpWizard = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Data classes for level up steps
sealed class LevelUpStep(
    val level: Int,
    val title: String,
    val type: LevelUpType
) {
    data class SubclassStep(
        val subclassLevel: Int,
        val availableSubclasses: List<Pair<Int, String>>
    ) : LevelUpStep(subclassLevel, "Choose Subclass", LevelUpType.SUBCLASS)

    data class FeatStep(
        val featLevel: Int,
        val availableFeats: List<table_entities.Feature>
    ) : LevelUpStep(featLevel, "Feat or Ability Score Improvement", LevelUpType.FEAT)

    data class ClassFeatureStep(
        val featureLevel: Int,
        val features: List<table_entities.Feature>
    ) : LevelUpStep(featureLevel, "Class Features", LevelUpType.CLASS_FEATURE)
}

enum class LevelUpType {
    SUBCLASS, FEAT, CLASS_FEATURE
}

// Calculate level up preview
private fun calculateLevelUpPreview(
    character: Character?,
    classInfo: table_entities.CharacterClass?,
    levelsToAdd: Int
): LevelUpPreview {
    if (character == null || classInfo == null) return LevelUpPreview(0, 0, emptyList())

    val currentLevel = character.level
    val newLevel = currentLevel + levelsToAdd

    // Calculate HP gain
    val hitDie = classInfo.hitDie.toInt()
    val conMod = (character.constitution - 10) / 2
    var totalHPGain = 0

    // For each level gained, add hit die + CON mod
    for (i in 1..levelsToAdd) {
        // Using average: (hitDie / 2) + 1 rounded up
        val levelHP = Math.ceil(hitDie / 2.0).toInt() + 1 + conMod
        totalHPGain += levelHP
    }

    // Determine what choices need to be made
    val choicesNeeded = mutableListOf<Int>()

    for (level in (currentLevel + 1)..newLevel) {
        when {
            level == 3 && character.subclassId == 0 -> choicesNeeded.add(level)
            level in listOf(4, 8, 12, 16, 19) -> choicesNeeded.add(level)
        }
    }

    return LevelUpPreview(
        newLevel = newLevel,
        hpGain = totalHPGain,
        choicesNeeded = choicesNeeded
    )
}

data class LevelUpPreview(
    val newLevel: Int,
    val hpGain: Int,
    val choicesNeeded: List<Int>
)

// Prepare level up wizard - suspend function
private suspend fun prepareLevelUpWizard(
    character: Character,
    classInfo: table_entities.CharacterClass?,
    levelsToAdd: Int,
    viewModel: CharacterViewModel,
    onReady: (List<LevelUpStep>) -> Unit
) {
    val currentLevel = character.level
    val newLevel = currentLevel + levelsToAdd
    val steps = mutableListOf<LevelUpStep>()

    // For each level from current+1 to newLevel
    for (level in (currentLevel + 1)..newLevel) {
        when {
            // Subclass at level 3
            level == 3 && character.subclassId == 0 -> {
                val subclasses = viewModel.getSubclassesForClass(character.classId).first()
                steps.add(LevelUpStep.SubclassStep(
                    level,
                    subclasses.map { it.subclassId to it.subclassName }
                ))
            }

            // Feat/ASI at specific levels
            level in listOf(4, 8, 12, 16, 19) -> {
                val feats = viewModel.getAvailableFeats(level, character.classId).first()
                steps.add(LevelUpStep.FeatStep(level, feats))
            }
        }
    }

    onReady(steps)
}

// Composable for level up preview
@Composable
fun LevelUpPreviewSection(
    character: Character,
    classInfo: table_entities.CharacterClass?,
    levelsToAdd: Int,
    onLevelsToAddChange: (Int) -> Unit,
    levelUpPreview: LevelUpPreview,
    onStartLevelUp: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0x2200FF00))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Level Up", style = MaterialTheme.typography.headlineSmall)

            // Levels to add selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Levels to add:")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { if (levelsToAdd > 1) onLevelsToAddChange(levelsToAdd - 1) },
                        enabled = levelsToAdd > 1
                    ) {
                        Text("-")
                    }
                    Text(
                        text = levelsToAdd.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.width(40.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Button(
                        onClick = { if (character.level + levelsToAdd < 20) onLevelsToAddChange(levelsToAdd + 1) },
                        enabled = character.level + levelsToAdd < 20
                    ) {
                        Text("+")
                    }
                }
            }

            // Preview information
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0x22000000))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Level Up Preview:", fontWeight = FontWeight.Bold)
                    Text("Current Level: ${character.level}")
                    Text("New Level: ${levelUpPreview.newLevel}")
                    Text("HP Gain: +${levelUpPreview.hpGain}")
                    Text("New Max HP: ${character.hitPointMax + levelUpPreview.hpGain}")

                    if (levelUpPreview.choicesNeeded.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Choices to make:", fontWeight = FontWeight.Bold)
                        levelUpPreview.choicesNeeded.forEach { level ->
                            when {
                                level == 3 -> Text("• Level 3: Choose subclass")
                                level in listOf(4, 8, 12, 16, 19) -> Text("• Level $level: Feat or Ability Score Improvement")
                            }
                        }
                    } else {
                        Text("No choices needed for these levels")
                    }
                }
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = onStartLevelUp,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Start Level Up")
                }
            }
        }
    }
}

// Composable for level up step content
@Composable
fun LevelUpStepContent(
    step: LevelUpStep,
    character: Character,
    classInfo: table_entities.CharacterClass?,
    viewModel: CharacterViewModel,
    selectedSubclass: Pair<Int, String>?,
    onSubclassSelected: (Pair<Int, String>) -> Unit,
    featChoices: Map<Int, Int?>,
    onFeatSelected: (Int, Int?) -> Unit,
    asiChoices: Map<Int, Map<String, Int>>,
    onAsiSelected: (Int, String, Int) -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (step) {
            is LevelUpStep.SubclassStep -> {
                Text("Choose your subclass:")
                step.availableSubclasses.forEach { subclass ->
                    SubclassOptionItem(
                        subclass = subclass,
                        selected = selectedSubclass?.first == subclass.first,
                        onSelected = { onSubclassSelected(subclass) }
                    )
                }
            }

            is LevelUpStep.FeatStep -> {
                Text("Choose one:")

                // ASI Option
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onFeatSelected(step.featLevel, null) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (featChoices[step.featLevel] == null)
                            MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Ability Score Improvement", fontWeight = FontWeight.Bold)
                        Text("Increase one ability score by 2, or two ability scores by 1")

                        // ASI selection if chosen
                        if (featChoices[step.featLevel] == null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            AsiSelection(
                                level = step.featLevel,
                                asiChoices = asiChoices[step.featLevel] ?: emptyMap(),
                                onAsiSelected = onAsiSelected,
                                maxPoints = 2
                            )
                        }
                    }
                }

                // Feat Options
                step.availableFeats.forEach { feat ->
                    FeatOptionItem(
                        feat = feat,
                        selected = featChoices[step.featLevel] == feat.featureId,
                        onSelected = { onFeatSelected(step.featLevel, feat.featureId) }
                    )
                }
            }

            is LevelUpStep.ClassFeatureStep -> {
                Text("You gain the following features:")
                step.features.forEach { feature ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(feature.featureName, fontWeight = FontWeight.Bold)
                            Text(feature.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

// ASI Selection Component
@Composable
fun AsiSelection(
    level: Int,
    asiChoices: Map<String, Int>,
    onAsiSelected: (Int, String, Int) -> Unit,
    maxPoints: Int
) {
    val usedPoints = asiChoices.values.sum()
    val remainingPoints = maxPoints - usedPoints

    Column {
        Text("Remaining points: $remainingPoints", fontWeight = FontWeight.Bold)

        // Ability score options
        listOf("STR", "DEX", "CON", "INT", "WIS", "CHA").forEach { ability ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(ability)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // - button
                    Button(
                        onClick = {
                            val current = asiChoices[ability] ?: 0
                            if (current > 0) {
                                onAsiSelected(level, ability, current - 1)
                            }
                        },
                        enabled = (asiChoices[ability] ?: 0) > 0
                    ) {
                        Text("-")
                    }

                    // Current value
                    Text(
                        text = "${asiChoices[ability] ?: 0}",
                        modifier = Modifier.width(24.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    // + button
                    Button(
                        onClick = {
                            val current = asiChoices[ability] ?: 0
                            if (current < 2 && remainingPoints > 0) {
                                onAsiSelected(level, ability, current + 1)
                            }
                        },
                        enabled = (asiChoices[ability] ?: 0) < 2 && remainingPoints > 0
                    ) {
                        Text("+")
                    }
                }
            }
        }
    }
}

// Validate step
private fun validateStep(
    step: LevelUpStep,
    selectedSubclass: Pair<Int, String>?,
    featChoices: Map<Int, Int?>,
    asiChoices: Map<Int, Map<String, Int>>
): Boolean {
    return when (step) {
        is LevelUpStep.SubclassStep -> selectedSubclass != null
        is LevelUpStep.FeatStep -> {
            val choice = featChoices[step.featLevel]
            if (choice == null) {
                // ASI selected, check points
                val asi = asiChoices[step.featLevel] ?: emptyMap()
                asi.values.sum() == 2
            } else {
                // Feat selected
                true
            }
        }
        is LevelUpStep.ClassFeatureStep -> true
    }
}

// Process all level ups
private fun processLevelUps(
    character: Character,
    viewModel: CharacterViewModel,
    steps: List<LevelUpStep>,
    selectedSubclass: Pair<Int, String>?,
    featChoices: Map<Int, Int?>,
    asiChoices: Map<Int, Map<String, Int>>,
    preview: LevelUpPreview
) {
    // First, apply ASI choices to update ability scores
    asiChoices.forEach { (level, asi) ->
        var newStr = character.strength
        var newDex = character.dexterity
        var newCon = character.constitution
        var newInt = character.intelligence
        var newWis = character.wisdom
        var newCha = character.charisma

        asi.forEach { (ability, increase) ->
            when (ability) {
                "STR" -> newStr += increase
                "DEX" -> newDex += increase
                "CON" -> newCon += increase
                "INT" -> newInt += increase
                "WIS" -> newWis += increase
                "CHA" -> newCha += increase
            }
        }

        // Update character stats
        viewModel.updateCharacterStats(
            character.characterId,
            newStr,
            newDex,
            newCon,
            newInt,
            newWis,
            newCha
        )
    }

    // Update level and HP
    viewModel.updateCharacterLevel(character, preview.newLevel)

    // Apply subclass if chosen
    selectedSubclass?.let { subclass ->
        viewModel.updateCharacterSubclass(character, subclass.first)
    }

    // Apply feat choices
    featChoices.forEach { (level, featId) ->
        featId?.let {
            viewModel.addFeatureToCharacter(character.characterId, it)
        }
    }
}

// Basic Information Section (Read-only) - Updated with subclass
@Composable
fun BasicInfoReadOnlySection(
    character: Character,
    raceInfo: table_entities.Race?,
    classInfo: table_entities.CharacterClass?,
    backgroundInfo: table_entities.Background?,
    alignmentInfo: table_entities.Alignment?,
    currentSubclass: table_entities.Subclass?
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

            // Character Name
            Text("Name: ${character.characterName}", fontWeight = FontWeight.Bold)

            // Race
            raceInfo?.let {
                Text("Race: ${it.raceName}")
                Text("Speed: ${it.speed} ft")
                Text("Size: ${it.size}")
            }

            // Class
            classInfo?.let {
                Text("Class: ${it.className}")
                Text("Hit Die: d${it.hitDie}")
            }

            // Subclass if exists
            currentSubclass?.let {
                Text("Subclass: ${it.subclassName}")
            }

            // Background
            backgroundInfo?.let {
                Text("Background: ${it.backgroundName}")
            }

            // Alignment
            alignmentInfo?.let {
                Text("Alignment: ${it.alignmentName}")
            }
        }
    }
}

// Current Level and Stats Section
@Composable
fun CurrentLevelAndStatsSection(
    character: Character,
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
    onCharismaChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0x22000000))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Current Status", style = MaterialTheme.typography.headlineSmall)

            // Current Level
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0x220000FF))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Level:")
                    Text(character.level.toString(), style = MaterialTheme.typography.headlineMedium)
                }
            }

            // HP Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0x22FF0000))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Hit Points", fontWeight = FontWeight.Bold)
                    Text("Current: ${character.currentHitPoints}")
                    Text("Maximum: ${character.hitPointMax}")
                    Text("Temporary: ${character.temporaryHitPoints}")
                }
            }

            // Stats Input
            Text("Ability Scores:", style = MaterialTheme.typography.titleSmall)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DetailsStatFieldWithModifier("STR", strength, onStrengthChange, Modifier.weight(1f))
                DetailsStatFieldWithModifier("DEX", dexterity, onDexterityChange, Modifier.weight(1f))
                DetailsStatFieldWithModifier("CON", constitution, onConstitutionChange, Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DetailsStatFieldWithModifier("INT", intelligence, onIntelligenceChange, Modifier.weight(1f))
                DetailsStatFieldWithModifier("WIS", wisdom, onWisdomChange, Modifier.weight(1f))
                DetailsStatFieldWithModifier("CHA", charisma, onCharismaChange, Modifier.weight(1f))
            }
        }
    }
}

// Subclass Option Item
@Composable
fun SubclassOptionItem(
    subclass: Pair<Int, String>,
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
        Column(modifier = Modifier.padding(12.dp)) {
            Text(subclass.second, fontWeight = FontWeight.Bold)
        }
    }
}

// Feat Option Item
@Composable
fun FeatOptionItem(
    feat: table_entities.Feature,
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
        Column(modifier = Modifier.padding(12.dp)) {
            Text(feat.featureName, fontWeight = FontWeight.Bold)
            Text(feat.description, style = MaterialTheme.typography.bodySmall)
            Text("Level Required: ${feat.levelRequirement}",
                style = MaterialTheme.typography.bodySmall)
        }
    }
}

// Stat Field with Modifier - RENAMED to avoid conflict
@Composable
fun DetailsStatFieldWithModifier(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || (newValue.toIntOrNull() != null && newValue.toInt() in 1..30)) {
                    onChange(newValue)
                }
            },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        val currentValue = value.toIntOrNull() ?: 10
        Text(
            text = "Mod: ${if (currentValue >= 10) "+" else ""}${(currentValue - 10) / 2}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}