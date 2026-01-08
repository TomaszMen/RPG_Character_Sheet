package com.example.rpg_character_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import table_entities.*
import java.util.*
import kotlin.math.floor

// Local DiceRollResult for this screen only
data class LocalDiceRollResult(
    val label: String,
    val diceType: String,
    val roll: Int,
    val modifier: Int,
    val total: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterPlayScreen(viewModel: CharacterViewModel, navController: NavHostController) {
    val character by viewModel.getSelectedCharacter().collectAsState(initial = null)
    val skills by viewModel.getAllSkills().collectAsState(initial = emptyList())
    val characterSkills by viewModel.characterSkillsState.collectAsState(initial = emptyList())
    val savingThrows by viewModel.characterSavingThrowsState.collectAsState(initial = emptyList())

    // State for dice roll dialog
    var showDiceRollDialog by remember { mutableStateOf(false) }
    var diceRollResult by remember { mutableStateOf<LocalDiceRollResult?>(null) }

    // State for HP editing
    var showHPEditDialog by remember { mutableStateOf(false) }
    var currentHPInput by remember { mutableStateOf("") }
    var tempHPInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    character?.let {
                        Text(
                            text = it.characterName,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    } ?: Text("Character Sheet")
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            character?.let { char ->
                // Quick Stats Row (HP, AC, Initiative, Speed)
                QuickStatsRow(
                    character = char,
                    onHPClick = {
                        currentHPInput = char.currentHitPoints.toString()
                        tempHPInput = char.temporaryHitPoints.toString()
                        showHPEditDialog = true
                    },
                    onACClick = {
                        // AC should not be rolled - it's a static value
                        // Show AC details instead
                    },
                    onInitiativeClick = {
                        val dexMod = calculateAbilityModifier(char.dexterity)
                        val roll = rollD20()
                        diceRollResult = LocalDiceRollResult(
                            label = "Initiative",
                            diceType = "d20",
                            roll = roll,
                            modifier = dexMod,
                            total = roll + dexMod
                        )
                        showDiceRollDialog = true
                    }
                )

                // Main Content with ability scores and skills
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    // Left Column: Ability Scores and Saving Throws (scrollable)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    ) {
                        // Ability Scores
                        Text(
                            text = "Ability Scores",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        AbilityScoresSection(
                            character = char,
                            onClick = { abilityName, abilityValue, proficiency ->
                                val abilityMod = calculateAbilityModifier(abilityValue)
                                val profBonus = if (proficiency) calculateProficiencyBonus(char.level) else 0
                                val roll = rollD20()
                                diceRollResult = LocalDiceRollResult(
                                    label = "$abilityName Check",
                                    diceType = "d20",
                                    roll = roll,
                                    modifier = abilityMod + profBonus,
                                    total = roll + abilityMod + profBonus
                                )
                                showDiceRollDialog = true
                            },
                            savingThrows = savingThrows
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Saving Throws
                        Text(
                            text = "Saving Throws",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        SavingThrowsSection(
                            character = char,
                            savingThrows = savingThrows,
                            onClick = { abilityName, abilityValue, proficiency ->
                                val abilityMod = calculateAbilityModifier(abilityValue)
                                val profBonus = if (proficiency) calculateProficiencyBonus(char.level) else 0
                                val roll = rollD20()
                                diceRollResult = LocalDiceRollResult(
                                    label = "$abilityName Saving Throw",
                                    diceType = "d20",
                                    roll = roll,
                                    modifier = abilityMod + profBonus,
                                    total = roll + abilityMod + profBonus
                                )
                                showDiceRollDialog = true
                            }
                        )
                    }

                    // Right Column: Skills
                    Column(
                        modifier = Modifier
                            .weight(1.5f)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "Skills",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        SkillsSection(
                            character = char,
                            allSkills = skills,
                            characterSkills = characterSkills,
                            onClick = { skillName, abilityMod, proficiency, expertise ->
                                val profBonus = calculateProficiencyBonus(char.level)
                                val totalBonus = abilityMod +
                                        if (expertise) profBonus * 2 else if (proficiency) profBonus else 0
                                val roll = rollD20()
                                diceRollResult = LocalDiceRollResult(
                                    label = "$skillName Check",
                                    diceType = "d20",
                                    roll = roll,
                                    modifier = totalBonus,
                                    total = roll + totalBonus
                                )
                                showDiceRollDialog = true
                            }
                        )
                    }
                }

                // HP Edit Dialog
                if (showHPEditDialog) {
                    AlertDialog(
                        onDismissRequest = { showHPEditDialog = false },
                        title = { Text("Edit Hit Points") },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = currentHPInput,
                                    onValueChange = { currentHPInput = it },
                                    label = { Text("Current HP") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = tempHPInput,
                                    onValueChange = { tempHPInput = it },
                                    label = { Text("Temporary HP") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                Text(
                                    text = "Max HP: ${char.hitPointMax}",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    val currentHP = currentHPInput.toIntOrNull() ?: char.currentHitPoints
                                    val tempHP = tempHPInput.toIntOrNull() ?: char.temporaryHitPoints
                                    viewModel.updateCurrentHP(char.characterId, currentHP)
                                    viewModel.updateTemporaryHP(char.characterId, tempHP)
                                    showHPEditDialog = false
                                }
                            ) {
                                Text("Save")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showHPEditDialog = false }
                            ) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                // Dice Roll Dialog
                if (showDiceRollDialog) {
                    diceRollResult?.let { result ->
                        AlertDialog(
                            onDismissRequest = { showDiceRollDialog = false },
                            title = { Text("${result.label} Roll") },
                            text = {
                                DiceRollResultDisplay(result = result)
                            },
                            confirmButton = {
                                Button(
                                    onClick = { showDiceRollDialog = false }
                                ) {
                                    Text("Close")
                                }
                            }
                        )
                    }
                }
            } ?: run {
                // Show loading or empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No character selected")
                }
            }
        }
    }
}

@Composable
fun QuickStatsRow(
    character: Character,
    onHPClick: () -> Unit,
    onACClick: () -> Unit,
    onInitiativeClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // HP - click to edit
            QuickStatItem(
                label = "HP",
                value = "${character.currentHitPoints}/${character.hitPointMax}" +
                        if (character.temporaryHitPoints > 0) " (+${character.temporaryHitPoints})" else "",
                color = Color(0xFFFF6B6B),
                onClick = onHPClick
            )

            // AC - static value, no click
            QuickStatItem(
                label = "AC",
                value = character.armorClass.toString(),
                color = Color(0xFF4ECDC4),
                onClick = {} // No click for AC
            )

            // Initiative - click to roll
            QuickStatItem(
                label = "Initiative",
                value = if (character.initiative >= 0) "+${character.initiative}" else character.initiative.toString(),
                color = Color(0xFF45B7D1),
                onClick = onInitiativeClick
            )

            // Speed - static value
            QuickStatItem(
                label = "Speed",
                value = "${character.speed} ft",
                color = Color(0xFF96CEB4),
                onClick = {}
            )

            // Level - static value
            QuickStatItem(
                label = "Level",
                value = character.level.toString(),
                color = Color(0xFFFFEAA7),
                onClick = {}
            )
        }
    }
}

