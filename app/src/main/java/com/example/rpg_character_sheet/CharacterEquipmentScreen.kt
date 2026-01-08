package com.example.rpg_character_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.Flow
import table_entities.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults

enum class ItemFilter {
    ALL, WEAPONS, ARMOR, OTHER, EQUIPPED
}

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
    var selectedFilter by remember { mutableStateOf(ItemFilter.ALL) }
    val focusManager = LocalFocusManager.current

    // Collect flows
    val allItems by viewModel.getAllItems().collectAsState(initial = emptyList())
    val allWeapons by viewModel.getAllWeapons().collectAsState(initial = emptyList())
    val allArmor by viewModel.getAllArmor().collectAsState(initial = emptyList())
    val otherItems by viewModel.getOtherItems().collectAsState(initial = emptyList())
    val characterInventory by viewModel.getCharacterInventory(character.characterId).collectAsState(initial = emptyList())
    val equippedItems by viewModel.getEquippedItems(character.characterId).collectAsState(initial = emptyList())

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

        Spacer(modifier = Modifier.height(8.dp))

        // Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedFilter == ItemFilter.ALL,
                    onClick = { selectedFilter = ItemFilter.ALL },
                    label = { Text("All") },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == ItemFilter.WEAPONS,
                    onClick = { selectedFilter = ItemFilter.WEAPONS },
                    label = { Text("Weapons") },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == ItemFilter.ARMOR,
                    onClick = { selectedFilter = ItemFilter.ARMOR },
                    label = { Text("Armor") },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == ItemFilter.OTHER,
                    onClick = { selectedFilter = ItemFilter.OTHER },
                    label = { Text("Other") },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == ItemFilter.EQUIPPED,
                    onClick = { selectedFilter = ItemFilter.EQUIPPED },
                    label = { Text("Equipped") },
                    modifier = Modifier.padding(vertical = 4.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2196F3),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Equipped: ${equippedItems.size} items",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Text(
                text = "Weapons: ${equippedItems.count { item ->
                    allWeapons.any { it.itemId == item.itemId }
                }}/5",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (searchQuery.isNotEmpty()) {
            // Display search results
            val filteredItems = when (selectedFilter) {
                ItemFilter.WEAPONS -> allWeapons.filter {
                    it.itemName.contains(searchQuery, ignoreCase = true)
                }
                ItemFilter.ARMOR -> allArmor.filter {
                    it.itemName.contains(searchQuery, ignoreCase = true)
                }
                ItemFilter.OTHER -> otherItems.filter {
                    it.itemName.contains(searchQuery, ignoreCase = true)
                }
                else -> allItems.filter {
                    it.itemName.contains(searchQuery, ignoreCase = true)
                }
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
                        val inventoryItem = characterInventory.firstOrNull { it.itemId == item.itemId }
                        val isInInventory = inventoryItem != null

                        SearchResultItem(
                            item = item,
                            isInInventory = isInInventory,
                            inventoryItem = inventoryItem,
                            onAdd = { viewModel.addItemToInventory(character.characterId, item.itemId) },
                            onRemove = {
                                inventoryItem?.let {
                                    viewModel.removeItemFromInventory(it)
                                }
                            },
                            onEquip = {
                                inventoryItem?.let {
                                    viewModel.equipItem(character.characterId, it)
                                }
                            },
                            onUnequip = {
                                inventoryItem?.let {
                                    viewModel.unequipItem(character.characterId, it)
                                }
                            }
                        )

                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        } else {
            // Display filtered inventory
            val filteredInventory = when (selectedFilter) {
                ItemFilter.WEAPONS -> characterInventory.filter { inventoryItem ->
                    allWeapons.any { it.itemId == inventoryItem.itemId }
                }
                ItemFilter.ARMOR -> characterInventory.filter { inventoryItem ->
                    allArmor.any { it.itemId == inventoryItem.itemId }
                }
                ItemFilter.OTHER -> characterInventory.filter { inventoryItem ->
                    otherItems.any { it.itemId == inventoryItem.itemId }
                }
                ItemFilter.EQUIPPED -> characterInventory.filter { it.equipped }
                else -> characterInventory
            }

            Text(
                "Your ${selectedFilter.name.lowercase().replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (filteredInventory.isEmpty()) {
                Text(
                    "No items in this category",
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
                    items(filteredInventory) { inventoryItem ->
                        val item = allItems.firstOrNull { it.itemId == inventoryItem.itemId }

                        item?.let {
                            InventoryItem(
                                item = it,
                                inventoryItem = inventoryItem,
                                onRemove = { viewModel.removeItemFromInventory(inventoryItem) },
                                onEquip = { viewModel.equipItem(character.characterId, inventoryItem) },
                                onUnequip = { viewModel.unequipItem(character.characterId, inventoryItem) }
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
    inventoryItem: CharacterInventory?,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onEquip: () -> Unit,
    onUnequip: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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

                Row {
                    if (isInInventory && inventoryItem != null) {
                        // Show equip/unequip button for weapons and armor
                        if (item.itemType == Item.ItemType.Weapon || item.itemType == Item.ItemType.Armor) {
                            if (inventoryItem.equipped) {
                                IconButton(
                                    onClick = onUnequip,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            color = Color(0xFFFFF3E0),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Unequip",
                                        tint = Color(0xFFF57C00)
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = onEquip,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            color = Color(0xFFE8F5E8),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Equip",
                                        tint = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }

                        // Remove button
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
                        // Add button
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
    }
}

@Composable
private fun InventoryItem(
    item: Item,
    inventoryItem: CharacterInventory,
    onRemove: () -> Unit,
    onEquip: () -> Unit,
    onUnequip: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.itemName,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        if (inventoryItem.equipped) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(
                                containerColor = Color(0xFF2196F3),
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Text(
                                    "Equipped",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Row {
                        Text(
                            text = "Quantity: ${inventoryItem.quantity}",
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

                Row {
                    // Show equip/unequip button for weapons and armor
                    if (item.itemType == Item.ItemType.Weapon || item.itemType == Item.ItemType.Armor) {
                        if (inventoryItem.equipped) {
                            IconButton(
                                onClick = onUnequip,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = Color(0xFFFFF3E0),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Unequip",
                                    tint = Color(0xFFF57C00)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = onEquip,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = Color(0xFFE8F5E8),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Equip",
                                    tint = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }

                    // Remove button
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
    }
}