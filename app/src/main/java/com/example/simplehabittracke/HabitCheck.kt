package com.example.simplehabittracke

import org.threeten.bp.LocalDate

data class HabitCheck(val id: Int, val date: LocalDate, var checked: Boolean, val ref_key:Int)