@Composable
fun QuickStatItem(
    label: String,
    value: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                enabled = onClick != {},
                onClick = onClick
            )
            .padding(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun AbilityScoresSection(
    character: Character,
    onClick: (String, Int, Boolean) -> Unit,
    savingThrows: List<CharacterSavingThrow>
) {
    val abilityScores = listOf(
        Triple("STR", character.strength, savingThrows.any { it.abilityScoreId == 1 && it.proficiency > 0 }),
        Triple("DEX", character.dexterity, savingThrows.any { it.abilityScoreId == 2 && it.proficiency > 0 }),
        Triple("CON", character.constitution, savingThrows.any { it.abilityScoreId == 3 && it.proficiency > 0 }),
        Triple("INT", character.intelligence, savingThrows.any { it.abilityScoreId == 4 && it.proficiency > 0 }),
        Triple("WIS", character.wisdom, savingThrows.any { it.abilityScoreId == 5 && it.proficiency > 0 }),
        Triple("CHA", character.charisma, savingThrows.any { it.abilityScoreId == 6 && it.proficiency > 0 })
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.heightIn(max = 300.dp) // Limit height for scrolling
    ) {
        items(abilityScores) { (abilityName, value, hasProficiency) ->
            AbilityScoreItem(
                abilityName = abilityName,
                abilityValue = value,
                hasProficiency = hasProficiency,
                onClick = { onClick(abilityName, value, false) }
            )
        }
    }
}

@Composable
fun AbilityScoreItem(
    abilityName: String,
    abilityValue: Int,
    hasProficiency: Boolean,
    onClick: () -> Unit
) {
    val abilityMod = calculateAbilityModifier(abilityValue)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasProficiency)
                MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = abilityName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Modifier: ${if (abilityMod >= 0) "+" else ""}$abilityMod",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = abilityValue.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (hasProficiency) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            "PROF",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SavingThrowsSection(
    character: Character,
    savingThrows: List<CharacterSavingThrow>,
    onClick: (String, Int, Boolean) -> Unit
) {
    val abilityMapping = listOf(
        Triple(1, "STR", character.strength),
        Triple(2, "DEX", character.dexterity),
        Triple(3, "CON", character.constitution),
        Triple(4, "INT", character.intelligence),
        Triple(5, "WIS", character.wisdom),
        Triple(6, "CHA", character.charisma)
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.heightIn(max = 200.dp) // Limit height for scrolling
    ) {
        items(abilityMapping) { (abilityId, abilityName, value) ->
            val hasProficiency = savingThrows.any {
                it.abilityScoreId == abilityId && it.proficiency > 0
            }
            val abilityMod = calculateAbilityModifier(value)
            val profBonus = if (hasProficiency) calculateProficiencyBonus(character.level) else 0
            val totalBonus = abilityMod + profBonus

            SavingThrowItem(
                abilityName = abilityName,
                totalBonus = totalBonus,
                hasProficiency = hasProficiency,
                onClick = { onClick(abilityName, value, hasProficiency) }
            )
        }
    }
}

