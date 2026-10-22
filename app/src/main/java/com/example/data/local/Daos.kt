package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun clearUsers()
}

@Dao
interface CareScheduleDao {
    @Query("SELECT * FROM care_schedules WHERE plantId = :plantId")
    fun getSchedulesForPlant(plantId: Long): Flow<List<CareScheduleEntity>>

    @Query("SELECT * FROM care_schedules WHERE isEnabled = 1 ORDER BY nextDueDate ASC")
    fun getAllActiveSchedules(): Flow<List<CareScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: CareScheduleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(schedules: List<CareScheduleEntity>)

    @Update
    suspend fun updateSchedule(schedule: CareScheduleEntity)

    @Delete
    suspend fun deleteSchedule(schedule: CareScheduleEntity)
}

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants WHERE isArchived = 0 ORDER BY id ASC")
    fun getAllActivePlants(): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plants WHERE id = :id LIMIT 1")
    suspend fun getPlantById(id: Long): PlantEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<PlantEntity>)

    @Update
    suspend fun updatePlant(plant: PlantEntity)

    @Delete
    suspend fun deletePlant(plant: PlantEntity)

    @Query("UPDATE plants SET lastWateredDate = :timestamp WHERE id = :plantId")
    suspend fun updateLastWatered(plantId: Long, timestamp: Long)

    @Query("UPDATE plants SET lastFertilizedDate = :timestamp WHERE id = :plantId")
    suspend fun updateLastFertilized(plantId: Long, timestamp: Long)

    @Query("UPDATE plants SET isArchived = 1 WHERE id = :plantId")
    suspend fun archivePlant(plantId: Long)

    @Query("SELECT COUNT(*) FROM plants")
    suspend fun getPlantCount(): Int
}

@Dao
interface CareLogDao {
    @Query("SELECT * FROM watering_logs WHERE plantId = :plantId ORDER BY wateredDate DESC")
    fun getWateringLogsForPlant(plantId: Long): Flow<List<WateringLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWateringLog(log: WateringLogEntity): Long

    @Query("SELECT * FROM fertilizer_logs WHERE plantId = :plantId ORDER BY appliedDate DESC")
    fun getFertilizerLogsForPlant(plantId: Long): Flow<List<FertilizerLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFertilizerLog(log: FertilizerLogEntity): Long
}

@Dao
interface GrowthDao {
    @Query("SELECT * FROM growth_records WHERE plantId = :plantId ORDER BY recordedDate ASC")
    fun getGrowthRecordsForPlant(plantId: Long): Flow<List<GrowthRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrowthRecord(record: GrowthRecordEntity): Long

    @Query("DELETE FROM growth_records WHERE id = :id")
    suspend fun deleteGrowthRecord(id: Long)
}

@Dao
interface SunlightDao {
    @Query("SELECT * FROM sunlight_assessments WHERE plantId = :plantId ORDER BY recordedDate DESC")
    fun getAssessmentsForPlant(plantId: Long): Flow<List<SunlightAssessmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: SunlightAssessmentEntity): Long
}

@Dao
interface HarvestDao {
    @Query("SELECT * FROM harvest_records WHERE plantId = :plantId ORDER BY harvestDate DESC")
    fun getHarvestRecordsForPlant(plantId: Long): Flow<List<HarvestRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHarvestRecord(record: HarvestRecordEntity): Long
}

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary_entries ORDER BY createdAt DESC")
    fun getAllDiaryEntries(): Flow<List<DiaryEntryEntity>>

    @Query("SELECT * FROM diary_entries WHERE plantId = :plantId ORDER BY createdAt DESC")
    fun getDiaryEntriesForPlant(plantId: Long): Flow<List<DiaryEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaryEntry(entry: DiaryEntryEntity): Long

    @Delete
    suspend fun deleteDiaryEntry(entry: DiaryEntryEntity)
}

@Dao
interface HealthReportDao {
    @Query("SELECT * FROM health_reports ORDER BY scanDate DESC")
    fun getAllHealthReports(): Flow<List<HealthReportEntity>>

    @Query("SELECT * FROM health_reports WHERE plantId = :plantId ORDER BY scanDate DESC")
    fun getHealthReportsForPlant(plantId: Long): Flow<List<HealthReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthReport(report: HealthReportEntity): Long
}

@Dao
interface DailyCareTaskDao {
    @Query("SELECT * FROM daily_care_tasks ORDER BY isCompleted ASC, dueDate ASC, id ASC")
    fun getAllDailyTasks(): Flow<List<DailyCareTaskEntity>>

    @Query("SELECT * FROM daily_care_tasks WHERE plantId = :plantId ORDER BY dueDate ASC")
    fun getTasksForPlant(plantId: Long): Flow<List<DailyCareTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: DailyCareTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<DailyCareTaskEntity>)

    @Update
    suspend fun updateTask(task: DailyCareTaskEntity)

    @Query("UPDATE daily_care_tasks SET isCompleted = :isCompleted, completedAt = :completedAt WHERE id = :taskId")
    suspend fun setTaskCompletion(taskId: Long, isCompleted: Boolean, completedAt: Long?)

    @Query("DELETE FROM daily_care_tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)

    @Query("SELECT COUNT(*) FROM daily_care_tasks")
    suspend fun getTaskCount(): Int

    @Query("DELETE FROM daily_care_tasks")
    suspend fun clearAll()
}
