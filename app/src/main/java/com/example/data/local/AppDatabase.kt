package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        UserEntity::class,
        PlantEntity::class,
        CareScheduleEntity::class,
        WateringLogEntity::class,
        FertilizerLogEntity::class,
        GrowthRecordEntity::class,
        SunlightAssessmentEntity::class,
        HarvestRecordEntity::class,
        DiaryEntryEntity::class,
        HealthReportEntity::class,
        DailyCareTaskEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun plantDao(): PlantDao
    abstract fun careScheduleDao(): CareScheduleDao
    abstract fun careLogDao(): CareLogDao
    abstract fun growthDao(): GrowthDao
    abstract fun sunlightDao(): SunlightDao
    abstract fun harvestDao(): HarvestDao
    abstract fun diaryDao(): DiaryDao
    abstract fun healthReportDao(): HealthReportDao
    abstract fun dailyCareTaskDao(): DailyCareTaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "plantpal_app_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