@Composable
fun SavingThrowItem(
    abilityName: String,
    totalBonus: Int,
    hasProficiency: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasProficiency)
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (hasProficiency) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Proficient",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = abilityName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = if (totalBonus >= 0) "+$totalBonus" else totalBonus.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SkillsSection(
    character: Character,
    allSkills: List<Skill>,
    characterSkills: List<CharacterSkill>,
    onClick: (String, Int, Boolean, Boolean) -> Unit
) {
    val abilityMapping = mapOf(
        "STR" to character.strength,
        "DEX" to character.dexterity,
        "CON" to character.constitution,
        "INT" to character.intelligence,
        "WIS" to character.wisdom,
        "CHA" to character.charisma
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(allSkills) { skill ->
            val characterSkill = characterSkills.firstOrNull { it.skillId == skill.skillId }
            val proficiency = characterSkill?.proficiency ?: 0
            val expertise = characterSkill?.expertise ?: 0
            val abilityMod = abilityMapping[skill.abilityScore.name]?.let {
                calculateAbilityModifier(it)
            } ?: 0
            val profBonus = calculateProficiencyBonus(character.level)
            val totalBonus = abilityMod +
                    if (expertise > 0) profBonus * 2 else if (proficiency > 0) profBonus else 0

            SkillItem(
                skillName = skill.skillName,
                abilityName = skill.abilityScore.name,
                totalBonus = totalBonus,
                hasProficiency = proficiency > 0,
                hasExpertise = expertise > 0,
                onClick = {
                    onClick(skill.skillName, abilityMod, proficiency > 0, expertise > 0)
                }
            )
        }
    }
}

@Composable
fun SkillItem(
    skillName: String,
    abilityName: String,
    totalBonus: Int,
    hasProficiency: Boolean,
    hasExpertise: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                hasExpertise -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                hasProficiency -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = skillName,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "($abilityName)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (hasExpertise) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                    ) {
                        Text(
                            "EXP",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                } else if (hasProficiency) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                    ) {
                        Text(
                            "PROF",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }

                Text(
                    text = if (totalBonus >= 0) "+$totalBonus" else totalBonus.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DiceRollResultDisplay(
    result: LocalDiceRollResult
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Dice animation/icon (simplified)
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = result.roll.toString(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Rolled: ${result.diceType}",
            style = MaterialTheme.typography.bodyMedium
        )

        // Modifier breakdown
        if (result.modifier != 0) {
            Text(
                text = "Modifier: ${if (result.modifier > 0) "+" else ""}${result.modifier}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Total
        Text(
            text = "Total:",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = result.total.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Success indication (for DC checks) - optional
        if (result.total >= 10) { // Simple example
            Text(
                text = "✓ Success",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Green
            )
        }
    }
}

// Funkcje pomocnicze
fun calculateAbilityModifier(score: Int): Int {
    return floor((score - 10) / 2.0).toInt()
}

fun calculateProficiencyBonus(level: Int): Int {
    return when (level) {
        in 1..4 -> 2
        in 5..8 -> 3
        in 9..12 -> 4
        in 13..16 -> 5
        in 17..20 -> 6
        else -> 2
    }
}

fun rollD20(): Int {
    return Random().nextInt(20) + 1
}