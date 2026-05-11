package com.ruralhealth.shishusneh.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ShishuDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBabyProfile(profile: BabyProfile)

    @Query("SELECT * FROM baby_profile LIMIT 1")
    fun getBabyProfile(): LiveData<BabyProfile?>

    @Query("SELECT * FROM baby_profile LIMIT 1")
    suspend fun getBabyProfileSync(): BabyProfile?

    @Insert
    suspend fun insertGrowthRecord(record: GrowthRecord)

    @Query("SELECT * FROM growth_record ORDER BY monthSequence ASC")
    fun getAllGrowthRecords(): LiveData<List<GrowthRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaccines(vaccines: List<Vaccine>)

    @Update
    suspend fun updateVaccine(vaccine: Vaccine)

    @Query("SELECT * FROM vaccine ORDER BY dueDateMillis ASC")
    fun getAllVaccines(): LiveData<List<Vaccine>>

    @Query("SELECT * FROM vaccine WHERE isGiven = 0 ORDER BY dueDateMillis ASC")
    suspend fun getPendingVaccinesSync(): List<Vaccine>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<Milestone>)
    
    @Update
    suspend fun updateMilestone(milestone: Milestone)

    @Query("SELECT * FROM milestone ORDER BY monthExpected ASC")
    fun getAllMilestones(): LiveData<List<Milestone>>
    
    @Query("SELECT COUNT(*) FROM milestone")
    suspend fun getMilestoneCount(): Int
    
    @Query("SELECT COUNT(*) FROM vaccine")
    suspend fun getVaccineCount(): Int
}
