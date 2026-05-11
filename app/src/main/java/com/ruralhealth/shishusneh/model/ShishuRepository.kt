package com.ruralhealth.shishusneh.model

import androidx.lifecycle.LiveData
import java.util.Calendar

class ShishuRepository(private val shishuDao: ShishuDao) {
    val babyProfile: LiveData<BabyProfile?> = shishuDao.getBabyProfile()
    val growthRecords: LiveData<List<GrowthRecord>> = shishuDao.getAllGrowthRecords()
    val vaccines: LiveData<List<Vaccine>> = shishuDao.getAllVaccines()
    val milestones: LiveData<List<Milestone>> = shishuDao.getAllMilestones()

    suspend fun saveBabyProfile(name: String, dobMillis: Long) {
        shishuDao.insertBabyProfile(BabyProfile(name = name, dateOfBirthMillis = dobMillis))
        
        // Generate and insert standard vaccines if not already present
        if (shishuDao.getVaccineCount() == 0) {
            val baseVaccines = generateVaccineSchedule(dobMillis)
            shishuDao.insertVaccines(baseVaccines)
        }

        // Generate milestones
        if (shishuDao.getMilestoneCount() == 0) {
            val baseMilestones = generateMilestones()
            shishuDao.insertMilestones(baseMilestones)
        }
    }

    suspend fun addGrowthRecord(record: GrowthRecord) {
        shishuDao.insertGrowthRecord(record)
    }

    suspend fun updateVaccine(vaccine: Vaccine) {
        shishuDao.updateVaccine(vaccine)
    }

    suspend fun updateMilestone(milestone: Milestone) {
        shishuDao.updateMilestone(milestone)
    }

    private fun generateVaccineSchedule(dobMillis: Long): List<Vaccine> {
        val list = mutableListOf<Vaccine>()
        // 0 Weeks
        list.add(Vaccine(name = "BCG", diseasePrevented = "Tuberculosis", dueDateMillis = dobMillis))
        list.add(Vaccine(name = "OPV 0", diseasePrevented = "Polio", dueDateMillis = dobMillis))
        list.add(Vaccine(name = "Hep B 1", diseasePrevented = "Hepatitis B", dueDateMillis = dobMillis))
        
        // 6 Weeks (42 days)
        val week6 = dobMillis + 42L * 24 * 60 * 60 * 1000
        list.add(Vaccine(name = "DPT 1", diseasePrevented = "Diphtheria, Pertussis, Tetanus", dueDateMillis = week6))
        list.add(Vaccine(name = "IPV 1", diseasePrevented = "Polio", dueDateMillis = week6))
        list.add(Vaccine(name = "Rotavirus 1", diseasePrevented = "Diarrhea", dueDateMillis = week6))

        // 10 Weeks (70 days)
        val week10 = dobMillis + 70L * 24 * 60 * 60 * 1000
        list.add(Vaccine(name = "DPT 2", diseasePrevented = "Diphtheria, Pertussis, Tetanus", dueDateMillis = week10))
        list.add(Vaccine(name = "IPV 2", diseasePrevented = "Polio", dueDateMillis = week10))
        list.add(Vaccine(name = "Rotavirus 2", diseasePrevented = "Diarrhea", dueDateMillis = week10))

        // 14 Weeks (98 days)
        val week14 = dobMillis + 98L * 24 * 60 * 60 * 1000
        list.add(Vaccine(name = "DPT 3", diseasePrevented = "Diphtheria, Pertussis, Tetanus", dueDateMillis = week14))
        list.add(Vaccine(name = "IPV 3", diseasePrevented = "Polio", dueDateMillis = week14))
        list.add(Vaccine(name = "Rotavirus 3", diseasePrevented = "Diarrhea", dueDateMillis = week14))

        // 9 Months (270 days approx)
        val month9 = dobMillis + 270L * 24 * 60 * 60 * 1000
        list.add(Vaccine(name = "Measles 1", diseasePrevented = "Measles", dueDateMillis = month9))

        return list
    }

    private fun generateMilestones(): List<Milestone> {
        return listOf(
            Milestone(title = "Smiles at people", monthExpected = 2),
            Milestone(title = "Can hold head up and begins to push up when lying on tummy", monthExpected = 2),
            Milestone(title = "Brings hands to mouth", monthExpected = 4),
            Milestone(title = "Rolls over from tummy to back", monthExpected = 4),
            Milestone(title = "Sits without support", monthExpected = 6),
            Milestone(title = "Begins to crawl", monthExpected = 8),
            Milestone(title = "Pulls to stand", monthExpected = 9),
            Milestone(title = "Walks holding on to furniture", monthExpected = 11),
            Milestone(title = "May take a few steps without holding on", monthExpected = 12)
        )
    }
}
