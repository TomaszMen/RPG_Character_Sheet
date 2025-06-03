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
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.Flow
import table_entities.Character
import table_entities.CharacterInventory
import table_entities.Item
import table_entities.Weapon
import table_entities.Armor

@Composable
fun CharacterEquipmentScreen(viewModel: CharacterViewModel, navController: NavHostController) {
	val character by viewModel.getSelectedCharacter().collectAsState(initial = null)

	Scaffold { innerPadding ->
		Column(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize()
		) {
			character?.let {
				EquipmentContent(character = it, viewModel = viewModel)
			} ?: Text("No character selected", modifier = Modifier.padding(16.dp))
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EquipmentContent(character: Character, viewModel: CharacterViewModel) {
	var searchQuery by remember { mutableStateOf("") }
	var isSearching by remember { mutableStateOf(false) }
	val focusManager = LocalFocusManager.current

	// This would need to be implemented in your ViewModel/Dao
	val allItems by viewModel.getAllItems().collectAsState(initial = emptyList())
	val characterInventory by viewModel.getCharacterInventory(character.characterId).collectAsState(initial = emptyList())

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp)
	) {
		// Search Section
		OutlinedTextField(
			value = searchQuery,
			onValueChange = { searchQuery = it },
			modifier = Modifier.fillMaxWidth(),
			placeholder = { Text("Search for items...") },
			leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
			trailingIcon = {
				if (searchQuery.isNotEmpty()) {
					IconButton(onClick = {
						searchQuery = ""
						focusManager.clearFocus()
					}) {
						Icon(Icons.Default.Close, contentDescription = "Clear")
					}
				}
			},
			keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
			keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
			singleLine = true
		)

		Spacer(modifier = Modifier.height(16.dp))

		if (searchQuery.isNotEmpty()) {
			// Display search results
			val filteredItems = allItems.filter {
				it.itemName.contains(searchQuery, ignoreCase = true)
			}

			Text(
				"Search Results",
				style = MaterialTheme.typography.titleMedium,
				modifier = Modifier.padding(bottom = 8.dp)
			)

			if (filteredItems.isEmpty()) {
				Text("No items found", modifier = Modifier.padding(8.dp))
			} else {
				LazyColumn(
					modifier = Modifier
						.weight(1f)
						.padding(bottom = 16.dp)
				) {
					items(filteredItems) { item ->
						val isInInventory = characterInventory.any { it.itemId == item.itemId }

						SearchResultItem(
							item = item,
							isInInventory = isInInventory,
							onAdd = { viewModel.addItemToInventory(character.characterId, item.itemId) },
							onRemove = {
								val inventoryItem = characterInventory.first { it.itemId == item.itemId }
								viewModel.removeItemFromInventory(inventoryItem)
							}
						)

						Divider(modifier = Modifier.padding(vertical = 4.dp))
					}
				}
			}
		} else {
			// Display character inventory
			Text(
				"Your Equipment",
				style = MaterialTheme.typography.titleMedium,
				modifier = Modifier.padding(bottom = 8.dp)
			)

			if (characterInventory.isEmpty()) {
				Text(
					"Your inventory is empty",
					modifier = Modifier
						.padding(16.dp)
						.align(Alignment.CenterHorizontally)
				)
			} else {
				LazyColumn(
					modifier = Modifier
						.weight(1f)
						.padding(bottom = 16.dp)
				) {
					items(characterInventory) { inventoryItem ->
						val item = allItems.firstOrNull { it.itemId == inventoryItem.itemId }

						item?.let {
							InventoryItem(
								item = it,
								quantity = inventoryItem.quantity,
								onRemove = { viewModel.removeItemFromInventory(inventoryItem) }
							)

							Divider(modifier = Modifier.padding(vertical = 4.dp))
						}
					}
				}
			}
		}
	}
}

@Composable
private fun SearchResultItem(
	item: Item,
	isInInventory: Boolean,
	onAdd: () -> Unit,
	onRemove: () -> Unit
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(4.dp),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
		shape = RoundedCornerShape(8.dp)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(12.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = item.itemName,
					style = MaterialTheme.typography.titleSmall,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
				Text(
					text = item.itemType.toString(),
					style = MaterialTheme.typography.bodySmall,
					color = Color.Gray
				)
				Text(
					text = "Weight: ${item.weight} lbs, Cost: ${item.cost} gp",
					style = MaterialTheme.typography.bodySmall
				)
			}

			if (isInInventory) {
				IconButton(
					onClick = onRemove,
					modifier = Modifier
						.size(40.dp)
						.background(
							color = Color(0xFFFFCDD2),
							shape = RoundedCornerShape(8.dp)
						)
				) {
					Icon(
						Icons.Default.Delete,
						contentDescription = "Remove",
						tint = Color(0xFFC62828)
					)
				}
			} else {
				IconButton(
					onClick = onAdd,
					modifier = Modifier
						.size(40.dp)
						.background(
							color = Color(0xFFC8E6C9),
							shape = RoundedCornerShape(8.dp)
						)
				) {
					Icon(
						Icons.Default.Add,
						contentDescription = "Add",
						tint = Color(0xFF2E7D32)
					)
				}
			}
		}
	}
}

@Composable
private fun InventoryItem(
	item: Item,
	quantity: Int,
	onRemove: () -> Unit
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(4.dp),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
		shape = RoundedCornerShape(8.dp)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(12.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = item.itemName,
					style = MaterialTheme.typography.titleSmall,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
				Row {
					Text(
						text = "Quantity: $quantity",
						style = MaterialTheme.typography.bodySmall,
						modifier = Modifier.padding(end = 8.dp)
					)
					Text(
						text = item.itemType.toString(),
						style = MaterialTheme.typography.bodySmall,
						color = Color.Gray
					)
				}
				Text(
					text = "Weight: ${item.weight} lbs, Cost: ${item.cost} gp",
					style = MaterialTheme.typography.bodySmall
				)
			}

			IconButton(
				onClick = onRemove,
				modifier = Modifier
					.size(40.dp)
					.background(
						color = Color(0xFFFFCDD2),
						shape = RoundedCornerShape(8.dp)
					)
			) {
				Icon(
					Icons.Default.Delete,
					contentDescription = "Remove",
					tint = Color(0xFFC62828)
				)
			}
		}
	}
}