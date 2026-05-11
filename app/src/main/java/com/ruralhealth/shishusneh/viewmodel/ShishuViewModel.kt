package com.ruralhealth.shishusneh.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ruralhealth.shishusneh.model.*
import kotlinx.coroutines.launch

class ShishuViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ShishuRepository
    val babyProfile: LiveData<BabyProfile?>
    val growthRecords: LiveData<List<GrowthRecord>>
    val vaccines: LiveData<List<Vaccine>>
    val milestones: LiveData<List<Milestone>>

    init {
        val dao = AppDatabase.getDatabase(application).shishuDao()
        repository = ShishuRepository(dao)
        babyProfile = repository.babyProfile
        growthRecords = repository.growthRecords
        vaccines = repository.vaccines
        milestones = repository.milestones
    }

    fun saveBabyProfile(name: String, dobMillis: Long) = viewModelScope.launch {
        repository.saveBabyProfile(name, dobMillis)
    }

    fun addGrowthRecord(monthSeq: Int, weight: Float, height: Float, dateMillis: Long) = viewModelScope.launch {
        repository.addGrowthRecord(GrowthRecord(
            monthSequence = monthSeq,
            weightKg = weight,
            heightCm = height,
            dateRecordedMillis = dateMillis
        ))
    }

    fun updateVaccine(vaccine: Vaccine) = viewModelScope.launch {
        repository.updateVaccine(vaccine)
    }

    fun updateMilestone(milestone: Milestone) = viewModelScope.launch {
        repository.updateMilestone(milestone)
    }
}
