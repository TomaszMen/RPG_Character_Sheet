package com.example.rpg_character_sheet

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.rpg_character_sheet.ui.theme.CharacterSheetTheme


class MainActivity : AppCompatActivity() {

    // New Compose-powered UI
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CharacterSheetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }
            }
        }
    }

    // Old xml-based UI
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//
//        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
//        val adapter = CharacterAdapter(object : CharacterAdapter.OnItemClickListener {
//            override fun onItemClick(character: Character) {
//                // Handle item click
//                val intent = Intent(this@MainActivity, AddEditCharacterActivity::class.java).apply {
//                    putExtra("character_id", character.id)
//                }
//                startActivity(intent)
//            }
//        })
//
//
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        val viewModel = ViewModelProvider(this).get(CharacterViewModel::class.java)
//        viewModel.allCharacters.observe(this) { characters ->
//            adapter.submitList(characters)
//        }
//
//        findViewById<FloatingActionButton>(R.id.add_button).setOnClickListener {
//            try {
//                val intent = Intent(this@MainActivity, AddEditCharacterActivity::class.java)
//                startActivity(intent)
//            } catch (e: Exception) {
//                Log.e("MainActivity", "Add button crash: ${e.stackTraceToString()}")
//                Toast.makeText(
//                    this@MainActivity,
//                    "Could not open editor: ${e.localizedMessage}",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }
}