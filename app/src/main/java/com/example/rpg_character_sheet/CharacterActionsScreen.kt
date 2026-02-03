package com.example.rpg_character_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import table_entities.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.flow.first

@Composable
fun CharacterActionsScreen(viewModel: CharacterViewModel, navController: NavHostController) {
    val character by viewModel.getSelectedCharacter().collectAsState(initial = null)
    val weapons by viewModel.getCharacterWeapons(character?.characterId ?: 0).collectAsState(initial = emptyList())
    val spells by viewModel.getCharacterSpells(character?.characterId ?: 0).collectAsState(initial = emptyList())
    val features by viewModel.getCharacterFeatures(character?.characterId ?: 0).collectAsState(initial = emptyList())
    val spellSlots by viewModel.getCharacterSpellSlots(character?.characterId ?: 0).collectAsState(initial = emptyList())

    // State for adding/removing spells
    var showSpellSelectionDialog by remember { mutableStateOf(false) }
    var availableSpells by remember { mutableStateOf<List<Spell>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // State for spell slot management
    var showSpellSlotManager by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Actions",
                    style = MaterialTheme.typography.headlineMedium
                )

                // Spell management buttons
                Row {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                character?.let { char ->
                                    // Get available spells for this character's class
                                    val classSpellsFlow = viewModel.getClassSpells(char.classId)
                                    val classSpells = classSpellsFlow.first()
                                    val spellIds = classSpells.map { it.spellId }
                                    val spellsFlow = viewModel.getSpellsByIds(spellIds)
                                    val allSpells = spellsFlow.first()

                                    // Filter out spells the character already has
                                    val currentSpells = viewModel.getCharacterSpells(char.characterId).first()
                                    val currentSpellIds = currentSpells.map { it.spellId }

                                    availableSpells = allSpells.filter { spell ->
                                        !currentSpellIds.contains(spell.spellId)
                                    }
                                    showSpellSelectionDialog = true
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Spell")
                    }

                    IconButton(
                        onClick = { showSpellSlotManager = true }
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Manage Spell Slots")
                    }
                }
            }
        }

        // Spell Slot Tracker
        item {
            SpellSlotsTracker(
                spellSlots = spellSlots,
                onSpellSlotUsed = { spellSlotId, newUsed ->
                    viewModel.updateSpellSlotUsed(spellSlotId, newUsed)
                },
                character = character
            )
        }

        // Attacks Section
        item {
            Text(
                text = "Attacks",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (weapons.isEmpty()) {
            item { Text("No weapons equipped", style = MaterialTheme.typography.bodyMedium) }
        } else {
            items(weapons) { weaponWithItem ->
                WeaponAttackCard(weaponWithItem, character)
            }
        }

        // Spells Section
        item {
            Text(
                text = "Spells",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (spells.isEmpty()) {
            item {
                Text(
                    "No spells known. Add spells from your class list.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            // Group spells by level inside the LazyColumn scope
            val spellsByLevel = spells.groupBy { it.spellLevel }
            spellsByLevel.forEach { (level, spellsForLevel) ->
                item {
                    SpellsLevelSection(
                        level = level,
                        spells = spellsForLevel,
                        character = character,
                        onCastSpell = { spell ->
                            // Handle spell casting logic
                            // This would deduct spell slots, etc.
                        },
                        onRemoveSpell = { spell ->
                            coroutineScope.launch {
                                character?.let { char ->
                                    // Remove spell from character
                                    // This requires additional implementation
                                }
                            }
                        }
                    )
                }
            }
        }

        // Features Section
        item {
            Text(
                text = "Features & Abilities",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color.Green,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (features.isEmpty()) {
            item { Text("No features available", style = MaterialTheme.typography.bodyMedium) }
        } else {
            items(features) { feature ->
                FeatureCard(feature)
            }
        }
    }

    // Spell Selection Dialog
    if (showSpellSelectionDialog) {
        AlertDialog(
            onDismissRequest = { showSpellSelectionDialog = false },
            title = { Text("Add Spell") },
            text = {
                if (availableSpells.isEmpty()) {
                    Text("No spells available for your class.")
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 400.dp)
                    ) {
                        items(availableSpells) { spell ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        coroutineScope.launch {
                                            character?.let { char ->
                                                viewModel.addSpellToCharacter(char.characterId, spell.spellId)
                                                showSpellSelectionDialog = false
                                            }
                                        }
                                    }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = spell.spellName,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Level ${spell.spellLevel} - ${spell.school}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    if (spell.description.length > 100) {
                                        Text(
                                            text = spell.description.substring(0, 100) + "...",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    } else {
                                        Text(
                                            text = spell.description,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSpellSelectionDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Spell Slot Manager Dialog
    if (showSpellSlotManager && character != null) {
        SpellSlotManagerDialog(
            character = character!!,
            spellSlots = spellSlots,
            onDismiss = { showSpellSlotManager = false },
            onSave = { updatedSlots ->
                coroutineScope.launch {
                    updatedSlots.forEach { slot ->
                        viewModel.updateSpellSlotUsed(slot.spellSlotId, slot.usedSlots)
                    }
                    showSpellSlotManager = false
                }
            }
        )
    }
}

@Composable
fun WeaponAttackCard(weaponAndItem: WeaponAndItem, character: Character?) {
    val weapon = weaponAndItem.weapon
    val item = weaponAndItem.item

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x22FF0000))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.itemName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                // Attack button
                Button(
                    onClick = {
                        // Roll attack
                        // Calculate attack bonus based on character stats
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Text("⚔️")
                }
            }

            // Attack details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("To Hit", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = "+${calculateAttackBonus(character, weapon)}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Damage", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = weapon.damageDice,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Type", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = weapon.damageType.toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (!weapon.properties.isNullOrEmpty()) {
                Text(
                    text = "Properties: ${weapon.properties}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (weapon.rangeNormal != null) {
                Text(
                    text = "Range: ${weapon.rangeNormal}/${weapon.rangeLong} ft",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun SpellSlotsTracker(
    spellSlots: List<CharacterSpellSlot>,
    onSpellSlotUsed: (Int, Int) -> Unit,
    character: Character?
) {
    val isSpellcaster = character?.let { isCharacterSpellcaster(it.classId) } ?: false

    if (!isSpellcaster) {
        return
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x220000FF))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Spell Slots",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            // Spell slots by level
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                spellSlots.sortedBy { it.spellLevel }.forEach { slot ->
                    SpellSlotCounter(
                        slot = slot,
                        onIncrement = {
                            if (slot.usedSlots < slot.totalSlots) {
                                onSpellSlotUsed(slot.spellSlotId, slot.usedSlots + 1)
                            }
                        },
                        onDecrement = {
                            if (slot.usedSlots > 0) {
                                onSpellSlotUsed(slot.spellSlotId, slot.usedSlots - 1)
                            }
                        }
                    )
                }
            }

            // Spellcasting ability
            character?.let {
                val spellcastingAbility = getSpellcastingAbility(it.classId)
                Text(
                    text = "Spellcasting Ability: $spellcastingAbility",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun SpellSlotCounter(
    slot: CharacterSpellSlot,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = "Lvl ${slot.spellLevel}",
            style = MaterialTheme.typography.labelSmall
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDecrement,
                modifier = Modifier.size(24.dp),
                enabled = slot.usedSlots > 0
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Decrement",
                    modifier = Modifier.size(16.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (slot.usedSlots > 0) Color.Blue else Color.LightGray,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${slot.usedSlots}/${slot.totalSlots}",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
            }

            IconButton(
                onClick = onIncrement,
                modifier = Modifier.size(24.dp),
                enabled = slot.usedSlots < slot.totalSlots
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Increment",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun SpellSlotManagerDialog(
    character: Character,
    spellSlots: List<CharacterSpellSlot>,
    onDismiss: () -> Unit,
    onSave: (List<CharacterSpellSlot>) -> Unit
) {
    val maxSpellLevel = calculateMaxSpellLevel(character.classId, character.level)
    var localSpellSlots by remember { mutableStateOf(spellSlots) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Manage Spell Slots") },
        text = {
            Column {
                Text(
                    text = "Character Level ${character.level}",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Calculate and show expected spell slots
                val expectedSlots = calculateExpectedSpellSlots(character.classId, character.level)

                Text(
                    text = "Expected spell slots for your level:",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                expectedSlots.forEach { (level, count) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Level $level:")
                        Text("$count")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = "Current Slots:",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Editable spell slots
                for (level in 1..maxSpellLevel) {
                    val slot = localSpellSlots.firstOrNull { it.spellLevel == level }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Level $level:")

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    val current = slot?.totalSlots ?: 0
                                    if (current > 0) {
                                        val newSlot = slot?.copy(totalSlots = current - 1)
                                            ?: CharacterSpellSlot(
                                                characterId = character.characterId,
                                                spellLevel = level,
                                                totalSlots = current - 1,
                                                usedSlots = 0
                                            )
                                        localSpellSlots = localSpellSlots.filter { it.spellLevel != level } + newSlot
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Decrease")
                            }

                            Text(
                                text = slot?.totalSlots?.toString() ?: "0",
                                modifier = Modifier.width(30.dp),
                                textAlign = TextAlign.Center
                            )

                            IconButton(
                                onClick = {
                                    val current = slot?.totalSlots ?: 0
                                    val newSlot = slot?.copy(totalSlots = current + 1)
                                        ?: CharacterSpellSlot(
                                            characterId = character.characterId,
                                            spellLevel = level,
                                            totalSlots = current + 1,
                                            usedSlots = 0
                                        )
                                    localSpellSlots = localSpellSlots.filter { it.spellLevel != level } + newSlot
                                }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(localSpellSlots) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SpellsLevelSection(
    level: Int,
    spells: List<Spell>,
    character: Character?,
    onCastSpell: (Spell) -> Unit,
    onRemoveSpell: (Spell) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x220000FF))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (level == 0) "Cantrips" else "Level $level Spells",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Badge(
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(spells.size.toString())
                    }
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }

            if (expanded) {
                spells.forEach { spell ->
                    SpellCard(
                        spell = spell,
                        character = character,
                        onCast = { onCastSpell(spell) },
                        onRemove = { onRemoveSpell(spell) }
                    )
                }
            }
        }
    }
}

@Composable
fun SpellCard(
    spell: Spell,
    character: Character?,
    onCast: () -> Unit,
    onRemove: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = spell.spellName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${spell.school} • ${spell.castingTime}",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Row {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.Info else Icons.Default.Info,
                            contentDescription = "Details"
                        )
                    }
                }
            }

            if (expanded) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Range: ${spell.spellRange}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Duration: ${spell.duration}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Components: ${spell.components}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (spell.concentration) {
                        Text(
                            text = "• Requires Concentration",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Red)
                        )
                    }

                    if (spell.ritual) {
                        Text(
                            text = "• Can be cast as Ritual",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Blue)
                        )
                    }

                    Text(
                        text = spell.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    spell.higherLevels?.let { higherLevels ->
                        Text(
                            text = "At Higher Levels: $higherLevels",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureCard(feature: Feature) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x2200FF00))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = feature.featureName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Source: ${feature.sourceType} • Level ${feature.levelRequirement}+",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }

            if (expanded) {
                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

// Helper functions for spellcasting logic
fun isCharacterSpellcaster(classId: Int): Boolean {
    return when (classId) {
        2, 3, 4, 10, 11, 12 -> true  // Bard, Cleric, Druid, Sorcerer, Warlock, Wizard
        5, 6, 7, 8, 9 -> {           // Fighter, Monk, Paladin, Ranger, Rogue
            // Some subclasses have spellcasting
            when (classId) {
                5 -> true  // Eldritch Knight
                6 -> true  // Four Elements Monk
                7 -> true  // Paladin
                8 -> true  // Ranger
                9 -> true  // Arcane Trickster
                else -> false
            }
        }
        else -> false
    }
}

fun getSpellcastingAbility(classId: Int): String {
    return when (classId) {
        2, 10 -> "Charisma"    // Bard, Sorcerer
        3, 4 -> "Wisdom"       // Cleric, Druid
        11 -> "Charisma"       // Warlock
        12 -> "Intelligence"   // Wizard
        5, 9 -> "Intelligence" // Eldritch Knight, Arcane Trickster
        6 -> "Wisdom"          // Monk (Four Elements)
        7 -> "Charisma"        // Paladin
        8 -> "Wisdom"          // Ranger
        else -> "None"
    }
}

fun calculateMaxSpellLevel(classId: Int, characterLevel: Int): Int {
    if (!isCharacterSpellcaster(classId)) return 0

    val maxLevel = when (classId) {
        2, 3, 4, 5, 6, 7, 8, 9, 10, 12 -> 9  // Full casters and half/third casters can eventually get 9th level
        11 -> 5  // Warlock pact magic (gets up to 5th level)
        else -> 0
    }

    return minOf(maxLevel, (characterLevel + 1) / 2)  // Simplified progression
}

fun calculateExpectedSpellSlots(classId: Int, characterLevel: Int): Map<Int, Int> {
    val slots = mutableMapOf<Int, Int>()

    when (classId) {
        // Full casters (Wizard, Cleric, Druid, Bard, Sorcerer)
        2, 3, 4, 10, 12 -> {
            when (characterLevel) {
                1 -> slots[1] = 2
                2 -> slots[1] = 3
                3 -> { slots[1] = 4; slots[2] = 2 }
                4 -> { slots[1] = 4; slots[2] = 3 }
                5 -> { slots[1] = 4; slots[2] = 3; slots[3] = 2 }
                6 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3 }
                7 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 1 }
                8 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 2 }
                9 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 3; slots[5] = 1 }
                10 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 3; slots[5] = 2 }
                11 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 3; slots[5] = 2; slots[6] = 1 }
                12 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 3; slots[5] = 2; slots[6] = 1 }
                13 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 3; slots[5] = 2; slots[6] = 1; slots[7] = 1 }
                14 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 3; slots[5] = 2; slots[6] = 1; slots[7] = 1 }
                15 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 3; slots[5] = 2; slots[6] = 1; slots[7] = 1; slots[8] = 1 }
                16 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 3; slots[5] = 2; slots[6] = 1; slots[7] = 1; slots[8] = 1 }
                17 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 3; slots[5] = 2; slots[6] = 1; slots[7] = 1; slots[8] = 1; slots[9] = 1 }
                18 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 3; slots[5] = 3; slots[6] = 1; slots[7] = 1; slots[8] = 1; slots[9] = 1 }
                19 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 3; slots[5] = 3; slots[6] = 2; slots[7] = 1; slots[8] = 1; slots[9] = 1 }
                20 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 3; slots[5] = 3; slots[6] = 2; slots[7] = 2; slots[8] = 1; slots[9] = 1 }
            }
        }
        // Paladin, Ranger (half casters)
        7, 8 -> {
            when (characterLevel) {
                2 -> slots[1] = 2
                3 -> slots[1] = 3
                4 -> slots[1] = 3
                5 -> { slots[1] = 4; slots[2] = 2 }
                6 -> { slots[1] = 4; slots[2] = 2 }
                7 -> { slots[1] = 4; slots[2] = 3 }
                8 -> { slots[1] = 4; slots[2] = 3 }
                9 -> { slots[1] = 4; slots[2] = 3; slots[3] = 2 }
                10 -> { slots[1] = 4; slots[2] = 3; slots[3] = 2 }
                11 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3 }
                12 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3 }
                13 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 1 }
                14 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 1 }
                15 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 2 }
                16 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 2 }
                17 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 3; slots[5] = 1 }
                18 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 3; slots[5] = 1 }
                19 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 3; slots[5] = 2 }
                20 -> { slots[1] = 4; slots[2] = 3; slots[3] = 3; slots[4] = 3; slots[5] = 2 }
            }
        }
        // Warlock (pact magic)
        11 -> {
            val slotLevel = when {
                characterLevel >= 9 -> 5
                characterLevel >= 7 -> 4
                characterLevel >= 5 -> 3
                characterLevel >= 3 -> 2
                else -> 1
            }

            val numberOfSlots = when {
                characterLevel >= 17 -> 4
                characterLevel >= 11 -> 3
                characterLevel >= 2 -> 2
                else -> 1
            }

            slots[slotLevel] = numberOfSlots
        }
    }

    return slots
}

fun hasAvailableSpellSlot(character: Character, spellLevel: Int): Boolean {
    // This would check if there's an available spell slot
    // You'll need to implement this based on your data structure
    return true  // Placeholder
}

fun calculateAttackBonus(character: Character?, weapon: Weapon): Int {
    character ?: return 0

    val abilityModifier = when (weapon.weaponCategory) {
        Weapon.WeaponCategory.Melee -> {
            if (weapon.properties?.contains("Finesse") == true) {
                maxOf(
                    (character.strength - 10) / 2,
                    (character.dexterity - 10) / 2
                )
            } else {
                (character.strength - 10) / 2
            }
        }
        Weapon.WeaponCategory.Ranged -> (character.dexterity - 10) / 2
    }

    return abilityModifier
}