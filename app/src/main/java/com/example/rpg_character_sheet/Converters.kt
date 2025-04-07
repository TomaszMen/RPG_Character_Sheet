package com.example.rpg_character_sheet

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun intArrayToString(array: IntArray): String = array.joinToString(",")

    @TypeConverter
    fun stringToIntArray(data: String): IntArray =
        if (data.isEmpty()) IntArray(0)
        else data.split(",").map { it.toInt() }.toIntArray()
}