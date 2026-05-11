package com.ruralhealth.shishusneh.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "baby_profile")
data class BabyProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val dateOfBirthMillis: Long
)

@Entity(tableName = "growth_record")
data class GrowthRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val monthSequence: Int,
    val weightKg: Float,
    val heightCm: Float,
    val dateRecordedMillis: Long
)

@Entity(tableName = "vaccine")
data class Vaccine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val diseasePrevented: String,
    val dueDateMillis: Long,
    val isGiven: Boolean = false,
    val givenDateMillis: Long? = null
)

@Entity(tableName = "milestone")
data class Milestone(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val monthExpected: Int,
    var isCompleted: Boolean = false
)
