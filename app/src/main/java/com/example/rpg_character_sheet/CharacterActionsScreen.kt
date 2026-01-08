package com.example.rpg_character_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import table_entities.Character
import table_entities.CharacterInventory
import table_entities.Item
import table_entities.Weapon
import table_entities.Armor
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import table_entities.*


@Composable
fun CharacterActionsScreen(viewModel: CharacterViewModel, navController: NavHostController) {
	val character by viewModel.getSelectedCharacter().collectAsState(initial = null)
	val weapons by viewModel.getCharacterWeapons(character?.characterId ?: 0).collectAsState(initial = emptyList())
	val spells by viewModel.getCharacterSpells(character?.characterId ?: 0).collectAsState(initial = emptyList())
	val features by viewModel.getCharacterFeatures(character?.characterId ?: 0).collectAsState(initial = emptyList())
	val spellSlots by viewModel.getCharacterSpellSlots(character?.characterId ?: 0).collectAsState(initial = emptyList())

	LazyColumn(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp)
	) {
		item {
			Text(
				text = "Actions",
				style = MaterialTheme.typography.headlineMedium,
				modifier = Modifier.padding(bottom = 16.dp)
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
			item { Text("No weapons equipped") }
		} else {
			items(weapons) { weaponWithItem ->
				WeaponAttackCard(weaponWithItem)
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

		if (spellSlots.isNotEmpty()) {
			item { SpellSlotsTracker(spellSlots) }
		}

		if (spells.isEmpty()) {
			item { Text("No spells known") }
		} else {
			// Group spells by level inside the LazyColumn scope
			val spellsByLevel = spells.groupBy { it.spellLevel }
			spellsByLevel.forEach { (level, spellsForLevel) ->
				item {
					SpellsLevelSection(level, spellsForLevel)
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
			item { Text("No features available") }
		} else {
			items(features) { feature ->
				FeatureCard(feature)
			}
		}
	}
}

@Composable
fun WeaponAttackCard(WeaponAndItem: WeaponAndItem) {
	val weapon = WeaponAndItem.weapon
	val item = WeaponAndItem.item
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
				Text(
					text = "${weapon.damageDice} ${weapon.damageType}",
					style = MaterialTheme.typography.titleMedium
				)
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
fun SpellSlotsTracker(spellSlots: List<CharacterSpellSlot>) {
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

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceEvenly
			) {
				spellSlots.sortedBy { it.spellLevel }.forEach { slot ->
					Column(horizontalAlignment = Alignment.CenterHorizontally) {
						Text(
							text = "Level ${slot.spellLevel}",
							style = MaterialTheme.typography.labelSmall
						)
						Text(
							text = "${slot.usedSlots}/${slot.totalSlots}",
							style = MaterialTheme.typography.titleMedium
						)
					}
				}
			}
		}
	}
}

@Composable
fun SpellsLevelSection(level: Int, spells: List<Spell>) {
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
				Text(
					text = if (level == 0) "Cantrips" else "Level $level Spells",
					style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
				)
				IconButton(onClick = { expanded = !expanded }) {
					Icon(
						imageVector = Icons.Default.CheckCircle,
						contentDescription = if (expanded) "Collapse" else "Expand"
					)
				}
			}

			if (expanded) {
				spells.forEach { spell ->
					SpellCard(spell)
				}
			}
		}
	}
}

@Composable
fun SpellCard(spell: Spell) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp, horizontal = 8.dp)
	) {
		Column(modifier = Modifier.padding(12.dp)) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				Text(
					text = spell.spellName,
					style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
				)
				Text(
					text = spell.school.toString(),
					style = MaterialTheme.typography.labelMedium
				)
			}

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				Text(
					text = "Casting Time: ${spell.castingTime}",
					style = MaterialTheme.typography.bodySmall
				)
				Text(
					text = "Range: ${spell.spellRange}",
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
				Text(
					text = "Duration: ${spell.duration}",
					style = MaterialTheme.typography.bodySmall
				)
			}

			if (spell.concentration) {
				Text(
					text = "Concentration",
					style = MaterialTheme.typography.bodySmall.copy(color = Color.Red)
				)
			}

			if (spell.ritual) {
				Text(
					text = "Ritual",
					style = MaterialTheme.typography.bodySmall.copy(color = Color.Blue)
				)
			}

			Text(
				text = spell.description,
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier.padding(top = 8.dp)
			)
		}
	}
}

@Composable
fun FeatureCard(feature: Feature) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0x2200FF00))
	) {
		Column(modifier = Modifier.padding(12.dp)) {
			Text(
				text = feature.featureName,
				style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
			)

			Text(
				text = "Source: ${feature.sourceType}",
				style = MaterialTheme.typography.labelSmall
			)

			Text(
				text = feature.description,
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier.padding(top = 8.dp)
			)
		}
	}
}