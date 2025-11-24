package com.example.rpg_character_sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import table_entities.Character

@Composable
fun CharacterAddScreen(
    viewModel: CharacterViewModel,
    navController: NavHostController
) {
    val races       by viewModel.getAllRacesAsPairs()      .collectAsState(initial = emptyList())
    val classes     by viewModel.getAllClassesAsPair()     .collectAsState(initial = emptyList())
    val backgrounds by viewModel.getAllBackgroundsAsPairs().collectAsState(initial = emptyList())
    val alignments  by viewModel.getAllAlignmentsAsPairs() .collectAsState(initial = emptyList())

    var name by remember { mutableStateOf("") }

    var selectedRace       by remember { mutableStateOf<Pair<Int,String>?>(null) }
    var availableSubraces  by remember { mutableStateOf<List<Pair<Int, String>>>(emptyList()) }
    var selectedSubrace    by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var selectedClass      by remember { mutableStateOf<Pair<Int,String>?>(null) }
    var selectedBackground by remember { mutableStateOf<Pair<Int,String>?>(null) }
    var selectedAlignment  by remember { mutableStateOf<Pair<Int,String>?>(null) }

    var strength     by remember { mutableStateOf("") }
    var dex          by remember { mutableStateOf("") }
    var con          by remember { mutableStateOf("") }
    var intelligence by remember { mutableStateOf("") }
    var wisdom       by remember { mutableStateOf("") }
    var charisma     by remember { mutableStateOf("") }

    LaunchedEffect(selectedRace) {
        selectedRace?.let { race ->
            availableSubraces = viewModel.getSubracesForRace(race.first) as List<Pair<Int, String>>
            selectedSubrace = null
        } ?: run {
            availableSubraces = emptyList()
            selectedSubrace = null
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (
                        name.isNotBlank() &&
                        selectedRace != null &&
                        selectedClass != null &&
                        selectedBackground != null &&
                        selectedAlignment != null
                    ) {
                        val character = Character(
                            characterName = name,
                            raceId        = selectedRace!!.first,
                            subraceId     = selectedSubrace!!.first,
                            classId       = selectedClass!!.first,
                            subclassId    = 0,
                            backgroundId  = selectedBackground!!.first,
                            alignmentId   = selectedAlignment!!.first,
                            strength      = strength.toIntOrNull() ?: 10,
                            dexterity     = dex.toIntOrNull() ?: 10,
                            constitution  = con.toIntOrNull() ?: 10,
                            intelligence  = intelligence.toIntOrNull() ?: 10,
                            wisdom        = wisdom.toIntOrNull() ?: 10,
                            charisma      = charisma.toIntOrNull() ?: 10,
                            level         = 1
                        )
                        viewModel.insertCharacter(character)
                        navController.navigate(Screens.CharactersScreen.route)
                    }
                },
                containerColor = Color.White
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownField(
                label = "Race",
                options = races.map { it.second },
                selected = selectedRace?.second ?: "",
                onSelected = { selected ->
                    selectedRace = races.first { it.second == selected }
                }
            )

            if (availableSubraces.isNotEmpty()) {
                DropdownField(
                    label = "Subrace",
                    options = availableSubraces.map { it.second },
                    selected = selectedSubrace?.second ?: "",
                    onSelected = { selected ->
                        selectedSubrace = availableSubraces.first { it.second == selected }
                    }
                )
            }

            DropdownField(
                label = "Class",
                options = classes.map { it.second },
                selected = selectedClass?.second ?: "",
                onSelected = { selected ->
                    selectedClass = classes.first { it.second == selected }
                }
            )

            DropdownField(
                label = "Background",
                options = backgrounds.map { it.second },
                selected = selectedBackground?.second ?: "",
                onSelected = { selected ->
                    selectedBackground = backgrounds.first { it.second == selected }
                }
            )

            DropdownField(
                label = "Alignment",
                options = alignments.map { it.second },
                selected = selectedAlignment?.second ?: "",
                onSelected = { selected ->
                    selectedAlignment = alignments.first { it.second == selected }
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatField(
                    label = "STR",
                    value = strength,
                    onChange = { strength = it },
                    modifier = Modifier.weight(1f)
                )
                StatField(
                    label = "DEX",
                    value = dex,
                    onChange = { dex = it },
                    modifier = Modifier.weight(1f)
                )
                StatField(
                    label = "CON",
                    value = con,
                    onChange = { con = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatField(
                    label = "INT",
                    value = intelligence,
                    onChange = { intelligence = it },
                    modifier = Modifier.weight(1f)
                )
                StatField(
                    label = "WIS",
                    value = wisdom,
                    onChange = { wisdom = it },
                    modifier = Modifier.weight(1f)
                )
                StatField(
                    label = "CHA",
                    value = charisma,
                    onChange = { charisma = it },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier // 1. Accept a Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = modifier, // 2. Apply the passed-in modifier
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // 1. Make the entire Box clickable to expand the dropdown
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = true }) { // This makes the whole area clickable
        OutlinedTextField(
            value = selected,
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            // 2. Disable the TextField to allow clicks to pass through to the parent Box
            enabled = false,
            readOnly = true,
            trailingIcon = {
                // The icon can remain as a visual cue
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
            },
            // 3. Customize colors to make the disabled field look enabled
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
            modifier = Modifier.fillMaxWidth() // Make dropdown match the width of the field
        ) {
            options.forEach { entry ->
                DropdownMenuItem(
                    text = { Text(entry) },
                    onClick = {
                        onSelected(entry)
                        expanded = false
                    }
                )
            }
        }
    }
}