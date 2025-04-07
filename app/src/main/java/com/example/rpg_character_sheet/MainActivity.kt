package com.example.rpg_character_sheet

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = CharacterAdapter(object : CharacterAdapter.OnItemClickListener {
            override fun onItemClick(character: Character) {
                // Handle item click
                val intent = Intent(this@MainActivity, AddEditCharacterActivity::class.java).apply {
                    putExtra("character_id", character.id)
                }
                startActivity(intent)
            }
        })


        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val viewModel = ViewModelProvider(this).get(CharacterViewModel::class.java)
        viewModel.allCharacters.observe(this) { characters ->
            adapter.submitList(characters)
        }

        findViewById<FloatingActionButton>(R.id.add_button).setOnClickListener {
            startActivity(Intent(this, AddEditCharacterActivity::class.java))
        }
    }
}